package com.scheduling.service;

import com.scheduling.model.csv.vehicleservice.VehicleService;
import com.scheduling.model.depot.Depot;
import com.scheduling.model.graph.edge.Edge;
import com.scheduling.model.graph.node.Node;

import java.util.Map;
import java.util.Set;

public class SchedulingService {

    // Create the stations: Vá.1 and Vá.2 with their name

    // Create the depots: one for Vá.1 and one for Vá.2

    // Get the routes from the Paraméterek sheet and create a List of Routes

    // Get the vehicle services from the Járatok sheet, these come with the departure- and arrival stations, and create a List of VehicleServices

    // Create a Timeline for each station (Vá.1 and Vá.2)
    // In these Timelines set the departure- and arrival nodes according to the departure- and arrival times and stations found inside the VehicleService

    // Du from D: create a set of depots for a u vehicle service. All of the depots which can be served by the u vehicle service.
    // Integer: ID of vehicle service
    Map<Integer, Set<Depot>> Du;

    // Ud from U: create a set of vehicle services for a d depot. All of the vehicle services which can be served by the d depot.
    // Iterate over each depot, get the stationID
    // Get those vehicle services where the departureStationID or the arrivalStationID equals to the stationID above
    // Integer: ID of depot
    Map<Integer, Set<VehicleService>> Ud;

    // N: all departure- and arrival times (nodes) from the stations
    Set<Node> N;

    // Ed from D: Ud is required. Edges of scheduled services.
    // Iterate over each depot, get Ud
    // Iterate over Ud to get u, and create and edge between the dt(u) and at(u) nodes (departure- and arrivalTime for u).

    // After the vehicle reached the departure station, it departs to the arrival station: (N-type routes)
    //      check in which timeOfTheDay this time fits in (departureTime from VehicleService), inside routes
    //          -> get the duration (menettartam)
    //      this will be the weight of the edge
    //      (this is the time required for the vehicle to reach the arrival station)

    // EdgeType: SERVICE
    // Integer: ID of depot
    Map<Integer, Set<Edge>> Ed;

    // Bd from D: two phase merging strategy
    // EdgeType: OVERHEAD
    // Integer: ID of depot
    Map<Integer, Set<Edge>> Bd;

    // Rd from D: Ud is required. Edges of unscheduled services.
    // Iterate over each depot, get Ud

    // Idea:
    // Check which of departure- or arrivalStationID matches the stationID found inside the depot, and set the values accordingly
    // If this approach is not working, do without the check

    // Create a Timeline for each depot
    // Depot + vehicle service connection:
    // The vehicle departs from the depot: (G-type routes)
    //      check in which depot-interval (timeOfTheDay) this time fits in (departureTime from VehicleService), inside routes
    //          -> get the duration (menettartam)
    //      Subtract the duration from the departureTime -> departureTime for this depot
    //      Duration will be the weight of the edge
    //      (this is the time required for the vehicle to reach the departure station)
    //      Set this departure time inside the Depot

    // After the vehicle reached the arrival station, it departs to the depot: (G-type routes)
    //      check in which timeOfTheDay this time fits in (departureTime from VehicleService), inside routes
    //          -> get technical- and compensatoryTime (the vehicle wil have to wait this much time at the station before departing to the depot)
    //      check in which depot-interval (timeOfTheDay) this time fits in (arrivalTime from VehicleService), inside routes
    //          -> get the duration (menettartam)
    //      Add the duration + technical- and compensatoryTime to the arrivalTime -> arrivalTime for this depot
    //      Duration + technical- and compensatoryTime will be the weight of the edge
    //      (this is the time required for the vehicle to reach the depot)
    //      Set this arrival time inside the Depot

    // EdgeType: DEPOT
    // Integer: ID of depot
    Map<Integer, Set<Edge>> Rd;

    // Kd from D:
    // Iterate over the depots
    // Create edges between arrivalTimes and the departureTimes
    // EdgeType: DEPOT
    // Integer: ID of depot
    Map<Integer, Set<Edge>> Kd;

    // Wd: Create waiting edges for each depot: these always follow the current station's timeline and they connect the following departure times, collecting their stream
    // Iterate over the depots, get the stationID
    // Get the station by its ID
    // Iterate over the departureTimes found inside the station
    // Get the difference between these times
    // This will be the weight of the edge
    // Connect the times (nodes) to form an edge
    // EdgeType: WAITING
    // Integer: ID of depot
    Map<Integer, Set<Edge>> Wd;

    // Ad from D: all of the edges of the given depot. Union of Ed, Bd, Rd, Kd and Wd.
    // Integer: ID of depot
    Map<Integer, Set<Edge>> Ad;

    // E: all of the edges of the graph. Union of all Ads.
    Set<Edge> E;
}
