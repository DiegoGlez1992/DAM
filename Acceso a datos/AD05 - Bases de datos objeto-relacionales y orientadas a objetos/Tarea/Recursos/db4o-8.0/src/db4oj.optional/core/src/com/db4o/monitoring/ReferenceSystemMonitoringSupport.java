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
package com.db4o.monitoring;

import java.util.*;

import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.references.*;
import com.db4o.monitoring.internal.*;

/**
 * Publishes statistics about the ReferenceSystem to JMX.
 */
@decaf.Ignore
public class ReferenceSystemMonitoringSupport implements ConfigurationItem {

	private final static class MonitoringSupportReferenceSystemFactory implements ReferenceSystemFactory, DeepClone {
		
		private final HashMap<String, com.db4o.monitoring.ReferenceSystem> _mBeans;

		public MonitoringSupportReferenceSystemFactory() {
			this(new HashMap<String, ReferenceSystem>());
		}
		
		private MonitoringSupportReferenceSystemFactory(HashMap<String, ReferenceSystem> mBeans) {
			_mBeans = mBeans;
		}


		public com.db4o.internal.references.ReferenceSystem newReferenceSystem(InternalObjectContainer container) {
			return new MonitoringReferenceSystem(mBeanFor(container));
		}

		private ReferenceSystemListener mBeanFor(InternalObjectContainer container) {
			com.db4o.monitoring.ReferenceSystem mBean = _mBeans.get(container.toString());
			if(mBean == null){
				mBean = Db4oMBeans.newReferenceSystemMBean(container);
				_mBeans.put(container.toString(), mBean);
			}
			return mBean;
		}

		public Object deepClone(Object context) {
			return new MonitoringSupportReferenceSystemFactory(new HashMap<String, ReferenceSystem>(_mBeans));
		}

	}

	public void apply(InternalObjectContainer container) {
		
	}

	public void prepare(Configuration configuration) {
		((Config4Impl)configuration).referenceSystemFactory(new MonitoringSupportReferenceSystemFactory());
	}

}
