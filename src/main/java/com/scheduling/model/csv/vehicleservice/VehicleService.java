package com.scheduling.model.csv.vehicleservice;

import com.scheduling.model.util.ClassWithID;

import java.time.LocalTime;

/**
 * Járatok sheet in Input_VSP.xlsx
 *
 * Object for storing data.
 */
public class VehicleService extends ClassWithID {

    // Column A
    private LocalTime departureTime;

    // Column B
    private LocalTime arrivalTime;

    // Column C
    private int departureStationID;

    // Column D
    private int arrivalStationID;

    public VehicleService() {
        this.id = ID_COUNTER.getAndIncrement();
    }

    public VehicleService(LocalTime departureTime, LocalTime arrivalTime) {
        this.id = ID_COUNTER.getAndIncrement();

        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }

    public VehicleService(LocalTime departureTime, LocalTime arrivalTime, int departureStationID, int arrivalStationID) {
        this.id = ID_COUNTER.getAndIncrement();

        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.departureStationID = departureStationID;
        this.arrivalStationID = arrivalStationID;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
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

    @Override
    public String toString() {
        return "VehicleService{" +
                "departureTime=" + departureTime +
                ", arrivalTime=" + arrivalTime +
                ", departureStationID=" + departureStationID +
                ", arrivalStationID=" + arrivalStationID +
                ", id=" + id +
                '}';
    }
}
