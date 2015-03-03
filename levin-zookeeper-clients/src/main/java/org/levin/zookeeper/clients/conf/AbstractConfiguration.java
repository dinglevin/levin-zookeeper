package org.levin.zookeeper.clients.conf;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractConfiguration implements Configuration, Watcher {
    private static final Logger logger = LoggerFactory.getLogger(AbstractConfiguration.class);
    
    protected final String connString;
    protected final ZooKeeper zookeeper;
    
    protected AbstractConfiguration(String connString, int sessionTimeout,
            String rootPath) throws IOException, KeeperException {
        this.connString = connString;
        this.zookeeper = new ZooKeeper(connString, sessionTimeout, this);
        
        createIfNotExists(rootPath);
    }
    
    @Override
    public void process(WatchedEvent event) {
        System.out.println("Triggered event: " + event);
    }
    
    protected void createIfNotExists(String path) throws KeeperException {
        try {
            String createdPath = zookeeper.create(path, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            logger.info("Created node: " + createdPath);
        } catch (InterruptedException ex) {
            logger.error("Creating node: " + path + " interrupted", ex);
            throw new RuntimeException(ex);
        }
    }

}
