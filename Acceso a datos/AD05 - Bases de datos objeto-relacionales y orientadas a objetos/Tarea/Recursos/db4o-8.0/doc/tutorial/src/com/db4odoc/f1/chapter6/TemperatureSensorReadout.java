package com.db4odoc.f1.chapter6;

import java.util.*;


public class TemperatureSensorReadout extends SensorReadout {
    private double temperature;
    
    public TemperatureSensorReadout(
            Date time,Car car,
            String description,double temperature) {
        super(time,car,description);
        this.temperature=temperature;
    }
    
    public double getTemperature() {
        return temperature;
    }

    public String toString() {
        return super.toString()+" temp : "+temperature;
    }
}
