package com.scheduling.model.route;

public enum RouteType {

    N("Normal"), G("Garage");

    private final String name;

    RouteType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
