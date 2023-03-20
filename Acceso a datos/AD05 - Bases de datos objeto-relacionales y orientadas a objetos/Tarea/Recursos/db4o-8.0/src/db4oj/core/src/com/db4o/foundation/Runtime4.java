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

/**
 * A collection of static methods that should be part of the runtime environment but are not.
 * 
 * @exclude
 */
public class Runtime4 {

	/**
	 * sleeps without checked exceptions
	 */
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (Exception ignored) {
			
   		}
	}
	
	/**
	 * sleeps with implicit exception
	 */
	public static void sleepThrowsOnInterrupt(long millis) throws RuntimeInterruptedException {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RuntimeInterruptedException(e.toString());
   		}
	}
	
	/**
	 * Keeps executing a block of code until it either returns true or millisecondsTimeout
	 * elapses.
	 */
	public static boolean retry(long millisecondsTimeout, Closure4<Boolean> block) {
		return retry(millisecondsTimeout, 1, block);
	}
	
	
	public static boolean retry(long millisecondsTimeout, int millisecondsBetweenRetries, Closure4<Boolean> block) {
		final StopWatch watch = new AutoStopWatch();
		do {
			if (block.run()) {
				return true;
			}
			sleep(millisecondsBetweenRetries);
		} while (watch.peek() < millisecondsTimeout);
		return false;
	}


}
