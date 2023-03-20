package com.db4odoc.f1.chapter2;

public class Car {   
    private String model;
    private Pilot pilot;
    
    public Car(String model) {
        this.model=model;
        this.pilot=null;
    }
      
    public Pilot getPilot() {
        return pilot;
    }
    
    public void setPilot(Pilot pilot) {
        this.pilot = pilot;
    }
    
    public String getModel() {
        return model;
    }
    
    public String toString() {
        return model+"["+pilot+"]";
    }
}
