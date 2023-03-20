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
import com.db4o.ext.*;

/**
 * @exclude
 */
public class Exceptions4 {

    public static final void throwRuntimeException (int code) {
        throwRuntimeException(code, null, null);
    }

    public static final void throwRuntimeException (int code, Throwable cause) {
    	throwRuntimeException(code, null, cause);
    }

    public static final void throwRuntimeException (int code, String msg) {
        throwRuntimeException(code, msg, null);
    }

    public static final void throwRuntimeException (int code, String msg, Throwable cause) {
    	throwRuntimeException(code, msg, cause, true);
    }

    /**
     * @deprecated
     */
    public static final void throwRuntimeException (int code, String msg, Throwable cause,boolean doLog) {
    	if(doLog) {
    		Messages.logErr(Db4o.configure(), code,msg, cause);
    	}
        throw new Db4oException(Messages.get(code, msg));
    }
    
    public static final void catchAllExceptDb4oException(Throwable exc) throws Db4oException {
    	if(exc instanceof Db4oException) {
    		throw (Db4oException)exc;
    	}
    }
    
    public static RuntimeException shouldNeverBeCalled(){
        throw new RuntimeException();
     }

    public static void shouldNeverHappen(){
        throw new Error();
     }

    public static RuntimeException virtualException(){
        throw new RuntimeException();
    }	
}
