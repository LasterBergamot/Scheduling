package com.scheduling.model.graph.node;

import com.scheduling.model.util.ClassWithID;

import java.time.LocalTime;

public class Node extends ClassWithID {

    private NodeType nodeType;

    private LocalTime localTime;

    public Node() {
        this.id = ID_COUNTER.getAndIncrement();
    }

    public Node(NodeType nodeType, LocalTime localTime) {
        this.id = ID_COUNTER.getAndIncrement();

        this.nodeType = nodeType;
        this.localTime = localTime;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public LocalTime getLocalTime() {
        return localTime;
    }

    public void setLocalTime(LocalTime localTime) {
        this.localTime = localTime;
    }

    @Override
    public String toString() {
        return "Node{" +
                "nodeType=" + nodeType +
                ", localTime=" + localTime +
                ", id=" + id +
                '}';
    }
}
