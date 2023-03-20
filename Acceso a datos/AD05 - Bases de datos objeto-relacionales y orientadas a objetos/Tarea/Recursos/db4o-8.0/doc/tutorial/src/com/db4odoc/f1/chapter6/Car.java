package com.db4odoc.f1.chapter6;

import java.util.*;

public class Car {
    private String model;
    private Pilot pilot;
    private SensorReadout history;

    public Car(String model) {
        this.model=model;
        this.pilot=null;
        this.history=null;
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

    public SensorReadout getHistory() {
        return history;
    }
    
    public void snapshot() {        
        appendToHistory(new TemperatureSensorReadout(
                new Date(),this,"oil",pollOilTemperature()));
        appendToHistory(new TemperatureSensorReadout(
                new Date(),this,"water",pollWaterTemperature()));
        appendToHistory(new PressureSensorReadout(
                new Date(),this,"oil",pollOilPressure()));
    }

    protected double pollOilTemperature() {
        return 0.1*countHistoryElements();
    }

    protected double pollWaterTemperature() {
        return 0.2*countHistoryElements();
    }

    protected double pollOilPressure() {
        return 0.3*countHistoryElements();
    }

    public String toString() {
        return model+"["+pilot+"]/"+countHistoryElements();
    }
    
    private int countHistoryElements() {
        return (history==null ? 0 : history.countElements());
    }
    
    private void appendToHistory(SensorReadout readout) {
        if(history==null) {
            history=readout;
        }
        else {
            history.append(readout);
        }
    }
}