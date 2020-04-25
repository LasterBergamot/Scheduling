package com.scheduling.service;

import com.scheduling.model.busservice.BusService;
import com.scheduling.model.depot.Depot;
import com.scheduling.model.graph.edge.Edge;
import com.scheduling.model.graph.node.Node;

import java.util.Map;
import java.util.Set;

public class SchedulingService {

    // Create the stations: Vá.1 and Vá.2 with their name

    // Create the depots: one for Vá.1 and one for Vá.2

    // Get the routes from the Paraméterek sheet and create a List of Routes

    // Get the bus services from the Járatok sheet, these come with the departure- and arrival stations, and create a List of BusServices
    // Create a Timeline for each station (Vá.1 and Vá.2)
    // In these Timelines set the departure- and arrival nodes according to the departure- and arrival times and stations found inside the BusService


    // Wd: Create waiting edges for each depot: these always follow the current station's timeline and they connect the following departure times, collecting their stream

    // All of the nodes from the stations
    Set<Node> N;

    // Ud
    Map<Depot, Set<BusService>> Ud;

    // Ed
    Map<Depot, Set<Edge>> Ed;

    // Bd
    Map<Depot, Set<Edge>> Bd;
}
