package com.scheduling.model.graph.edge;

public class Edge {

    private EdgeType edgeType;
    private int departureNodeID;
    private int arrivalNodeID;
    private int weight;

    public Edge(EdgeType edgeType, int departureNodeID, int arrivalNodeID, int weight) {
        this.edgeType = edgeType;
        this.departureNodeID = departureNodeID;
        this.arrivalNodeID = arrivalNodeID;
        this.weight = weight;
    }

    public EdgeType getEdgeType() {
        return edgeType;
    }

    public void setEdgeType(EdgeType edgeType) {
        this.edgeType = edgeType;
    }

    public int getDepartureNodeID() {
        return departureNodeID;
    }

    public void setDepartureNodeID(int departureNodeID) {
        this.departureNodeID = departureNodeID;
    }

    public int getArrivalNodeID() {
        return arrivalNodeID;
    }

    public void setArrivalNodeID(int arrivalNodeID) {
        this.arrivalNodeID = arrivalNodeID;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
