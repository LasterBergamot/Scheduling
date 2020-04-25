package com.scheduling.model.route;

import com.scheduling.model.station.Station;

/**
 * Paraméterek sheet in Input_VSP.xlsx
 */
public class Route {

    // Column A
    private String routeName;

    // From column B
    private Station departureStation;

    // From column B
    private Station arrivalStation;

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
