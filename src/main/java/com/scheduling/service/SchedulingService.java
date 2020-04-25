package com.scheduling.service;

import com.scheduling.model.busservice.BusService;
import com.scheduling.model.depot.Depot;
import com.scheduling.model.graph.Edge;
import com.scheduling.model.graph.Node;

import java.util.Map;
import java.util.Set;

public class SchedulingService {

    // Get the services from the Járatok sheet, these come with the departure- and arrival stations, and create a List of BusServices
    // Create a Timeline for each station (Vá.1 and Vá.2)
    // In these Timelines set the departure- and arrival nodes according to the departure- and arrival times and stations found inside the BusService

    // All of the nodes from the stations
    Set<Node> N;

    // Ud
    Map<Depot, Set<BusService>> Ud;

    // Ed
    Map<Depot, Set<Edge>> Ed;

    // Bd
    Map<Depot, Set<Edge>> Bd;
}
