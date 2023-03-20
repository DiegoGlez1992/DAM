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
import com.db4o.foundation.*;
import com.db4o.internal.caching.*;

/**
 * @exclude
 */
class CachingBin extends BinDecorator {

	private final int _pageSize;
	
	private final Cache4<Long, Page> _cache;

	private final ObjectPool<Page> _pagePool;
	
	private long _fileLength;
	
	private Procedure4<Page> _onDiscardPage = new Procedure4<Page>() {
    	public void apply(Page discardedPage) {
    		flushPage(discardedPage);
    		_pagePool.returnObject(discardedPage);
        }
    };
    
	public CachingBin(Bin bin, Cache4 cache, int pageCount, int pageSize) throws Db4oIOException {
		super(bin);
		_pageSize = pageSize;
		_pagePool = new SimpleObjectPool<Page>(newPagePool(pageCount));
		_cache = cache;
		_fileLength = _bin.length();
	}

	private Page[] newPagePool(int pageCount) {
	    final Page[] pages = new Page[pageCount];
		for (int i=0; i<pages.length; ++i) {
			pages[i] = new Page(_pageSize);
		}
	    return pages;
    }
	
	/**
	 * Reads the file into the buffer using pages from cache. If the next page
	 * is not cached it will be read from the file.
	 * 
	 * @param pos 
	 * 			  start position to read
	 * @param buffer
	 *            destination buffer
	 * @param length
	 *            how many bytes to read
	 */
	public int read(final long pos, byte[] buffer, int length) throws Db4oIOException {
		return readInternal(pos, buffer, length, false);
	}

	private int readInternal(final long pos, byte[] buffer, int length, boolean syncRead) {
		long startAddress = pos;
		int bytesToRead = length;
		int totalRead = 0;
		while (bytesToRead > 0) {
			final Page page = syncRead ? 
					syncReadPage(startAddress) : 
					getPage(startAddress, _producerFromDisk);
			final int readBytes = page.read(buffer, totalRead, startAddress, bytesToRead);
			if (readBytes <= 0) {
				break;
			}
			bytesToRead -= readBytes;
			startAddress += readBytes;
			totalRead += readBytes;
		}
		return totalRead == 0 ? -1 : totalRead;
	}

	/**
	 * Writes the buffer to cache using pages
	 * 
	 * @param pos
	 *            start position to write    
	 * @param buffer
	 *            source buffer
	 * @param length
	 *            how many bytes to write
	 */
	public void write(final long pos, byte[] buffer, int length) throws Db4oIOException {
		long startAddress = pos;
		int bytesToWrite = length;
		int bufferOffset = 0;
		while (bytesToWrite > 0) {
			// page doesn't need to loadFromDisk if the whole page is dirty
			boolean loadFromDisk = (bytesToWrite < _pageSize) || (startAddress % _pageSize != 0);

			final Page page = getPage(startAddress, loadFromDisk);

			final int writtenBytes = page.write(buffer, bufferOffset, startAddress, bytesToWrite);

			bytesToWrite -= writtenBytes;
			startAddress += writtenBytes;
			bufferOffset += writtenBytes;
		}
		long endAddress = startAddress;
		_fileLength = Math.max(endAddress, _fileLength);
	}

	/**
	 * Flushes cache to a physical storage
	 */
	public void sync() throws Db4oIOException {
		flushAllPages();
		super.sync();
	}
	
	@Override
	public void sync(final Runnable runnable) {
		flushAllPages();
		super.sync(new Runnable() {
			public void run() {
				runnable.run();
				flushAllPages();
			}
		});
	}
	
	@Override
	public int syncRead(long position, byte[] bytes, int bytesToRead) {
		return readInternal(position, bytes, bytesToRead, true);
	}

	/**
	 * Returns the file length
	 */
	public long length() throws Db4oIOException {
		return _fileLength;
	}

	final Function4<Long, Page> _producerFromDisk = new Function4<Long, Page>() {
		public Page apply(Long pageAddress) {
			// in case that page is not found in the cache
			final Page newPage = _pagePool.borrowObject();
			loadPage(newPage, pageAddress.longValue());
			return newPage;
		}
	};
	
	final Function4<Long, Page> _producerFromPool = new Function4<Long, Page>() {
		public Page apply(Long pageAddress) {
			// in case that page is not found in the cache
			final Page newPage = _pagePool.borrowObject();
			resetPageAddress(newPage, pageAddress.longValue());
			return newPage;
		}
	};

	private Page getPage(final long startAddress, final boolean loadFromDisk) throws Db4oIOException {
		final Function4<Long, Page> producer = loadFromDisk ? _producerFromDisk : _producerFromPool;
		return getPage(startAddress, producer);
	}

	private Page getPage(final long startAddress, final Function4<Long, Page> producer) {
	    Page page = _cache.produce(pageAddressFor(startAddress), producer, _onDiscardPage);
	    page.ensureEndAddress(_fileLength);
		return page;
    }
	
	private Page syncReadPage(final long startAddress) {
	    Page page = new Page(_pageSize);
		loadPage(page, startAddress);
		page.ensureEndAddress(_fileLength);
	    return page;
    }

	private Long pageAddressFor(long startAddress) {
		return (startAddress / _pageSize) * _pageSize;
    }

	private void resetPageAddress(Page page, long startAddress) {
		page._startAddress = startAddress;
		page._endAddress = startAddress + _pageSize;
	}

	protected void flushAllPages() throws Db4oIOException {
		 for (Page p : _cache) {
			 flushPage(p);
		 }
	}

	private void flushPage(Page page) throws Db4oIOException {
		if (!page._dirty) {
			return;
		}
		writePageToDisk(page);
	}

	private void loadPage(Page page, long pos) throws Db4oIOException {
		long startAddress = pos - pos % _pageSize;
		page._startAddress = startAddress;
		int count = _bin.read(page._startAddress, page._buffer, page._bufferSize);
		if (count > 0) {
			page._endAddress = startAddress + count;
		} else {
			page._endAddress = startAddress;
		}
	}
	
	private void writePageToDisk(Page page) throws Db4oIOException {
		super.write(page._startAddress, page._buffer, page.size());
		page._dirty = false;
	}
	
	private static class Page {

		public final byte[] _buffer;

		public long _startAddress = -1;

		public long _endAddress;

		public final int _bufferSize;

		public boolean _dirty;

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
				System.arraycopy(zeroBytes, 0, _buffer, (int) (_endAddress - _startAddress),
				        (int) (newEndAddress - _endAddress));
				_endAddress = newEndAddress;
			}
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
			System.arraycopy(data, dataOffset, _buffer, bufferOffset, writtenBytes);
			long endAddress = startAddress + writtenBytes;
			if (endAddress > _endAddress) {
				_endAddress = endAddress;
			}
			_dirty = true;
			return writtenBytes;
		}
	}
}
