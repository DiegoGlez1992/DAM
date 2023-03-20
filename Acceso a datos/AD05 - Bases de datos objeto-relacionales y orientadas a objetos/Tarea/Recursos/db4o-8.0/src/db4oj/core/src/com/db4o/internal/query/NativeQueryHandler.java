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
package com.db4o.internal.query;

import java.lang.reflect.*;

import com.db4o.*;
import com.db4o.diagnostic.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.diagnostic.*;
import com.db4o.query.*;
import com.db4o.reflect.*;

/**
 * @sharpen.ignore
 */
public class NativeQueryHandler {
	
	private static final String OPTIMIZER_IMPL_NAME = "com.db4o.nativequery.optimization.Db4oOnTheFlyEnhancer";

	public final static String UNOPTIMIZED = "UNOPTIMIZED";
	public final static String PREOPTIMIZED = "PREOPTIMIZED";
	public final static String DYNOPTIMIZED = "DYNOPTIMIZED";
	
	private ObjectContainer _container;
	private Db4oNQOptimizer _enhancer;
	private List4 _listeners;
	
	public NativeQueryHandler(ObjectContainer container) {
		_container = container;
		loadQueryOptimizer();
    }	

	public void addListener(Db4oQueryExecutionListener listener) {
		_listeners=new List4(_listeners,listener);
	}

	public void clearListeners() {
		_listeners=null;
	}
	
	public <T> ObjectSet<T> execute(Query query, Predicate<T> predicate,QueryComparator<T> comparator) {
		return configureQuery(query, predicate,comparator).execute();
	}
	
	private Query configureQuery(Query query, Predicate predicate,QueryComparator comparator) {
		if(comparator!=null) {
			query.sortBy(comparator);
		}
		query.constrain(predicate.extentType());
		if(predicate instanceof Db4oEnhancedFilter) {
			((Db4oEnhancedFilter)predicate).optimizeQuery(query);
			notifyListeners(predicate,NativeQueryHandler.PREOPTIMIZED,null);
			return query;
		}
		try {
			if (shouldOptimize()) {
				Object optimized=_enhancer.optimize(query,predicate);
				notifyListeners(predicate,NativeQueryHandler.DYNOPTIMIZED,optimized);
				return query;
			}
		} catch (Exception optimizationFailure) {
//			optimizationFailure.printStackTrace();
		}
		query.constrain(new PredicateEvaluation(predicate));
		notifyListeners(predicate,NativeQueryHandler.UNOPTIMIZED,null);
        if(shouldOptimize()){
            DiagnosticProcessor dp = ((ObjectContainerBase)_container)._handlers.diagnosticProcessor();
            if(dp.enabled()){
                dp.nativeQueryUnoptimized(predicate, null);
            }
        }
		return query;
	}

    private boolean shouldOptimize() {
        return _container.ext().configure().optimizeNativeQueries() && _enhancer!=null;
    }

	private void notifyListeners(Predicate predicate, String msg,Object optimized) {
		NQOptimizationInfo info=new NQOptimizationInfo(predicate,msg,optimized);
		for(Iterator4 iter=new Iterator4Impl(_listeners);iter.moveNext();/**/) {
			((Db4oQueryExecutionListener)iter.current()).notifyQueryExecuted(info);
		}
	}
	
	private void loadQueryOptimizer() {
		Class clazz = ReflectPlatform.forName(NativeQueryHandler.OPTIMIZER_IMPL_NAME);
		DiagnosticProcessor dp = ((ObjectContainerBase)_container)._handlers.diagnosticProcessor();
		
		if(clazz == null){
            if(dp.enabled()){
                dp.nativeQueryOptimizerNotLoaded(NativeQueryOptimizerNotLoaded.NQ_NOT_PRESENT, null);
            }
			return;
		}
		
		try {
			Constructor constructor;
			constructor = clazz.getConstructor(new Class[]{
													Reflector.class
													});
			
			if(constructor == null)
				return;
			
			_enhancer = (Db4oNQOptimizer) constructor.newInstance(new Object[]{
												this._container.ext().reflector()
												}); 
		} catch (Exception e) {
            if(dp.enabled()){
                dp.nativeQueryOptimizerNotLoaded(NativeQueryOptimizerNotLoaded.NQ_CONSTRUCTION_FAILED, e);
            }
		}
		
	}
}
