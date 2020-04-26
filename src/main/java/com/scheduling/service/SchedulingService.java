package com.scheduling.service;

import com.scheduling.model.csv.route.Route;
import com.scheduling.model.csv.vehicleservice.VehicleService;
import com.scheduling.model.depot.Depot;
import com.scheduling.model.graph.edge.Edge;
import com.scheduling.model.graph.node.Node;
import com.scheduling.model.station.TerminalStation;
import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class SchedulingService {

    private static final String PATH_INPUT_VSP_XLSX = "D:\\Input_VSP.xlsx";

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

        List<Route> routes = getRoutesFromTheCSV();

        Set<VehicleService> vehicleServices = getVehicleServicesFromTheCSV();

        createTimelinesForAllOfTheTerminalStations(terminalStations, vehicleServices);

        createTheTimelineForTheDepot(depot, vehicleServices, routes);

        Set<Node> N = createN(terminalStations);

        Set<Edge> E = createE(vehicleServices, routes);

        Set<Edge> B = createB(terminalStations, vehicleServices);

        Set<Edge> R = createR(depot, vehicleServices);

        Set<Edge> K = createK(depot);

        Set<Edge> W = createW(terminalStations);

        Set<Edge> A = createA(E, B, R, K, W);

        // E: all of the edges of the graph. Union of all Ads.
        Set<Edge> allOfTheEdgesOfTheGraph;

        System.out.println("\nDone with creating a proper scheduling.");
    }

    /**
     * Get the routes from the Paraméterek sheet (Input_VSP.xlsx) and create a List of Routes
     *
     * @return an ArrayList of Routes
     */
    private List<Route> getRoutesFromTheCSV() {
        System.out.println("Getting the Routes from the CSV.");
        List<Route> routes = new ArrayList<>();

        System.out.println("Getting the Routes from the CSV - DONE.");
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
        System.out.println("Getting the Vehicle Services from the CSV.");
        Set<VehicleService> vehicleServices = new HashSet<>();

        System.out.println("Getting the Vehicle Services from the CSV - DONE.");
        return vehicleServices;
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

        System.out.println("Creating Timelines for all of the Terminal Stations - DONE.");
    }

    private void createTheTimelineForTheDepot(Depot depot, Set<VehicleService> vehicleServices, List<Route> routes) {
        System.out.println("Creating the Timeline for the depot.");

        System.out.println("Creating the Timeline for the depot - DONE.");
    }

    /**
     * N: all departure- and arrival times (nodes) from the terminal stations
     *
     * @return a HashSet containing all of the nodes of the network
     */
    private Set<Node> createN(List<TerminalStation> terminalStations) {
        System.out.println("Creating N.");
        Set<Node> allOfTheNodesOfTheNetwork = new HashSet<>();

        System.out.println("Creating N - DONE.");
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
    private Set<Edge> createE(Set<VehicleService> vehicleServices, List<Route> routes) {
        System.out.println("Creating E.");
        Set<Edge> edgesForScheduledServices = new HashSet<>();

        System.out.println("Creating E - DONE.");
        return edgesForScheduledServices;
    }

    /**
     *  Bd from D: two phase merging strategy
     *  EdgeType: OVERHEAD
     * @return a HashSet with all of the overhead edges of the graph
     */
    private Set<Edge> createB(List<TerminalStation> terminalStations, Set<VehicleService> vehicleServices) {
        System.out.println("Creating B.");
        Set<Edge> edgesForOverheadServices = new HashSet<>();

        System.out.println("Creating B - DONE.");
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
    private Set<Edge> createR(Depot depot, Set<VehicleService> vehicleServices) {
        System.out.println("Creating R.");
        Set<Edge> depotDepartingAndArrivingEdges = new HashSet<>();

        System.out.println("Creating R - DONE.");
        return depotDepartingAndArrivingEdges;
    }

    /**
     * Kd from D:
     * For the depot: create edges between arrivalTimes and the departureTimes
     * EdgeType: DEPOT
     *
     * @return a HashSet with all of the circular flow edges between depot arriving- and departing nodes
     */
    private Set<Edge> createK(Depot depot) {
        System.out.println("Creating K.");
        Set<Edge> depotCircularFlowEdges = new HashSet<>();

        System.out.println("Creating K - DONE.");
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
     * @return a HashSet containing the waiting edges for each station
     */
    private Set<Edge> createW(List<TerminalStation> terminalStations) {
        System.out.println("Creating W.");
        Set<Edge> waitingEdgesForTheTerminalStations = new HashSet<>();

        System.out.println("Creating W - DONE.");
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
        System.out.println("Creating A.");
        Set<Edge> allOfTheEdgesOfTheNetwork = new HashSet<>();

        allOfTheEdgesOfTheNetwork.addAll(E);
        allOfTheEdgesOfTheNetwork.addAll(B);
        allOfTheEdgesOfTheNetwork.addAll(R);
        allOfTheEdgesOfTheNetwork.addAll(K);
        allOfTheEdgesOfTheNetwork.addAll(W);

        System.out.println("Creating A - DONE.");
        return allOfTheEdgesOfTheNetwork;
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
                        stringBuilder.append(",");
                    }
                }

                stringBuilder.append('\n');
            }

            Files.write(Paths.get("D:\\" + sheetName + ".csv"), stringBuilder.toString().getBytes("ISO-8859-2"));
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
