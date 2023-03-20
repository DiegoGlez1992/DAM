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


/**
 * @exclude
 */
public class ListenerRegistry <E>{
	
	public static <E> ListenerRegistry<E> newInstance() {
		return new ListenerRegistry<E>();
	}

	private IdentitySet4 _listeners;
	
	public void register(Listener4<E> listener){
		if(_listeners == null){
			_listeners = new IdentitySet4();
		}
		_listeners.add(listener);
	}
	
	public void notifyListeners(E event){
		if(_listeners == null){
			return;
		}
		Iterator4 i = _listeners.iterator();
		while(i.moveNext()){
			((Listener4)i.current()).onEvent(event);
		}
	}

	public void remove(Listener4<E> listener) {
		if (_listeners == null) {
			return;
		}
		
		_listeners.remove(listener);			
	}
}
