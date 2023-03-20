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
package com.db4o.test.legacy.soda;

import java.lang.reflect.*;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.query.*;
import com.db4o.test.legacy.soda.arrays.object.*;
import com.db4o.test.legacy.soda.arrays.typed.*;
import com.db4o.test.legacy.soda.arrays.untyped.*;
import com.db4o.test.legacy.soda.classes.simple.*;
import com.db4o.test.legacy.soda.classes.typedhierarchy.*;
import com.db4o.test.legacy.soda.classes.untypedhierarchy.*;
import com.db4o.test.legacy.soda.collections.*;
import com.db4o.test.legacy.soda.deepOR.*;
import com.db4o.test.legacy.soda.engines.db4o.*;
import com.db4o.test.legacy.soda.experiments.*;
import com.db4o.test.legacy.soda.joins.typed.*;
import com.db4o.test.legacy.soda.joins.untyped.*;
import com.db4o.test.legacy.soda.ordered.*;
import com.db4o.test.legacy.soda.wrapper.typed.*;
import com.db4o.test.legacy.soda.wrapper.untyped.*;
import com.db4o.test.util.*;


public class SodaTest {

    private static final STEngine[] ENGINES = new STEngine[] { 
    	new STDb4o(),
        // new STDb4oClientServer()
    };

    public static final STClass[] CLASSES = sodaTestCases();
    
    private static STClass[] sodaTestCases() {
    	STClass[] commonCases = commonTestCases();
    	STClass[] collectionCases = collectionTestCases();
    	STClass[] allCases = new STClass[commonCases.length + collectionCases.length];
    	System.arraycopy(commonCases, 0, allCases, 0, commonCases.length);
    	System.arraycopy(collectionCases, 0, allCases, commonCases.length, collectionCases.length);
    	return allCases;
    }
    
    private static STClass[] commonTestCases() {
        return new STClass[] {
            new STArrMixed(),
            new STArrStringO(),
            new STArrStringON(),
            new STArrStringT(),
            new STArrStringTN(),
            new STArrStringU(),
            new STArrStringUN(),
            new STArrIntegerO(),
            new STArrIntegerON(),
            new STArrIntegerT(),
            new STArrIntegerTN(),
            new STArrIntegerU(),
            new STArrIntegerUN(),
            new STArrIntegerWT(),
            new STArrIntegerWTON(),
            new STArrIntegerWUON(),
            new STBoolean(),
            new STBooleanWT(),
            new STBooleanWU(),
            new STByte(),
            new STByteWT(),
            new STByteWU(),
            new STCaseInsensitive(),
            new STChar(),
            new STCharWT(),
            new STCharWU(),
            new STDate(),
            new STDateU(),
            new STDouble(),
            new STDoubleWT(),
            new STDoubleWU(),
            new STETH1(),
            new STFloat(),
            new STFloatWT(),
            new STFloatWU(),
            
            // QBE is no longer supported where the
            // query can't be expressed with SODA
            // new STHashtableD(),
            // new STHashtableED(),
            
            new STHashtableET(),
            new STHashtableEU(),
            new STHashtableT(),
            new STHashtableU(),
            new STIdentityEvaluation(),
            new STInteger(),
            new STIntegerWT(),
            new STIntegerWU(),
            new STLong(),
            new STLongWT(),
            new STLongWU(),
            new STNullOnPath(),
			new STOrContains(), 
            new STOrT(),
            new STOrU(),
            new STOString(),
            new STOInteger(),
            new STOIntegerWT(),
            new STRTH1(),
            new STSDFT1(),
            new STShort(),
            new STShortWT(),
            new STShortWU(),
            new STString(),
            new STStringU(),
            new STRUH1(),
            new STTH1(),
            new STUH1(),
            
            // QBE is no longer supported where the
            // query can't be expressed with SODA
            // new STVectorD(),
            // new STVectorED(),
            
            new STVectorT(),
            new STVectorU(),
            new STVectorET(),
            new STVectorEU(),
        	new STMagic(),
       };
    }

    @decaf.ReplaceFirst(value="return new STClass[0];", platform=decaf.Platform.JDK11)
    private static STClass[] collectionTestCases() {
        return new STClass[] {
            new STArrayListT(),
            new STArrayListU(),
            new STHashSetT(),
            new STHashSetU(),
            new STLinkedListT(),
            new STLinkedListU(),
            new STOwnCollectionT(),
            new STOwnCollectionW(),
            new STTreeSetT(),
            new STTreeSetU(),
       };
    }

		protected static final boolean quiet = false;
        protected static STEngine engine;
        private static int testCases;
        private STClass currentTestClass;
        protected static Collection4 failedTestClasses = new Collection4();
        private final TCompare comparer = new TCompare();
        static long time;

        public static void main(String[] args) { time = System.currentTimeMillis();
            SodaTest st = new SodaTest();
            st.run(CLASSES, ENGINES, quiet);
            st.completed();
        }

        protected void completed() {
            time = System.currentTimeMillis() - time;
            System.out.println(name() + " completed. " + time + " ms");
            System.out.println("Test cases: " + testCases);
        }

        protected String name() {
            return "S.O.D.A. functionality test";
        }
        
        protected static void begin(){
			failedTestClasses = new Collection4();
			testCases = 0;
        }

        public void run(STClass[] classes, STEngine[] engines, boolean a_quiet) {
            begin();
            setSodaTestOn(classes);

            for (int i = 0; i < engines.length; i++) {
                engine = engines[i];
                engine.reset();
                engine.open();
                store(classes);
                engine.commit();
                engine.close();
                engine.open();
                test(classes);
                engine.close();
                engine.reset();
            }

            if (failedTestClasses.size() > 0) {
                System.err.println("\nFailed test classes:\n");
                Iterator4 i = failedTestClasses.iterator();
                while (i.moveNext()) {
                    System.err.println(i.current().getClass().getName());
                }
                System.err.println("\n");
            }
        }
        
        protected void store(STClass[] classes) {
            for (int i = 0; i < classes.length; i++) {
                if (jdkOK(classes[i])) {
                    Object[] objects = classes[i].store();
                    if (objects != null) {
                        for (int j = 0; j < objects.length; j++) {
                            engine.store(objects[j]);
                        }
                    }
                }
            }
        }

        /** dynamic execution of all public methods that begin with "test" in all CLASSES */
        protected void test(STClass[] classes) {
            for (int i = 0; i < classes.length; i++) {
                if (jdkOK(classes[i])) {
                    System.out.println("  S.O.D.A. testing " + classes[i].getClass().getName());
                    currentTestClass = classes[i];
                    Method[] methods = classes[i].getClass().getDeclaredMethods();
                    for (int j = 0; j < methods.length; j++) {
                        Method method = methods[j];
                        if (method.getName().startsWith("test")) {
                            try {
                                method.invoke(classes[i], new Object[0]);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        protected static boolean jdkOK(Object obj) {
            return Platform4.jdk().ver() >= 2
                || STClass1.class.isAssignableFrom(obj.getClass());
        }

        public Query query() {
            return engine.query();
        }

        public void expectOne(Query query, Object object) {
            expect(query, new Object[] { object });
        }

        public void expectNone(Query query) {
            expect(query, null);
        }

        public void expect(Query query, Object[] results) {
            expect(query, results, false);
        }

        public void expectOrdered(Query query, Object[] results) {
            expect(query, results, true);
        }

        private void expect(Query query, Object[] results, boolean ordered) {
            testCases++;
            ObjectSet set = query.execute();
            if (results == null || results.length == 0) {
                if (set.size() > 0) {
                    error("No content expected.");
                }
                return;
            }
            int j = 0;
            if (set.size() == results.length) {
                while (set.hasNext()) {
                    Object obj = set.next();
                    boolean found = false;
                    if (ordered) {
                        if (comparer.isEqual(results[j], obj)) {
                            results[j] = null;
                            found = true;
                        }
                        j++;
                    } else {
                        for (int i = 0; i < results.length; i++) {
                            if (results[i] != null) {
                                if (comparer.isEqual(results[i], obj)) {
                                    results[i] = null;
                                    found = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (!found) {
                        error("Object not expected: " + obj);
                    }
                }
                for (int i = 0; i < results.length; i++) {
                    if (results[i] != null) {
                        error("Expected object not returned: " + results[i]);
                    }
                }
            } else {
                error(
                    "Unexpected size returned.\nExpected: "
                        + results.length
                        + " Returned: "
                        + set.size());
            }
        }

        public void error(String msg) {
            if (!failedTestClasses.contains(currentTestClass)) {
                failedTestClasses.add(currentTestClass);
            }
            if (!quiet) {
                System.err.println(msg);
                new ExpectationFailed().printStackTrace();
                System.err.println();
            }
        }
        
        public static int failedClassesSize(){
        	return failedTestClasses.size();
        }
        
        public static int testCaseCount(){
        	return testCases;
        }

        public void log(Query query) {
            ObjectSet set = query.execute();
            while (set.hasNext()) {
                TLogger.log(set.next());
            }
        }

        protected void setSodaTestOn(STClass[] classes) {
            for (int i = 0; i < classes.length; i++) {
                try {
                    Field field = classes[i].getClass().getDeclaredField("st");
                    try {
                        Platform4.setAccessible(field);
                    } catch (Throwable t) {
                        // JDK 1.x has no setAccessible
                    }
                    field.set(classes[i], this);
                } catch (Exception e) {
                    System.err.println(
                        "Add the following line to Class " + classes[i].getClass().getName());
                    System.err.println("public static transient SodaTest st;");
                }
            }
        }

    }

    class ExpectationFailed extends Exception {
        ExpectationFailed() {
        }
    }
