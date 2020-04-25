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
}
