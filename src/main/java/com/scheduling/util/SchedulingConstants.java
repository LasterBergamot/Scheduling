package com.scheduling.util;

public class SchedulingConstants {

    // Couldn't managed to get these files from the resource folder, so I'm using these paths instead
    public static final String PATH_INPUT_VSP_XLSX = "D:\\Input_VSP.xlsx";
    public static final String PATH_PARAMETEREK_CSV = "D:\\Parameterek.csv";
    public static final String PATH_JARATOK_CSV = "D:\\Jaratok.csv";

    // Used when creating the CSVs
    public static final String ENCODING_ISO_8859_2 = "ISO-8859-2";
    public static final String CSV_DELIMITER = ",";
    public static final String TIME_FORMAT_HH_MM_SS = "HH:mm:ss";

    // Used when getting the Routes from the Parameterek sheet
    public static final String FROM_TO_VA_2_FELE = "Vá.2 felé";
    public static final String FROM_TO_VA_1_FELE = "Vá.1 felé";

    public static final String FROM_TO_GARAZS_VA_1 = "Garázs - Vá.1";
    public static final String FROM_TO_VA_1_GARAZS = "Vá.1 - Garázs";
    public static final String FROM_TO_GARAZS_VA_2 = "Garázs - Vá.2";
    public static final String FROM_TO_VA_2_GARAZS = "Vá.2 - Garázs";

    // Used when getting the VehicleServices from the Jaratok sheet
    public static final String TYPE_1_VA_2_VA_1 = "Vá.2->Vá.1";

    // Used when converting the given minutes found inside the Jaratok sheet to LocalTime objects
    public static final String INDEX_OF_DECIMAL_SEPARATOR = ".";
    public static final String LOCAL_TIME_EXTRA_ZERO = "0";
    public static final String LOCAL_TIME_SEPARATOR = ":";
    public static final String LOCAL_TIME_SECONDS = ":00";

    // Used when creating the output CSVs
    public static final String PATH_NODES_CSV = "D:\\Nodes.csv";
    public static final String PATH_EDGES_CSV = "D:\\Edges.csv";

    public static final String OUTPUT_CSV_HEADER_ID = "Id";
    public static final String OUTPUT_CSV_HEADER_LABEL = "Label";
    public static final String OUTPUT_CSV_HEADER_CATEGORY = "Category";
    public static final String OUTPUT_CSV_HEADER_STATION_NAME = "StationName";
    public static final String OUTPUT_CSV_STATION_NAME_DEPOT = "Depot";
    public static final String OUTPUT_CSV_HEADER_SOURCE = "Source";
    public static final String OUTPUT_CSV_HEADER_TARGET = "Target";
    public static final String OUTPUT_CSV_HEADER_TYPE = "Type";
    public static final String OUTPUT_CSV_TYPE_DIRECTED = "Directed";

    public static final char NEW_LINE_CHARACTER = '\n';

    // Used at the end of the program
    public static final String GRAPH_PRINT_QUESTION = "\nWould you like to print out the whole graph? Type in 'y' or 'yes' to print.";
    public static final String NODES_EDGES_CSV_EXPORT_QUESTION = "\nWould you like to export the nodes and edges as .csv files to the already defined paths: nodes: "
            + PATH_NODES_CSV
            + ", edges: "
            + PATH_EDGES_CSV
            + "? Type in 'y' or 'yes' to export.";
    public static final String ANSWER_Y = "y";
    public static final String ANSWER_YES = "yes";
}
