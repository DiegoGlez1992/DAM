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
package com.db4o.db4ounit.common.internal.convert;

import java.util.*;

import com.db4o.foundation.*;
import com.db4o.internal.convert.*;
import com.db4o.internal.convert.conversions.*;

import db4ounit.*;

public class ConverterTestCase implements TestSuiteBuilder {

	public Iterator4 iterator() {
		final int startingVersion = ClassIndexesToBTrees_5_5.VERSION;
		return Iterators.map(
			Iterators.range(startingVersion, Converter.VERSION + 1),
			new Function4<Integer, Test>() {
				public Test apply(final Integer version) {
					return new Test() {

						public String label() {
							return "ConverterTestCase: from " + version + " to " + Converter.VERSION;
                        }

						public void run() {
							assertConverterBehaviorForVersion(version);
                        }

						public boolean isLeafTest() {
							return true;
						}
						
						public Test transmogrify(Function4<Test, Test> fun) {
							return fun.apply(this);
						}
					};
                }
			});
    }

	private void assertConverterBehaviorForVersion(final int converterVersion) {
	    final RecordingStage stage = new RecordingStage(converterVersion);
		Converter.convert(stage);
		
		Iterator4Assert.areEqual(
				Iterators.iterator(expectedConversionsFor(converterVersion)),
				Iterators.iterator(stage.conversions()));
    }

	private ArrayList expectedConversionsFor(final int converterVersion) {
	    final ArrayList expected = new ArrayList();
		for (int version = converterVersion + 1; version <= Converter.VERSION; ++version) {
			expected.add(Converter.instance().conversionFor(version));
		}
	    return expected;
    }
	
	private static final class RecordingStage extends ConversionStage {
	    private final int _converterVersion;
	    private final ArrayList<Conversion> _conversions = new ArrayList<Conversion>();

	    public RecordingStage(int converterVersion) {
		    super(null);
		    _converterVersion = converterVersion;
	    }

	    @Override
	    public void accept(Conversion conversion) {
	    	conversions().add(conversion);
	    }

	    @Override
	    public int converterVersion() {
	    	return _converterVersion;
	    }

		public ArrayList<Conversion> conversions() {
	        return _conversions;
        }
    }

}
