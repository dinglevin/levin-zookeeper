package org.levin.zookeeper.versionrepo;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZooKeeperVersionRepoTest {
    private static final Logger logger = LoggerFactory.getLogger(ZooKeeperVersionRepoTest.class);
    
    private static VersionRepo versionRepo;
    private static ZooKeeperServerTestRunner testServerRunner;
    
    @BeforeClass
    public static void setup() throws Exception {
        //testServerRunner = new ZooKeeperServerTestRunner();
        //testServerRunner.start();
        //CountDownLatch startupLatch = testServerRunner.startDaemon();
        
        //startupLatch.await(5, TimeUnit.MINUTES);
        //logger.info("ZooKeeper embeded server successfully startup");
        
        versionRepo = new ZooKeeperVersionRepo("127.0.0.1:2811");
    }
    
    @AfterClass
    public static void tearDown() throws Exception {
        //testServerRunner.stop();
    }
    
    @Test
    public void testGetNoExistObjectNameVersion() {
        ObjectName name = new ObjectName("not", "exist", "name");
        int version = versionRepo.getVersion(name);
        assertEquals(-1, version);
    }
    
    @Test
    public void testIncrementNonExistObjectNameVersion() {
        ObjectName name = new ObjectName("init", "not-exist", Long.toString(System.currentTimeMillis()));
        int version1 = versionRepo.incrementVersion(name);
        int version2 = versionRepo.incrementVersion(name);
        
        assertEquals(1, version1);
        assertEquals(2, version2);
    }
}
