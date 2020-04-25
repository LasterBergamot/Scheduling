package com.scheduling.service;

import com.scheduling.model.vehicleservice.VehicleService;
import com.scheduling.model.depot.Depot;
import com.scheduling.model.graph.edge.Edge;
import com.scheduling.model.graph.node.Node;

import java.util.Map;
import java.util.Set;

public class SchedulingService {

    // Create the stations: Vá.1 and Vá.2 with their name

    // Get the routes from the Paraméterek sheet and create a List of Routes

    // Create the depots: one for Vá.1 and one for Vá.2
    // Add the required routes to them and remove these from the actual Route list

    // Get the vehicle services from the Járatok sheet, these come with the departure- and arrival stations, and create a List of VehicleServices

    // Depot + vehicle service connection:
    // The vehicle departs from the depot: (G-type routes)
    //      check in which depot-interval (timeOfTheDay) this time fits in (departureTime from VehicleService), inside routes/depots
    //          -> get the duration (menettartam)
    //      this will be the weight of the edge
    //      (this is the time required for the vehicle to reach the departure station)
    // After the vehicle reached the departure station, it departs to the arrival station: (N-type routes)
    //      check in which timeOfTheDay this time fits in (departureTime from VehicleService), inside routes
    //          -> get the technical- and compensatoryTime and add it to the difference between departure- and arrivalTime (basically the duration, but not the duration from Route)
    //      this will be the weight of the edge
    //      (this is the time required for the vehicle to reach the arrival station)
    // After the vehicle reached the arrival station, it departs to the depot: (G-type routes)
    //      check in which depot-interval (timeOfTheDay) this time fits in (departureTime from VehicleService), inside routes/depots
    //          -> get the duration (menettartam)
    //      this will be the weight of the edge
    //      (this is the time required for the vehicle to reach the departure station)

    // Du from D: create a set of depots for a u vehicle service. All of the depots which can be served by the u vehicle service.
    // Integer: ID of vehicle service
    Map<Integer, Set<Depot>> Du;

    // Ud from U: create a set of vehicle services for a d depot. All of the vehicle services which can be served by the d depot.
    // Integer: ID of depot
    Map<Integer, Set<VehicleService>> Ud;

    // Create a Timeline for each station (Vá.1 and Vá.2)
    // In these Timelines set the departure- and arrival nodes according to the departure- and arrival times and stations found inside the VehicleService


    // Wd: Create waiting edges for each depot: these always follow the current station's timeline and they connect the following departure times, collecting their stream

}
