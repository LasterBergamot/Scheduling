package com.scheduling.model.depot;

import com.scheduling.model.station.Timeline;
import com.scheduling.model.util.ClassWithID;

public class Depot extends ClassWithID {

    // Vá.1_Depot or Vá.2_Depot (Station1_Depot or Station2_Depot)
    private String depotName;

    // The ID of the station where this depot is used
    private int stationId;

   // Inside this: departure- and arrivalNodes for this Depot, so basically one for each type
    private Timeline timeline;

    public Depot() {
        this.id = ID_COUNTER.getAndIncrement();
    }
}
