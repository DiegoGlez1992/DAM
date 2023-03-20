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
package com.db4o.db4ounit.common.acid;

import java.io.*;

import com.db4o.foundation.*;
import com.db4o.foundation.io.*;
import com.db4o.internal.transactionlog.*;


public class CrashSimulatingBatch {
	
	private int _counter;
    
    private Collection4 _writes = new Collection4();
    
    private Collection4 _currentWrite = new Collection4();
    
    public void add(String path, byte[] bytes, long offset, int length){
    	byte[] lockFileBuffer = null;
    	byte[] logFileBuffer = null;
    	if(File4.exists(FileBasedTransactionLogHandler.lockFileName(path))){
    		try {
				lockFileBuffer = readAllBytes(FileBasedTransactionLogHandler.lockFileName(path));
				logFileBuffer =  readAllBytes(FileBasedTransactionLogHandler.logFileName(path));
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
        CrashSimulatingWrite crashSimulatingWrite = new CrashSimulatingWrite(_counter++, bytes, offset, length, lockFileBuffer, logFileBuffer);
        if(CrashSimulatingTestSuite.VERBOSE){
        	System.out.println("Recording " + crashSimulatingWrite);
        }
		_currentWrite.add(crashSimulatingWrite);
    }
    
    private byte[] readAllBytes(String fileName) throws IOException{
    	int length = (int) new File(fileName).length();
    	RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
    	byte[] buffer = new byte[length];
    	raf.read(buffer);
    	raf.close();
    	return buffer;
    }

    public void sync() {
        _writes.add(_currentWrite);
        _currentWrite = new Collection4();
    }

    public int numSyncs() {
    	return _writes.size();
    }
    
    public int writeVersions(String file, boolean writeTrash) throws IOException {
        
        int count = 0;
        int rcount = 0;
        
        String lastFileName = file + "0";
        
        String rightFileName = file + "R" ;
        
        File4.copy(lastFileName, rightFileName);
                
        Iterator4 syncIter = _writes.iterator();
        while(syncIter.moveNext()){
            
            rcount++;
            
            Collection4 writesBetweenSync = (Collection4)syncIter.current();
            
            if(CrashSimulatingTestSuite.VERBOSE){
                System.out.println("Starting to write file " + rightFileName + rcount );
            }
            
            RandomAccessFile rightRaf = new RandomAccessFile(rightFileName, "rw");
            Iterator4 singleForwardIter = writesBetweenSync.iterator();
            while(singleForwardIter.moveNext()){
                CrashSimulatingWrite csw = (CrashSimulatingWrite)singleForwardIter.current();
                csw.write(rightFileName, rightRaf, false);
                
                if(CrashSimulatingTestSuite.VERBOSE){
                    System.out.println(csw);
                }
                
            }
            rightRaf.close();
                        
            Iterator4 singleBackwardIter = writesBetweenSync.iterator();
            while(singleBackwardIter.moveNext()){
                count ++;
                CrashSimulatingWrite csw = (CrashSimulatingWrite)singleBackwardIter.current();
                String currentFileName = file + "W" + count;
                File4.copy(lastFileName, currentFileName);
                
                if(CrashSimulatingTestSuite.VERBOSE){
                    System.out.println("Starting to write file " + currentFileName);
                }
                
                RandomAccessFile raf = new RandomAccessFile(currentFileName, "rw");
                if(CrashSimulatingTestSuite.VERBOSE){
                    System.out.println(csw);
                }
                csw.write(currentFileName, raf, writeTrash);
                raf.close();
                lastFileName = currentFileName;
            }
            File4.copy(rightFileName, rightFileName+rcount);
            lastFileName = rightFileName;
        }
        return count;
    }

}
