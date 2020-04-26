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

    public Route(String routeName, int departureStationID, int arrivalStationID, RouteType routeType, TimeOfTheDay timeOfTheDay, int duration, int technicalTime, int compensatoryTime) {
        this.routeName = routeName;
        this.departureStationID = departureStationID;
        this.arrivalStationID = arrivalStationID;
        this.routeType = routeType;
        this.timeOfTheDay = timeOfTheDay;
        this.duration = duration;
        this.technicalTime = technicalTime;
        this.compensatoryTime = compensatoryTime;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public int getDepartureStationID() {
        return departureStationID;
    }

    public void setDepartureStationID(int departureStationID) {
        this.departureStationID = departureStationID;
    }

    public int getArrivalStationID() {
        return arrivalStationID;
    }

    public void setArrivalStationID(int arrivalStationID) {
        this.arrivalStationID = arrivalStationID;
    }

    public RouteType getRouteType() {
        return routeType;
    }

    public void setRouteType(RouteType routeType) {
        this.routeType = routeType;
    }

    public TimeOfTheDay getTimeOfTheDay() {
        return timeOfTheDay;
    }

    public void setTimeOfTheDay(TimeOfTheDay timeOfTheDay) {
        this.timeOfTheDay = timeOfTheDay;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getTechnicalTime() {
        return technicalTime;
    }

    public void setTechnicalTime(int technicalTime) {
        this.technicalTime = technicalTime;
    }

    public int getCompensatoryTime() {
        return compensatoryTime;
    }

    public void setCompensatoryTime(int compensatoryTime) {
        this.compensatoryTime = compensatoryTime;
    }
}
