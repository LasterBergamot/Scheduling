package com.scheduling.model.depot;

import com.scheduling.model.graph.node.Node;
import com.scheduling.model.station.Timeline;

public class Depot {

    // Vá.1_Depot or Vá.2_Depot (Station1_Depot or Station2_Depot)
    private String depotName;

    // The ID of the station where this depot is used
    private int stationId;

    private Node depotDeparture;
    private Node depotArrival;
    private Timeline timeline;
}
