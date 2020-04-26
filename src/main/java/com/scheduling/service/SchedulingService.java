package com.scheduling.service;

import com.scheduling.model.csv.route.Route;
import com.scheduling.model.csv.vehicleservice.VehicleService;
import com.scheduling.model.depot.Depot;
import com.scheduling.model.graph.edge.Edge;
import com.scheduling.model.graph.node.Node;
import com.scheduling.model.station.TerminalStation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SchedulingService {

    public void schedule() {
        System.out.println("Starting to create the proper scheduling...");

        // Create the stations: Vá.1 and Vá.2 with their name
        TerminalStation terminalStation1;
        TerminalStation terminalStation2;

        // There's only one depot, which can serve all of the services
        Depot depot;

        List<Route> routes = getRoutesFromTheCSV();

        Set<VehicleService> vehicleServices = getVehicleServicesFromTheCSV();

        // Create a Timeline for each terminal station (Vá.1 and Vá.2)
        // In these Timelines set the departure- and arrival nodes according to the departure- and arrival times and stations found inside the VehicleService


        Set<Node> N = createN();

        Set<Edge> E = createE();

        Set<Edge> B = createB();

        Set<Edge> R = createR();

        Set<Edge> K = createK();

        Set<Edge> W = createW();

        Set<Edge> A = createA(E, B, R, K, W);

        // E: all of the edges of the graph. Union of all Ads.
        Set<Edge> allOfTheEdgesOfTheGraph;
    }

    /**
     * Get the routes from the Paraméterek sheet (Input_VSP.xlsx) and create a List of Routes
     *
     * @return an ArrayList of Routes
     */
    private List<Route> getRoutesFromTheCSV() {
        List<Route> routes = new ArrayList<>();

        return routes;
    }

    /**
     * All of the services can be served by the only depot we have
     * Get the vehicle services from the Járatok sheet, these come with the departure- and arrival stations, and create a Set of VehicleServices
     * Where Ud is needed, just use U instead
     *
     * @return a HashSet of VehicleServices
     */
    private Set<VehicleService> getVehicleServicesFromTheCSV() {
        Set<VehicleService> vehicleServices = new HashSet<>();

        return vehicleServices;
    }

    /**
     * N: all departure- and arrival times (nodes) from the terminal stations
     *
     * @return a HashSet containing all of the nodes of the network
     */
    private Set<Node> createN() {
        Set<Node> allOfTheNodesOfTheNetwork = new HashSet<>();

        return allOfTheNodesOfTheNetwork;
    }

    /**
     * Ed from D: Ud is required. Edges of scheduled services.
     * Iterate over each service, get the departure- and arrivalTime
     * Create an edge between these
     *
     * After the vehicle reached the departure station, it departs to the arrival station: (N-type routes)
     * check in which timeOfTheDay this time fits in (departureTime from VehicleService), inside routes
     *      -> get the duration (difference between departureTime and arrivalTime from VehicleService)
     *      this will be the weight of the edge
     *      (this is the time required for the vehicle to reach the arrival station)
     *
     * EdgeType: SERVICE
     *
     * @return a HashSet containing all of the edges for scheduled services
     */
    private Set<Edge> createE() {
        Set<Edge> edgesForScheduledServices = new HashSet<>();

        return edgesForScheduledServices;
    }

    /**
     *  Bd from D: two phase merging strategy
     *  EdgeType: OVERHEAD
     * @return a HashSet with all of the overhead edges of the graph
     */
    private Set<Edge> createB() {
        Set<Edge> edgesForOverheadServices = new HashSet<>();

        return edgesForOverheadServices;
    }

    /**
     *  Rd from D: Ud is required. Edges of unscheduled services.
     *
     *  Create the Timeline the depot
     *  There are a lot of services in the day
     *  The depot is only used twice: at the start- and at the end of the day
     *  There are two types of services:
     *      1.: Vá.2 -> Vá.1
     *      2.: Vá.1 -> Vá.2
     *  So, a total of 4 edges needed
     *  Use the menettartam from Routes
     *
     *  Depot + vehicle service connection:
     *  The vehicle departs from the depot: (G-type routes)
     *      check in which depot-interval (timeOfTheDay) this time fits in (departureTime from VehicleService), inside routes
     *          -> get the duration (menettartam)
     *      Subtract the duration from the departureTime -> departureTime for this depot
     *      Duration will be the weight of the edge
     *      (this is the time required for the vehicle to reach the departure station)
     *      Set this departure time inside the Depot
     *
     *  After the vehicle reached the arrival station, it departs to the depot: (G-type routes)
     *      check in which timeOfTheDay this time fits in (departureTime from VehicleService), inside routes
     *          -> get technical- and compensatoryTime (the vehicle wil have to wait this much time at the station before departing to the depot)
     *      check in which depot-interval (timeOfTheDay) this time fits in (arrivalTime from VehicleService), inside routes
     *          -> get the duration (menettartam)
     *      Add the duration + technical- and compensatoryTime to the arrivalTime -> arrivalTime for this depot
     *      Duration + technical- and compensatoryTime will be the weight of the edge
     *      (this is the time required for the vehicle to reach the depot)
     *      Set this arrival time inside the Depot
     *
     *  EdgeType: DEPOT
     *
     * @return a HashSet containing all of the depot departing- and arriving edges
     */
    private Set<Edge> createR() {
        Set<Edge> depotDepartingAndArrivingEdges = new HashSet<>();

        return depotDepartingAndArrivingEdges;
    }

    /**
     * Kd from D:
     * For the depot: create edges between arrivalTimes and the departureTimes
     * EdgeType: DEPOT
     *
     * @return a HashSet with all of the circular flow edges between depot arriving- and departing nodes
     */
    private Set<Edge> createK() {
        Set<Edge> depotCircularFlowEdges = new HashSet<>();

        return depotCircularFlowEdges;
    }

    /**
     * Wd: Create waiting edges for each depot: these always follow the current station's timeline and they connect the following departure times, collecting their stream
     * Iterate over the two terminal stations
     * Iterate over the departureTimes found inside the terminal station
     * Get the difference between these times (check if it's the first node on the Timeline)
     * This will be the weight of the edge
     * Connect the times (nodes) to form an edge
     * EdgeType: WAITING
     *
     * @return a HashSet containing the waiting edges for each station
     */
    private Set<Edge> createW() {
        Set<Edge> waitingEdgesForTheTerminalStations = new HashSet<>();

        return waitingEdgesForTheTerminalStations;
    }

    /**
     * Ad from D: all of the edges of the given depot. Union of Ed, Bd, Rd, Kd and Wd.
     * Ad == E, in this case, because there's only one depot
     *
     * @param E - edges of scheduled services
     * @param B - overhead edges
     * @param R - departing- and arriving edges for the depot
     * @param K - circular flow edges for the depot
     * @param W - waiting edges for each terminal station
     *
     * @return a HashSet containing all of the edges of the network
     */
    private Set<Edge> createA(Set<Edge> E, Set<Edge> B, Set<Edge> R, Set<Edge> K, Set<Edge> W) {
        Set<Edge> allOfTheEdgesOfTheNetwork = new HashSet<>();

        allOfTheEdgesOfTheNetwork.addAll(E);
        allOfTheEdgesOfTheNetwork.addAll(B);
        allOfTheEdgesOfTheNetwork.addAll(R);
        allOfTheEdgesOfTheNetwork.addAll(K);
        allOfTheEdgesOfTheNetwork.addAll(W);

        return allOfTheEdgesOfTheNetwork;
    }
}
