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
import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.db4ounit.common.util.*;

/**
 * This is the main db4o regression test. 
 * 
 * The parameters of the testing environment and all registered test
 * cases can be found in AllTestsConfAll.java.
 * 
 * Derive this class from AllTestsConfSingle if you only want to run
 * single test cases and enter the test case that you want to run in
 * the AllTestsConfSingle#TESTS[] array.
 */
public class AllTests extends AllTestsConfAll implements Runnable {

    public static void main(String[] args) {
    	if(args!=null&&args.length>0&&args[0].startsWith("-")) {
    		boolean solo=false;
    		boolean cs=false;
    		if("-solo".equals(args[0])||"-full".equals(args[0])) {
    			solo=true;
    		}
    		if("-cs".equals(args[0])||"-full".equals(args[0])) {
    			cs=true;
    		}
    		AllTests.run(solo, cs, testCasesFromArgs(args,1));
    		return;
    	}
    	if(args!=null&&args.length==1&&args[0].equals("-withExceptions")) {
            new AllTests(new String[]{}).runWithException();
            return;
    	}
        new AllTests(args).run();
    }
    
    public AllTests(String[] testcasenames) {
    	
        Configuration conf = Db4o.configure();
        conf.messageLevel(-1);
        
//         conf.io(new MemoryIoAdapter());
        
//        conf.generateUUIDs(Integer.MAX_VALUE);
//        conf.generateVersionNumbers(Integer.MAX_VALUE);
        
//		  conf.blockSize(8);
//        conf.automaticShutDown(false);
//        conf.lockDatabaseFile(false);
//        conf.singleThreadedClient(true);
//        conf.automaticShutDown(false);
//        conf.lockDatabaseFile(false);
//        conf.weakReferences(false);
//        conf.callbacks(false);
//        conf.detectSchemaChanges(false);
//        conf.testConstructors(false);
//        conf.discardFreeSpace(Integer.MAX_VALUE);
//        conf.password("hudhoododod");
//        conf.encrypt(true);
//        conf.singleThreadedClient(true);
//

    	
        if(testcasenames!=null&&testcasenames.length>0) {
            _testCases=testCasesFromArgs(testcasenames);
        } else{
            testCasesFromTestSuites();
        }
        
        Test.currentRunner = this;
    }

    public void run() {
		printOutResult(runResult());
    }

    public void runWithException() {
		TestResult result = runResult();
		printOutResult(result);
		if(result.numFailures()>0) {
			throw new RuntimeException("db4o regression test failure: "+result);
		}
    }
    
	private void printOutResult(TestResult result) {
		System.err.println("\n\nAllTests completed.\nAssertions: "
		    + result.numAssertions() + "\nTime: " + result.timeTaken() + "ms");
		if (result.numFailures() == 0) {
		    System.err.println("No errors detected.\n");
		} else {
		    System.err
		        .println("" + result.numFailures() + " ERRORS DETECTED !!!.\n");
		}
	}

	public TestResult runResult() {
		logConfiguration();

        Test.beginTesting();

        long time = System.currentTimeMillis();

        if (DELETE_FILE) {
            Test.delete();
        }

        configure();

        for (Test.run = 1; Test.run <= RUNS; Test.run++) {
            System.out.println("\ncom.db4o.test.AllTests run " + Test.run
                + " from " + RUNS + "\n");
            if (SOLO) {
                Test.runServer = false;
                Test.clientServer = false;
                runTests();
            }
            if (CLIENT_SERVER) {
                Test.runServer = !REMOTE_SERVER;
                Test.clientServer = true;
                runTests();
            }
            Test.end();
        }
        time = System.currentTimeMillis() - time;
        TestResult result=new TestResult(Test.assertionCount,Test.errorCount,time);
		return result;
	}

    protected void configure() {
        for (int i = 0; i < _testCases.length; i++) {
            Object toTest = newInstance(_testCases[i]);
            runMethod(toTest, "configure");
        }
    }

    private void runTests() {
        String cs = Test.clientServer ? "C/S" : "SOLO";
        for (int i = 0; i < _testCases.length; i++) {
            System.out.println(cs + " testing " + _testCases[i].getName());
            Object toTest = newInstance(_testCases[i]);
            Test.open();
            if (!runStoreOne(toTest)) {
                runMethod(toTest, "store");
            }
            Test.commit();
            Test.close();
            Test.open();
            toTest = newInstance(_testCases[i]);
            runTestOne(toTest);
            toTest = newInstance(_testCases[i]);
            Method[] methods = _testCases[i].getDeclaredMethods();
            for (int j = 0; j < methods.length; j++) {
                Method method = methods[j];
                String methodName = method.getName();
                if (!methodName.equals("testOne")) {
                    if (method.getName().indexOf("test") == 0) {
                        try {
                            method.invoke(toTest, (Object[])null);
                        } catch (Exception e) {
                        	Test.errorCount++;
                            e.printStackTrace();
                        }
                    }
                }
            }
            Test.close();
        }
    }

    private Object newInstance(Class clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        }
        System.out.println("Instantiation failed. Class:" + clazz.getName());
        System.out.println("The class needs a #newInstance() constructor for the test framework.");
        new Exception().printStackTrace();
        return null;
    }

    private void runMethod(Object onObject, String methodName) {
        try {
            Method method = onObject.getClass().getDeclaredMethod(methodName,
                (Class[])null);
            if (method != null) {
                try {
                    method.invoke(onObject, (Object[])null);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        } catch (Exception e) {
        }
    }

    private boolean runStoreOne(Object onObject) {
        try {
            Method method = onObject.getClass().getDeclaredMethod("storeOne",
                (Class[])null);
            if (method != null) {
                try {
                    Test.deleteAllInstances(onObject);
                    method.invoke(onObject, (Object[])null);
                    Test.store(onObject);
                    return true;
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    private boolean runTestOne(Object onObject) {
        try {
            Method method = onObject.getClass().getDeclaredMethod("testOne",
                (Class[])null);
            if (method != null) {
                try {
                    onObject = Test.getOne(onObject);
                    method.invoke(onObject, (Object[])null);
                    return true;
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        } catch (Exception e) {
        }
        return false;
    }
    
    public static void run(Class clazz){
        run(new Class[]{clazz});
    }
    
    public static void run(TestSuite suite){
        run(suite.tests());
    }
    
    public static void run(Class[] classes){
        run(true, true, classes);
    }
    
    public static void runSolo(Class clazz){
        runSolo(new Class[]{clazz});
    }
    
    public static void runSolo(TestSuite suite){
        runSolo(suite.tests());
    }
    
    public static void runSolo(Class[] classes){
        run(true, false, classes);
    }
    
    public static void runClientServer(Class clazz){
        runClientServer(new Class[]{clazz});
    }
    
    public static void runClientServer(TestSuite suite){
        runClientServer(suite.tests());
    }
    
    public static void runClientServer(Class[] classes){
        run(false, true, classes);
    }
    
    public static void run(boolean solo, boolean clientServer, Class[] classes){
        AllTests allTests = new AllTests();
        allTests._testCases=classes;
        allTests.SOLO = solo;
        allTests.CLIENT_SERVER = clientServer;
        allTests.run();
    }

    protected void logConfiguration() {
        System.err.println("Running " + getClass().getName() + " against\n"
            + Db4o.version() + "\n");
        System.out.println("Using " + TEST_CONFIGURATION
        	+ ".\n");
        System.out.println("SERVER_HOSTNAME: " + SERVER_HOSTNAME);
        System.out.println("SERVER_PORT: " + SERVER_PORT);
        System.out.println("FILE_SERVER: " + FILE_SERVER);
        System.out.println("FILE_SOLO: " + FILE_SOLO);
        System.out.println("DELETE_FILE: " + DELETE_FILE);
        System.out.println("BLOB_PATH: " + BLOB_PATH + "\n");

    }

    public AllTests() {
        this(null);
    }
    
    private void testCasesFromTestSuites() {
    	_testCases=new Class[0];
        _testSuites = new Vector();
    	
    	addTestSuites(this);
    	
    	Enumeration e = _testSuites.elements();
    	while (e.hasMoreElements()) {
    		TestSuite suite = (TestSuite)e.nextElement();
            _testCases = Db4oUnitTestUtil.mergeClasses(_testCases, suite.tests());
    	}
    }
    
	private static Class[] testCasesFromArgs(String[] testcasenames) {
		return testCasesFromArgs(testcasenames,0);
    }

	private static Class[] testCasesFromArgs(String[] testcasenames,int offset) {
        Class[] testCases=new Class[testcasenames.length-offset];
        for (int testidx = offset; testidx < testcasenames.length; testidx++) {
            try {
                testCases[testidx-offset]=Class.forName(testcasenames[testidx]);
            } catch (ClassNotFoundException e) {
                System.err.println("Test case class not found: "+testcasenames[testidx]);
                e.printStackTrace();
                System.exit(0);
            }
        }
        return testCases;
    }

	
   private Class[] _testCases;

}