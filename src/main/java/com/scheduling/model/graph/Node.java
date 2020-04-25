package com.scheduling.model.graph;

import com.scheduling.model.util.ClassWithID;

public class Node extends ClassWithID {

    public Node() {
        this.id = ID_COUNTER.getAndIncrement();
    }
}
