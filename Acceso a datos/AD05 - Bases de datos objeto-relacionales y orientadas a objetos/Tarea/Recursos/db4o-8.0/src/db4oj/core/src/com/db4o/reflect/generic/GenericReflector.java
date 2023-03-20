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
package com.db4o.reflect.generic;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.reflect.generic.*;
import com.db4o.reflect.*;

/**
 * db4o provides GenericReflector as a wrapper around specific 
 * reflector (delegate). GenericReflector is set when an 
 * ObjectContainer is opened. All subsequent reflector 
 * calls are routed through this interface.<br><br>
 * An instance of GenericReflector can be obtained through
 * {@link com.db4o.ext.ExtObjectContainer#reflector()}.<br><br>
 * GenericReflector keeps list of known classes in memory. 
 * When the GenericReflector is called, it first checks its list of 
 * known classes. If the class cannot be found, the task is 
 * transferred to the delegate reflector. If the delegate fails as 
 * well, generic objects are created, which hold simulated 
 * "field values" in an array of objects.<br><br>
 * Generic reflector makes possible the following usecases:<ul>
 * <li>running a db4o server without deploying application classes;</li>
 * <li>running db4o on Java dialects without reflection (J2ME CLDC, MIDP);</li>
 * <li>easier access to stored objects where classes or fields are not available;</li>
 * <li>running refactorings in the reflector;</li>
 * <li>building interfaces to db4o from any programming language.</li></ul>
 * <br><br>
 * One of the live usecases is ObjectManager, which uses GenericReflector 
 * to read C# objects from Java.
 */
public class GenericReflector implements Reflector, DeepClone {
	
	private KnownClassesRepository _repository;

	/* default delegate Reflector is the JdkReflector */
	private Reflector _delegate;
    private GenericArrayReflector _array;
    
    
    private Collection4 _collectionPredicates = new Collection4();

	// todo: Why have this when there is already the _repository by name? Redundant
	private final Hashtable4 _classByClass = new Hashtable4();

	private Transaction _trans;
	private ObjectContainerBase _stream;
	
	/**
	 * Creates an instance of GenericReflector
	 * @param trans transaction
	 * @param delegateReflector delegate reflector, 
	 * providing specific reflector functionality. For example 
	 */
	public GenericReflector(Transaction trans, Reflector delegateReflector){
		_repository=new KnownClassesRepository(new GenericClassBuilder(this,delegateReflector));
		setTransaction(trans);
		_delegate = delegateReflector;
        if(_delegate != null){
            _delegate.setParent(this);
        }
	}
	
	public GenericReflector(Reflector delegateReflector) {
		this(null, delegateReflector);
	}
	
	/**
	 * Creates a clone of provided object
	 * @param obj object to copy
	 * @return copy of the submitted object
	 */
	public Object deepClone(Object obj)  {
        GenericReflector myClone = new GenericReflector(null, (Reflector)_delegate.deepClone(this));
        myClone._collectionPredicates = (Collection4)_collectionPredicates.deepClone(myClone);
        
        // Interesting, adding the following messes things up.
        // Keep the code, since it may make sense to carry the
        // global reflectors into a running db4o session.
        
        
//        Iterator4 i = _classes.iterator();
//        while(i.hasNext()){
//            GenericClass clazz = (GenericClass)i.next();
//            clazz = (GenericClass)clazz.deepClone(myClone);
//            myClone._classByName.put(clazz.getName(), clazz);
//            myClone._classes.add(clazz);
//        }
        
		return myClone;
	}
	
	ObjectContainerBase getStream(){
		return _stream;
	}

	/**
	 * If there is a transaction assosiated with the current refector.
	 * @return true if there is a transaction assosiated with the current refector.
	 */
	public boolean hasTransaction(){
		return _trans != null;
	}
	
	/**
	 * Associated a transaction with the current reflector.
	 * @param trans
	 */
	public void setTransaction(Transaction trans){
		if(trans != null){
			_trans = trans;
			_stream = trans.container();
		}
		_repository.setTransaction(trans);
	}

	/**
	 * @return generic reflect array instance.
	 */
    public ReflectArray array() {
        if(_array == null){
            _array = new GenericArrayReflector(this);
        }
        return _array;
    }

    GenericClass ensureDelegate(ReflectClass clazz){
        if(clazz == null){
        	return null;
        }
        GenericClass claxx = (GenericClass)_repository.lookupByName(clazz.getName());
        if(claxx == null){
            //  We don't have to worry about the superclass, it can be null
            //  because handling is delegated anyway
			claxx = genericClass(clazz);
			_repository.register(claxx);
        }
        return claxx;
    }

	private GenericClass genericClass(ReflectClass clazz) {
		GenericClass ret;
		String name = clazz.getName();
		if(name.equals(ReflectPlatform.fullyQualifiedName(GenericArray.class))){ // special case, comparing name because can't compare class == class directly with ReflectClass
			ret = new GenericArrayClass(this, clazz, name, null);
		} else {
			ret = new GenericClass(this, clazz, name, null);
		}
		return ret;
	}

	/**
	 * Returns a ReflectClass instance for the specified class
	 * @param clazz class
	 * @return a ReflectClass instance for the specified class
	 * @see com.db4o.reflect.ReflectClass
	 */
	public ReflectClass forClass(Class clazz) {
        if(clazz == null){
            return null;
        }        
        ReflectClass claxx = (ReflectClass) _classByClass.get(clazz);
        if(claxx != null){
            return claxx;
        }
        if (!clazz.isArray() && ReflectPlatform.isNamedClass(clazz)) {
	        claxx = forName(ReflectPlatform.fullyQualifiedName(clazz));
	        if(claxx != null){
	            _classByClass.put(clazz, claxx);
	            return claxx;
	        }
        }
        claxx = _delegate.forClass(clazz);
        if(claxx == null){
            return null;
        }
        claxx = ensureDelegate(claxx);
        _classByClass.put(clazz, claxx);
        return claxx;
    }
    
	 /**
	  * Returns a ReflectClass instance for the specified class name
	 * @param className  class name
	 * @return a ReflectClass instance for the specified class name
	 * @see com.db4o.reflect.ReflectClass
	 */
    public ReflectClass forName(final String className) {
    	return withLock(new Closure4<ReflectClass>() {
			public ReflectClass run() {
		        ReflectClass clazz = _repository.lookupByName(className);
		        if(clazz != null){
		            return clazz;
		        }
		        clazz = _delegate.forName(className);
		        if(clazz != null){
		            return ensureDelegate(clazz);
		        }
		    	return _repository.forName(className);
			}
    	});
    }

    /**
	  * Returns a ReflectClass instance for the specified class object
	 * @param obj class object
	 * @return a ReflectClass instance for the specified class object
	 * @see com.db4o.reflect.ReflectClass
	 */
    public ReflectClass forObject(Object obj) {
        if (obj instanceof GenericObject){
			return forGenericObject((GenericObject)obj);
        }
        if (obj instanceof GenericArray){
			return ((GenericArray)obj)._clazz;
        }
        return _delegate.forObject(obj);
    }

	private ReflectClass forGenericObject(final GenericObject genericObject) {
		GenericClass claxx = genericObject._class;
		if(claxx == null){
			throw new IllegalStateException(); 
		}
		String name = claxx.getName();
		if(name == null){
			throw new IllegalStateException();
		}
		GenericClass existingClass = (GenericClass) forName(name);
		if(existingClass == null){
			_repository.register(claxx);
			return claxx;
		}
		// TODO: Using .equals() here would be more consistent with 
		//       the equals() method in GenericClass.
		if(existingClass != claxx){
			
			throw new IllegalStateException();
		}
		
		return claxx;
	}
    
	/**
	 * Returns delegate reflector
	 * @return delegate reflector
	 */
    public Reflector getDelegate(){
        return _delegate;
    }

    /**
     * Determines if a candidate ReflectClass is a collection
     * @param candidate candidate ReflectClass 
     * @return true  if a candidate ReflectClass is a collection.
     */
    public boolean isCollection(ReflectClass candidate) {
        //candidate = candidate.getDelegate(); 
        Iterator4 i = _collectionPredicates.iterator();
        while(i.moveNext()){
            if (((ReflectClassPredicate)i.current()).match(candidate)) {
            	return true;
            }
        }
        return _delegate.isCollection(candidate.getDelegate());
        
        //TODO: will need knowledge for .NET collections here
        // possibility: call registercollection with strings
    }

    /**
     * Register a class as a collection 
     * @param clazz class to be registered
     */
    public void registerCollection(Class clazz) {
		registerCollection(classPredicate(clazz));
    }

    /**
     * Register a predicate as a collection 
     * @param predicate predicate to be registered
     */
    	public void registerCollection(ReflectClassPredicate predicate) {
		_collectionPredicates.add(predicate);
	}

	private ReflectClassPredicate classPredicate(Class clazz) {
		final ReflectClass collectionClass = forClass(clazz);
		ReflectClassPredicate predicate = new ReflectClassPredicate() {
			public boolean match(ReflectClass candidate) {
	            return collectionClass.isAssignableFrom(candidate);
			}
		};
		return predicate;
	}
    
	/**
	 * Register a class
	 * @param clazz class
	 */
    public void register(final GenericClass clazz) {
    	withLock(new Closure4<Object>() {
			public Object run() {
		    	String name = clazz.getName();
		    	if(_repository.lookupByName(name) == null){
		    		_repository.register(clazz);
		    	}
		    	return null;
			}
    	});
    }
    
    /**
     * Returns an array of classes known to the reflector
     * @return an array of classes known to the reflector
     */
	public ReflectClass[] knownClasses() {
    	return withLock(new Closure4<ReflectClass[]>() {
			public ReflectClass[] run() {
	    		return new KnownClassesCollector(_stream, _repository).collect();	
			}
    	});
	}
	
	/**
	 * Registers primitive class
	 * @param id class id
	 * @param name class name
	 * @param converter class converter
	 */
	public void registerPrimitiveClass(final int id, final String name, final GenericConverter converter) {
    	withLock(new Closure4<Object>() {
			public Object run() {
		        GenericClass existing = (GenericClass)_repository.lookupByID(id);
				if (existing != null) {
					if (null != converter) {
//						existing.setSecondClass();
					} else {
						existing.setConverter(null);
					}
					return null;
				}
				ReflectClass clazz = _delegate.forName(name);
				
				GenericClass claxx = null;
				if(clazz != null) {
			        claxx = ensureDelegate(clazz);
				}else {
			        claxx = new GenericClass(GenericReflector.this, null, name, null);
			        register(claxx);
				    claxx.initFields(new GenericField[] {new GenericField(null, null, true)});
				    claxx.setConverter(converter);
				}
//			    claxx.setSecondClass();
			    claxx.setPrimitive();
			    _repository.register(id,claxx);
			    return null;

			}
    	});
	}

	/**
	 * method stub: generic reflector does not have a parent
	 */
    public void setParent(Reflector reflector) {
        // do nothing, the generic reflector does not have a parant
    }
    
	public void configuration(ReflectorConfiguration config) {
		if(_delegate != null) {
			_delegate.configuration(config);
		}
	}
	
	private <T> T withLock(Closure4<T> block) {
		if(_stream == null || _stream.isClosed()) {
			return block.run();
		}
		return _stream.syncExec(block);
	}
	
}
