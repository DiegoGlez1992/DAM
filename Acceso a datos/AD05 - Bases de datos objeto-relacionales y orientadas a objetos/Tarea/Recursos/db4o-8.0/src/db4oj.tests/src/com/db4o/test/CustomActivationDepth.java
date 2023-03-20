/* This file is part of the db4o object database http://www.db4o.com

Copyright (C) 2004 - 2011  Versant Corporation http://www.versant.com

db4o is free software; you can redistribute it and/or modify it under
the terms of version 3 of the GNU General Public License as published
by the Free Software Foundation.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program.  If not, see http://www.gnu.org/licenses/. */
package com.db4o.test;

import com.db4o.*;

/**
 * 
 */
public class CustomActivationDepth {
    
    int myInt;
    String myString;
    int[] ints;
    String[] strings;
    
    CA1 ca1;
    CA2 ca2;
    CA3 ca3;
    
    CA1[] ca1s;
    CA2[] ca2s;
    CA3[] ca3s;
    
    
    public void configure(){
        Db4o.configure().objectClass(CA1.class).maximumActivationDepth(1);
    }
    
    
    public void storeOne(){
        myInt = 7;
        myString = "seven";
        ints = new int[]{77};
        strings = new String[]{"sevenseven"};
        ca1 = new CA1("1");
        ca2 = new CA2("2");
        ca3 = new CA3("3");
        
        ca1s = new CA1[] {new CA1("1arr1"), new CA1("1arr2")}; 
        ca2s = new CA2[] {new CA2("2arr1"), new CA2("2arr2")}; 
        ca3s = new CA3[] {new CA3("3arr1"), new CA3("3arr2")}; 
        
        Db4o.configure().activationDepth(0);
    }
    
    public void testOne(){
        Test.objectContainer().activate(this, 10);
        
        Test.objectContainer().activate(this.ca1, 10);
        
        Db4o.configure().activationDepth(5);
    }
    
    public static class CA1{
    	
    	public String name;
    	
    	public CA2 ca2;
    	
    	public CA1(){
    		
    	}
    	
    	public CA1(String name){
    		this.name = name;
    		ca2 = new CA2(name + ".2");
    	}
    	
    	
    }
    
    public static class CA2{
    	
    	public String name;
    	
    	public CA3 ca3;
    	
    	public CA2(){
    		
    	}
    	
    	public CA2(String name){
    		this.name = name;
    		ca3 = new CA3(name + ".3");
    	}
    
    }
    
    
    public static class CA3{
    	
    	public String name;
    	
    	public CA3(){
    		
    	}
    	
    	public CA3(String name){
    		this.name = name;
    	}
    
    }

    
    
    

}
