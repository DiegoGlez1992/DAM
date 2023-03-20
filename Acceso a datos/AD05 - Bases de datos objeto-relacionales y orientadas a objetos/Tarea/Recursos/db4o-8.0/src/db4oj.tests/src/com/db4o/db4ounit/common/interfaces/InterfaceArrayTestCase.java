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
package com.db4o.db4ounit.common.interfaces;

import db4ounit.*;
import db4ounit.extensions.*;

public class InterfaceArrayTestCase extends AbstractDb4oTestCase {
        
        public interface Foo {
        }
        
        public static class FooImpl implements Foo {
        }
        
        public static class Bar {
                public Bar(Foo... foos) {
                        this.foos = foos; 
                }

                public Foo[] foos;
        }
        
        @Override
        protected void store() throws Exception {
                store(new Bar(new FooImpl()));
        }
        
        public void test() {
                Assert.areEqual(1, retrieveOnlyInstance(Bar.class).foos.length);
        }

}