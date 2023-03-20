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
package com.db4o.reflect.jdk;

import com.db4o.internal.*;
import com.db4o.reflect.*;

/**
 * db4o wrapper for JDK reflector functionality
 * @see com.db4o.ext.ExtObjectContainer#reflector()
 * @see com.db4o.reflect.generic.GenericReflector
 * 
 * @sharpen.ignore
 */
public class JdkReflector implements Reflector {
	
	private final JdkLoader _classLoader;
    protected Reflector _parent;
    private ReflectArray _array;
	private ReflectorConfiguration _config;
	
	
    
    /**
     * Constructor
     * @param classLoader class loader
     */
	public JdkReflector(ClassLoader classLoader){
		this(new ClassLoaderJdkLoader(classLoader));
	}
	
	/**
     * Constructor
     * @param classLoader class loader
     */
	public JdkReflector(JdkLoader classLoader){
		this(classLoader, defaultConfiguration());
	}
	
	private JdkReflector(JdkLoader classLoader, ReflectorConfiguration config){
		_classLoader = classLoader;
		_config = config;
	}

	private static ReflectorConfiguration defaultConfiguration() {
		return new ReflectorConfiguration() {
			public boolean testConstructors() {
				return false;
			}
			public boolean callConstructor(ReflectClass clazz) {
				return false;
			}
		};
	}
	
	/**
	 * ReflectArray factory
	 * @return ReflectArray instance
	 */
	public ReflectArray array(){
        if(_array == null){
            _array = new JdkArray(parent());
        }
		return _array;
	}
	
	/**
	 * Creates a copy of the object
	 * @param obj object to copy
	 * @return object copy
	 */
    public Object deepClone(Object obj) {
        return new JdkReflector(_classLoader, _config);
    }
	
    /**
     * Returns ReflectClass for the specified class
     * @param clazz class 
     * @return ReflectClass for the specified class
     */
	public ReflectClass forClass(Class clazz){
        return createClass(clazz);
	}
	
	/**
     * Returns ReflectClass for the specified class name
     * @param className class name
     * @return ReflectClass for the specified class name
     */
	public ReflectClass forName(String className) {
		Class clazz = _classLoader.loadClass(className);
		if (clazz == null) {
			return null;
		}
		return createClass(clazz);
	}

	/**
	 * creates a Class reflector when passed a class.
	 * This method is protected to allow overriding in 
	 * cusom reflectors that override JdkReflector. 
	 * @param clazz the class
	 * @return the class reflector
	 */
	protected JdkClass createClass(Class clazz) {
		if(clazz == null) {
			return null;
		}
		return new JdkClass(parent(), this, clazz);
	}
	
	/**
     * Returns ReflectClass for the specified class object
     * @param a_object class object
     * @return ReflectClass for the specified class object
     */
	public ReflectClass forObject(Object a_object) {
		if(a_object == null){
			return null;
		}
		return parent().forClass(a_object.getClass());
	}
	
	/**
	 * Method stub. Returns false.
	 */
	public boolean isCollection(ReflectClass candidate) {
		return false;
	}

	/**
	 * Method stub. Returns false.
	 */
	public boolean methodCallsSupported(){
		return true;
	}

	/**
	 * Sets parent reflector
	 * @param reflector parent reflector
	 */
    public void setParent(Reflector reflector) {
        _parent = reflector;
    }

    /**
     * Creates ReflectClass[] array from the Class[]
     * array using the reflector specified 
     * @param reflector reflector to use
     * @param clazz class
     * @return ReflectClass[] array 
     */
    public static ReflectClass[] toMeta(Reflector reflector, Class[] clazz){
        ReflectClass[] claxx = null;
        if(clazz != null){
            claxx = new ReflectClass[clazz.length];
            for (int i = 0; i < clazz.length; i++) {
                if(clazz[i] != null){
                    claxx[i] = reflector.forClass(clazz[i]);
                }
            }
        }
        return claxx;
    }
    
    
    /**
     * Creates Class[] array from the ReflectClass[]
     * array  
     * @param claxx ReflectClass array
     * @return Class[] array 
     */
    static Class[] toNative(ReflectClass[] claxx){
        Class[] clazz = null;
        if(claxx != null){
            clazz = new Class[claxx.length];
            for (int i = 0; i < claxx.length; i++) {
                clazz[i] = toNative(claxx[i]);
            }
        }
        return clazz;
    }
 
    /**
     * Translates a ReflectClass into a native Class
     * @param claxx ReflectClass to translate
     * @return Class 
     */
    public static Class toNative(ReflectClass claxx){
        if(claxx == null){
            return null;
        }
        if(claxx instanceof JavaReflectClass){
            return ((JavaReflectClass)claxx).getJavaClass();
        }
        ReflectClass d = claxx.getDelegate();
        if(d == claxx){
            return null;
        }
        return toNative(d);
    }

	public void configuration(ReflectorConfiguration config) {
		_config = config;
	}
	
	public ReflectorConfiguration configuration(){
		return _config;
	}
	
	Object nullValue(ReflectClass clazz) {
		return Platform4.nullValue(toNative(clazz));
	}
	
	protected Reflector parent() {
		if(_parent == null){
			return this;
		}
		return _parent; 
	} 
	
}
