package com.scheduling.model.graph.edge;

import java.util.Objects;

public class Edge {

    private EdgeType edgeType;
    private int departureNodeID;
    private int arrivalNodeID;
    private int weight = 0;

    public Edge(EdgeType edgeType, int departureNodeID, int arrivalNodeID) {
        this.edgeType = edgeType;
        this.departureNodeID = departureNodeID;
        this.arrivalNodeID = arrivalNodeID;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return departureNodeID == edge.departureNodeID &&
                arrivalNodeID == edge.arrivalNodeID &&
                edgeType == edge.edgeType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(edgeType, departureNodeID, arrivalNodeID);
    }
}
