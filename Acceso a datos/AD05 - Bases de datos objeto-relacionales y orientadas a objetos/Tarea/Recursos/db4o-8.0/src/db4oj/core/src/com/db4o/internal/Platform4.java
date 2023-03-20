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

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.handlers.array.*;
import com.db4o.internal.query.processor.*;
import com.db4o.query.*;
import com.db4o.reflect.*;
import com.db4o.reflect.generic.*;

/**
 * @exclude
 * @sharpen.ignore
 */
public final class Platform4 {
    
	static private TernaryBool collectionCheck=TernaryBool.UNSPECIFIED;

    static private JDK jdkWrapper;
    static private TernaryBool nioCheck=TernaryBool.UNSPECIFIED;

    static private TernaryBool setAccessibleCheck=TernaryBool.UNSPECIFIED;
    static private TernaryBool shutDownHookCheck=TernaryBool.UNSPECIFIED;
    static TernaryBool callConstructorCheck=TernaryBool.UNSPECIFIED;
    static ShutDownRunnable shutDownRunnable;

    static Thread shutDownThread;
    
    static final String ACCESSIBLEOBJECT = "java.lang.reflect.AccessibleObject";
    static final String GETCONSTRUCTOR = "newConstructorForSerialization";
    static final String REFERENCEQUEUE = "java.lang.ref.ReferenceQueue"; 
    static final String REFLECTIONFACTORY = "sun.reflect.ReflectionFactory";
    static final String RUNFINALIZERSONEXIT = "runFinalizersOnExit";
    
    static final String UTIL = "java.util.";
    static final String DB4O_PACKAGE = "com.db4o.";
    static final String DB4O_CONFIG = DB4O_PACKAGE + "config.";  
    
    // static private int cCreateNewFile;
    static private TernaryBool weakReferenceCheck=TernaryBool.UNSPECIFIED;
    
    private static final Class[] SIMPLE_CLASSES = {
		Integer.class,
		Long.class,
		Float.class,
		Boolean.class,
		Double.class,
		Byte.class,
		Character.class,
		Short.class,
		String.class,
		java.util.Date.class
	};
    
    synchronized static final void addShutDownHook(ObjectContainerBase container) {
        if (!hasShutDownHook()) {
        	return;
        }
        
        if (shutDownThread == null) {
            shutDownRunnable = new ShutDownRunnable();
            shutDownThread = jdk().addShutdownHook(shutDownRunnable);
        }
        shutDownRunnable.ensure(container);
    }

    public static final boolean canSetAccessible() {
        if (setAccessibleCheck.isUnspecified()) {
            if (jdk().ver() >= 2) {
                setAccessibleCheck = TernaryBool.YES;
            } else {
                setAccessibleCheck = TernaryBool.NO;
                if (((Config4Impl)Db4o.configure()).messageLevel() >= 0) {
                    Messages.logErr(Db4o.configure(), 47, null, null);
                }
            }
        }
        return setAccessibleCheck.definiteYes();
    }
    
    /**
     * use for system classes only, since not ClassLoader
     * or Reflector-aware
     */
    static final boolean classIsAvailable(String className) {
    	return ReflectPlatform.forName(className) != null;
    }
    
    static final Reflector createReflector(Object classLoader){
        return jdk().createReflector(classLoader);
    }

    public static final Object createReferenceQueue() {
        return jdk().createReferenceQueue();
    }
    
    public static Object createWeakReference(Object obj){
        return jdk().createWeakReference(obj);
    }

    public static final Object createActiveObjectReference(Object referenceQueue, Object objectReference, Object obj) {
        return jdk().createActivateObjectReference(referenceQueue, (ObjectReference) objectReference, obj);
    }
    
    public static Object deserialize(byte[] bytes) {
    	return jdk().deserialize(bytes);
    }
    
    public static final long doubleToLong(double a_double) {
        return Double.doubleToLongBits(a_double);
    }

    public static final QConEvaluation evaluationCreate(Transaction a_trans, Object example){
        if(example instanceof Evaluation){
            return new QConEvaluation(a_trans, example);
        }
        return null;
    }
    
    public static final void evaluationEvaluate(Object a_evaluation, Candidate a_candidate){
        ((Evaluation)a_evaluation).evaluate(a_candidate);
    }

    /** may be needed for YapConfig processID() at a later date */
    /*
    static boolean createNewFile(File file) throws IOException{
    	return file.createNewFile();
    }
    */
	
	public static Object[] collectionToArray(ObjectContainerBase stream, Object obj){
		Collection4 col = flattenCollection(stream, obj);
		Object[] ret = new Object[col.size()];
		col.toArray(ret);
		return ret;
	}

    static final Collection4 flattenCollection(ObjectContainerBase stream, Object obj) {
        Collection4 col = new Collection4();
        flattenCollection1(stream, obj, col);
        return col;
    }

    /**
     * Should create additional configuration, for example through reflection
     * on annotations.
     * 
     * - If a valid configuration is passed as classConfig, any additional
     *   configuration, if available, should be applied to this object, and
     *   this object should be returned.
     * - If classConfig is null and there is no additional configuration,
     *   null should be returned.
     * - If classConfig is null and there is additional configuration, this code
     *   should create and register a new configuration via config.objectClass(),
     *   apply additional configuration there and return this new instance.
     * 
     * The reason for this dispatch is to avoid creation of a configuration
     * for a class that doesn't need configuration at all.
     * 
     * @param clazz The class to be searched for additional configuration information
     * @param config The global database configuration
     * @param classConfig A class configuration, if one already exists
     * @return classConfig, if not null, a newly created ObjectClass otherwise.
     */
    public static Config4Class extendConfiguration(ReflectClass clazz,Configuration config,Config4Class classConfig) {
    	return jdk().extendConfiguration(clazz, config, classConfig);
    }
    
    static final void flattenCollection1(ObjectContainerBase stream, Object obj, Collection4 col) {
        if (obj == null) {
            col.add(null);
        } else {
            ReflectClass claxx = stream.reflector().forObject(obj);
            if (claxx.isArray()) {
                Iterator4 objects = ArrayHandler.iterator(claxx, obj);
                while (objects.moveNext()) {
                    flattenCollection1(stream, objects.current(), col);
                }
            } else {
                flattenCollection2(stream, obj, col);
            }
        }
    }

    static final void flattenCollection2(final ObjectContainerBase container, Object obj, final Collection4 col) {
        if (container.reflector().forObject(obj).isCollection()) {
            forEachCollectionElement(obj, new Visitor4() {
                public void visit(Object element) {
                    flattenCollection1(container, element, col);
                }
            });
        } else {
            col.add(obj);
        }
    }

    public static final void forEachCollectionElement(Object obj, Visitor4 visitor) {
        jdk().forEachCollectionElement(obj, visitor);
    }

    public static final String format(Date date, boolean showTime) {
    	return jdk().format(date, showTime);
    }

    public static final void getDefaultConfiguration(Config4Impl config) {
		
    	// Initialize all JDK stuff first, before doing ClassLoader stuff
    	jdk();
    	hasWeakReferences();
    	hasNio();
    	hasCollections();
    	hasShutDownHook();
        
    	if(config.reflector()==null) {
    		config.reflectWith(jdk().createReflector(null));
    	}
    	
        configStringBufferCompare(config);
        
        translate(config.objectClass("java.lang.Class"), "TClass");
        translateCollection(config, "Hashtable", "THashtable", false);
        if (jdk().ver() >= 2) {
			try {
				translateCollection(config, "AbstractCollection", "TCollection", false);
				translateUtilNull(config, "AbstractList");
				translateUtilNull(config, "AbstractSequentialList");
				translateUtilNull(config, "LinkedList");
				translateUtilNull(config, "ArrayList");
				translateUtilNull(config, "Vector");
				translateUtilNull(config, "Stack");
				translateUtilNull(config, "AbstractSet");
				translateUtilNull(config, "HashSet");
				translate(config, UTIL + "TreeSet", "TTreeSet");
				translateCollection(config, "AbstractMap", "TMap", false);
				translateUtilNull(config, "HashMap");
				translateUtilNull(config, "WeakHashMap");
				translate(config, UTIL + "TreeMap", "TTreeMap");
			} catch (Exception e) {
			}
        } else {
			translateCollection(config, "Vector", "TVector", false);
        }
        
        config.objectClass(ActivatableBase.class).indexed(false);
        
        jdk().commonConfigurations(config);
        jdk().extendConfiguration(config);
    }
    

    /**
     * @deprecated uses deprecated API
     */
	private static void configStringBufferCompare(Config4Impl config) {
		config.objectClass("java.lang.StringBuffer").compare(new ObjectAttribute() {
            public Object attribute(Object original) {
                if (original instanceof StringBuffer) {
                    return ((StringBuffer) original).toString();
                }
                return original;
            }
        });
	}
    
    public static Object getTypeForClass(Object obj){
        return obj;
    }

    static final Object getYapRefObject(Object a_object) {
        return jdk().getYapRefObject(a_object);
    }

    static final synchronized boolean hasCollections() {
        if (collectionCheck.isUnspecified()) {
        	if (classIsAvailable(UTIL + "Collection")) {
        		collectionCheck = TernaryBool.YES;
        		return true;
        	}
            collectionCheck = TernaryBool.NO;
        }
        return collectionCheck.definiteYes();
    }
    
    public static boolean needsLockFileThread(){
    	return ! hasNio();
    }

    private static final boolean hasNio() {
        if (!Debug4.nio) {
            return false;
        }
        if (nioCheck.isUnspecified()) {
            if ((jdk().ver() >= 4)
                && (!noNIO())) {
                nioCheck = TernaryBool.YES;
                return true;
            }
            nioCheck = TernaryBool.NO;
        }
        return nioCheck.definiteYes();

    }

    static final boolean hasShutDownHook() {
        if (shutDownHookCheck.isUnspecified()) {            
            if (jdk().ver() >= 3){
                shutDownHookCheck = TernaryBool.YES;
                return true;
            } 
            Reflection4.invoke(System.class, RUNFINALIZERSONEXIT, new Class[] {boolean.class}, new Object[]{new Boolean(true)});
            shutDownHookCheck = TernaryBool.NO;
        }
        return shutDownHookCheck.definiteYes();
    }

    public static final boolean hasWeakReferences() {
        if (!Debug4.weakReferences) {
            return false;
        }
        if (weakReferenceCheck.isUnspecified()) {
            if (classIsAvailable(ACCESSIBLEOBJECT)
                && classIsAvailable(REFERENCEQUEUE)
                && jdk().ver() >= 2) {
                weakReferenceCheck = TernaryBool.YES;
                return true;
            }
            weakReferenceCheck = TernaryBool.NO;
        }
        return weakReferenceCheck.definiteYes();
    }
    
    /** @param obj */
    static final boolean ignoreAsConstraint(Object obj){
        return false;
    }

    static final boolean isCollectionTranslator(Config4Class a_config) {
        return jdk().isCollectionTranslator(a_config); 
    }
    
    public static boolean isConnected(Socket socket) {
        return jdk().isConnected(socket);
    }   
    
    /**
     * Returns true if claxx represents a .net struct (a value type with
     * members in the type handler jargon).
     * 
     * @param claxx
     **/
    public static final boolean isStruct(ReflectClass claxx){
    	return false;
    }
    
    public static JDK jdk() {
        if (jdkWrapper == null) {
            createJdk();
        }
        return jdkWrapper;
    }
    
    
	public static class JDKFactoryInstantiationException extends RuntimeException {

		public JDKFactoryInstantiationException(Throwable cause) {
			super(JDKFactory.class.getName() + " instances must have a public default constructor and be accessible from "
					+ Platform4.class.getName() + " ("+cause.getMessage()+")");
		}

	}
    
    private static void createJdk() {
    	
    	
    	Class<?>[] jdkFactories = {
    			DalvikVM.Factory.class,
    			JDK_5.Factory.class,
    			JDK_1_4.Factory.class,
    			JDK_1_3.Factory.class,
    			JDK_1_2.Factory.class,
    			JDKReflect.Factory.class,
    	};
    	
    	for (Class<?> clazz : jdkFactories) {
			try {
				
				jdkWrapper = ((JDKFactory) clazz.newInstance()).tryToCreate();
				if (jdkWrapper != null) {
					break;
				}
				
			} catch (SecurityException e) {
				throw new JDKFactoryInstantiationException(e);
			} catch (IllegalAccessException e) {
				throw new JDKFactoryInstantiationException(e);
			} catch (InstantiationException e) {
				throw new JDKFactoryInstantiationException(e);
			}
		}

        
    }
    
	public static boolean isSimple(Class clazz){
		for (int i = 0; i < SIMPLE_CLASSES.length; i++) {
			if(clazz == SIMPLE_CLASSES[i]){
				return true;
			}
		}
		return false;
	}
    
    static final void killYapRef(Object a_object){
    	jdk().killYapRef(a_object);
    }
    
    public static void link(){
        // link standard translators, so they won't get deleted
        // by deployment
        
        new TClass();
        new TVector();
        new THashtable();
        new TNull();
    }

    public static final void lockFile(String path,Object file) {
        if (!hasNio()) {
            return;
        }
        jdk().lockFile(path,file);
    }
    
    public static final void unlockFile(String path,Object file) {
        if (hasNio()) {
            jdk().unlockFile(path,file);
        }
    }

    public static final double longToDouble(long a_long) {
        return Double.longBitsToDouble(a_long);
    }

    /** @param marker */
    static void markTransient(String marker) {
        // do nothing
    }

    public static boolean callConstructor() {
        if (callConstructorCheck.isUnspecified()) {
            
            if(jdk().supportSkipConstructorCall()){
                callConstructorCheck = TernaryBool.NO;
                return false;
            }
            callConstructorCheck = TernaryBool.YES;
        }
        return callConstructorCheck.definiteYes();
    }    

    private static final boolean noNIO() {
        try {
            if (propertyIs("java.vendor", "Sun")
                && propertyIs("java.version", "1.4.0")
                && (propertyIs("os.name", "Linux")
                    || propertyIs("os.name", "Windows 95")
                    || propertyIs("os.name", "Windows 98"))) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public static final void pollReferenceQueue(Object a_stream, Object a_referenceQueue) {
        jdk().pollReferenceQueue((ObjectContainerBase) a_stream, a_referenceQueue);
    }

    private static final boolean propertyIs(String propertyName, String propertyValue) {
        String property = System.getProperty(propertyName);
        return (property != null) && (property.indexOf(propertyValue) == 0);
    }
    
	public static void registerCollections(GenericReflector reflector) {
		jdk().registerCollections(reflector);
	}	

    synchronized static final void removeShutDownHook(ObjectContainerBase container) {
        if (!hasShutDownHook() || shutDownRunnable == null) {
        	return;
        }
        
        shutDownRunnable.remove(container);
        if (shutDownRunnable.size() == 0) {
            if (!shutDownRunnable.dontRemove) {
                try {
                    jdk().removeShutdownHook(shutDownThread);
                } catch (Exception e) {
                    // this is safer than attempting perfect
                    // synchronisation
                }
            }
            shutDownThread = null;
            shutDownRunnable = null;
        }
    }
    
    public static final byte[] serialize(Object obj) throws Exception{
    	return jdk().serialize(obj);
    }

    public static final void setAccessible(Object a_accessible) {
        if (setAccessibleCheck == TernaryBool.UNSPECIFIED) {
            canSetAccessible();
        }
        if (setAccessibleCheck == TernaryBool.YES) {
            jdk().setAccessible(a_accessible);
        }
    }
    
    public static boolean storeStaticFieldValues(Reflector reflector, ReflectClass claxx) {
        return isEnum(reflector, claxx);
    }

	public static boolean isEnum(Reflector reflector, ReflectClass claxx) {
		return jdk().isEnum(reflector, claxx);
	}
	
	public static boolean isJavaEnum(GenericReflector reflector, ReflectClass classReflector) {
		return isEnum(reflector, classReflector);
	}	

    private static final void translate(ObjectClass oc, String to) {
        ((Config4Class)oc).translateOnDemand(DB4O_CONFIG + to);
    }

    private static final void translate(Config4Impl config, String from, String to) {
        translate(config.objectClass(from), to);
    }

    private static final void translateCollection(
        Config4Impl config,
        String from,
        String to,
        boolean cascadeOnDelete) {
        ObjectClass oc = config.objectClass(UTIL + from);
        
        // FIXME: Maybe we don't need any special
        //        configuration here. Or should this be 2
        //        after the Typehandler changes ?
        oc.updateDepth(3);
        
        if (cascadeOnDelete) {
            oc.cascadeOnDelete(true);
        }
        translate(oc, to);
    }

    private static final void translateUtilNull(Config4Impl config, String className) {
        translate(config, UTIL + className, "TNull");
    }

    static final NetTypeHandler[] types(Reflector reflector) {
        return jdk().types(reflector);
    }
    
    public static byte[] updateClassName(byte[] bytes) {
        // needed for .NET only: update assembly names if necessary
        return bytes;
    }
    
    public static Object weakReferenceTarget(Object weakRef){
        return jdk().weakReferenceTarget(weakRef);
    }

	public static Object wrapEvaluation(Object evaluation) {
		return evaluation;
	}

    /** @param claxx */
    public static boolean isTransient(ReflectClass claxx) {
        return false;
    }
    
	public static Reflector reflectorForType(Class clazz) {
		return jdk().reflectorForType(clazz);
	}
	
	public static Date now(){
		return new Date();
	}

	public static boolean useNativeSerialization() {
		return jdk().useNativeSerialization();
	}

    public static void registerPlatformHandlers(ObjectContainerBase container) {
        container.handlers().treatAsOpenType(java.lang.Number.class);
    }

    public static Class nullableTypeFor(Class primitiveJavaClass) {
    	if(_primitive2Wrapper == null)
    		initPrimitive2Wrapper();
    	Class wrapperClazz = (Class)_primitive2Wrapper.get(primitiveJavaClass);
    	if(wrapperClazz==null)        
    		throw new NotImplementedException("No nullableTypeFor : " + primitiveJavaClass.getName());
    	return wrapperClazz;
    }
    
    private static void initPrimitive2Wrapper(){
    	_primitive2Wrapper = new Hashtable4();
    	_primitive2Wrapper.put(int.class, Integer.class);
    	_primitive2Wrapper.put(byte.class, Byte.class);
    	_primitive2Wrapper.put(short.class, Short.class);
    	_primitive2Wrapper.put(float.class, Float.class);
    	_primitive2Wrapper.put(double.class, Double.class);
    	_primitive2Wrapper.put(long.class, Long.class);
    	_primitive2Wrapper.put(boolean.class, Boolean.class);
    	_primitive2Wrapper.put(char.class, Character.class);
    	
    }
	
    private static Hashtable4 _primitive2Wrapper;
    
    public static Object nullValue(Class clazz)
    {
    	if(_nullValues == null) {
    		initNullValues();
    	}
    	return _nullValues.get(clazz);
    	
    }
    
    private static void initNullValues() {
    	_nullValues = new Hashtable4();
    	_nullValues.put(boolean.class, Boolean.FALSE);
    	_nullValues.put(byte.class, new Byte((byte)0));
    	_nullValues.put(short.class, new Short((short)0));
    	_nullValues.put(char.class, new Character((char)0));
    	_nullValues.put(int.class, new Integer(0));
    	_nullValues.put(float.class, new Float(0.0));
    	_nullValues.put(long.class, new Long(0));
    	_nullValues.put(double.class, new Double(0.0));
	}

	private static Hashtable4 _nullValues;

	public static Class[] primitiveTypes() {
		return new Class[] 
		                 {
							boolean.class,
							byte.class,
							short.class,
							char.class,
							int.class,
							long.class,
							float.class,
							double.class,
						};		
	}

	public static void throwUncheckedException(Throwable origExc) {
		if(origExc instanceof RuntimeException){
			throw (RuntimeException)origExc;
		}
		if(origExc instanceof Error){
			throw (Error)origExc;
		}
		jdk().throwIllegalArgumentException(origExc);
	}
	
	public static final byte toSByte(byte b){
		return b;
	}	
}