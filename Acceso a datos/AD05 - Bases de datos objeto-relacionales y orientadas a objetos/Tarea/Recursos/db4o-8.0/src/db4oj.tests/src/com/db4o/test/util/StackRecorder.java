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
package com.db4o.test.util;

import com.db4o.foundation.*;

/**
 * simple annotated stack trace for debugging
 * 
 * @exclude
 */
public class StackRecorder {
    
	// exclude addCase()/record() + newTrace()
    
	private static final int EXCLUDEDEPTH = 2;
    
	private static StackTrace _curTrace;
    
	private static Collection4 _traces=new Collection4();
    
    private static Collection4 _includes;
    
    private static Collection4 _excludes;
    
	
	public static void addCase(String caseInfo) {
		_curTrace=newTrace(caseInfo);
	}
    
    public static void addInclude(String classNameSubstring){
        if(_includes == null){
            _includes = new Collection4();
        }
        _includes.add(classNameSubstring);
    }
    
    public static void addExclude(String classNameSubstring){
        if(_excludes == null){
            _excludes = new Collection4();
        }
        _excludes.add(classNameSubstring);
    }
    

	public static void record() {
		StackTrace trace=newTrace(null);
		if(!_traces.contains(trace)) {
			_traces.add(trace);
		}
	}
    
    public static void record(Object obj){
        if(obj == null){
            return;
        }
        Class clazz = (obj instanceof Class) ? (Class)obj : obj.getClass();
        if(recordClass(clazz)){
            record();
        }
    }
    
    private static boolean recordClass(Class clazz){
        
        String className = clazz.getName();
        
        if(_excludes != null){
            Iterator4 i = _excludes.iterator();
            while(i.moveNext()){
                String name = (String)i.current();
                if(className.indexOf(name) >= 0){
                    return false;
                }
            }
        }
        
        boolean onNotFound = true;
        
        if(_includes != null){
            onNotFound = false;
            Iterator4 i = _includes.iterator();
            while(i.moveNext()){
                String name = (String)i.current();
                if(className.indexOf(name) >= 0){
                    onNotFound = true;
                }
            }
        }
        
        
        Class claxx = clazz.getSuperclass();
        if(claxx != null){
            if ( recordClass(claxx)){
                return true;
            }
        }
        
        return onNotFound;
    }
	
	public static void logAll() {
		Iterator4 iter=_traces.iterator();
		while(iter.moveNext()) {
			System.out.println(iter.current());
			if(iter.moveNext()) {
				System.out.println("\n---\n");
			}
		}
	}

	private static StackTrace newTrace(String caseInfo) {
		return new StackTrace(EXCLUDEDEPTH,caseInfo,_curTrace);
	}
	
	public static void main(String[] args) {
		for(int i=0;i<2;i++) {
			for(int j=0;j<2;j++) {
				StackRecorder.addCase("main"+i);
				foo();
			}
		}
		for(int i=0;i<2;i++) {
			for(int j=0;j<2;j++) {
				StackRecorder.addCase("mainX"+i);
				foo();
			}
		}
		StackRecorder.logAll();
	}
	
	public static void foo() {
		for(int i=0;i<2;i++) {
			for(int j=0;j<2;j++) {
				StackRecorder.addCase("foo"+i);
				bar();
			}
		}
	}
	
	public static void bar() {
		for(int i=0;i<2;i++) {
			StackRecorder.record();
		}
	}
}
