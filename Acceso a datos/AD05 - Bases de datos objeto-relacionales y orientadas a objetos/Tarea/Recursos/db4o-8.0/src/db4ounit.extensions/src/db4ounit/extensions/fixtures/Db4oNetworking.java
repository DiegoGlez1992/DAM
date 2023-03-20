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

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.cs.config.*;
import com.db4o.cs.internal.config.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.internal.threading.*;

import db4ounit.extensions.*;
import db4ounit.extensions.util.*;

/**
 * @sharpen.if !SILVERLIGHT
 */
public class Db4oNetworking extends
		AbstractDb4oFixture implements Db4oClientServerFixture {
    
	private static final int THREADPOOL_TIMEOUT = 3000;

	protected static final String FILE = "Db4oClientServer.db4o";
    
    public static final String HOST = "127.0.0.1";

    public static final String USERNAME = "db4o";

    public static final String PASSWORD = USERNAME;

    private ObjectServer _server;

    private final File _file;

	private ExtObjectContainer _objectContainer;
	
	private String _label;
    
    private int _port;

	private Configuration _serverConfig;
	
	private final ClientServerFactory _csFactory;
	
	public Db4oNetworking(ClientServerFactory csFactory, String label) {
		_csFactory = csFactory != null ? csFactory : defaultClientServerFactory();
		_file = new File(filePath());
        _label = label;
	}

	private ClientServerFactory defaultClientServerFactory() {
	    return new StandardClientServerFactory();
    }     
    
    public Db4oNetworking(String label){
    	this(null, label);
    }
    
    public Db4oNetworking() {
    	this("C/S");
    }
    
    public void open(Db4oTestCase testInstance) throws Exception {
		openServerFor(testInstance);
		openClientFor(testInstance);
		
		listenToUncaughtExceptions();
	}

	private void listenToUncaughtExceptions() {
		listenToUncaughtExceptions(serverThreadPool());
		
		final ThreadPool4 clientThreadPool = clientThreadPool();
		if (null != clientThreadPool) {
			listenToUncaughtExceptions(clientThreadPool);
		}
		
    }

	private ThreadPool4 clientThreadPool() {
		return threadPoolFor(_objectContainer);
	}

	private ThreadPool4 serverThreadPool() {
		return threadPoolFor(_server.ext().objectContainer());
	}

	private void openClientFor(Db4oTestCase testInstance) throws Exception {
	    final Configuration config = clientConfigFor(testInstance);
		_objectContainer = openClientWith(config);
    }

	private Configuration clientConfigFor(Db4oTestCase testInstance) throws Exception {

        if (requiresCustomConfiguration(testInstance)) {
        	final Configuration customServerConfig = newConfiguration();
			((CustomClientServerConfiguration)testInstance).configureClient(customServerConfig);
			return customServerConfig;
        }
        
	    final Configuration config = cloneConfiguration();
		applyFixtureConfiguration(testInstance, config);
	    return config;
    }

	private ExtObjectContainer openSocketClient(final Configuration config) {
	    return _csFactory.openClient(asClientConfiguration(config), HOST, _port, USERNAME, PASSWORD).ext();
    }

	public ExtObjectContainer openNewSession(Db4oTestCase testInstance) throws Exception {
	    final Configuration config = clientConfigFor(testInstance);		
		return openClientWith(config);
	}

	private ExtObjectContainer openClientWith(final Configuration config) {
	    return openSocketClient(config);
    }

	private void openServerFor(Db4oTestCase testInstance) throws Exception {
        _serverConfig = serverConfigFor(testInstance);
		_server = _csFactory.openServer(asServerConfiguration(_serverConfig),_file.getAbsolutePath(), -1);
        _port = _server.ext().port();
        _server.grantAccess(USERNAME, PASSWORD);
    }

	private Configuration serverConfigFor(Db4oTestCase testInstance) throws Exception {
		
        if (requiresCustomConfiguration(testInstance)) {
        	final Configuration customServerConfig = newConfiguration();
			((CustomClientServerConfiguration)testInstance).configureServer(customServerConfig);
			return customServerConfig;
        }
        
        return cloneConfiguration();
    }

	private boolean requiresCustomConfiguration(Db4oTestCase testInstance) {
		if (testInstance instanceof CustomClientServerConfiguration) {
			return true;
		}
		return false;
	}
    
    public void close() throws Exception {
		if (null != _objectContainer) {
			ThreadPool4 clientThreadPool = clientThreadPool();
			
			_objectContainer.close();
			_objectContainer = null;
			
			if (null != clientThreadPool) {
				clientThreadPool.join(THREADPOOL_TIMEOUT);
			}
		}
		closeServer();
	}

    private void closeServer() throws Exception {
    	if (null != _server) {
    		ThreadPool4 serverThreadPool = serverThreadPool();
	        _server.close();
	        _server = null;
			
	        if (null != serverThreadPool) {
	        	serverThreadPool.join(THREADPOOL_TIMEOUT);
	        }
    	}
    }	

	public ExtObjectContainer db() {
		return _objectContainer;
	}
    
    protected void doClean() {
        _file.delete();
    }
    
    public ObjectServer server() {
    	return _server;
    }
    
    /**
	 * Does not accept a clazz which is assignable from OptOutCS, or not
	 * assignable from Db4oTestCase.
	 * 
	 * @return returns false if the clazz is assignable from OptOutCS, or not
	 *         assignable from Db4oTestCase. Otherwise, returns true.
	 */
	public boolean accept(Class clazz) {
		if (!Db4oTestCase.class.isAssignableFrom(clazz)) {
			return false;
		}
		if (OptOutMultiSession.class.isAssignableFrom(clazz)) {
			return false;
		}
		if (OptOutNetworkingCS.class.isAssignableFrom(clazz)) {
			return false;
		}
		return true;
	}
    
	public LocalObjectContainer fileSession() {
		return (LocalObjectContainer)_server.ext().objectContainer();
	}
	
	public void defragment() throws Exception {
		defragment(filePath());
	}
	
	public String label() {
		return buildLabel(_label);
	}

	public int serverPort() {
		return _port;
	}

	private static String filePath() {
		return CrossPlatformServices.databasePath(FILE);
	}

	public void configureAtRuntime(RuntimeConfigureAction action) {
		action.apply(config());
		action.apply(_serverConfig);
	}

	private ClientConfiguration asClientConfiguration(Configuration serverConfig) {
		return Db4oClientServerLegacyConfigurationBridge.asClientConfiguration(serverConfig);
	}

	private ServerConfiguration asServerConfiguration(Configuration serverConfig) {
		return Db4oClientServerLegacyConfigurationBridge.asServerConfiguration(serverConfig);
	}
}
