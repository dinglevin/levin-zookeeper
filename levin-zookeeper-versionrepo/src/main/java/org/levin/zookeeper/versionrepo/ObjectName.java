package org.levin.zookeeper.versionrepo;

import java.io.Serializable;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;

public class ObjectName implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Node[] EMPTY_NODES = new Node[0];
    
    private final Node[] nodes;
    
    public ObjectName() { 
        nodes = EMPTY_NODES;
    }
    
    public ObjectName(String... nodes) {
        if (nodes.length == 0) {
            this.nodes = EMPTY_NODES;
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
        if (nodes.length == 0) {
            return new ObjectName(node);
        }
        
        Node newNode = new Node(node);
        Node[] newNodes = new Node[nodes.length + 1];
        System.arraycopy(nodes, 0, newNodes, 0, nodes.length);
        newNodes[nodes.length] = newNode;
        return new ObjectName(newNodes);
    }
    
    public ObjectName prependNode(String node) {
        if (nodes.length == 0) {
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
        checkArgument(name.contains("/"), "name contains '/'");
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
