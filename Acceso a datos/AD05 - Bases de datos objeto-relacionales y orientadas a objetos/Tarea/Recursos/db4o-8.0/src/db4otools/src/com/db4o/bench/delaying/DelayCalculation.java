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
package com.db4o.bench.delaying;

import java.io.*;

import com.db4o.bench.logging.*;
import com.db4o.bench.timing.*;
/**
 * 
 * @sharpen.ignore
 *
 */

public class DelayCalculation {

	private static final int ADJUSTMENT_ITERATIONS = 10000;
	
	private MachineCharacteristics _machine1;
	private MachineCharacteristics _machine2;
	private MachineCharacteristics _fasterMachine = null;
	private MachineCharacteristics _slowerMachine = null;
	
	public DelayCalculation(String logFileName1, String logFileName2) throws NumberFormatException, IOException {
		_machine1 = new MachineCharacteristics(logFileName1);
		_machine2 = new MachineCharacteristics(logFileName2);
	}
	
	public void validateData() {
		if (_machine1.isFasterThan(_machine2)) {
			_fasterMachine = _machine1;
			_slowerMachine = _machine2;
			System.out.println("> machine1 ("+ _machine1.logFileName() +") is faster!");
		}
		else if (_machine2.isFasterThan(_machine1)) {
			_fasterMachine = _machine2;
			_slowerMachine = _machine1;
			System.out.println("> machine2 ("+ _machine2.logFileName() +") is faster!");
		}
	}
	
	public boolean isValidData() {
		return ((_fasterMachine != null) && (_slowerMachine != null));
	}
	

	public Delays calculatedDelays() {
		long[] tempDelays = new long[Delays.COUNT];
		for (int i = 0; i < Delays.COUNT; i++) {
			tempDelays[i] = _slowerMachine.times.values[i] - _fasterMachine.times.values[i];
		}	
		return new Delays(tempDelays[Delays.READ], tempDelays[Delays.WRITE], tempDelays[Delays.SEEK], tempDelays[Delays.SYNC]);
	}
	
	public void adjustDelays(Delays delays) {
		for(int i = 0; i < Delays.COUNT; i++) {
			adjustDelay(delays, i);
		}
	}
	
	private void adjustDelay(Delays delays, int index) {
		NanoStopWatch watch = new NanoStopWatch();
		NanoTiming timing = new NanoTiming();
		long difference, differencePerIteration;
		long average = 0, oldAverage = 0;
		long delay = delays.values[index];
		long adjustedDelay = delay;
		int adjustmentRuns = 1;
		long targetRuntime = ADJUSTMENT_ITERATIONS*delay;
        long minimumDelay = minimumDelay();
        warmUpIterations(delay, timing);	
        
        do {
        	watch.start();
        	for (int i = 0; i < ADJUSTMENT_ITERATIONS; i++) {
        		timing.waitNano(adjustedDelay);
        	}
        	watch.stop();
        	
        	difference = targetRuntime - watch.elapsed();
        	differencePerIteration = difference/ADJUSTMENT_ITERATIONS;
        	if (-differencePerIteration > adjustedDelay) {
        		adjustedDelay /= 2;				
        	} 
        	else {
        		adjustedDelay += differencePerIteration;
        		oldAverage = average;
        		if (adjustmentRuns == 1) {
        			average = adjustedDelay;
        		}
        		else {
        			average = ((average*(adjustmentRuns-1)) / adjustmentRuns) + (adjustedDelay / adjustmentRuns);
        		}
        		adjustmentRuns++;
        	}
        	if(adjustedDelay <= 0){
        	    break;
        	}
        	if( (Math.abs(average - oldAverage) < (0.01*delay)) && adjustmentRuns > 10){
        	    break;
        	}
        } while (true);
        if (average < minimumDelay) {
            System.err.println(">> Smallest achievable delay: " + minimumDelay);
            System.err.println(">> Required delay setting: " + average);
            System.err.println(">> Using delay(0) to wait as short as possible.");
            System.err.println(">> Results will not be accurate.");
            average = 0;
        }
        delays.values[index] = average;
	}

	private void warmUpIterations(long delay, NanoTiming timing) {
		for (int i = 0; i < ADJUSTMENT_ITERATIONS; i++) {
			timing.waitNano(delay);
		}
	}
	
	private long minimumDelay() {
		NanoStopWatch watch = new NanoStopWatch();
		NanoTiming timing = new NanoTiming();
		watch.start();
		for (int i = 0; i < ADJUSTMENT_ITERATIONS; i++) {
			timing.waitNano(0);
		}
		watch.stop();
		return watch.elapsed()/ADJUSTMENT_ITERATIONS;
	}
}


class MachineCharacteristics {

	private String _logFileName;
	public Delays times;

	public MachineCharacteristics(String logFileName) throws NumberFormatException, IOException {
		_logFileName = logFileName;
		parseLog();
	}

	private void parseLog() throws NumberFormatException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(_logFileName));
		long readTime = 0, writeTime = 0, seekTime = 0, syncTime = 0;
		String line = null;
		while ( (line = reader.readLine()) != null ) {
			if (line.startsWith(LogConstants.READ_ENTRY)) {
				readTime = extractNumber(line);
			}
			else if (line.startsWith(LogConstants.WRITE_ENTRY)) {
				writeTime = extractNumber(line);
			}
			else if (line.startsWith(LogConstants.SEEK_ENTRY)) {
				seekTime = extractNumber(line);
			}
			else if (line.startsWith(LogConstants.SYNC_ENTRY)) {
				syncTime = extractNumber(line);
			}
		}
		reader.close();
		times = new Delays(readTime, writeTime, seekTime, syncTime);
	}

	private long extractNumber(String line) {
		return Long.parseLong(extractNumberString(line));
	}
	
	private String extractNumberString(String line) {
		int start = line.indexOf(' ') + 1;
		int end = line.indexOf(' ', start);
		return line.substring(start, end);
	}
	
	public boolean isFasterThan(MachineCharacteristics otherMachine) {
		boolean result = true;
		for (int i = 0; i < Delays.COUNT; i++) {
			result = result && (times.values[i] <= otherMachine.times.values[i]);
		}
		return result;
	}
	
	public String logFileName() {
		return _logFileName;
	}
	

}