package org.levin.zookeeper.versionrepo;

import com.google.common.base.Objects;

public class Version {
    public static final Version NO_VERSION = new Version(-1, -1);
    
    private final int version;
    private final long sequence;
    
    public Version(int version, long sequence) {
        this.version = version;
        this.sequence = sequence;
    }
    
    public int getVersion() {
        return version;
    }
    
    public long getSequence() {
        return sequence;
    }
    
    public Version nextVersion(long sequence) {
        return new Version(version + 1, sequence);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(version, sequence);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (!(obj instanceof Version)) {
            return false;
        }
        
        return Objects.equal(version, ((Version)obj).version) &&
                Objects.equal(sequence, ((Version)obj).sequence);
    }
    
    public String toString() {
        return "version: " + version + ", sequence: " + sequence;
    }
}
