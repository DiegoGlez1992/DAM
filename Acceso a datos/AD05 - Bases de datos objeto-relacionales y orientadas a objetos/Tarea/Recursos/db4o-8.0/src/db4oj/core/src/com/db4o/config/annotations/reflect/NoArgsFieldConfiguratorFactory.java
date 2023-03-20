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
package com.db4o.config.annotations.reflect;

import java.lang.annotation.*;
import java.lang.reflect.*;


/**
 * @exclude
 * @sharpen.ignore
 */
@decaf.Ignore
public class NoArgsFieldConfiguratorFactory implements Db4oConfiguratorFactory {
	private Constructor _constructor;

	public NoArgsFieldConfiguratorFactory(Class<?> configuratorClass) throws NoSuchMethodException {
		_constructor=configuratorClass.getConstructor(new Class[]{String.class,String.class});
	}

	public Db4oConfigurator configuratorFor(AnnotatedElement element, Annotation annotation) {
		try {
			if(!(element instanceof Field)) {
				return null;
			}
			Field field=(Field)element;
			String className=field.getDeclaringClass().getName();
			String fieldName=field.getName();
			return (Db4oConfigurator)_constructor.newInstance(new Object[]{className,fieldName});
		} catch (Exception exc) {
			return null;
		}
	}
}
