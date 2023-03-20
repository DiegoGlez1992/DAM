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

import com.db4o.foundation.*;
import com.db4o.internal.diagnostic.*;
import com.db4o.internal.encoding.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.handlers.array.*;
import com.db4o.internal.handlers.versions.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.replication.*;
import com.db4o.reflect.*;
import com.db4o.reflect.generic.*;
import com.db4o.typehandlers.*;


/**
 * @exclude
 * 
 * TODO: This class was written to make ObjectContainerBase 
 * leaner, so TransportObjectContainer has less members.
 * 
 * All functionality of this class should become part of 
 * ObjectContainerBase and the functionality in 
 * ObjectContainerBase should delegate to independent
 * modules without circular references.
 * 
 */
public final class HandlerRegistry {
    
    public static final byte HANDLER_VERSION = (byte) 10;
    
    private final ObjectContainerBase _container;  // this is the master container and not valid
	                                   // for TransportObjectContainer

    private static final Db4oTypeImpl[]   _db4oTypes = { new BlobImpl() };

    private TypeHandler4 _openArrayHandler;
    
    private TypeHandler4 _openMultiDimensionalArrayHandler;
    
    private TypeHandler4 _openTypeHandler;

    public StringHandler _stringHandler;
    
    private Hashtable4 _mapIdToTypeInfo = newHashtable();
    
    private Hashtable4 _mapReflectorToClassMetadata = newHashtable();

    private int _highestBuiltinTypeID = Handlers4.ANY_ARRAY_N_ID + 1;

    private final VirtualFieldMetadata[]         _virtualFields = new VirtualFieldMetadata[3]; 

    private final Hashtable4        _mapReflectorToTypeHandler  = newHashtable();
    
    private SharedIndexedFields              		_indexes;
    
    Db4oReplicationReferenceProvider _replicationReferenceProvider;
    
    private final DiagnosticProcessor      _diagnosticProcessor;
    
    public boolean                 i_encrypt;
    byte[]                  i_encryptor;
    int                     i_lastEncryptorByte;
    
    final GenericReflector                _reflector;
    
    private final HandlerVersionRegistry _handlerVersions;
    
    private LatinStringIO _stringIO;
    
    public ReflectClass ICLASS_COMPARE;
    ReflectClass ICLASS_DB4OTYPE;
    ReflectClass ICLASS_DB4OTYPEIMPL;
	public ReflectClass ICLASS_INTERNAL;
    ReflectClass ICLASS_UNVERSIONED;
    public ReflectClass ICLASS_OBJECT;
    ReflectClass ICLASS_OBJECTCONTAINER;
	public ReflectClass ICLASS_STATICCLASS;
	public ReflectClass ICLASS_STRING;
    ReflectClass ICLASS_TRANSIENTCLASS;

	private PrimitiveTypeMetadata _untypedArrayMetadata;

	private PrimitiveTypeMetadata _untypedMultiDimensionalMetadata;

    HandlerRegistry(final ObjectContainerBase container, byte stringEncoding, GenericReflector reflector) {
        
        _handlerVersions = new HandlerVersionRegistry(this);
        
        _stringIO = BuiltInStringEncoding.stringIoForEncoding(stringEncoding, container.configImpl().stringEncoding());
    	
    	_container = container;
    	container._handlers = this;
        
        _reflector = reflector;
        _diagnosticProcessor = container.configImpl().diagnosticProcessor();
    	
    	initClassReflectors(reflector);
        
        _indexes = new SharedIndexedFields();
        
        _virtualFields[0] = _indexes._version;
        _virtualFields[1] = _indexes._uUID;
        _virtualFields[2] = _indexes._commitTimestamp;

        registerBuiltinHandlers();
        
        registerPlatformTypes();
        
        initArrayHandlers();
        
        Platform4.registerPlatformHandlers(container);
    }

    private void initArrayHandlers() {
        TypeHandler4 elementHandler = openTypeHandler();
        
        _untypedArrayMetadata = new PrimitiveTypeMetadata(
            container(), 
            new ArrayHandler(elementHandler, false), 
            Handlers4.ANY_ARRAY_ID,
            ICLASS_OBJECT);
		_openArrayHandler = _untypedArrayMetadata.typeHandler();
        mapTypeInfo(
            Handlers4.ANY_ARRAY_ID, 
            _untypedArrayMetadata, 
            null );

        _untypedMultiDimensionalMetadata = new PrimitiveTypeMetadata(
            container(), 
            new MultidimensionalArrayHandler(elementHandler, false), 
            Handlers4.ANY_ARRAY_N_ID,
            ICLASS_OBJECT);
		_openMultiDimensionalArrayHandler = _untypedMultiDimensionalMetadata.typeHandler();
        mapTypeInfo(
            Handlers4.ANY_ARRAY_N_ID, 
            _untypedMultiDimensionalMetadata, 
            null );
    }
    
    private void registerPlatformTypes() {
        NetTypeHandler[] handlers = Platform4.types(_container.reflector());
        for (int i = 0; i < handlers.length; i++) {
        	registerNetTypeHandler(handlers[i]);
        }
    }

	public void registerNetTypeHandler(NetTypeHandler handler) {
		handler.registerReflector(_reflector);
		GenericConverter converter = (handler instanceof GenericConverter) ? (GenericConverter)handler : null;
		registerBuiltinHandler(handler.getID(), handler, true, handler.getName(), converter);
	}
    
    private void registerBuiltinHandlers(){
        
        IntHandler intHandler = new IntHandler();
        registerBuiltinHandler(Handlers4.INT_ID, intHandler);
        registerHandlerVersion(intHandler, 0, new IntHandler0());
        
        LongHandler longHandler = new LongHandler();
        registerBuiltinHandler(Handlers4.LONG_ID, longHandler);
        registerHandlerVersion(longHandler, 0, new LongHandler0());
        
        FloatHandler floatHandler = new FloatHandler();
        registerBuiltinHandler(Handlers4.FLOAT_ID, floatHandler);
        registerHandlerVersion(floatHandler, 0, new FloatHandler0());
        
        BooleanHandler booleanHandler = new BooleanHandler();
        registerBuiltinHandler(Handlers4.BOOLEAN_ID, booleanHandler);
        // TODO: Are we missing a boolean handler version?
        
        DoubleHandler doubleHandler = new DoubleHandler();
        registerBuiltinHandler(Handlers4.DOUBLE_ID, doubleHandler);
        registerHandlerVersion(doubleHandler, 0, new DoubleHandler0());
        
        ByteHandler byteHandler = new ByteHandler();
        registerBuiltinHandler(Handlers4.BYTE_ID, byteHandler);
        // TODO: Are we missing a byte handler version?

        CharHandler charHandler = new CharHandler();
        registerBuiltinHandler(Handlers4.CHAR_ID, charHandler);
        // TODO: Are we missing a char handler version?
        
        ShortHandler shortHandler = new ShortHandler();
        registerBuiltinHandler(Handlers4.SHORT_ID, shortHandler);
        registerHandlerVersion(shortHandler, 0, new ShortHandler0());
        
        _stringHandler = new StringHandler();
        registerBuiltinHandler(Handlers4.STRING_ID, _stringHandler);
        registerHandlerVersion(_stringHandler, 0, new StringHandler0());

        DateHandler dateHandler = new DateHandler();
        registerBuiltinHandler(Handlers4.DATE_ID, dateHandler);
        registerHandlerVersion(dateHandler, 0, new DateHandler0());
        
        registerUntypedHandlers();
        
        registerCompositeHandlerVersions();
    }

    private void registerUntypedHandlers() {
        _openTypeHandler = new OpenTypeHandler(container());
        PrimitiveTypeMetadata classMetadata = new ObjectTypeMetadata(container(), _openTypeHandler, Handlers4.UNTYPED_ID, ICLASS_OBJECT);
        map(Handlers4.UNTYPED_ID, classMetadata, ICLASS_OBJECT);
        registerHandlerVersion(_openTypeHandler, 0, new OpenTypeHandler0(container()));
        registerHandlerVersion(_openTypeHandler, 2, new OpenTypeHandler2(container()));
        registerHandlerVersion(_openTypeHandler, 7, new OpenTypeHandler7(container()));
    }
    
    private void registerCompositeHandlerVersions(){
        
        registerHandlerVersion(new StandardReferenceTypeHandler(), 0, new StandardReferenceTypeHandler0());
        
        ArrayHandler arrayHandler = new ArrayHandler();
        registerHandlerVersion(arrayHandler, 0, new ArrayHandler0());
        registerHandlerVersion(arrayHandler, 1, new ArrayHandler1());
        registerHandlerVersion(arrayHandler, 3, new ArrayHandler3());
        registerHandlerVersion(arrayHandler, 5, new ArrayHandler5());
        
        MultidimensionalArrayHandler multidimensionalArrayHandler = new MultidimensionalArrayHandler();
        registerHandlerVersion(multidimensionalArrayHandler, 0, new MultidimensionalArrayHandler0());
        registerHandlerVersion(multidimensionalArrayHandler, 3, new MultidimensionalArrayHandler3());
    }
    
    private void registerBuiltinHandler(int id, BuiltinTypeHandler handler) {
        registerBuiltinHandler(id, handler, true, null, null);
    }

    private void registerBuiltinHandler(int id, BuiltinTypeHandler typeHandler, boolean registerPrimitiveClass, String primitiveName, GenericConverter converter) {

        typeHandler.registerReflector(_reflector);
        if(primitiveName == null) {
        	primitiveName = typeHandler.classReflector().getName();
        }

        if(registerPrimitiveClass){
            _reflector.registerPrimitiveClass(id, primitiveName, converter);
        }
        
        ReflectClass classReflector = typeHandler.classReflector();
        
        PrimitiveTypeMetadata classMetadata = new PrimitiveTypeMetadata(container(), typeHandler, id, classReflector);
        
        map(id, classMetadata, classReflector);
        
        if(typeHandler instanceof PrimitiveHandler){
            ReflectClass primitiveClassReflector = 
                ((PrimitiveHandler) typeHandler).primitiveClassReflector();
            if(primitiveClassReflector != null){
                mapPrimitive(0, classMetadata, primitiveClassReflector);
            }
        }
    }
    
    private void map(
        int id,
        PrimitiveTypeMetadata classMetadata,  // TODO: remove when _mapIdToClassMetadata is gone 
        ReflectClass classReflector) {
        mapTypeInfo(id, classMetadata, classReflector);
        mapPrimitive(id, classMetadata, classReflector);
        if (id > _highestBuiltinTypeID) {
            _highestBuiltinTypeID = id;
        }
    }
    
    private void mapTypeInfo(
        int id,
        ClassMetadata classMetadata, 
        ReflectClass classReflector) {
        _mapIdToTypeInfo.put(id, new TypeInfo(classMetadata, classReflector));
    }
    
    private void mapPrimitive(int id, ClassMetadata classMetadata, ReflectClass classReflector) {
        mapClassToTypeHandler(classReflector, classMetadata.typeHandler());
        if(classReflector != null){
            _mapReflectorToClassMetadata.put(classReflector, classMetadata);
        }
    }

	private void mapClassToTypeHandler(ReflectClass classReflector, TypeHandler4 typeHandler) {
		_mapReflectorToTypeHandler.put(classReflector, typeHandler);
	}
	
	public void registerHandlerVersion(TypeHandler4 handler, int version, TypeHandler4 replacement) {
		if(replacement instanceof BuiltinTypeHandler) {
			((BuiltinTypeHandler)replacement).registerReflector(_reflector);
		}
	    _handlerVersions.put(handler, version, replacement);
    }

    public TypeHandler4 correctHandlerVersion(TypeHandler4 handler, int version){
        return _handlerVersions.correctHandlerVersion(handler, version);
    }
    
	public static TypeHandler4 correctHandlerVersion(HandlerVersionContext context, TypeHandler4 typeHandler, ClassMetadata classMetadata)
	{
		TypeHandler4 correctHandlerVersion = correctHandlerVersion(context, typeHandler);
		if (typeHandler != correctHandlerVersion)
		{
			correctClassMetadataOn(correctHandlerVersion, classMetadata);

			if (correctHandlerVersion instanceof ArrayHandler) {
				ArrayHandler arrayHandler = (ArrayHandler) correctHandlerVersion;
				correctClassMetadataOn(arrayHandler.delegateTypeHandler(), classMetadata);
			}
		}

		return correctHandlerVersion;
	}    

	private static void correctClassMetadataOn(TypeHandler4 typeHandler, ClassMetadata classMetadata)
	{
		if (typeHandler instanceof StandardReferenceTypeHandler) {
			StandardReferenceTypeHandler handler = (StandardReferenceTypeHandler) typeHandler;
			handler.classMetadata(classMetadata);
		}
	}
	
    ArrayType arrayType(Object obj) {
    	ReflectClass claxx = reflector().forObject(obj);
        if (! claxx.isArray()) {
            return ArrayType.NONE;
        }
        if (isNDimensional(claxx)) {
        	return ArrayType.MULTIDIMENSIONAL_ARRAY;
        } 
        return ArrayType.PLAIN_ARRAY;
    }
	
	public final void decrypt(ByteArrayBuffer reader) {
	    if(i_encrypt){
			int encryptorOffSet = i_lastEncryptorByte;
			byte[] bytes = reader._buffer;
			for (int i = reader.length() - 1; i >= 0; i--) {
				bytes[i] += i_encryptor[encryptorOffSet];
				if (encryptorOffSet == 0) {
					encryptorOffSet = i_lastEncryptorByte;
				} else {
					encryptorOffSet--;
				}
			}
	    }
	}
	
    public final void encrypt(ByteArrayBuffer reader) {
        if(i_encrypt){
	        byte[] bytes = reader._buffer;
	        int encryptorOffSet = i_lastEncryptorByte;
	        for (int i = reader.length() - 1; i >= 0; i--) {
	            bytes[i] -= i_encryptor[encryptorOffSet];
	            if (encryptorOffSet == 0) {
	                encryptorOffSet = i_lastEncryptorByte;
	            } else {
	                encryptorOffSet--;
	            }
	        }
        }
    }
    
    public void oldEncryptionOff() {
        i_encrypt = false;
        i_encryptor = null;
        i_lastEncryptorByte = 0;
        container().configImpl().oldEncryptionOff();
    }
    
    public final ReflectClass classForID(int id) {
        TypeInfo typeInfo = typeInfoForID(id);
        if(typeInfo == null){
            return null;
        }
        return typeInfo.classReflector;
    }
    
    private TypeInfo typeInfoForID(int id){
        return (TypeInfo)_mapIdToTypeInfo.get(id);
    }
    
	private void initClassReflectors(GenericReflector reflector){
		ICLASS_COMPARE = reflector.forClass(Const4.CLASS_COMPARE);
		ICLASS_DB4OTYPE = reflector.forClass(Const4.CLASS_DB4OTYPE);
		ICLASS_DB4OTYPEIMPL = reflector.forClass(Const4.CLASS_DB4OTYPEIMPL);
        ICLASS_INTERNAL = reflector.forClass(Const4.CLASS_INTERNAL);
        ICLASS_UNVERSIONED = reflector.forClass(Const4.CLASS_UNVERSIONED);
		ICLASS_OBJECT = reflector.forClass(Const4.CLASS_OBJECT);
		ICLASS_OBJECTCONTAINER = reflector
				.forClass(Const4.CLASS_OBJECTCONTAINER);
		ICLASS_STATICCLASS = reflector.forClass(Const4.CLASS_STATICCLASS);
		ICLASS_STRING = reflector.forClass(String.class);
		ICLASS_TRANSIENTCLASS = reflector
				.forClass(Const4.CLASS_TRANSIENTCLASS);
		
		Platform4.registerCollections(reflector);
    }
    
    void initEncryption(Config4Impl a_config){
        if (a_config.encrypt() && a_config.password() != null
            && a_config.password().length() > 0) {
            i_encrypt = true;
            i_encryptor = new byte[a_config.password().length()];
            for (int i = 0; i < i_encryptor.length; i++) {
                i_encryptor[i] = (byte) (a_config.password().charAt(i) & 0xff);
            }
            i_lastEncryptorByte = a_config.password().length() - 1;
            return;
        }
        
        oldEncryptionOff();
    }
    
    static Db4oTypeImpl getDb4oType(ReflectClass clazz) {
        for (int i = 0; i < _db4oTypes.length; i++) {
            if (clazz.isInstance(_db4oTypes[i])) {
                return _db4oTypes[i];
            }
        }
        return null;
    }

    public ClassMetadata classMetadataForId(int id) {
        TypeInfo typeInfo = typeInfoForID(id);
        if(typeInfo == null){
            return null;
        }
        return typeInfo.classMetadata;
    }

    ClassMetadata classMetadataForClass(ReflectClass clazz) {
        if (clazz == null) {
            return null;
        }
        if (clazz.isArray()) {
        	return isNDimensional(clazz)
        		? _untypedMultiDimensionalMetadata
        		: _untypedArrayMetadata;
        }
        return (ClassMetadata) _mapReflectorToClassMetadata.get(clazz);
    }
    
    public TypeHandler4 openTypeHandler(){
        return _openTypeHandler;
    }
    
    public TypeHandler4 openArrayHandler(ReflectClass clazz){
        if (clazz.isArray()) {
            if (isNDimensional(clazz)) {
                return _openMultiDimensionalArrayHandler;
            }
            return _openArrayHandler;
        }
        return null;
    }

	private boolean isNDimensional(ReflectClass clazz) {
		return reflector().array().isNDimensional(clazz);
	}
    
    public TypeHandler4 typeHandlerForClass(ReflectClass clazz){
        if (clazz == null) {
            return null;
        }
        if (clazz.isArray()) {
            if (isNDimensional(clazz)) {
                return _openMultiDimensionalArrayHandler;
            }
            return _openArrayHandler;
        }
        
        TypeHandler4 cachedTypeHandler = (TypeHandler4) _mapReflectorToTypeHandler.get(clazz);
        if(cachedTypeHandler != null){
        	return cachedTypeHandler;
        }
        TypeHandler4 configuredTypeHandler = configuredTypeHandler(clazz);
        if(Handlers4.isValueType(configuredTypeHandler)){
        	return configuredTypeHandler;	 
        }
        return null;
    }

    public boolean isSystemHandler(int id) {
    	return id > 0 && id <= _highestBuiltinTypeID;
    }
    
    public int lowestValidId(){
    	return _highestBuiltinTypeID + 1;
    }

	public VirtualFieldMetadata virtualFieldByName(String name) {
        for (int i = 0; i < _virtualFields.length; i++) {
            if (name.equals(_virtualFields[i].getName())) {
                return _virtualFields[i];
            }
        }
        return null;
	}

    public SharedIndexedFields indexes(){
        return _indexes;
    }
    
    public LatinStringIO stringIO(){
        return _stringIO;
    }

    public void stringIO(LatinStringIO io) {
        _stringIO = io;
    }
    
    private GenericReflector reflector() {
        return container().reflector();
    }

    private ObjectContainerBase container() {
        return _container;
    }
    
    private static final Hashtable4 newHashtable(){
        return new Hashtable4(32);
    }

    public TypeHandler4 configuredTypeHandler(ReflectClass claxx) {
    	final Object cachedHandler = _mapReflectorToTypeHandler.get(claxx);
    	if (null != cachedHandler) {
    		return (TypeHandler4) cachedHandler;
    	}
        TypeHandler4 typeHandler = container().configImpl().typeHandlerForClass(claxx, HANDLER_VERSION);
        if(typeHandler instanceof BuiltinTypeHandler) {
        	((BuiltinTypeHandler) typeHandler).registerReflector(reflector());
        }
        if(Handlers4.isValueType(typeHandler)){
        	mapClassToTypeHandler(claxx, typeHandler);
        }
        return typeHandler;
    }

	public static TypeHandler4 correctHandlerVersion(HandlerVersionContext context, TypeHandler4 handler){
	    int version = context.handlerVersion();
	    if(version >= HANDLER_VERSION){
	        return handler;
	    }
	    return context.transaction().container().handlers().correctHandlerVersion(handler, version);
	}

	public boolean isTransient(ReflectClass claxx) {
		return ICLASS_TRANSIENTCLASS.isAssignableFrom(claxx)
    		|| Platform4.isTransient(claxx);
    }

	public void treatAsOpenType(Class<?> clazz) {
		mapClassToTypeHandler(reflectClassFor(clazz), openTypeHandler());
	}

	private ReflectClass reflectClassFor(Class<?> clazz) {
		return container().reflector().forClass(clazz);
	}
	
	public DiagnosticProcessor diagnosticProcessor() {
		return _diagnosticProcessor;
	}

	private static class TypeInfo {
	    
	 // TODO: remove when no longer needed in HandlerRegistry
	    public ClassMetadata classMetadata;  
	    
	    public ReflectClass classReflector;

	    public TypeInfo(
	        ClassMetadata classMetadata_, 
	        ReflectClass classReflector_) {
	        classMetadata = classMetadata_;
	        classReflector = classReflector_;
	    }

	}

}