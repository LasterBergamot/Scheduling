package com.scheduling.model.graph.node;

import com.scheduling.model.util.ClassWithID;

public class Node extends ClassWithID {

    private NodeType nodeType;

    public Node() {
        this.id = ID_COUNTER.getAndIncrement();
    }
}
