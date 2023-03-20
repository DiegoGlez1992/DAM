package com.db4odoc.f1.chapter5;

import java.util.*;

public class Car {
    private String model;
    private Pilot pilot;
    private List history;

    public Car(String model) {
        this.model=model;
        this.pilot=null;
        this.history=new ArrayList();
    }

    public Pilot getPilot() {
        return pilot;
    }

    public void setPilot(Pilot pilot) {
        this.pilot=pilot;
    }

    public String getModel() {
        return model;
    }

    public SensorReadout[] getHistory() {
        return (SensorReadout[])history.toArray(new SensorReadout[history.size()]);
    }
    
    public void snapshot() {        
        history.add(new TemperatureSensorReadout(
                new Date(),this,"oil",pollOilTemperature()));
        history.add(new TemperatureSensorReadout(
                new Date(),this,"water",pollWaterTemperature()));
        history.add(new PressureSensorReadout(
                new Date(),this,"oil",pollOilPressure()));
    }

    protected double pollOilTemperature() {
        return 0.1*history.size();
    }

    protected double pollWaterTemperature() {
        return 0.2*history.size();
    }

    protected double pollOilPressure() {
        return 0.3*history.size();
    }

    public String toString() {
        return model+"["+pilot+"]/"+history.size();
    }
}