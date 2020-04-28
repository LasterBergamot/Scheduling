package com.scheduling.util;

import com.scheduling.model.csv.route.Route;
import com.scheduling.model.csv.route.RouteType;
import com.scheduling.model.csv.route.TimeOfTheDay;
import com.scheduling.model.csv.vehicleservice.VehicleService;
import com.scheduling.model.depot.Depot;
import com.scheduling.model.graph.edge.Edge;
import com.scheduling.model.graph.node.Node;
import com.scheduling.model.station.TerminalStation;
import com.scheduling.model.station.Timeline;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;

import static com.scheduling.util.SchedulingConstants.*;

public class CSVUtil {

    /**
     * Get the routes from the Paraméterek sheet (Input_VSP.xlsx) and create a List of Routes
     *
     * @return an ArrayList of Routes
     */
    public static List<Route> getRoutesFromTheCSV(List<TerminalStation> terminalStations, Depot depot) {
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

    private static Map<String, Integer> createArrivalFromTosMap(int terminalStation1ID, int terminalStation2ID, int depotID) {
        return Map.of(
                FROM_TO_VA_2_FELE, terminalStation2ID,
                FROM_TO_VA_1_FELE, terminalStation1ID,
                FROM_TO_GARAZS_VA_1, terminalStation1ID,
                FROM_TO_VA_1_GARAZS, depotID,
                FROM_TO_GARAZS_VA_2, terminalStation2ID,
                FROM_TO_VA_2_GARAZS, depotID
        );
    }

    private static Map<String, Integer> createDepartureFromTosMap(int terminalStation1ID, int terminalStation2ID, int depotID) {
        return Map.of(
                FROM_TO_VA_2_FELE, terminalStation1ID,
                FROM_TO_VA_1_FELE, terminalStation2ID,
                FROM_TO_GARAZS_VA_1, depotID,
                FROM_TO_VA_1_GARAZS, terminalStation1ID,
                FROM_TO_GARAZS_VA_2, depotID,
                FROM_TO_VA_2_GARAZS, terminalStation2ID
        );
    }

    private static Route createRouteFromRouteFromCSV(Map<String, Integer> departureFromTos, Map<String, Integer> arrivalFromTos, String[] routeFromCSV) {
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

    private static void setIDsForRouteAccordingly(Map<String, Integer> departureFromTos, Map<String, Integer> arrivalFromTos, String fromToFromCSV, RouteType routeType, Route route) {
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
    public static Set<VehicleService> getVehicleServicesFromTheCSV(List<TerminalStation> terminalStations) {
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

    private static void setDepartureAndArrivalStationIDsAccordingly(int terminalStation1ID, int terminalStation2ID, String trimmedTypeFromCSV, VehicleService vehicleService) {
        if (TYPE_1_VA_2_VA_1.equals(trimmedTypeFromCSV)) {
            vehicleService.setDepartureStationID(terminalStation2ID);
            vehicleService.setArrivalStationID(terminalStation1ID);
        } else {
            vehicleService.setDepartureStationID(terminalStation1ID);
            vehicleService.setArrivalStationID(terminalStation2ID);
        }
    }

    private static LocalTime createLocalTimeFromMinutes(String departureTimeFromCSV) {
        String stringDoubleValueOfTime = String.valueOf(Double.parseDouble(departureTimeFromCSV) / 60);
        int indexOfDecimal = stringDoubleValueOfTime.indexOf(INDEX_OF_DECIMAL_SEPARATOR);
        int hours = Integer.parseInt(stringDoubleValueOfTime.substring(0, indexOfDecimal));
        int minutes = (int) (60 * Double.parseDouble(stringDoubleValueOfTime.substring(indexOfDecimal)));

        return LocalTime.parse(addExtraZeroIfRequired(hours) + LOCAL_TIME_SEPARATOR + addExtraZeroIfRequired(minutes) + LOCAL_TIME_SECONDS);
    }

    private static String addExtraZeroIfRequired(int value) {
        return String.valueOf(value).length() == 1 ? LOCAL_TIME_EXTRA_ZERO + value : String.valueOf(value);
    }

    public static void createNodesCSV(List<TerminalStation> terminalStations, Depot depot) {
        System.out.println("Creating CSV from the nodes.");

        try (PrintWriter writer = new PrintWriter(new File(PATH_NODES_CSV))) {
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append(String.join(CSV_DELIMITER, OUTPUT_CSV_HEADER_ID, OUTPUT_CSV_HEADER_LABEL, OUTPUT_CSV_HEADER_CATEGORY, OUTPUT_CSV_HEADER_STATION_NAME));
            stringBuilder.append(NEW_LINE_CHARACTER);

            terminalStations.forEach(terminalStation -> {
                Timeline timelineFromTerminalStation = terminalStation.getTimeline();

                createCSVRows(stringBuilder, timelineFromTerminalStation.getDepartureNodes(), terminalStation.getStationName());
                createCSVRows(stringBuilder, timelineFromTerminalStation.getArrivalNodes(), terminalStation.getStationName());
            });

            Timeline timelineFromDepot = depot.getTimeline();

            createCSVRows(stringBuilder, timelineFromDepot.getDepartureNodes(), OUTPUT_CSV_STATION_NAME_DEPOT);
            createCSVRows(stringBuilder, timelineFromDepot.getArrivalNodes(), OUTPUT_CSV_STATION_NAME_DEPOT);

            writer.write(stringBuilder.toString());

            System.out.println("Creating CSV from the nodes - DONE.");
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void createCSVRows(StringBuilder stringBuilder, List<Node> nodes, String stationName) {
        nodes.forEach(node -> {
            stringBuilder.append(String.join(CSV_DELIMITER, String.valueOf(node.getId()), node.getLocalTime().toString(),
                    node.getNodeType().getName(), stationName));
            stringBuilder.append(NEW_LINE_CHARACTER);
        });
    }

    // Source: https://stackoverflow.com/questions/30073980/java-writing-strings-to-a-csv-file
    public static void createEdgesCSV(List<Edge> edges) {
        System.out.println("Creating CSV from the edges.");

        try (PrintWriter writer = new PrintWriter(new File(PATH_EDGES_CSV))) {
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append(String.join(CSV_DELIMITER, OUTPUT_CSV_HEADER_SOURCE, OUTPUT_CSV_HEADER_TARGET, OUTPUT_CSV_HEADER_TYPE, OUTPUT_CSV_HEADER_CATEGORY));
            stringBuilder.append(NEW_LINE_CHARACTER);

            edges.forEach(edge -> {
                stringBuilder.append(String.join(CSV_DELIMITER,
                        String.valueOf(edge.getDepartureNodeID()), String.valueOf(edge.getArrivalNodeID()), OUTPUT_CSV_TYPE_DIRECTED, edge.getEdgeType().getName()));
                stringBuilder.append(NEW_LINE_CHARACTER);
            });

            writer.write(stringBuilder.toString());

            System.out.println("Creating CSV from the edges - DONE.");
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

            for(int index=0; index < workbook.getNumberOfSheets(); index++) {
                Sheet sheet = workbook.getSheetAt(index);
                String sheetName = workbook.getSheetAt(index).getSheetName();

                convertExcelToCSV(sheet, sheetName);
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
        SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT_HH_MM_SS);

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

                stringBuilder.append(NEW_LINE_CHARACTER);
            }

            Files.write(Paths.get("D:\\" + sheetName + ".csv"), stringBuilder.toString().getBytes(ENCODING_ISO_8859_2));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
