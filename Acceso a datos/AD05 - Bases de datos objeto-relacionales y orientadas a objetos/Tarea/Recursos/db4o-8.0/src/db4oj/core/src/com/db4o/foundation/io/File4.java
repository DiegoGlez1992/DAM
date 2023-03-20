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
package com.db4o.foundation.io;

import java.io.*;

import com.db4o.ext.*;

/**
 * @sharpen.ignore
 */
public class File4 {
	
	public static byte[] readAllBytes(final String fname) 
		throws FileNotFoundException, IOException {
			
		final File file = new File(fname);
		byte[] bytes = new byte[(int)file.length()];
		final FileInputStream fis = new FileInputStream(file);
		try {
			fis.read(bytes);
		} finally {
			fis.close();
		}
		return bytes;
	}

	/**
	 * @sharpen.rename System.IO.File.Move
	 */
	public static void rename(String oldPath,String newPath) throws IOException {
		if(!new File(oldPath).renameTo(new File(newPath))) {
			throw new IOException("Could not rename '"+oldPath+"' to '"+newPath+"'.");
		}
	}
	
    public static void copy(String source, String target) throws IOException {
		java.io.File sourceFile = new java.io.File(source);

		java.io.File targetFile = new java.io.File(target);
		targetFile.mkdirs();
		File4.delete(target);

		if (sourceFile.isDirectory()) {
			copyDirectory(sourceFile, targetFile);
		} else {
			copyFile(sourceFile, targetFile);
		}
	}

	/**
	 * @sharpen.rename System.IO.File.Copy
	 */
	public static void copyFile(File source, File target) throws IOException {
		final int bufferSize = 64000;

		RandomAccessFile rafIn = new RandomAccessFile(source.getAbsolutePath(), "r");
		RandomAccessFile rafOut = new RandomAccessFile(target.getAbsolutePath(), "rw");
		final long len = rafIn.length();
		final byte[] bytes = new byte[bufferSize];

		long totalRead=0;
		while (totalRead<len) {
		    int numRead=rafIn.read(bytes,0,bufferSize);
		    rafOut.write(bytes,0,numRead);
		    totalRead+=numRead;
		}

		rafIn.close();
		rafOut.close();
	}

	private static void copyDirectory(File source, File target) throws IOException {
		String[] files = source.list();
		if (files != null) {
		    for (int i = 0; i < files.length; i++) {
		        copy(Path4.combine(source.getAbsolutePath(), files[i]),
		        	Path4.combine(target.getAbsolutePath(), files[i]));
		    }
		}
	}
	
    public static void delete(String fname) {
		final File file = new File(fname);
		if (!file.exists()) {
			return;
		}
		translateDeleteFailureToException(file);
	}

	static void translateDeleteFailureToException(final File file) {
		if (!file.delete()) {
			throw new Db4oIOException("Could not delete '" + file.getAbsolutePath() + "'.");
		}
	}
    
	/**
	 * @sharpen.rename System.IO.File.Exists
	 */
    public static boolean exists(String fname){
        return new File(fname).exists();
    }

	/**
	 * @sharpen.rename System.IO.Directory.CreateDirectory
	 */
    public static void mkdirs(String path) {
		new File(path).mkdirs();
	}

	public static long size(final String filePath) {
		File f = new File(filePath);
		return f.length();
	}
}
