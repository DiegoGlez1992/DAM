package com.db4odoc.f1.chapter6;

import com.db4o.*;
import com.db4o.cs.*;
import com.db4o.messaging.*;

/**
 * stops the db4o Server started with {@link StartServer}. <br>
 * <br>
 * This is done by opening a client connection to the server and by sending a
 * StopServer object as a message. {@link StartServer} will react in it's
 * processMessage method.
 */
public class StopServer implements ServerInfo {

	/**
	 * stops a db4o Server started with StartServer.
	 * 
	 * @throws Exception
	 */
	public static void main(String[] args) {
		ObjectContainer objectContainer = null;
		try {

			// connect to the server
			objectContainer = Db4oClientServer.openClient(Db4oClientServer
					.newClientConfiguration(), HOST, PORT, USER, PASS);

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (objectContainer != null) {

			// get the messageSender for the ObjectContainer
			MessageSender messageSender = objectContainer.ext().configure()
					.clientServer().getMessageSender();

			// send an instance of a StopServer object
			messageSender.send(new StopServer());

			// close the ObjectContainer
			objectContainer.close();
		}
	}
}
