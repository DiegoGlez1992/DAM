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
package com.db4o.test.nativequery.cats;

import java.util.Iterator;
import java.util.List;

import com.db4o.ObjectContainer;
import com.db4o.config.Configuration;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.query.Predicate;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;


public class NQCatConsistencyTestCase extends AbstractDb4oTestCase {
    
	protected void configure(Configuration config) {
        config.optimizeNativeQueries(false);
	}

    public void store(){
        storeCats();
    }
    
    public void test(){
        
        ExtObjectContainer oc = db();
        oc.configure().optimizeNativeQueries(true);
        runTests();
        oc.configure().optimizeNativeQueries(false);
        runTests();

    }
    
    
    public void runTests(){
        
        expect(new Predicate<Cat>(){
            public boolean match(Cat cat){
                return cat._age == 7;
            }
        }, null);
        
        expect(new Predicate<Cat>(){
            public boolean match(Cat cat){
                return cat._age == 1;
            }
        }, new String[]{"Occam", "Vahiné" });
        
        expect(new Predicate<Cat>(){
            public boolean match(Cat cat){
                return cat._father._age == 1;
            }
        }, new String[]{"Achat", "Acrobat" });
        
        expect(new Predicate<Cat>(){
            public boolean match(Cat cat){
                return cat._father._father._firstName.equals("Edwin");
            }
        }, new String[]{"Achat", "Acrobat" });
        
        
        // The following will fail with nqopt
        // because SODA swallows nulls
        
//        expect(new Predicate<Cat>(){
//            public boolean match(Cat cat){
//                return cat._father._father._firstName.equals("Edwin")
//                    || cat._father._firstName.equals("Edwin");
//            }
//        }, new String[]{"Achat", "Acrobat"});

        
        // will be run unoptimized (arithmetics on candidate member)        
        expect(new Predicate<Cat>(){
            public boolean match(Cat cat){
                return cat._age + 1 == 2;
            }
        }, new String[]{"Occam", "Vahiné" });

        
        expect(new Predicate<Cat>(){
            public boolean match(Cat cat){
                return cat.getFirstName().equals("Occam")
                    && cat.getAge() == 1;
            }
        }, new String[]{"Occam"});
        
        
        // will be run unoptimized (non-getter method call: getFullName)        
        expect(new Predicate<Cat>(){
            public boolean match(Cat cat){
                return cat.getFullName().equals("Achat Leo Lenis");
            }
        }, new String[]{"Achat"});


        // will be run unoptimized (non-getter method call: getFullName)        
        expect(new Predicate<Cat>(){
            public boolean match(Cat cat){
                return cat.getFullName() == null;
            }
        }, new String[]{"Trulla"});
        

        // will be run optimized        
        expect(new Predicate<Cat>(){
            public boolean match(Cat cat){
                return cat._firstName.startsWith("A");
            }
            
        }, new String[]{"Achat", "Acrobat"});
        
    }
    
    public void storeCats(){
        
        Cat winni = new Cat();
        winni._sex = Animal.MALE;
        winni._firstName = "Edwin";
        winni._lastName = "Sanddrops";
        winni._age = 12;
        
        Cat bachi = new Cat();
        bachi._sex = Animal.FEMALE;
        bachi._firstName = "Frau Bachmann";
        bachi._lastName = "von der Bärenhöhle";
        bachi._age = 10;
        
        Cat occam = new Cat();
        occam._sex = Animal.MALE;
        occam._firstName = "Occam";
        occam._lastName = "von der Bärenhöhle";
        occam._age = 1;
        occam._father = winni;
        occam._mother = bachi;
        
        Cat zora = new Cat();
        zora._sex = Animal.FEMALE;
        zora._firstName = "Vahiné";
        zora._lastName = "des Fauves et Or";
        zora._age = 1;
        
        Cat achat = new Cat();
        achat._sex = Animal.FEMALE;
        achat._firstName = "Achat";
        achat._lastName = "Leo Lenis";
        achat._father = occam;
        achat._mother = zora;
        
        Cat acrobat = new Cat();
        acrobat._sex = Animal.FEMALE;
        acrobat._firstName = "Acrobat";
        acrobat._lastName = "Leo Lenis";
        acrobat._father = occam;
        acrobat._mother = zora;
        
        ObjectContainer db=db();
        
        db.store(achat);
        db.store(acrobat);
        
        Cat trulla = new Cat();
        trulla._firstName = "Trulla";
        db.store(trulla);
        
    }
    
    private void expect(Predicate predicate, String[] names){
        
        if(names == null){
            names = new String[] {};
        }
        
        List list = db().query(predicate);
        
        Iterator i = list.iterator();
        while(i.hasNext()){
            Cat cat = (Cat)i.next();
            boolean good = false;
            for (int j = 0; j < names.length; j++) {
                if(names[j] != null){
                    if(cat._firstName.equals(names[j])){
                        names[j] = null;
                        good = true;
                        break;
                    }
                }
            }
            Assert.isTrue(good);
        }
        for (int j = 0; j < names.length; j++) {
            Assert.isNull(names[j]);
        }
    }
    
    
    


}
