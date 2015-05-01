package org.levin.zookeeper.versionrepo;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

public class ZooKeeperVersionRepoTest {
    private static final Logger logger = LoggerFactory.getLogger(ZooKeeperVersionRepoTest.class);
    
    private static VersionRepo versionRepo;
    private static ZooKeeperServerTestRunner testServerRunner;
    
    @BeforeClass
    public static void setup() throws Exception {
        int port = 62181;
        String dataDir = "tmp/test_data";
        testServerRunner = new ZooKeeperServerTestRunner(port, dataDir);
        CountDownLatch startupLatch = testServerRunner.startDaemon();
        
        startupLatch.await(5, TimeUnit.MINUTES);
        logger.info("ZooKeeper embeded server successfully startup");
        
        versionRepo = new ZooKeeperVersionRepo("localhost:" + port);
    }
    
    @AfterClass
    public static void tearDown() throws Exception {
        versionRepo.shutdown();
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
    
    @Test
    public void testIncrementNotExistPerfTest() {
        ObjectName name = new ObjectName("perf", "not-exist");
        int testNum = 10000;
        Stopwatch stopwatch = Stopwatch.createStarted();
        for (int i = 0; i < testNum; i++) {
            Version version = versionRepo.incrementVersion(name.appendNode("child-" + i), System.currentTimeMillis());
            assertEquals(1, version.getVersion());
        }
        stopwatch.stop();
        System.out.println("not exist test: " + testNum + " took: " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + 
                "ms, avg: " + stopwatch.elapsed(TimeUnit.MILLISECONDS) / (double)testNum + "ms");
    }
    
    @Test
    public void testIncrementExistPerfTest() {
        ObjectName name = new ObjectName("perf", "exist", "child");
        int testNum = 10000;
        Stopwatch stopwatch = Stopwatch.createStarted();
        for (int i = 0; i < testNum; i++) {
            Version version = versionRepo.incrementVersion(name, System.currentTimeMillis());
            assertEquals(i + 1, version.getVersion());
        }
        stopwatch.stop();
        System.out.println("exist test: " + testNum + " took: " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + 
                "ms, avg: " + stopwatch.elapsed(TimeUnit.MILLISECONDS) / (double)testNum + "ms");
    }
}
