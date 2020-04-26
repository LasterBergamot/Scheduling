package com.scheduling.model.csv.route;

/**
 * Paraméterek sheet in Input_VSP.xlsx
 *
 * Object for storing data.
 */
public class Route {

    // Column A
    private String routeName;

    // From column B
    private int departureStationID ;

    // From column B
    private int arrivalStationID;

    // Column C
    private RouteType routeType;

    // Column G and H
    // An interval
    private TimeOfTheDay timeOfTheDay;

    // Column I
    private int duration;

    // Column J
    private int technicalTime;

    // Column K
    private int compensatoryTime;
}
