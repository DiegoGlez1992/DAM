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

import com.db4o.internal.*;

/**
 * @exclude
 */
public abstract class ConversionStage {
	
	public final static class ClassCollectionAvailableStage extends ConversionStage {
		
		public ClassCollectionAvailableStage(LocalObjectContainer file) {
			super(file);
		}

		public void accept(Conversion conversion) {
			conversion.convert(this);
		}
	}

	public final static class SystemUpStage extends ConversionStage {
		public SystemUpStage(LocalObjectContainer file) {
			super(file);
		}
		public void accept(Conversion conversion) {
			conversion.convert(this);
		}
	}

	private LocalObjectContainer _file;
	
	protected ConversionStage(LocalObjectContainer file) {
		_file = file;
	}

	public LocalObjectContainer file() {
		return _file;
	}

	public int converterVersion() {
		return _file.systemData().converterVersion();
	}
	
    public abstract void accept(Conversion conversion);

}
