package org.levin.zookeeper.clients.conf;

import java.io.IOException;
import java.lang.invoke.ConstantCallSite;

import org.apache.zookeeper.KeeperException;
import org.levin.zookeeper.clients.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleNodeConfiguration extends AbstractConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(SingleNodeConfiguration.class);
    
    private static final String ROOT = "/levin/conf/singlenode";
    
    public SingleNodeConfiguration(String connString, int sessionTimeout) 
            throws IOException, KeeperException {
        super(connString, sessionTimeout, ROOT);
    }

    @Override
    public void writeConf(String key, byte[] value) {
        
    }

    @Override
    public byte[] readConf(String key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean exists(String key) {
        // TODO Auto-generated method stub
        return false;
    }
    
    public static void main(String[] args) throws Exception {
        Configuration conf = new SingleNodeConfiguration(Constants.CONNECT_STRING, 
                Constants.DEFAULT_SESSION_TIMEOUT);
        conf.writeConf("key1", "value1".getBytes());
    }

}
