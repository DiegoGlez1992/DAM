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

import com.db4o.*;
import com.db4o.internal.handlers.*;


final class MessageOutput
{
	final PrintStream stream;

	MessageOutput(ObjectContainerBase a_stream, String msg){
		stream = a_stream.configImpl().outStream();
		print(msg, true);
	}

	MessageOutput(String a_StringParam, int a_intParam, PrintStream a_stream, boolean header){
		stream = a_stream;
		print(Messages.get(a_intParam,a_StringParam), header );
	}

	MessageOutput(String a_StringParam, int a_intParam, PrintStream a_stream){
		this(a_StringParam, a_intParam , a_stream, true);
	}


	private void print(String msg, boolean header){
		if(stream != null){
			if(header){
				stream.println("[" + Db4o.version() + "   " + DateHandlerBase.now() + "] ");
			}
			stream.println(" " + msg);
		}
	}
}
