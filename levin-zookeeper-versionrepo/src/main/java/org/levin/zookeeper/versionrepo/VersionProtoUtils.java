package org.levin.zookeeper.versionrepo;

import org.levin.zookeeper.versionrepo.generated.VersionProtos;

import com.google.protobuf.InvalidProtocolBufferException;

public final class VersionProtoUtils {
    public VersionProtoUtils() { }
    
    public static Version toVersion(byte[] data) throws InvalidProtocolBufferException {
        VersionProtos.Version version = VersionProtos.Version.parseFrom(data);
        return toVersion(version);
    }
    
    public static Version toVersion(VersionProtos.Version version) {
        return new Version(version.getVersion(), version.getSequence());
    }
    
    public static byte[] toData(Version version) {
        VersionProtos.Version protoVersion = toProtoVersion(version);
        return protoVersion.toByteArray();
    }
    
    public static VersionProtos.Version toProtoVersion(Version version) {
        return VersionProtos.Version.newBuilder()
                .setVersion(version.getVersion())
                .setSequence(version.getSequence())
                .build();
    }
}
