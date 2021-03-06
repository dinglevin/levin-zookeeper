// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: Version.proto

package org.levin.zookeeper.versionrepo.generated;

public final class VersionProtos {
  private VersionProtos() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface VersionOrBuilder
      extends com.google.protobuf.MessageOrBuilder {

    // optional uint32 version = 1;
    /**
     * <code>optional uint32 version = 1;</code>
     */
    boolean hasVersion();
    /**
     * <code>optional uint32 version = 1;</code>
     */
    int getVersion();

    // optional uint64 sequence = 2;
    /**
     * <code>optional uint64 sequence = 2;</code>
     */
    boolean hasSequence();
    /**
     * <code>optional uint64 sequence = 2;</code>
     */
    long getSequence();
  }
  /**
   * Protobuf type {@code Version}
   */
  public static final class Version extends
      com.google.protobuf.GeneratedMessage
      implements VersionOrBuilder {
    // Use Version.newBuilder() to construct.
    private Version(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
      this.unknownFields = builder.getUnknownFields();
    }
    private Version(boolean noInit) { this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); }

    private static final Version defaultInstance;
    public static Version getDefaultInstance() {
      return defaultInstance;
    }

    public Version getDefaultInstanceForType() {
      return defaultInstance;
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;
    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
      return this.unknownFields;
    }
    private Version(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      initFields();
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 8: {
              bitField0_ |= 0x00000001;
              version_ = input.readUInt32();
              break;
            }
            case 16: {
              bitField0_ |= 0x00000002;
              sequence_ = input.readUInt64();
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e.getMessage()).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.levin.zookeeper.versionrepo.generated.VersionProtos.internal_static_Version_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.levin.zookeeper.versionrepo.generated.VersionProtos.internal_static_Version_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.levin.zookeeper.versionrepo.generated.VersionProtos.Version.class, org.levin.zookeeper.versionrepo.generated.VersionProtos.Version.Builder.class);
    }

    public static com.google.protobuf.Parser<Version> PARSER =
        new com.google.protobuf.AbstractParser<Version>() {
      public Version parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new Version(input, extensionRegistry);
      }
    };

    @java.lang.Override
    public com.google.protobuf.Parser<Version> getParserForType() {
      return PARSER;
    }

    private int bitField0_;
    // optional uint32 version = 1;
    public static final int VERSION_FIELD_NUMBER = 1;
    private int version_;
    /**
     * <code>optional uint32 version = 1;</code>
     */
    public boolean hasVersion() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>optional uint32 version = 1;</code>
     */
    public int getVersion() {
      return version_;
    }

    // optional uint64 sequence = 2;
    public static final int SEQUENCE_FIELD_NUMBER = 2;
    private long sequence_;
    /**
     * <code>optional uint64 sequence = 2;</code>
     */
    public boolean hasSequence() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>optional uint64 sequence = 2;</code>
     */
    public long getSequence() {
      return sequence_;
    }

    private void initFields() {
      version_ = 0;
      sequence_ = 0L;
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized != -1) return isInitialized == 1;

      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeUInt32(1, version_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeUInt64(2, sequence_);
      }
      getUnknownFields().writeTo(output);
    }

    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(1, version_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt64Size(2, sequence_);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @java.lang.Override
    protected java.lang.Object writeReplace()
        throws java.io.ObjectStreamException {
      return super.writeReplace();
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof org.levin.zookeeper.versionrepo.generated.VersionProtos.Version)) {
        return super.equals(obj);
      }
      org.levin.zookeeper.versionrepo.generated.VersionProtos.Version other = (org.levin.zookeeper.versionrepo.generated.VersionProtos.Version) obj;

      boolean result = true;
      result = result && (hasVersion() == other.hasVersion());
      if (hasVersion()) {
        result = result && (getVersion()
            == other.getVersion());
      }
      result = result && (hasSequence() == other.hasSequence());
      if (hasSequence()) {
        result = result && (getSequence()
            == other.getSequence());
      }
      result = result &&
          getUnknownFields().equals(other.getUnknownFields());
      return result;
    }

    private int memoizedHashCode = 0;
    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptorForType().hashCode();
      if (hasVersion()) {
        hash = (37 * hash) + VERSION_FIELD_NUMBER;
        hash = (53 * hash) + getVersion();
      }
      if (hasSequence()) {
        hash = (37 * hash) + SEQUENCE_FIELD_NUMBER;
        hash = (53 * hash) + hashLong(getSequence());
      }
      hash = (29 * hash) + getUnknownFields().hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static org.levin.zookeeper.versionrepo.generated.VersionProtos.Version parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.levin.zookeeper.versionrepo.generated.VersionProtos.Version parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.levin.zookeeper.versionrepo.generated.VersionProtos.Version parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.levin.zookeeper.versionrepo.generated.VersionProtos.Version parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.levin.zookeeper.versionrepo.generated.VersionProtos.Version parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static org.levin.zookeeper.versionrepo.generated.VersionProtos.Version parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }
    public static org.levin.zookeeper.versionrepo.generated.VersionProtos.Version parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }
    public static org.levin.zookeeper.versionrepo.generated.VersionProtos.Version parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }
    public static org.levin.zookeeper.versionrepo.generated.VersionProtos.Version parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static org.levin.zookeeper.versionrepo.generated.VersionProtos.Version parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(org.levin.zookeeper.versionrepo.generated.VersionProtos.Version prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code Version}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder>
       implements org.levin.zookeeper.versionrepo.generated.VersionProtos.VersionOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.levin.zookeeper.versionrepo.generated.VersionProtos.internal_static_Version_descriptor;
      }

      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.levin.zookeeper.versionrepo.generated.VersionProtos.internal_static_Version_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                org.levin.zookeeper.versionrepo.generated.VersionProtos.Version.class, org.levin.zookeeper.versionrepo.generated.VersionProtos.Version.Builder.class);
      }

      // Construct using org.levin.zookeeper.versionrepo.generated.VersionProtos.Version.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessage.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
        }
      }
      private static Builder create() {
        return new Builder();
      }

      public Builder clear() {
        super.clear();
        version_ = 0;
        bitField0_ = (bitField0_ & ~0x00000001);
        sequence_ = 0L;
        bitField0_ = (bitField0_ & ~0x00000002);
        return this;
      }

      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.levin.zookeeper.versionrepo.generated.VersionProtos.internal_static_Version_descriptor;
      }

      public org.levin.zookeeper.versionrepo.generated.VersionProtos.Version getDefaultInstanceForType() {
        return org.levin.zookeeper.versionrepo.generated.VersionProtos.Version.getDefaultInstance();
      }

      public org.levin.zookeeper.versionrepo.generated.VersionProtos.Version build() {
        org.levin.zookeeper.versionrepo.generated.VersionProtos.Version result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public org.levin.zookeeper.versionrepo.generated.VersionProtos.Version buildPartial() {
        org.levin.zookeeper.versionrepo.generated.VersionProtos.Version result = new org.levin.zookeeper.versionrepo.generated.VersionProtos.Version(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.version_ = version_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.sequence_ = sequence_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof org.levin.zookeeper.versionrepo.generated.VersionProtos.Version) {
          return mergeFrom((org.levin.zookeeper.versionrepo.generated.VersionProtos.Version)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(org.levin.zookeeper.versionrepo.generated.VersionProtos.Version other) {
        if (other == org.levin.zookeeper.versionrepo.generated.VersionProtos.Version.getDefaultInstance()) return this;
        if (other.hasVersion()) {
          setVersion(other.getVersion());
        }
        if (other.hasSequence()) {
          setSequence(other.getSequence());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }

      public final boolean isInitialized() {
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        org.levin.zookeeper.versionrepo.generated.VersionProtos.Version parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (org.levin.zookeeper.versionrepo.generated.VersionProtos.Version) e.getUnfinishedMessage();
          throw e;
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      // optional uint32 version = 1;
      private int version_ ;
      /**
       * <code>optional uint32 version = 1;</code>
       */
      public boolean hasVersion() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>optional uint32 version = 1;</code>
       */
      public int getVersion() {
        return version_;
      }
      /**
       * <code>optional uint32 version = 1;</code>
       */
      public Builder setVersion(int value) {
        bitField0_ |= 0x00000001;
        version_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional uint32 version = 1;</code>
       */
      public Builder clearVersion() {
        bitField0_ = (bitField0_ & ~0x00000001);
        version_ = 0;
        onChanged();
        return this;
      }

      // optional uint64 sequence = 2;
      private long sequence_ ;
      /**
       * <code>optional uint64 sequence = 2;</code>
       */
      public boolean hasSequence() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>optional uint64 sequence = 2;</code>
       */
      public long getSequence() {
        return sequence_;
      }
      /**
       * <code>optional uint64 sequence = 2;</code>
       */
      public Builder setSequence(long value) {
        bitField0_ |= 0x00000002;
        sequence_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional uint64 sequence = 2;</code>
       */
      public Builder clearSequence() {
        bitField0_ = (bitField0_ & ~0x00000002);
        sequence_ = 0L;
        onChanged();
        return this;
      }

      // @@protoc_insertion_point(builder_scope:Version)
    }

    static {
      defaultInstance = new Version(true);
      defaultInstance.initFields();
    }

    // @@protoc_insertion_point(class_scope:Version)
  }

  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_Version_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_Version_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\rVersion.proto\",\n\007Version\022\017\n\007version\030\001 " +
      "\001(\r\022\020\n\010sequence\030\002 \001(\004B?\n)org.levin.zooke" +
      "eper.versionrepo.generatedB\rVersionProto" +
      "sH\001\240\001\001"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_Version_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_Version_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_Version_descriptor,
              new java.lang.String[] { "Version", "Sequence", });
          return null;
        }
      };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
  }

  // @@protoc_insertion_point(outer_class_scope)
}
