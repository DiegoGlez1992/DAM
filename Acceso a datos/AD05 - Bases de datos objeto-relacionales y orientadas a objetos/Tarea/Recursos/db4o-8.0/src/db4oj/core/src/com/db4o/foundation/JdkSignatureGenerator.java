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
package com.db4o.foundation;

import java.net.*;
import java.util.*;

/**
 * @exclude
 * @sharpen.ignore
 */
public class JdkSignatureGenerator {
	
	private static final Random _random = new Random();
	
	private static int _counter;
	
	public static String generateSignature() {
		StringBuffer sb = new StringBuffer();
		try {
			String hostName = java.net.InetAddress.getLocalHost().getHostName() + "_";
			if(hostName.length() > 15){
				hostName = hostName.substring(0,15);
			}
			sb.append(hostName);
		} catch (UnknownHostException e) {
		}
		sb.append(Long.toHexString(System.currentTimeMillis()));
		sb.append(Integer.toHexString(_counter++));
		int hostAddress = 0;
		byte[] addressBytes;
		try {
			addressBytes = java.net.InetAddress.getLocalHost().getAddress();
			for (int i = 0; i < addressBytes.length; i++) {
				hostAddress <<= 4;
				hostAddress -= addressBytes[i];
			}
		} catch (UnknownHostException e) {
		}
		sb.append(Integer.toHexString(hostAddress));
		for (int i = 0; i < 4; i++) {
			sb.append(Integer.toHexString(randomInt()));
		}
		return sb.toString().substring(0, 31);
	}

	private static int randomInt() {
		return _random.nextInt();
	}


}
