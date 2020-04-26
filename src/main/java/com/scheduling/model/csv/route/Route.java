package com.scheduling.model.csv.route;

/**
 * Paraméterek sheet in Input_VSP.xlsx
 *
 * Object for storing data.
 */
public class Route {

    // Column A
    private String routeName;

    // From column B: default value is 0 == unused
    private int departureStationID = 0;

    // From column B: default value is 0 == unused
    private int arrivalStationID = 0;

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

    // The ID of the depot: default value is 0 == unused
    private int depotID = 0;

    public Route(String routeName, RouteType routeType, TimeOfTheDay timeOfTheDay, int duration, int technicalTime, int compensatoryTime) {
        this.routeName = routeName;
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

    public int getDepotID() {
        return depotID;
    }

    public void setDepotID(int depotID) {
        this.depotID = depotID;
    }

    @Override
    public String toString() {
        return "Route{" +
                "routeName='" + routeName + '\'' +
                ", departureStationID=" + departureStationID +
                ", arrivalStationID=" + arrivalStationID +
                ", routeType=" + routeType +
                ", timeOfTheDay=" + timeOfTheDay +
                ", duration=" + duration +
                ", technicalTime=" + technicalTime +
                ", compensatoryTime=" + compensatoryTime +
                ", depotID=" + depotID +
                '}';
    }
}
