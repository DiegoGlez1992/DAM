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
package com.db4o.internal;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.handlers.array.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.query.processor.*;
import com.db4o.internal.reflect.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;
import com.db4o.reflect.generic.*;
import com.db4o.typehandlers.*;

/**
 * @exclude
 */
public class FieldMetadata extends ClassAspect implements StoredField {

    private ClassMetadata         _containingClass;

    private String         _name;
    
    protected boolean          _isArray;

    private boolean          _isNArray;

    private boolean          _isPrimitive;
    
    private ReflectField     _reflectField;


    private FieldMetadataState              _state = FieldMetadataState.NOT_LOADED;

    private Config4Field     _config;

    private Db4oTypeImpl     _db4oType;
    
    private BTree _index;

	protected ClassMetadata _fieldType;

	protected int _fieldTypeID;
	
    static final FieldMetadata[]  EMPTY_ARRAY = new FieldMetadata[0];

    public FieldMetadata(ClassMetadata classMetadata) {
        _containingClass = classMetadata;
    }

	protected final Class translatorStoredClass(ObjectTranslator translator) {
		try {
			return translator.storedClass();
		} catch (RuntimeException e) {
			throw new ReflectException(e);
		}
	}

    FieldMetadata(ClassMetadata containingClass, ReflectField field, ClassMetadata fieldType) {
    	this(containingClass);
        init(field.getName());
        _reflectField = field;
        _fieldType = fieldType;
        _fieldTypeID = fieldType.getID();
        
        // TODO: beautify !!!  possibly pull up isPrimitive to ReflectField
        boolean isPrimitive = field instanceof GenericField
        	? ((GenericField)field).isPrimitive()
        	: false;
        configure(field.getFieldType(), isPrimitive);
        checkDb4oType();
        setAvailable();
    }

	protected void setAvailable() {
		_state = FieldMetadataState.AVAILABLE;
	}
    
    protected FieldMetadata(int fieldTypeID){
        _fieldTypeID = fieldTypeID;
    }
    
    public FieldMetadata(ClassMetadata containingClass, String name,
			int fieldTypeID, boolean primitive, boolean isArray, boolean isNArray) {
    	this(containingClass);
    	init(name, fieldTypeID, primitive, isArray, isNArray);
	}

	protected FieldMetadata(ClassMetadata containingClass, String name) {
		this(containingClass);
		init(name);
	}

	public void addFieldIndex(ObjectIdContextImpl context)  throws FieldIndexException {
        if (! hasIndex()) {
            incrementOffset(context, context);
            return;
        }
        try {
            addIndexEntry(context.transaction(), context.objectId(), readIndexEntry(context));
        } catch (CorruptionException exc) {
            throw new FieldIndexException(exc,this);
        } 
    }
    
    protected final void addIndexEntry(StatefulBuffer a_bytes, Object indexEntry) {
        addIndexEntry(a_bytes.transaction(), a_bytes.getID(), indexEntry);
    }

    public void addIndexEntry(Transaction trans, int parentID, Object indexEntry) {
        if (! hasIndex()) {
            return;
        }
            
        BTree index = getIndex(trans);
        index.add(trans, createFieldIndexKey(parentID, indexEntry));
    }

	protected FieldIndexKey createFieldIndexKey(int parentID, Object indexEntry) {
		Object convertedIndexEntry = indexEntryFor(indexEntry);
		return new FieldIndexKeyImpl(parentID,  convertedIndexEntry);
	}

	protected Object indexEntryFor(Object indexEntry) {
		return _reflectField.indexEntry(indexEntry);
	}
    
    public boolean canUseNullBitmap(){
        return true;
    }
    
    public final Object readIndexEntry(ObjectIdContext context) throws CorruptionException, Db4oIOException {
        IndexableTypeHandler indexableTypeHandler = (IndexableTypeHandler) HandlerRegistry.correctHandlerVersion(context, getHandler());
        return indexableTypeHandler.readIndexEntry(context);
    }
    
    public void removeIndexEntry(Transaction trans, int parentID, Object indexEntry){
        if (! hasIndex()) {
            return;
        }
        
        BTree index = getIndex(trans);
		if(index == null){
            return;
        }
        index.remove(trans, createFieldIndexKey(parentID,  indexEntry));
    }

    //TODO: Split into command query separation.
    public boolean alive() {
        if (_state == FieldMetadataState.AVAILABLE) {
            return true;
        }
        if (_state == FieldMetadataState.NOT_LOADED) {
            return load();
        }
        return _state == FieldMetadataState.AVAILABLE;
    }

	private boolean load() {
		if (_fieldType == null) {

		    // this may happen if the local ClassMetadataRepository
		    // has not been updated from the server and presumably 
		    // in some refactoring cases. 

		    // We try to heal the problem by re-reading the class.

		    // This could be dangerous, if the class type of a field
		    // has been modified.

		    // TODO: add class refactoring features

		    _fieldType = detectFieldType();
		    checkFieldTypeID();
		}

		checkCorrectTypeForField();

		if(_fieldType == null || _reflectField == null){
		    _state = FieldMetadataState.UNAVAILABLE;
		    _reflectField = null;
		    return false;
		}
		
		if(updating()){
			return false;
		}
		
		setAvailable();
		checkDb4oType();
		return true;
	}

	private boolean shouldStoreField() {
		return !_reflectField.isTransient() || (_containingClass != null && _containingClass.shouldStoreTransientFields());
	}

    public boolean updating() {
        return _state == FieldMetadataState.UPDATING;
    }

    private void checkFieldTypeID() {
    	
        int id = _fieldType != null ? _fieldType.getID() : 0;
        if (_fieldTypeID == 0) {
            _fieldTypeID = id;
            return;
        }
        if(id > 0 && id != _fieldTypeID){
            // wrong type, refactoring, field should be turned off
        	// TODO: it would be cool to log something here
            _fieldType = null;
        }
    }

    boolean canAddToQuery(String fieldName){
        if(! alive()){
            return false;
        }
        return fieldName.equals(getName())  && containingClass() != null && !containingClass().isInternal(); 
    }
    
    private boolean canHold(ReflectClass type) {
        if (type == null) {
        	throw new ArgumentNullException();
        }
        final TypeHandler4 typeHandler = getHandler();
		if (typeHandler instanceof QueryableTypeHandler) {
        	if (((QueryableTypeHandler)typeHandler).descendsIntoMembers()) {
        		return true;
        	}
        }
		ReflectClass classReflector = fieldType().classReflector();
		if (classReflector.isCollection()) {
			return true;
		}
		return classReflector.isAssignableFrom(type);
    }

    public GenericReflector reflector() {
        ObjectContainerBase container = container();
        if (container == null) {
            return null;
        }
        return container.reflector();
    }

    public Object coerce(ReflectClass valueClass, Object value) {
    	
    	 if (value == null) {
             return _isPrimitive ? No4.INSTANCE : value;
         }
    	
    	if (valueClass == null) {
    		throw new ArgumentNullException();
    	}
    	
        if(getHandler() instanceof PrimitiveHandler){
            return ((PrimitiveHandler)getHandler()).coerce(valueClass, value);
        }

        if(! canHold(valueClass)){
            return No4.INSTANCE;
        }
        
        return value;
    }

    public final boolean canLoadByIndex() {
        return Handlers4.canLoadFieldByIndex(getHandler());
    }

	public final void cascadeActivation(ActivationContext context) {
        if (! alive()) {
            return;
        }
        
        Object cascadeTo = cascadingTarget(context);
        if (cascadeTo == null) {
        	return;
        }
        
        final ActivationContext cascadeContext = context.forObject(cascadeTo);
        ClassMetadata classMetadata = cascadeContext.classMetadata();
        if(classMetadata == null){
        	return;
        }
        ensureObjectIsActive(cascadeContext);
		Handlers4.cascadeActivation(cascadeContext, classMetadata.typeHandler());
    }

    private void ensureObjectIsActive(ActivationContext context) {
        if(!context.depth().mode().isActivate()){
            return;
        }
        if(Handlers4.isValueType(getHandler())){
            return;
        }
        ObjectContainerBase container = context.container();
        ClassMetadata classMetadata = container.classMetadataForObject(context.targetObject());
        if(classMetadata == null || !classMetadata.hasIdentity()){
            return;
        }
        if(container.isActive(context.targetObject())){
            return;
        }
        container.stillToActivate(context.descend());
    }

	protected final Object cascadingTarget(ActivationContext context) {
		if (context.depth().mode().isDeactivate()) {
			if (null == _reflectField) {
				return null;
			}
			return fieldAccessor().get( _reflectField, context.targetObject());
		}
		return getOrCreate(context.transaction(), context.targetObject());
	}

    private void checkDb4oType() {
        if (_reflectField != null) {
            if (container()._handlers.ICLASS_DB4OTYPE.isAssignableFrom(_reflectField.getFieldType())) {
                _db4oType = HandlerRegistry.getDb4oType(_reflectField.getFieldType());
            }
        }
    }

    void collectConstraints(Transaction trans, QConObject a_parent,
        Object a_template, Visitor4 a_visitor) {
        Object obj = getOn(trans, a_template);
        if (obj != null) {
            Collection4 objs = Platform4.flattenCollection(trans.container(), obj);
            Iterator4 j = objs.iterator();
            while (j.moveNext()) {
                obj = j.current();
                if (obj != null) {
                    
                    if (_isPrimitive && !_isArray) {
                        Object nullValue = _reflectField.getFieldType().nullValue();
						if (obj.equals(nullValue)) {
                            return;
                        }
                    }
                    
                    if(Platform4.ignoreAsConstraint(obj)){
                    	return;
                    }
                    if (!a_parent.hasObjectInParentPath(obj)) {
                        QConObject constraint = new QConObject(trans, a_parent,
                                qField(trans), obj);
                        constraint.byExample();
                        a_visitor.visit(constraint);
                    }
                }
            }
        }
    }
    
    public final void collectIDs(CollectIdContext context) throws FieldIndexException {
        if (! alive()) {
        	incrementOffset(context.buffer(), context);
            return ;
        }
        
        final TypeHandler4 handler = HandlerRegistry.correctHandlerVersion(context, getHandler());
        Handlers4.collectIdsInternal(context, handler, linkLength(context), true);
    }

    void configure(ReflectClass clazz, boolean isPrimitive) {
        _isArray = clazz.isArray();
        if (_isArray) {
            ReflectArray reflectArray = reflector().array();
            _isNArray = reflectArray.isNDimensional(clazz);
            _isPrimitive = reflectArray.getComponentType(clazz).isPrimitive();
        } else {
        	_isPrimitive = isPrimitive | clazz.isPrimitive();
        }
    }
    
    protected final TypeHandler4 wrapHandlerToArrays(TypeHandler4 handler) {
        if(handler == null){
            return null;
        }
        if (_isNArray) {
            return new MultidimensionalArrayHandler(handler, arraysUsePrimitiveClassReflector());
        } 
        if (_isArray) {
            return new ArrayHandler(handler, arraysUsePrimitiveClassReflector());
        }
        return handler;
    }

    private boolean arraysUsePrimitiveClassReflector() {
    	return _isPrimitive;
    }

    public void deactivate(ActivationContext context) {
        
    	if (!alive() || !shouldStoreField()) {
            return;
        }
    	
        boolean isEnumClass = _containingClass.isEnum();
		if (_isPrimitive && !_isArray) {
			if (!isEnumClass) {
				Object nullValue = _reflectField.getFieldType().nullValue();
				fieldAccessor().set(_reflectField, context.targetObject(), nullValue);
			}
			return;
		}
		if (context.depth().requiresActivation()) {
			cascadeActivation(context);
		}
		if (!isEnumClass) {
			fieldAccessor().set(_reflectField, context.targetObject(), null);
		}
    }

	private FieldAccessor fieldAccessor() {
		return _containingClass.fieldAccessor();
	}

    public void delete(DeleteContextImpl context, boolean isUpdate) throws FieldIndexException {
        if (! checkAlive(context, context)) {
            return;
        }
        try {
            removeIndexEntry(context);
            if(isUpdate && ! isStruct()){
            	incrementOffset(context, context);
            	return;
            }
            StatefulBuffer buffer = (StatefulBuffer) context.buffer();
            final DeleteContextImpl childContext = new DeleteContextImpl(context, getStoredType(), _config);
            context.slotFormat().doWithSlotIndirection(buffer, getHandler(), new Closure4() {
                public Object run() {
                    childContext.delete(getHandler());
                    return null;
                }
            });
        } catch (CorruptionException exc) {
            throw new FieldIndexException(exc, this);
        }
    }

    private final void removeIndexEntry(DeleteContextImpl context) throws CorruptionException, Db4oIOException {
        if(! hasIndex()){
            return;
        }
        int offset = context.offset();
        Object obj = readIndexEntry(context);
        removeIndexEntry(context.transaction(), context.objectId(), obj);
        context.seek(offset);
    }


    public boolean equals(Object obj) {
        if (! (obj instanceof FieldMetadata)) {
            return false;
        }
        FieldMetadata other = (FieldMetadata) obj;
        other.alive();
        alive();
        return other._isPrimitive == _isPrimitive
            && other._fieldType == _fieldType
            && other._name.equals(_name);
    }

    public int hashCode() {
    	return _name.hashCode();
    }
    
    public final Object get(Object onObject) {
        return get(null, onObject);
    }
    
    public final Object get(Transaction trans, Object onObject) {
		if (_containingClass == null) {
			return null;
		}
		ObjectContainerBase container = container();
		if (container == null) {
			return null;
		}
		synchronized (container.lock()) {
		    
            // FIXME: The following is not really transactional.
            //        This will work OK for normal C/S and for
            //        single local mode but the transaction will
            //        be wrong for MTOC.
		    if(trans == null){
		        trans = container.transaction();
		    }
		    
			container.checkClosed();
			ObjectReference ref = trans.referenceForObject(onObject);
			if (ref == null) {
				return null;
			}
			int id = ref.getID();
			if (id <= 0) {
				return null;
			}
			UnmarshallingContext context = new UnmarshallingContext(trans, ref, Const4.ADD_TO_ID_TREE, false);
			context.activationDepth(new LegacyActivationDepth(1));
            return context.readFieldValue(this);
		}
	}

    public String getName() {
        return _name;
    }

    public final ClassMetadata fieldType() {
        // alive needs to be checked by all callers: Done
    	return _fieldType;
    }
    
    public TypeHandler4 getHandler() {
    	if (_fieldType == null) {
    		return null;
    	}
        // alive needs to be checked by all callers: Done
        return wrapHandlerToArrays(_fieldType.typeHandler());
    }
    
    public int fieldTypeID(){
        // alive needs to be checked by all callers: Done
        return _fieldTypeID;
    }

    public Object getOn(Transaction trans, Object onObject) {
		if (alive()) {
			return fieldAccessor().get( _reflectField, onObject);
		}
		return null;
	}

    /**
	 * dirty hack for com.db4o.types some of them (BlobImpl) need to be set automatically
	 * TODO: Derive from FieldMetadata for Db4oTypes
	 */
    public Object getOrCreate(Transaction trans, Object onObject) {
		if (!alive()) {
			return null;
		}
		Object obj = fieldAccessor().get( _reflectField, onObject);
		if (_db4oType != null && obj == null) {
			obj = _db4oType.createDefault(trans);
			fieldAccessor().set(_reflectField, onObject, obj);
		}
		return obj;
	}

    public final ClassMetadata containingClass() {
        // alive needs to be checked by all callers: Done
        return _containingClass;
    }

    public ReflectClass getStoredType() {
        if(_reflectField == null){
            return null;
        }
        return Handlers4.baseType(_reflectField.getFieldType());
    }
    
    public ObjectContainerBase container(){
        if(_containingClass == null){
            return null;
        }
        return _containingClass.container();
    }
    
    public boolean hasConfig() {
    	return _config!=null;
    }
    
    public boolean hasIndex() {
        return _index != null;
    }

    public final void init(String name) {
        _name = name;
        initConfiguration(name);
    }

	final void initConfiguration(String name) {
		final Config4Class containingClassConfig = _containingClass.config();
		if (containingClassConfig == null) {
		    return;
		}
        _config = containingClassConfig.configField(name);
        if (Debug4.configureAllFields) {
			if (_config == null) {
			    _config = (Config4Field) containingClassConfig.objectField(_name);
			}
		}
	}
    
    public void init(String name, int fieldTypeID, boolean isPrimitive, boolean isArray, boolean isNArray) {
        _fieldTypeID = fieldTypeID;
        _isPrimitive = isPrimitive;
        _isArray = isArray;
        _isNArray = isNArray;
        
        init(name);
        loadFieldTypeById();
        alive();
    }

    private boolean _initialized=false;

    final void initConfigOnUp(Transaction trans) {
    	if (_initialized) {
    		return;
    	}
    	_initialized = true;
        if (_config != null) {
            _config.initOnUp(trans, this);
        }
    }

    public void activate(UnmarshallingContext context) {
        if(! checkAlive(context, context)) {
            return;
        }
        if(!shouldStoreField()) {
            incrementOffset(context, context);
            return;
        }
        Object toSet = read(context);
        informAboutTransaction(toSet, context.transaction());
        set(context.persistentObject(), toSet);
    }
    
    public void attemptUpdate(UnmarshallingContext context) {
        if(! updating()){
            incrementOffset(context, context);
            return;
        }
        int savedOffset = context.offset();
        try{
            Object toSet = context.read(getHandler());
            if(toSet != null){
                set(context.persistentObject(), toSet);
            }
        }catch(Exception ex){
            
            // FIXME: COR-547 Diagnostics here please.
            
            context.buffer().seek(savedOffset);
            incrementOffset(context, context);
        }
    }
    
    private boolean checkAlive(AspectVersionContext context, HandlerVersionContext versionContext){
    	if(! checkEnabled(context, versionContext)){
			return false;
		}		
		boolean alive = alive(); 
		if (!alive) {
		    incrementOffset((ReadBuffer)context, versionContext);
		}
		return alive;
    }

    private void informAboutTransaction(Object obj, Transaction trans){
        if (_db4oType != null  && obj != null) {
            ((Db4oTypeImpl) obj).setTrans(trans);
        }
    }

    public boolean isArray() {
        return _isArray;
    }
    
    public int linkLength(HandlerVersionContext context) {
        alive();
        
        return calculateLinkLength(context);
    }
    
    private int calculateLinkLength(HandlerVersionContext context){
    	return Handlers4.calculateLinkLength(HandlerRegistry.correctHandlerVersion(context, getHandler()));
    }
    
    public void loadFieldTypeById() {
        _fieldType = container().classMetadataForID(_fieldTypeID);
    }
    
    private ClassMetadata detectFieldType() {
        ReflectClass claxx = _containingClass.classReflector();
        if (claxx == null) {
            return null;
        }
        _reflectField = claxx.getDeclaredField(_name);
        if (_reflectField == null) {
            return null;
        }
        ReflectClass fieldType = _reflectField.getFieldType();
        if(fieldType == null) {
        	return null;
        }
		return Handlers4.erasedFieldType(container(), fieldType);
    }

	protected TypeHandler4 typeHandlerForClass(ObjectContainerBase container, ReflectClass fieldType) {
        container.showInternalClasses(true);
        try{
        	return container.typeHandlerForClass(Handlers4.baseType(fieldType));
        }finally{
        	container.showInternalClasses(false);
        }
    }

    private void checkCorrectTypeForField() {
        ClassMetadata currentFieldType = detectFieldType();
        if (currentFieldType == null){
            _reflectField = null;
            _state = FieldMetadataState.UNAVAILABLE;
            return;
        }
        if (currentFieldType == _fieldType && Handlers4.baseType(_reflectField.getFieldType()).isPrimitive() == _isPrimitive) {
        	return;
        }
    	// special case when migrating from type handler ids
    	// to class metadata ids which caused
    	// any interface metadata id to be mapped to UNTYPED_ID
        if (Handlers4.isUntyped(currentFieldType.typeHandler())
        	&& Handlers4.isUntyped(_fieldType.typeHandler())) {
        	return;
        }	
        	
        // FIXME: COR-547 Diagnostics here please.
        _state = FieldMetadataState.UPDATING;
    }

    private UpdateDepth adjustUpdateDepthForCascade(Object obj, UpdateDepth updateDepth) {
    	return updateDepth.adjustUpdateDepthForCascade(_containingClass.isCollection(obj));
    }

    private boolean cascadeOnUpdate(Config4Class parentClassConfiguration) {
        return ((parentClassConfiguration != null && (parentClassConfiguration.cascadeOnUpdate().definiteYes())) || (_config != null && (_config.cascadeOnUpdate().definiteYes())));
    }
    
    public void marshall(MarshallingContext context, Object obj){
    	
        // alive needs to be checked by all callers: Done
        UpdateDepth updateDepth = context.updateDepth();
        if (obj != null && cascadeOnUpdate(context.classConfiguration())) {
            context.updateDepth(adjustUpdateDepthForCascade(obj, updateDepth));
        }
        context.writeObjectWithCurrentState(getHandler(), obj);
        context.updateDepth(updateDepth);
        
        if(hasIndex()){
            context.addIndexEntry(this, obj);
        }
    }
    
    public boolean needsArrayAndPrimitiveInfo(){
        return true;
    }
    
    public PreparedComparison prepareComparison(Context context, Object obj) {
        if (!alive()) {
        	return null;
        }
        return Handlers4.prepareComparisonFor(getHandler(), context, obj);
    }

	public QField qField(Transaction a_trans) {
        int classMetadataID = 0;
        if(_containingClass != null){
            classMetadataID = _containingClass.getID();
        }
        return new QField(a_trans, _name, this, classMetadataID, _handle);
    }

    public Object read(ObjectIdContext context) {
        if(!canReadFromSlot((AspectVersionContext) context)) {
			incrementOffset(context, context);
            return null;
        }
        return context.read(getHandler());
    }

	private boolean canReadFromSlot(AspectVersionContext context) {
    	if(! isEnabledOn(context)){
    		return false;
    	}
    	if(alive()) {
    		return true;
    	}
		return _state != FieldMetadataState.NOT_LOADED;
	}
    
    void refresh() {
        ClassMetadata newFieldType = detectFieldType();
        if (newFieldType != null && newFieldType.equals(_fieldType)) {
        	return;
        }
        _reflectField = null;
        _state = FieldMetadataState.UNAVAILABLE;
    }

    // FIXME: needs test case
    public void rename(String newName) {
        ObjectContainerBase container = container();
        if (! container.isClient()) {
            _name = newName;
            _containingClass.setStateDirty();
            _containingClass.write(container.systemTransaction());
        } else {
            Exceptions4.throwRuntimeException(58);
        }
    }

    public void set(Object onObject, Object obj){
    	// TODO: remove the following if and check callers
    	if (null == _reflectField) return;
    	fieldAccessor().set(_reflectField, onObject, obj);
    }

    void setName(String a_name) {
        _name = a_name;
    }

    boolean supportsIndex() {
        return alive() && 
            (getHandler() instanceof Indexable4)  && 
            (! Handlers4.isUntyped(getHandler()));
    }
    
    public final void traverseValues(final Visitor4 userVisitor) {
        if(! alive()){
            return;
        }
        traverseValues(container().transaction(), userVisitor);
    }
    
    public final void traverseValues(final Transaction transaction, final Visitor4 userVisitor) {
        if(! alive()){
            return;
        }
        assertHasIndex();
        ObjectContainerBase stream = transaction.container();
        if(stream.isClient()){
            Exceptions4.throwRuntimeException(Messages.CLIENT_SERVER_UNSUPPORTED);
        }
        synchronized(stream.lock()){
            final Context context = transaction.context();
            _index.traverseKeys(transaction, new Visitor4() {
                public void visit(Object obj) {
                    FieldIndexKey key = (FieldIndexKey) obj;
                    userVisitor.visit(((IndexableTypeHandler)getHandler()).indexEntryToObject(context, key.value()));
                }
            });
        }
    }
    
	private void assertHasIndex() {
		if(! hasIndex()){
            Exceptions4.throwRuntimeException(Messages.ONLY_FOR_INDEXED_FIELDS);
        }
	}

    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (_containingClass != null) {
            sb.append(_containingClass.getName());
            sb.append(".");
        }
        sb.append(getName());
        return sb.toString();
    }

    private void initIndex(Transaction systemTrans) {        
        initIndex(systemTrans, 0);
    }

    public void initIndex(Transaction systemTrans, final int id) {
    	if(_index != null){
    		throw new IllegalStateException();
        }
        if(systemTrans.container().isClient()){
            return;
        }
        _index = newBTree(systemTrans, id);
    }

	protected final BTree newBTree(Transaction systemTrans, final int id) {
		ObjectContainerBase stream = systemTrans.container();
		Indexable4 indexHandler = indexHandler(stream);
		if(indexHandler==null) {
			if(Debug4.atHome) {
				System.err.println("Could not create index for "+this+": No index handler found");
			}
			return null;
		}
		return new BTree(systemTrans, id, new FieldIndexKeyHandler(indexHandler));
	}

	protected Indexable4 indexHandler(ObjectContainerBase stream) {
		if(_reflectField ==null) {
		    return null;
		}
		ReflectClass indexType = _reflectField.indexType();
		TypeHandler4 classHandler = typeHandlerForClass(stream,indexType);
		if(! (classHandler instanceof Indexable4)){
		    return null;
		}
		return (Indexable4) classHandler;
	}
    
	/** @param trans */
	public BTree getIndex(Transaction trans){
        return _index;
    }

    public boolean isVirtual() {
        return false;
    }

    public boolean isPrimitive() {
        return _isPrimitive;
    }
	
	public BTreeRange search(Transaction transaction, Object value) {
		assertHasIndex();
		Object transActionalValue = Handlers4.wrapWithTransactionContext(transaction, value, getHandler());
		BTreeNodeSearchResult lowerBound = searchLowerBound(transaction, transActionalValue);
	    BTreeNodeSearchResult upperBound = searchUpperBound(transaction, transActionalValue);	    
		return lowerBound.createIncludingRange(upperBound);
	}

    private BTreeNodeSearchResult searchUpperBound(Transaction transaction, final Object value) {
		return searchBound(transaction, Integer.MAX_VALUE, value);
	}

	private BTreeNodeSearchResult searchLowerBound(Transaction transaction, final Object value) {
		return searchBound(transaction, 0, value);
	}

	private BTreeNodeSearchResult searchBound(Transaction transaction, int parentID, Object keyPart) {
	    return getIndex(transaction).searchLeaf(transaction, createFieldIndexKey(parentID, keyPart), SearchTarget.LOWEST);
	}

	public boolean rebuildIndexForClass(LocalObjectContainer stream, ClassMetadata classMetadata) {
		// FIXME: BTree traversal over index here.
		long[] ids = classMetadata.getIDs();		
		for (int i = 0; i < ids.length; i++) {
		    rebuildIndexForObject(stream, classMetadata, (int)ids[i]);
		}
		return ids.length > 0;
	}

	protected void rebuildIndexForObject(LocalObjectContainer stream, final ClassMetadata classMetadata, final int objectId) throws FieldIndexException {
		StatefulBuffer writer = stream.readStatefulBufferById(stream.systemTransaction(), objectId);
		if (writer != null) {
		    rebuildIndexForWriter(stream, writer, objectId);
		} else {
		    if(Deploy.debug){
		        throw new Db4oException("Unexpected null object for ID");
		    }
		}
	}

	protected void rebuildIndexForWriter(LocalObjectContainer stream, StatefulBuffer writer, final int objectId) {
		ObjectHeader oh = new ObjectHeader(stream, writer);
		Object obj = readIndexEntryForRebuild(writer, oh);
		addIndexEntry(stream.systemTransaction(), objectId, obj);
	}

	private final Object readIndexEntryForRebuild(StatefulBuffer writer, ObjectHeader oh) {
	    ClassMetadata classMetadata = oh.classMetadata();
        if(classMetadata == null){
            return defaultValueForFieldType();
        }
        ObjectIdContextImpl context = new ObjectIdContextImpl(writer.transaction(), writer, oh, writer.getID());
        if(! classMetadata.seekToField(context, this)){
            return defaultValueForFieldType();
        }
        try {
            return readIndexEntry(context);
        } catch (CorruptionException exc) {
            throw new FieldIndexException(exc,this);
        } 
	}

	private Object defaultValueForFieldType() {
	    final TypeHandler4 handler = _fieldType.typeHandler();
	    return (handler instanceof PrimitiveHandler)
	    	? ((PrimitiveHandler)handler).primitiveNull()
	    	: null;
    }

    public final void dropIndex(LocalTransaction systemTrans) {
        if(_index == null){
            return;
        }
        ObjectContainerBase stream = systemTrans.container(); 
        if (stream.configImpl().messageLevel() > Const4.NONE) {
            stream.message("dropping index " + toString());
        }
        _index.free(systemTrans);
        stream.setDirtyInSystemTransaction(containingClass());
        _index = null;
    }    
    
    public void defragAspect(final DefragmentContext context) {
    	if(!canDefragment()){
    		throw new IllegalStateException("Field '" + toString() + "' cannot be defragmented at this time.");
    	}
    	final TypeHandler4 correctTypeHandlerVersion = HandlerRegistry.correctHandlerVersion(context, getHandler(), _fieldType);
        context.slotFormat().doWithSlotIndirection(context, correctTypeHandlerVersion, new Closure4() {
            public Object run() {
                context.defragment(correctTypeHandlerVersion);
                return null;
            }
        });
    }

	private boolean canDefragment() {
		if (alive() || updating() ) {
			return true;
		}
    	if(_fieldType == null || getHandler() == null){
    		return false;
    	}
    	return ! _fieldType.stateDead();
	}
    
	public void createIndex() {
	    
		if(hasIndex()) {
			return;
		}
		LocalObjectContainer container= (LocalObjectContainer) container();
		
        if (container.configImpl().messageLevel() > Const4.NONE) {
            container.message("creating index " + toString());
        }
	    initIndex(container.systemTransaction());
	    container.setDirtyInSystemTransaction(containingClass());
        reindex(container);
	}

	private void reindex(LocalObjectContainer container) {
		ClassMetadata clazz = containingClass();		
		if (rebuildIndexForClass(container, clazz)) {
		    container.systemTransaction().commit();
		}
	}

    public AspectType aspectType() {
        return AspectType.FIELD;
    }

    // overriden in VirtualFieldMetadata
	public boolean canBeDisabled() {
		return true;
	}

	public void dropIndex() {
		dropIndex((LocalTransaction)container().systemTransaction());
	}

	public boolean canUpdateFast() {
		if(hasIndex()){
			return false;
		}
		if(isStruct()){
			return false;
		}
		return true;
	}

	private boolean isStruct() {
		return _fieldType != null && _fieldType.isStruct();
	}

}