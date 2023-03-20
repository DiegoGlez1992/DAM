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
package com.db4o.ta.instrumentation.test.integration;import java.util.*;class Project {		List _subProjects = new com.db4o.db4ounit.common.ta.collections.PagedList();		List _workLog = new com.db4o.db4ounit.common.ta.collections.PagedList();		String _name;		public Project(String name) {		_name = name;	}	public void logWorkDone(UnitOfWork work) {		_workLog.add(work);	}	public long totalTimeSpent() {				long total = 0;		for (Iterator iter = _workLog.iterator(); iter.hasNext();) {			UnitOfWork item = (UnitOfWork) iter.next();			total += item.timeSpent();		}		return total;	}
	
	public boolean sameName(Project project) {
		return _name.equals(project._name);
	}}