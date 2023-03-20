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


public class MReadBlob extends MsgBlob implements ServerSideMessage {
	public void processClient(Socket4Adapter sock) throws IOException {
        Msg message = Msg.readMessage(messageDispatcher(), transaction(), sock);
        if (message.equals(Msg.LENGTH)) {
            try {
                _currentByte = 0;
                _length = message.payLoad().readInt();
                _blob.getStatusFrom(this);
                _blob.setStatus(Status.PROCESSING);
                copy(sock,this._blob.getClientOutputStream(),_length,true);
                message = Msg.readMessage(messageDispatcher(), transaction(), sock);
                if (message.equals(Msg.OK)) {
                    this._blob.setStatus(Status.COMPLETED);
                } else {
                    this._blob.setStatus(Status.ERROR);
                }
            } catch (Exception e) {
            }
        } else if (message.equals(Msg.ERROR)) {
            this._blob.setStatus(Status.ERROR);
        }

    }
    public void processAtServer() {
        try {
            BlobImpl blobImpl = this.serverGetBlobImpl();
            if (blobImpl != null) {
                blobImpl.setTrans(transaction());
                File file = blobImpl.serverFile(null, false);
                int length = (int) file.length();
                Socket4Adapter sock = serverMessageDispatcher().socket();
                Msg.LENGTH.getWriterForInt(transaction(), length).write(sock);
                FileInputStream fin = new FileInputStream(file);
                copy(fin,sock,false);
                sock.flush();
                Msg.OK.write(sock);
            }
        } catch (Exception e) {
        	write(Msg.ERROR);
        }
    }
}