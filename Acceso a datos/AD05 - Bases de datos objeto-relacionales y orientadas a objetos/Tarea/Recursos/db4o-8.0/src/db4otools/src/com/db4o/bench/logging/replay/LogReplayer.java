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
package com.db4o.bench.logging.replay;

import java.io.*;
import java.util.*;

import com.db4o.bench.logging.*;
import com.db4o.bench.logging.replay.commands.*;
import com.db4o.foundation.*;
import com.db4o.io.IoAdapter;
/**
 * @sharpen.ignore
 */
public class LogReplayer {
	
	private String _logFilePath;
	private IoAdapter _io;
	private Set _commands;
	private Map _counts;
	
	public LogReplayer(String logFilePath, IoAdapter io, Set commands) {
		_logFilePath = logFilePath;
		_io = io;
		_commands = commands;
		_counts = new HashMap();
		Iterator it = commands.iterator();
		while (it.hasNext()) {
			_counts.put(it.next(), new Long(0));
		}
	}
	
	public LogReplayer(String logFilePath, IoAdapter io) {
		this(logFilePath, io, LogConstants.allEntries());
	}
	
	
	public void replayLog() throws IOException {
		playCommandList(readCommandList());
	}
	
	public List4 readCommandList() throws IOException {
		List4 list = null;
		BufferedReader reader = new BufferedReader(new FileReader(_logFilePath));
		String line = null;
		while ( (line = reader.readLine()) != null ) {
			IoCommand ioCommand = readLine(line);
			if(ioCommand != null){
				list = new List4(list, ioCommand);
			}
		}
		reader.close();
		return list;
	}
	
	public void playCommandList(List4 commandList){
		while(commandList != null){
			IoCommand ioCommand = (IoCommand) commandList._element;
			ioCommand.replay(_io);
			commandList = commandList._next;
		}
	}
	
	
	private IoCommand readLine(String line) {
		String commandName;
		if ((commandName = acceptedCommandName(line)) != null) {
			incrementCount(commandName);
			return commandForLine(line);
		}
		return null;
	}

	private String acceptedCommandName(String line) {
		if (line.length() == 0) {
			return null;
		}
		Iterator it = _commands.iterator();
		while (it.hasNext()) {
			String commandName = (String)it.next();
			if ( line.startsWith(commandName) ) {
				return commandName;
			}
		}
		return null;
	}
	
	private IoCommand commandForLine(String line) {
		if (line.startsWith(LogConstants.READ_ENTRY)) {
			int length = parameter(LogConstants.READ_ENTRY,  line);
			return new ReadCommand(length);
		}
		if ( line.startsWith(LogConstants.WRITE_ENTRY) ) {
			int length = parameter(LogConstants.WRITE_ENTRY,  line);
			return new WriteCommand(length);
		}
		if ( line.startsWith(LogConstants.SYNC_ENTRY) ) {
			return new SyncCommand();
		}
		if ( line.startsWith(LogConstants.SEEK_ENTRY) ) {
			int address = parameter(LogConstants.READ_ENTRY,  line);
			return new SeekCommand(address);
		}
		
		return null;
	}

	
	private int parameter(String command, String line){
		return parameter(command.length(),  line);
	}

	private int parameter(int start, String line) {
		return Integer.parseInt(line.substring(start));
	}
	
	private void incrementCount(String key) {
		long count = ((Long)_counts.get(key)).longValue();
		_counts.put(key, new Long(count+1));
	}
	
	public Map operationCounts() {
		return _counts;
	}
}
