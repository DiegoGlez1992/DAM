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
package com.db4o.internal.convert.conversions;

import com.db4o.internal.convert.*;

/**
 * @exclude
 */
public class CommonConversions {
    
    public static void register(Converter converter){
        converter.register(ClassIndexesToBTrees_5_5.VERSION, new ClassIndexesToBTrees_5_5());
        converter.register(FieldIndexesToBTrees_5_7.VERSION, new FieldIndexesToBTrees_5_7());
        converter.register(ClassAspects_7_4.VERSION, new ClassAspects_7_4());
        converter.register(ReindexNetDateTime_7_8.VERSION, new ReindexNetDateTime_7_8());
        converter.register(DropEnumClassIndexes_7_10.VERSION, new DropEnumClassIndexes_7_10());
        converter.register(DropGuidClassAndFieldIndexes_7_12.VERSION, new DropGuidClassAndFieldIndexes_7_12());
        converter.register(DropDateTimeOffsetClassIndexes_7_12.VERSION, new DropDateTimeOffsetClassIndexes_7_12());
        converter.register(VersionNumberToCommitTimestamp_8_0.VERSION, new VersionNumberToCommitTimestamp_8_0());
    }   
}
