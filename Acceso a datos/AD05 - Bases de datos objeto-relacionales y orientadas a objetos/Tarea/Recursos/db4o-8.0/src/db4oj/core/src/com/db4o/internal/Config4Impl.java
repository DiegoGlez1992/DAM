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

import java.io.*;
import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.config.encoding.*;
import com.db4o.diagnostic.*;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.config.*;
import com.db4o.internal.diagnostic.*;
import com.db4o.internal.encoding.*;
import com.db4o.internal.events.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.ids.*;
import com.db4o.internal.references.*;
import com.db4o.io.*;
import com.db4o.messaging.*;
import com.db4o.reflect.*;
import com.db4o.reflect.core.*;
import com.db4o.reflect.generic.*;
import com.db4o.typehandlers.*;


/**
 * Configuration template for creating new db4o files
 * 
 * @exclude
 * 
 * @sharpen.partial
 */
public final class Config4Impl implements Configuration, DeepClone,
		MessageSender, FreespaceConfiguration, QueryConfiguration,
		ClientServerConfiguration {
    
	private KeySpecHashtable4 _config=new KeySpecHashtable4(50);
	
	private final static KeySpec ACTIVATION_DEPTH_KEY=new KeySpec(5);
	
	private final static KeySpec ACTIVATION_DEPTH_PROVIDER_KEY=new KeySpec(LegacyActivationDepthProvider.INSTANCE);
	private final static KeySpec UPDATE_DEPTH_PROVIDER_KEY=new KeySpec(new LegacyUpdateDepthProvider());
    
	private final static KeySpec ALLOW_VERSION_UPDATES_KEY=new KeySpec(false);
	
	private final static KeySpec ASYNCHRONOUS_SYNC_KEY = new KeySpec(false);

    private final static KeySpec AUTOMATIC_SHUTDOWN_KEY=new KeySpec(true);

    //  TODO: consider setting default to 8, it's more efficient with freespace.
    private final static KeySpec BLOCKSIZE_KEY=new KeySpec((byte)1);
    
	private final static KeySpec BLOB_PATH_KEY=new KeySpec(null);
    
    private final static KeySpec BTREE_NODE_SIZE_KEY=new KeySpec(201);
    
	private final static KeySpec CALLBACKS_KEY=new KeySpec(CallBackMode.ALL);
    
	private final static KeySpec CALL_CONSTRUCTORS_KEY=new KeySpec(TernaryBool.UNSPECIFIED);
	
	private final static KeySpec CONFIGURATION_ITEMS_KEY=new KeySpec(null);

	private final static KeySpec CONFIGURED_REFLECTOR_KEY=new KeySpec(null);
    
	private final static KeySpec CLASS_ACTIVATION_DEPTH_CONFIGURABLE_KEY=new KeySpec(true);
    
	private final static KeySpec CLASSLOADER_KEY=new KeySpec(null);
	
	private final static KeySpec CLIENT_SERVER_FACTORY_KEY=new KeySpec(new KeySpec.Deferred() {
		public Object evaluate() {
			return defaultClientServerFactory();
		}
	});
	
	private final static KeySpec DATABASE_GROWTH_SIZE_KEY=new KeySpec(0);
    
	private final static KeySpec DETECT_SCHEMA_CHANGES_KEY=new KeySpec(true);
    
    private final static KeySpec DIAGNOSTIC_KEY=new KeySpec(new KeySpec.Deferred() { 
    	public Object evaluate() { 
    		return new DiagnosticProcessor(); 
    	}
    });
    
    private final static KeySpec DISABLE_COMMIT_RECOVERY_KEY=new KeySpec(false);
    
	private final static KeySpec DISCARD_FREESPACE_KEY=new KeySpec(0);
	
	private final static StringEncoding DEFAULT_STRING_ENCODING = StringEncodings.unicode(); 
    
	private final static KeySpec STRING_ENCODING_KEY=new KeySpec(DEFAULT_STRING_ENCODING);
	
	private final static KeySpec ENCODING_KEY=new KeySpec(BuiltInStringEncoding.encodingByteForEncoding(DEFAULT_STRING_ENCODING));
	
	private final static KeySpec ENCRYPT_KEY=new KeySpec(false);
	
	private final static KeySpec ENVIRONMENT_CONTRIBUTIONS_KEY=new KeySpec(new KeySpec.Deferred() {
		public Object evaluate() {
			return new ArrayList();
		}
	});	
    
	private final static KeySpec EXCEPTIONAL_CLASSES_KEY=new KeySpec(null);
    
	private final static KeySpec EXCEPTIONS_ON_NOT_STORABLE_KEY=new KeySpec(true);
	
	private final static KeySpec FILE_BASED_TRANSACTION_LOG_KEY = new KeySpec(false);
    
	private final static KeySpec FREESPACE_FILLER_KEY=new KeySpec(null);

	private final static KeySpec FREESPACE_SYSTEM_KEY=new KeySpec(AbstractFreespaceManager.FM_DEFAULT);
    
	private final static KeySpec GENERATE_UUIDS_KEY=new KeySpec(ConfigScope.INDIVIDUALLY);
    
	private final static KeySpec GENERATE_COMMIT_TIMESTAMPS_KEY=new KeySpec(TernaryBool.UNSPECIFIED);
	
	private final static KeySpec ID_SYSTEM_KEY=new KeySpec(StandardIdSystemFactory.DEFAULT);
	
	private final static KeySpec ID_SYSTEM_CUSTOM_FACTORY_KEY=new KeySpec(null);
	
	private final static KeySpec QUERY_EVALUATION_MODE_KEY=new KeySpec(QueryEvaluationMode.IMMEDIATE);
	
	private final static KeySpec LOCK_FILE_KEY=new KeySpec(true);
    
	private final static KeySpec MESSAGE_RECIPIENT_KEY=new KeySpec(null);
    
	private final static KeySpec OPTIMIZE_NQ_KEY=new KeySpec(true);
    
	private final static KeySpec OUTSTREAM_KEY=new KeySpec(null);
    
	private final static KeySpec PASSWORD_KEY=new KeySpec((String)null);
	
	// for playing with different strategies of prefetching
	// object
	private static final KeySpec CLIENT_QUERY_RESULT_ITERATOR_FACTORY_KEY=new KeySpec(null);
    
	private static final KeySpec PREFETCH_ID_COUNT_KEY = new KeySpec(10);

	private static final KeySpec PREFETCH_OBJECT_COUNT_KEY = new KeySpec(10);
	
	private static final KeySpec PREFETCH_DEPTH_KEY = new KeySpec(0);
	
	public static final int PREFETCH_SLOT_CACHE_SIZE_FACTOR = 10;
	
	private static final int MAXIMUM_PREFETCH_SLOT_CACHE_SIZE = 10000;
	
	private static final KeySpec PREFETCH_SLOT_CACHE_SIZE_KEY = new KeySpec(0);
	
	private final static KeySpec READ_AS_KEY = new KeySpec(new KeySpec.Deferred() {
		public Object evaluate() {
			return new Hashtable4(16);
		}
	});	

	private final static KeySpec RECOVERY_MODE_KEY=new KeySpec(false);
    
	private final static KeySpec REFLECTOR_KEY=new KeySpec(null);
    
	private final static KeySpec RENAME_KEY=new KeySpec(null);
    
	private final static KeySpec RESERVED_STORAGE_SPACE_KEY=new KeySpec(0);
    
	private final static KeySpec SINGLE_THREADED_CLIENT_KEY=new KeySpec(false);
    
	private final static KeySpec TEST_CONSTRUCTORS_KEY=new KeySpec(true);
    
	private final static KeySpec TIMEOUT_CLIENT_SOCKET_KEY=new KeySpec(Const4.CLIENT_SOCKET_TIMEOUT);
    
	private final static KeySpec TIMEOUT_SERVER_SOCKET_KEY=new KeySpec(Const4.SERVER_SOCKET_TIMEOUT);
    
	private final static KeySpec UPDATE_DEPTH_KEY=new KeySpec(1);
    
	private final static KeySpec WEAK_REFERENCE_COLLECTION_INTERVAL_KEY=new KeySpec(1000);
    
	private final static KeySpec WEAK_REFERENCES_KEY=new KeySpec(true);
	
	private final static KeySpec STORAGE_FACTORY_KEY=new KeySpec(new CachingStorage(new FileStorage()));
        
	private final static KeySpec ALIASES_KEY=new KeySpec(null);
	
	private final static KeySpec BATCH_MESSAGES_KEY=new KeySpec(true);
	
	private static final KeySpec MAX_BATCH_QUEUE_SIZE_KEY = new KeySpec(Integer.MAX_VALUE);
	
	private final static KeySpec TAINTED_KEY = new KeySpec(false);
	
	private final static KeySpec REFERENCE_SYSTEM_FACTORY_KEY = new KeySpec(new ReferenceSystemFactory() {
		public ReferenceSystem newReferenceSystem(InternalObjectContainer container) {
			return new TransactionalReferenceSystem();
		}
	});
	
	private static final KeySpec NAME_PROVIDER_KEY = new KeySpec(new NameProvider() {
		public String name(ObjectContainer db) {
			return null;
		}
	});
	
	// TODO find a better place to do this, and use AndroidConfiguration instead.
	private final static KeySpec MAX_STACK_DEPTH_KEY =
		new KeySpec( "Dalvik".equals(System.getProperty("java.vm.name")) ? 2 : Const4.DEFAULT_MAX_STACK_DEPTH);


	//  is null in the global configuration until deepClone is called
	private ObjectContainerBase        _container;
	
	// The following are very frequently being asked for, so they show up in the profiler. 
	// Let's keep them out of the Hashtable.
	private boolean _internStrings;
	private int _messageLevel;
	private boolean	_readOnly;
	
	private Collection4 _registeredTypeHandlers;

	private final Event4Impl<EventArgs> _prefetchSettingsChanged = Event4Impl.newInstance();

	private boolean _prefetchSlotCacheSizeModifiedExternally;

    public int activationDepth() {
    	return _config.getAsInt(ACTIVATION_DEPTH_KEY);
    }

    /**
     * @sharpen.ignore
     */
    private static LegacyClientServerFactory defaultClientServerFactory() {
    	try {
    		// FIXME: circular cs dependancy. Improve.
			 return (LegacyClientServerFactory) Class.forName("com.db4o.cs.internal.config.LegacyClientServerFactoryImpl").newInstance();
		} catch (Exception e) {
			throw new Db4oException("ClientServer jar db4o-[version]-cs-java.jar not in CLASSPATH", e);
		}
	}

	public void activationDepth(int depth) {
    	_config.put(ACTIVATION_DEPTH_KEY,depth);
    }
    
	public void add(ConfigurationItem item) {
		item.prepare(this);
		safeConfigurationItems().put(item, item);
	}
	
	/**
	 * Returns an iterator for all {@link ConfigurationItem} instances
	 * added.
	 * 
	 * @see Config4Impl#add
	 * @return the iterator
	 */
	public Iterator4 configurationItemsIterator() {
		Hashtable4 items = configurationItems();
		if (items == null) {
			return Iterators.EMPTY_ITERATOR;
		}
		return items.keys();
	}

	private Hashtable4 safeConfigurationItems() {
		Hashtable4 items = configurationItems();
		if(items==null) {
			items=new Hashtable4(16);
			_config.put(CONFIGURATION_ITEMS_KEY,items);
		}
		return items;
	}
	
    public void allowVersionUpdates(boolean flag){
    	_config.put(ALLOW_VERSION_UPDATES_KEY,flag);
    }
    
    private Hashtable4 configurationItems(){
    	return (Hashtable4)_config.get(CONFIGURATION_ITEMS_KEY);
    }
    
	public void applyConfigurationItems(final InternalObjectContainer container) {
		Hashtable4 items = configurationItems();
		if(items == null){
			return;
		}
		Iterator4 i = items.iterator();
		while(i.moveNext()){
			Entry4 entry = (Entry4) i.current();
			ConfigurationItem item = (ConfigurationItem) entry.value();
			item.apply(container);
		}
	}

    public void automaticShutDown(boolean flag) {
    	_config.put(AUTOMATIC_SHUTDOWN_KEY,flag);
    }
    
    public void blockSize(int bytes){
       if (bytes < 1 || bytes > 127) {
           throw new IllegalArgumentException();
       } 
       globalSettingOnly();       
       _config.put(BLOCKSIZE_KEY,(byte)bytes);
    }
    
    public void bTreeNodeSize(int size){
        _config.put(BTREE_NODE_SIZE_KEY,size);
    }
    
    public void bTreeCacheHeight(int height){
    }

    public void callbacks(boolean turnOn) {
        callbackMode(turnOn ? CallBackMode.ALL : CallBackMode.NONE);
    }

    public void callbackMode(CallBackMode mode) {
        _config.put(CALLBACKS_KEY, mode);
    }

    public void callConstructors(boolean flag){
        _config.put(CALL_CONSTRUCTORS_KEY,TernaryBool.forBoolean(flag));
    }

    public void classActivationDepthConfigurable(boolean turnOn) {
        _config.put(CLASS_ACTIVATION_DEPTH_CONFIGURABLE_KEY,turnOn);
    }

    public Config4Class configClass(String className) {
		Config4Class config = (Config4Class)exceptionalClasses().get(className);

        if (Debug4.configureAllClasses) {
            if (config == null) {
                if (!isIgnoredClass(className)) {
                    config = (Config4Class) objectClass(className);
                }

            }
        }
        return config;
    }

	private boolean isIgnoredClass(String className) {
		Class[] ignore = ignoredClasses();
		for (int i = 0; i < ignore.length; i++) {
		    if (ignore[i].getName().equals(className)) {
		        return true;
		    }
		}
		return false;
	}

	private Class[] ignoredClasses() {
		return new Class[] {
				StaticClass.class, 
				StaticField.class
		};
	}

    public Object deepClone(Object param) {
        Config4Impl ret = new Config4Impl();
        ConfigDeepCloneContext context = new ConfigDeepCloneContext(this, ret);
        ret._config=(KeySpecHashtable4)_config.deepClone(context);
        ret._internStrings = _internStrings;
        ret._messageLevel = _messageLevel;
        ret._readOnly = _readOnly;
        if(_registeredTypeHandlers != null){
            ret._registeredTypeHandlers = (Collection4) _registeredTypeHandlers.deepClone(context);
        }
        return ret;
    }
    
    public void container(ObjectContainerBase container) {
    	_container=container;
    }
    
	public void databaseGrowthSize(int bytes) {
		_config.put(DATABASE_GROWTH_SIZE_KEY,bytes);
	}
	
	public int databaseGrowthSize() {
		return _config.getAsInt(DATABASE_GROWTH_SIZE_KEY);
	}

    public void detectSchemaChanges(boolean flag) {
        _config.put(DETECT_SCHEMA_CHANGES_KEY,flag);
    }

    public void disableCommitRecovery() {
        _config.put(DISABLE_COMMIT_RECOVERY_KEY,true);
    }

    public void discardSmallerThan(int byteCount) {
        if(byteCount < 0){
			throw new IllegalArgumentException();
		}
		_config.put(DISCARD_FREESPACE_KEY,byteCount);
    }

    /**
     * @deprecated
     */
    public void encrypt(boolean flag) {
        globalSettingOnly();
        _config.put(ENCRYPT_KEY,flag);
    }
    
    void oldEncryptionOff() {
        _config.put(ENCRYPT_KEY,false);
    }

    void ensureDirExists(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (file.exists() && file.isDirectory()) {
        } else {
            throw new IOException(Messages.get(37, path));
        }
    }

    PrintStream errStream() {
    	PrintStream outStream=outStreamOrNull();
        return outStream == null ? System.err : outStream;
    }

    
    public void exceptionsOnNotStorable(boolean flag) {
        _config.put(EXCEPTIONS_ON_NOT_STORABLE_KEY,flag);
    }
    
    public FreespaceConfiguration freespace() {
        return this;
    }
    
    public void freespaceFiller(FreespaceFiller freespaceFiller) {
    	_config.put(FREESPACE_FILLER_KEY, freespaceFiller);
    }

    public FreespaceFiller freespaceFiller() {
    	return (FreespaceFiller) _config.get(FREESPACE_FILLER_KEY);
    }
    
	public void generateUUIDs(ConfigScope scope) {
        _config.put(GENERATE_UUIDS_KEY,scope);
    }

	@Deprecated
    public void generateVersionNumbers(ConfigScope scope) {
        if (scope == ConfigScope.INDIVIDUALLY) {
        	throw new UnsupportedOperationException();
        }
        generateCommitTimestamps(scope == ConfigScope.GLOBALLY);
    }

    public void generateCommitTimestamps(boolean flag) {
        _config.put(GENERATE_COMMIT_TIMESTAMPS_KEY,TernaryBool.forBoolean(flag));
    }

    public MessageSender getMessageSender() {
        return this;
    }

    private void globalSettingOnly() {
        if (_container != null) {
           throw new GlobalOnlyConfigException();
        }
    }
    
    public void internStrings(boolean doIntern) {
    	_internStrings = doIntern;
    }
    
    @SuppressWarnings("deprecation")
    public void io(IoAdapter adapter){
    	globalSettingOnly();
    	storage(new IoAdapterStorage(adapter));
    }

    public void lockDatabaseFile(boolean flag) {
    	_config.put(LOCK_FILE_KEY,flag);
    }
    
    public void markTransient(String marker) {
        Platform4.markTransient(marker);
    }

    public void messageLevel(int level) {
    	_messageLevel = level;
        if (outStream() == null) {
            setOut(System.out);
        }
    }

    public void optimizeNativeQueries(boolean optimizeNQ) {
    	_config.put(OPTIMIZE_NQ_KEY,optimizeNQ);
    }
    
    public boolean optimizeNativeQueries() {
    	return _config.getAsBoolean(OPTIMIZE_NQ_KEY);
    }
    
    public ObjectClass objectClass(Object clazz) {
        
        String className = null;
        
        if(clazz instanceof String){
            className = (String)clazz;
        }else{
            ReflectClass claxx = reflectorFor(clazz);
            if(claxx == null){
                return null;
            }
            className = claxx.getName();
        }
        if(ReflectPlatform.fullyQualifiedName(Object.class).equals(className)){
        	throw new IllegalArgumentException("Configuration of the Object class is not supported.");
        }
        
        Hashtable4 xClasses=exceptionalClasses();
        Config4Class c4c = (Config4Class) xClasses.get(className);
        if (c4c == null) {
            c4c = new Config4Class(this, className);
            xClasses.put(className, c4c);
        }
        return c4c;
    }

    private PrintStream outStreamOrNull() {
    	return (PrintStream)_config.get(OUTSTREAM_KEY);
    }
    
    public PrintStream outStream() {
    	PrintStream outStream=outStreamOrNull();
        return outStream == null ? System.out : outStream;
    }

    /**
     * @deprecated
     */
    public void password(String pw) {
        globalSettingOnly();
        _config.put(PASSWORD_KEY,pw);
    }

    public void readOnly(boolean flag) {
        _readOnly = flag;
    }

	public GenericReflector reflector() {
		GenericReflector reflector=(GenericReflector)_config.get(REFLECTOR_KEY);
		if(reflector == null){
			Reflector configuredReflector=(Reflector)_config.get(CONFIGURED_REFLECTOR_KEY);
			if(configuredReflector == null){
				configuredReflector=Platform4.createReflector(classLoader());
				_config.put(CONFIGURED_REFLECTOR_KEY,configuredReflector);	
			}
			reflector=new GenericReflector(configuredReflector);
            
            _config.put(REFLECTOR_KEY,reflector);
		}
// TODO: transaction assignment has been moved to YapStreamBase#initialize1().
// implement better, more generic solution as described in COR-288
//		if(! reflector.hasTransaction() && i_stream != null){
//			reflector.setTransaction(i_stream.getSystemTransaction());
//		}
		return reflector;
	}

	public void reflectWith(Reflector reflect) {
		
        if(_container != null){
        	Exceptions4.throwRuntimeException(46);   // see readable message for code in Messages.java
        }
		
        if (reflect == null) {
            throw new NullPointerException();
        }
        _config.put(CONFIGURED_REFLECTOR_KEY,reflect);
		_config.put(REFLECTOR_KEY,null);
    }

    public void refreshClasses() {
        throw new NotImplementedException();
    }

    void rename(Rename a_rename) {
    	Collection4 renameCollection=rename();
        if (renameCollection == null) {
            renameCollection = new Collection4();
            _config.put(RENAME_KEY,renameCollection);
        }
        renameCollection.add(a_rename);
    }

    public void reserveStorageSpace(long byteCount) throws DatabaseReadOnlyException {
        int reservedStorageSpace = (int) byteCount;
        if (reservedStorageSpace < 0) {
            reservedStorageSpace = 0;
        }
        _config.put(RESERVED_STORAGE_SPACE_KEY,reservedStorageSpace);
        if (_container != null) {
            _container.reserve(reservedStorageSpace);
        }
    }

    /**
     * The ConfigImpl also is our messageSender
     */
    public void send(Object obj) {
        if (_container != null) {
            _container.send(obj);
        }
    }

    public void setBlobPath(String path) throws IOException {
        ensureDirExists(path);
        _config.put(BLOB_PATH_KEY,path);
    }

    public void setMessageRecipient(MessageRecipient messageRecipient) {
    	_config.put(MESSAGE_RECIPIENT_KEY,messageRecipient);
    }

    /**
     * @deprecated
     */
    public void setOut(PrintStream outStream) {
        _config.put(OUTSTREAM_KEY,outStream);
        if (_container != null) {
            _container.logMsg(19, Db4o.version());
        } else {
            Messages.logMsg(this, 19, Db4o.version());
        }
    }

    public void singleThreadedClient(boolean flag) {
    	_config.put(SINGLE_THREADED_CLIENT_KEY,flag);
    }
    
	public StringEncoding stringEncoding() {
		return (StringEncoding) _config.get(STRING_ENCODING_KEY);
	}
	
	public void stringEncoding(StringEncoding encoding) {
		_config.put(STRING_ENCODING_KEY, encoding);
		_config.put(ENCODING_KEY, BuiltInStringEncoding.encodingByteForEncoding(encoding));
	}

    public void testConstructors(boolean flag) {
    	_config.put(TEST_CONSTRUCTORS_KEY,flag);
    }

    public void timeoutClientSocket(int milliseconds) {
    	_config.put(TIMEOUT_CLIENT_SOCKET_KEY,milliseconds);
    }

    public void timeoutServerSocket(int milliseconds) {
    	_config.put(TIMEOUT_SERVER_SOCKET_KEY,milliseconds);
    }

    public void updateDepth(int depth) {
    	if(depth < 0) {
    		throw new IllegalArgumentException("update depth must not be negative");
    	}
        DiagnosticProcessor dp = diagnosticProcessor();
        if (dp.enabled()) {
            dp.checkUpdateDepth(depth);
        }
    	_config.put(UPDATE_DEPTH_KEY, depth);
    }
    
    public void useBTreeSystem() {
        _config.put(FREESPACE_SYSTEM_KEY,AbstractFreespaceManager.FM_BTREE);
    }

    public void useRamSystem() {
        _config.put(FREESPACE_SYSTEM_KEY,AbstractFreespaceManager.FM_RAM);
    }

	/**
	 * @deprecated
	 */
    public void useIndexSystem() {
		throw new NotSupportedException();
	}
    
    public void weakReferenceCollectionInterval(int milliseconds) {
    	_config.put(WEAK_REFERENCE_COLLECTION_INTERVAL_KEY,milliseconds);
    }

    public void weakReferences(boolean flag) {
    	_config.put(WEAK_REFERENCES_KEY,flag);
    }
    
    private Collection4 aliases() {
    	Collection4 aliasesCollection=(Collection4)_config.get(ALIASES_KEY);
    	if (null == aliasesCollection) {
    		aliasesCollection = new Collection4();
    		_config.put(ALIASES_KEY,aliasesCollection);
    	}
    	return aliasesCollection;
    }
    
    public void addAlias(Alias alias) {
    	if (null == alias) throw new com.db4o.foundation.ArgumentNullException("alias");
    	aliases().add(alias);
    }
    
    public void removeAlias(Alias alias) {
    	if (null == alias) throw new com.db4o.foundation.ArgumentNullException("alias");
    	aliases().remove(alias);
    }
    
    public String resolveAliasRuntimeName(String runtimeType) {

    	Collection4 configuredAliases=aliases();
    	if (null == configuredAliases) {
    		return runtimeType;
    	}
    	
    	Iterator4 i = configuredAliases.iterator();
    	while (i.moveNext()) {
    		String resolved = ((Alias)i.current()).resolveRuntimeName(runtimeType);
    		if (null != resolved){
    			return resolved; 
    		}
    	}
    	
    	return runtimeType;
    }
    
    public String resolveAliasStoredName(String storedType) {

    	Collection4 configuredAliases=aliases();
    	if (null == configuredAliases){
    		return storedType;
    	}
    	
    	Iterator4 i = configuredAliases.iterator();
    	while (i.moveNext()) {
    		String resolved = ((Alias)i.current()).resolveStoredName(storedType);
    		if (null != resolved){
    			return resolved; 
    		}
    	}
    	
    	return storedType;
    }
    
    ReflectClass reflectorFor(Object clazz) {
    	return ReflectorUtils.reflectClassFor(reflector(), clazz);
    }

	public boolean allowVersionUpdates() {
		return _config.getAsBoolean(ALLOW_VERSION_UPDATES_KEY);
	}

	public boolean automaticShutDown() {
		return _config.getAsBoolean(AUTOMATIC_SHUTDOWN_KEY);
	}

	public byte blockSize() {
		return _config.getAsByte(BLOCKSIZE_KEY);
	}
    
    public int bTreeNodeSize() {
        return _config.getAsInt(BTREE_NODE_SIZE_KEY);
    }
    
	public String blobPath() {
		return _config.getAsString(BLOB_PATH_KEY);
	}

	public CallBackMode callbackMode() {
		return (CallBackMode)_config.get(CALLBACKS_KEY);
	}

	public TernaryBool callConstructors() {
		return _config.getAsTernaryBool(CALL_CONSTRUCTORS_KEY);
	}

	boolean classActivationDepthConfigurable() {
		return _config.getAsBoolean(CLASS_ACTIVATION_DEPTH_CONFIGURABLE_KEY);
	}

	Object classLoader() {
		return _config.get(CLASSLOADER_KEY);
	}

	public boolean detectSchemaChanges() {
		return _config.getAsBoolean(DETECT_SCHEMA_CHANGES_KEY);
	}

	public boolean commitRecoveryDisabled() {
		return _config.getAsBoolean(DISABLE_COMMIT_RECOVERY_KEY);
	}

    public DiagnosticConfiguration diagnostic() {
        return (DiagnosticConfiguration)_config.get(DIAGNOSTIC_KEY);
    }
    
    public DiagnosticProcessor diagnosticProcessor(){
        return (DiagnosticProcessor)_config.get(DIAGNOSTIC_KEY); 
    }

	public int discardFreeSpace() {
		return _config.getAsInt(DISCARD_FREESPACE_KEY);
	}

	byte encoding() {
		return _config.getAsByte(ENCODING_KEY);
	}

	boolean encrypt() {
		return _config.getAsBoolean(ENCRYPT_KEY);
	}

	public Hashtable4 exceptionalClasses() {
		Hashtable4 exceptionalClasses = (Hashtable4)_config.get(EXCEPTIONAL_CLASSES_KEY);
		if(exceptionalClasses==null) {
			exceptionalClasses=new Hashtable4(16);
			_config.put(EXCEPTIONAL_CLASSES_KEY,exceptionalClasses);
		}
		return exceptionalClasses;
	}

	public boolean exceptionsOnNotStorable() {
		return _config.getAsBoolean(EXCEPTIONS_ON_NOT_STORABLE_KEY);
	}

	byte freespaceSystem() {
		return _config.getAsByte(FREESPACE_SYSTEM_KEY);
	}

	public ConfigScope generateUUIDs() {
		return (ConfigScope) _config.get(GENERATE_UUIDS_KEY);
	}

	public TernaryBool generateCommitTimestamps() {
		return (TernaryBool) _config.get(GENERATE_COMMIT_TIMESTAMPS_KEY);
	}

	public boolean internStrings() {
		return _internStrings;
	}
	
	public boolean lockFile() {
		return _config.getAsBoolean(LOCK_FILE_KEY);
	}

	public int messageLevel() {
		return _messageLevel;
	}

	public MessageRecipient messageRecipient() {
		return (MessageRecipient)_config.get(MESSAGE_RECIPIENT_KEY);
	}

	boolean optimizeNQ() {
		return _config.getAsBoolean(OPTIMIZE_NQ_KEY);
	}

	String password() {
		return _config.getAsString(PASSWORD_KEY);
	}

	public void prefetchIDCount(int prefetchIDCount) {
		_config.put(PREFETCH_ID_COUNT_KEY,prefetchIDCount);
	}

	public int prefetchIDCount() {
		return _config.getAsInt(PREFETCH_ID_COUNT_KEY);
	}

	public void prefetchObjectCount(int prefetchObjectCount) {
		_config.put(PREFETCH_OBJECT_COUNT_KEY,prefetchObjectCount);
		ensurePrefetchSlotCacheSize();
	}

	public int prefetchObjectCount() {
		return _config.getAsInt(PREFETCH_OBJECT_COUNT_KEY);
	}

	public Hashtable4 readAs() {
		return (Hashtable4)_config.get(READ_AS_KEY);
	}

	public boolean isReadOnly() {
		return _readOnly;
	}
	
	public void recoveryMode(boolean flag) {
		_config.put(RECOVERY_MODE_KEY, flag);
	}
	
	public boolean recoveryMode(){
		return _config.getAsBoolean(RECOVERY_MODE_KEY);
	}

	Collection4 rename() {
		return (Collection4)_config.get(RENAME_KEY);
	}

	public int reservedStorageSpace() {
		return _config.getAsInt(RESERVED_STORAGE_SPACE_KEY);
	}

	public boolean singleThreadedClient() {
		return _config.getAsBoolean(SINGLE_THREADED_CLIENT_KEY);
	}

	public boolean testConstructors() {
		return _config.getAsBoolean(TEST_CONSTRUCTORS_KEY);
	}

	public int timeoutClientSocket() {
		return _config.getAsInt(TIMEOUT_CLIENT_SOCKET_KEY);
	}

	public int timeoutServerSocket() {
		return _config.getAsInt(TIMEOUT_SERVER_SOCKET_KEY);
	}

	public int updateDepth() {
		return _config.getAsInt(UPDATE_DEPTH_KEY);
	}

	public int weakReferenceCollectionInterval() {
		return _config.getAsInt(WEAK_REFERENCE_COLLECTION_INTERVAL_KEY);
	}

	public boolean weakReferences() {
		return _config.getAsBoolean(WEAK_REFERENCES_KEY);
	}

	@SuppressWarnings("deprecation")
	public IoAdapter io() {
		throw new NotImplementedException();
	}
	
	public Storage storage() {
		return (Storage)_config.get(STORAGE_FACTORY_KEY);
	}
	
	public void storage(final Storage factory) {
	    _config.put(STORAGE_FACTORY_KEY, factory);
    }
	
	public QueryConfiguration queries() {
		return this;
	}

	public void evaluationMode(QueryEvaluationMode mode) {
		_config.put(QUERY_EVALUATION_MODE_KEY, mode);
	}
	
	public QueryEvaluationMode evaluationMode() {
		return (QueryEvaluationMode)_config.get(QUERY_EVALUATION_MODE_KEY);
	}
	

	public void queryResultIteratorFactory(QueryResultIteratorFactory factory) {
		_config.put(CLIENT_QUERY_RESULT_ITERATOR_FACTORY_KEY, factory);
	}
	
	public QueryResultIteratorFactory queryResultIteratorFactory() {
		return (QueryResultIteratorFactory)_config.get(CLIENT_QUERY_RESULT_ITERATOR_FACTORY_KEY);
	}


	public ClientServerConfiguration clientServer() {
		return this;
	}

	public void batchMessages(boolean flag) {
		_config.put(BATCH_MESSAGES_KEY, flag);
	}
	
	public boolean batchMessages() {
		return _config.getAsBoolean(BATCH_MESSAGES_KEY);
	}
	
	public void maxBatchQueueSize(int maxSize) {
		_config.put(MAX_BATCH_QUEUE_SIZE_KEY, maxSize);
	}

	public int maxBatchQueueSize() {
		return _config.getAsInt(MAX_BATCH_QUEUE_SIZE_KEY);
	}

	public void activationDepthProvider(ActivationDepthProvider provider) {
		_config.put(ACTIVATION_DEPTH_PROVIDER_KEY, provider);
	}

	public void updateDepthProvider(UpdateDepthProvider provider) {
		_config.put(UPDATE_DEPTH_PROVIDER_KEY, provider);
	}

	public ActivationDepthProvider activationDepthProvider() {
		return (ActivationDepthProvider) _config.get(ACTIVATION_DEPTH_PROVIDER_KEY);
	}

	public UpdateDepthProvider updateDepthProvider() {
		return (UpdateDepthProvider) _config.get(UPDATE_DEPTH_PROVIDER_KEY);
	}

	public void registerTypeHandler(TypeHandlerPredicate predicate, TypeHandler4 typeHandler){
	    if(_registeredTypeHandlers == null){
	        _registeredTypeHandlers = new Collection4();
	    }
	    _registeredTypeHandlers.add(new TypeHandlerPredicatePair(predicate, typeHandler));
	}
	
	public TypeHandler4 typeHandlerForClass(ReflectClass classReflector, byte handlerVersion){
	    if(_registeredTypeHandlers == null){
	        return null;
	    }
	    Iterator4 i = _registeredTypeHandlers.iterator();
	    while(i.moveNext()){
	        TypeHandlerPredicatePair pair = (TypeHandlerPredicatePair) i.current();
	        if(pair._predicate.match(classReflector)){
	            return pair._typeHandler;
	        }
	    }
	    return null;
	}
	
	public static class ConfigDeepCloneContext {
		public final Config4Impl _orig;
		public final Config4Impl _cloned;
		
		public ConfigDeepCloneContext(Config4Impl orig, Config4Impl cloned) {
			_orig = orig;
			_cloned = cloned;
		}
	}

	public void factory(LegacyClientServerFactory factory) {
		_config.put(CLIENT_SERVER_FACTORY_KEY, factory);
	}
	
	public LegacyClientServerFactory clientServerFactory(){
		return (LegacyClientServerFactory) _config.get(CLIENT_SERVER_FACTORY_KEY);
	}
	
	public CacheConfiguration cache() {
		return new CacheConfigurationImpl(this);
	}

	public boolean fileBasedTransactionLog() {
		return _config.getAsBoolean(FILE_BASED_TRANSACTION_LOG_KEY);
	}
	
	public void fileBasedTransactionLog(boolean flag) {
		_config.put(FILE_BASED_TRANSACTION_LOG_KEY, flag);
	}

	private boolean isTainted() {
		return _config.getAsBoolean(TAINTED_KEY);
    }

	public void taint() {
		_config.put(TAINTED_KEY, true);
    }

	public static void assertIsNotTainted(Configuration config) {
        if (((Config4Impl)config).isTainted()) {
        	throw new IllegalArgumentException("Configuration already used.");
        }
    }

	public void prefetchDepth(int prefetchDepth) {
		_config.put(PREFETCH_DEPTH_KEY, prefetchDepth);
		ensurePrefetchSlotCacheSize();
    }

	private void ensurePrefetchSlotCacheSize() {
		if(! _prefetchSlotCacheSizeModifiedExternally){
			prefetchSlotCacheSize(calculatedPrefetchSlotcacheSize());
			_prefetchSlotCacheSizeModifiedExternally = false;
		}
	}
	
	public int prefetchDepth() {
		return _config.getAsInt(PREFETCH_DEPTH_KEY);
	}

	public List environmentContributions() {
		return (List) _config.get(ENVIRONMENT_CONTRIBUTIONS_KEY);
	}

	public void prefetchSlotCacheSize(int slotCacheSize) {
		_prefetchSlotCacheSizeModifiedExternally = true;
		_config.put(PREFETCH_SLOT_CACHE_SIZE_KEY, slotCacheSize );
		_prefetchSettingsChanged.trigger(EventArgs.EMPTY);
	}
	
	public int prefetchSlotCacheSize() {
		return _config.getAsInt(PREFETCH_SLOT_CACHE_SIZE_KEY);
	}

	private int calculatedPrefetchSlotcacheSize() {
		long calculated = (long)prefetchDepth() * prefetchObjectCount() * PREFETCH_SLOT_CACHE_SIZE_FACTOR;
		if(calculated > MAXIMUM_PREFETCH_SLOT_CACHE_SIZE){
			calculated = MAXIMUM_PREFETCH_SLOT_CACHE_SIZE;
		}
		return (int) calculated; 
	}

	/**
	 * @sharpen.event EventArgs
	 */
	public Event4<EventArgs> prefetchSettingsChanged(){
		return _prefetchSettingsChanged;
	}

	public void referenceSystemFactory(
			ReferenceSystemFactory referenceSystemFactory) {
		_config.put(REFERENCE_SYSTEM_FACTORY_KEY, referenceSystemFactory);
	}
	
	public ReferenceSystemFactory referenceSystemFactory() {
		return (ReferenceSystemFactory) _config.get(REFERENCE_SYSTEM_FACTORY_KEY);
	}

	public void nameProvider(NameProvider provider) {
		_config.put(NAME_PROVIDER_KEY, provider);
	}

	public NameProvider nameProvider() {
		return (NameProvider) _config.get(NAME_PROVIDER_KEY);
	}

	public void usePointerBasedIdSystem() {
		_config.put(ID_SYSTEM_KEY,StandardIdSystemFactory.POINTER_BASED);		
	}

	public void useStackedBTreeIdSystem() {
		_config.put(ID_SYSTEM_KEY,StandardIdSystemFactory.STACKED_BTREE);
	}
	
	public void useSingleBTreeIdSystem() {
		_config.put(ID_SYSTEM_KEY,StandardIdSystemFactory.SINGLE_BTREE);
	}
	
	public byte idSystemType() {
		return _config.getAsByte(ID_SYSTEM_KEY);
	}

	public void useInMemoryIdSystem() {
		_config.put(ID_SYSTEM_KEY,StandardIdSystemFactory.IN_MEMORY);
	}

	public void useCustomIdSystem(IdSystemFactory factory) {
		_config.put(ID_SYSTEM_KEY, StandardIdSystemFactory.CUSTOM);
		_config.put(ID_SYSTEM_CUSTOM_FACTORY_KEY, factory);
	}
	
	public IdSystemFactory customIdSystemFactory(){
		return (IdSystemFactory) _config.get(ID_SYSTEM_CUSTOM_FACTORY_KEY);
	}

	public void asynchronousSync(boolean flag) {
		_config.put(ASYNCHRONOUS_SYNC_KEY, flag);
	}
	
	public boolean asynchronousSync(){
		return _config.getAsBoolean(ASYNCHRONOUS_SYNC_KEY);
	}

	public int maxStackDepth() {
		return _config.getAsInt(MAX_STACK_DEPTH_KEY);
	}

	public void maxStackDepth(int maxStackDepth) {
		_config.put(MAX_STACK_DEPTH_KEY, maxStackDepth);
	}

}