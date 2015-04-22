package org.levin.zookeeper.versionrepo;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZooKeeperVersionRepo implements VersionRepo, Watcher {
    private static final Logger logger = LoggerFactory.getLogger(ZooKeeperVersionRepo.class);
    
    private static final int DEFAULT_SESSION_TIMEOUT = 60000; // 60s
    
    private final ZooKeeper zookeeper;
    
    public ZooKeeperVersionRepo(String quorumServers) throws IOException {
        this(quorumServers, DEFAULT_SESSION_TIMEOUT);
    }
    
    public ZooKeeperVersionRepo(String quorumServers, int sessionTimeout) throws IOException {
        zookeeper = new ZooKeeper(quorumServers, sessionTimeout, this);
    }
    
    @Override
    public int getVersion(ObjectName name) {
        Stat stat = new Stat();
        try {
            zookeeper.getData(name.toPath(), false, stat);
        } catch (KeeperException e) {
            if (e instanceof KeeperException.NoNodeException) {
                return -1;
            }
            
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        
        return stat.getVersion();
    }
    
    @Override
    public int incrementVersion(ObjectName name) {
        int curVersion = getVersion(name);
        String path = name.toPath();
        while (true) {
            byte[] data = getTimestampBytes();
            try {
                Stat stat = zookeeper.setData(path, data, curVersion);
                return stat.getVersion();
            } catch (KeeperException e) {
                if (e instanceof KeeperException.NoNodeException) {
                    recursiveCreate(name);
                } else {
                    throw new RuntimeException(e);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    public void recursiveCreate(ObjectName name) {
        String[] paths = name.toPaths();
        for (String path : paths) {
            try {
                zookeeper.create(path, getTimestampBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
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
    
    private static byte[] getTimestampBytes() {
        return Long.toString(System.currentTimeMillis()).getBytes();
    }

    @Override
    public void process(WatchedEvent event) {
        logger.info("Received zookeer event: " + event);
    }
}
