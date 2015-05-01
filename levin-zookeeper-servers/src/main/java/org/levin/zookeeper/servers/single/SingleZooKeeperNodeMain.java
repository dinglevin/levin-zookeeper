package org.levin.zookeeper.servers.single;

import org.apache.zookeeper.server.ZooKeeperServerMain;

public class SingleZooKeeperNodeMain {
    public static void main(String[] args) {
        ZooKeeperServerMain.main(new String[] { "2181", "data" });
    }
}
