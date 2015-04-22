package org.levin.zookeeper.versionrepo;

public interface VersionRepo {
    public int getVersion(ObjectName name);
    public int incrementVersion(ObjectName name);
}
