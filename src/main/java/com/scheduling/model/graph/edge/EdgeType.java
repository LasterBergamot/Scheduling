package com.scheduling.model.graph.edge;

public enum EdgeType {
    SERVICE("Service"), DEPOT("Depot"), WAITING("Waiting"), OVERHEAD("Overhead"); // Overhead = rezsi

    private final String name;

    EdgeType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
