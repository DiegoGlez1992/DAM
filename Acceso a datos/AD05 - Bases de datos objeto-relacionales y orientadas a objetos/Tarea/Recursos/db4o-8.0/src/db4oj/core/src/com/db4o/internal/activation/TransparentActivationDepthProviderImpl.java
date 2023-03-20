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
package com.db4o.internal.activation;

import com.db4o.*;
import com.db4o.events.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.reflect.generic.*;
import com.db4o.ta.*;

public class TransparentActivationDepthProviderImpl implements ActivationDepthProvider, TransparentActivationDepthProvider {
	
	public ActivationDepth activationDepth(int depth, ActivationMode mode) {
		if (Integer.MAX_VALUE == depth)
			return new FullActivationDepth(mode);
		return new FixedActivationDepth(depth, mode);
	}

	public ActivationDepth activationDepthFor(ClassMetadata classMetadata, ActivationMode mode) {
		if (isTAAware(classMetadata))
			return new NonDescendingActivationDepth(mode);
		if (mode.isPrefetch())
			return new FixedActivationDepth(1, mode);
		return new DescendingActivationDepth(this, mode);
	}

	private boolean isTAAware(ClassMetadata classMetadata) {
		final GenericReflector reflector = classMetadata.reflector();
		return reflector.forClass(Activatable.class).isAssignableFrom(classMetadata.classReflector());
	}
	
	private RollbackStrategy _rollbackStrategy;
	public boolean _transparentPersistenceIsEnabled;
	
	/* (non-Javadoc)
	 * @see com.db4o.internal.activation.TransparentActivationDepthProvider#enableTransparentPersistenceSupportFor(com.db4o.internal.InternalObjectContainer, com.db4o.ta.RollbackStrategy)
	 */
	public void enableTransparentPersistenceSupportFor(InternalObjectContainer container, RollbackStrategy rollbackStrategy) {
		flushOnQueryStarted(container);
		_rollbackStrategy = rollbackStrategy;
		_transparentPersistenceIsEnabled = true;
	}

	private void flushOnQueryStarted(InternalObjectContainer container) {
	    final EventRegistry registry = EventRegistryFactory.forObjectContainer(container);
		registry.queryStarted().addListener(new EventListener4() {
			public void onEvent(Event4 e, final EventArgs args) {
				objectsModifiedIn(transactionFrom(args)).flush();
            }
		});
    }

	protected Transaction transactionFrom(EventArgs args) {
		return (Transaction) ((TransactionalEventArgs) args).transaction();
    }

	/* (non-Javadoc)
	 * @see com.db4o.internal.activation.TransparentActivationDepthProvider#addModified(com.db4o.internal.Transaction, java.lang.Object)
	 */
	public void addModified(Object object, Transaction transaction) {
		if (!_transparentPersistenceIsEnabled)
			return;
		objectsModifiedIn(transaction).add(object);
	}
	
	public void removeModified(Object object, Transaction transaction) {
		if (!_transparentPersistenceIsEnabled)
			return;
		
		objectsModifiedIn(transaction).remove(object);		
	}
	
	private final TransactionLocal<ObjectsModifiedInTransaction> _objectsModifiedInTransaction = new TransactionLocal<ObjectsModifiedInTransaction>() {
		@Override public ObjectsModifiedInTransaction initialValueFor(final Transaction transaction) {
			
			final ObjectsModifiedInTransaction objectsModifiedInTransaction = new ObjectsModifiedInTransaction(transaction);
			transaction.addTransactionListener(new TransactionListener() {
				
				public void postRollback() {
	                objectsModifiedInTransaction.rollback(_rollbackStrategy);
                }

				public void preCommit() {
	                objectsModifiedInTransaction.flush();
                }
			});
			
			return objectsModifiedInTransaction;
		}
	};
	
	private ObjectsModifiedInTransaction objectsModifiedIn(Transaction transaction) {
		return transaction.get(_objectsModifiedInTransaction).value;
	}

	private static final class ObjectsModifiedInTransaction {

		private final IdentitySet4 _removedAfterModified = new IdentitySet4();
		private final IdentitySet4 _modified = new IdentitySet4();
		private final Transaction _transaction;

		public ObjectsModifiedInTransaction(Transaction transaction) {
			_transaction = transaction;
        }

		public void add(Object object) {
			if (contains(object))
				return;
			
			_modified.add(object);
		}
		
		public void remove(Object object) {
			if (!contains(object))
				return;
			
			_modified.remove(object);
			_removedAfterModified.add(object);
		}		

		private boolean contains(Object object) {
			return _modified.contains(object);
		}

		public void flush() {
			storeModifiedObjects();
			_modified.clear();
		}

		private void storeModifiedObjects() {
	        final ObjectContainerBase container = _transaction.container();
	        container.storeAll(_transaction, _modified.valuesIterator(), container.updateDepthProvider().unspecified(new ModifiedObjectQuery() {
				
				public boolean isModified(Object obj) {
					return contains(obj);
				}
			}));
	        _transaction.processDeletes();
        }

		public void rollback(RollbackStrategy rollbackStrategy) {
			applyRollbackStrategy(rollbackStrategy);
			_modified.clear();
		}

		private void applyRollbackStrategy(RollbackStrategy rollbackStrategy) {
			if (null == rollbackStrategy)
				return;
			
			applyRollbackStrategy(rollbackStrategy, _modified.valuesIterator());
			applyRollbackStrategy(rollbackStrategy, _removedAfterModified.valuesIterator());
		}

		private void applyRollbackStrategy(RollbackStrategy rollbackStrategy, final Iterator4 values) {
			final ObjectContainer objectContainer = _transaction.objectContainer();
			while (values.moveNext()) {
				rollbackStrategy.rollback(objectContainer, values.current());
			}
		}
	}
}
