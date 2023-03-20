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

public class NullCallbacks implements Callbacks {

	public void queryOnFinished(Transaction transaction, Query query) {
	}

	public void queryOnStarted(Transaction transaction, Query query) {
	}

	public boolean objectCanNew(Transaction transaction, Object obj) {
		return true;
	}

	public boolean objectCanActivate(Transaction transaction, Object obj) {
		return true;
	}
	
	public boolean objectCanUpdate(Transaction transaction, ObjectInfo objectInfo) {
		return true;
	}
	
	public boolean objectCanDelete(Transaction transaction, ObjectInfo objectInfo) {
		return true;
	}
	
	public boolean objectCanDeactivate(Transaction transaction, ObjectInfo objectInfo) {
		return true;
	}
	
	public void objectOnNew(Transaction transaction, ObjectInfo obj) {
	}
	
	public void objectOnActivate(Transaction transaction, ObjectInfo obj) {
	}

	public void objectOnUpdate(Transaction transaction, ObjectInfo obj) {
	}

	public void objectOnDelete(Transaction transaction, ObjectInfo obj) {
	}

	public void objectOnDeactivate(Transaction transaction, ObjectInfo obj) {	
	}

	public void objectOnInstantiate(Transaction transaction, ObjectInfo obj) {
	}

	public void commitOnStarted(Transaction transaction, CallbackObjectInfoCollections objectInfoCollections) {
	}
	
	public void commitOnCompleted(Transaction transaction, CallbackObjectInfoCollections objectInfoCollections, boolean isOwnCommit) {
	}

	public boolean caresAboutCommitting() {
		return false;
	}

	public boolean caresAboutCommitted() {
		return false;
	}

	public void classOnRegistered(ClassMetadata clazz) {
	}

    public boolean caresAboutDeleting() {
        return false;
    }

    public boolean caresAboutDeleted() {
        return false;
    }

	public void closeOnStarted(ObjectContainer container) {
	}

	public void openOnFinished(ObjectContainer container) {
	}
}
