package com.db4odoc.f1.chapter8;

import java.util.*;

import com.db4o.activation.*;


public class PressureSensorReadout extends SensorReadout {
    private double pressure;
    
    public PressureSensorReadout(
            Date time,Car car,
            String description,double pressure) {
        super(time,car,description);
        this.pressure=pressure;
    }
    
    public double getPressure() {
    	activate(ActivationPurpose.READ);
        return pressure;
    }
    
    public String toString() {
        return super.toString()+" pressure : "+pressure;
    }
}
