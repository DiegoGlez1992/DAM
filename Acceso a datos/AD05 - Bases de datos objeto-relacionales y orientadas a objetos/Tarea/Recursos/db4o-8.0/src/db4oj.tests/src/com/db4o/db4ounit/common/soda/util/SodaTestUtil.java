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
package com.db4o.db4ounit.common.soda.util;

import com.db4o.*;
import com.db4o.query.*;

import db4ounit.*;

public class SodaTestUtil {

    public static void expectOne(Query query, Object object) {
        expect(query, new Object[] { object });
    }

    public static void expectNone(Query query) {
        expect(query, null);
    }

    public static void expect(Query query, Object[] results) {
        expect(query, results, false);
    }

    public static void expectOrdered(Query query, Object[] results) {
        expect(query, results, true);
    }

    public static void expect(Query query, Object[] results, boolean ordered) {
        ObjectSet set = query.execute();
        if (results == null || results.length == 0) {
            if (set.size() > 0) {
                Assert.fail("No content expected.");
            }
            return;
        }
        int j = 0;
        Assert.areEqual(results.length, set.size());
        while (set.hasNext()) {
            Object obj = set.next();
            boolean found = false;
            if (ordered) {
                if (TCompare.isEqual(results[j], obj)) {
                    results[j] = null;
                    found = true;
                }
                j++;
            } else {
                for (int i = 0; i < results.length; i++) {
                    if (results[i] != null) {
                        if (TCompare.isEqual(results[i], obj)) {
                            results[i] = null;
                            found = true;
                            break;
                        }
                    }
                }
            }
            if (ordered){
            	Assert.isTrue(found, "Expected '" + safeToString(results[j-1]) + "' but got '" +  safeToString(obj) + "' at index " + (j-1));
            } else {
            	Assert.isTrue(found, "Object not expected: " + safeToString(obj));
            }
        }
        for (int i = 0; i < results.length; i++) {
            if (results[i] != null) {
                Assert.fail("Expected object not returned: " + results[i]);
            }
        }
    }
	
	private static String safeToString(Object obj) {
		return obj != null ? obj.toString() : "";
	}

	private SodaTestUtil() {}
}
