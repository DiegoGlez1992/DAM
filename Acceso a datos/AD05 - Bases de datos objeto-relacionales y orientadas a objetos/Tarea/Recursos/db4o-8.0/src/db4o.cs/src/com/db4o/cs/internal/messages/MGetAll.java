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
package com.db4o.cs.internal.messages;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.cs.internal.objectexchange.*;
import com.db4o.foundation.*;
import com.db4o.internal.query.processor.*;
import com.db4o.internal.query.result.*;

public final class MGetAll extends MsgQuery implements MessageWithResponse {
	
	public final Msg replyFromServer() {
		QueryEvaluationMode evaluationMode = QueryEvaluationMode.fromInt(readInt());
		int prefetchDepth = readInt();
		int prefetchCount = readInt();
		synchronized(containerLock()) {
			return writeQueryResult(getAll(evaluationMode), evaluationMode, new ObjectExchangeConfiguration(prefetchDepth, prefetchCount));
		}
	}

	private AbstractQueryResult getAll(final QueryEvaluationMode mode) {
		return newQuery(mode).triggeringQueryEvents(new Closure4<AbstractQueryResult>() { public AbstractQueryResult run() {
			try {
				return localContainer().getAll(transaction(), mode);
			} catch (Exception e) {
				if(Debug4.atHome){
					e.printStackTrace();
				}
			}
			return newQueryResult(mode);
		}});
	}

	private QQuery newQuery(final QueryEvaluationMode mode) {
		QQuery query = (QQuery)localContainer().query();
		query.evaluationMode(mode);
		return query;
	}
	
}