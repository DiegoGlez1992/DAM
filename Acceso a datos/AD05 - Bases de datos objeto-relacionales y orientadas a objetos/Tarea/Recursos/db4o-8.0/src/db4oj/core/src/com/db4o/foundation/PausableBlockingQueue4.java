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

public interface PausableBlockingQueue4<T> extends BlockingQueue4<T> {

	/**
	 * <p>
	 * Pauses the queue, making calls to {@link BlockingQueue4#next()} block
	 * until {@link PausableBlockingQueue4#resume()} is called.
	 * 
	 * @return whether or not this call changed the state of the queue.
	 */
	boolean pause();

	/**
	 * <p>
	 * Resumes the queue, releasing blocked calls to
	 * {@link BlockingQueue4#next()} that can reach a next queue item..
	 * 
	 * @return whether or not this call changed the state of the queue.
	 */
	boolean resume();

	boolean isPaused();

	/**
	 * <p>
	 * Returns the next element in queue if there is one available, returns null
	 * otherwise.
	 * <p>
	 * This method will not never block, regardless of the queue being paused or
	 * no elements are available.
	 * 
	 * @return next element, if available and queue not paused.
	 */
	T tryNext();

}
