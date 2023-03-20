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
package com.db4o.bench.logging.statistics;

import java.io.*;



public class BatchLogStatistics {


	public static void main(String[] args) {
		if ( args.length < 1 ) {
			System.out.println("[BATCH] No path given.");
			throw new RuntimeException("[BATCH] No path given.");
		}		
		new BatchLogStatistics().run(args[0]);
	}

	
	public void run(String logDirectoryPath) {
		File directory = new File(logDirectoryPath);
		if ( directory.isDirectory() ) {
			System.out.println("[BATCH] Creating statistics for logs in " + logDirectoryPath);
			FilenameFilter logFilter = new LogFilter();
			File[] logFiles = directory.listFiles(logFilter);
			
			int i;
			for ( i = 0; i < logFiles.length; i++ ) {
				new LogStatistics().run(logFiles[i].getPath());
			}
			
			System.out.println("[BATCH] Statistics for " + i + " log files have been created!");
		}
		else {
			System.out.println("[BATCH] Given path is no directory!");
			System.out.println("[BATCH] Path: " + logDirectoryPath);
		}
	}
	
}


class LogFilter implements FilenameFilter {

	public boolean accept(File dir, String name) {
        return (name.toLowerCase().endsWith(".log"));
    }
	
}