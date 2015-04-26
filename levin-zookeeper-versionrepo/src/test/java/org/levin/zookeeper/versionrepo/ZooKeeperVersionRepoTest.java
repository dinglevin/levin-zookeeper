package org.levin.zookeeper.versionrepo;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Ignore
public class ZooKeeperVersionRepoTest {
    private static final Logger logger = LoggerFactory.getLogger(ZooKeeperVersionRepoTest.class);
    
    private static VersionRepo versionRepo;
    private static ZooKeeperServerTestRunner testServerRunner;
    
    @BeforeClass
    public static void setup() throws Exception {
        testServerRunner = new ZooKeeperServerTestRunner();
        CountDownLatch startupLatch = testServerRunner.startDaemon();
        
        startupLatch.await(5, TimeUnit.MINUTES);
        logger.info("ZooKeeper embeded server successfully startup");
        
        versionRepo = new ZooKeeperVersionRepo("localhost:2811");
    }
    
    @AfterClass
    public static void tearDown() throws Exception {
        testServerRunner.stop();
    }
    
    @Test
    public void testGetNoExistObjectNameVersion() {
        ObjectName name = new ObjectName("not", "exist", "name");
        Version version = versionRepo.getVersion(name);
        assertEquals(-1, version.getVersion());
        assertEquals(-1, version.getSequence());
    }
    
    @Test
    public void testIncrementNonExistObjectNameVersion() {
        long sequence = System.currentTimeMillis();
        ObjectName name = new ObjectName("init", "not-exist", Long.toString(sequence));
        Version version1 = versionRepo.incrementVersion(name, sequence);
        Version version2 = versionRepo.incrementVersion(name, sequence + 1);
        
        assertEquals(1, version1.getVersion());
        assertEquals(sequence, version1.getSequence());
        assertEquals(2, version2.getVersion());
        assertEquals(sequence + 1, version2.getSequence());
    }
}
