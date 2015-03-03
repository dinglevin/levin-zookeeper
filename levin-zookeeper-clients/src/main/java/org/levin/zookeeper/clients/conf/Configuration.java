package org.levin.zookeeper.clients.conf;

public interface Configuration {
    public void writeConf(String key, byte[] value);
    public byte[] readConf(String key);
    public boolean exists(String key);
}
