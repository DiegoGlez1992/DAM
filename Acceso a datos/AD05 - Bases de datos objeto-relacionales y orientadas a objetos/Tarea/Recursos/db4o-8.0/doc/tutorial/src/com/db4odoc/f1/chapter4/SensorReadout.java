package com.db4odoc.f1.chapter4;

import java.util.*;

public class SensorReadout {
    private double[] values;
    private Date time;
    private Car car;

    public SensorReadout(double[] values,Date time,Car car) {
        this.values=values;
        this.time=time;
        this.car=car;
    }

    public Car getCar() {
        return car;
    }

    public Date getTime() {
        return time;
    }

    public int getNumValues() {
        return values.length;
    }
    
    public double[] getValues(){
    	return values;
    }
    
    public double getValue(int idx) {
        return values[idx];
    }

    public String toString() {
        StringBuffer str=new StringBuffer();
        str.append(car.toString())
        	.append(" : ")
        	.append(time.getTime())
        	.append(" : ");
        for(int idx=0;idx<values.length;idx++) {
            if(idx>0) {
                str.append(',');
            }
            str.append(values[idx]);
        }
        return str.toString();
    }
}