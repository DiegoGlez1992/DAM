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
package com.db4o.cs.internal.messages;

import java.io.*;

import com.db4o.cs.internal.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.internal.activation.*;


public class MWriteBlob extends MsgBlob implements ServerSideMessage {
	
	public void processClient(Socket4Adapter sock) throws IOException {
        Msg message = Msg.readMessage(messageDispatcher(), transaction(), sock);
        if (message.equals(Msg.OK)) {
            try {
                _currentByte = 0;
                _length = this._blob.getLength();
                _blob.getStatusFrom(this);
                _blob.setStatus(Status.PROCESSING);
                FileInputStream inBlob = this._blob.getClientInputStream();
                copy(inBlob,sock,true);
                sock.flush();
                message = Msg.readMessage(messageDispatcher(), transaction(), sock);
                if (message.equals(Msg.OK)) {

                    // make sure to load the filename to i_blob
                    // to allow client databasefile switching
                    container().deactivate(transaction(), _blob, Integer.MAX_VALUE);
                    container().activate(transaction(), _blob, new FullActivationDepth());

                    this._blob.setStatus(Status.COMPLETED);
                } else {
                    this._blob.setStatus(Status.ERROR);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

	public void processAtServer() {
        try {
            BlobImpl blobImpl = this.serverGetBlobImpl();
            if (blobImpl != null) {
                blobImpl.setTrans(transaction());
                File file = blobImpl.serverFile(null, true);
                Socket4Adapter sock = serverMessageDispatcher().socket();
                Msg.OK.write(sock);
                FileOutputStream fout = new FileOutputStream(file);
                copy(sock,fout,blobImpl.getLength(),false);
                Msg.OK.write(sock);
            }
        } catch (Exception e) {
        }
    }
}