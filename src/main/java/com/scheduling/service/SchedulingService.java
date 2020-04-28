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
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class SchedulingService {

    private static final String PATH_INPUT_VSP_XLSX = "D:\\Input_VSP.xlsx";
    private static final String PATH_PARAMETEREK_CSV = "C:\\asd\\Parameterek.csv";
//    private static final String PATH_PARAMETEREK_CSV = "D:\\Parameterek.csv";
    private static final String PATH_JARATOK_CSV = "C:\\asd\\Jaratok.csv";
//    private static final String PATH_JARATOK_CSV = "D:\\Jaratok.csv";

    private static final String ENCODING_ISO_8859_2 = "ISO-8859-2";
    private static final String CSV_DELIMITER = ",";

    private static final String FROM_TO_VA_2_FELE = "Vá.2 felé";
    private static final String FROM_TO_VA_1_FELE = "Vá.1 felé";

    private static final String FROM_TO_GARAZS_VA_1 = "Garázs - Vá.1";
    private static final String FROM_TO_VA_1_GARAZS = "Vá.1 - Garázs";
    private static final String FROM_TO_GARAZS_VA_2 = "Garázs - Vá.2";
    private static final String FROM_TO_VA_2_GARAZS = "Vá.2 - Garázs";

    private static final String TYPE_1_VA_2_VA_1 = "Vá.2->Vá.1";

    private static final String INDEX_OF_DECIMAL_SEPARATOR = ".";
    private static final String LOCAL_TIME_EXTRA_ZERO = "0";
    private static final String LOCAL_TIME_SEPARATOR = ":";
    private static final String LOCAL_TIME_SECONDS = ":00";

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

        List<Route> routes = getRoutesFromTheCSV(terminalStations, depot);

        Set<VehicleService> vehicleServices = getVehicleServicesFromTheCSV(terminalStations);

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

        createNodesCSV(terminalStations, depot);

        // To create a CSV from the edges: iterate over all of the edges and put the IDs and the type in these columns: Source, Target and Type
//        createEdgesCSV(new ArrayList<>(A));

        System.out.println("\nDone with creating a proper scheduling.");
    }

    /**
     * Get the routes from the Paraméterek sheet (Input_VSP.xlsx) and create a List of Routes
     *
     * @return an ArrayList of Routes
     */
    private List<Route> getRoutesFromTheCSV(List<TerminalStation> terminalStations, Depot depot) {
        System.out.println("Getting the Routes from the CSV.");
        List<Route> routes = new ArrayList<>();

        int terminalStation1ID = terminalStations.get(0).getId();
        int terminalStation2ID = terminalStations.get(1).getId();
        int depotID = depot.getId();

        Map<String, Integer> departureFromTos = createDepartureFromTosMap(terminalStation1ID, terminalStation2ID, depotID);
        Map<String, Integer> arrivalFromTos = createArrivalFromTosMap(terminalStation1ID, terminalStation2ID, depotID);

        // Source: https://mkyong.com/java/how-to-read-and-parse-csv-file-in-java/
        BufferedReader br = null;
        String line;

        try {

            br = new BufferedReader(new FileReader(PATH_PARAMETEREK_CSV, Charset.forName(ENCODING_ISO_8859_2)));
            while ((line = br.readLine()) != null) {
                String[] routeFromCSV = line.split(CSV_DELIMITER);

                routes.add(createRouteFromRouteFromCSV(departureFromTos, arrivalFromTos, routeFromCSV));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println(String.format("Getting the Routes from the CSV - DONE. Found %d routes.", routes.size()));
        return routes;
    }

    private Map<String, Integer> createArrivalFromTosMap(int terminalStation1ID, int terminalStation2ID, int depotID) {
        return Map.of(
                FROM_TO_VA_2_FELE, terminalStation2ID,
                FROM_TO_VA_1_FELE, terminalStation1ID,
                FROM_TO_GARAZS_VA_1, terminalStation1ID,
                FROM_TO_VA_1_GARAZS, depotID,
                FROM_TO_GARAZS_VA_2, terminalStation2ID,
                FROM_TO_VA_2_GARAZS, depotID
        );
    }

    private Map<String, Integer> createDepartureFromTosMap(int terminalStation1ID, int terminalStation2ID, int depotID) {
        return Map.of(
                FROM_TO_VA_2_FELE, terminalStation1ID,
                FROM_TO_VA_1_FELE, terminalStation2ID,
                FROM_TO_GARAZS_VA_1, depotID,
                FROM_TO_VA_1_GARAZS, terminalStation1ID,
                FROM_TO_GARAZS_VA_2, depotID,
                FROM_TO_VA_2_GARAZS, terminalStation2ID
        );
    }

    private Route createRouteFromRouteFromCSV(Map<String, Integer> departureFromTos, Map<String, Integer> arrivalFromTos, String[] routeFromCSV) {
        String routeNameFromCSV = routeFromCSV[0];
        String fromToFromCSV = routeFromCSV[1];
        String routeTypeFromCSV = routeFromCSV[2];
        String startTimeOfTheDayFromCSV = routeFromCSV[3];
        String finishTimeOfTheDayFromCSV = routeFromCSV[4];
        String durationFromCSV = routeFromCSV[5];
        String technicalTimeFromCSV = routeFromCSV[6];
        String compensatoryTimeFromCSV = routeFromCSV[7];

        RouteType routeType= RouteType.valueOf(routeTypeFromCSV);

        Route route = new Route(routeNameFromCSV, routeType, new TimeOfTheDay(LocalTime.parse(startTimeOfTheDayFromCSV),
                LocalTime.parse(finishTimeOfTheDayFromCSV)), (int) Float.parseFloat(durationFromCSV), (int) Float.parseFloat(technicalTimeFromCSV),
                (int) Float.parseFloat(compensatoryTimeFromCSV));

        setIDsForRouteAccordingly(departureFromTos, arrivalFromTos, fromToFromCSV, routeType, route);

        return route;
    }

    private void setIDsForRouteAccordingly(Map<String, Integer> departureFromTos, Map<String, Integer> arrivalFromTos, String fromToFromCSV, RouteType routeType, Route route) {
        if (routeType == RouteType.N) {
            route.setDepartureStationID(departureFromTos.get(fromToFromCSV));
            route.setArrivalStationID(arrivalFromTos.get(fromToFromCSV));
        } else {
            String trimmedFromToFromCSV = fromToFromCSV.trim();

            if (FROM_TO_GARAZS_VA_1.equals(trimmedFromToFromCSV) || FROM_TO_GARAZS_VA_2.equals(trimmedFromToFromCSV)) {
                route.setDepotID(departureFromTos.get(trimmedFromToFromCSV));
                route.setArrivalStationID(arrivalFromTos.get(trimmedFromToFromCSV));
            } else {
                route.setDepartureStationID(departureFromTos.get(trimmedFromToFromCSV));
                route.setDepotID(arrivalFromTos.get(trimmedFromToFromCSV));
            }
        }
    }

    /**
     * All of the services can be served by the only depot we have
     * Get the vehicle services from the Járatok sheet, these come with the departure- and arrival stations, and create a Set of VehicleServices
     * Where Ud is needed, just use U instead
     *
     * @return a LinkedHashSet of VehicleServices
     */
    private Set<VehicleService> getVehicleServicesFromTheCSV(List<TerminalStation> terminalStations) {
        System.out.println("Getting the Vehicle Services from the CSV.");
        Set<VehicleService> vehicleServices = new LinkedHashSet<>();

        int terminalStation1ID = terminalStations.get(0).getId();
        int terminalStation2ID = terminalStations.get(1).getId();

        // Source: https://mkyong.com/java/how-to-read-and-parse-csv-file-in-java/
        BufferedReader br = null;
        String line;

        try {

            br = new BufferedReader(new FileReader(PATH_JARATOK_CSV, Charset.forName(ENCODING_ISO_8859_2)));
            while ((line = br.readLine()) != null) {
                String[] vehicleServiceFromCSV = line.split(CSV_DELIMITER);

                String departureTimeFromCSV = vehicleServiceFromCSV[0];
                String arrivalTimeFromCSV = vehicleServiceFromCSV[1];
                String typeFromCSV = vehicleServiceFromCSV[2];

                VehicleService vehicleService = new VehicleService(createLocalTimeFromMinutes(departureTimeFromCSV), createLocalTimeFromMinutes(arrivalTimeFromCSV));

                setDepartureAndArrivalStationIDsAccordingly(terminalStation1ID, terminalStation2ID, typeFromCSV.trim(), vehicleService);

                vehicleServices.add(vehicleService);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println(String.format("Getting the Vehicle Services from the CSV - DONE. Found %d vehicle services.", vehicleServices.size()));
        return vehicleServices;
    }

    private void setDepartureAndArrivalStationIDsAccordingly(int terminalStation1ID, int terminalStation2ID, String trimmedTypeFromCSV, VehicleService vehicleService) {
        if (TYPE_1_VA_2_VA_1.equals(trimmedTypeFromCSV)) {
            vehicleService.setDepartureStationID(terminalStation2ID);
            vehicleService.setArrivalStationID(terminalStation1ID);
        } else {
            vehicleService.setDepartureStationID(terminalStation1ID);
            vehicleService.setArrivalStationID(terminalStation2ID);
        }
    }

    private LocalTime createLocalTimeFromMinutes(String departureTimeFromCSV) {
        String stringDoubleValueOfTime = String.valueOf(Double.parseDouble(departureTimeFromCSV) / 60);
        int indexOfDecimal = stringDoubleValueOfTime.indexOf(INDEX_OF_DECIMAL_SEPARATOR);
        int hours = Integer.parseInt(stringDoubleValueOfTime.substring(0, indexOfDecimal));
        int minutes = (int) (60 * Double.parseDouble(stringDoubleValueOfTime.substring(indexOfDecimal)));

        return LocalTime.parse(addExtraZeroIfRequired(hours) + LOCAL_TIME_SEPARATOR + addExtraZeroIfRequired(minutes) + LOCAL_TIME_SECONDS);
    }

    private String addExtraZeroIfRequired(int value) {
        return String.valueOf(value).length() == 1 ? LOCAL_TIME_EXTRA_ZERO + value : String.valueOf(value);
    }

    /**
     * Create a Timeline for each terminal station (Vá.1 and Vá.2)
     * In these Timelines set the departure- and arrival nodes according to the departure- and arrival times and stations found inside the VehicleService
     *
     * @param terminalStations - all of the Terminal Stations
     * @param vehicleServices - all of the Vehicle Services
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
     * N: all departure- and arrival times (nodes) from the terminal stations
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

            // Getting the node ID of the first compatible vehicle service
            LocalTime departureTimeFromTheFirstCompatibleVehicleService = vehicleServicesAccordingToTheIDs.get(0).getDepartureTime();

            Optional<Node> departureNode = departureNodesFromTerminalStation.stream()
                    .filter(node -> node.getLocalTime().equals(departureTimeFromTheFirstCompatibleVehicleService))
                    .findFirst();

            int arrivalNodeID = departureNode.map(ClassWithID::getId).orElse(0);

            edgesFromTheFirstPhase.add(new Edge(EdgeType.OVERHEAD, departureNodeID, arrivalNodeID));
        }

        System.out.println(String.format("Done with the First Phase. Number of edges: %d", edgesFromTheFirstPhase.size()));
        return edgesFromTheFirstPhase;
    }

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
            TerminalStation terminalStation = departureNodeIDsFromTerminalStation1.contains(arrivalNodeID)? terminalStation2: terminalStation1;

            if (departureNodeIDs.size() == 1) {
                edgesFromTheSecondPhase.add(new Edge(EdgeType.OVERHEAD, departureNodeIDs.get(0), arrivalNodeID));
            } else {
                List<Node> arrivalNodes = terminalStation.getTimeline().getArrivalNodes()
                        .stream()
                        .filter(node -> departureNodeIDs.contains(node.getId()))
                        .sorted(Comparator.comparing(Node::getLocalTime))
                        .collect(Collectors.toList());

                Node lastDeparting = arrivalNodes.get(arrivalNodes.size() - 1);
                edgesFromTheSecondPhase.add(new Edge(EdgeType.OVERHEAD, lastDeparting.getId(), arrivalNodeID));
            }
        });

        System.out.println(String.format("Done with the Second Phase. Number of edges: %d", edgesFromTheSecondPhase.size()));
        return edgesFromTheSecondPhase;
    }

    // Integer: ID of VehicleService
    // List of Integers: IDs of compatible vehicle services
    private Map<Integer, List<Integer>> getCompatibleVehicleServices(List<VehicleService> vehicleServices, List<Route> routes) {
        Map<Integer, List<Integer>> compatibleVehicleServices = new HashMap<>();

        for (int index = 0; index < vehicleServices.size() - 1; index++) {
            VehicleService vehicleService = vehicleServices.get(index);

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

    private boolean isCompatible(VehicleService one, VehicleService other, List<Route> routes) {
        int arrivalStationIDOfOne = one.getArrivalStationID();
        int departureStationIDOfOther = other.getDepartureStationID();

        // the timeToGetFromTheDepartureStationToTheArrivalStation would be zero
        if (arrivalStationIDOfOne == departureStationIDOfOther) {
            return false;
        }

        LocalTime arrivalTimeOfOne = one.getArrivalTime();
        LocalTime departureTimeOfOther = other.getDepartureTime();

        List<Route> NTypeRoutes = routes.stream().filter(route -> route.getRouteType().equals(RouteType.N)).collect(Collectors.toList());
        LocalTime departureTimeOfOne = one.getDepartureTime();

        long differenceInMinutes = Math.abs(Duration.between(departureTimeOfOther, arrivalTimeOfOne).toMinutes());
        for (Route route : NTypeRoutes) {
            if (isTimeInsideTimeOfTheDay(departureTimeOfOne, route.getTimeOfTheDay())) {
                differenceInMinutes -= route.getTechnicalTime();
                differenceInMinutes -= route.getCompensatoryTime();

                break;
            }
        }

        boolean oneArrivedBeforeOrAtTheSameTimeAsTheOtherDeparts = arrivalTimeOfOne.isBefore(departureTimeOfOther) || arrivalTimeOfOne.equals(departureTimeOfOther);

        long timeToGetFromTheDepartureStationToTheArrivalStation = Duration.between(other.getDepartureTime(), other.getArrivalTime()).toMinutes();

        return oneArrivedBeforeOrAtTheSameTimeAsTheOtherDeparts && (differenceInMinutes < timeToGetFromTheDepartureStationToTheArrivalStation);
    }

    private boolean isTimeInsideTimeOfTheDay(LocalTime time, TimeOfTheDay timeOfTheDay) {
        LocalTime startingTime = timeOfTheDay.getStartingTime();
        LocalTime finishTime = timeOfTheDay.getFinishTime();

        return (time.isAfter(startingTime) || time.equals(startingTime)) && (time.isBefore(finishTime) || time.equals(finishTime));
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
     * @return a LinkedHashSet containing all of the depot departing- and arriving edges
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
            depotDepartingAndArrivingEdges.add(new Edge(EdgeType.DEPOT, depotArrivalNodeID, vehicleServiceArrivalNodeID));
        });

        System.out.println(String.format("Creating R - DONE. Number of edges: %d", depotDepartingAndArrivingEdges.size()));
        return depotDepartingAndArrivingEdges;
    }

    /**
     * Kd from D:
     * For the depot: create edges between arrivalTimes and the departureTimes
     * EdgeType: DEPOT
     *
     * @return a LinkedHashSet with all of the circular flow edges between depot arriving- and departing nodes
     */
    private Set<Edge> createK(Depot depot) {
        System.out.println("Creating K.");
        Set<Edge> depotCircularFlowEdges = new LinkedHashSet<>();

        Timeline timelineFromDepot = depot.getTimeline();
        int depotDepartureNodeID = timelineFromDepot.getDepartureNodes().get(0).getId();
        int depotArrivalNodeID = timelineFromDepot.getArrivalNodes().get(0).getId();

        depotCircularFlowEdges.add(new Edge(EdgeType.DEPOT, depotDepartureNodeID, depotArrivalNodeID));

        System.out.println(String.format("Creating K - DONE. Number of edges: %d", depotCircularFlowEdges.size()));
        return depotCircularFlowEdges;
    }

    /**
     * Wd: Create waiting edges: these always follow the current station's timeline and they connect the following departure times, collecting their stream
     * Iterate over the two terminal stations
     * Iterate over the departureTimes found inside the terminal station
     * Get the difference between these times (check if it's the first node on the Timeline)
     * This will be the weight of the edge
     * Connect the times (nodes) to form an edge
     * EdgeType: WAITING
     *
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

    private Graph<Integer> createGraphFromEdges(List<Edge> edges) {
        Graph<Integer> graph = new Graph<>();

        edges.forEach(edge -> graph.addEdge(edge.getDepartureNodeID(), edge.getArrivalNodeID(), false));

        return graph;
    }

    // Id: id of the node
    // Label: the terminal station's name
    // Category: node type
    // Time: local time inside the node
    private void createNodesCSV(List<TerminalStation> terminalStations, Depot depot) {
        try (PrintWriter writer = new PrintWriter(new File("C:\\asd\\Nodes.csv"))) {

            StringBuilder sb = new StringBuilder();
            sb.append("Id");
            sb.append(',');
            sb.append("Label");
            sb.append(',');
            sb.append("Category");
            sb.append(',');
            sb.append("StationName");
            sb.append('\n');

            terminalStations.forEach(terminalStation -> {
                Timeline timelineFromTerminalStation = terminalStation.getTimeline();

                timelineFromTerminalStation.getDepartureNodes().forEach(node -> {
                    sb.append(node.getId());
                    sb.append(',');
                    sb.append(node.getLocalTime());
                    sb.append(',');
                    sb.append(node.getNodeType().getName());
                    sb.append(',');
                    sb.append(terminalStation.getStationName());
                    sb.append('\n');
                });

                timelineFromTerminalStation.getArrivalNodes().forEach(node -> {
                    sb.append(node.getId());
                    sb.append(',');
                    sb.append(node.getLocalTime());
                    sb.append(',');
                    sb.append(node.getNodeType().getName());
                    sb.append(',');
                    sb.append(terminalStation.getStationName());
                    sb.append('\n');
                });

            });

            Timeline timelineFromDepot = depot.getTimeline();

            timelineFromDepot.getDepartureNodes().forEach(node -> {
                sb.append(node.getId());
                sb.append(',');
                sb.append(node.getLocalTime());
                sb.append(',');
                sb.append(node.getNodeType().getName());
                sb.append(',');
                sb.append("Depot");
                sb.append('\n');
            });

            timelineFromDepot.getArrivalNodes().forEach(node -> {
                sb.append(node.getId());
                sb.append(',');
                sb.append(node.getLocalTime());
                sb.append(',');
                sb.append(node.getNodeType().getName());
                sb.append(',');
                sb.append("Depot");
                sb.append('\n');
            });

            writer.write(sb.toString());

            System.out.println("done!");

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    // Source: https://stackoverflow.com/questions/30073980/java-writing-strings-to-a-csv-file
    private void createEdgesCSV(List<Edge> edges) {
        try (PrintWriter writer = new PrintWriter(new File("C:\\asd\\Edges.csv"))) {

            StringBuilder sb = new StringBuilder();
            sb.append("Source");
            sb.append(',');
            sb.append("Target");
            sb.append(',');
            sb.append("Type");
            sb.append(',');
            sb.append("Category");
            sb.append('\n');

            edges.forEach(edge -> {
                sb.append(edge.getDepartureNodeID());
                sb.append(',');
                sb.append(edge.getArrivalNodeID());
                sb.append(',');
                sb.append("Directed");
                sb.append(',');
                sb.append(edge.getEdgeType().getName());
                sb.append('\n');
            });

            writer.write(sb.toString());

            System.out.println("done!");

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    // Source: https://gist.github.com/Munawwar/924389/adec31107f16e3938806e25c6ea2f6a15007d79b
    public void readCSV() {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(PATH_INPUT_VSP_XLSX);
            Workbook workbook = WorkbookFactory.create(inputStream);

            for(int index=0; index<workbook.getNumberOfSheets(); index++) {
                Sheet sheet = workbook.getSheetAt(index);
                String sheetName = workbook.getSheetAt(index).getSheetName();
                convertExcelToCSV(sheet, sheetName);
//                echoAsCSV(wb.getSheetAt(i));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Source: https://stackoverflow.com/questions/17345696/convert-xlsx-to-csv-with-apache-poi-api
    private void convertExcelToCSV(Sheet sheet, String sheetName) {
        StringBuilder stringBuilder = new StringBuilder();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        try {
            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();

                    CellType type = cell.getCellTypeEnum();
                    if (type == CellType.BOOLEAN) {
                        stringBuilder.append(cell.getBooleanCellValue());
                    } else if (type == CellType.NUMERIC) {

                        // Source: https://stackoverflow.com/questions/25238527/java-get-timestamp-from-excel-file-without-converting-to-decimal
                        if (DateUtil.isCellDateFormatted(cell)) {
                            String formattedTime = timeFormat.format(cell.getDateCellValue());
                            stringBuilder.append(formattedTime);
                        } else {
                            stringBuilder.append(cell.getNumericCellValue());
                        }
                    } else if (type == CellType.STRING) {
                        stringBuilder.append(cell.getStringCellValue());
                    } else if (type == CellType.BLANK) {

                    } else {
                        stringBuilder.append(cell);
                    }

                    if (cellIterator.hasNext()) {
                        stringBuilder.append(CSV_DELIMITER);
                    }
                }

                stringBuilder.append('\n');
            }

            Files.write(Paths.get("D:\\" + sheetName + ".csv"), stringBuilder.toString().getBytes(ENCODING_ISO_8859_2));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void echoAsCSV(Sheet sheet) {
        Row row = null;

        for (int i = 0; i < sheet.getLastRowNum(); i++) {
            row = sheet.getRow(i);
            for (int j = 0; j < row.getLastCellNum(); j++) {
                System.out.print("\"" + row.getCell(j) + "\";");
            }
            System.out.println();
        }
    }
}
