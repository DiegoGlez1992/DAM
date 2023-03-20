package com.db4odoc.f1.chapter6;

import com.db4o.*;
import com.db4o.cs.*;
import com.db4o.cs.config.*;
import com.db4o.messaging.*;

/**
 * starts a db4o server with the settings from {@link ServerInfo}. <br>
 * <br>
 * This is a typical setup for a long running server. <br>
 * <br>
 * The Server may be stopped from a remote location by running StopServer. The
 * StartServer instance is used as a MessageRecipient and reacts to receiving an
 * instance of a StopServer object. <br>
 * <br>
 */
public class StartServer implements ServerInfo, MessageRecipient {

	/**
	 * setting the value to true denotes that the server should be closed
	 */
	private boolean stop = false;

	/**
	 * starts a db4o server using the configuration from {@link ServerInfo}.
	 */
	public static void main(String[] arguments) {
		new StartServer().runServer();
	}

	/**
	 * opens the ObjectServer, and waits forever until close() is called or a
	 * StopServer message is being received.
	 */
	public void runServer() {
		synchronized (this) {
			ServerConfiguration config = Db4oClientServer.newServerConfiguration();
			// Using the messaging functionality to redirect all
			// messages to this.processMessage 
			config.networking().messageRecipient(this);
			ObjectServer db4oServer = Db4oClientServer.openServer(config
					, FILE, PORT);
			db4oServer.grantAccess(USER, PASS);

			
			// to identify the thread in a debugger
			Thread.currentThread().setName(this.getClass().getName());

			// We only need low priority since the db4o server has
			// it's own thread.
			Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
			try {
				if (!stop) {
					// wait forever for notify() from close()
					this.wait(Long.MAX_VALUE);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			db4oServer.close();
		}
	}

	/**
	 * messaging callback
	 * 
	 * @see com.db4o.messaging.MessageRecipient#processMessage(MessageContext,
	 *      Object)
	 */
	public void processMessage(MessageContext con, Object message) {
		if (message instanceof StopServer) {
			close();
		}
	}

	/**
	 * closes this server.
	 */
	public void close() {
		synchronized (this) {
			stop = true;
			this.notify();
		}
	}
}
