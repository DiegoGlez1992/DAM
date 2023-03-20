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
package com.db4o.cs.internal.config;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.cs.internal.*;
import com.db4o.cs.internal.messages.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.query.*;

/**
 * @exclude
 * @deprecated Since 8.0
 * @sharpen.ignore
 */
public class DotNetSupportClientServer implements ConfigurationItem {

	public void apply(InternalObjectContainer container) {
		// do nothing.
	}

	public void prepare(Configuration config) {
		config.addAlias(new TypeAlias("System.Exception, mscorlib", ChainedRuntimeException.class.getName()));
		
		//		config.addAlias(new TypeAlias("java.lang.Throwable", FullTypeNameFor(typeof(Exception))));
		//		config.addAlias(new TypeAlias("java.lang.RuntimeException", FullTypeNameFor(typeof(Exception))));
		//		config.addAlias(new TypeAlias("java.lang.Exception", FullTypeNameFor(typeof(Exception))));
		
		
		config.addAlias(new TypeAlias("Db4objects.Db4o.Query.IEvaluation, Db4objects.Db4o", Evaluation.class.getName()));
		config.addAlias(new TypeAlias("Db4objects.Db4o.Query.ICandidate, Db4objects.Db4o", Candidate.class.getName()));
		
		config.addAlias(new WildcardAlias("Db4objects.Db4o.Internal.Query.Processor.*, Db4objects.Db4o", "com.db4o.internal.query.processor.*"));

		config.addAlias(new TypeAlias("Db4objects.Db4o.Foundation.Collection4, Db4objects.Db4o", Collection4.class.getName()));
		config.addAlias(new TypeAlias("Db4objects.Db4o.Foundation.List4, Db4objects.Db4o", List4.class.getName()));
		config.addAlias(new TypeAlias("Db4objects.Db4o.User, Db4objects.Db4o", User.class.getName()));

		config.addAlias(new TypeAlias("Db4objects.Db4o.CS.Internal.ClassInfo, Db4objects.Db4o.CS", ClassInfo.class.getName()));
		config.addAlias(new TypeAlias("Db4objects.Db4o.CS.Internal.FieldInfo, Db4objects.Db4o.CS", FieldInfo.class.getName()));
		
		config.addAlias(
				new TypeAlias(
						"Db4objects.Db4o.CS.Internal.Messages.MUserMessage+UserMessagePayload, Db4objects.Db4o.CS", 
						MUserMessage.UserMessagePayload.class.getName()));
		
		config.addAlias(new WildcardAlias("Db4objects.Db4o.CS.Internal.Messages.*, Db4objects.Db4o.CS", "com.db4o.cs.internal.messages.*"));

		
	}

}
