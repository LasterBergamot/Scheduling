package com.scheduling.model.busservice;

import com.scheduling.model.station.Station;

import java.time.LocalTime;

/**
 * Járatok sheet in Input_VSP.xlsx
 */
public class BusService {

    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private Station departureStation;
    private Station arrivalStation;
}
