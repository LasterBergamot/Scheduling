package com.scheduling.model.graph.node;

import com.scheduling.model.util.ClassWithID;

import java.time.LocalTime;

public class Node extends ClassWithID {

    private NodeType nodeType;

    private LocalTime localTime;

    public Node() {
        this.id = ID_COUNTER.getAndIncrement();
    }
}
