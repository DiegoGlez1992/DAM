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
package com.db4o.internal.convert;

import java.util.*;

import com.db4o.internal.convert.conversions.*;

/**
 * @exclude
 */
public class Converter {
    
    public static final int VERSION = VersionNumberToCommitTimestamp_8_0.VERSION;
    
    public static boolean convert(ConversionStage stage) {
    	if(!needsConversion(stage.converterVersion())) {
    		return false;
    	}
    	return instance().runConversions(stage);
    }
    
    private static Converter _instance;
    
    private Map<Integer, Conversion> _conversions;

	private int _minimumVersion = Integer.MAX_VALUE;
    
    private Converter() {
        _conversions = new HashMap<Integer, Conversion>();
        
        // TODO: There probably will be Java and .NET conversions
        //       Create Platform4.registerConversions() method ann
        //       call from here when needed.
        CommonConversions.register(this);
    }

	public static Converter instance() {
	    if(_instance == null){
    		_instance = new Converter();
    	}
    	return _instance;
    }

	public Conversion conversionFor(int version) {
	    return _conversions.get(version);
    }
	
	private static boolean needsConversion(final int converterVersion) {
	    return converterVersion < VERSION;
    }

    public void register(int introducedVersion, Conversion conversion) {
        if(_conversions.containsKey(introducedVersion)){
            throw new IllegalStateException();
        }
        if (introducedVersion < _minimumVersion) {
        	_minimumVersion  = introducedVersion;
        }
        _conversions.put(introducedVersion, conversion);
    }
    
    public boolean runConversions(ConversionStage stage) {
    	int startingVersion = Math.max(stage.converterVersion() + 1, _minimumVersion);
        for (int version = startingVersion; version <= VERSION; version++) {
            Conversion conversion = conversionFor(version);
            if (conversion == null) {
            	throw new IllegalStateException("Could not find a conversion for version " + version);
            }
            stage.accept(conversion);
        }
        return true;
    }
    
}
