package com.scheduling.model.depot;

import com.scheduling.model.station.Timeline;
import com.scheduling.model.util.ClassWithID;

public class Depot extends ClassWithID {

    private Timeline timeline;

    public Depot() {
        this.id = ID_COUNTER.getAndIncrement();
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
    }
}
