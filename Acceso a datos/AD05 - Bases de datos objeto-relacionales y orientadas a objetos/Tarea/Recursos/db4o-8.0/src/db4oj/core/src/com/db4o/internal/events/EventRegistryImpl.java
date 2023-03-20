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
package com.db4o.internal.events;

import com.db4o.*;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.internal.callbacks.*;
import com.db4o.query.*;

/**
 * @exclude
 */
public class EventRegistryImpl  implements Callbacks, EventRegistry {
	
	protected final Event4Impl _queryStarted = new Event4Impl();
	protected final Event4Impl _queryFinished = new Event4Impl();
	protected final Event4Impl _creating = new Event4Impl();
	protected final Event4Impl _activating = new Event4Impl();
	protected final Event4Impl _updating = new Event4Impl();
	protected final Event4Impl _deleting = new Event4Impl();
	protected final Event4Impl _deactivating = new Event4Impl();
	protected final Event4Impl _created = new Event4Impl();
	protected final Event4Impl _activated = new Event4Impl();
	protected final Event4Impl _updated = new Event4Impl();
	protected final Event4Impl _deleted = new Event4Impl();
	protected final Event4Impl _deactivated = new Event4Impl();
	protected final Event4Impl _committing = new Event4Impl();
	protected final Event4Impl _committed = new CommittedEvent();
	protected final Event4Impl _instantiated = new Event4Impl();
	protected final Event4Impl _classRegistered = new Event4Impl();	
	protected final Event4Impl _closing = new Event4Impl();
	protected final Event4Impl _opened = new Event4Impl();
	
	/**
	 * @sharpen.ignore
	 */
	protected class CommittedEvent extends Event4Impl {
		protected void onListenerAdded() {
			onCommittedListenerAdded();
		}
	}

	// Callbacks implementation
	public void queryOnFinished(final Transaction transaction, final Query query) {
		if (!_queryFinished.hasListeners())
			return;
		
		withExceptionHandling(new Runnable() { public void run() {
			_queryFinished.trigger(new QueryEventArgs(transaction, query));
		}});
	}

	public void queryOnStarted(final Transaction transaction, final Query query) {
		if (!_queryStarted.hasListeners())
			return;
		
		withExceptionHandling(new Runnable() { public void run() {
			_queryStarted.trigger(new QueryEventArgs(transaction, query));
		}});
	}
	
	public boolean objectCanNew(Transaction transaction, Object obj) {
		return triggerCancellableObjectEventArgsInCallback(transaction, _creating, null, obj);
	}
	
	public boolean objectCanActivate(Transaction transaction, Object obj) {
		return triggerCancellableObjectEventArgsInCallback(transaction, _activating, null, obj);
	}
	
	public boolean objectCanUpdate(Transaction transaction, ObjectInfo objectInfo) {
		return triggerCancellableObjectEventArgsInCallback(transaction, _updating, objectInfo, objectInfo.getObject());
	}
	
	public boolean objectCanDelete(Transaction transaction, ObjectInfo objectInfo) {
		return triggerCancellableObjectEventArgsInCallback(transaction, _deleting, objectInfo, objectInfo.getObject());
	}
	
	public boolean objectCanDeactivate(Transaction transaction, ObjectInfo objectInfo) {
		return triggerCancellableObjectEventArgsInCallback(transaction, _deactivating, objectInfo, objectInfo.getObject());
	}
	
	public void objectOnActivate(Transaction transaction, ObjectInfo obj) {
		triggerObjectInfoEventInCallback(transaction, _activated, obj);
	}
	
	public void objectOnNew(Transaction transaction, ObjectInfo obj) {
		triggerObjectInfoEventInCallback(transaction, _created, obj);
	}
	
	public void objectOnUpdate(Transaction transaction, ObjectInfo obj) {
		triggerObjectInfoEventInCallback(transaction, _updated, obj);
	}
	
	public void objectOnDelete(Transaction transaction, ObjectInfo obj) {
		triggerObjectInfoEventInCallback(transaction, _deleted, obj);		
	}	

	public void classOnRegistered(final ClassMetadata clazz) {
		if (!_classRegistered.hasListeners())
			return;
		
		withExceptionHandling(new Runnable() { public void run() {
			_classRegistered.trigger(new ClassEventArgs(clazz));
		}});
	}	

	public void objectOnDeactivate(Transaction transaction, ObjectInfo obj) {
		triggerObjectInfoEventInCallback(transaction, _deactivated, obj);
	}
	
	public void objectOnInstantiate(Transaction transaction, ObjectInfo obj) {
		triggerObjectInfoEventInCallback(transaction, _instantiated, obj);
	}
	
	public void commitOnStarted(final Transaction transaction, final CallbackObjectInfoCollections objectInfoCollections) {
		if (!_committing.hasListeners())
			return;
		
		withExceptionHandlingInCallback(new Runnable() {
        	public void run() {
        		_committing.trigger(new CommitEventArgs(transaction, objectInfoCollections, false));
        	}
        });
	}
	
	public void commitOnCompleted(final Transaction transaction, final CallbackObjectInfoCollections objectInfoCollections, final boolean isOwnCommit) {
		if (!_committed.hasListeners())
			return;
		
		withExceptionHandlingInCallback(new Runnable() {
        	public void run() {
        		_committed.trigger(new CommitEventArgs(transaction, objectInfoCollections, isOwnCommit));
        	}
        });
	}
	
	public void closeOnStarted(final ObjectContainer container) {
		if (!_closing.hasListeners())
			return;
		
		withExceptionHandlingInCallback(new Runnable() {
        	public void run() {
        		_closing.trigger(new ObjectContainerEventArgs(container));
        	}
        });
	}

	public void openOnFinished(final ObjectContainer container) {
		if (!_opened.hasListeners())
			return;
		
		withExceptionHandlingInCallback(new Runnable() {
        	public void run() {
        		_opened.trigger(new ObjectContainerEventArgs(container));
        	}
        });
	}

	
	public Event4 queryFinished() {
		return _queryFinished;
	}

	public Event4 queryStarted() {
		return _queryStarted;
	}

	public Event4 creating() {
		return _creating;
	}

	public Event4 activating() {
		return _activating;
	}

	public Event4 updating() {
		return _updating;
	}

	public Event4 deleting() {
		return _deleting;
	}

	public Event4 deactivating() {
		return _deactivating;
	}

	public Event4 created() {
		return _created;
	}

	public Event4 activated() {
		return _activated;
	}

	public Event4 updated() {
		return _updated;
	}

	public Event4 deleted() {
		return _deleted;
	}

	public Event4 deactivated() {
		return _deactivated;
	}
	
	public Event4 committing() {
		return _committing;
	}
	
	/**
	 * @sharpen.event.onAdd onCommittedListenerAdded
	 */
	public Event4 committed() {
		return _committed;
	}

	public Event4 classRegistered() {
		return _classRegistered;
	}

	public Event4 instantiated() {
		return _instantiated;
	}
	
	public Event4 closing() {
		return _closing;
	}
	
	protected void onCommittedListenerAdded() {
		// do nothing 
	}

	public boolean caresAboutCommitting() {
		return _committing.hasListeners();
	}

	public boolean caresAboutCommitted() {
		return _committed.hasListeners();
	}
	
    public boolean caresAboutDeleting() {
        return _deleting.hasListeners();
    }

    public boolean caresAboutDeleted() {
        return _deleted.hasListeners();
    }

	boolean triggerCancellableObjectEventArgsInCallback(final Transaction transaction, final Event4Impl<CancellableObjectEventArgs> e, final ObjectInfo objectInfo, final Object o) {
		if (!e.hasListeners())
			return true;
		
		final CancellableObjectEventArgs args = new CancellableObjectEventArgs(transaction, objectInfo, o);
		withExceptionHandlingInCallback(new Runnable() {
        	public void run() {
        		e.trigger(args);
        	}
        });		
    	return !args.isCancelled();
    }

	void triggerObjectInfoEventInCallback(final Transaction transaction, final Event4Impl<ObjectInfoEventArgs> e, final ObjectInfo o) {
		if (!e.hasListeners())
			return;
		
    	withExceptionHandlingInCallback(new Runnable() {
        	public void run() {
        		e.trigger(new ObjectInfoEventArgs(transaction, o));
        	}
        });
    }

	private void withExceptionHandlingInCallback(final Runnable runnable) {
	    try {
        	InCallback.run(runnable);
	    } catch (Db4oException e) {
	    	throw e;
        } catch (Throwable x) {
        	throw new EventException(x);
        }
    }
	
	private void withExceptionHandling(final Runnable runnable) {
	    try {
        	runnable.run();
	    } catch (Db4oException e) {
	    	throw e;
        } catch (Throwable x) {
        	throw new EventException(x);
        }
    }

	public Event4<ObjectContainerEventArgs> opened() {
		return _opened;
	}
}
