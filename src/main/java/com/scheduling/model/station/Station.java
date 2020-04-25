package com.scheduling.model.station;

import com.scheduling.model.util.ClassWithID;

public class Station extends ClassWithID {

    // Can be Vá.1 or Vá.2 (station1 or station2)
    private String stationName;
    private Timeline timeline;

    public Station() {
        this.id = ID_COUNTER.getAndIncrement();
    }
}
