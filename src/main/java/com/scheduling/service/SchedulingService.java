package com.scheduling.service;

import com.scheduling.model.graph.BusService;
import com.scheduling.model.graph.Depot;
import com.scheduling.model.graph.Edge;
import com.scheduling.model.graph.Node;

import java.util.Map;
import java.util.Set;

public class SchedulingService {

    Set<Node> N;

    // Ud
    Map<Depot, Set<BusService>> Ud;

    // Ed
    Map<Depot, Set<Edge>> Ed;

    // Bd
    Map<Depot, Set<Edge>> Bd;
}
