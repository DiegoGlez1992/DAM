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
package com.db4o.config;


/**
 * This class provides static constants for the query evaluation
 * modes that db4o supports. 
 * <br><br><b>For detailed documentation please see 
 * {@link QueryConfiguration#evaluationMode(QueryEvaluationMode)}</b> 
 */
public class QueryEvaluationMode {
	
	private final String _id;
	
	private QueryEvaluationMode(String id){
		_id = id;
	}
	
	/**
	 * Constant for immediate query evaluation. The query is executed fully
	 * when {@link com.db4o.query.Query#execute()} is called.
	 * <br><br><b>For detailed documentation please see 
	 * {@link QueryConfiguration#evaluationMode(QueryEvaluationMode)}</b> 
	 */
	public static final QueryEvaluationMode IMMEDIATE = new QueryEvaluationMode("IMMEDIATE");

	/**
	 * Constant for snapshot query evaluation. When {@link com.db4o.query.Query#execute()} is called,
	 * the query processor chooses the best indexes, does all index processing
	 * and creates a snapshot of the index at this point in time. Non-indexed
	 * constraints are evaluated lazily when the application iterates through 
	 * the {@link com.db4o.ObjectSet} resultset of the query.
	 * <br><br><b>For detailed documentation please see 
	 * {@link QueryConfiguration#evaluationMode(QueryEvaluationMode)}</b> 
	 */
	public static final QueryEvaluationMode SNAPSHOT = new QueryEvaluationMode("SNAPSHOT");
	
	/**
	 * Constant for lazy query evaluation. When {@link com.db4o.query.Query#execute()} is called, the
	 * query processor only chooses the best index and creates an iterator on 
	 * this index. Indexes and constraints are evaluated lazily when the 
	 * application iterates through the {@link com.db4o.ObjectSet} resultset of the query.
	 * <br><br><b>For detailed documentation please see 
	 * {@link QueryConfiguration#evaluationMode(QueryEvaluationMode)}</b> 
	 */
	public static final QueryEvaluationMode LAZY = new QueryEvaluationMode("LAZY");
	
	
    private static final QueryEvaluationMode[] MODES = new QueryEvaluationMode[] {
    	QueryEvaluationMode.IMMEDIATE,
    	QueryEvaluationMode.SNAPSHOT,
    	QueryEvaluationMode.LAZY,
    };
    
    /**
     * internal method, ignore please. 
     */
    public int asInt(){
    	for (int i = 0; i < MODES.length; i++) {
    		if(MODES[i] == this){
    			return i;
    		}
		}
    	throw new IllegalStateException();
    }
    
    /**
     * internal method, ignore please. 
     */
    public static QueryEvaluationMode fromInt(int i){
    	return MODES[i];
    }

    public String toString() {
    	return _id;
    }
}
