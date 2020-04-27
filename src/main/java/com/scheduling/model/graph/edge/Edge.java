package com.scheduling.model.graph.edge;

import java.util.Objects;

public class Edge {

    private EdgeType edgeType;
    private int departureNodeID;
    private int arrivalNodeID;

    public Edge(EdgeType edgeType, int departureNodeID, int arrivalNodeID) {
        this.edgeType = edgeType;
        this.departureNodeID = departureNodeID;
        this.arrivalNodeID = arrivalNodeID;
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

    @Override
    public String toString() {
        return "Edge{" +
                "edgeType=" + edgeType +
                ", departureNodeID=" + departureNodeID +
                ", arrivalNodeID=" + arrivalNodeID +
                '}';
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
