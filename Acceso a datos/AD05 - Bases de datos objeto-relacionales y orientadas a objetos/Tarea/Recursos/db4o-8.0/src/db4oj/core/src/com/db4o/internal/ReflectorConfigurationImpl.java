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
package com.db4o.internal;

import com.db4o.foundation.*;
import com.db4o.reflect.*;

public class ReflectorConfigurationImpl implements ReflectorConfiguration {

	private Config4Impl _config;
	
	public ReflectorConfigurationImpl(Config4Impl config) {
		_config = config;
	}
	
	public boolean testConstructors() {
		return _config.testConstructors();
	}
	
	public boolean callConstructor(ReflectClass clazz) {
        TernaryBool specialized = callConstructorSpecialized(clazz);
		if(!specialized.isUnspecified()){
		    return specialized.definiteYes();
		}
		return _config.callConstructors().definiteYes();
    }
    
    private final TernaryBool callConstructorSpecialized(ReflectClass clazz){
    	Config4Class clazzConfig = _config.configClass(clazz.getName());
        if(clazzConfig!= null){
            TernaryBool res = clazzConfig.callConstructor();
            if(!res.isUnspecified()){
                return res;
            }
        }
        if(Platform4.isEnum(_config.reflector(), clazz)){
            return TernaryBool.NO;
        }
        ReflectClass ancestor = clazz.getSuperclass();
		if(ancestor != null){
            return callConstructorSpecialized(ancestor);
        }
        return TernaryBool.UNSPECIFIED;
    }

}
