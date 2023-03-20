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
import com.db4o.ext.*;
import com.db4o.foundation.io.*;
import com.db4o.internal.activation.*;
import com.db4o.types.*;

/**
 * Transfer of blobs to and from the db4o system,
 * if users use the Blob Db4oType.
 * 
 * @moveto com.db4o.internal.blobs
 * @exclude
 * 
 * @sharpen.if !SILVERLIGHT
 */
public class BlobImpl implements Blob, Cloneable, Db4oTypeImpl {
	
	public final static int COPYBUFFER_LENGTH=4096;

    public String fileName;
    public String i_ext;
    private transient File i_file;
    private transient BlobStatus i_getStatusFrom;
    public int i_length;
    private transient double i_status = Status.UNUSED;
    private transient ObjectContainerBase i_stream;
    private transient Transaction i_trans;

    /**
     * @param depth
     */
    public int adjustReadDepth(int depth) {
        return 1;
    }

    private String checkExt(File file) {
        String name = file.getName();
        int pos = name.lastIndexOf(".");
        if (pos > 0) {
            i_ext = name.substring(pos);
            return name.substring(0, pos);
        }
        
        i_ext = "";
        return name;
        
    }

    private static void copy(File from, File to) throws IOException {
    	File4.copyFile(from, to);
    }

    public Object createDefault(Transaction a_trans) {
        BlobImpl bi = null;
        try {
            bi = (BlobImpl) this.clone();
            bi.setTrans(a_trans);
        } catch (CloneNotSupportedException e) {
            return null;
        }
        return bi;
    }

    public FileInputStream getClientInputStream() throws Exception {
        return new FileInputStream(i_file);
    }

    public FileOutputStream getClientOutputStream() throws Exception {
        return new FileOutputStream(i_file);
    }

    public String getFileName() {
        return fileName;
    }

    public int getLength() {
        return i_length;
    }

    public double getStatus() {
        if (i_status == Status.PROCESSING && i_getStatusFrom != null) {
            return i_getStatusFrom.getStatus();
        }
        if (i_status == Status.UNUSED) {
            if (i_length > 0) {
                i_status = Status.AVAILABLE;
            }
        }
        return i_status;
    }

    public void getStatusFrom(BlobStatus from) {
        i_getStatusFrom = from;
    }

    public boolean hasClassIndex() {
        return false;
    }

    public void readFrom(File file) throws IOException {
        if (!file.exists()) {
            throw new IOException(Messages.get(41, file.getAbsolutePath()));
        }
        i_length = (int) file.length();
        checkExt(file);
        if (i_stream.isClient()) {
            i_file = file;
            ((BlobTransport)i_stream).readBlobFrom(i_trans, this);
        } else {
            readLocal(file);
        }
    }

    public void readLocal(File file) throws IOException {
        boolean copied = false;
        if (fileName == null) {
            File newFile = new File(serverPath(), file.getName());
            if (!newFile.exists()) {
                copy(file, newFile);
                copied = true;
                fileName = newFile.getName();
            }
        }
        if (!copied) {
            copy(file, serverFile(checkExt(file), true));
        }
        synchronized (i_stream.lock()) {
            i_stream.storeInternal(i_trans, this, false);
        }
        i_status = Status.COMPLETED;
    }

    public File serverFile(String promptName, boolean writeToServer) throws IOException {
        synchronized (i_stream.lock()) {
            i_stream.activate(i_trans, this, new FixedActivationDepth(2));
        }
        String path = serverPath();
        i_stream.configImpl().ensureDirExists(path);
        if (writeToServer) {
            if (fileName == null) {
                if (promptName != null) {
                    fileName = promptName;
                } else {
                    fileName = "b_" + System.currentTimeMillis();
                }
                String tryPath = fileName + i_ext;
                int i = 0;
                while (new File(path, tryPath).exists()) {
                    tryPath = fileName + "_" + i++ +i_ext;
                    if (i == 99) {
                        // should never happen
                        i_status = Status.ERROR;
                        throw new IOException(Messages.get(40));
                    }
                }
                fileName = tryPath;
                synchronized (i_stream.lock()) {
                    i_stream.storeInternal(i_trans, this, false);
                }
            }
        } else {
            if (fileName == null) {
                throw new IOException(Messages.get(38));
            }
        }
        String lastTryPath = path + File.separator + fileName;
        if (!writeToServer) {
            if (!(new File(lastTryPath).exists())) {
                throw new IOException(Messages.get(39));
            }
        }
        return new File(lastTryPath);
    }

    private String serverPath() throws IOException {
        String path = i_stream.configImpl().blobPath();
        if (path == null) {
            path = "blobs";
        }
        i_stream.configImpl().ensureDirExists(path);
        return path;
    }

    public void setStatus(double status) {
        i_status = status;
    }

    public void setTrans(Transaction a_trans) {
        i_trans = a_trans;
        i_stream = a_trans.container();
    }

    public void writeLocal(File file) throws IOException {
        copy(serverFile(null, false), file);
        i_status = Status.COMPLETED;
    }

    public void writeTo(File file) throws IOException {
        if (getStatus() == Status.UNUSED) {
            throw new IOException(Messages.get(43));
        }
        if (i_stream.isClient()) {
            i_file = file;
            i_status = Status.QUEUED;
            ((BlobTransport)i_stream).writeBlobTo(i_trans, this);
        } else {
            writeLocal(file);
        }
    }
    
    public void setObjectReference(ObjectReference objectReference) {
        // not necessary
    }

    public void deleteFile() throws IOException {
        if (getStatus() == Status.UNUSED) {
            throw new IOException(Messages.get(43));
        }
        if (i_stream.isClient()) {
            ((BlobTransport)i_stream).deleteBlobFile(i_trans, this);
        } else {
            serverFile(null, false).delete();
        }
        fileName = null;
        i_ext = null;
        i_length = 0;
        setStatus(Status.UNUSED);
        synchronized (i_stream.lock()) {
            i_stream.storeInternal(i_trans, this, false);
        }
    } 

}