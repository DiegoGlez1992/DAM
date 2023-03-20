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
package com.db4o.cs.internal;

import java.io.*;
import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.cs.caching.*;
import com.db4o.cs.config.*;
import com.db4o.cs.internal.caching.*;
import com.db4o.cs.internal.config.*;
import com.db4o.cs.internal.events.*;
import com.db4o.cs.internal.messages.*;
import com.db4o.cs.internal.objectexchange.*;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.convert.*;
import com.db4o.internal.encoding.*;
import com.db4o.internal.events.*;
import com.db4o.internal.qlin.*;
import com.db4o.internal.query.processor.*;
import com.db4o.internal.query.result.*;
import com.db4o.internal.references.*;
import com.db4o.internal.slots.*;
import com.db4o.io.*;
import com.db4o.qlin.*;
import com.db4o.reflect.*;

/**
 * @exclude
 */
public class ClientObjectContainer extends ExternalObjectContainer implements ExtClient, BlobTransport, ClientMessageDispatcher {
	
	final Object _blobLock = new Object();

	private BlobProcessor _blobTask;

	private Socket4Adapter _socket;

	private BlockingQueue _synchronousMessageQueue = new BlockingQueue();
	
	private BlockingQueue _asynchronousMessageQueue = new BlockingQueue();

	private final String _password; // null denotes password not necessary

	int[] _prefetchedIDs;

	ClientMessageDispatcher _messageDispatcher;
	
	ClientAsynchronousMessageProcessor _asynchronousMessageProcessor;

	int remainingIDs;

	private String switchedToFile;

	private boolean _singleThreaded;

	private final String _userName;

	private Db4oDatabase i_db;

	protected boolean _doFinalize=true;
    
    private int _blockSize = 1;
    
	private Collection4 _batchedMessages = new Collection4();
	
	// initial value of _batchedQueueLength is
	// used for to write the number of messages.
	private int _batchedQueueLength = Const4.INT_LENGTH;

	private boolean _login;
	
	private final ClientHeartbeat _heartbeat;
	
    private final ClassInfoHelper _classInfoHelper;
    
    private ClientSlotCache _clientSlotCache;

    private int _serverSideID = 0;
    
	private MessageListener _messageListener = new MessageListener() {
		public void onMessage(Msg msg) {
			// do nothing
		}
	};

	private boolean _bypassSlotCache = false;
	
	public interface MessageListener {
		public void onMessage(Msg msg);
	}
	
	static{
		// Db4o.registerClientConstructor(new ClientConstructor());
	}

	public ClientObjectContainer(ClientConfiguration config, Socket4Adapter socket, String user, String password, boolean login) {
		this((ClientConfigurationImpl)config, socket, user, password, login);
	}
	
	public ClientObjectContainer(ClientConfigurationImpl config, Socket4Adapter socket, String user, String password, boolean login) {
		super(Db4oClientServerLegacyConfigurationBridge.asLegacy(config));
		_userName = user;
		_password = password;
		_login = login;
		_heartbeat = new ClientHeartbeat(this);
		_classInfoHelper = new ClassInfoHelper(Db4oClientServerLegacyConfigurationBridge.asLegacy(config));
		setAndConfigSocket(socket);
		open();
		config.applyConfigurationItems(this);
	}

	private void setAndConfigSocket(Socket4Adapter socket) {
		_socket = socket;
		_socket.setSoTimeout(_config.timeoutClientSocket());
	}
	
	protected final void openImpl() {
        initializeClassMetadataRepository();
        initalizeWeakReferenceSupport();
		initalizeClientSlotCache();
		_singleThreaded = configImpl().singleThreadedClient();
		// TODO: Experiment with packet size and noDelay
		// socket.setSendBufferSize(100);
		// socket.setTcpNoDelay(true);
		// System.out.println(socket.getSendBufferSize());
		if (_login) {
			loginToServer(_socket);
		}
		if (!_singleThreaded) {
			startDispatcherThread(_socket, _userName);
		}
		logMsg(36, toString());
		startHeartBeat();
		readThis();
	}
	
	private final void initalizeClientSlotCache(){
		configImpl().prefetchSettingsChanged().addListener(new EventListener4<EventArgs>(){
			public void onEvent(Event4<EventArgs> e, EventArgs args) {
				initalizeClientSlotCache();
			}
		});
		if(configImpl().prefetchSlotCacheSize() > 0){
			_clientSlotCache = new ClientSlotCacheImpl(this);
			return;
		}
		_clientSlotCache = new NullClientSlotCache();
	}
	
	private void startHeartBeat(){
	    _heartbeat.start();
	}
	
	private void startDispatcherThread(Socket4Adapter socket, String user) {
		if(! _singleThreaded){
			startAsynchronousMessageProcessor();
		}
		
		final ClientMessageDispatcherImpl dispatcherImpl = new ClientMessageDispatcherImpl(this, socket, _synchronousMessageQueue, _asynchronousMessageQueue);
		String dispatcherName = "db4o client side message dispatcher for " + user;
		_messageDispatcher = dispatcherImpl;
		threadPool().start(dispatcherName, dispatcherImpl);
	}

	private void startAsynchronousMessageProcessor() {
	    _asynchronousMessageProcessor = new ClientAsynchronousMessageProcessor(_asynchronousMessageQueue);
	    threadPool().start("Client Asynchronous Message Processor Thread for: " + toString(), _asynchronousMessageProcessor);
    }

	public void backup(Storage targetStorage, String path) throws NotSupportedException {
		throw new NotSupportedException();
	}
	
	public void closeTransaction(Transaction transaction, boolean isSystemTransaction, boolean rollbackOnClose) {
		if(isSystemTransaction){
			return;
		}
		_transaction.close(rollbackOnClose);
	}
	
	public void reserve(int byteCount) {
		throw new NotSupportedException();
	}
    
    public byte blockSize() {
        return (byte)_blockSize;
    }

    protected void close2() {
		if ((!_singleThreaded) && (_messageDispatcher == null || !_messageDispatcher.isMessageDispatcherAlive())) {
		    stopHeartBeat();
			shutdownObjectContainer();
			return;
		}
		try {
			commit1(_transaction);
		} catch (Exception e) {
			Exceptions4.catchAllExceptDb4oException(e);
		}
		try {
			write(Msg.CLOSE);
		} catch (Exception e) {
			Exceptions4.catchAllExceptDb4oException(e);
		}
		
		shutDownCommunicationRessources();
		
		try {
			_socket.close();
		} catch (Exception e) {
			Exceptions4.catchAllExceptDb4oException(e);
		}
		
		shutdownObjectContainer();
	}
    
    private void stopHeartBeat(){
        _heartbeat.stop();
    }
    
    private void closeMessageDispatcher(){
        try {
            if (!_singleThreaded) {
                _messageDispatcher.close();
            }
        } catch (Exception e) {
            Exceptions4.catchAllExceptDb4oException(e);
        }
        try {
            if (!_singleThreaded) {
            	_asynchronousMessageProcessor.stopProcessing();
            }
        } catch (Exception e) {
            Exceptions4.catchAllExceptDb4oException(e);
        }
    }

	public final void commit1(Transaction trans) {
		trans.commit();
	}
    
    public int converterVersion() {
        return Converter.VERSION;
    }
	
	Socket4Adapter createParallelSocket() throws IOException {
		write(Msg.GET_THREAD_ID);
		
		int serverThreadID = expectedBufferResponse(Msg.ID_LIST).readInt();

		Socket4Adapter sock = _socket.openParalellSocket();
		loginToServer(sock);

		if (switchedToFile != null) {
			MsgD message = Msg.SWITCH_TO_FILE.getWriterForString(systemTransaction(),
					switchedToFile);
			message.write(sock);
			if (!(Msg.OK.equals(Msg.readMessage(this, systemTransaction(), sock)))) {
				throw new IOException(Messages.get(42));
			}
		}
		Msg.USE_TRANSACTION.getWriterForInt(_transaction, serverThreadID).write(
				sock);
		return sock;
	}

	public AbstractQueryResult newQueryResult(Transaction trans, QueryEvaluationMode mode) {
		throw new IllegalStateException();
	}

	final public Transaction newTransaction(Transaction parentTransaction, ReferenceSystem referenceSystem, boolean isSystemTransaction) {
		return new ClientTransaction(this, parentTransaction, referenceSystem);
	}

	public boolean createClassMetadata(ClassMetadata clazz, ReflectClass claxx, ClassMetadata superClazz) {		
		write(Msg.CREATE_CLASS.getWriterForString(systemTransaction(), config().resolveAliasRuntimeName(claxx.getName())));
		Msg resp = getResponse();
		if (resp == null) {
			return false;
		}
		
		if (resp.equals(Msg.FAILED)) {
			// if the class can not be created on the server, send class meta to the server.
			sendClassMeta(claxx);
			resp = getResponse();
		}
		
		if (resp.equals(Msg.FAILED)) {
			if (configImpl().exceptionsOnNotStorable()) {
				throw new ObjectNotStorableException(claxx);
			}
			return false;
		}
		if (!resp.equals(Msg.OBJECT_TO_CLIENT)) {
			return false;
		}

		MsgObject message = (MsgObject) resp;
		StatefulBuffer bytes = message.unmarshall();
		if (bytes == null) {
			return false;
		}
		bytes.setTransaction(systemTransaction());
		if (!super.createClassMetadata(clazz, claxx, superClazz)) {
			return false;
		}
		clazz.setID(message.getId());
		clazz.readName1(systemTransaction(), bytes);
		classCollection().addClassMetadata(clazz);
		classCollection().readClassMetadata(clazz, claxx);
		return true;
	}

	private void sendClassMeta(ReflectClass reflectClass) {
		ClassInfo classMeta = _classInfoHelper.getClassMeta(reflectClass);
		write(Msg.CLASS_META.getWriter(Serializer.marshall(systemTransaction(),classMeta)));
	}
	
	public long currentVersion() {
		write(Msg.CURRENT_VERSION);
		return ((MsgD) expectedResponse(Msg.ID_LIST)).readLong();
	}

	public final boolean delete4(Transaction ta, ObjectReference yo, Object obj, int a_cascade, boolean userCall) {
		MsgD msg = Msg.DELETE.getWriterForInts(_transaction, new int[] { yo.getID(), userCall ? 1 : 0 });
		writeBatchedMessage(msg);
		return true;
	}

	public boolean detectSchemaChanges() {
		return false;
	}

	protected boolean doFinalize() {
		return _doFinalize;
	}
	
	final ByteArrayBuffer expectedBufferResponse(Msg expectedMessage) {
		Msg msg = expectedResponse(expectedMessage);
		if (msg == null) {
			// TODO: throw Exception to allow
			// smooth shutdown
			return null;
		}
		return msg.getByteLoad();
	}

	public final Msg expectedResponse(Msg expectedMessage) {
		Msg message = getResponse();
		if (expectedMessage.equals(message)) {
			return message;
		}
		checkExceptionMessage(message);
		throw new IllegalStateException("Unexpected Message:" + message
				+ "  Expected:" + expectedMessage);
	}

	private void checkExceptionMessage(Msg msg) {
		if(msg instanceof MRuntimeException) {
			((MRuntimeException)msg).throwPayload();
		}
	}
		
	public AbstractQueryResult queryAllObjects(Transaction trans) {
		int mode = config().evaluationMode().asInt();
		MsgD msg = Msg.GET_ALL.getWriterForInts(trans, mode, prefetchDepth(), prefetchCount());
		write(msg);
		return readQueryResult(trans);
	}
	
    public final HardObjectReference getHardReferenceBySignature(Transaction trans, long uuid, byte[] signature) {
        int messageLength = Const4.LONG_LENGTH + Const4.INT_LENGTH + signature.length;
        MsgD message = Msg.OBJECT_BY_UUID.getWriterForLength(trans, messageLength);
        message.writeLong(uuid);
        message.writeInt(signature.length);
        message.writeBytes(signature);
        write(message);
        message = (MsgD)expectedResponse(Msg.OBJECT_BY_UUID);
        int id = message.readInt();
        if(id > 0){
            return getHardObjectReferenceById(trans, id);
        }
        return HardObjectReference.INVALID;
    }

	/**
	 * may return null, if no message is returned. Error handling is weak and
	 * should ideally be able to trigger some sort of state listener (connection
	 * dead) on the client.
	 */
	public Msg getResponse() {
		while(true){
			Msg msg = _singleThreaded ? getResponseSingleThreaded(): getResponseMultiThreaded();
			if(isClientSideMessage(msg)){
				if(((ClientSideMessage)msg).processAtClient()){
					continue;
				}
			}
			return msg;
		}
	}
	
	private Msg getResponseSingleThreaded() {
		while (isMessageDispatcherAlive()) {
			try {
				final Msg message = Msg.readMessage(this, _transaction, _socket);
				if(isClientSideMessage(message)) {
					if(((ClientSideMessage)message).processAtClient()){
						continue;
					}
				}
				return message;
	         } catch (Db4oIOException exc) {
	             onMsgError();
	         }
		}
		return null;
	}

	private Msg getResponseMultiThreaded() {
		Msg msg;
		try {
			msg = (Msg)_synchronousMessageQueue.next();
		} catch (BlockingQueueStoppedException e) {
			if(DTrace.enabled){
				DTrace.BLOCKING_QUEUE_STOPPED_EXCEPTION.log(e.toString());
			}
			msg = Msg.ERROR;
		}
		if(msg instanceof MError) {	
			onMsgError();
		}
		return msg;
	}
	
	private boolean isClientSideMessage(Msg message) {
		return message instanceof ClientSideMessage;
	}

	private void onMsgError() {
		close();
		throw new DatabaseClosedException();
	}
	
	public boolean isMessageDispatcherAlive() {
		return _socket != null;
	}

	public ClassMetadata classMetadataForID(int clazzId) {
		if(clazzId == 0) {
			return null;
		}
		ClassMetadata yc = super.classMetadataForID(clazzId);
		if (yc != null) {
			return yc;
		}
		MsgD msg = Msg.CLASS_NAME_FOR_ID.getWriterForInt(systemTransaction(), clazzId);
		write(msg);
		MsgD message = (MsgD) expectedResponse(Msg.CLASS_NAME_FOR_ID);
		String className = config().resolveAliasStoredName(message.readString());
		if (className != null && className.length() > 0) {
			ReflectClass claxx = reflector().forName(className);
			if (claxx != null) {
				return produceClassMetadata(claxx);
			}
			// TODO inform client class not present
		}
		return null;
	}

	public boolean needsLockFileThread() {
		return false;
	}

	protected boolean hasShutDownHook() {
		return false;
	}

	public Db4oDatabase identity() {
		if (i_db == null) {
			write(Msg.IDENTITY);
			ByteArrayBuffer reader = expectedBufferResponse(Msg.ID_LIST);
			showInternalClasses(true);
			try {
				i_db = (Db4oDatabase) getByID(systemTransaction(), reader.readInt());
				activate(systemTransaction(), i_db, new FixedActivationDepth(3));
			} finally {
				showInternalClasses(false);
			}
		}
		return i_db;
	}

	public boolean isClient() {
		return true;
	}

	private void loginToServer(Socket4Adapter iSocket) throws InvalidPasswordException {
		UnicodeStringIO stringWriter = new UnicodeStringIO();
		int length = stringWriter.length(_userName)
				+ stringWriter.length(_password);
		MsgD message = Msg.LOGIN
				.getWriterForLength(systemTransaction(), length);
		message.writeString(_userName);
		message.writeString(_password);
		message.write(iSocket);
		Msg msg = readLoginMessage(iSocket);
		ByteArrayBuffer payLoad = msg.payLoad();
		blockSize(payLoad.readInt());
		int doEncrypt = payLoad.readInt();
		if (doEncrypt == 0) {
			_handlers.oldEncryptionOff();
		}
		if(payLoad.remainingByteCount() > 0) {
			_serverSideID = payLoad.readInt();
		}
	}
	
	private Msg readLoginMessage(Socket4Adapter iSocket){
       Msg msg = Msg.readMessage(this, systemTransaction(), iSocket);
       while(Msg.PONG.equals(msg)){
           msg = Msg.readMessage(this, systemTransaction(), iSocket);
       }
       if (!Msg.LOGIN_OK.equals(msg)) {
            throw new InvalidPasswordException();
       }
       return msg;
	}

	public boolean maintainsIndices() {
		return false;
	}

	public final int idForNewUserObject(Transaction trans) {
		int prefetchIDCount = config().prefetchIDCount();
		ensureIDCacheAllocated(prefetchIDCount);
		ByteArrayBuffer reader = null;
		if (remainingIDs < 1) {
			MsgD msg = Msg.PREFETCH_IDS.getWriterForInt(_transaction, prefetchIDCount);
			write(msg);
			reader = expectedBufferResponse(Msg.ID_LIST);
			for (int i = prefetchIDCount - 1; i >= 0; i--) {
				_prefetchedIDs[i] = reader.readInt();
			}
			remainingIDs = prefetchIDCount;
		}
		remainingIDs--;
		return _prefetchedIDs[remainingIDs];
	}

	void processBlobMessage(MsgBlob msg) {
		synchronized (_blobLock) {
			boolean needStart = _blobTask == null || _blobTask.isTerminated();
			if (needStart) {
				_blobTask = new BlobProcessor(this);
			}
			_blobTask.add(msg);
			if (needStart) {
				threadPool().startLowPriority("Blob processor task", _blobTask);
			}
		}
	}

	public void raiseCommitTimestamp(long a_minimumVersion) {
		synchronized(lock()){
			write(Msg.RAISE_COMMIT_TIMESTAMP.getWriterForLong(_transaction, a_minimumVersion));
		}
	}

	public void readBytes(byte[] bytes, int address, int addressOffset, int length) {
		throw Exceptions4.virtualException();
	}

	public void readBytes(byte[] a_bytes, int a_address, int a_length) {
		MsgD msg = Msg.READ_SLOT.getWriterForInts(_transaction, new int[] {
				a_address, a_length });
		write(msg);
		ByteArrayBuffer reader = expectedBufferResponse(Msg.READ_SLOT);
		System.arraycopy(reader._buffer, 0, a_bytes, 0, a_length);
	}

	protected boolean applyRenames(Config4Impl config) {
		logMsg(58, null);
		return false;
	}
	
	public final StatefulBuffer readStatefulBufferById(Transaction a_ta, int a_id) {
		return readStatefulBufferById(a_ta, a_id, false);
	}
	
	public final StatefulBuffer readStatefulBufferById(Transaction a_ta, int a_id, boolean lastCommitted) {
		MsgD msg = Msg.READ_OBJECT.getWriterForInts(a_ta, new int[]{a_id, lastCommitted?1:0});
		write(msg);
		StatefulBuffer bytes = ((MsgObject) expectedResponse(Msg.OBJECT_TO_CLIENT)).unmarshall();
		if(bytes != null){
			bytes.setTransaction(a_ta);
		}
		return bytes;
	}
	
	@Override
	public Object peekPersisted(Transaction trans, Object obj, ActivationDepth depth, boolean committed)
	        throws DatabaseClosedException {
		_bypassSlotCache = true;
		try { 
			return super.peekPersisted(trans, obj, depth, committed);
		} finally {
			_bypassSlotCache = false;
		}
	}
	
	@Override
	protected void refreshInternal(final Transaction trans, final Object obj, final int depth) {
		_bypassSlotCache  = true;
		try {
			super.refreshInternal(trans, obj, depth);
		} finally {
			_bypassSlotCache = false;
		}
	}

	@Override
	public final ByteArrayBuffer[] readSlotBuffers(final Transaction transaction, final int[] ids) {
		return readSlotBuffers(transaction, ids, 1);
	}
	
	public final ByteArrayBuffer[] readObjectSlots(final Transaction transaction, final int[] ids) {
		
		final int prefetchDepth = config().prefetchDepth();
		return readSlotBuffers(transaction, ids, prefetchDepth);
    }

	private ByteArrayBuffer[] readSlotBuffers(final Transaction transaction, final int[] ids, final int prefetchDepth) {
	    final Map<Integer, ByteArrayBuffer> buffers = new HashMap(ids.length);
        	
    	final ArrayList<Integer> cacheMisses = populateSlotBuffersFromCache(transaction, ids, buffers);
		fetchMissingSlotBuffers(transaction, cacheMisses, buffers, prefetchDepth);
        
        return packSlotBuffers(ids, buffers);
    }

	public final ByteArrayBuffer readBufferById(final Transaction transaction, final int id, final boolean lastCommitted) {
		
		if (lastCommitted || _bypassSlotCache) {
			return fetchSlotBuffer(transaction, id, lastCommitted);
		}
		
		final ByteArrayBuffer cached = _clientSlotCache.get(transaction, id);
		if (cached != null) {
			return cached;
		}
		
		final ByteArrayBuffer slot = fetchSlotBuffer(transaction, id, lastCommitted);
		_clientSlotCache.add(transaction, id, slot);
		return slot;
	}

	public final ByteArrayBuffer readBufferById(Transaction a_ta, int a_id) {
		return readBufferById(a_ta, a_id, false); 
	}

	private AbstractQueryResult readQueryResult(final Transaction trans) {
		
		final ByRef<AbstractQueryResult> result = ByRef.newInstance();
		
		withEnvironment(new Runnable() { public void run() {
			
			ByteArrayBuffer reader = expectedBufferResponse(Msg.QUERY_RESULT);
			int queryResultID = reader.readInt();
			AbstractQueryResult queryResult = queryResultFor(trans, queryResultID);
			
			queryResult.loadFromIdReader(idIteratorFor(trans, reader));
			
			result.value = queryResult;
			
		}});
		return result.value;
	}

	public FixedSizeIntIterator4 idIteratorFor(Transaction trans, ByteArrayBuffer reader) {
		return idIteratorFor(objectExchangeStrategy(), trans, reader);
    }

	private FixedSizeIntIterator4 idIteratorFor(final ObjectExchangeStrategy strategy, Transaction trans,
            ByteArrayBuffer reader) {
	    return strategy.unmarshall((ClientTransaction)trans, _clientSlotCache, reader);
    }

	private ObjectExchangeStrategy objectExchangeStrategy() {
		return ObjectExchangeStrategyFactory.forConfig(defaultObjectExchangeConfiguration());
    }

	private ObjectExchangeConfiguration defaultObjectExchangeConfiguration() {
	    return new ObjectExchangeConfiguration(prefetchDepth(), prefetchCount());
    }

	void readThis() {
		write(Msg.GET_CLASSES.getWriter(systemTransaction()));
		ByteArrayBuffer bytes = expectedBufferResponse(Msg.GET_CLASSES);
		classCollection().setID(bytes.readInt());
		
		final byte stringEncoding = bytes.readByte();
		createStringIO(stringEncoding);
		
		classCollection().read(systemTransaction());
	}
	
	public void releaseSemaphore(Transaction trans, final String name){
		synchronized (_lock) {
			checkClosed();
			if (name == null) {
				throw new NullPointerException();
			}
			trans = checkTransaction(trans);
			write(Msg.RELEASE_SEMAPHORE.getWriterForString(trans, name));
		}
	}

	public void releaseSemaphore(String name) {
		releaseSemaphore(_transaction, name);
	}

	public void releaseSemaphores(Transaction ta) {
		// do nothing
	}

	public final void rollback1(Transaction trans) {
		if (_config.batchMessages()) {
			clearBatchedObjects();
		} 
		write(Msg.ROLLBACK);
		trans.rollback();
	}

	public void send(Object obj) {
		synchronized (_lock) {
			if (obj != null) {
				final MUserMessage message = Msg.USER_MESSAGE;
				write(message.marshallUserMessage(_transaction, obj));
			}
		}
	}

	public final void setDirtyInSystemTransaction(PersistentBase a_object) {
		// do nothing
	}
	
	public boolean setSemaphore(Transaction trans, final String name, final int timeout){
		synchronized (_lock) {
			checkClosed();
			trans = checkTransaction(trans);
			if (name == null) {
				throw new NullPointerException();
			}
			MsgD msg = Msg.SET_SEMAPHORE.getWriterForIntString(trans,timeout, name);
			write(msg);
			Msg message = getResponse();
			return (message.equals(Msg.SUCCESS));
		}
		
	}

	public boolean setSemaphore(String name, int timeout) {
		return setSemaphore(_transaction, name, timeout);
	}

	protected String defaultToString() {
		return "Client connection " + _userName + "(" +  _socket + ")";
	}

	public void shutdown() {
		// do nothing
	}

	public final void writeDirtyClassMetadata() {
		// do nothing
	}

	public final boolean write(Msg msg) {
		writeMsg(msg, true);
		return true;
	}
	
	public final void writeBatchedMessage(Msg msg) {
		writeMsg(msg, false);
	}
	
	public final void writeMsg(Msg msg, boolean flush) {
		if(_config.batchMessages()) {
			if(flush && _batchedMessages.isEmpty()) {
				// if there's nothing batched, just send this message directly
				writeMessageToSocket(msg);
			} else {
				addToBatch(msg);
				if(flush || _batchedQueueLength > _config.maxBatchQueueSize()) {
					writeBatchedMessages();
				}
			}
		} else {
			if (!_batchedMessages.isEmpty()) {
				addToBatch(msg);
				writeBatchedMessages();
			} else {
				writeMessageToSocket(msg);
			}
		}
	}

	public boolean writeMessageToSocket(Msg msg) {
		if(_messageListener != null){
			_messageListener.onMessage(msg);
		}
		return msg.write(_socket);
	}
	
	public final void writeNew(Transaction trans, Pointer4 pointer, ClassMetadata classMetadata, ByteArrayBuffer buffer) {
		MsgD msg = Msg.WRITE_NEW.getWriter(trans, pointer, classMetadata, buffer);
		writeBatchedMessage(msg);
	}
    
	public final void writeUpdate(Transaction trans, Pointer4 pointer, ClassMetadata classMetadata, ArrayType arrayType, ByteArrayBuffer buffer) {
		MsgD msg = Msg.WRITE_UPDATE.getWriter(trans, pointer, classMetadata, arrayType.value(), buffer);
		writeBatchedMessage(msg);
	}

	public boolean isAlive() {
		try {
			synchronized (lock()) {
				if(isClosed()) {
					return false;
				}
				
				write(Msg.IS_ALIVE);
				return expectedResponse(Msg.IS_ALIVE) != null;				
			}
		} catch (Db4oException exc) {
			return false;
		}
	}

	public Socket4Adapter socket() {
		return _socket;
	}
	
	private void ensureIDCacheAllocated(int prefetchIDCount) {
		if(_prefetchedIDs==null) {
			_prefetchedIDs = new int[prefetchIDCount];
			return;
		}
		if(prefetchIDCount>_prefetchedIDs.length) {
			int[] newPrefetchedIDs=new int[prefetchIDCount];
			System.arraycopy(_prefetchedIDs, 0, newPrefetchedIDs, 0, _prefetchedIDs.length);
			_prefetchedIDs=newPrefetchedIDs;
		}
	}

    public SystemInfo systemInfo() {
        throw new NotImplementedException("Functionality not availble on clients.");
    }

	
    public void writeBlobTo(Transaction trans, BlobImpl blob) throws IOException {
        MsgBlob msg = (MsgBlob) Msg.READ_BLOB.getWriterForInt(trans, (int) getID(blob));
        msg._blob = blob;
        processBlobMessage(msg);
    }
    
    public void readBlobFrom(Transaction trans, BlobImpl blob) throws IOException {
        MsgBlob msg = null;
        synchronized (lock()) {
            store(blob);
            int id = (int) getID(blob);
            msg = (MsgBlob) Msg.WRITE_BLOB.getWriterForInt(trans, id);
            msg._blob = blob;
            blob.setStatus(Status.QUEUED);
        }
        processBlobMessage(msg);
    }
    
    public void deleteBlobFile(Transaction trans, BlobImpl blob){
        MDeleteBlobFile msg = (MDeleteBlobFile) Msg.DELETE_BLOB_FILE.getWriterForInt(trans, (int) getID(blob));
		writeMsg(msg, false);
    }

    public long[] getIDsForClass(final Transaction trans, ClassMetadata clazz){
    	boolean triggerQueryEvents = false;
    	return getIDsForClass(trans, clazz, triggerQueryEvents);
    }

	private long[] getIDsForClass(final Transaction trans, ClassMetadata clazz, boolean triggerQueryEvents) {
		MsgD msg = Msg.GET_INTERNAL_IDS.getWriterForInts(trans, clazz.getID(), prefetchDepth(), prefetchCount(), triggerQueryEvents ? 1 : 0);
    	write(msg);
    	
    	final ByRef<long[]> result = ByRef.newInstance();
    	
    	withEnvironment(new Runnable() { public void run() {
	    	ByteArrayBuffer reader = expectedBufferResponse(Msg.ID_LIST);
	    	FixedSizeIntIterator4 idIterator = idIteratorFor(trans, reader);
	    	result.value = toLongArray(idIterator);
	    }});
    	
    	return result.value;
	}

    @Override
    public QueryResult classOnlyQuery(QQueryBase query, ClassMetadata clazz){
    	final Transaction trans = query.transaction();
    	long[] ids = getIDsForClass(trans, clazz, true); 
    	ClientQueryResult resClient = new ClientQueryResult(trans, ids.length);
    	for (int i = 0; i < ids.length; i++) {
    		resClient.add((int)ids[i]);
    	}
    	return resClient;
    }

	private long[] toLongArray(FixedSizeIntIterator4 idIterator) {
	    final long[] ids = new long[idIterator.size()];
    	int i = 0;
    	while (idIterator.moveNext()) {
    	    ids[i++] = (Integer)idIterator.current();
    	}
    	return ids;
    }

	int prefetchDepth() {
	    return _config.prefetchDepth();
    }
	
	int prefetchCount() {
	    return _config.prefetchObjectCount();
    }
    
    
    public QueryResult executeQuery(QQuery query){
    	Transaction trans = query.transaction();
    	query.captureQueryResultConfig();
        query.marshall();
		MsgD msg = Msg.QUERY_EXECUTE.getWriter(Serializer.marshall(trans,query));
		write(msg);
		return readQueryResult(trans);
    }

    public final void writeBatchedMessages() {
    	synchronized(lock()) {
			if (_batchedMessages.isEmpty()) {
				return;
			}
	
			Msg msg;
			MsgD multibytes = Msg.WRITE_BATCHED_MESSAGES.getWriterForLength(
					transaction(), _batchedQueueLength);
			multibytes.writeInt(_batchedMessages.size());
			Iterator4 iter = _batchedMessages.iterator();
			while(iter.moveNext()) {
				msg = (Msg) iter.current();
				if (msg == null) {
					multibytes.writeInt(0);
				} 
				else {
					multibytes.writeInt(msg.payLoad().length());
					multibytes.payLoad().append(msg.payLoad()._buffer);
				}
			}
			writeMessageToSocket(multibytes);
			clearBatchedObjects();
    	}
	}

	public final void addToBatch(Msg msg) {
		synchronized(lock()) {
			_batchedMessages.add(msg);
			// the first INT_LENGTH is for buffer.length, and then buffer content.
			_batchedQueueLength += Const4.INT_LENGTH + msg.payLoad().length();
		}
	}

	private final void clearBatchedObjects() {
		_batchedMessages.clear();
		// initial value of _batchedQueueLength is Const4.INT_LENGTH, which is
		// used for to write the number of messages.
		_batchedQueueLength = Const4.INT_LENGTH;
	}

	int timeout() {
	    return configImpl().timeoutClientSocket();
	}

	protected void shutdownDataStorage() {
	    shutDownCommunicationRessources();
	}
	
	private void shutDownCommunicationRessources() {
	    stopHeartBeat();
	    closeMessageDispatcher();
	    _synchronousMessageQueue.stop();
	    _asynchronousMessageQueue.stop();
	}

	public void setDispatcherName(String name) {
		// do nothing here		
	}
	
	public ClientMessageDispatcher messageDispatcher() {
		return _singleThreaded ? this : _messageDispatcher;
	}

	public void onCommittedListenerAdded() {
		if(_singleThreaded) {
			return;
		}
		write(Msg.COMMITTED_CALLBACK_REGISTER);
		expectedResponse(Msg.OK);
	}
	
	@Override
	public ClassMetadata classMetadataForReflectClass(ReflectClass claxx) {
		ClassMetadata classMetadata = super.classMetadataForReflectClass(claxx);
		if(classMetadata != null){
			return classMetadata;
		}
		String className = config().resolveAliasRuntimeName(claxx.getName());
		if( classMetadataIdForName(className) == 0){
			return null;
		}
		return produceClassMetadata(claxx);
	}
	
	public int classMetadataIdForName(String name) {
        MsgD msg = Msg.CLASS_METADATA_ID_FOR_NAME.getWriterForString(systemTransaction(), name);
        msg.write(_socket);
        MsgD response = (MsgD) expectedResponse(Msg.CLASS_ID);
        return response.readInt();
    }

	public int instanceCount(ClassMetadata clazz, Transaction trans) {
        MsgD msg = Msg.INSTANCE_COUNT.getWriterForInt(trans, clazz.getID());
        write(msg);
        MsgD response = (MsgD) expectedResponse(Msg.INSTANCE_COUNT);
        return response.readInt();
	}
	
	public void messageListener(MessageListener listener){
		_messageListener = listener;
	}
	
	@Override
	public void storeAll(final Transaction transaction, final Iterator4 objects) {
		boolean configuredBatchMessages = _config.batchMessages();
		_config.batchMessages(true);
		try{
			super.storeAll(transaction, objects);
		} finally{
			_config.batchMessages(configuredBatchMessages);
		}
	}

	private void sendReadMultipleObjectsMessage(MReadMultipleObjects message, final Transaction transaction, final int prefetchDepth, final List<Integer> idsToRead) {
	    MsgD msg = message.getWriterForLength(transaction, Const4.INT_LENGTH + Const4.INT_LENGTH + Const4.ID_LENGTH * idsToRead.size());
	    msg.writeInt(prefetchDepth);
	    msg.writeInt(idsToRead.size());
	    for (int id : idsToRead) {
	    	msg.writeInt(id);
	    }
	    write(msg);
    }

	private AbstractQueryResult queryResultFor(final Transaction trans, int queryResultID) {
	    if (queryResultID > 0) { 
	    	return new LazyClientQueryResult(trans, ClientObjectContainer.this, queryResultID);
	    }
	    return new ClientQueryResult(trans);
    }

	private void fetchMissingSlotBuffers(final Transaction transaction, final ArrayList<Integer> missing,
            final Map<Integer, ByteArrayBuffer> buffers, int prefetchDepth) {
	    if (missing.size() == 0) {
	    	return;
	    }
	    
    	final int safePrefetchDepth = Math.max(1, prefetchDepth);
    	sendReadMultipleObjectsMessage(Msg.READ_MULTIPLE_OBJECTS, transaction, safePrefetchDepth, missing);
    	
    	final MsgD response = (MsgD) expectedResponse(Msg.READ_MULTIPLE_OBJECTS);
    	
    	Iterator4<Pair<Integer, ByteArrayBuffer>> slots = new CacheContributingObjectReader((ClientTransaction) transaction, _clientSlotCache, response.payLoad()).buffers();
    	while (slots.moveNext()) {
    		final Pair<Integer, ByteArrayBuffer> pair = slots.current();
			buffers.put(pair.first, pair.second);
    	}
    }
	
	private ByteArrayBuffer[] packSlotBuffers(final int[] ids, final Map<Integer, ByteArrayBuffer> buffers) {
        final ByteArrayBuffer[] returnValue = new ByteArrayBuffer[buffers.size()];
    	for (int i=0; i<ids.length; ++i) {
    		returnValue[i] = buffers.get(ids[i]);
    	}
        return returnValue;
    }

	private ArrayList<Integer> populateSlotBuffersFromCache(final Transaction transaction, final int[] ids,
            final Map<Integer, ByteArrayBuffer> buffers) {
	    final ArrayList<Integer> missing = new ArrayList();
	    
	    for (int id: ids) {
	    	final ByteArrayBuffer slot = _clientSlotCache.get(transaction, id);
	    	if (null == slot) {
	    		missing.add(id);
	    	} else {
	    		buffers.put(id, slot);
	    	}
	    }
	    return missing;
    }

	private ByteArrayBuffer fetchSlotBuffer(final Transaction transaction, final int id, final boolean lastCommitted) {
	    MsgD msg = Msg.READ_READER_BY_ID.getWriterForInts(transaction, new int[]{id, lastCommitted?1:0});
	    write(msg);
	    final ByteArrayBuffer buffer = ((MReadBytes) expectedResponse(Msg.READ_BYTES)).unmarshall();
	    return buffer;
    }

	@Override
	protected void fatalStorageShutdown() {
		shutdownDataStorage();
	}
	
	/**
	 * @sharpen.property
	 */
	public String userName() {
		return _userName;
	}
	
	@Override
	public boolean isDeleted(Transaction trans, int id){
        // This one really is a hack.
        // It only helps to get information about the current
        // transaction.

        // We need a better strategy for C/S concurrency behaviour.
        MsgD msg = Msg.TA_IS_DELETED.getWriterForInt(trans, id);
		write(msg);
        int res = expectedBufferResponse(Msg.TA_IS_DELETED).readInt();
        return res == 1;
	}
	
    public void blockSize(int size){
    	createBlockConverter(size);
    	_blockSize = size;
    }
    
    @Override
    protected void closeIdSystem() {
    	// do nothing
    }
    
	public ObjectContainer openSession(){
		synchronized(lock()){
			return new ObjectContainerSession(this);
		}
	}
	
    public int serverSideID() {
    	return _serverSideID;
    }
    
	public EventRegistryImpl newEventRegistry(){
		return new ClientEventRegistryImpl(this);
	}
	
	public <T> QLin<T> from(Class<T> clazz) {
		return new QLinRoot<T>(query(), clazz);
	}
	
	public void commitReplication(long replicationRecordId, long timestamp){
		synchronized (_lock) {		
			checkReadOnly();
			ClientTransaction clientTransaction = (ClientTransaction) transaction();
			clientTransaction.preCommit();
			write(Msg.COMMIT_REPLICATION.getWriterForLongs(clientTransaction, replicationRecordId, timestamp));
			expectedResponse(Msg.OK);
			clientTransaction.postCommit();
		}
	}

}
