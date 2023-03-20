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
package com.db4o.test;

import com.db4o.test.cluster.*;
import com.db4o.test.collections.*;
import com.db4o.test.reflect.*;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class Jdk1_2TestSuite extends TestSuite{
    
    public Class[] tests(){
        return new Class[] {
            ClusterQueryImplementsList.class,
            DiscreteArrayInMap.class,
            GenericObjects.class,
            JdkComparatorSort.class,
            MultipleEvaluationGetObjectCalls.class,
            ObjectSetAsList.class,
            OrClassConstraintInList.class,
            QueryForList.class,
            Reflection.class,
            RefreshList.class,
            SelectDistinct.class,
            SodaEvaluation.class,
            SortedSameOrder.class,
            StoreBigDecimal.class,
            StringCaseInsensitive.class,
            TestHashMap.class,
            TestStringBuffer.class,
            TestTreeMap.class,
            TestTreeSet.class,
        };
    }
}