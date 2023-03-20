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
import java.text.*;
import java.util.*;

import com.db4o.bench.logging.*;
/**
 * 
 * @sharpen.ignore
 *
 */

public class LogStatistics {

	private String _logFilePath;
	private String _logFileName;
	private String _statisticsFilePath;
	private PrintStream _out;
	private BufferedReader _in;
	
	private long _readCount = 0;
	private long _readBytes = 0;
	private long _writeCount = 0;
	private long _writeBytes = 0;
	private long _syncCount = 0;
	private long _seekCount = 0;
	
	
	
	public static void main(String[] args) {
		if ( args.length < 1 ) {
			System.out.println("Usage: LogStatistics <log file path> [<statistics file path>]");
			throw new RuntimeException("Usage: LogStatistics <log file path> [<statistics file path>]");
		}
		
		if ( args.length > 1 ) {
			new LogStatistics().run(args[0], args[1]);
		}
		else {
			new LogStatistics().run(args[0]);
		}
	}
	
	
	public void run(String logFilePath, String statisticsFilePath) {
		_logFilePath = logFilePath;
		_statisticsFilePath = statisticsFilePath;
		
		try {
			openFiles();			
			_logFileName = new File(_logFilePath).getName();
			System.out.print("  Creating statistics for " + _logFileName + "  ...   ");
			
			long start = System.currentTimeMillis();
			createStatistics();
			long elapsed = System.currentTimeMillis() - start;
			
			String elapsedString = formatTime(elapsed);
			System.out.println("Finished! Time taken: " + elapsedString);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		closeFiles();
	}


	public void run(String logFilepath) {
		run(logFilepath, logFilepath+"-stat.htm");
	}


	private void createStatistics() {
		String line;
		try {
			while ( (line = _in.readLine()) != null ) {
				handleLine(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}		
		outputStatistics();
	}

	

	private void outputStatistics() {
		DecimalFormat formatPercentage = new DecimalFormat("##.##");
		DecimalFormat formatCount = new DecimalFormat("###,###");
		
		long totalCount = _readCount + _writeCount + _syncCount + _seekCount;
		String totalCountString = formatCount.format(totalCount);
		
		double readCountPercentage = countPercentage(_readCount, totalCount);
		String readCountPercentageString = formatPercentage.format(readCountPercentage);
		
		double writeCountPercentage = countPercentage(_writeCount, totalCount);
		String writeCountPercentageString = formatPercentage.format(writeCountPercentage);
		
		double syncCountPercentage = countPercentage(_syncCount, totalCount);
		String syncCountPercentageString = formatPercentage.format(syncCountPercentage);
		
		double seekCountPercentage = countPercentage(_seekCount, totalCount);
		String seekCountPercentageString = formatPercentage.format(seekCountPercentage);
		
		long totalBytes = _readBytes + _writeBytes;
		String totalBytesString = formatCount.format(totalBytes);
		
		double readBytesPercentage = countPercentage(_readBytes, totalBytes);
		String readBytesPercentageString = formatPercentage.format(readBytesPercentage);
		
		double writeBytesPercentage = countPercentage(_writeBytes, totalBytes);
		String writeBytesPercentageString = formatPercentage.format(writeBytesPercentage);
		
		String readCountString = formatCount.format(_readCount);
		String writeCountString = formatCount.format(_writeCount);
		String syncCountString = formatCount.format(_syncCount);
		String seekCountString = formatCount.format(_seekCount);
		
		String readBytesString = formatCount.format(_readBytes);
		String writeBytesString = formatCount.format(_writeBytes);
		
		printHeader();
		_out.println("<tr><td>Reads</td><td></td><td align=\"right\">" + readCountString + "</td><td></td><td align=\"right\">" + readCountPercentageString + "</td><td></td><td align=\"right\">" + readBytesString + "</td><td></td><td align=\"right\">" + readBytesPercentageString + "</td></tr>");
		_out.println("<tr><td>Writes</td><td></td><td align=\"right\">" + writeCountString + "</td><td></td><td align=\"right\">" + writeCountPercentageString + "</td><td></td><td align=\"right\">" + writeBytesString + "</td><td></td><td align=\"right\">" + writeBytesPercentageString + "</td></tr>");
		_out.println("<tr><td>Seeks</td><td></td><td align=\"right\">" + seekCountString + "</td><td></td><td align=\"right\">" + seekCountPercentageString + "</td><td></td><td></td></tr>");
		_out.println("<tr><td>Syncs</td><td></td><td align=\"right\">" + syncCountString + "</td><td></td><td align=\"right\">" + syncCountPercentageString + "</td><td></td><td></td></tr>");
		_out.println("<tr><td colspan=\"9\"></td></tr>");
		_out.println("<tr><td>Total</td><td></td><td align=\"right\">" + totalCountString + "</td><td></td><td></td><td></td><td>" + totalBytesString + "</td><td></td></tr>");
		_out.println("</table>");
		
		double avgBytesPerRead = _readBytes / _readCount;
		String avgBytesPerReadString = formatCount.format(avgBytesPerRead);
		double avgBytesPerWrite = _writeBytes / _writeCount;
		String avgBytesPerWriteString = formatCount.format(avgBytesPerWrite);
		
		_out.println("<p>");
		_out.println("Average byte count per read: " + avgBytesPerReadString);
		_out.println("<br>");
		_out.println("Average byte count per write: " + avgBytesPerWriteString);
		_out.println("</p>");
		
		printFooter();
	}


	private void printHeader() {
		_out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		_out.println("<html>");
		_out.println("<head>");
		_out.println("<title>Log Statistics - " + _logFileName + "</title>");
		_out.println("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\">");
		_out.println("</head>");
		_out.println("<body>");
		_out.println("<p>Statistics for logfile '" + _logFilePath + "'</p>");
		_out.println("<table border=\"0\" cellpadding=\"4\">");
		_out.println("<tr><th></th><th></th><th>Count</th><th></th><th>%</th><th></th><th>Bytes</th><th></th><th>%</th></tr>");
	}
	
	private void printFooter() {
		_out.println("</body>");
		_out.println("</html>");
	}

	private double countPercentage(long count, long totalCount) {
		return 100 * (double)count / (double)totalCount;
	}

	private void handleLine(String line) {
		if ( line.startsWith(LogConstants.WRITE_ENTRY) ) {
			handleWrite(line);
		}
		else if ( line.startsWith(LogConstants.READ_ENTRY) ) {
			handleRead(line);
		}
		else if ( line.startsWith(LogConstants.SYNC_ENTRY) ) {
			handleSync();
		}
		else if ( line.startsWith(LogConstants.SEEK_ENTRY)  ) {
			handleSeek();
		}
		else {
			throw new IllegalArgumentException("Unknown command in log: " + line);
		}
	}


	private void handleSeek() {
		_seekCount++;
	}

	private void handleSync() {
		_syncCount++;
	}

	private void handleRead(String line) {
		_readCount++;
		_readBytes += bytesForLine(line, LogConstants.READ_ENTRY.length());
	}

	private void handleWrite(String line) {
		_writeCount++;
		_writeBytes += bytesForLine(line, LogConstants.WRITE_ENTRY.length());
	}

	private long bytesForLine(String line, int commandLength) {
		return Long.parseLong(line.substring(commandLength));
	}
	
	private void closeFiles() {
		closeStatisticsFile();
		closeLogFile();
	}

	private void openFiles() throws FileNotFoundException {
		openStatisticsFile();
		openLogFile();
	}
	
	private void closeLogFile() {
		try {
			_in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void closeStatisticsFile() {
		_out.flush();
		_out.close();
	}

	private void openLogFile() throws FileNotFoundException {
		_in = new BufferedReader(new FileReader(_logFilePath));
	}

	private void openStatisticsFile() throws FileNotFoundException {
		_out = new PrintStream(new FileOutputStream(_statisticsFilePath));
	}

	private String formatTime(long millis) {
		Date date = new Date(millis);
		SimpleDateFormat sdf = new SimpleDateFormat("mm'min' ss'sec' S'millisec'");
		String elapsedString = sdf.format(date);
		return elapsedString;
	}
	
}
