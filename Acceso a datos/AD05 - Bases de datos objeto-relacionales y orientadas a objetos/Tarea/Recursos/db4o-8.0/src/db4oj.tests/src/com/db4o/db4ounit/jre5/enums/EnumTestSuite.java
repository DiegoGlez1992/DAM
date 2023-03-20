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
package com.db4o.db4ounit.jre5.enums;

import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.internal.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.fixtures.*;


/**
 */
@decaf.Ignore
public class EnumTestSuite extends FixtureBasedTestSuite /* implements OptOutTemporary */ implements Db4oTestCase {
	
	
	private static FixtureVariable<Integer> STACK_DEPTH = new FixtureVariable<Integer>("stackDepth");

	public static void main(String[] args) {
		new ConsoleTestRunner(EnumTestSuite.class).run();
	}
	
	@Override
	public FixtureProvider[] fixtureProviders() {
		return new FixtureProvider[] {
				new Db4oFixtureProvider(),
				new SimpleFixtureProvider(STACK_DEPTH, 2, Const4.DEFAULT_MAX_STACK_DEPTH)
		};
	}

	@Override
	public Class[] testUnits() {
		return new Class[] {
				EnumTestCase.class
		};
	}

	
	
	public static class EnumTestCase extends AbstractDb4oTestCase {
		private final static int NUMRUNS=1;
		
		
		@Override
		protected void configure(Configuration config) throws Exception {
			super.configure(config);
	        config.maxStackDepth(STACK_DEPTH.value());
		}
    
	    @SuppressWarnings("unchecked")
		public void testSingleStoreRetrieve() throws Exception {     	
	        // We make sure the Jdk5Enum class is already loaded, otherwise
	        // we may get the side effect that storing it will load the class
	        // and overwrite our changes exactly when we store them. 
	        db().store(TypeCountEnum.A);
	        
	        EnumHolder data=new EnumHolder(TypeCountEnum.A);
	        TypeCountEnum.A.reset();
	        Assert.areEqual(0, TypeCountEnum.A.getCount());
	        TypeCountEnum.A.incCount();
	        
	        // The Jdk5Enum object may already be stored on the server, so we
	        // can't persist by reachability. We have to store the object
	        // explicitely.
	        db().store(TypeCountEnum.A);
	        
	        Assert.areEqual(1, TypeCountEnum.A.getCount());
	        Assert.areEqual(0, TypeCountEnum.B.getCount());
	        Assert.areEqual(TypeCountEnum.A, data.getType());
	        
	        db().store(data);
	        reopen();        
	        data=null;
	        
	        Query query=db().query();
	        query.constrain(EnumHolder.class);
	        Query sub=query.descend("type");
	        sub.constrain(TypeCountEnum.class);
	        sub.constrain(TypeCountEnum.A);
	        sub.descend("type").constrain("A");
	        sub.descend("count").constrain(Integer.valueOf(1));
	
	        ObjectSet<EnumHolder> result = query.execute();
	        Assert.areEqual(1, result.size());
	        data=(EnumHolder)result.next();
	        Assert.areSame(TypeCountEnum.A, data.getType());
	        Assert.areEqual(data.getType().name(), TypeCountEnum.A.name());
	        Assert.areEqual(1, result.size());
	
	        ensureEnumInstancesInDB(db());
	    }
	
		private static class TypeCountEnumComparator implements Comparator<TypeCountEnum> {
			public int compare(TypeCountEnum e1, TypeCountEnum e2) {
				return e1.name().compareTo(e2.name());
			}
		}
	
		public static class CollectionHolder {
			public List<TypeCountEnum> list; 
			public Set<TypeCountEnum> set; 
			public Map<TypeCountEnum,String> keymap; 
			public Map<String,TypeCountEnum> valmap; 
			public TypeCountEnum[] array; 
		}
	        
		/**
		 * @deprecated testing deprecated api
		 */
	    @SuppressWarnings("unchecked")    
		public void testEnumsInCollections() throws Exception {
	    	CollectionHolder holder=new CollectionHolder();
	    	holder.list=new ArrayList<TypeCountEnum>(NUMRUNS);
	    	Comparator<TypeCountEnum> comp=new TypeCountEnumComparator();
	    	holder.set=new TreeSet<TypeCountEnum>(comp);
	    	holder.keymap=new HashMap<TypeCountEnum,String>(NUMRUNS);
	    	holder.valmap=new HashMap<String,TypeCountEnum>(NUMRUNS);
	    	holder.array=new TypeCountEnum[NUMRUNS];
	    	for(int i=0;i<NUMRUNS;i++) {
	    		TypeCountEnum curenum=nthEnum(i);
				holder.list.add(curenum);
	    		
	    		holder.array[i]=curenum;
	    	}
			holder.set.add(TypeCountEnum.A);
			holder.set.add(TypeCountEnum.B);
			holder.keymap.put(TypeCountEnum.A,TypeCountEnum.A.name());
			holder.keymap.put(TypeCountEnum.B,TypeCountEnum.B.name());
			holder.valmap.put(TypeCountEnum.A.name(),TypeCountEnum.A);
			holder.valmap.put(TypeCountEnum.B.name(),TypeCountEnum.B);	
	    	db().store(holder);
	    	
	    	reopen();
	    	ObjectSet result=db().queryByExample(CollectionHolder.class);
	    	Assert.areEqual(1, result.size());
	    	holder=(CollectionHolder)result.next();
	
	    	Assert.areEqual(NUMRUNS, holder.list.size());
	    	Assert.areEqual(2, holder.set.size());
	    	Assert.areEqual(2, holder.keymap.size());
	    	Assert.areEqual(2, holder.valmap.size());
	    	Assert.areEqual(NUMRUNS, holder.array.length);
	    	ensureEnumInstancesInDB(db());
	    }
	    
		@SuppressWarnings("unchecked")
		private void ensureEnumInstancesInDB(ObjectContainer db) {
			Query query;
			ObjectSet<TypeCountEnum> result;
			query=db.query();
			query.constrain(TypeCountEnum.class);
			result = query.execute();
			// We should have all enum members once in the database, since they're
	        // statically referenced by the Enum subclass.
			if(result.size()!=2) {
				System.err.println("# instances in db: "+result.size());
				while(result.hasNext()) {
					TypeCountEnum curenum=(TypeCountEnum)result.next();
	                long id = db.ext().getID(curenum);
	                System.err.println(curenum+"  :  ihc "+System.identityHashCode(curenum) + "  : id " + id);
				}
				
			}
	        Assert.areEqual(2, result.size());
		}
		
		private TypeCountEnum nthEnum(int n) {
			return (n%2==0 ? TypeCountEnum.A : TypeCountEnum.B);
		}
	}
}
