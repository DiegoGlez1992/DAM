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
package com.db4o.internal.query;

import java.util.*;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.marshall.*;

public class SodaQueryComparator implements Comparator<Integer>, IntComparator {
	
	public static class Ordering {
		
		@decaf.Public
		private Direction _direction;
		
		@decaf.Public
		private String[] _fieldPath;
		
		@decaf.Public
		transient List<FieldMetadata> _resolvedPath;
		
		public Ordering(Direction direction, String... fieldPath) {
			_direction = direction;
			_fieldPath = fieldPath;
		}

		public Direction direction() {
			return _direction;
		}
		
		public String[] fieldPath() {
			return _fieldPath;
		}
	}
	
	public static class Direction {
		public static final Direction ASCENDING = new Direction(0);
		public static final Direction DESCENDING = new Direction(1);
		
		@decaf.Public
		private int value;
		
		@decaf.Public
		private Direction() {
		}
		
		private Direction(int value) {
			this.value = value;
		}
		
		@Override
		public boolean equals(Object obj) {
			return ((Direction)obj).value == value;
		}

		@Override
		public String toString() {
			return this.equals(ASCENDING) ? "ASCENDING" : "DESCENDING";
		}
	}

	private final LocalObjectContainer _container;
	private final LocalTransaction _transaction;
	private final ClassMetadata _extentType;
	private final Ordering[] _orderings;
	private final Map<Integer, ByteArrayBuffer> _bufferCache = new HashMap<Integer, ByteArrayBuffer>();
	private final Map<FieldValueKey, Object> _fieldValueCache = new HashMap<FieldValueKey, Object>();

	public SodaQueryComparator(
			LocalObjectContainer container,
			Class extentType,
			Ordering... orderings) {
		
		this(container, container.produceClassMetadata(container.reflector().forClass(extentType)), orderings);
	}

	public SodaQueryComparator(
			LocalObjectContainer container,
			final ClassMetadata extent,
			Ordering... orderings) {
		_container = container;
		_transaction = ((LocalTransaction) _container.transaction());
		_extentType = extent;
		_orderings = orderings;
		resolveFieldPaths(orderings);
	}

	private void resolveFieldPaths(Ordering[] orderings) {
		for (Ordering fieldPath : orderings) {
			fieldPath._resolvedPath = resolveFieldPath(fieldPath.fieldPath());
		}
	}

	public List<Integer> sort(long[] ids) {
		ArrayList<Integer> idList = listFrom(ids);
		Collections.sort(idList, this);
		return idList;
	}

	private ArrayList<Integer> listFrom(long[] ids) {
		ArrayList<Integer> idList = new ArrayList<Integer>(ids.length);
		for (long id : ids) {
			idList.add((int)id);
		}
		return idList;
	}

	private List<FieldMetadata> resolveFieldPath(String[] fieldPath) {
		List<FieldMetadata> fields = new ArrayList<FieldMetadata>(fieldPath.length);
		ClassMetadata currentType = _extentType;
		for (String fieldName : fieldPath) {
			FieldMetadata field = currentType.fieldMetadataForName(fieldName);
			if(field == null){
				fields.clear();
				break;
			}
			currentType = field.fieldType();
			fields.add(field);
		}
		return fields;
	}

	public int compare(Integer x, Integer y) {
		return compare(x.intValue(), y.intValue());
	}
	
	public int compare(int x, int y) {
		for (Ordering ordering : _orderings) {
			List<FieldMetadata> resolvedPath = ordering._resolvedPath;
			if(resolvedPath.size() == 0){
				continue;
			}
			int result = compareByField(x, y, resolvedPath);
			if (result != 0) {
				return ordering.direction().equals(Direction.ASCENDING)
					? result
					: -result;
			}
		}
		return 0;
	}

	private int compareByField(int x, int y, List<FieldMetadata> path) {
		final Object xFieldValue = getFieldValue(x, path);
		final Object yFieldValue = getFieldValue(y, path);
		final FieldMetadata field = path.get(path.size() - 1);
		return field.prepareComparison(_transaction.context(), xFieldValue).compareTo(yFieldValue);
	}

	private Object getFieldValue(int id, List<FieldMetadata> path) {
		for (int i = 0; i < path.size() - 1; ++i) {
			final Object obj = getFieldValue(id, path.get(i));
			if (null == obj) {
				return null;
			}
			id = _container.getID(_transaction, obj);
		}
		return getFieldValue(id, path.get(path.size() - 1));
	}

	static class FieldValueKey {
		private int _id;
		private FieldMetadata _field;

		public FieldValueKey(int id, FieldMetadata field) {
			_id = id;
			_field = field;
		}

		@Override
		public int hashCode() {
			return _field.hashCode() ^ _id;
		}

		@Override
		public boolean equals(Object obj) {
			FieldValueKey other = (FieldValueKey) obj;
			return _field == other._field && _id == other._id;
		}
	}

	private Object getFieldValue(int id, FieldMetadata field) {
		final FieldValueKey key = new FieldValueKey(id, field);

		Object cachedValue = _fieldValueCache.get(key);
		if (null != cachedValue)
			return cachedValue;

		Object fieldValue = readFieldValue(id, field);
		_fieldValueCache.put(key, fieldValue);
		return fieldValue;
	}

	private Object readFieldValue(int id, FieldMetadata field) {
		ByteArrayBuffer buffer = bufferFor(id);
		HandlerVersion handlerVersion = field.containingClass().seekToField(_transaction, buffer, field);
		if (handlerVersion == HandlerVersion.INVALID) {
			return null;
		}
		
		QueryingReadContext context = new QueryingReadContext(_transaction, handlerVersion._number, buffer, id);
		return field.read(context);
	}

	private ByteArrayBuffer bufferFor(int id) {
		ByteArrayBuffer cachedBuffer = _bufferCache.get(id);
		if (null != cachedBuffer)
			return cachedBuffer;
		
		ByteArrayBuffer buffer = _container.readBufferById(_transaction, id);
		_bufferCache.put(id, buffer);
		return buffer;
	}
}