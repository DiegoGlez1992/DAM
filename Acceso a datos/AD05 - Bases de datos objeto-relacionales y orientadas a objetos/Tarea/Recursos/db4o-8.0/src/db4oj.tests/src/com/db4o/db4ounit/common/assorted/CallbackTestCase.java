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
package com.db4o.db4ounit.common.assorted;

import com.db4o.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * Regression test case for COR-1117
 */
public class CallbackTestCase extends AbstractDb4oTestCase {

    public static void main(String[] args) {
        new CallbackTestCase().runAll();
    }
    
    public void testPublicCallback() {
    	runTest(new PublicCallback());
    }

    /**
     * @sharpen.if !SILVERLIGHT
     */
    @decaf.Ignore(decaf.Platform.JDK11)
    public void testPrivateCallback() {
        runTest(new PrivateCallback());
    }
    
    /**
     * @sharpen.if !SILVERLIGHT
     */
    @decaf.Ignore(decaf.Platform.JDK11)
    public void testPackageCallback() {
    	runTest(new PackageCallback());
    }
    
    public void testInheritedPublicCallback() {
    	runTest(new InheritedPublicCallback());
    }

    /**
     * @sharpen.if !SILVERLIGHT
     * @see testPrivateCallback()
     */
    @decaf.Ignore(decaf.Platform.JDK11)
    public void testInheritedPrivateCallback() {
        runTest(new InheritedPrivateCallback());
    }
    
    /**
     * @sharpen.if !SILVERLIGHT
     * @see testPackageCallback()
     */
    @decaf.Ignore(decaf.Platform.JDK11)
    public void testInheritedPackageCallback() {
    	runTest(new InheritedPackageCallback());
    }
    
    public void testThrowingCallback(){
    	Assert.expect(RuntimeException.class, new CodeBlock() {
			public void run() throws Throwable {
				store(new ThrowingCallback());
			}
		});
    }

	private void runTest(Item item) {
		store(item);
        db().commit();
        Assert.isTrue(item.isStored());
        Assert.isTrue(db().ext().isStored(item));
	}
	
	public static class Item {
		public transient ObjectContainer _objectContainer;

		public boolean isStored() {
            return _objectContainer.ext().isStored(this);
        }
	}

    public static class PackageCallback extends Item {
        void objectOnNew(ObjectContainer container) {
            _objectContainer = container;
        }
    }
    
    public static class ThrowingCallback extends Item {
        void objectOnNew(ObjectContainer container) {
            throw new RuntimeException();
        }
    }
    
    
    public static class InheritedPackageCallback extends PackageCallback {
    }
    
    public static class PrivateCallback extends Item {
        @SuppressWarnings("unused")
		private void objectOnNew(ObjectContainer container) {
            _objectContainer = container;
        }
    }
    
    public static class InheritedPrivateCallback extends PrivateCallback {
    }
    
    public static class PublicCallback extends Item {
        public void objectOnNew(ObjectContainer container) {
            _objectContainer = container;
        }
    }
    
    public static class InheritedPublicCallback extends PublicCallback {
    }
}
