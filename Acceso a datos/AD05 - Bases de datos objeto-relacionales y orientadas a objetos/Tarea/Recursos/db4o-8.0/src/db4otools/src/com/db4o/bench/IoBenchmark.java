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
package com.db4o.bench;

import java.io.*;
import java.util.*;

import com.db4o.bench.crud.*;
import com.db4o.bench.delaying.*;
import com.db4o.bench.logging.*;
import com.db4o.bench.logging.replay.*;
import com.db4o.bench.util.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.io.*;


/** 
 * IoBenchmark is a benchmark that measures I/O performance as seen by db4o.
 * The benchmark can also run in delayed mode which allows simulating the I/O behaviour of a slower machine
 * on a faster one.
 * 
 * For further information and usage instructions please refer to README.htm.
 * @sharpen.ignore
 */

public class IoBenchmark {
	
	private static final String _doubleLine = "=============================================================";

	private static final String _singleLine = "-------------------------------------------------------------";
	
	private static final String _dbFileName = "IoBenchmark.db4o";
	
	private Delays _delays = null;
	
	
	public static void main(String[] args) throws IOException {
		IoBenchmarkArgumentParser argumentParser = new IoBenchmarkArgumentParser(args);		
		printBenchmarkHeader();
		IoBenchmark ioBenchmark = new IoBenchmark();
		if (argumentParser.delayed()) {
			ioBenchmark.processResultsFiles(argumentParser.resultsFile1(), argumentParser.resultsFile2());
		}
		ioBenchmark.run(argumentParser);
	}

	
	private void run(IoBenchmarkArgumentParser argumentParser) throws IOException {
		runTargetApplication(argumentParser.objectCount());
		prepareDbFile(argumentParser.objectCount());
		runBenchmark(argumentParser.objectCount());
	}


	
	private void runTargetApplication(int itemCount) {
		sysout("Running target application ...");
		new CrudApplication().run(itemCount);
	}


	private void prepareDbFile(int itemCount) {
		sysout("Preparing DB file ...");
		deleteFile(_dbFileName);
		IoAdapter rafFactory = new RandomAccessFileAdapter();
		IoAdapter raf = rafFactory.open(_dbFileName, false, 0, false);
		LogReplayer replayer = new LogReplayer(CrudApplication.logFileName(itemCount), raf);
		try {
			replayer.replayLog();
		} catch (IOException e) {
			exitWithError("Error reading I/O operations log file");
		} finally {
			raf.close();
		}
	}


	private void runBenchmark(int itemCount) throws IOException {
		sysout("Running benchmark ...");
		deleteBenchmarkResultsFile(itemCount);
		PrintStream out = new PrintStream(new FileOutputStream(resultsFileName(itemCount), true));
		printRunHeader(itemCount, out);
		for (int i = 0; i < LogConstants.ALL_CONSTANTS.length; i++) {
			String currentCommand = LogConstants.ALL_CONSTANTS[i];
			benchmarkCommand(currentCommand, itemCount, _dbFileName, out);	
		}
		deleteFile(_dbFileName);
		deleteCrudLogFile(itemCount);
	}

		
	private void benchmarkCommand(String command, int itemCount, String dbFileName, PrintStream out) throws IOException {
		HashSet commands = commandSet(command);
		IoAdapter io = ioAdapter(dbFileName);
		LogReplayer replayer = new LogReplayer(CrudApplication.logFileName(itemCount), io, commands);
		List4 commandList = replayer.readCommandList();
		
		StopWatch watch = new StopWatch();
		watch.start();
		replayer.playCommandList(commandList);		
		watch.stop();
		io.close();
		
		long timeElapsed = watch.elapsed();
		long operationCount = ((Long)replayer.operationCounts().get(command)).longValue();
		printStatisticsForCommand(out, command, timeElapsed, operationCount);
	}


	private IoAdapter ioAdapter(String dbFileName) throws NumberFormatException, IOException, Db4oIOException {
		if (delayed()) {
			return delayingIoAdapter(dbFileName);
		}
		
		IoAdapter rafFactory = new RandomAccessFileAdapter();
		return rafFactory.open(dbFileName, false, 0, false);
	}
	
	
	private IoAdapter delayingIoAdapter(String dbFileName) throws NumberFormatException{
		IoAdapter rafFactory = new RandomAccessFileAdapter();
		IoAdapter delFactory = new DelayingIoAdapter(rafFactory, _delays);
		return delFactory.open(dbFileName, false, 0, false);
	}


	private void processResultsFiles(String resultsFile1, String resultsFile2) throws NumberFormatException {
		sysout("Delaying:");
		try {
			DelayCalculation calculation = new DelayCalculation(resultsFile1, resultsFile2);
			calculation.validateData();
			if (!calculation.isValidData()) {
				exitWithError("> Result files are invalid for delaying!");
			}
			_delays = calculation.calculatedDelays();
			sysout("> Required delays:");
			sysout("> " + _delays);
            sysout("> Adjusting delay timer to match required delays...");
			calculation.adjustDelays(_delays);
			sysout("> Adjusted delays:");
			sysout("> " + _delays);
		} catch (IOException e) {
			exitWithError("> Could not open results file(s)!\n" +
						"> Please check the file name settings in IoBenchmark.properties.");
		}
	}


	private void exitWithError(String error) {
		System.err.println(error + "\n Aborting execution!");
		throw new RuntimeException(error + "\n Aborting execution!");
	}
	
	private String resultsFileName(int itemCount){
		String fileName =  "db4o-IoBenchmark-results-" + itemCount;
		if (delayed()) {
			fileName += "-delayed";
		}
		fileName += ".log";
		return fileName;
	}

	private boolean delayed() {
		return _delays != null;
	}
	
	private HashSet commandSet(String command) {
		HashSet commands = new HashSet();
		commands.add(command);
		return commands;
	}
	
	private void deleteBenchmarkResultsFile(int itemCount) {
		deleteFile(resultsFileName(itemCount));
	}
	
	private void deleteCrudLogFile(int itemCount) {
		deleteFile(CrudApplication.logFileName(itemCount));
	}

	private void deleteFile(String fileName) {
		new File(fileName).delete();
	}
	
	private static void printBenchmarkHeader() {
		printDoubleLine();
		sysout("Running db4o IoBenchmark");
		printDoubleLine();
	}
	
	private void printRunHeader(int itemCount, PrintStream out) {
		output(out, _singleLine);
		output(out, "db4o IoBenchmark results with " + itemCount + " items");
		sysout("Statistics written to " + resultsFileName(itemCount));
		output(out, _singleLine);
		output(out, "");
	}
	
	private void printStatisticsForCommand(PrintStream out, String currentCommand, long timeElapsed, long operationCount) {
		double avgTimePerOp = (double)timeElapsed/(double)operationCount;
		double opsPerMs = (double)operationCount/(double)timeElapsed;
		double nanosPerMilli = Math.pow(10, 6);
		
		String output = "Results for " + currentCommand + "\r\n" +
						"> operations executed: " + operationCount + "\r\n" +
						"> time elapsed: " + timeElapsed + " ms\r\n" + 
						"> operations per millisecond: " + opsPerMs + "\r\n" +
						"> average duration per operation: " + avgTimePerOp + " ms\r\n" +
						currentCommand + (int)(avgTimePerOp*nanosPerMilli) + " ns\r\n";
		
		output(out, output);
		sysout(" ");
	}

	private void output(PrintStream out, String text) {
		out.println(text);
		sysout(text);
	}
	
	
	private static void printDoubleLine() {
		sysout(_doubleLine);
	}
	
	private static void sysout(String text) {
		System.out.println(text);
	}
}
