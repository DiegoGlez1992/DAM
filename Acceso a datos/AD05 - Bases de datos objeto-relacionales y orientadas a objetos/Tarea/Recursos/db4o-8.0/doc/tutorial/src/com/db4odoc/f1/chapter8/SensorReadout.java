package com.db4odoc.f1.chapter8;

import java.util.*;

import com.db4o.activation.*;
import com.db4o.ta.*;

public class SensorReadout implements Activatable {
    private Date time;
    private Car car;
    private String description;
    private SensorReadout next;
    private transient Activator _activator;

    protected SensorReadout(Date time,Car car,String description) {
        this.time=time;
        this.car=car;
        this.description=description;
        this.next=null;
    }

    public Car getCar() {
    	activate(ActivationPurpose.READ);
        return car;
    }

    public Date getTime() {
    	activate(ActivationPurpose.READ);
        return time;
    }

    public String getDescription() {
    	activate(ActivationPurpose.READ);
        return description;
    }

    public SensorReadout getNext() {
    	activate(ActivationPurpose.READ);
        return next;
    }
    
    public void append(SensorReadout readout) {
    	activate(ActivationPurpose.WRITE);
        if(next==null) {
            next=readout;
        }
        else {
            next.append(readout);
        }
    }
    
    public int countElements() {
    	activate(ActivationPurpose.READ);
        return (next==null ? 1 : next.countElements()+1);
    }
    
    public String toString() {
    	activate(ActivationPurpose.READ);
        return car+" : "+time+" : "+description;
    }

    public void activate(ActivationPurpose purpose) {
        if(_activator != null) {
            _activator.activate(purpose);
        }
    }

    public void bind(Activator activator) {
    	if (_activator == activator) {
    		return;
    	}
    	if (activator != null && _activator != null) {
            throw new IllegalStateException();
        }
        _activator = activator;
    }

}