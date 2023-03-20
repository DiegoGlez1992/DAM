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
package com.db4o.config.annotations;

import java.lang.annotation.*;

/**
 * turns on storing static field values for this class. <br>
 * <br>
 * By default, static field values of classes are not stored to the database
 * file. By decoration a specific class with this switch, all non-simple-typed
 * static field values of this class are stored the first time an object of the
 * class is stored, and restored, every time a database file is opened
 * afterwards. <br>
 * <br>
 * This annotation will be ignored for simple types. <br>
 * <br>
 * Use {@code @PersistedStaticFieldValues } for constant static object members.
 * <br>
 * <br>
 * <br>
 * <br>
 * This option will slow down the process of opening database files and the
 * stored objects will occupy space in the database file.
 * @exclude
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PersistedStaticFieldValues {
}