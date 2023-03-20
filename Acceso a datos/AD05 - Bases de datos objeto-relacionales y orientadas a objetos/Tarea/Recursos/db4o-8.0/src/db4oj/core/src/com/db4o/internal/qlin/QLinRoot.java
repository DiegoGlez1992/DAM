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
package com.db4o.internal.qlin;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.query.*;
import com.db4o.internal.query.processor.*;
import com.db4o.internal.query.result.*;
import com.db4o.qlin.*;
import com.db4o.query.*;

import static com.db4o.qlin.QLinSupport.*;

/**
 * @exclude
 */
public class QLinRoot<T> extends QLinSodaNode<T>{
	
	private final QQuery _query;
	
	private int _limit = -1;

	public QLinRoot(Query query, Class<T> clazz) {
		_query = (QQuery) query;
		query.constrain(clazz);
		context(clazz);
	}
	
	public Query query(){
		return _query;
	}

	public ObjectSet<T> select() {
		if(_limit == -1){
			return _query.execute();
		}
		QueryResult queryResult = _query.getQueryResult();
		IdListQueryResult limitedResult = new IdListQueryResult(_query.transaction(), _limit);
		int counter = 0;
		IntIterator4 i = queryResult.iterateIDs();
		while(i.moveNext()){
			if(counter++ >= _limit){
				break;
			}
			limitedResult.add(i.currentInt());
		}
		return new ObjectSetFacade(limitedResult);
	}
	
	public QLin<T> limit(int size){
		if(size < 1){
			throw new QLinException("Limit must be greater that 0");
		}
		_limit = size;
		return this;
	}


	@Override
	protected QLinRoot<T> root() {
		return this;
	}

	Query descend(Object expression) {
		// TODO: Implement deep descend
		return query().descend(field(expression).getName());
	}

}
