package com.scheduling.model.busservice;

import java.time.LocalTime;

/**
 * Járatok sheet in Input_VSP.xlsx
 */
public class BusService {

    // Column A
    private LocalTime departureTime;

    // Column B
    private LocalTime arrivalTime;

    // Column C
    private int departureStationID;

    // Column D
    private int arrivalStationID;
}
