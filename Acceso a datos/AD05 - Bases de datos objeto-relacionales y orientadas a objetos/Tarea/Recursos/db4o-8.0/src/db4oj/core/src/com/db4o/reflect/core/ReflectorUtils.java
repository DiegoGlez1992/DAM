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
package com.db4o.reflect.core;

import com.db4o.foundation.*;
import com.db4o.reflect.*;


/**
 * @exclude
 */
public class ReflectorUtils {
	
	public static ReflectClass reflectClassFor(Reflector reflector, Object clazz) {
        
       if(clazz instanceof ReflectClass){
            return (ReflectClass)clazz;
        }
        
        if(clazz instanceof Class){
            return reflector.forClass((Class)clazz);
        }
        
        if(clazz instanceof String){
            return reflector.forName((String)clazz);
        }
        
        return reflector.forObject(clazz);
    }
	
	public static ReflectField field(ReflectClass claxx, String name){
		while(claxx!=null) {
			try {
				return claxx.getDeclaredField(name);
			} catch (Exception e) {
				
			}
			claxx=claxx.getSuperclass();
		}
		return null;
	}
	
	public static void forEachField(ReflectClass claxx, Procedure4<ReflectField> procedure){
		while(claxx!=null) {
			final ReflectField[] declaredFields = claxx.getDeclaredFields();
			for (ReflectField reflectField : declaredFields) {
				procedure.apply(reflectField);
			}
			claxx=claxx.getSuperclass();
		}
	}

}
