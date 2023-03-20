package com.db4odoc.f1.chapter8;

import java.util.*;

import com.db4o.activation.*;


public class TemperatureSensorReadout extends SensorReadout {
    private double temperature;
    
    public TemperatureSensorReadout(
            Date time,Car car,
            String description,double temperature) {
        super(time,car,description);
        this.temperature=temperature;
    }
    
    public double getTemperature() {
    	activate(ActivationPurpose.READ);
        return temperature;
    }

    public String toString() {
        return super.toString()+" temp : "+temperature;
    }
}
