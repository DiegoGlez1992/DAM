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
package com.db4o.db4ounit.common.assorted;

import java.util.*;

import com.db4o.config.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.util.*;

public class IndexCreateDropTestCase extends AbstractDb4oTestCase implements OptOutDefragSolo {
    
    public static class IndexCreateDropItem {
        
        public int _int;
        
        public String _string;
        
        public Date _date;

        public IndexCreateDropItem(int int_, String string_, Date date_) {
            _int = int_;
            _string = string_;
            _date = date_;
        }
        
        public IndexCreateDropItem(int int_, Date nullDate) {
            this(int_, int_ == 0 ? null : "" + int_, int_ == 0 ? nullDate : new Date(int_));
        }

    }
    
    private final int[] VALUES = new int[]{4, 7, 6, 6, 5, 4, 0, 0};
    
    public static void main(String[] arguments) {
        new IndexCreateDropTestCase().runSolo();
    }
    
    protected void configure(Configuration config) throws Exception {
    	// TODO
    	super.configure(config);
    }
    
    protected void store(){
        for (int i = 0; i < VALUES.length; i++) {
            db().store(new IndexCreateDropItem(VALUES[i], nullDate()));
        }
    }
    
    public void test() throws Exception{
        assertQueryResults();
        assertQueryResults(true);
        assertQueryResults(false);
        assertQueryResults(true);
    }
    
    private void assertQueryResults(boolean indexed) throws Exception{
        indexed(indexed);
        reopen();
        assertQueryResults();
    }
    
    private void indexed(boolean flag){
        ObjectClass oc = fixture().config().objectClass(IndexCreateDropItem.class);
        oc.objectField("_int").indexed(flag);
        oc.objectField("_string").indexed(flag);
        oc.objectField("_date").indexed(flag);
    }
    
    protected Query newQuery(){
        Query q = super.newQuery();
        q.constrain(IndexCreateDropItem.class);
        return q;
    }
    
    private void assertQueryResults(){
        Query q = newQuery();
        q.descend("_int").constrain(new Integer(6));
        assertQuerySize(2, q);
        
        q = newQuery();
        q.descend("_int").constrain(new Integer(4)).greater();
        assertQuerySize(4, q);
        
        q = newQuery();
        q.descend("_int").constrain(new Integer(4)).greater().equal();
        assertQuerySize(6, q);
        
        q = newQuery();
        q.descend("_int").constrain(new Integer(7)).smaller().equal();
        assertQuerySize(8, q);
        
        q = newQuery();
        q.descend("_int").constrain(new Integer(7)).smaller();
        assertQuerySize(7, q);
        
        q = newQuery();
        q.descend("_string").constrain("6");
        assertQuerySize(2, q);
        
        q = newQuery();
        q.descend("_string").constrain("7");
        assertQuerySize(1, q);
        
        q = newQuery();
        q.descend("_string").constrain("4");
        assertQuerySize(2, q);
        
        q = newQuery();
        q.descend("_string").constrain(null);
        assertQuerySize(2, q);
        
        q = newQuery();
        q.descend("_date").constrain(new Date(4)).greater();
        assertQuerySize(4, q);
        
        q = newQuery();
        q.descend("_date").constrain(new Date(4)).greater().equal();
        assertQuerySize(6, q);
        
        q = newQuery();
        q.descend("_date").constrain(new Date(7)).smaller().equal();
        assertQuerySize(PlatformInformation.isJava() ? 6 : 8, q);
        
        q = newQuery();
        q.descend("_date").constrain(new Date(7)).smaller();
        assertQuerySize(PlatformInformation.isJava() ? 5 : 7, q);
        
        q = newQuery();
        q.descend("_date").constrain(null);
        assertQuerySize(PlatformInformation.isJava() ? 2 : 0, q);
        
    }

    private void assertQuerySize(int size, Query q) {
        Assert.areEqual(size, q.execute().size());
    }

    /**
     * java.util.Date gets translated to System.DateTime on .net which is
     * a value type thus no null.
     * 
     * We ask the DateHandler the proper 'null' representation for the
     * current platform.
     */
	private Date nullDate() {
		return (Date) db().reflector().forClass(Date.class).nullValue();
	}

}
