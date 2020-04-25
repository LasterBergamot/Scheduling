package com.scheduling.model.graph.node;

public enum NodeType {
    DEPARTURE("Departure"), ARRIVAL("Arrival"), DEPOT_DEPARTURE("Depot departure"), DEPOT_ARRIVAL("Depot arrival");

    private final String name;

    NodeType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
