package com.scheduling.model.csv.route;

import java.time.LocalTime;

/**
 * From Input_VSP.xlsx
 */
public class TimeOfTheDay {

    // Column G
    private LocalTime startingTime;

    // Column H
    private LocalTime finishTime;

    public TimeOfTheDay(LocalTime startingTime, LocalTime finishTime) {
        this.startingTime = startingTime;
        this.finishTime = finishTime;
    }

    public LocalTime getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(LocalTime startingTime) {
        this.startingTime = startingTime;
    }

    public LocalTime getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(LocalTime finishTime) {
        this.finishTime = finishTime;
    }
}
