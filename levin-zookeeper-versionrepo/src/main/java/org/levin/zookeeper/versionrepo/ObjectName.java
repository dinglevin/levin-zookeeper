package org.levin.zookeeper.versionrepo;

import java.io.Serializable;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;

public class ObjectName implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Node[] ROOT_NODES = new Node[0];
    
    public static final String SEPARATOR = "/";
    
    private final Node[] nodes;
    
    public ObjectName() { 
        nodes = ROOT_NODES;
    }
    
    public ObjectName(String... nodes) {
        if (nodes.length == 0) {
            this.nodes = ROOT_NODES;
        } else {
            this.nodes = new Node[nodes.length];
            for (int i = 0; i < nodes.length; i++) {
                this.nodes[i] = new Node(nodes[i]);
            }
        }
    }
    
    // Used internally
    private ObjectName(Node[] nodes) {
        this.nodes = nodes;
    }
    
    public ObjectName appendNode(String node) {
        if (nodes == ROOT_NODES) {
            return new ObjectName(node);
        }
        
        Node newNode = new Node(node);
        Node[] newNodes = new Node[nodes.length + 1];
        System.arraycopy(nodes, 0, newNodes, 0, nodes.length);
        newNodes[nodes.length] = newNode;
        return new ObjectName(newNodes);
    }
    
    public ObjectName prependNode(String node) {
        if (nodes == ROOT_NODES) {
            return new ObjectName(node);
        }
        
        Node newNode = new Node(node);
        Node[] newNodes = new Node[nodes.length + 1];
        System.arraycopy(nodes, 1, newNodes, 0, nodes.length);
        newNodes[0] = newNode;
        return new ObjectName(newNodes);
    }
    
    private static void validateNodeName(String name) {
        checkNotNull(name, "name is null");
        
        name = name.trim();
        checkArgument(name.isEmpty(), "name is empty");
        checkArgument(name.contains(SEPARATOR), "name contains " + SEPARATOR);
    }
    
    public String toPath() {
        if (nodes == ROOT_NODES) {
            return SEPARATOR;
        }
        
        StringBuilder builder = new StringBuilder();
        for (Node node : nodes) {
            builder.append(SEPARATOR).append(node.name);
        }
        return builder.toString();
    }
    
    public String[] toPaths() {
        if (nodes == ROOT_NODES) {
            throw new IllegalStateException("No paths for root node");
        }
        
        String[] results = new String[nodes.length];
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < nodes.length; i++) {
            builder.append(SEPARATOR).append(nodes[i].name);
            results[i] = builder.toString();
        }
        
        return results;
    }
    
    private static class Node {
        public final String name;
        
        public Node(String name) {
            validateNodeName(name);
            
            this.name = name;
        }
        
        @Override
        public int hashCode() {
            return name.hashCode();
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            
            if (!(obj instanceof Node)) {
                return false;
            }
            
            return name.equals(((Node)obj).name);
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
}
