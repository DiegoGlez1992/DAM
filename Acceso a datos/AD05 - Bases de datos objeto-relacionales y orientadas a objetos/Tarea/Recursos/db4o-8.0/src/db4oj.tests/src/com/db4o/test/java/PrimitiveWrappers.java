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
package com.db4o.test.java;

import java.lang.reflect.*;

import com.db4o.test.*;


public class PrimitiveWrappers {
    
    public Boolean boolNull;
    public Boolean boolMin;
    public Boolean boolMax;
    
    public Byte bNull;
    public Byte bMin;
    public Byte bMax;
    
    public Character cNull;
    public Character cMin;
    public Character cMax;
    
    public Double dNull;
    public Double dMin;
    public Double dMax;
    
    public Float fNull;
    public Float fMin;
    public Float fMax;
    
    public Integer iNull;
    public Integer iMin;
    public Integer iMax;
    
    public Long lNull;
    public Long lMin;
    public Long lMax;
    
    public Short sNull;
    public Short sMin;
    public Short sMax;
    
    
    public void storeOne(){
        
        boolMin = new Boolean(false);
        boolMax = new Boolean(true);
        
        bMin = new Byte(Byte.MAX_VALUE);
        bMax = new Byte(Byte.MAX_VALUE);
        
        cMin = new Character(Character.MIN_VALUE);
        cMax = new Character(Character.MAX_VALUE);
        
        dMin = new Double(Double.MIN_VALUE);
        dMax = new Double(Double.MAX_VALUE);
        
        fMin = new Float(Float.MIN_VALUE);
        fMax = new Float(Float.MAX_VALUE);
        
        iMin = new Integer(Integer.MIN_VALUE);
        iMax = new Integer(Integer.MAX_VALUE);
        
        lMin = new Long(Long.MIN_VALUE);
        lMax = new Long(Long.MAX_VALUE);
        
        sMin = new Short(Short.MIN_VALUE);
        sMax = new Short(Short.MAX_VALUE);
        
    }
    
    public void testOne(){
        PrimitiveWrappers original = new PrimitiveWrappers();
        original.storeOne();
        Test.ensure(this.equals(original));
    }
    
    public boolean equals(Object obj) {
        if(! (obj instanceof PrimitiveWrappers)){
            return false;
        }
        try {
            Class clazz = getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                Object myMember = field.get(this);
                Object otherMember = field.get(obj);
                if(myMember == null){
                    if(otherMember != null){
                        return false;
                    }
                }else{
                    if(! myMember.equals(otherMember)){
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return true;
    }

}
