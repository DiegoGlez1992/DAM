package com.db4odoc.f1.chapter7;

public class NotStorable {
  private int id;
  private String name;
  private transient int length;

  public NotStorable(int id,String name) {
    this.id=id;
    this.name=name;
    this.length=name.length();
  }

  public int getId() {
    return id;
  }
  
  public String getName() {
    return name;
  }  

  public int getLength() {
    return length;
  }

  public String toString() {
    return id+"/"+name+": "+length;
  }
}