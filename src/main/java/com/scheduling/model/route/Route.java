package com.scheduling.model.route;

/**
 * Paraméterek sheet in Input_VSP.xlsx
 */
public class Route {

    // Column A
    private String routeName;

    // From column B: 0 if not used, this is the default value
    private int departureStationID = 0;

    // From column B: 0 if not used, this is the default value
    private int arrivalStationID = 0;

    // If column B contains Garázs as well: 0 if not used, this is the default value
    private int departureDepotID = 0;

    // If column B contains Garázs as well: 0 if not used, this is the default value
    private int getArrivalDepotID = 0;

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
