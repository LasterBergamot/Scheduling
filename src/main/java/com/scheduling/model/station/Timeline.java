package com.scheduling.model.station;

import com.scheduling.model.graph.node.Node;

import java.util.List;

public class Timeline {

    List<Node> departureNodes;

    List<Node> arrivalNodes;

    public Timeline(List<Node> departureNodes, List<Node> arrivalNodes) {
        this.departureNodes = departureNodes;
        this.arrivalNodes = arrivalNodes;
    }

    public List<Node> getDepartureNodes() {
        return departureNodes;
    }

    public void setDepartureNodes(List<Node> departureNodes) {
        this.departureNodes = departureNodes;
    }

    public List<Node> getArrivalNodes() {
        return arrivalNodes;
    }

    public void setArrivalNodes(List<Node> arrivalNodes) {
        this.arrivalNodes = arrivalNodes;
    }
}
