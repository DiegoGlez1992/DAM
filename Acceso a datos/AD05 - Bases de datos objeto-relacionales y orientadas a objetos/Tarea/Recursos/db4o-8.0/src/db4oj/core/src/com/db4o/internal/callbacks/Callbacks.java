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
package com.db4o.internal.callbacks;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.query.*;

public interface Callbacks {

	boolean objectCanNew(Transaction transaction, Object obj);
	boolean objectCanActivate(Transaction transaction, Object obj);
	boolean objectCanUpdate(Transaction transaction, ObjectInfo objectInfo);
	boolean objectCanDelete(Transaction transaction, ObjectInfo objectInfo);
	boolean objectCanDeactivate(Transaction transaction, ObjectInfo objectInfo);

	void objectOnActivate(Transaction transaction, ObjectInfo obj);
	void objectOnNew(Transaction transaction, ObjectInfo obj);
	void objectOnUpdate(Transaction transaction, ObjectInfo obj);
	void objectOnDelete(Transaction transaction, ObjectInfo obj);
	void objectOnDeactivate(Transaction transaction, ObjectInfo obj);
	void objectOnInstantiate(Transaction transaction, ObjectInfo obj);

	void queryOnStarted(Transaction transaction, Query query);
	void queryOnFinished(Transaction transaction, Query query);
	
	boolean caresAboutCommitting();
	boolean caresAboutCommitted();
	
	void classOnRegistered(ClassMetadata clazz);
	
	void commitOnStarted(Transaction transaction, CallbackObjectInfoCollections objectInfoCollections);
	void commitOnCompleted(Transaction transaction, CallbackObjectInfoCollections objectInfoCollections, boolean isOwnCommit);

    boolean caresAboutDeleting();
    boolean caresAboutDeleted();
    
    void openOnFinished(ObjectContainer container);
    void closeOnStarted(ObjectContainer container);
}
