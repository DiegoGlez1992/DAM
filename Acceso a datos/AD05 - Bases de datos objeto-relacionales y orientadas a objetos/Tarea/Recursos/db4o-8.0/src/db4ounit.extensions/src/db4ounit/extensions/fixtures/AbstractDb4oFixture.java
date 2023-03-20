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
package db4ounit.extensions.fixtures;

import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.defragment.*;
import com.db4o.events.*;
import com.db4o.internal.*;
import com.db4o.internal.threading.*;

import db4ounit.extensions.*;

public abstract class AbstractDb4oFixture implements Db4oFixture {

	private FixtureConfiguration _fixtureConfiguration;
	private Configuration _configuration;
	private List<Throwable> _uncaughtExceptions;

	protected AbstractDb4oFixture() {
		resetUncaughtExceptions();
	}

	private void resetUncaughtExceptions() {
	    _uncaughtExceptions = new ArrayList<Throwable>();
    }
	
	public void fixtureConfiguration(FixtureConfiguration fc) {
		_fixtureConfiguration = fc;
	}
	
	public List<Throwable> uncaughtExceptions() {
		return _uncaughtExceptions;
	}

	protected void listenToUncaughtExceptions(final ThreadPool4 threadPool) {
		if (null == threadPool)
			return; // mocks don't have thread pools
		
	    threadPool.uncaughtException().addListener(new EventListener4<UncaughtExceptionEventArgs>() {
	    	public void onEvent(Event4<UncaughtExceptionEventArgs> e, UncaughtExceptionEventArgs args) {
	    		_uncaughtExceptions.add(args.exception());
	        }
	    });
    }
	
	public void reopen(Db4oTestCase testInstance) throws Exception {
		close();
		open(testInstance);
	}

	public Configuration config() {
		if (_configuration == null) {
			_configuration = newConfiguration();
		}
		return _configuration;
	}
	
	public void clean() {
		doClean();
		resetConfig();
		resetUncaughtExceptions();
	}
	
	public abstract boolean accept(Class clazz);

	protected abstract void doClean();	
	
	public void resetConfig() {
		_configuration = null;
	}

	/**
	 * Method can be overridden in subclasses with special instantiation requirements (oSGI for instance).
	 * 
	 * @return
	 */
	protected Configuration newConfiguration() {
	    return Db4o.newConfiguration();
    }
	
	protected void defragment(String fileName) throws Exception{
        String targetFile = fileName + ".defrag.backup";
        DefragmentConfig defragConfig = new DefragmentConfig(fileName, targetFile);
        defragConfig.forceBackupDelete(true);
        defragConfig.db4oConfig(cloneConfiguration());
		com.db4o.defragment.Defragment.defrag(defragConfig);
	}
	
	protected String buildLabel(String label) {
		if (null == _fixtureConfiguration) return label;
		return label + " - " + _fixtureConfiguration.getLabel();
	}

	protected void applyFixtureConfiguration(Db4oTestCase testInstance, final Configuration config) {
		if (null == _fixtureConfiguration) return;
		_fixtureConfiguration.configure(testInstance, config);
	}
	
	public String toString() {
		return label();
	}

	protected Config4Impl cloneConfiguration() {
        return cloneDb4oConfiguration((Config4Impl) config());
    }

	protected Config4Impl cloneDb4oConfiguration(Configuration config) {
    	return (Config4Impl) ((Config4Impl)config).deepClone(this);
    }

	protected ThreadPool4 threadPoolFor(final ObjectContainer container) {
		if (container instanceof ObjectContainerBase) {
			return ((ObjectContainerBase)container).threadPool();
		}
		return null;
    }
}
