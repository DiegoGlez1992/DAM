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
package com.db4o.foundation;


public interface BlockingQueue4<T> extends Queue4<T> {

	/**
	 * <p>
	 * Returns the next queued item or waits for it to be available for the
	 * maximum of <code>timeout</code> miliseconds.
	 * 
	 * @param timeout
	 *            maximum time to wait for the next avilable item in miliseconds
	 * @return the next item or <code>null</code> if <code>timeout</code> is
	 *         reached
	 * @throws BlockingQueueStoppedException
	 *             if the {@link BlockingQueue4#stop()} is called.
	 */
	T next(final long timeout) throws BlockingQueueStoppedException;

	void stop();

	/**
	 * <p>
	 * Removes all the available elements in the queue to the colletion passed
	 * as argument.
	 * <p>
	 * It will block until at least one element is available.
	 * 
	 * @param list
	 * @return the number of elements added to the list.
	 * @throws BlockingQueueStoppedException
	 *             if the {@link BlockingQueue4#stop()} is called.
	 */
	int drainTo(Collection4<T> list) throws BlockingQueueStoppedException;

}
