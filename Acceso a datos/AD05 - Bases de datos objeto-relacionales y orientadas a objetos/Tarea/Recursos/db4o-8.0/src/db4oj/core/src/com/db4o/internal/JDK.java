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

import java.net.*;
import java.util.*;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.handlers.*;
import com.db4o.reflect.*;
import com.db4o.reflect.core.*;
import com.db4o.reflect.generic.*;

/**
 * @exclude
 * @sharpen.ignore
 */
public class JDK {
	
	Thread addShutdownHook(Runnable runnable){
		return null;
	}
    
	/**
	 * always call super if you override
	 */
	public void commonConfigurations(Config4Impl config) {
		
	}
	
    Class constructorClass(){
        return null;
    }
	
	Object createReferenceQueue() {
		return null;
	}

    public Object createWeakReference(Object obj){
        return obj;
    }
    
	Object createActivateObjectReference(Object queue, ObjectReference ref, Object obj) {
		return null;
	}
	
	Object deserialize(byte[] bytes) {
    	throw new Db4oException(Messages.NOT_IMPLEMENTED);
    }
	
    public void extendConfiguration(Config4Impl config) {
        new TypeHandlerConfigurationJDK_1_1(config).apply();
    }

    public Config4Class extendConfiguration(ReflectClass clazz, Configuration config, Config4Class classConfig) {
    	return classConfig;
    }

    void forEachCollectionElement(Object obj, Visitor4 visitor) {
        Enumeration e = null;
        if (obj instanceof Hashtable) {
            e = ((Hashtable)obj).elements();
        } else if (obj instanceof Vector) {
            e = ((Vector)obj).elements();
        }
        if (e != null) {
            while (e.hasMoreElements()) {
                visitor.visit(e.nextElement());
            }
        }
	}
	
    String format(Date date, boolean showTime) {
		return date.toString();
	}
	
	Object getContextClassLoader(){
		return null;
	}

	Object getYapRefObject(Object obj) {
		return null;
	}
    
    boolean isCollectionTranslator(Config4Class config) {
        if (config != null) {
            ObjectTranslator ot = config.getTranslator();
            if (ot != null) {
                return ot instanceof THashtable;
            }
        }
        return false;
    }
    
   public boolean isConnected(Socket socket){
       return socket != null;
   }

	public int ver(){
	    return 1;
	}
	
	void killYapRef(Object obj){
		
	}
	
	public Class loadClass(String className, Object classLoader) throws ClassNotFoundException {
	    // We can't use the ClassLoader here since JDK get's converted to .NET
	    // Functionality is overridden in JDKReflect 
		return Class.forName(className);
	}
	
	synchronized void lockFile(String path,Object file){
	}
	
	/**
     * use for system classes only, since not ClassLoader
     * or Reflector-aware
	 */
	boolean methodIsAvailable(String className, String methodName, Class[] params) {
    	return false;
    }
	
	boolean supportSkipConstructorCall() {
		return false;
	}

	public long nanoTime() {
		throw new NotImplementedException();
	}
	
	void pollReferenceQueue(ObjectContainerBase session, Object referenceQueue) {
		
	}
	
	public void registerCollections(GenericReflector reflector) {
		
	}
	
	void removeShutdownHook(Thread thread){
		
	}
	
	public ReflectConstructor serializableConstructor(Reflector reflector, Class clazz){
	    return null;
	}
	
	byte[] serialize(Object obj) throws Exception{
    	throw new Db4oException(Messages.NOT_IMPLEMENTED);
    }

	void setAccessible(Object accessibleObject) {
	}
    
    boolean isEnum(Reflector reflector, ReflectClass clazz) {
        return false;
    }
	
    synchronized void unlockFile(String path,Object file) {
	}
    
    public Object weakReferenceTarget(Object weakRef){
        return weakRef;
    }
    
    public Reflector createReflector(Object classLoader) {
    	return null;
    }

    public Reflector reflectorForType(Class clazz) {
		return null;
	}

    public NetTypeHandler[] types(Reflector reflector) {
        return new NetTypeHandler[]{};
    }

    public NetTypeHandler[] netTypes(Reflector reflector) {
        return new NetTypeHandler[]{};
    }

	public boolean useNativeSerialization() {
		return true;
	}

	public void throwIllegalArgumentException(Throwable origExc) {
		throw new IllegalArgumentException("Argument " + origExc.getClass().getName() + " not an unchecked Exception.");
	}

	protected static final boolean classIsAvailable(String className) {
    	return ReflectPlatform.forName(className) != null;
    }
	
	public String generateSignature() {
		return JdkSignatureGenerator.generateSignature();
	}


}
