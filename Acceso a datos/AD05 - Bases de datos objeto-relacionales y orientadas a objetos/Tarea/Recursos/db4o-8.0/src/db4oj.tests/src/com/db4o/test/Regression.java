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

import java.lang.reflect.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.test.types.*;

/**
 * The original old regression test, without any S.O.D.A. functionality. 
 * Tests will be slowly migrated to AllTests.java.
 */
public class Regression {

	/**
	 * uses a small subset of classes
	 * see method testClasses at the end of this file
	 */
	private static final boolean DEBUG = false;

	/** no comparisons, used to time performance only */
	private static final boolean PROFILE_ONLY = false;

	/** number of regression runs */
	private static final int RUNS = 1;

	/** run the default JDK1 test on all classes */
	private static final boolean DEFAULT = true;

	/** run JDK2 tests */
	private static final boolean JDK2 = true;

	/** number of thread regression runs */
	protected static final int THREAD_RUNS = 300;

	/**
	 * runs the thread test
	 * For a real thread test run, set DEFAULT and JDK 2 to false
	 * Increase runs to a higher value
	 */
	private static final boolean THREADS = false;

	/** the number of RUNS or THREAD_RUNS between commits */
	private static final int COMMIT_AFTER = 5;

	/** log class name of current test class */
	private static final boolean LOG_CLASS_NAMES = true;

	public static void main(String[] args) {

		new java.io.File(FILE).delete();

		Db4o.configure().messageLevel(-1);
		// Db4o.configure().exceptionsOnNotStorable(true);

		// Db4o.licensedTo("tobi@db4o.com");
		// Db4o.configure().password("Houilo7");
		// Db4o.configure().encrypt(true);

		new Regression().run();
	}

	public void run() {
		if (isJDK2() && JDK2) {
			Thread.currentThread().setName("JDK2 Regression Test");
			try {
				Regression regression2 =
					(Regression) Class.forName("com.db4o.test.test2.Regression2").newInstance();
				run1(regression2.testClasses());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (DEFAULT) {
			Thread.currentThread().setName("Default Regression Test");
			run1(testClasses());
		}

		if (THREADS) {
			try {
				threadsSharp = true;
				new Thread(new Thread1(this), "Regression.Thread1").start();
				Thread.sleep(100);
				// the server socket neads a little time to come up
				new Thread(new Thread2(this), "Regression.Thread2").start();
				Thread.sleep(100);
				new Thread(new Thread3(this), "Regression.Thread3").start();

				// We don't want to run out of main to allow sequential
				// execution of Ant tasks.
				do {
				    Runtime4.sleep(300);
				} while (threadsSharp);

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			completed();
		}
	}

	public void run1(RTestable[] clazzes) {
		openedThreads++;
		long time = System.currentTimeMillis();
		mainLoop(clazzes);
		time = System.currentTimeMillis() - time;
		System.out.println("\n" + Thread.currentThread().getName() + ": " + time + " ms.");
		returnedThreads++;
		if (returnedThreads >= openedThreads) {
			if (errors.length() == 0) {
				ObjectContainer con = open();
				int objectCount = con.queryByExample(null).size();
				closeAllButMemoryFile(con);
				System.out.println(
					PROFILE_ONLY
						? "Profile run completed."
						: "Regression Test Passed. " + objectCount + " objects.");
			} else {
				System.out.println("!!! Regression Test Failed. !!!");
			}
			System.out.println(Db4o.version());
			System.out.println(errors);
			if (threadsSharp) {
				if (openDelegate != null) {
					openDelegate.completed();
				}
				threadsSharp = false;
			}
		}
	}

	public void mainLoop(RTestable[] clazzes) {
		int commitCounter = 0;
		int run = 0;
		ObjectContainer con = open();
		for (int k = 0; k < runs(); k++) {
			run++;
			System.out.println(Thread.currentThread().getName() + "   Run:" + run);
			closeFile = false;
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < clazzes.length; j++) {
					if(! clazzes[j].jdk2()  || isJDK2()){
						con = cycle(con, clazzes[j], run);
					}
				}
				closeFile = true;
			}
			commitCounter++;
			if (commitCounter > COMMIT_AFTER) {
				con.commit();
				commitCounter = 0;
			}
		}


		if(closeFile()){
			close(con);
			con = open();
		}

		// check for no duplicates
		StoredClass[] intClasses = con.ext().storedClasses();
		String[] noDuplicates = new String[intClasses.length];
		for (int i = 0; i < intClasses.length; i++) {
			noDuplicates[i] = intClasses[i].getName();
		}
		for (int i = 0; i < noDuplicates.length; i++) {
			for (int j = i + 1; j < noDuplicates.length; j++) {
				if (noDuplicates[i].equals(noDuplicates[j])) {
					addError("Duplicate class definition: " + noDuplicates[i]);
				}
			}
		}
		closeAllButMemoryFile(con);
	}

	ObjectContainer cycle(ObjectContainer con, RTestable clazz, int a_run) {
		if (LOG_CLASS_NAMES) {
			System.out.println("Testing class: " + clazz.getClass().getName());
		}
		if (clazz.jdk2() && (! isJDK2())) {
			return con;
		}
		Object obj = clazz.newInstance();
		con = reOpen(con);

		// remove all
		Object get = clazz.newInstance();
		con.deactivate(get, Integer.MAX_VALUE);
		ObjectSet set = con.queryByExample(get);
		while (set.hasNext()) {
			con.delete(set.next());
		}

		// STEP1 add one object
		clazz.set(obj, 1);
		con.store(obj);
		specificTest(clazz, con, ONE);
		con = reOpen(con);

		// The check here has proved invaluable multiple times.
		// Don't delete the uncommented code.

		//        set = con.get(null);
		//        System.out.println(set.size());
		//        while(set.hasNext()){
		//        	Logger.log(con, set.next());
		//        }

		// check 1, retrieving no members set
		compare(con, get, clazz, 1, 1);

		// update 1, retrieving all members set
		clazz.set(get, 1);
		compare(con, get, clazz, 1, 1);
		con = reOpen(con);

		// add 4
		for (int i = 0; i < 4; i++) {
			obj = clazz.newInstance();
			clazz.set(obj, 1);
			con.store(obj);
		}
		con = reOpen(con);

		// check 5
		compare(con, get, clazz, 1, 5);
		specificTest(clazz, con, FIVE);
		con = reOpen(con);

		// delete 1
		set = con.queryByExample(get);
		obj = set.next();
		con.delete(obj);
		con = reOpen(con);

		// check 4
		compare(con, get, clazz, 1, 4);
		specificTest(clazz, con, DELETED);
		con = reOpen(con);
		
		// update 1
		set = con.queryByExample(get);
		obj = set.next();
		clazz.set(obj, 2);
		con.store(obj);
		con = reOpen(con);
		
		
//		Defragment test
//		
//		if(closeFile()){
//			con.close();
//			new Defragment().run(FILE, true);
//			configure();
//			con = open();
//		}

		// check 3
		compare(con, get, clazz, 1, 3);
		specificTest(clazz, con, SAME);
		con = reOpen(con);

		// check 1
		clazz.set(get, 2);
		compare(con, get, clazz, 2, 1);
		specificTest(clazz, con, UPDATED);
		con = reOpen(con);

		if (clazz.ver3()) {

			// update another 1 with ver3
			set = con.queryByExample(get);
			obj = set.next();
			clazz.set(obj, 3);
			con.store(obj);
			con = reOpen(con);

			// check 2
			clazz.set(get, 1);
			compare(con, get, clazz, 1, 3);
			specificTest(clazz, con, SAME);
			con = reOpen(con);

			// check 1
			clazz.set(get, 3);
			compare(con, get, clazz, 3, 1);
			specificTest(clazz, con, UPDATED);
			con = reOpen(con);
		}
		
		return con;
	}

	public void completed() {
		// virtual
	}

	public void compare(ObjectContainer con, Object get, RTestable clazz, int ver, int count) {
		ObjectSet set = con.queryByExample(get);
		if (!PROFILE_ONLY) {
			set.reset();
			if (set.size() == count) {
				while (set.hasNext()) {
					Object res = set.next();
					clazz.compare(con, res, ver);
					if (DEACTIVATE) {
						con.deactivate(res, 1);
						con.activate(res, Integer.MAX_VALUE);
						clazz.compare(con, res, ver);
					}
				}
			} else {
				Regression.addError(
					clazz.getClass().getName()
						+ ":offcount:expected"
						+ count
						+ ":actual:"
						+ set.size());
			}
		}
	}

	void specificTest(Object clazz, ObjectContainer con, int step) {
		String methodName = "specific";
		Class[] parameterClasses = { ObjectContainer.class, Integer.TYPE };
		try {
			Method method = clazz.getClass().getMethod(methodName, parameterClasses);
			if (method != null) {
				method.invoke(clazz, new Object[] { con, new Integer(step)});
			}
		} catch (Exception e) {
		}
	}

	public ObjectContainer open() {
		return openContainer();
	}

	public static void configure() {
		Configuration config = Db4o.configure();
		// Set ActivationDepth deep enough for Recursive classes.
		config.activationDepth(12);
		ObjectClass oc = config.objectClass("com.db4o.test.DeepUpdate");
		oc.updateDepth(2);
		oc = config.objectClass("com.db4o.test.CustomConstructor");
		oc.translate(new TCustomConstructor());
		oc = config.objectClass("com.db4o.test.Debug");
		oc.updateDepth(5);
	}

	public ObjectContainer openContainer() {
		if (openDelegate != null) {
			return openDelegate.openContainer();
		}
		configure();
		return Db4o.openFile(FILE);
	}
	
	public void close(ObjectContainer con) {
		if (openDelegate != null) {
			openDelegate.close(con);
		}
		if(closeFile()){
			con.close();
		}
	}

	protected int runs() {
		return RUNS;
	}
	
	protected void closeAllButMemoryFile(ObjectContainer con){
		if (openDelegate != null) {
			openDelegate.closeAllButMemoryFile(con);
		}else{
			con.close();
		}
	}

	protected boolean closeFile() {
		return closeFile;
	}

	public ObjectContainer reOpen(ObjectContainer con) {
		if (closeFile()) {
			close(con);
			return open();
		}
		return con;
	}

	Object newInstance(Class a_class) {
		try {
			return a_class.newInstance();
		} catch (Throwable t) {
			try {
				Constructor[] constructors = a_class.getDeclaredConstructors();
				for (int i = 0; i < constructors.length; i++) {
					try {
						Platform4.setAccessible(constructors[i]);
						
						Class[] pTypes = constructors[i].getParameterTypes();
						Object[] parms = new Object[constructors[i].getParameterTypes().length];
						for (int j = 0; j < parms.length; j++) {
							for (int k = 0; k < simpleNullWrappers.length; k++) {
								if (pTypes[j] == simpleClasses[k]) {
									parms[j] = simpleNullWrappers[k];
									break;
								}
							}
						}
						Object res = constructors[i].newInstance(parms);
						if (res != null) {
							return res;
						}
					} catch (Exception exc) {
						System.out.println(exc.getClass().getName());
						System.out.println(exc.getMessage());
					}
				}
			} catch (Exception ex) {
			}
			System.out.println("NewInstance failed:" + a_class.getName());
			return null;
		}
	}
	
	boolean isJDK2(){
		return Platform4.jdk().ver() >= 2;
	}

	// The following errors are expected.
	// They occur due to the fact that:
	// - Byte Objects will be instantiated even if the stored Byte object was null
	// - the "Empty" objects does not change on update
	// - the RecursiveTyped objects create more instances of themselves than would be expected
	static String[] expectedErrors =
		{
			"1e3==null:com.db4o.test.types.ArrayTypedPrivate:oByte:",
			"1e0==null:com.db4o.test.types.ArrayTypedPrivate:nByte:",
			"1e3==null:com.db4o.test.types.ArrayTypedPublic:oByte:",
			"1e0==null:com.db4o.test.types.ArrayTypedPublic:nByte:",
			"com.db4o.test.types.Empty:offcount:expected3:actual:4",
			"com.db4o.test.types.Empty:offcount:expected1:actual:4",
			"f1==null:com.db4o.test.types.MasterMonster:ooo:nByte:",
			"1e3==null:com.db4o.test.types.MasterMonster:ooo:oByte:",
			"1e0==null:com.db4o.test.types.MasterMonster:ooo:nByte:",
			"com.db4o.test.types.RecursiveTypedPrivate:offcount:expected1:actual:11",
			"com.db4o.test.types.RecursiveUnTypedPrivate:offcount:expected1:actual:11",
			"com.db4o.test.types.RecursiveTypedPublic:offcount:expected1:actual:11",
			"com.db4o.test.types.RecursiveUnTypedPublic:offcount:expected1:actual:11",
			"f1==null:com.db4o.test.types.TypedPrivate:nByte:",
			"f1==null:com.db4o.test.types.TypedPublic:nByte:" };

	static Object[] simpleNullWrappers =
		{
			new Integer(0),
			new Long(0),
			new Character((char) 0),
			new Double(0),
			new Float(0),
			new Boolean(false),
			new Short((short) 0),
			new Byte((byte) 0)};

	static Class[] simpleClasses =
		{
			Integer.TYPE,
			Long.TYPE,
			Character.TYPE,
			Double.TYPE,
			Float.TYPE,
			Boolean.TYPE,
			Short.TYPE,
			Byte.TYPE };

	public static final int ONE = 1;
	public static final int FIVE = 5;
	public static final int DELETED = 4;
	public static final int SAME = 3;
	public static final int UPDATED = 0;

	public synchronized static void addError(String err) {
		for (int i = 0; i < expectedErrors.length; i++) {
			if (err.equals(expectedErrors[i])) {
				return;
			}
		}
		errors = errors + err + System.getProperty("line.separator");
	}

	public static final boolean DEACTIVATE = false;
	public static String FILE = "regression.db4o";
	protected Regression openDelegate;
	private static String errors = "";
	private static int openedThreads = 0;
	private static int returnedThreads = 0;
	private static boolean threadsSharp = false;
	
	private boolean closeFile;

	public static RTestable[] allClasses() {
		return new RTestable[] {
			new ArrayInObjectPrivate(),
			new ArrayInObjectPublic(),
			new ArrayMixedInObjectPrivate(),
			new ArrayMixedInObjectPublic(),
			new ArrayMixedTypedPrivate(),
			new ArrayMixedTypedPublic(),
			new ArrayNDimensionalPrivate(),
			new ArrayNDimensionalPublic(),
			new ArrayTypedPrivate(),
			new ArrayTypedPublic(),
			new ArrayUntypedPrivate(),
			new ArrayUntypedPublic(),
			new BiParentTypedPrivate(),
			new BiParentTypedPublic(),
			new BiParentUnTypedPrivate(),
			new BiParentUnTypedPublic(),
			new DeepUpdate(),
			new Empty(),
			new InterfacePrivate(),
			new InterfacePublic(),
			new ObjectSimplePrivate(),
			new ObjectSimplePublic(),
			new ParameterConstructor(0),
			PrivateConstructor.construct(),
			new RecursiveTypedPrivate(),
			new RecursiveTypedPublic(),
			new RecursiveUnTypedPrivate(),
			new RecursiveUnTypedPublic(),
			new RHashtable(),
			new RProperties(),
			new RStack(),
			new RVector(),
			new SelfReference(),
			new TypedPrivate(),
			new TypedPublic(),
			new UntypedPrivate(),
			new UntypedPublic()
			// new MasterMonster()
		};
	}
	public RTestable[] testClasses() {
		if (!DEBUG) {
			return allClasses();
		}
		return new RTestable[] {
			new UntypedDebug()
//			new RStack(),
//			new RVector(),
//			new SelfReference(),
		};
	}

}
