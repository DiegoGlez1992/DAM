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
package com.db4o.foundation;

import java.io.*;

/**
 * @exclude
 *
 * Just delegates to the platform chaining mechanism.
 * 
 * @sharpen.ignore
 * @sharpen.rename System.Exception
 */
public abstract class ChainedRuntimeException extends RuntimeException {

    public ChainedRuntimeException() {
    }

    @decaf.ReplaceFirst("super(msg);")
    public ChainedRuntimeException(String msg) {
        super(msg, null);
    }

    @decaf.ReplaceFirst("super(msg);")
    public ChainedRuntimeException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
    /**
     * @exclude
     * 
     * Provides jdk11 compatible exception chaining. decaf will mix this class
     * into {@link ChainedRuntimeException}.
     **/
    @decaf.Mixin
    public static class ChainedRuntimeExceptionMixin {

        public ChainedRuntimeException _subject;
        public Throwable _cause;

        public ChainedRuntimeExceptionMixin(ChainedRuntimeException mixee) {
            _subject = mixee;
            _cause = null;
        }
        
        public ChainedRuntimeExceptionMixin(ChainedRuntimeException mixee, String msg) {
            _subject = mixee;
            _cause = null;
        }
        
        public ChainedRuntimeExceptionMixin(ChainedRuntimeException mixee, String msg, Throwable cause) {
            _subject = mixee;
            _cause = cause;
        }

        /**
        * @return The originating exception, if any
        */
        public final Throwable getCause() {
            return _cause;
        }

        public void printStackTrace() {
            printStackTrace(System.err);
        }

        public void printStackTrace(PrintStream ps) {
            printStackTrace(new PrintWriter(ps, true));
        }

        public void printStackTrace(PrintWriter pw) {
            _subject.superPrintStackTrace(pw);
            if (_cause != null) {
                pw.println("Nested cause:");
                _cause.printStackTrace(pw);
            }
        }
    }

	private void superPrintStackTrace(PrintWriter pw) {
		super.printStackTrace(pw);
	}
}

