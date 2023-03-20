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
package db4ounit.extensions;

import java.util.*;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.internal.*;

import db4ounit.fixtures.*;

public interface Db4oFixture extends Labeled {
    
	void open(Db4oTestCase testInstance) throws Exception;
    
	void close() throws Exception;
	
	void reopen(Db4oTestCase testInstance) throws Exception;
    
    void clean();
    
	LocalObjectContainer fileSession();
	
	ExtObjectContainer db();
	
	Configuration config();
	
	boolean accept(Class clazz);

	void defragment() throws Exception;

	void configureAtRuntime(RuntimeConfigureAction action);

	void fixtureConfiguration(FixtureConfiguration configuration);

	void resetConfig();

	List<Throwable> uncaughtExceptions();

}
