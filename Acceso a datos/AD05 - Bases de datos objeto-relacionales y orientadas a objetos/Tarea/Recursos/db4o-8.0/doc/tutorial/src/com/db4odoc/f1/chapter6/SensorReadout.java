package com.db4odoc.f1.chapter6;

import java.util.*;

public class SensorReadout {
    private Date time;
    private Car car;
    private String description;
    private SensorReadout next;

    protected SensorReadout(Date time,Car car,String description) {
        this.time=time;
        this.car=car;
        this.description=description;
        this.next=null;
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

    public SensorReadout getNext() {
        return next;
    }
    
    public void append(SensorReadout readout) {
        if(next==null) {
            next=readout;
        }
        else {
            next.append(readout);
        }
    }
    
    public int countElements() {
        return (next==null ? 1 : next.countElements()+1);
    }
    
    public String toString() {
        return car+" : "+time+" : "+description;
    }
}