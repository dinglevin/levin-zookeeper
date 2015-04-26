package org.levin.zookeeper;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Op;
import org.apache.zookeeper.OpResult;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.proto.CreateRequest;
import org.apache.zookeeper.proto.SetDataRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class RecoverableZooKeeper {
    private static final Logger logger = LoggerFactory.getLogger(RecoverableZooKeeper.class);
    
    private static final byte[] MAGIC = Bytes.toBytes(0xABCD);
    private static final int MAGIC_SIZE = MAGIC.length;
    private static final int ID_LENGTH_SIZE = Bytes.SIZEOF_INT;
    private static final int ID_LENGTH_OFFSET = MAGIC_SIZE;
    
    private ZooKeeper zooKeeper;
    private final String quorumServers;
    private final int sessionTimeout;
    private final Watcher watcher;
    
    private final String identifier;
    private final byte[] id;
    private final Random salter;
    private final RetryCounterFactory retryCounterFactory;
    
    public RecoverableZooKeeper(String quorumServers, int sessionTimeout, Watcher watcher,
            int maxRetries, int retryIntervalMillis) throws KeeperException {
        this(quorumServers, sessionTimeout, watcher, maxRetries, retryIntervalMillis, null);
    }
    
    public RecoverableZooKeeper(String quorumServers, int sessionTimeout, Watcher watcher,
            int maxRetries, int retryIntervalMillis, String identifier) throws KeeperException {
        if (identifier == null || identifier.isEmpty()) {
            identifier = ManagementFactory.getRuntimeMXBean().getName();
        }
        
        this.retryCounterFactory = new RetryCounterFactory(maxRetries, retryIntervalMillis);
        this.identifier = identifier;
        this.id = Bytes.toBytes(identifier);
        
        this.quorumServers = quorumServers;
        this.sessionTimeout = sessionTimeout;
        this.watcher = watcher;
        this.salter = new SecureRandom();
        
        logger.info("Connecting to ZooKeeper ensemble: " + quorumServers + 
                ", with identifier: " + identifier);
        checkZK();
    }
    
    public synchronized long getSessionId() {
        return zooKeeper == null ? -1 : zooKeeper.getSessionId();
    }
    
    public synchronized States getState() {
        return zooKeeper == null ? null : zooKeeper.getState();
    }
    
    public synchronized byte[] getSessionPassword() {
        return zooKeeper == null ? null : zooKeeper.getSessionPasswd();
    }
    
    public String getIdentifier() {
        return identifier;
    }
    
    public String getQuorumServers() {
        return quorumServers;
    }
    
    public synchronized void close() throws InterruptedException {
        if (zooKeeper != null) {
            zooKeeper.close();
        }
    }
    
    public synchronized void reconnectAfterExpiration() 
    throws KeeperException, InterruptedException {
        if (zooKeeper != null) {
            logger.info("Closing dead ZooKeeper connection, session was: 0x" + 
                    Long.toHexString(zooKeeper.getSessionId()));
            zooKeeper.close();
            zooKeeper = null;
        }
        
        checkZK();
        logger.info("Recreated ZooKeeper session: 0x" + Long.toHexString(zooKeeper.getSessionId()));
    }
    
    public String create(String path, byte[] data, List<ACL> acl, CreateMode createMode)
    throws KeeperException, InterruptedException {
        byte[] newData = appendMetaData(data);
        switch (createMode) {
        case EPHEMERAL:
        case PERSISTENT:
            return createNonSequential(path, newData, acl, createMode);
            
        case EPHEMERAL_SEQUENTIAL:
        case PERSISTENT_SEQUENTIAL:
            return createSequential(path, newData, acl, createMode);
            
        default:
            throw new IllegalArgumentException("Unrecognized CreateMode: " + createMode);
        }
    }
    
    public Stat setData(final String path, final byte[] data, final int version) 
    throws KeeperException, InterruptedException {
        final byte[] newData = appendMetaData(data);
        return retriableCall("setData", new ZooKeeperExecutor<Stat>() {
            @Override
            public Stat execute(RetryCounter retryCounter) throws KeeperException, InterruptedException {
                try {
                    return checkZK().setData(path, newData, version);
                } catch (KeeperException ex) {
                    switch (ex.code()) {
                    case BADVERSION:
                        if (retryCounter.isRetry()) {
                            Stat stat = new Stat();
                            byte[] recvData = checkZK().getData(path, false, stat);
                            if (Bytes.equals(recvData, newData)) {
                                // the bad version is caused by previous successful setData
                                return stat;
                            }
                        }
                    default:
                        throw ex;
                    }
                }
            }
        });
    }
    
    public byte[] getData(final String path, final boolean watch, final Stat stat)
    throws KeeperException, InterruptedException {
        byte[] recvData = retriableCall("getData", new ZooKeeperExecutor<byte[]>() {
            @Override
            public byte[] execute(RetryCounter retryCounter) throws KeeperException, InterruptedException {
                return checkZK().getData(path, watch, stat);
            }
        });
        
        return removeMetaData(recvData);
    }
    
    public byte[] getData(final String path, final Watcher watcher, final Stat stat)
    throws KeeperException, InterruptedException {
        byte[] recvdata = retriableCall("getData", new ZooKeeperExecutor<byte[]>() {
            @Override
            public byte[] execute(RetryCounter retryCounter) throws KeeperException, InterruptedException {
                return checkZK().getData(path, watcher, stat);
            }
        });
        
        return removeMetaData(recvdata);
    }
    
    public List<String> getChildren(final String path, final boolean watch)
    throws KeeperException, InterruptedException {
        return retriableCall("getChildren", new ZooKeeperExecutor<List<String>>() {
            @Override
            public List<String> execute(RetryCounter retryCounter) throws KeeperException, InterruptedException {
                return checkZK().getChildren(path, watch);
            }
        });
    }
    
    public List<String> getChildren(final String path, final Watcher watcher)
    throws KeeperException, InterruptedException {
        return retriableCall("getChildren", new ZooKeeperExecutor<List<String>>() {
            @Override
            public List<String> execute(RetryCounter retryCounter) throws KeeperException, InterruptedException {
                return checkZK().getChildren(path, watcher);
            }
        });
    }
    
    public Stat exists(final String path, final boolean watch) 
    throws KeeperException, InterruptedException {
        return retriableCall("exists", new ZooKeeperExecutor<Stat>() {
            @Override
            public Stat execute(RetryCounter retryCounter) throws KeeperException, InterruptedException {
                return checkZK().exists(path, watch);
            }
        });
    }
    
    public Stat exists(final String path, final Watcher watcher)
    throws KeeperException, InterruptedException {
        return retriableCall("exists", new ZooKeeperExecutor<Stat>() {
            @Override
            public Stat execute(RetryCounter retryCounter) throws KeeperException, InterruptedException {
                return checkZK().exists(path, watcher);
            }
        });
    }
    
    public void delete(final String path, final int version)
    throws KeeperException, InterruptedException {
        retriableCall("delete", new ZooKeeperExecutor<Void>() {
            @Override
            public Void execute(RetryCounter retryCounter) throws KeeperException, InterruptedException {
                try {
                    checkZK().delete(path, version);
                } catch (KeeperException ex) {
                    switch (ex.code()) {
                    case NONODE:
                        if (retryCounter.isRetry()) {
                            logger.info("Node: " + path + " already deleted, Assuming a " + 
                                    "previous attempt succeeded.");
                            return null;
                        } else {
                            logger.info("Node: " + path + " already deleted");
                            throw ex;
                        }
                    default:
                        throw ex;
                    }
                }
                return null;
            }
        });
    }
    
    public void sync(String path, AsyncCallback.VoidCallback cb, Object ctx)
    throws KeeperException, InterruptedException {
        checkZK().sync(path, cb, ctx);
    }
    
    public List<OpResult> multi(final Iterable<Op> ops)
    throws KeeperException, InterruptedException {
        // To append meta data when necessary
        final Iterable<Op> preparedOps = prepareZKMulti(ops);
        return retriableCall("multi", new ZooKeeperExecutor<List<OpResult>>() {
            @Override
            public List<OpResult> execute(RetryCounter retryCounter) throws KeeperException, InterruptedException {
                return checkZK().multi(preparedOps);
            }
        });
    }
    
    public byte[] removeMetaData(byte[] data) {
        checkArgument(data != null && data.length > 0, "data is null or empty");
        checkMagic(data);
        
        int idLen = Bytes.toInt(data, ID_LENGTH_OFFSET);
        int dataLen = data.length - MAGIC_SIZE - ID_LENGTH_SIZE - idLen;
        if (dataLen == 0) {
            return Bytes.EMPTY_BYTE_ARRAY;
        }
        int dataOffset = MAGIC_SIZE + ID_LENGTH_OFFSET + idLen;
        
        byte[] newData = new byte[dataLen];
        System.arraycopy(data, dataOffset, newData, 0, dataLen);
        return newData;
    }
    
    private Iterable<Op> prepareZKMulti(Iterable<Op> ops)
    throws UnsupportedOperationException {
        checkArgument(ops != null, "ops is null");
        
        List<Op> preparedOps = Lists.newLinkedList();
        for (Op op : ops) {
            if (op.getType() == ZooDefs.OpCode.create) {
                CreateRequest create = (CreateRequest)op.toRequestRecord();
                preparedOps.add(Op.create(create.getPath(), appendMetaData(create.getData()), 
                        create.getAcl(), create.getFlags()));
            } else if (op.getType() == ZooDefs.OpCode.delete) {
                preparedOps.add(op);
            } else if (op.getType() == ZooDefs.OpCode.setData) {
                SetDataRequest setData = (SetDataRequest)op.toRequestRecord();
                preparedOps.add(Op.setData(setData.getPath(), appendMetaData(setData.getData()), 
                        setData.getVersion()));
            } else {
                throw new UnsupportedOperationException("Unexpected ZooKeeper OP type: " + op.getClass().getName());
            }
        }
        return preparedOps;
    }
    
    private String createNonSequential(final String path, final byte[] data, final List<ACL> acl, 
            final CreateMode createMode)
    throws KeeperException, InterruptedException {
        return retriableCall("create", new ZooKeeperExecutor<String>() {
            @Override
            public String execute(RetryCounter retryCounter) throws KeeperException, InterruptedException {
                try {
                    return checkZK().create(path, data, acl, createMode);
                } catch (KeeperException ex) {
                    switch (ex.code()) {
                    case NODEEXISTS: {
                        // On create, if the connection was lost, there is still a possibility that
                        // we have successfully created the node at our previous attempt, 
                        // so we read the node and compare
                        if (retryCounter.isRetry()) {
                            byte[] currentData = checkZK().getData(path, false, null);
                            if (currentData != null && Bytes.equals(currentData, data)) {
                                return path;
                            }
                        }
                    }
                    
                    default:
                        throw ex;
                    }
                }
            }
        });
    }
    
    private String createSequential(final String path, final byte[] data, final List<ACL> acl, 
            final CreateMode createMode)
    throws KeeperException, InterruptedException {
        final String newPath = path + identifier;
        return retriableCall("create", new ZooKeeperExecutor<String>() {
            @Override
            public String execute(RetryCounter retryCounter) throws KeeperException, InterruptedException {
                if (retryCounter.isRetry()) {
                    // This implementation depends on the requirement, if we are going to 
                    // create multiple sequential nodes of this path, then the implementation is not correct
                    String previousResult = findPreviousSequentialNode(newPath);
                    if (previousResult != null) {
                        return previousResult;
                    }
                }
                return checkZK().create(newPath, data, acl, createMode);
            }
        });
    }
    
    // This implementation depends on the requirement, if we are going to 
    // create multiple sequential nodes of this path, then the implementation is not correct
    private String findPreviousSequentialNode(String path)
    throws KeeperException, InterruptedException {
        int lastSlashIdx = path.lastIndexOf('/');
        assert(lastSlashIdx != -1);
        
        String parent = path.substring(0, lastSlashIdx);
        String nodePrefix = path.substring(lastSlashIdx + 1);
        
        List<String> nodes = checkZK().getChildren(parent, false);
        for (String node : nodes) {
            if (node.startsWith(nodePrefix)) {
                String nodePath = parent + "/" + node;
                Stat stat = checkZK().exists(nodePath, false);
                if (stat != null) {
                    return nodePath;
                }
            }
        }
        return null;
    }
    
    private byte[] appendMetaData(byte[] data) {
        int dataLen = 0;
        if (data != null) {
            dataLen = data.length;
        }
        
        byte[] salt = Bytes.toBytes(salter.nextLong());
        int idLen = id.length + salt.length;
        byte[] newData = new byte[MAGIC_SIZE + ID_LENGTH_SIZE + idLen + dataLen];
        
        int pos = 0;
        pos = Bytes.putBytes(newData, pos, MAGIC, 0, MAGIC_SIZE);
        pos = Bytes.putInt(newData, pos, idLen);
        pos = Bytes.putBytes(newData, pos, id, 0, id.length);
        pos = Bytes.putBytes(newData, pos, salt, 0, salt.length);
        if (dataLen > 0) {
            pos = Bytes.putBytes(newData, pos, data, 0, dataLen);
        }
        return newData;
    }
    
    private <T> T retriableCall(String opName, ZooKeeperExecutor<T> executor) 
    throws KeeperException, InterruptedException {
        RetryCounter retryCounter = retryCounterFactory.create();
        while(true) {
            try {
                return executor.execute(retryCounter);
            } catch (KeeperException ex) {
                switch (ex.code()) {
                case CONNECTIONLOSS:
                case SESSIONEXPIRED:
                case OPERATIONTIMEOUT:
                    retryOrThrow(retryCounter, ex, opName);
                    break;
                
                default:
                    throw ex;
                }
            }
            
            retryCounter.sleepUntilNextRetry();
        }
    }
    
    private static interface ZooKeeperExecutor<T> {
        public T execute(RetryCounter retryCounter) throws KeeperException, InterruptedException;
    }
    
    private synchronized ZooKeeper checkZK() throws KeeperException {
        if (zooKeeper == null) {
            try {
                zooKeeper = new ZooKeeper(identifier, sessionTimeout, watcher);
            } catch (IOException e) {
                logger.warn("Unable to connect to ZooKeeper", e);
                throw new KeeperException.OperationTimeoutException();
            }
        }
        return zooKeeper;
    }
    
    private void retryOrThrow(RetryCounter retryCounter, KeeperException ex, String opName) throws KeeperException {
        logger.warn("Possible transient ZooKeeper, quorum: " + quorumServers + ", excepiton: " + ex);
        
        if (!retryCounter.shouldRetry()) {
            logger.error("ZooKeeper {} failed after {} attempts", opName, retryCounter.getAttemptTimes());
            throw ex;
        }
    }
    
    private void checkMagic(byte[] data) {
        byte magic1 = data[0];
        byte magic2 = data[1];
        if (magic1 != 0xAB || magic2 != 0xCD) {
            throw new IllegalArgumentException("MAGIC value not match, actual:" +
                    Bytes.toString(data, 0, MAGIC_SIZE) + ", expected: " + Bytes.toString(MAGIC));
        }
    }
}
