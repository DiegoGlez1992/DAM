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
package com.db4o.db4ounit.common.exceptions.propagation;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.db4ounit.common.exceptions.*;
import com.db4o.internal.*;
import com.db4o.internal.config.*;
import com.db4o.internal.ids.*;

import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.fixtures.*;

public class ExceptionDuringTopLevelCallTestSuite extends FixtureBasedTestSuite implements Db4oTestCase, OptOutNetworkingCS, OptOutIdSystem {

	public static class ExceptionDuringTopLevelCallTestUnit extends AbstractDb4oTestCase {
	
		private ExceptionSimulatingStorage _storage;
		
		private ExceptionSimulatingIdSystem _idSystem;
		
		private IdSystemFactory _idSystemFactory;
		
		private Object _unactivated;
		
		public static class Item {
			public String _name;
		}
		
		@Override
		protected void configure(Configuration config) throws Exception {
			
			if(Platform4.needsLockFileThread()){
				config.lockDatabaseFile(false);
			}
			final ExceptionPropagationFixture propagationFixture = currentExceptionPropagationFixture();
			final ExceptionFactory exceptionFactory = new ExceptionFactory() {
				private boolean _alreadyCalled = false;
				
				public void throwException() {
					try {
						if(!_alreadyCalled) {
							propagationFixture.throwInitialException();
						}
						else {
							propagationFixture.throwShutdownException();
						}
					}
					finally {
						_alreadyCalled = true;
					}
				}

				public void throwOnClose() {
					propagationFixture.throwCloseException();
				}
			};
			_storage = new ExceptionSimulatingStorage(config.storage(), exceptionFactory);
			config.storage(_storage);
			
			_idSystemFactory = new IdSystemFactory() {
				public IdSystem newInstance(LocalObjectContainer container) {
					_idSystem = new ExceptionSimulatingIdSystem(container, exceptionFactory);
					return _idSystem;
				}
			};
			configureIdSystem(config);
		}


		private void configureIdSystem(Configuration config) {
			Db4oLegacyConfigurationBridge.asIdSystemConfiguration(config).useCustomSystem(_idSystemFactory);
		}
		
		@Override
		protected void db4oSetupAfterStore() throws Exception {
			store(new Item());
		}
		
		public void testExceptionDuringTopLevelCall() {
			_unactivated = retrieveOnlyInstance(Item.class);
			db().deactivate(_unactivated);
			_storage.triggerException(true);
			_idSystem.triggerException(true);
			DatabaseContext context = new DatabaseContext(db(), _unactivated);
			currentExceptionPropagationFixture().assertExecute(context, currentOperationFixture());
			if(context.storageIsClosed()){
				assertIsNotLocked(fileSession().fileName());
			}
		}
		
		private void assertIsNotLocked(String fileName) {
			EmbeddedConfiguration embeddedConfiguration = Db4oEmbedded.newConfiguration();
			embeddedConfiguration.idSystem().useCustomSystem(_idSystemFactory);
			ObjectContainer oc = Db4oEmbedded.openFile(embeddedConfiguration, fileName);
			oc.close();
		}

		private ExceptionPropagationFixture currentExceptionPropagationFixture() {
			return EXCEPTION_BEHAVIOUR_FIXTURE.value();
		}

		private TopLevelOperation currentOperationFixture() {
			return OPERATION_FIXTURE.value();
		}

		@Override
		protected void db4oTearDownBeforeClean() throws Exception {
			_storage.triggerException(false);
			_idSystem.triggerException(false);
		}
	}

	private static FixtureVariable<ExceptionPropagationFixture> EXCEPTION_BEHAVIOUR_FIXTURE = FixtureVariable.<ExceptionPropagationFixture>newInstance("exc");
	private static FixtureVariable<TopLevelOperation> OPERATION_FIXTURE = FixtureVariable.<TopLevelOperation>newInstance("op");
	
	@Override
	public FixtureProvider[] fixtureProviders() {
		return new FixtureProvider[] {
			new Db4oFixtureProvider(),
			new SimpleFixtureProvider(EXCEPTION_BEHAVIOUR_FIXTURE,
					new OutOfMemoryErrorPropagationFixture(),
					new OneTimeDb4oExceptionPropagationFixture(),
					new OneTimeRuntimeExceptionPropagationFixture(),
					new RecurringDb4oExceptionPropagationFixture(),
					new RecurringRuntimeExceptionPropagationFixture(),
					new RecoverableExceptionPropagationFixture()
			),
			new SimpleFixtureProvider(OPERATION_FIXTURE,
					new TopLevelOperation("commit") {
						@Override
						public void apply(DatabaseContext context) {
							context._db.commit();
						}
					},
					new TopLevelOperation("store") {
						@Override
						public void apply(DatabaseContext context) {
							context._db.store(new Item());
						}
					},
					new TopLevelOperation("activate") {
						@Override
						public void apply(DatabaseContext context) {
							context._db.activate(context._unactivated, Integer.MAX_VALUE);
						}
					},
					// - no deactivate test, since it doesn't trigger I/O activity
					// - no getByID test, not refactored to asTopLevelCall, since it has custom, more relaxed exception handling -> InvalidSlotExceptionTestCase
					// FIXME doesn't trigger initial exception - deletes are processed in finally block
//					new TopLevelOperation("delete") {
//						@Override
//						public void apply(DatabaseContext context) {
//							context._db.delete(context._unactivated);
//						}
//					},
					new TopLevelOperation("peek") {
						@Override
						public void apply(DatabaseContext context) {
							context._db.ext().peekPersisted(context._unactivated, 1, true);
						}
					},
					new TopLevelOperation("qbe") {
						@Override
						public void apply(DatabaseContext context) {
							context._db.queryByExample(new Item());
						}
					},
					new TopLevelOperation("query") {
						@Override
						public void apply(DatabaseContext context) {
							ObjectSet<Object> result = context._db.query().execute();
							if(result.hasNext()) {
								result.next();
							}
						}
					}
			),
		};
	}

	@Override
	public Class[] testUnits() {
		return new Class[] {
			ExceptionDuringTopLevelCallTestUnit.class,
		};
	}
}
