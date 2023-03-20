package com.db4odoc.f1.chapter5;

import java.util.*;

public class SensorReadout {
    private Date time;
    private Car car;
    private String description;

    protected SensorReadout(Date time,Car car,String description) {
        this.time=time;
        this.car=car;
        this.description=description;
    }

    public Car getCar() {
        return car;
    }

    public Date getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }
    public String toString() {
        return car+" : "+time+" : "+description;
    }
}