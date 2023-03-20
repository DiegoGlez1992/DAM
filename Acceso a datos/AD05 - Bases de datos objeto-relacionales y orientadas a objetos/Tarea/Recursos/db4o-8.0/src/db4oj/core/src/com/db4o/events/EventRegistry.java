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
package com.db4o.events;

/**
 * Provides the interface to register event handlers for  
 * {@link com.db4o.ObjectContainer} events.<br>
 * EventRegistry methods represent events available for registering callbacks.
 * An EventRegistry instance can be obtained from the {@link EventRegistryFactory}.
 * <code>EventRegistry registry =  EventRegistryFactory.forObjectContainer(container);</code>
 * A new callback can be registered for an event with the following code:
 * <code>registry.created().addListener(new EventListener4(){...});</code>
 * @see EventRegistryFactory
 * @see EventListener4
 */
public interface EventRegistry {
	
	/**
	 * This event is fired upon a query start and can be used to gather 
	 * query statistics. 
	 * The query object is available from  {@link QueryEventArgs}
	 * event parameter.<br>
	 * @sharpen.event com.db4o.events.QueryEventArgs
	 * @return event
	 * @see QueryEventArgs
	 */
	public Event4<QueryEventArgs> queryStarted();
	
	/**
	 * This event is fired upon a query end and can be used to gather 
	 * query statistics.  
	 * The query object is available from  {@link QueryEventArgs}
	 * event parameter.<br>
	 * @sharpen.event com.db4o.events.QueryEventArgs
	 * @return event
	 * @see QueryEventArgs
	 */
	public Event4<QueryEventArgs> queryFinished();

	/**
	 * This event is fired before an object is saved for the first time.
	 * The object can be obtained from {@link CancellableObjectEventArgs}
	 * event parameter. The action can be cancelled using 
	 * {@link CancellableObjectEventArgs#cancel()}
	 * @sharpen.event com.db4o.events.CancellableObjectEventArgs
	 * @return event
	 * @see CancellableObjectEventArgs
	 * @see com.db4o.ObjectContainer#store(Object)
	 */
	public Event4<CancellableObjectEventArgs> creating();

	/**
	 * 	This event is fired before an object is activated.
	 * The object can be obtained from {@link CancellableObjectEventArgs}
	 * event parameter. The action can be cancelled using 
	 * {@link CancellableObjectEventArgs#cancel()}
	 * @sharpen.event com.db4o.events.CancellableObjectEventArgs
	 * @return event
	 * 	@see CancellableObjectEventArgs
	 * @see com.db4o.ObjectContainer#activate(Object, int)
	 */
	public Event4<CancellableObjectEventArgs> activating();
	
	/**
	 * This event is fired before an object is updated.
	 * The object can be obtained from {@link CancellableObjectEventArgs}
	 * event parameter. The action can be cancelled using 
	 * {@link CancellableObjectEventArgs#cancel()}
	 *
	 * @sharpen.event com.db4o.events.CancellableObjectEventArgs
	 * @return event
	 * @see CancellableObjectEventArgs
	 * @see com.db4o.ObjectContainer#store(Object)
	 */
	public Event4<CancellableObjectEventArgs> updating();
	
	/**
	 * This event is fired before an object is deleted.
	 * The object can be obtained from {@link CancellableObjectEventArgs}
	 * event parameter. The action can be cancelled using 
	 * {@link CancellableObjectEventArgs#cancel()}<br><br>
	 * Note, that this event is not available in networked client/server
	 * mode and will throw an exception when attached to a client ObjectContainer.
	 *
	 * @sharpen.event com.db4o.events.CancellableObjectEventArgs
	 * @return event
	 * @see CancellableObjectEventArgs
	 * @see com.db4o.ObjectContainer#delete(Object)
	 */
	public Event4<CancellableObjectEventArgs> deleting();
	
	/**
	 * This event is fired before an object is deactivated.
	 * The object can be obtained from {@link CancellableObjectEventArgs}
	 * event parameter. The action can be cancelled using 
	 * {@link CancellableObjectEventArgs#cancel()}
	 * 
	 * @sharpen.event com.db4o.events.CancellableObjectEventArgs
	 * @return event
	 * @see CancellableObjectEventArgs
	 * @see com.db4o.ObjectContainer#deactivate(Object, int)
	 */
	public Event4 deactivating();

	/**
	 * This event is fired after an object is activated.
	 * The object can be obtained from the {@link ObjectInfoEventArgs}
	 * event parameter. <br><br>
	 * The event can be used to trigger some post-activation 
	 * functionality.
	 * 
	 * @sharpen.event com.db4o.events.ObjectInfoEventArgs
	 * @return event
	 * @see ObjectInfoEventArgs
	 * @see com.db4o.ObjectContainer#activate(Object, int)
	 */
	public Event4<ObjectInfoEventArgs> activated();

	/**
	 * This event is fired after an object is created (saved for the first time).
	 * The object can be obtained from the {@link ObjectInfoEventArgs}
	 * event parameter.<br><br>
	 * The event can be used to trigger some post-creation
	 * functionality.
	 * 
	 * @sharpen.event com.db4o.events.ObjectInfoEventArgs
	 * @return event
	 * @see ObjectEventArgs
	 * @see com.db4o.ObjectContainer#store(Object)
	 */
	public Event4<ObjectInfoEventArgs> created();

	/**
	 * This event is fired after an object is updated.
	 * The object can be obtained from the {@link ObjectInfoEventArgs}
	 * event parameter.<br><br>
	 * The event can be used to trigger some post-update
	 * functionality.
	 * 
	 * @sharpen.event com.db4o.events.ObjectInfoEventArgs
	 * @return event
	 * @see ObjectInfoEventArgs
	 * @see com.db4o.ObjectContainer#store(Object)
	 */
	public Event4<ObjectInfoEventArgs> updated();

	/**
	 * This event is fired after an object is deleted.
	 * The object can be obtained from the {@link ObjectInfoEventArgs}
	 * event parameter.<br><br>
	 * The event can be used to trigger some post-deletion
	 * functionality.<br><br>
	 * Note, that this event is not available in networked client/server
	 * mode and will throw an exception when attached to a client ObjectContainer.
	 *  
	 * @sharpen.event com.db4o.events.ObjectInfoEventArgs
	 * @return event
	 * @see ObjectEventArgs
	 * @see com.db4o.ObjectContainer#delete(Object)
	 */
	public Event4<ObjectInfoEventArgs> deleted();

	/**
	 * This event is fired after an object is deactivated.
	 * The object can be obtained from the {@link ObjectInfoEventArgs}
	 * event parameter.<br><br>
	 * The event can be used to trigger some post-deactivation
	 * functionality.
	 * 
	 * @sharpen.event com.db4o.events.ObjectInfoEventArgs
	 * @return event
	 * @see ObjectEventArgs
	 * @see com.db4o.ObjectContainer#delete(Object)
	 */
	public Event4<ObjectInfoEventArgs> deactivated();
	
	/**
	 * This event is fired just before a transaction is committed.
	 * The transaction and a list of the modified objects can 
	 * be obtained from the {@link CommitEventArgs}
	 * event parameter.<br><br>
	 * Committing event gives a user a chance to interrupt the commit
	 * and rollback the transaction.
	 * 
	 * @sharpen.event com.db4o.events.CommitEventArgs
	 * @return event
	 * @see CommitEventArgs
	 * @see com.db4o.ObjectContainer#commit()
	 */
	public Event4<CommitEventArgs> committing();
	
	/**
	 * This event is fired after a transaction has been committed.
	 * The transaction and a list of the modified objects can 
	 * be obtained from the {@link CommitEventArgs}
	 * event parameter.<br><br>
	 * The event can be used to trigger some post-commit functionality.
	 * 
	 * @sharpen.event com.db4o.events.CommitEventArgs
	 * @return event
	 * @see CommitEventArgs
	 * @see com.db4o.ObjectContainer#commit()
	 */
	public Event4<CommitEventArgs> committed();

	/**
	 * This event is fired when a persistent object is instantiated. 
	 * The object can be obtained from the {@link ObjectInfoEventArgs}
	 * event parameter.
	 * 
	 * @sharpen.event com.db4o.events.ObjectInfoEventArgs
	 * @return event
	 * @see ObjectInfoEventArgs
	 */
	public Event4<ObjectInfoEventArgs> instantiated();

	/**
	 * This event is fired when a new class is registered with metadata.
	 * The class information can be obtained from {@link ClassEventArgs}
	 * event parameter.
	 * 
	 * @sharpen.event com.db4o.events.ClassEventArgs
	 * @return event
	 * @see ClassEventArgs
	 */
	public Event4<ClassEventArgs> classRegistered();
	
	/**
	 * This event is fired when the {@link com.db4o.ObjectContainer#close} is
	 * called.
	 * 
	 * @sharpen.event com.db4o.events.ObjectContainerEventArgs
	 * @return event
	 */
	public Event4<ObjectContainerEventArgs> closing();

	/**
	 * This event is fired when the {@link com.db4o.ObjectContainer} has
	 * finished its startup procedure.
	 * 
	 * @sharpen.event com.db4o.events.ObjectContainerEventArgs
	 * @return event
	 */
	public Event4<ObjectContainerEventArgs> opened();
}
