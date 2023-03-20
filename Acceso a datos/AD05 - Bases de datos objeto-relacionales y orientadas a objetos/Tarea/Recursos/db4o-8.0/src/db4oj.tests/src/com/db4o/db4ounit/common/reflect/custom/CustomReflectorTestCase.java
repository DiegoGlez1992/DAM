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
package com.db4o.db4ounit.common.reflect.custom;

import com.db4o.foundation.*;
import com.db4o.foundation.io.*;
import com.db4o.internal.*;

import db4ounit.*;
import db4ounit.extensions.OptOutAndroid;

/**
 * This test case serves two purposes:
 *
 * 1) testing the reflector API
 * 2) documenting a common use case for the reflector API which is adapting an external
 * data model to db4o's internal OO based mechanism.
 * 
 * See CustomReflector, CustomClassRepository, CustomClass, CustomField and CustomUidField
 * for details.
 *
 */
public class CustomReflectorTestCase implements TestCase, TestLifeCycle {

	private static final String CAT_CLASS = "Cat";
	private static final String[] CAT_FIELD_NAMES = new String[] { "name", "troubleMakingScore" };
	private static final String[] CAT_FIELD_TYPES = new String[] { "string", "int" };
	
	private static final String PERSON_CLASS = "Person";
	private static final String[] PERSON_FIELD_NAMES = new String[] { "name" };
	private static final String[] PERSON_FIELD_TYPES = new String[] { "string" };
	
	private static final PersistentEntry[] CAT_ENTRIES = {
		new PersistentEntry(CAT_CLASS, "0", new Object[] { "Biro-Biro", new Integer(9) }),
		new PersistentEntry(CAT_CLASS, "1", new Object[] { "Samira", new Integer(4) }),
		new PersistentEntry(CAT_CLASS, "2", new Object[] { "Ivo", new Integer(2) }),
	};
	
	private static final PersistentEntry[] PERSON_ENTRIES = {
		new PersistentEntry(PERSON_CLASS, "10", new Object[] { "Eric Idle" }),
		new PersistentEntry(PERSON_CLASS, "11", new Object[] { "John Cleese" }),
	};

	PersistenceContext _context;
	Db4oPersistenceProvider _provider;

	public void setUp() {
		purge();
		
		initializeContext();
		initializeProvider();
		
		createEntryClass(CAT_CLASS, CAT_FIELD_NAMES, CAT_FIELD_TYPES);
		createIndex(CAT_CLASS, CAT_FIELD_NAMES[0]);
		restartProvider();
		
		createEntryClass(PERSON_CLASS, PERSON_FIELD_NAMES, PERSON_FIELD_TYPES);
		restartProvider();
		
		insertEntries();		
		restartProvider();
	}
	
	public void testUpdate() {
		PersistentEntry entry = new PersistentEntry(CAT_CLASS, CAT_ENTRIES[0].uid, new Object[] { "Birinho", new Integer(10) });
		update(entry);
		restartProvider();
		
		//exerciseSelectByField(entry, CAT_FIELD_NAMES);
		
		PersistentEntry[] expected = copy(CAT_ENTRIES);
		expected[0] = entry;
		assertEntries(expected, selectAll(CAT_CLASS));
	}

	public void testSelectAll() {

		assertEntries(PERSON_ENTRIES, selectAll(PERSON_CLASS));
		assertEntries(CAT_ENTRIES, selectAll(CAT_CLASS));
	}
	
	public void testSelectByField() {

		exerciseSelectByField(CAT_ENTRIES, CAT_FIELD_NAMES);
		exerciseSelectByField(PERSON_ENTRIES, PERSON_FIELD_NAMES);
	}
	
	public void testSelectByFields() {
		
		PersistentEntry existing = CAT_ENTRIES[0];
		PersistentEntry newEntry = new PersistentEntry(CAT_CLASS, 3, new Object[] { existing.fieldValues[0], new Integer(10) });
		insert(newEntry);
		
		Iterator4 found = selectByField(existing.className, CAT_FIELD_NAMES[0], existing.fieldValues[0]);
		assertEntries(new PersistentEntry[] { existing, newEntry }, found);
		
		assertSingleEntry(existing, select(existing.className, CAT_FIELD_NAMES, existing.fieldValues));
		assertSingleEntry(newEntry, select(newEntry.className, CAT_FIELD_NAMES, newEntry.fieldValues));
		
	}
	
	public void testDropIndex() {
		
		dropIndex(CAT_CLASS, CAT_FIELD_NAMES[0]);
		
		FieldMetadata field = fieldMetadata(CAT_CLASS, CAT_FIELD_NAMES[0]);
		Assert.isFalse(field.hasIndex());
	}

	public void testFieldIndex() {
		
		FieldMetadata field0 = fieldMetadata(CAT_CLASS, CAT_FIELD_NAMES[0]);
		Assert.isTrue(field0.hasIndex());
		
		FieldMetadata field1 = fieldMetadata(CAT_CLASS, CAT_FIELD_NAMES[1]);
		Assert.isFalse(field1.hasIndex());
	}

	private FieldMetadata fieldMetadata(String className, String fieldName) {
		ClassMetadata meta = classMetadataForName(className);
		FieldMetadata field0 = meta.fieldMetadataForName(fieldName);
		return field0;
	}
	
	private void update(PersistentEntry entry) {
		_provider.update(_context, entry);
	}

	private void assertEntries(PersistentEntry[] expected, Iterator4 actual) {
		Collection4 checklist = new Collection4(actual);
		Assert.areEqual(expected.length, checklist.size());
		for (int i=0; i<expected.length; ++i) {
			PersistentEntry e = expected[i];
			PersistentEntry a = entryByUid(checklist.iterator(), e.uid);
			if (a != null) {
				assertEqualEntries(e, a);
				checklist.remove(a);
			}
		}
		Assert.isTrue(checklist.isEmpty(), checklist.toString());
	}

	private PersistentEntry entryByUid(Iterator4 iterator, Object uid) {
		while (iterator.moveNext()) {
			PersistentEntry e = (PersistentEntry)iterator.current();
			if (uid.equals(e.uid)) {
				return e;
			}
		}
		return null;
	}

	private ClassMetadata classMetadataForName(String className) {
		InternalObjectContainer container = (InternalObjectContainer)_provider.dataContainer(_context);
		return container.classMetadataForReflectClass(container.reflector().forName(className));
	}


	private void exerciseSelectByField(PersistentEntry[] entries, String[] fieldNames) {
		for (int i=0; i<entries.length; ++i) { 
			exerciseSelectByField(entries[i], fieldNames);
		}
	}

	private void exerciseSelectByField(PersistentEntry expected, String[] fieldNames) {
		for (int i=0; i<fieldNames.length; ++i) {
			Iterator4 found = selectByField(expected.className, fieldNames[i], expected.fieldValues[i]);
			assertSingleEntry(expected, found);
		}
	}

	private void assertSingleEntry(PersistentEntry expected, Iterator4 found) {
		Assert.isTrue(found.moveNext(), "Expecting entry '" + expected + "'");
		PersistentEntry actual = (PersistentEntry)found.current();
		assertEqualEntries(expected, actual);
		Assert.isFalse(found.moveNext(), "Expecting only '" + expected + "'");
	}

	private void initializeContext() {
		_context = new PersistenceContext(dataFile());
	}

	private void initializeProvider() {
		_provider = new Db4oPersistenceProvider();
		_provider.initContext(_context);
	}

	private void insertEntries() {
		insertEntries(CAT_ENTRIES);
		insertEntries(PERSON_ENTRIES);
	}

	private void insertEntries(PersistentEntry[] entries) {
		PersistentEntry entry = new PersistentEntry(null, null, null);
		for (int i=0; i<entries.length; ++i) {
			entry.className = entries[i].className;
			entry.uid = entries[i].uid;
			entry.fieldValues = entries[i].fieldValues;
			// reuse entries so the provider can't assume
			// anything about identity
			insert(entry);
		}
	}

	private void assertEqualEntries(PersistentEntry expected, PersistentEntry actual) {
		Assert.areEqual(expected.className, actual.className);
		Assert.areEqual(expected.uid, actual.uid);
		ArrayAssert.areEqual(expected.fieldValues, actual.fieldValues);
	}

	private Iterator4 selectByField(String className, String fieldName, Object fieldValue) {
		return select(className, new String[] { fieldName }, new Object[] { fieldValue });
	}

	private Iterator4 select(String className, String[] fieldNames,
			Object[] fieldValues) {
		return select(new PersistentEntryTemplate(className, fieldNames, fieldValues));
	}

	private Iterator4 selectAll(String className) {
		return select(className, new String[0], new Object[0]);
	}

	private Iterator4 select(PersistentEntryTemplate template) {
		return _provider.select(_context, template);
	}

	private void insert(PersistentEntry entry) {
		_provider.insert(_context, entry);
	}

	private void createIndex(String className, String fieldName) {
		_provider.createIndex(_context, className, fieldName);
	}
	
	private void dropIndex(String className, String fieldName) {
		_provider.dropIndex(_context, className, fieldName);
	}

	private void createEntryClass(String className, String[] fieldNames,
			String[] fieldTypes) {
		_provider.createEntryClass(_context, className, fieldNames, fieldTypes);
	}

	public void tearDown() {
		shutdownProvider(true);
		_context = null;
	}

	private void shutdownProvider(boolean purge) {
		if (_provider != null) _provider.closeContext(_context);
		if (purge) purge();
		_provider = null;
	}
	
	void purge() {
		new Db4oPersistenceProvider().purge(dataFile());
	}

	void restartProvider() {
		shutdownProvider(false);
		initializeProvider();
	}

	private String dataFile() {
		return Path4.combine(Path4.getTempPath(), "CustomReflector.db4o");
	}
	
	
	private PersistentEntry[] copy(PersistentEntry[] entries) {
		PersistentEntry[] clone = new PersistentEntry[entries.length];
		System.arraycopy(entries, 0, clone, 0, clone.length);
		return clone;
	}

}
