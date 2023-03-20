package com.db4odoc.f1.chapter8;

import com.db4o.activation.*;
import com.db4o.ta.*;

public class Pilot implements Activatable{
    private String name;
    private int points;
    private transient Activator _activator;

    public Pilot(String name,int points) {
        this.name=name;
        this.points=points;
    }

    public int getPoints() {
    	activate(ActivationPurpose.READ);
        return points;
    }

    public void addPoints(int points) {
    	activate(ActivationPurpose.WRITE);
        this.points+=points;
    }

    public String getName() {
    	activate(ActivationPurpose.READ);
        return name;
    }

    public String toString() {
    	activate(ActivationPurpose.READ);
        return name+"/"+points;
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