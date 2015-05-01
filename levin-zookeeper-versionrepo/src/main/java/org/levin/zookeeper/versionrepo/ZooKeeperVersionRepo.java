package org.levin.zookeeper.versionrepo;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.levin.zookeeper.RecoverableZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.InvalidProtocolBufferException;

public class ZooKeeperVersionRepo implements VersionRepo, Watcher {
    private static final Logger logger = LoggerFactory.getLogger(ZooKeeperVersionRepo.class);
    
    private static final int DEFAULT_SESSION_TIMEOUT = 60000; // 60s
    private static final int DEFAULT_MAX_RETRY_COUNT = 10;
    private static final int DEFAULT_RETRY_INTERVAL_MS = 100; // 100ms
    
    private final RecoverableZooKeeper zookeeper;
    
    public ZooKeeperVersionRepo(String quorumServers) throws KeeperException {
        this(quorumServers, DEFAULT_SESSION_TIMEOUT, DEFAULT_MAX_RETRY_COUNT, DEFAULT_RETRY_INTERVAL_MS);
    }
    
    public ZooKeeperVersionRepo(String quorumServers, int sessionTimeout, 
            int maxRetries, int retryIntervalMillis) throws KeeperException {
        zookeeper = new RecoverableZooKeeper(quorumServers, sessionTimeout, this,
                maxRetries, retryIntervalMillis);
    }
    
    @Override
    public Version getVersion(ObjectName name) {
        return getVersion(name, null);
    }
    
    @Override
    public Version incrementVersion(ObjectName name, long sequence) {
        String path = name.toPath();
        while (true) {
            try {
                Stat stat = new Stat();
                byte[] versionData = zookeeper.getData(name.toPath(), false, stat);
                Version curVersion = VersionProtoUtils.toVersion(versionData);
                Version nextVersion = curVersion.nextVersion(sequence);
                zookeeper.setData(path, VersionProtoUtils.toData(nextVersion), stat.getVersion());
                return nextVersion;
            } catch (KeeperException e) {
                if (e instanceof KeeperException.NoNodeException) {
                    recursiveCreate(name, sequence);
                } else {
                    throw new RuntimeException(e);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (InvalidProtocolBufferException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    @Override
    public void shutdown() {
        if (zookeeper != null) {
            try {
                zookeeper.close();
            } catch (InterruptedException e) {
                logger.error("zookeeper client close interrupted", e);
            }
        }
    }
    
    private Version getVersion(ObjectName name, Stat stat) {
        try {
            byte[] versionData = zookeeper.getData(name.toPath(), false, stat);
            return VersionProtoUtils.toVersion(versionData);
        } catch (KeeperException e) {
            if (e instanceof KeeperException.NoNodeException) {
                return Version.NO_VERSION;
            }
            
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void recursiveCreate(ObjectName name, long sequence) {
        String[] paths = name.toPaths();
        for (String path : paths) {
            try {
                Stat stat = zookeeper.exists(path, false);
                if (stat != null) {
                    continue;
                }
                byte[] versionData = VersionProtoUtils.toData(new Version(0, sequence));
                zookeeper.create(path, versionData, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            } catch (KeeperException e) {
                if (e instanceof KeeperException.NodeExistsException) {
                    logger.debug("path already exists: " + path);
                } else {
                    throw new RuntimeException(e);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void process(WatchedEvent event) {
        logger.debug(prefix("Received ZooKeeper Event, type={}, state={}, path={}"),
                event.getType(), event.getState(), event.getPath());

        // Only handle Connection Event
        if (event.getType() == EventType.None) {
            connectionEvent(event);
        }
    }
    
    private void connectionEvent(WatchedEvent event) {
        switch(event.getState()) {
          case SyncConnected:
            logger.info(prefix("Received SyncConnected connection event from zookeeper"));
            break;

          case Disconnected:
            logger.warn(prefix("Received Disconnected connection event from zookeeper"));
            break;

          case Expired:
            logger.warn(prefix("Received Expired connection event from zookeeper"));
            break;

          case ConnectedReadOnly:
          case SaslAuthenticated:
          case AuthFailed:
            break;

          default:
            throw new IllegalStateException("Received event is not valid: " + event.getState());
        }
      }
    
    private String prefix(String msg) {
        return "identifier: " + zookeeper.getIdentifier() + ", quorum: " + 
                zookeeper.getQuorumServers() + " " + msg;
    }
}
