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
package com.db4o.io;

import com.db4o.ext.*;
import com.db4o.internal.fileheader.*;

/**
 * CachedIoAdapter is an IOAdapter for random access files, which caches data
 * for IO access. Its functionality is similar to OS cache.<br>
 * Example:<br>
 * <code>delegateAdapter = new RandomAccessFileAdapter();</code><br>
 * <code>Db4o.configure().io(new CachedIoAdapter(delegateAdapter));</code><br>
 * @deprecated Use {@link CachingStorage} instead.
 */
public class CachedIoAdapter extends IoAdapter {

	private Page _head;

	private Page _tail;

	private long _position;

	private int _pageSize;

	private int _pageCount;

	private long _fileLength;

	private long _filePointer;

	private IoAdapter _io;

	private boolean _readOnly;

	private static int DEFAULT_PAGE_SIZE = 1024;

	private static int DEFAULT_PAGE_COUNT = 64;

	// private Hashtable4 _posPageMap = new Hashtable4(PAGE_COUNT);

	/**
	 * Creates an instance of CachedIoAdapter with the default page size and
	 * page count.
	 * 
	 * @param ioAdapter
	 *            delegate IO adapter (RandomAccessFileAdapter by default)
	 */
	public CachedIoAdapter(IoAdapter ioAdapter) {
		this(ioAdapter, DEFAULT_PAGE_SIZE, DEFAULT_PAGE_COUNT);
	}

	/**
	 * Creates an instance of CachedIoAdapter with a custom page size and page
	 * count.<br>
	 * 
	 * @param ioAdapter
	 *            delegate IO adapter (RandomAccessFileAdapter by default)
	 * @param pageSize
	 *            cache page size
	 * @param pageCount
	 *            allocated amount of pages
	 */
	public CachedIoAdapter(IoAdapter ioAdapter, int pageSize, int pageCount) {
		_io = ioAdapter;
		_pageSize = pageSize;
		_pageCount = pageCount;
	}

	/**
	 * Creates an instance of CachedIoAdapter with extended parameters.<br>
	 * 
	 * @param path
	 *            database file path
	 * @param lockFile
	 *            determines if the file should be locked
	 * @param initialLength
	 *            initial file length, new writes will start from this point
     * @param readOnly 
     *            if the file should be used in read-onlyt mode.
	 * @param io
	 *            delegate IO adapter (RandomAccessFileAdapter by default)
	 * @param pageSize
	 *            cache page size
	 * @param pageCount
	 *            allocated amount of pages
	 */
	public CachedIoAdapter(String path, boolean lockFile, long initialLength,
			boolean readOnly, IoAdapter io, int pageSize, int pageCount)
			throws Db4oIOException {
		_readOnly = readOnly;
		_pageSize = pageSize;
		_pageCount = pageCount;

		initCache();
		initIOAdaptor(path, lockFile, initialLength, readOnly, io);

		_position = initialLength;
		_filePointer = initialLength;
		_fileLength = _io.getLength();
	}

	/**
	 * Creates and returns a new CachedIoAdapter <br>
	 * 
	 * @param path
	 *            database file path
	 * @param lockFile
	 *            determines if the file should be locked
	 * @param initialLength
	 *            initial file length, new writes will start from this point
	 */
	public IoAdapter open(String path, boolean lockFile, long initialLength, boolean readOnly)
			throws Db4oIOException {
		return new CachedIoAdapter(path, lockFile, initialLength, readOnly, _io,
				_pageSize, _pageCount);
	}

	/**
	 * Deletes the database file
	 * 
	 * @param path
	 *            file path
	 */
	public void delete(String path) {
		_io.delete(path);
	}

	/**
	 * Checks if the file exists
	 * 
	 * @param path
	 *            file path
	 */
	public boolean exists(String path) {
		return _io.exists(path);
	}

	private void initIOAdaptor(String path, boolean lockFile, long initialLength, boolean readOnly, IoAdapter io)
			throws Db4oIOException {
		_io = io.open(path, lockFile, initialLength, readOnly);
	}

	private void initCache() {
		_head = new Page(_pageSize);
		_head._prev = null;
		Page page = _head;
		Page next = _head;
		for (int i = 0; i < _pageCount - 1; ++i) {
			next = new Page(_pageSize);
			page._next = next;
			next._prev = page;
			page = next;
		}
		_tail = next;
	}

	/**
	 * Reads the file into the buffer using pages from cache. If the next page
	 * is not cached it will be read from the file.
	 * 
	 * @param buffer
	 *            destination buffer
	 * @param length
	 *            how many bytes to read
	 */
	public int read(byte[] buffer, int length) throws Db4oIOException {
		long startAddress = _position;
		int bytesToRead = length;
		int totalRead = 0;
		while (bytesToRead > 0) {
			final Page page = getPage(startAddress, true);
			final int readBytes = page.read(buffer, totalRead, startAddress, bytesToRead);
			movePageToHead(page);
			if (readBytes <= 0) {
				break;
			}
			bytesToRead -= readBytes;
			startAddress += readBytes;
			totalRead += readBytes;
		}
		_position = startAddress;
		return totalRead == 0 ? -1 : totalRead;
	}

	/**
	 * Writes the buffer to cache using pages
	 * 
	 * @param buffer
	 *            source buffer
	 * @param length
	 *            how many bytes to write
	 */
	public void write(byte[] buffer, int length) throws Db4oIOException {
		validateReadOnly();
		long startAddress = _position;
		int bytesToWrite = length;
		int bufferOffset = 0;
		while (bytesToWrite > 0) {
			// page doesn't need to loadFromDisk if the whole page is dirty
			boolean loadFromDisk = (bytesToWrite < _pageSize)
					|| (startAddress % _pageSize != 0);
			
			final Page page = getPage(startAddress, loadFromDisk);
			page.ensureEndAddress(getLength());
			
			final int writtenBytes = page.write(buffer, bufferOffset, startAddress, bytesToWrite);
			flushIfHeaderBlockPage(page);
			
			movePageToHead(page);
			bytesToWrite -= writtenBytes;
			startAddress += writtenBytes;
			bufferOffset += writtenBytes;
		}
		long endAddress = startAddress;
		_position = endAddress;
		_fileLength = Math.max(endAddress, _fileLength);
	}

	private void flushIfHeaderBlockPage(final Page page) {
	    if(containsHeaderBlock(page)) {
	    	flushPage(page);
	    }
    }

	private void validateReadOnly() {
		if(_readOnly) {
			throw new Db4oIOException();
		}
	}

	/**
	 * Flushes cache to a physical storage
	 */
	public void sync() throws Db4oIOException {
		validateReadOnly();
		flushAllPages();
		_io.sync();
	}

	/**
	 * Returns the file length
	 */
	public long getLength() throws Db4oIOException {
		return _fileLength;
	}

	/**
	 * Flushes and closes the file
	 */
	public void close() throws Db4oIOException {
		try {
			flushAllPages();
		}
		finally {
			_io.close();
		}
	}
	
	public IoAdapter delegatedIoAdapter() {
		return _io.delegatedIoAdapter();
	}

	private Page getPage(long startAddress, boolean loadFromDisk)
			throws Db4oIOException {
		Page page = getPageFromCache(startAddress);
		if (page != null) {
			if (containsHeaderBlock(page)) {
				getPageFromDisk(page, startAddress);
			}
			page.ensureEndAddress(_fileLength);
			return page;
		}
		// in case that page is not found in the cache
		page = getFreePageFromCache();
		if (loadFromDisk) {
			getPageFromDisk(page, startAddress);
		} else {
			resetPageAddress(page, startAddress);
		}

		return page;
	}

	private boolean containsHeaderBlock(Page page) {
		return page.startAddress() <= FileHeader1.HEADER_LENGTH;
	}

	private void resetPageAddress(Page page, long startAddress) {
		page.startAddress(startAddress);
		page.endAddress(startAddress + _pageSize);
	}

	private Page getFreePageFromCache() throws Db4oIOException {
		if (!_tail.isFree()) {
			flushPage(_tail);
			// _posPageMap.remove(new Long(tail.startPosition / PAGE_SIZE));
		}
		return _tail;
	}

	private Page getPageFromCache(long pos) throws Db4oIOException {
		Page page = _head;
		while (page != null) {
			if (page.contains(pos)) {
				return page;
			}
			page = page._next;
		}
		return null;
		// Page page = (Page) _posPageMap.get(new Long(pos/PAGE_SIZE));
		// return page;
	}

	private void flushAllPages() throws Db4oIOException {
		Page node = _head;
		while (node != null) {
			flushPage(node);
			node = node._next;
		}
	}

	private void flushPage(Page page) throws Db4oIOException {
		if (!page._dirty) {
			return;
		}
		ioSeek(page.startAddress());
		writePageToDisk(page);
		return;
	}

	private void getPageFromDisk(Page page, long pos) throws Db4oIOException {
		long startAddress = pos - pos % _pageSize;
		page.startAddress(startAddress);
		ioSeek(page._startAddress);
		int count = ioRead(page);
		if (count > 0) {
			page.endAddress(startAddress + count);
		} else {
			page.endAddress(startAddress);
		}
		// _posPageMap.put(new Long(page.startPosition / PAGE_SIZE), page);
	}

	private int ioRead(Page page) throws Db4oIOException {
		int count = _io.read(page._buffer);
		if (count > 0) {
			_filePointer = page._startAddress + count;
		}
		return count;
	}

	private void movePageToHead(Page page) {
		if (page == _head) {
			return;
		}
		if (page == _tail) {
			Page tempTail = _tail._prev;
			tempTail._next = null;
			_tail._next = _head;
			_tail._prev = null;
			_head._prev = page;
			_head = _tail;
			_tail = tempTail;
		} else {
			page._prev._next = page._next;
			page._next._prev = page._prev;
			page._next = _head;
			_head._prev = page;
			page._prev = null;
			_head = page;
		}
	}

	private void writePageToDisk(Page page) throws Db4oIOException {
		validateReadOnly();
	    try{
	        _io.write(page._buffer, page.size());
	        _filePointer = page.endAddress();
	        page._dirty = false;
	    }catch (Db4oIOException e){
	        _readOnly = true;
	        throw e;
	    }
	}

	/**
	 * Moves the pointer to the specified file position
	 * 
	 * @param pos
	 *            position within the file
	 */
	public void seek(long pos) throws Db4oIOException {
		_position = pos;
	}

	private void ioSeek(long pos) throws Db4oIOException {
		if (_filePointer != pos) {
			_io.seek(pos);
			_filePointer = pos;
		}
	}

	private static class Page {

		byte[] _buffer;

		long _startAddress = -1;

		long _endAddress;

		final int _bufferSize;

		boolean _dirty;

		Page _prev;

		Page _next;

		private byte[] zeroBytes;

		public Page(int size) {
			_bufferSize = size;
			_buffer = new byte[_bufferSize];
		}

		/*
		 * This method must be invoked before page.write/read, because seek and
		 * write may write ahead the end of file.
		 */
		void ensureEndAddress(long fileLength) {
			long bufferEndAddress = _startAddress + _bufferSize;
			if (_endAddress < bufferEndAddress && fileLength > _endAddress) {
				long newEndAddress = Math.min(fileLength, bufferEndAddress);
				if (zeroBytes == null) {
					zeroBytes = new byte[_bufferSize];
				}
				System.arraycopy(zeroBytes, 0, _buffer,
						(int) (_endAddress - _startAddress),
						(int) (newEndAddress - _endAddress));
				_endAddress = newEndAddress;
			}
		}

		long endAddress() {
			return _endAddress;
		}

		void startAddress(long address) {
			_startAddress = address;
		}

		long startAddress() {
			return _startAddress;
		}

		void endAddress(long address) {
			_endAddress = address;
		}

		int size() {
			return (int) (_endAddress - _startAddress);
		}

		int read(byte[] out, int outOffset, long startAddress, int length) {
			int bufferOffset = (int) (startAddress - _startAddress);
			int pageAvailbeDataSize = (int) (_endAddress - startAddress);
			int readBytes = Math.min(pageAvailbeDataSize, length);
			if (readBytes <= 0) { // meaning reach EOF
				return -1;
			}
			System.arraycopy(_buffer, bufferOffset, out, outOffset, readBytes);
			return readBytes;
		}

		int write(byte[] data, int dataOffset, long startAddress, int length) {
			int bufferOffset = (int) (startAddress - _startAddress);
			int pageAvailabeBufferSize = _bufferSize - bufferOffset;
			int writtenBytes = Math.min(pageAvailabeBufferSize, length);
			System.arraycopy(data, dataOffset, _buffer, bufferOffset,
					writtenBytes);
			long endAddress = startAddress + writtenBytes;
			if (endAddress > _endAddress) {
				_endAddress = endAddress;
			}
			_dirty = true;
			return writtenBytes;
		}

		boolean contains(long address) {
			return (_startAddress != -1 && address >= _startAddress && address < _startAddress
					+ _bufferSize);
		}

		boolean isFree() {
			return _startAddress == -1;
		}
	}

}
