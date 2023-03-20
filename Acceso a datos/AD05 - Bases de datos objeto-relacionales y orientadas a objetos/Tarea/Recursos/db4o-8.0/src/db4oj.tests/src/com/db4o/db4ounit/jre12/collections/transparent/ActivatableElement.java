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
package com.db4o.db4ounit.jre12.collections.transparent;

import com.db4o.activation.*;
import com.db4o.ta.*;

/**
 * @sharpen.remove
 */
@decaf.Remove(decaf.Platform.JDK11)
public class ActivatableElement implements Activatable, CollectionElement, Comparable{
	
	private Activator _activator;

	public void activate(ActivationPurpose purpose) {
		if(_activator != null) {
			_activator.activate(purpose);
		}
	}

	public void bind(Activator activator) {
    	if (_activator == activator) {
    		return;
    	}
    	if (activator != null && _activator != null) {
            throw new IllegalStateException();
        }
		_activator = activator;
	}
	
	public String _name;
	
	public ActivatableElement(String name){
		_name = name;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(! (obj instanceof ActivatableElement)){
			return false;
		}
		activate(ActivationPurpose.READ);
		ActivatableElement other = (ActivatableElement)obj;
		other.activate(ActivationPurpose.READ);
		return _name.equals(other._name);
	}
	
	@Override
	public int hashCode() {
		activate(ActivationPurpose.READ);
		return _name.hashCode();
	}
	
	@Override
	public String toString() {
		activate(ActivationPurpose.READ);
		return "ActivatableElement " + _name;
	}

	public int compareTo(Object o) {
		activate(ActivationPurpose.READ);
		CollectionElement other = (CollectionElement) o;
		if(_name == null){
			if(other.name() == null){
				return 0;
			}else{
				return -1;
			}
		}
		return _name.compareTo(other.name());
	}

	public String name() {
		activate(ActivationPurpose.READ);
		return _name;
	}
	
}
