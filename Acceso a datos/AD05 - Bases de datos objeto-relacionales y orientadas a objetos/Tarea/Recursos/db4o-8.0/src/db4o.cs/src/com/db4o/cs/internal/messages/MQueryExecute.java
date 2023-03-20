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

import com.db4o.cs.internal.objectexchange.*;
import com.db4o.foundation.*;
import com.db4o.internal.query.processor.*;
import com.db4o.internal.query.result.*;

public final class MQueryExecute extends MsgQuery implements MessageWithResponse {
	
	public Msg replyFromServer() {
		unmarshall(_payLoad._offset);
		final ObjectByRef<Msg> result = new ObjectByRef();
		container().withTransaction(transaction(), new Runnable() { public void run() {
			
			final QQuery query = unmarshallQuery();
			result.value = writeQueryResult(executeFully(query), query.evaluationMode(), new ObjectExchangeConfiguration(query.prefetchDepth(), query.prefetchCount()));
			
		}});
		return result.value;
	}

	private QQuery unmarshallQuery() {
	    // TODO: The following used to run outside of the
        // Synchronization block for better performance but
        // produced inconsistent results, cause unknown.

        QQuery query = (QQuery) readObjectFromPayLoad();
        query.unmarshall(transaction());
	    return query;
    }

	private AbstractQueryResult executeFully(final QQuery query) {
		return query.triggeringQueryEvents(new Closure4<AbstractQueryResult>() { public AbstractQueryResult run() {
			AbstractQueryResult qr = newQueryResult(query.evaluationMode());
			qr.loadFromQuery(query);
			return qr;
		}});
	}
	
}