package com.scheduling.model.util;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class ClassWithID {

    protected static AtomicInteger ID_COUNTER = new AtomicInteger(1);
    protected int id;

    public int getId() {
        return id;
    }
}
