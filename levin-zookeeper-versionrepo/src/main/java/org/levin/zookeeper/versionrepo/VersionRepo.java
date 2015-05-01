package org.levin.zookeeper.versionrepo;

public interface VersionRepo {
    public Version getVersion(ObjectName name);
    public Version incrementVersion(ObjectName name, long sequence);
    
    public void shutdown();
}
