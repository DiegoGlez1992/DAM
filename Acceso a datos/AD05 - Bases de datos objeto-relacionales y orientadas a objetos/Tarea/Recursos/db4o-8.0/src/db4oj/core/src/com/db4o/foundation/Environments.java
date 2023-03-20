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
package com.db4o.foundation;

import java.util.*;

import com.db4o.internal.*;

/**
 * @sharpen.partial
 */
public class Environments {
	
	private static final DynamicVariable<Environment> _current = DynamicVariable.newInstance();
	
	public static <T> T my(Class<T> service) {
		final Environment environment = current();
		if (null == environment) {
			throw new IllegalStateException();
		}
		return environment.provide(service);
	}

	private static Environment current() {
	    return _current.value();
    }
	
	public static void runWith(Environment environment, Runnable runnable) {
		_current.with(environment, runnable);
	}
	
	public static Environment newClosedEnvironment(final Object... bindings) {
		return new Environment() {

			public <T> T provide(Class<T> service) {
				for (Object binding : bindings) {
					if (service.isInstance(binding)) {
						return service.cast(binding);
					}
				}
				return null;
            }
			
		};
    }
	
	public static Environment newCachingEnvironmentFor(final Environment environment) {
		return new Environment() {
			private final Map<Class<?>, Object> _bindings = new HashMap<Class<?>, Object>();
	
		    public <T> T provide(Class<T> service) {
		        final Object existing = _bindings.get(service);
		        if (null != existing) {
		        	return service.cast(existing);
		        }
		        final T binding = environment.provide(service);
		        if (null == binding) {
		        	return null;
		        }
		        _bindings.put(service, binding);
		        return binding;
		    }
		};
	}
	
	public static Environment newConventionBasedEnvironment(Object... bindings) {
		return newCachingEnvironmentFor(compose(newClosedEnvironment(bindings), new ConventionBasedEnvironment()));
	}

	public static Environment newConventionBasedEnvironment() {
		return newCachingEnvironmentFor(new ConventionBasedEnvironment());
    }
	
	public static Environment compose(final Environment... environments) {
		return new Environment() {
			public <T> T provide(Class<T> service) {
				for (Environment e : environments) {
					final T binding = e.provide(service);
					if (null != binding) {
						return binding;
					}
				}
				return null;
            }
		};
	}
	
	private static final class ConventionBasedEnvironment implements Environment { 
		public <T> T provide(Class<T> service) {
			return resolve(service);
	    }
	    
	    /**
	     * Resolves a service interface to its default implementation using the
	     * db4o namespace convention:
	     * 
	     *      interface foo.bar.Baz
	     *      default implementation foo.internal.bar.BazImpl
	     *
	     * @return the convention based type name for the requested service
	     */
	    private <T> T resolve(Class<T> service) {
	    	final String className = defaultImplementationFor(service);
	    	final Object binding = ReflectPlatform.createInstance(className);
	    	if (null == binding) {
	        	throw new IllegalArgumentException("Cant find default implementation for " + service.toString() + ": " + className);
	        }
			return service.cast(binding);
	    }
    }

	/**
	 * @sharpen.ignore
	 */
	static String defaultImplementationFor(Class service) {
		final String implNameSuffix = "." + ReflectPlatform.simpleName(service) + "Impl";
		
		final String packageName = splitQualifiedName(service.getName()).qualifier;
		if (packageName.contains(".internal.")
			|| packageName.endsWith(".internal")) {
			// ignore convention for internal types
			return packageName + implNameSuffix;
		}
		
		final QualifiedName packageParts = splitQualifiedName(packageName);
		return packageParts.qualifier + ".internal" + packageParts.name + implNameSuffix;
	}
	
	/**
	 * @sharpen.ignore
	 */
	private static final class QualifiedName {
		final String qualifier;
		final String name;

		public QualifiedName(String qualifier, String name) {
	        this.qualifier = qualifier;
	        this.name = name;
        }
	}

	/**
	 * @sharpen.ignore
	 */
	private static QualifiedName splitQualifiedName(final String qualifiedName) {
	    final int lastDot = qualifiedName.lastIndexOf('.');
		final String qualifier = qualifiedName.substring(0, lastDot);
		final String name = qualifiedName.substring(lastDot);
		return new QualifiedName(qualifier, name);
    }

}
