package org.levin.zookeeper.versionrepo;

import java.io.IOException;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZooKeeperVersionRepo implements Watcher {
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
    public void process(WatchedEvent event) {
        logger.info("Received zookeer event: " + event);
    }
}
