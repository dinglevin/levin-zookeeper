package org.levin.zookeeper.versionrepo;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import javax.management.JMException;

import org.apache.commons.io.FileUtils;
import org.apache.zookeeper.jmx.ManagedUtil;
import org.apache.zookeeper.server.ServerCnxnFactory;
import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZooKeeperServerTestRunner {
    private static final Logger logger = LoggerFactory.getLogger(ZooKeeperServerTestRunner.class);
    
    private static final int DEFAULT_PORT = 2181;
    private static final String DEFAULT_DATA_DIR = "tmp/test_data";
    
    private ServerCnxnFactory cnxnFactory;
    private CountDownLatch startupLatch;
    private int port;
    private String dataDir;
    
    private Thread runnerThread;
    
    public ZooKeeperServerTestRunner() {
        this(DEFAULT_PORT, DEFAULT_DATA_DIR);
    }
    
    public ZooKeeperServerTestRunner(int port, String dataDir) {
        this.port = port;
        this.dataDir = dataDir;
        
        cleanupDataDir();
        
        if (!new File(dataDir).mkdirs()) {
            throw new IllegalStateException("Create data path fail: " + dataDir);
        }
    }
    
    public CountDownLatch startDaemon() {
        this.startupLatch = new CountDownLatch(1);
        this.runnerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                start();
            }
        }, "ZooKeeper-Runner");
        this.runnerThread.setDaemon(true);
        this.runnerThread.start();
        
        return this.startupLatch;
    }
    
    public void start() {
        try {
            ManagedUtil.registerLog4jMBeans();
        } catch (JMException e) {
            logger.warn("Unable to register log4j JMX control", e);
        }

        ServerConfig config = new ServerConfig();
        config.parse(new String[] { Integer.toString(port), dataDir });

        try {
            runFromConfig(config);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void stop() {
        if (cnxnFactory != null) {
            cnxnFactory.shutdown();
        }
        cleanupDataDir();
    }
    
    private void runFromConfig(ServerConfig config) throws IOException {
        logger.info("Starting server");
        FileTxnSnapLog txnLog = null;
        try {
            // Note that this thread isn't going to be doing anything else,
            // so rather than spawning another thread, we will just call
            // run() in this thread.
            // create a file logger url from the command line args
            ZooKeeperServer zkServer = new ZooKeeperServer();

            txnLog = new FileTxnSnapLog(new File(config.getDataLogDir()), new File(config.getDataDir()));
            zkServer.setTxnLogFactory(txnLog);
            zkServer.setTickTime(config.getTickTime());
            zkServer.setMinSessionTimeout(config.getMinSessionTimeout());
            zkServer.setMaxSessionTimeout(config.getMaxSessionTimeout());
            cnxnFactory = ServerCnxnFactory.createFactory();
            cnxnFactory.configure(config.getClientPortAddress(), config.getMaxClientCnxns());
            cnxnFactory.startup(zkServer);
            
            if (startupLatch != null) {
                startupLatch.countDown();
            }
            
            cnxnFactory.join();
            if (zkServer.isRunning()) {
                zkServer.shutdown();
            }
        } catch (InterruptedException e) {
            // warn, but generally this is ok
            logger.warn("Server interrupted", e);
        } finally {
            if (txnLog != null) {
                txnLog.close();
            }
        }
    }
    
    private void cleanupDataDir() {
        File dataPath = new File(dataDir);
        if (dataPath.exists()) {
            try {
                FileUtils.deleteDirectory(dataPath);
            } catch (IOException e) {
                throw new RuntimeException("delete path fail: " + dataDir, e);
            }
        }
    }
}
