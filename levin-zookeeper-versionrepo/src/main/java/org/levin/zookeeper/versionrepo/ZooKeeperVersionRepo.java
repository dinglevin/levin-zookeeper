package org.levin.zookeeper.versionrepo;

import org.apache.zookeeper.ZooKeeper;

public class ZooKeeperVersionRepo {
    private final ZooKeeper zookeeper;
    
    public ZooKeeperVersionRepo(String connectionString) {
        zookeeper = new ZooKeeper(connectionString);
    }
}
