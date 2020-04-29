package com.scheduling.service;

import com.scheduling.model.csv.route.Route;
import com.scheduling.model.csv.route.RouteType;
import com.scheduling.model.csv.route.TimeOfTheDay;
import com.scheduling.model.csv.vehicleservice.VehicleService;
import com.scheduling.model.depot.Depot;
import com.scheduling.model.graph.Graph;
import com.scheduling.model.graph.edge.Edge;
import com.scheduling.model.graph.edge.EdgeType;
import com.scheduling.model.graph.node.Node;
import com.scheduling.model.graph.node.NodeType;
import com.scheduling.model.station.TerminalStation;
import com.scheduling.model.station.Timeline;
import com.scheduling.model.util.ClassWithID;
import com.scheduling.util.CSVUtil;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import static com.scheduling.util.SchedulingConstants.ANSWER_Y;
import static com.scheduling.util.SchedulingConstants.ANSWER_YES;
import static com.scheduling.util.SchedulingConstants.GRAPH_PRINT_QUESTION;
import static com.scheduling.util.SchedulingConstants.NODES_EDGES_CSV_EXPORT_QUESTION;

public class SchedulingService {

    public void schedule() {
        System.out.println("Starting to create the proper scheduling.\n");

        // Create the stations: Vá.1 and Vá.2 with their name
        System.out.println("Creating stations.");
        List<TerminalStation> terminalStations = Arrays.asList(new TerminalStation("TerminalStation1"), new TerminalStation("TerminalStation2"));
        System.out.println("Creating stations - DONE.");

        // There's only one depot, which can serve all of the services
        System.out.println("Creating depot.");
        Depot depot = new Depot();
        System.out.println("Creating depot - DONE.");

        List<Route> routes = CSVUtil.getRoutesFromTheCSV(terminalStations, depot);

        Set<VehicleService> vehicleServices = CSVUtil.getVehicleServicesFromTheCSV(terminalStations);

        createTimelinesForAllOfTheTerminalStations(terminalStations, vehicleServices);

        createTheTimelineForTheDepot(depot, terminalStations, vehicleServices, routes);

        Set<Node> N = createN(terminalStations);

        Set<Edge> E = createE(vehicleServices, terminalStations);

        Set<Edge> B = createB(terminalStations, vehicleServices, routes);

        Set<Edge> R = createR(depot, vehicleServices, terminalStations);

        Set<Edge> K = createK(depot);

        Set<Edge> W = createW(terminalStations);

        Set<Edge> A = createA(E, B, R, K, W);

        Graph<Integer> graph = createGraphFromEdges(new ArrayList<>(A));

        System.out.println("\nDone with creating a proper scheduling.");

        printGraphDependingOnKeyboardInput(graph);

        exportNodesAndEdgesToCSVDependingOnKeyboardInput(terminalStations, depot, new ArrayList<>(A));
    }

    /**
     * Create a Timeline for each terminal station (Vá.1 and Vá.2)
     * In these Timelines set the departure- and arrival nodes according to the departure- and arrival times and stations found inside the VehicleService
     *
     * @param terminalStations - all of the TerminalStations
     * @param vehicleServices - all of the VehicleServices
     */
    private void createTimelinesForAllOfTheTerminalStations(List<TerminalStation> terminalStations, Set<VehicleService> vehicleServices) {
        System.out.println("Creating Timelines for all of the Terminal Stations.");

        terminalStations.forEach(terminalStation -> {
            int terminalStationID = terminalStation.getId();
            List<Node> departureNodes = new ArrayList<>();
            List<Node> arrivalNodes = new ArrayList<>();

            vehicleServices.forEach(vehicleService -> {
                if (terminalStationID == vehicleService.getDepartureStationID()) {
                    departureNodes.add(new Node(NodeType.DEPARTURE, vehicleService.getDepartureTime()));
                } else {
                    arrivalNodes.add(new Node(NodeType.ARRIVAL, vehicleService.getArrivalTime()));
                }
            });

            terminalStation.setTimeline(new Timeline(departureNodes, arrivalNodes));
        });

        System.out.println("Creating Timelines for all of the Terminal Stations - DONE.");
    }

    private void createTheTimelineForTheDepot(Depot depot, List<TerminalStation> terminalStations, Set<VehicleService> vehicleServices, List<Route> routes) {
        System.out.println("Creating the Timeline for the depot.");

        List<Node> departureNodes = Collections.singletonList(new Node(NodeType.DEPOT_DEPARTURE, LocalTime.MIN));
        List<Node> arrivalNodes = Collections.singletonList(new Node(NodeType.DEPOT_ARRIVAL, LocalTime.MAX));
        depot.setTimeline(new Timeline(departureNodes, arrivalNodes));

        System.out.println("Creating the Timeline for the depot - DONE.");
    }

    /**
     * N: all departure- and arrival times (Nodes) from the TerminalStations
     *
     * @return a LinkedHashSet containing all of the nodes of the network
     */
    private Set<Node> createN(List<TerminalStation> terminalStations) {
        System.out.println("Creating N.");
        Set<Node> allOfTheNodesInTheNetwork = new LinkedHashSet<>();

        terminalStations.forEach(terminalStation -> {
            Timeline timelineFromTerminalStation = terminalStation.getTimeline();

            allOfTheNodesInTheNetwork.addAll(timelineFromTerminalStation.getDepartureNodes());
            allOfTheNodesInTheNetwork.addAll(timelineFromTerminalStation.getArrivalNodes());
        });

        System.out.println(String.format("Creating N - DONE. Number of the nodes in the network: %d", allOfTheNodesInTheNetwork.size()));
        return allOfTheNodesInTheNetwork;
    }

    /**
     * Ed from D: Ud is required. Edges of scheduled VehicleServices.
     * Iterate over each service, get the departure- and arrivalTime.
     * Create an edge between these.
     *
     * EdgeType: SERVICE
     *
     * @return a LinkedHashSet containing all of the edges for scheduled services
     */
    private Set<Edge> createE(Set<VehicleService> vehicleServices, List<TerminalStation> terminalStations) {
        System.out.println("Creating E.");
        Set<Edge> edgesForScheduledServices = new LinkedHashSet<>();

        vehicleServices.forEach(vehicleService -> {
            int departureNodeID = getNodeID(vehicleService.getDepartureTime(), vehicleService.getDepartureStationID(), terminalStations, true);
            int arrivalNodeID = getNodeID(vehicleService.getArrivalTime(), vehicleService.getArrivalStationID(), terminalStations, false);

            edgesForScheduledServices.add(new Edge(EdgeType.SERVICE, departureNodeID, arrivalNodeID));
        });

        System.out.println(String.format("Creating E - DONE. Number of edges: %d", edgesForScheduledServices.size()));
        return edgesForScheduledServices;
    }

    /**
     * Get the ID of the node according to the time and terminal station Id found inside the vehicle service.
     *
     * @param timeFromVehicleService - the departure- or the arrival time from the vehicle service
     * @param terminalStationIDFromVehicleService - the departure- or the arrival terminal station's id from the vehicle service
     * @param terminalStations - all of the terminal stations of the network
     * @param isDeparture - boolean value indicating whether the given time and terminal station ID is related to departure or arrival
     * @return - the ID of the node associated with the given time and terminal station ID
     */
    private int getNodeID(LocalTime timeFromVehicleService, int terminalStationIDFromVehicleService, List<TerminalStation> terminalStations, boolean isDeparture) {
        int nodeID = 0;

        Optional<TerminalStation> terminalStation = terminalStations.stream()
                .filter(terminalStation1 -> terminalStation1.getId() == terminalStationIDFromVehicleService)
                .findFirst();

        if (terminalStation.isPresent()) {
            Timeline timeline = terminalStation.get().getTimeline();
            List<Node> nodes = isDeparture ? timeline.getDepartureNodes() : timeline.getArrivalNodes();
            Optional<Node> node = nodes.stream()
                    .filter(node1 -> node1.getLocalTime().equals(timeFromVehicleService))
                    .findFirst();

            if (node.isPresent()) {
                nodeID = node.get().getId();
            }
        }

        return nodeID;
    }

    /**
     *  Bd from D: two phase merging strategy
     *  EdgeType: OVERHEAD
     *
     * @param terminalStations - all of the terminal stations in the network
     * @param vehicleServices - all of the vehicle services in the network
     * @param routes - all of the routes found in the Parameterek sheet
     * @return a LinkedHashSet with all of the overhead edges of the graph
     */
    private Set<Edge> createB(List<TerminalStation> terminalStations, Set<VehicleService> vehicleServices, List<Route> routes) {
        System.out.println("Creating B.");
        Set<Edge> edgesForOverheadServices;

        Map<Integer, List<Integer>> compatibleVehicleServices = getCompatibleVehicleServices(new ArrayList<>(vehicleServices), routes);

        // Creating edges using the two phase merging strategy
        System.out.println("Starting the two phase merging strategy.");

        // First phase:
        Set<Edge> edgesFromTheFirstPhase = firstPhase(terminalStations, compatibleVehicleServices, new ArrayList<>(vehicleServices));

        // Second phase:
        edgesForOverheadServices = secondPhase(new ArrayList<>(edgesFromTheFirstPhase), terminalStations);

        System.out.println(String.format("Creating B - DONE. Number of edges: %d", edgesForOverheadServices.size()));
        return edgesForOverheadServices;
    }

    /**
     * The first phase of the two phase merging strategy.
     *
     * Creating edges between arrival nodes found on one of the terminal stations, and the first compatible nodes found on the other terminal station.
     *
     * @param terminalStations - all of the terminal stations of the network
     * @param compatibleVehicleServices - all of the compatible vehicle services
     * @param vehicleServices - all of the vehicle services
     * @return - all of the first-match edges
     */
    private Set<Edge> firstPhase(List<TerminalStation> terminalStations, Map<Integer, List<Integer>> compatibleVehicleServices, List<VehicleService> vehicleServices) {
        System.out.println("Starting the First Phase.");
        Set<Edge> edgesFromTheFirstPhase = new LinkedHashSet<>();

        // Need to use this approach, because the compatibleVehicleServices map is one VehicleService shy of vehicleServices
        for (int index = 0; index < compatibleVehicleServices.size(); index++) {

            // Preparation part
            VehicleService vehicleService = vehicleServices.get(index);
            TerminalStation terminalStation1 = terminalStations.get(0);
            TerminalStation terminalStation2 = terminalStations.get(1);
            List<Integer> compatibleVehicleServiceIDsForThisVehicleService = compatibleVehicleServices.get(vehicleService.getId());

            // If there are no compatible vehicle services for this vehicle service -> skip this one
            if (compatibleVehicleServiceIDsForThisVehicleService.isEmpty()) {
                continue;
            }

            // Getting the VehicleService objects according to the IDs found inside the compatibleVehicleServices list
            List<VehicleService> vehicleServicesAccordingToTheIDs = vehicleServices.stream()
                    .filter(vehicleService1 -> compatibleVehicleServiceIDsForThisVehicleService.contains(vehicleService1.getId()))
                    .collect(Collectors.toList());

            // Get where this vehicle service is an arrival node
            // From the overhead edge's point of view this node will be the departure node
            int departureNodeID = getNodeID(vehicleService.getArrivalTime(), vehicleService.getArrivalStationID(), terminalStations, false);

            // Get the possible departure nodes
            // The vehicle service's arrival node is on the first station -> get the other vehicle service from the second station
            List<Node> departureNodesFromTerminalStation = vehicleService.getArrivalStationID() == terminalStation1.getId()
                    ? terminalStation2.getTimeline().getDepartureNodes()
                    : terminalStation1.getTimeline().getDepartureNodes();

            // With the local time found inside the node, the nodes's ID can be found
            LocalTime departureTimeFromTheFirstCompatibleVehicleService = vehicleServicesAccordingToTheIDs.get(0).getDepartureTime();

            // Getting the node ID of the first compatible vehicle service
            Optional<Node> departureNode = departureNodesFromTerminalStation.stream()
                    .filter(node -> node.getLocalTime().equals(departureTimeFromTheFirstCompatibleVehicleService))
                    .findFirst();

            if (departureNode.isPresent()) {
                int arrivalNodeID = departureNode.map(ClassWithID::getId).get();

                edgesFromTheFirstPhase.add(new Edge(EdgeType.OVERHEAD, departureNodeID, arrivalNodeID));
            }
        }

        System.out.println(String.format("Done with the First Phase. Number of edges: %d", edgesFromTheFirstPhase.size()));
        return edgesFromTheFirstPhase;
    }

    /**
     * The second phase of the two phase merging strategy.
     *
     * In this the edges got from the first phase are reduced to the only edges
     * which are the least departing first-match edges (vehicle services)
     * for the currently checked vehicle service.
     *
     * @param edgesFromTheFirstPhase - the first-match edges found in the first phase
     * @param terminalStations - all of the terminal stations of the network
     * @return - all of the overhead edges of the network
     */
    private Set<Edge> secondPhase(List<Edge> edgesFromTheFirstPhase, List<TerminalStation> terminalStations) {
        System.out.println("Starting the Second Phase.");
        Set<Edge> edgesFromTheSecondPhase = new LinkedHashSet<>();

        // The arrival nodes found inside 'edgesFromTheFirstPhase' are originally departure nodes on each terminal station

        // Get the arrival nodes with how many edges point to them, so every departure node
        // Integer: ID of arrival node
        // List<Integer>: departure node IDs
        Map<Integer, List<Integer>> arrivalNodeWithDepartureNodes = new LinkedHashMap<>();

        edgesFromTheFirstPhase.forEach(edge -> {
            int arrivalNodeID = edge.getArrivalNodeID();
            int departureNodeID = edge.getDepartureNodeID();

            // If this key already exists inside the map, get the value
            // otherwise, create a new ArrayList
            List<Integer> departureNodeIDs = arrivalNodeWithDepartureNodes.containsKey(arrivalNodeID) ? arrivalNodeWithDepartureNodes.get(arrivalNodeID) : new ArrayList<>();
            departureNodeIDs.add(departureNodeID);
            arrivalNodeWithDepartureNodes.put(arrivalNodeID, departureNodeIDs);
        });

        // Go through the keys (arrival node ID) of the map above and check if this key is inside the current terminal station's departure nodes
        TerminalStation terminalStation1 = terminalStations.get(0);
        TerminalStation terminalStation2 = terminalStations.get(1);
        List<Integer> departureNodeIDsFromTerminalStation1 = terminalStation1.getTimeline().getDepartureNodes()
                .stream()
                .map(ClassWithID::getId)
                .collect(Collectors.toList());

        arrivalNodeWithDepartureNodes.forEach((arrivalNodeID, departureNodeIDs) -> {

            // If the arrival node is from the first terminal station, get the departure node from the other one
            TerminalStation terminalStation = departureNodeIDsFromTerminalStation1.contains(arrivalNodeID)? terminalStation2: terminalStation1;

            // If there's only one edge pointing to the arrival node
            // then there's no need to check for the last departing one
            if (departureNodeIDs.size() == 1) {
                edgesFromTheSecondPhase.add(new Edge(EdgeType.OVERHEAD, departureNodeIDs.get(0), arrivalNodeID));
            } else {

                // get the sorted list of the departing nodes of the selected terminal station
                // the last departing will be the last one in the list
                List<Node> departingNodes = terminalStation.getTimeline().getArrivalNodes()
                        .stream()
                        .filter(node -> departureNodeIDs.contains(node.getId()))
                        .sorted(Comparator.comparing(Node::getLocalTime))
                        .collect(Collectors.toList());

                Node lastDeparting = departingNodes.get(departingNodes.size() - 1);
                edgesFromTheSecondPhase.add(new Edge(EdgeType.OVERHEAD, lastDeparting.getId(), arrivalNodeID));
            }
        });

        System.out.println(String.format("Done with the Second Phase. Number of edges: %d", edgesFromTheSecondPhase.size()));
        return edgesFromTheSecondPhase;
    }

    /**
     * Find all of the compatible vehicle services for all services in the meaning found in the literature.
     *
     * @param vehicleServices - all of the vehicle services in the network
     * @param routes - all of the routes found in the Parameterek sheet
     * @return a LinkedHashMap where the key is the id of the current vehicle service and the value is the list of all compatible vehicle services
     */
    private Map<Integer, List<Integer>> getCompatibleVehicleServices(List<VehicleService> vehicleServices, List<Route> routes) {
        Map<Integer, List<Integer>> compatibleVehicleServices = new LinkedHashMap<>();

        // Starting from the first vehicle service
        // and going till the penultimate vehicle service, because of the comparison
        for (int index = 0; index < vehicleServices.size() - 1; index++) {
            VehicleService vehicleService = vehicleServices.get(index);

            // Starting from the vehicle service found at the current index + 1
            // and going till the last vehicle service, because of the comparison
            List<Integer> compatibleVehicleServicesIDs = new ArrayList<>();
            for (int innerIndex = index + 1; innerIndex < vehicleServices.size(); innerIndex++) {
                VehicleService innerVehicleService = vehicleServices.get(innerIndex);

                if (isCompatible(vehicleService, innerVehicleService, routes)) {
                    compatibleVehicleServicesIDs.add(innerVehicleService.getId());
                }
            }

            compatibleVehicleServices.put(vehicleService.getId(), compatibleVehicleServicesIDs);
        }

        return compatibleVehicleServices;
    }

    /**
     * Check if two vehicle services are compatible or not, in the meaning found in the literature.
     *
     * @param one - the first vehicle service
     * @param other - the second vehicle service
     * @param routes - all of the routes found in the Parameterek sheet
     * @return true, if they are compatible, false otherwise
     */
    private boolean isCompatible(VehicleService one, VehicleService other, List<Route> routes) {

        // the timeToGetFromTheDepartureStationToTheArrivalStation would be zero
        if (one.getArrivalStationID() == other.getDepartureStationID()) {
            return false;
        }

        LocalTime arrivalTimeOfOne = one.getArrivalTime();
        LocalTime departureTimeOfOther = other.getDepartureTime();

        List<Route> NTypeRoutes = routes.stream().filter(route -> route.getRouteType().equals(RouteType.N)).collect(Collectors.toList());
        LocalTime departureTimeOfOne = one.getDepartureTime();

        // The difference in minutes between the arrival of the first vehicle service and the departure of the second vehicle service
        // Math.abs() is required because of the negative numbers
        long differenceInMinutes = Math.abs(Duration.between(departureTimeOfOther, arrivalTimeOfOne).toMinutes());
        for (Route route : NTypeRoutes) {

            // Check in which time of the day the departure time of the first vehicle service fits in
            if (isTimeInsideTimeOfTheDay(departureTimeOfOne, route.getTimeOfTheDay())) {

                // If it's found, subtract the technical- and compensatory times from the difference
                // The chauffeur has to wait this much time at the terminal station
                differenceInMinutes -= route.getTechnicalTime();
                differenceInMinutes -= route.getCompensatoryTime();

                // Exit the loop, because we found our time of the day, and the required subtraction was made
                break;
            }
        }

        // The formula itself from the literature
        boolean oneArrivedBeforeOrAtTheSameTimeAsTheOtherDeparts = arrivalTimeOfOne.isBefore(departureTimeOfOther) || arrivalTimeOfOne.equals(departureTimeOfOther);
        long timeToGetFromTheDepartureStationToTheArrivalStation = Duration.between(other.getDepartureTime(), other.getArrivalTime()).toMinutes();

        return oneArrivedBeforeOrAtTheSameTimeAsTheOtherDeparts && (differenceInMinutes < timeToGetFromTheDepartureStationToTheArrivalStation);
    }

    /**
     *Checks if the given time is between the interval found inside the TimeOfTheDay object
     *
     * @param time - the departure time of the first vehicle service in the comparison
     * @param timeOfTheDay - an object containing two LocalTime objects, which contain the current time of the day from the route
     * @return true, if the given time is between the interval, false otherwise
     */
    private boolean isTimeInsideTimeOfTheDay(LocalTime time, TimeOfTheDay timeOfTheDay) {
        LocalTime startingTime = timeOfTheDay.getStartingTime();
        LocalTime finishTime = timeOfTheDay.getFinishTime();

        return (time.isAfter(startingTime) || time.equals(startingTime)) && (time.isBefore(finishTime) || time.equals(finishTime));
    }

    /**
     *  Rd from D: Ud is required. Edges of unscheduled services.
     *
     *  Get the departure- and arrival nodes from the depot's timeline.
     *  Go through all of the vehicle services and get their departure- and arrival nodes.
     *  Create an edge between the departure node of the depot and the departure node of the vehicle service.
     *  Create an edge between the arrival node of the vehicle service and the arrival node of the depot.
     *
     *  EdgeType: DEPOT
     *
     * @param depot - the only depot in the network
     * @param vehicleServices - all of the vehicle services in the network
     * @param terminalStations - all of the terminal stations in the network
     * @return a LinkedHashSet containing all of the depot departure- and arrival edges
     */
    private Set<Edge> createR(Depot depot, Set<VehicleService> vehicleServices, List<TerminalStation> terminalStations) {
        System.out.println("Creating R.");
        Set<Edge> depotDepartingAndArrivingEdges = new LinkedHashSet<>();

        Timeline timelineFromDepot = depot.getTimeline();
        int depotDepartureNodeID = timelineFromDepot.getDepartureNodes().get(0).getId();
        int depotArrivalNodeID = timelineFromDepot.getArrivalNodes().get(0).getId();

        vehicleServices.forEach(vehicleService -> {
            int vehicleServiceDepartureNodeID = getNodeID(vehicleService.getDepartureTime(), vehicleService.getDepartureStationID(), terminalStations, true);
            int vehicleServiceArrivalNodeID = getNodeID(vehicleService.getArrivalTime(), vehicleService.getArrivalStationID(), terminalStations, false);

            depotDepartingAndArrivingEdges.add(new Edge(EdgeType.DEPOT, depotDepartureNodeID, vehicleServiceDepartureNodeID));
            depotDepartingAndArrivingEdges.add(new Edge(EdgeType.DEPOT, vehicleServiceArrivalNodeID, depotArrivalNodeID));
        });

        System.out.println(String.format("Creating R - DONE. Number of edges: %d", depotDepartingAndArrivingEdges.size()));
        return depotDepartingAndArrivingEdges;
    }

    /**
     * Kd from D:
     * For the depot: create edges between arrival times and the departure times
     *
     * EdgeType: DEPOT
     *
     * @param depot - the only depot in the network
     * @return a LinkedHashSet with all of the circular flow edges between depot arrival- and departure nodes
     */
    private Set<Edge> createK(Depot depot) {
        System.out.println("Creating K.");
        Set<Edge> depotCircularFlowEdges = new LinkedHashSet<>();

        // The depot only has one departure- and one arrival node
        Timeline timelineFromDepot = depot.getTimeline();
        int depotDepartureNodeID = timelineFromDepot.getDepartureNodes().get(0).getId();
        int depotArrivalNodeID = timelineFromDepot.getArrivalNodes().get(0).getId();

        depotCircularFlowEdges.add(new Edge(EdgeType.DEPOT,  depotArrivalNodeID, depotDepartureNodeID));

        System.out.println(String.format("Creating K - DONE. Number of edges: %d", depotCircularFlowEdges.size()));
        return depotCircularFlowEdges;
    }

    /**
     * Wd: Create waiting edges: these always follow the current station's timeline and they connect the following departure times, collecting their stream.
     * Iterate over the two terminal stations
     * Iterate over the departure times found inside the terminal station
     * Connect the times (nodes) to form an edge
     *
     * EdgeType: WAITING
     *
     * @param terminalStations - all of the terminal stations in the network
     * @return a LinkedHashSet containing the waiting edges for each station
     */
    private Set<Edge> createW(List<TerminalStation> terminalStations) {
        System.out.println("Creating W.");
        Set<Edge> waitingEdgesForTheTerminalStations = new LinkedHashSet<>();

        terminalStations.forEach(terminalStation -> {
            Timeline timelineFromTerminalStation = terminalStation.getTimeline();
            List<Node> departureNodes = timelineFromTerminalStation.getDepartureNodes();

            for (int index = 0; index < departureNodes.size() - 1; index++) {
                Node firstNode = departureNodes.get(index);
                Node secondNode = departureNodes.get(index + 1);

                waitingEdgesForTheTerminalStations.add(new Edge(EdgeType.WAITING, firstNode.getId(), secondNode.getId()));
            }
        });

        System.out.println(String.format("Creating W - DONE. Number of edges: %d", waitingEdgesForTheTerminalStations.size()));
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
     * @return a LinkedHashSet containing all of the edges of the network
     */
    private Set<Edge> createA(Set<Edge> E, Set<Edge> B, Set<Edge> R, Set<Edge> K, Set<Edge> W) {
        System.out.println("Creating A.");
        Set<Edge> allOfTheEdgesInTheNetwork = new LinkedHashSet<>();

        allOfTheEdgesInTheNetwork.addAll(E);
        allOfTheEdgesInTheNetwork.addAll(B);
        allOfTheEdgesInTheNetwork.addAll(R);
        allOfTheEdgesInTheNetwork.addAll(K);
        allOfTheEdgesInTheNetwork.addAll(W);

        System.out.println(String.format("Creating A - DONE. Number of edges in the network: %d", allOfTheEdgesInTheNetwork.size()));
        return allOfTheEdgesInTheNetwork;
    }

    /**
     * Create a graph by adding all of the edges to it.
     *
     * @param edges - all of the edges of the network
     * @return a graph containing all of the edges of the network
     */
    private Graph<Integer> createGraphFromEdges(List<Edge> edges) {
        Graph<Integer> graph = new Graph<>();

        edges.forEach(edge -> graph.addEdge(edge.getDepartureNodeID(), edge.getArrivalNodeID(), false));

        return graph;
    }

    /**
     * Print the graph on the console, if the user types 'y' or 'yes'.
     *
     * @param graph - the graph created from the edges
     */
    private void printGraphDependingOnKeyboardInput(Graph<Integer> graph) {
        Scanner keyboard = new Scanner(System.in);
        System.out.println(GRAPH_PRINT_QUESTION);
        String answer = keyboard.nextLine();

        if (ANSWER_Y.equals(answer) || ANSWER_YES.equals(answer)) {
            System.out.println("The graph:\n" + graph.toString());
        }
    }

    /**
     * Export all the nodes and edges into .csv files, if the user types 'y' or 'yes'.
     *
     * @param terminalStations - all of the terminal stations, required for the nodes
     * @param depot - the only depot, required for the nodes
     * @param edges - all of the edges
     */
    private void exportNodesAndEdgesToCSVDependingOnKeyboardInput(List<TerminalStation> terminalStations, Depot depot, List<Edge> edges) {
        Scanner keyboard = new Scanner(System.in);
        System.out.println(NODES_EDGES_CSV_EXPORT_QUESTION);
        String answer = keyboard.nextLine();

        if (ANSWER_Y.equals(answer) || ANSWER_YES.equals(answer)) {
            CSVUtil.createNodesCSV(terminalStations, depot);
            CSVUtil.createEdgesCSV(edges);
        }
    }
}
