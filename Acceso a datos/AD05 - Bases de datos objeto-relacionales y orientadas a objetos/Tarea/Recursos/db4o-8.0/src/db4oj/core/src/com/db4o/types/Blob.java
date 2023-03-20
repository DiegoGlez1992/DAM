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
package com.db4o.types;

import java.io.*;

/**
 * the db4o Blob type to store blobs independent of the main database
 * file and allows to perform asynchronous upload and download operations.
 * <br><br>
 * <b>Usage:</b><br>
 * - Define Blob fields on your user classes.<br>
 * - As soon as an object of your class is stored, db4o automatically
 * takes care that the Blob field is set.<br>
 * - Call readFrom to read a blob file into the db4o system.<br>
 * - Call writeTo to write a blob file from within the db4o system.<br>
 * - getStatus may help you to determine, whether data has been
 * previously stored. It may also help you to track the completion
 * of the current process.
 * <br><br>
 * db4o client/server carries out all blob operations in a separate
 * thread on a specially dedicated socket. One socket is used for
 * all blob operations and operations are queued. Your application
 * may continue to access db4o while a blob is transferred in the
 * background.
 */
public interface Blob extends Db4oType {

    /**
     * returns the name of the file the blob was stored to.
     * <br><br>The method may return null, if the file was never
     * stored.
     * @return String the name of the file.
     */
    public String getFileName();

    /**
     * returns the status after the last read- or write-operation.
     * <br><br>The status value returned may be any of the following:<br>
     * {@link com.db4o.ext.Status#UNUSED}  no data was ever stored to the Blob field.<br>
     * {@link com.db4o.ext.Status#AVAILABLE} available data was previously stored to the Blob field.<br>
     * {@link com.db4o.ext.Status#QUEUED} an operation was triggered and is waiting for it's turn in the Blob queue.<br>
     * {@link com.db4o.ext.Status#COMPLETED} the last operation on this field was completed successfully.<br>
     * {@link com.db4o.ext.Status#PROCESSING} for internal use only.<br>
     * {@link com.db4o.ext.Status#ERROR} the last operation failed.<br>
     * or a double between 0 and 1 that signifies the current completion percentage of the currently
     * running operation.<br><br> the five {@link com.db4o.ext.Status} constants defined in this interface or a double
     * between 0 and 1 that signifies the completion of the currently running operation.<br><br>
     * @return status - the current status
     * @see com.db4o.ext.Status  constants
     */
    public double getStatus();

    /**
     * reads a file into the db4o system and stores it as a blob.
     * <br><br>
     * In Client/Server mode db4o will open an additional socket and
     * process writing data in an additional thread.
     * <br><br>
     * @param file the file the blob is to be read from.
     * @throws IOException in case of errors
     */
    public void readFrom(File file) throws IOException;

    /**
     * reads a file into the db4o system and stores it as a blob.
     * <br><br>
     * db4o will use the local file system in Client/Server mode also. 
     * <br><br>
     * @param file the file the blob is to be read from.
     * @throws IOException in case of errors
     */
    public void readLocal(File file) throws IOException;

    /**
     * writes stored blob data to a file.
     * <br><br>
     * db4o will use the local file system in Client/Server mode also. 
     * <br><br>
     * @throws IOException in case of errors and in case no blob
     * data was stored
     * @param file the file the blob is to be written to.
     */
    public void writeLocal(File file) throws IOException;

    /**
     * writes stored blob data to a file.
     * <br><br>
     * In Client/Server mode db4o will open an additional socket and
     * process writing data in an additional thread.
     * <br><br>
     * @throws IOException in case of errors and in case no blob
     * data was stored
     * @param file the file the blob is to be written to.
     */
    public void writeTo(File file) throws IOException;

    /**
     * Deletes the current file stored in this BLOB.
     *
     * @throws IOException in case of errors and in case no
     * data was stored
     */
    public void deleteFile() throws IOException;
}