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
package com.db4o.db4ounit.common.qlin;

import java.util.*;

import com.db4o.*;
import com.db4o.qlin.*;

import static com.db4o.qlin.QLinSupport.*;

import db4ounit.*;
import db4ounit.extensions.*;


/**
 * 
 * Syntax and implementation of QLin were inspired by:
 * http://www.h2database.com/html/jaqu.html 
 * 
 * @sharpen.if !SILVERLIGHT
 */
@decaf.Remove(decaf.Platform.JDK11)
public class BasicQLinTestCase  {
	
	private QLinable db(){
		// disabled for now, we removed QLinable from the 8.0 ObjectContainer interface
		return null;
	}
	
	private void storeAll(List expected) {
		for (Object obj : expected) {
			// store(obj);
		}
	}
	
	public void testFromSelect(){
		storeAll(occamAndZora());
		IteratorAssert.sameContent(occamAndZora(), db().from(Cat.class).select());
	}
	
	public void testWhereFieldNameAsString(){
		storeAll(occamAndZora());
		IteratorAssert.sameContent(occam(), 
				db().from(Cat.class)
					.where("name")
					.equal("Occam")
					.select());
	}
	
	public void testWherePrototypeFieldIsString(){
		storeAll(occamAndZora());
		IteratorAssert.sameContent(occam(), 
				db().from(Cat.class)
					.where(p(Cat.class).name())
					.equal("Occam")
					.select());
	}
	
	public void testWherePrototypeFieldStartsWith(){
		storeAll(occamAndZora());
		IteratorAssert.sameContent(occam(), 
				db().from(Cat.class)
					.where(p(Cat.class).name())
					.startsWith("Occ")
					.select());
	}
	
	public void testField(){
		storeAll(occamAndZora());
		IteratorAssert.sameContent(occam(), 
				db().from(Cat.class)
					.where(field("name"))
					.equal("Occam")
					.select());
	}
	
	public void testWherePrototypeFieldIsPrimitiveInt(){
		storeAll(occamAndZora());
		IteratorAssert.sameContent(occam(), 
				db().from(Cat.class)
					.where(p(Cat.class).age)
					.equal(7)
					.select());
	}
	
	public void testWherePrototypeFieldIsSmaller(){
		storeAll(occamAndZora());
		IteratorAssert.sameContent(zora(), 
				db().from(Cat.class)
					.where(p(Cat.class).age)
					.smaller(7)
					.select());
	}
	
	public void testWherePrototypeFieldIsGreater(){
		storeAll(occamAndZora());
		IteratorAssert.sameContent(occamAndZora(), 
				db().from(Cat.class)
					.where(p(Cat.class).age)
					.greater(5)
					.select());
	}
	
	public void testLimit(){
		storeAll(occamAndZora());
		Assert.areEqual(1,
				db().from(Cat.class)
					.limit(1)
					.select()
					.size());
	}
	
	public void testPredefinedPrototype(){
		storeAll(occamAndZora());
		Cat cat = prototype(Cat.class);
		IteratorAssert.sameContent(occam(), 
				db().from(Cat.class)
					.where(cat.name())
					.startsWith("Occ")
					.select());
	}
	
	public void testQueryingByInterface(){
		storeAll(occamAndIsetta());
		Dog dog = prototype(Dog.class);
		Cat cat = prototype(Cat.class);
		assertQuery(isetta(), dog, "Isetta");
		assertQuery(occam(), cat, "Occam");
	}
	
	public void testTwoLevelField(){
		storeAll(occamZoraAchatAcrobat());
		
	}
	
	public void testWhereAsNativeQuery(){
		storeAll(occamAndZora());
		Cat cat = prototype(Cat.class);
//		IteratorAssert.sameContent(occam(),
//			db().from(Cat.class)
//				.where(cat.name().equals("Occam"))
//				.select());
	}
	
	public void testUpdate(){
		storeAll(occamZoraAchatAcrobat());
		int newAge = 2; 
		Cat cat = prototype(Cat.class);
//		db().from(Cat.class)
//		   .where(cat.father()).equal("Occam")
//		   .update(cat.age(newAge));
		
		ObjectSet<Cat> updated = db().from(Cat.class)
		.where(cat.name()).equal("Occam")
		.select();
		Iterator<Cat> i = updated.iterator();
//		while(i.hasNext()){
//			Assert.areEqual(newAge, i.next().age());
//		}
	}
	
	public void testExecute(){
		storeAll(occamZoraAchatAcrobat());
		Cat cat = prototype(Cat.class);
//		db().from(Cat.class)
//		  .where(cat.name()).startsWith("Zor")
//		  .execute(cat.feed());
	}
	
	
	private List<Cat> occamZoraAchatAcrobat() {
		return family(
				new Cat("Occam", 7), 
				new Cat("Zora", 6), 
				new Cat("Achat", 1), 
				new Cat("Acrobat", 1));
	}
	
	private List<Cat> family(Cat father, Cat mother, Cat...children){
		List<Cat> list = new ArrayList<Cat>();
		list.add(father);
		list.add(mother);
		for (Cat child : children) {
			child.father = father;
			child.mother = mother;
			father.children.add(child);
			mother.children.add(child);
		}
		father.spouse(mother);
		return list;
	}

	public void assertQuery(List<? extends Pet> expected, Pet pet, String name){
		IteratorAssert.sameContent(expected, 
				db().from(pet.getClass())
					.where(pet.name())
					.equal(name)
					.select());
	}
	
	private List<Cat> occamAndZora() {
		List<Cat> list = new ArrayList<Cat>();
		Cat occam = new Cat("Occam", 7);
		Cat zora = new Cat("Zora", 6);
		occam.spouse(zora);
		list.add(occam);
		list.add(zora);
		return list;
	}
	
	private List<Cat> occam() {
		return singleCat("Occam");
	}
	
	private List<Cat> zora() {
		return singleCat("Zora");
	}
	
	private List<Dog> isetta() {
		return singleDog("Isetta");
	}
	
	private List<Pet> occamAndIsetta(){
		List<Pet> list = new ArrayList<Pet>();
		list.add(new Cat("Occam"));
		list.add(new Dog("Isetta"));
		return list;
	}

	private List<Cat> singleCat(String name) {
		List<Cat> list = new ArrayList<Cat>();
		list.add(new Cat(name));
		return list;
	}
	
	private List<Dog> singleDog(String name) {
		List<Dog> list = new ArrayList<Dog>();
		list.add(new Dog(name));
		return list;
	}
	
	public static class Cat implements Pet {
		
		public int age;
		
		public String name;
		
		public Cat spouse;
		
		public Cat father;
		
		public Cat mother;
		
		public List<Cat> children = new ArrayList();
		
		public Cat() {			
		}
		
		public Cat(String name){
			this.name = name;
		}
		
		public Cat(String name, int age){
			this(name);
			this.age = age;
		}
		
		public String name(){
			return name;
		}
		
		public void spouse(Cat spouse){
			this.spouse = spouse;
			spouse.spouse = this;
		}
		
		public Cat father(){
			return father;
		}
		
		public Cat mother(){
			return mother;
		}

		@Override
		public boolean equals(Object obj) {
			if(! (obj instanceof Cat)){
				return false;
			}
			Cat other = (Cat) obj;
			if (name == null) {
				return other.name == null;
			}
			return name.equals(other.name);
		}
		
		public int age(){
			return age;
		}
		
		public void age(int newAge){
			age = newAge;
		}
		
		public void feed(){
			System.out.println(name + ": 'Thanks for all the fish.'");
		}
		
	}
	
	public static class Dog implements Pet {
		
		private String _name;
		
		public Dog() {			
		}
		
		public Dog(String name){
			_name = name;
		}

		public String name() {
			return _name;
		}
		
		public boolean equals(Object obj) {
			if(! (obj instanceof Dog)){
				return false;
			}
			Dog other = (Dog) obj;
			if (_name == null) {
				return other._name == null;
			}
			return _name.equals(other._name);
		}
	}
	
	public interface Pet<T> {
		
		public String name();
		
	}

}
