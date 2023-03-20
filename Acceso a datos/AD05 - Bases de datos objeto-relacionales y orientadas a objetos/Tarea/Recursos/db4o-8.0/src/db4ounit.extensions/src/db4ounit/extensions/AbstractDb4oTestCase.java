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
package db4ounit.extensions;

import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.marshall.*;
import com.db4o.query.*;
import com.db4o.reflect.*;

import db4ounit.*;
import db4ounit.extensions.concurrency.*;
import db4ounit.extensions.fixtures.*;

/**
 * @sharpen.partial
 */
public class AbstractDb4oTestCase implements Db4oTestCase, TestLifeCycle {
	
	private static final int DEFAULT_CONCURRENCY_THREAD_COUNT = 10;	
	
	private transient int _threadCount = DEFAULT_CONCURRENCY_THREAD_COUNT;
	
	/* (non-Javadoc)
	 * @see db4ounit.extensions.Db4oTestCase#fixture()
	 */
	public static Db4oFixture fixture() {
		return Db4oFixtureVariable.fixture();
	}
	
	public boolean isMultiSession() {
		return fixture() instanceof MultiSessionFixture;
	}
	
	protected boolean isEmbedded() {
		return fixture() instanceof Db4oEmbeddedSessionFixture;
	}
	
	protected boolean isNetworking() {
		return fixture() instanceof Db4oNetworking;
	}
	
	public ExtObjectContainer openNewSession() {
		MultiSessionFixture fixture = (MultiSessionFixture) fixture();
        try {
			return fixture.openNewSession(this);
		} catch (Exception e) {
			throw new Db4oException(e);
		}
	}
	
	protected void reopen() throws Exception {
    	fixture().reopen(this);
    }
	
	public final void setUp() throws Exception {
		final Db4oFixture _fixture = fixture();
        _fixture.clean();
        db4oSetupBeforeConfigure();
		configure(_fixture.config());
		_fixture.open(this);
        db4oSetupBeforeStore();
		store();
		_fixture.db().commit();
        _fixture.close();
        _fixture.open(this);
        db4oSetupAfterStore();
	}

	public final void tearDown() throws Exception {
		try {
			db4oTearDownBeforeClean();
		} finally {
			
			final Db4oFixture fixture = fixture();
			fixture.close();
			
			List<Throwable> uncaughtExceptions = fixture.uncaughtExceptions();
			
	        fixture.clean();
	        
	        handleUncaughtExceptions(uncaughtExceptions);
	        
		}
		db4oTearDownAfterClean();
	}

	protected void handleUncaughtExceptions(List<Throwable> uncaughtExceptions) {
	    if (uncaughtExceptions.size() > 0) {
	    	Assert.fail("Uncaught exceptions: " + Iterators.join(Iterators.iterator(uncaughtExceptions), ", "), uncaughtExceptions.get(0));
	    }
    }
	
	protected void db4oSetupBeforeConfigure() throws Exception {}
	protected void db4oSetupBeforeStore() throws Exception {}
	protected void db4oSetupAfterStore() throws Exception {}
	protected void db4oTearDownBeforeClean() throws Exception {}
	protected void db4oTearDownAfterClean() throws Exception {}

	protected void configure(Configuration config) throws Exception {}
	
	protected void store() throws Exception {}

	/* (non-Javadoc)
	 * @see db4ounit.extensions.Db4oTestCase#db()
	 */
	public ExtObjectContainer db() {
		return fixture().db();
	}
	
	protected Class[] testCases() {
		return new Class[] { getClass() };
	}
	
	public int runAll() {
		return new ConsoleTestRunner(Iterators.concat(new Iterable4[] {
        		soloSuite(),
        		networkingSuite(),
        		embeddedSuite(),
        })).run();
	}
	
	public int runSolo(final String testLabelSubstring) {
		return new ConsoleTestRunner(
				Iterators.filter(soloSuite(), new Predicate4<Test>() {
					public boolean match(Test candidate) {
						return candidate.label().contains(testLabelSubstring);
					}
				})).run();
	}
	
	public int runSoloAndClientServer() {
		return new ConsoleTestRunner(Iterators.concat(new Iterable4[] {
        		soloSuite(),
        		networkingSuite(),				
        })).run();
	}

	public int runSoloAndEmbeddedClientServer() {
		return new ConsoleTestRunner(Iterators.concat(new Iterable4[] {
        		soloSuite(),
        		embeddedSuite(),				
        })).run();
	}

	public int runSolo() {
		return new ConsoleTestRunner(soloSuite()).run();
	}

	public int runInMemory() {
		return new ConsoleTestRunner(inMemorySuite()).run();
	}

	public int runNetworking() {
    	return new ConsoleTestRunner(networkingSuite()).run();
    }
    
    public int runEmbedded() {
    	return new ConsoleTestRunner(embeddedSuite()).run();
    }

    public int runConcurrency() {
    	return new ConsoleTestRunner(concurrenyClientServerSuite(false, "CONC")).run();
    }

    public int runEmbeddedConcurrency() {
    	return new ConsoleTestRunner(concurrenyClientServerSuite(true, "CONC EMBEDDED")).run();
    }

    public int runConcurrencyAll() {
		return new ConsoleTestRunner(Iterators.concat(new Iterable4[] {
        		concurrenyClientServerSuite(false, "CONC"),
        		concurrenyClientServerSuite(true, "CONC EMBEDDED"),
        })).run();
	}
	
    protected Db4oTestSuiteBuilder soloSuite() {
		return new Db4oTestSuiteBuilder(
				Db4oFixtures.newSolo(), testCases());
	}

    protected Db4oTestSuiteBuilder inMemorySuite() {
		return new Db4oTestSuiteBuilder(
				Db4oFixtures.newInMemory(), testCases());
	}

	protected Db4oTestSuiteBuilder networkingSuite() {
		return new Db4oTestSuiteBuilder(
		        Db4oFixtures.newNetworkingCS(), 
		        testCases());
	}

	protected Db4oTestSuiteBuilder embeddedSuite() {
		return new Db4oTestSuiteBuilder(
		        Db4oFixtures.newEmbedded(), 
		        testCases());
	}

	protected Db4oTestSuiteBuilder concurrenyClientServerSuite(boolean embedded, String label) {
		return new Db4oConcurrencyTestSuiteBuilder(
		        embedded ? Db4oFixtures.newEmbedded(label) : Db4oFixtures.newNetworkingCS(label), 
		        testCases());
	}
	
    protected InternalObjectContainer stream() {
        return (InternalObjectContainer) db();
    }

    protected ObjectContainerBase container() {
        return stream().container();
    }

    public LocalObjectContainer fileSession() {
        return fixture().fileSession();
    }

    public Transaction trans() {
        return ((InternalObjectContainer) db()).transaction();
    }

    protected Transaction systemTrans() {
        return trans().systemTransaction();
    }
    
    protected Query newQuery(Transaction transaction, Class clazz) {
		final Query query = newQuery(transaction);
		query.constrain(clazz);
		return query;
	}
	
	protected Query newQuery(Transaction transaction) {
		return container().query(transaction);
	}
    
    protected Query newQuery(){
        return newQuery(db());
    }
    
    protected static Query newQuery(ExtObjectContainer oc){
        return oc.query();
    }
    
	protected Query newQuery(Class clazz) {
		return newQuery(db(), clazz);
	}
	
	protected static Query newQuery(ExtObjectContainer oc, Class clazz) {
		final Query query = newQuery(oc);
		query.constrain(clazz);
		return query;
	}
	
    protected Reflector reflector(){
        return stream().reflector();
    }

	protected void indexField(Configuration config,Class clazz, String fieldName) {
		config.objectClass(clazz).objectField(fieldName).indexed(true);
	}

	protected Transaction newTransaction() {
		synchronized(container().lock()){
			return container().newUserTransaction();
		}
	}
	
	public <T> T retrieveOnlyInstance(Class<T> clazz) {
		return retrieveOnlyInstance(db(), clazz);
	}
	
	public static <T> T retrieveOnlyInstance(ExtObjectContainer oc, Class<T> clazz) {
		ObjectSet<T> result=newQuery(oc, clazz).execute();
		Assert.areEqual(1,result.size());
		return result.next();
	}
	
	protected int countOccurences(Class clazz) {
		return countOccurences(db(), clazz);
	}
	
	protected int countOccurences(ExtObjectContainer oc, Class clazz) {
		ObjectSet result = newQuery(oc, clazz).execute();
		return result.size();
	}
	
	protected void assertOccurrences(Class clazz, int expected) {
		assertOccurrences(db(), clazz, expected);
	}
	
	protected void assertOccurrences(ExtObjectContainer oc, Class clazz, int expected) {
		Assert.areEqual(expected, countOccurences(oc, clazz));
	}
	
	protected <T> void foreach(Class<T> clazz, Visitor4<T> visitor) {
        foreach(db(), clazz, visitor);
	}

	protected <T> void foreach(final ExtObjectContainer container, Class<T> clazz, Visitor4<T> visitor) {
	    ObjectSet<T> set = newQuery(container, clazz).execute();
        while (set.hasNext()) {
            visitor.visit(set.next());
        }
    }
	
	protected final void deleteAll(Class clazz) {
		deleteAll(db(), clazz);
	}
	
	protected final void deleteAll(final ExtObjectContainer oc, Class clazz) {
		foreach(oc, clazz, new Visitor4() {
			public void visit(Object obj) {
				oc.delete(obj);
			}
		});
	}
	
	protected final void deleteObjectSet(ObjectSet os) {
		deleteObjectSet(db(), os);
	}
	
	protected final void deleteObjectSet(ObjectContainer oc, ObjectSet os) {
		while (os.hasNext()) {
			oc.delete(os.next());
		}
	}
	
	public final void store(Object obj) {
		db().store(obj);
	}
	
	protected ClassMetadata classMetadataFor(Class clazz) {
		return stream().classMetadataForReflectClass(reflectClass(clazz));
	}

	protected ReflectClass reflectClass(Class clazz) {
		return reflector().forClass(clazz);
	}
	
	protected void defragment() throws Exception{
		fixture().close();
		fixture().defragment();
		fixture().open(this);
	}
	
	public final int threadCount() {
		return _threadCount;
	}
	
	public final void configureThreadCount(int count) {
		_threadCount = count;
	}

	protected EventRegistry eventRegistry() {
		return eventRegistryFor(db());
	}

	protected EventRegistry eventRegistryFor(final ExtObjectContainer container) {
		return EventRegistryFactory.forObjectContainer(container);
	}

	protected EventRegistry serverEventRegistry() {
		return eventRegistryFor(fileSession());
	}

	protected Context context() {
		return trans().context();
	}
	
	protected void commit() {
		db().commit();
	}
}
