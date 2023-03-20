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
package db4ounit.extensions.util;

import java.io.*;

import com.db4o.foundation.*;
import com.db4o.foundation.io.*;

/**
 * @sharpen.ignore
 */
public class IOServices {
    
	public static String buildTempPath(String fname) {
		return Path4.combine(Path4.getTempPath(), fname);
	}

	public static String safeCanonicalPath(String path) {
		try {
			return new File(path).getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
			return path;
		}
	}
	
	public static String exec(String program) throws IOException, InterruptedException{
	    return exec(program, null);
	}
	
	// TODO: There is a copy of this file in the dRS project. 
	//       In this copy we have changed the signature of 
	//       this method to return a ProcessRunneer.
	//       Port back here!
	public static String exec(String program, String[] arguments) throws IOException, InterruptedException{
	    ProcessRunner runner = new ProcessRunner(program, arguments);
	    runner.waitFor();
	    return runner.formattedResult();  
	}

	public static ProcessRunner start(String program, String[] arguments) throws IOException {
	    return new ProcessRunner(program, arguments);
	}

	public static String execAndDestroy(String program, String[] arguments, String expectedOutput, long timeout) throws IOException{
        ProcessRunner runner = new ProcessRunner(program, arguments);
        runner.destroy(expectedOutput, timeout);
        return runner.formattedResult();
    }
	
	public static class DestroyTimeoutException extends RuntimeException{
	}
	
	public static class ProcessTerminatedBeforeDestroyException extends RuntimeException{
	}
	
	public static class ProcessRunner{
	    
	    final long _startTime;
	    
	    private final String _command;
	    
        private final StreamReader _inputReader;
        
        private final StreamReader _errorReader;
        
        private final BlockingQueue<String> in = new BlockingQueue<String>();
        
        private final Process _process;
        
        private int _result;
        
        private StringBuilder _inputBuffer = new StringBuilder();
        private StringBuilder _errorBuffer = new StringBuilder();
	    
	    public ProcessRunner(String program, String[] arguments) throws IOException{
	        _command = generateCommand(program, arguments);
            _process = Runtime.getRuntime().exec(_command);
            _inputReader = new StreamReader("ProcessRunner Input Thread ["+program+" " + toString(arguments) +"]", _process.getInputStream(), new DelegatingBlockingQueue<String>(in) {
            	@Override
            	public void add(String obj) {
            		_inputBuffer.append(obj);
            		_inputBuffer.append("\n");
            		super.add(obj);
            	}
            });
            _errorReader = new StreamReader("ProcessRunner Output Thread ["+program+" " + toString(arguments) +"]", _process.getErrorStream(), new DelegatingBlockingQueue<String>(in) {
               	@Override
            	public void add(String obj) {
               		_errorBuffer.append(obj);
               		_errorBuffer.append("\n");
            		super.add(obj);
            	}
            });
            _startTime = System.currentTimeMillis();
	    }
	    
	    public String formattedResult(){
	        String res = formatOutput("IOServices.exec", _command); 
	        
	        if(_inputBuffer.length() > 0){
	            res += formatOutput("out", _inputBuffer.toString()); 
	        }
	        if(_errorBuffer.length() > 0){
	            res += formatOutput("err", _errorBuffer.toString()); 
	        }
	        
	        res += formatOutput("result", new Integer(_result).toString());
	        
	        return res;  

	    }
	    
	    private String formatOutput(String task, String output){
	        return headLine(task) + output + "\n";
	    }
	    
	    private String headLine(String task){
	        return "\n" + task + "\n----------------\n";  
	    }

		private String toString(String[] arguments) {
	    	String r = "";
	    	for (String s : arguments) {
	    		if (r.length() > 0) r += " ";
				r += "\"" + s + "\"";
			}
			return r;
		}

		private String generateCommand (String program, String[] arguments){
            String command = program;
            if(arguments != null){
                for (int i = 0; i < arguments.length; i++) {
                    command += " " + arguments[i];
                }
            }
            return command;
	    }
	    
	    public int waitFor() throws InterruptedException{
	        _result = _process.waitFor();
	        joinReaders();
	        return _result;
	    }
	    
	    public void destroy(String expectedOutput, long timeout){
	        try{
    	        waitFor(expectedOutput, timeout);
	        } 
	        finally {
	        	destroy();
	        }
	    }

	    public void destroy(){
	        try{
    	        checkIfTerminated();
    	        
    	        // Race condition: If the process is terminated right here , it may
    	        // terminate successfully before being destroyed.
    	        
	        } finally {
	            _process.destroy();
	            try {
					joinReaders();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	        }
	    }

	    public void write(String msg) throws IOException {
	    	OutputStreamWriter out = new OutputStreamWriter(_process.getOutputStream());
			out.write(msg + "\n");
			out.flush();
	    }
	    
        public void waitFor(String expectedOutput, long timeout) {
			long now = System.currentTimeMillis();
			while (timeout > 0 && !expectedOutput.equals(in.next(timeout))) {
				long l = now;
				now = System.currentTimeMillis();
				timeout -= now-l;
			};

        }

        private void checkIfTerminated() {
            boolean ok = false;
	        try{
	            _process.exitValue();
	        }catch (IllegalThreadStateException ex){
	            ok = true;
	        }
	        if(! ok){
	            throw new ProcessTerminatedBeforeDestroyException();
	        }
        }
	    
	    private void joinReaders() throws InterruptedException{
	        _inputReader.join();
	        _errorReader.join();
	    }
	    
	}

    static class StreamReader implements Runnable {
        
        private final InputStream _stream;
        
        private final Thread _thread;
        
		private final Queue4<String> _in;
        
        StreamReader(String threadName, InputStream stream, Queue4<String> in){
            _stream = stream;
			_in = in;
            _thread = new Thread(this, threadName);
            _thread.setDaemon(true);
            _thread.start();
        }
        
        public void run() {
        	BufferedReader in = new BufferedReader(new InputStreamReader(_stream));
            try {
            	String line;
                while((line=in.readLine()) != null){
                    _in.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        public void join() throws InterruptedException{
        	_thread.join();
        }
        
    }
    
    public static String joinArgs(String separator, String[] args, boolean doQuote)
    {
        StringBuffer buffer = new StringBuffer();
        for (String arg : args)
        {
            if (buffer.length() > 0) buffer.append(separator);
            buffer.append((doQuote ? quote(arg) : arg));
        }
        return buffer.toString();
    }
    
    public static String quote(String s)
    {
        if (s.startsWith("\"")) return s;
        return "\"" + s + "\"";
    }

}
