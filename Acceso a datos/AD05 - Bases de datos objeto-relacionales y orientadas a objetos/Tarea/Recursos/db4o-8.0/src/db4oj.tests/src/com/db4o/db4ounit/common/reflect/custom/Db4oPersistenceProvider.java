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

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.foundation.io.*;
import com.db4o.internal.*;
import com.db4o.query.*;

/**
 * Custom class information is stored to db4o itself as
 * a CustomClassRepository singleton.
 */
public class Db4oPersistenceProvider implements PersistenceProvider {

	static class MyContext {

		public final CustomClassRepository repository;
		public final ObjectContainer metadata;
		public final ObjectContainer data;

		public MyContext(CustomClassRepository repository, ObjectContainer metadata, ObjectContainer data) {
			this.repository = repository;
			this.metadata = metadata;
			this.data = data;
		}
	}

	public void createEntryClass(PersistenceContext context, String className,
			String[] fieldNames, String[] fieldTypes) {
		logMethodCall("createEntryClass", context, className);
		
		CustomClassRepository repository = repository(context);
		repository.defineClass(className, fieldNames, fieldTypes);
		updateMetadata(context, repository);
	}

	public void createIndex(PersistenceContext context, String className, String fieldName) {
		markIndexedField(context, className, fieldName, true);
	}
	
	public void dropIndex(PersistenceContext context, String className,
			String fieldName) {
		markIndexedField(context, className, fieldName, false);
	}

	private void markIndexedField(PersistenceContext context, String className,
			String fieldName, boolean indexed) {
		CustomField field = customClass(context, className).customField(fieldName);
		field.indexed(indexed);
		updateMetadata(context, field);
		restart(context);
	}

	private void restart(PersistenceContext context) {
		closeContext(context);
		initContext(context);
	}

	public int delete(PersistenceContext context, String className, Object uid) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void dropEntryClass(PersistenceContext context, String className) {
		// TODO Auto-generated method stub

	}

	public void initContext(PersistenceContext context) {
		logMethodCall("initContext", context);

		ObjectContainer metadata = openMetadata(context.url());
		try {
			CustomClassRepository repository = initializeClassRepository(metadata);
			CustomReflector reflector = new CustomReflector(repository);
			ObjectContainer data = openData(reflector, context.url());
			context.setProviderContext(new MyContext(repository, metadata, data));
		} catch (Exception e) {
			
			e.printStackTrace();
			
			// make sure metadata container is not left open
			// in case something goes wrong with the setup
			closeIgnoringExceptions(metadata);
			
			// cant use exception chaining here because the
			// test must run in jdk 1.1
			throw new Db4oException(e);
		}
	}

	private void closeIgnoringExceptions(ObjectContainer container) {
		try {
			container.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void insert(PersistenceContext context, PersistentEntry entry) {
		logMethodCall("insert", context, entry);

		// clone the entry because clients are allowed to reuse
		// entry objects
		dataContainer(context).store(clone(entry));
	}

	public Iterator4 select(PersistenceContext context, PersistentEntryTemplate template) {
		logMethodCall("select", context, template);

		Query query = queryFromTemplate(context, template);
		return new ObjectSetIterator(query.execute());
	}

	public void update(PersistenceContext context, PersistentEntry entry) {
		PersistentEntry existing = selectByUid(context, entry.className, entry.uid);
		existing.fieldValues = entry.fieldValues;
		
		dataContainer(context).store(existing);
	}

	private PersistentEntry selectByUid(PersistenceContext context, String className, Object uid) {
		Query query = newQuery(context, className);
		query.descend("uid").constrain(uid);
		return (PersistentEntry) query.execute().next();
	}

	private void addClassConstraint(PersistenceContext context, Query query, String className) {
		query.constrain(customClass(context, className));
	}

	private CustomClass customClass(PersistenceContext context, String className) {
		return repository(context).forName(className);
	}

	private Constraint addFieldConstraint(Query query, PersistentEntryTemplate template, int index) {
		return query.descend(template.fieldNames[index])
					.constrain(template.fieldValues[index]);
	}

	private void addFieldConstraints(Query query, PersistentEntryTemplate template) {
		if (template.fieldNames.length == 0) {
			return;
		}
		Constraint c =  addFieldConstraint(query, template, 0);
		for (int i=1; i<template.fieldNames.length; ++i) {
			c = c.and(addFieldConstraint(query, template, i));
		}
	}

	private PersistentEntry clone(PersistentEntry entry) {
		return new PersistentEntry(entry.className, entry.uid, entry.fieldValues);
	}

	public void closeContext(PersistenceContext context) {
		logMethodCall("closeContext", context);

		MyContext customContext = my(context);
		if (null != customContext) {
			closeIgnoringExceptions(customContext.metadata);
			closeIgnoringExceptions(customContext.data);
			context.setProviderContext(null);
		}
	}

	private MyContext my(PersistenceContext context) {
		return ((MyContext) context.getProviderContext());
	}

	private Configuration dataConfiguration(CustomReflector reflector) {
		Configuration config = Db4o.newConfiguration();
		config.reflectWith(reflector);
		configureCustomClasses(config, reflector);
		return config;
	}

	private void configureCustomClasses(Configuration config, CustomReflector reflector) {
		Iterator4 classes = reflector.customClasses();
		while (classes.moveNext()) {
			CustomClass cc = (CustomClass)classes.current();
			configureFields(config, cc);
		}
	}

	private void configureFields(Configuration config, CustomClass cc) {
		Iterator4 fields = cc.customFields();
		while (fields.moveNext()) {
			CustomField field = (CustomField)fields.current();
			config.objectClass(cc).objectField(field.getName()).indexed(field.indexed());
		}
	}

	public ObjectContainer dataContainer(PersistenceContext context) {
		return my(context).data;
	}

	private CustomClassRepository initializeClassRepository(ObjectContainer container) {
		CustomClassRepository repository = queryClassRepository(container);
		if (repository == null) {
			log("Initializing new class repository.");
			repository = new CustomClassRepository();
			store(container, repository);
		} else {
			log("Found existing class repository: " + repository);
		}
		return repository;
	}

	private Configuration metaConfiguration() {
		Configuration config = Db4o.newConfiguration();
		config.exceptionsOnNotStorable(true);
		
		// the following line is only necessary for the tests to run
		// in OSGi environment
		config.reflectWith(Platform4.reflectorForType(CustomClassRepository.class));
		
		cascade(config, CustomClassRepository.class);
		cascade(config, Hashtable4.class);
		cascade(config, CustomClass.class);
		
		// FIXME: [TA] this is necessary because the behavior
		// on .net differs with regards to cascade activation
		// remove the following two lines and run the test
		// on .net to see it fail
		cascade(config, CustomField.class);
		cascade(config, CustomUidField.class);
		
		return config;
	}

	private void cascade(Configuration config, Class klass) {
		config.objectClass(klass).cascadeOnUpdate(true);
		config.objectClass(klass).cascadeOnActivate(true);
	}

	private ObjectContainer metadataContainer(PersistenceContext context) {
		return my(context).metadata;
	}

	private String metadataFile(String fname) {
		return fname + ".metadata";
	}

	private ObjectContainer openData(CustomReflector reflector, String fname) {
		return Db4o.openFile(dataConfiguration(reflector), fname);
	}

	private ObjectContainer openMetadata(String fname) {
		return Db4o.openFile(metaConfiguration(), metadataFile(fname));
	}

	public void purge(String url) {
		File4.delete(url);
		File4.delete(metadataFile(url));
	}

	private CustomClassRepository queryClassRepository(ObjectContainer container) {
		ObjectSet found = container.query(CustomClassRepository.class);
		if (!found.hasNext()) {
			return null;
		}
		return (CustomClassRepository)found.next();
	}

	private Query queryFromTemplate(PersistenceContext context, PersistentEntryTemplate template) {
		Query query = newQuery(context, template.className);
		addFieldConstraints(query, template);
		return query;
	}

	private Query newQuery(PersistenceContext context, String className) {
		Query query = dataContainer(context).query();
		addClassConstraint(context, query, className);
		return query;
	}

	private CustomClassRepository repository(PersistenceContext context) {
		return my(context).repository;
	}

	private void store(ObjectContainer container, Object obj) {
		container.store(obj);
		container.commit();
	}

	private void updateMetadata(PersistenceContext context, Object metadata) {
		store(metadataContainer(context), metadata);
	}

	private void log(String message) {
		Logger.log("Db4oPersistenceProvider: " + message);
	}

	private void logMethodCall(String methodName, Object arg) {
		Logger.logMethodCall("Db4oPersistenceProvider", methodName, arg);
	}

	private void logMethodCall(String methodName, Object arg1, Object arg2) {
		Logger.logMethodCall("Db4oPersistenceProvider", methodName, arg1, arg2);
	}

}

