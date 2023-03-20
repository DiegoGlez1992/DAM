package com.db4odoc.f1.chapter6;

import java.util.*;


public class PressureSensorReadout extends SensorReadout {
    private double pressure;
    
    public PressureSensorReadout(
            Date time,Car car,
            String description,double pressure) {
        super(time,car,description);
        this.pressure=pressure;
    }
    
    public double getPressure() {
        return pressure;
    }
    
    public String toString() {
        return super.toString()+" pressure : "+pressure;
    }
}
