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

import com.db4o.internal.*;

public class ClientTransactionHandle {
	
    private final ClientTransactionPool _transactionPool;
    private Transaction _mainTransaction;
    private Transaction _transaction;
    private boolean _rollbackOnClose;
    
    public ClientTransactionHandle(ClientTransactionPool transactionPool) {
		_transactionPool = transactionPool;
        _mainTransaction = _transactionPool.acquireMain();
		_rollbackOnClose = true;
	}

    public void acquireTransactionForFile(String fileName) {
        _transaction = _transactionPool.acquire(fileName);
	}
	
    public void releaseTransaction(ShutdownMode mode) {
		if (_transaction != null) {
			_transactionPool.release(mode, _transaction, _rollbackOnClose);
			_transaction = null;
		}
	}
	
    public boolean isClosed() {
		return _transactionPool.isClosed();
	}
    
    public void close(ShutdownMode mode) {
		if ((!_transactionPool.isClosed()) && (_mainTransaction != null)) {
			_transactionPool.release(mode, _mainTransaction, _rollbackOnClose);
        }
	}
	
    public Transaction transaction() {
        if (_transaction != null) {
            return _transaction;
        }
        return _mainTransaction;
    }

    public void transaction(Transaction transaction) {
		if (_transaction != null) {
			_transaction = transaction;
		} else {
			_mainTransaction = transaction;
		}
		_rollbackOnClose = false;
    }

}
