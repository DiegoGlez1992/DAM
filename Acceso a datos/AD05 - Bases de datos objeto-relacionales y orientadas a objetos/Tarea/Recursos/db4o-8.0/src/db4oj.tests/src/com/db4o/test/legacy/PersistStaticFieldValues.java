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
package com.db4o.test.legacy;

import com.db4o.*;
import com.db4o.test.*;

public class PersistStaticFieldValues {
    
    public static final PsfvHelper ONE = new PsfvHelper();
    public static final PsfvHelper TWO = new PsfvHelper();
    public static final PsfvHelper THREE = new PsfvHelper();
    
    public PsfvHelper one;
    public PsfvHelper two;
    public PsfvHelper three;
    

    public void configure() {
        Db4o
            .configure()
            .objectClass(PersistStaticFieldValues.class)
            .persistStaticFieldValues();
    }
    
    public void store(){
        Test.deleteAllInstances(this);
        PersistStaticFieldValues psfv = new PersistStaticFieldValues();
        psfv.one = ONE;
        psfv.two = TWO;
        psfv.three = THREE; 
        Test.store(psfv);
    }
    
    public void test(){
        PersistStaticFieldValues psfv = (PersistStaticFieldValues)Test.getOne(this);
        Test.ensure(psfv.one == ONE);
        Test.ensure(psfv.two == TWO);
        Test.ensure(psfv.three == THREE);
    }
    
    public static class PsfvHelper{
        
    }
    

}
