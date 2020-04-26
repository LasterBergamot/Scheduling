package com.scheduling.model.station;

import com.scheduling.model.util.ClassWithID;

public class TerminalStation extends ClassWithID {

    // Can be Vá.1 or Vá.2 (station1 or station2)
    private String stationName;

    private Timeline timeline;

    public TerminalStation() {
        this.id = ID_COUNTER.getAndIncrement();
    }

    public TerminalStation(String stationName) {
        this.id = ID_COUNTER.getAndIncrement();

        this.stationName = stationName;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
    }
}
