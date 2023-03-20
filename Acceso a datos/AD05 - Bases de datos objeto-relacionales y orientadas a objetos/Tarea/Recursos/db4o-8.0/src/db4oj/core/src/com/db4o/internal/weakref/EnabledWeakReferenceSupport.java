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
package com.db4o.internal.weakref;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;

class EnabledWeakReferenceSupport implements WeakReferenceSupport {
   
	private final Object _queue;
    private final ObjectContainerBase _container;
    private SimpleTimer _timer;
    
    EnabledWeakReferenceSupport(ObjectContainerBase container) {
        _container = container;
        _queue = Platform4.createReferenceQueue();
    }

    public Object newWeakReference(ObjectReference referent, Object obj) {
        return Platform4.createActiveObjectReference(_queue, referent, obj);
    }

    public void purge() {
        Platform4.pollReferenceQueue(_container, _queue);
    }
    
    public void start() {
    	if (_timer != null) {
    		return;
    	}
    	
        if(! _container.configImpl().weakReferences()){
            return;
        }
    	
        if (_container.configImpl().weakReferenceCollectionInterval() <= 0) {
        	return;
        }
        
        _timer = new SimpleTimer(new Collector(), _container.configImpl().weakReferenceCollectionInterval());
        _container.threadPool().start("db4o WeakReference collector", _timer);
    }

    /* (non-Javadoc)
	 * @see com.db4o.internal.WeakReferenceSupport#stopTimer()
	 */
    public void stop() {
    	if (_timer == null){
            return;
        }
        _timer.stop();
        _timer = null;
    }
    
    private final class Collector implements Runnable {
		public void run() {
			try {
				purge();
			} catch (DatabaseClosedException dce) {
				// can happen, no stack trace
			} catch (Exception e) {
				// don't bring down the thread
				e.printStackTrace();
			}
		}
	}

    
}