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
package com.db4o.tools;

import java.io.*;
import java.lang.reflect.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;

/**
 * Logger class to log and analyse objects in RAM.
 * <br><br><b>This class is not part of db4o.jar!</b><br>
 * It is delivered as sourcecode in the
 * path ../com/db4o/tools/<br><br>
 * 
 * @sharpen.ignore
 */
public class Logger {
	
	private static final int MAXIMUM_OBJECTS = 20;
	
	/**
	 * opens a database file and logs the content of a class to standard out.
	 * @param args expects [database filename] [fully qualified classname]
	 */
	public static void main(String[] args) {
		if(args == null || args.length == 0){
			System.out.println("Usage: java com.db4o.tools.Logger <database filename> <class>");
		}else{
			if(! new File(args[0]).exists()){
				System.out.println("A database file with the name '" + args[0] + "' does not exist.");
			}else{
				Configuration config = Db4o.newConfiguration();
				config.messageLevel(-1);
				ExtObjectContainer con = null;
				try{
					ObjectContainer c = Db4o.openFile(config, args[0]);
					if(c == null){
						throw new RuntimeException();
					}
					con = c.ext();
				}catch(Exception e){
					System.out.println("The database file '" + args[0] + "' could not be opened.");
					e.printStackTrace();
					return;
				}
				
				if(args.length > 1){
					StoredClass sc = con.storedClass(args[1]);
					if(sc == null){
						System.out.println("There is no stored class with the name '" + args[1] + "'.");
					}else{
						long[] ids = sc.getIDs();
						for (int i = 0; i < ids.length; i++) {
							if(i > MAXIMUM_OBJECTS){
								break;
							}
							Object obj = con.getByID(ids[i]);
							con.activate(obj, Integer.MAX_VALUE);
							log(con, obj);
						}
						msgCount(ids.length);
					}
				}else{
					ObjectSet set = con.queryByExample(null);
					int i = 0;
					while(set.hasNext()){
						Object obj = set.next();
						con.activate(obj, Integer.MAX_VALUE);
						log(con, obj);
						
						if(++i > MAXIMUM_OBJECTS){
							break;
						}
					}
					msgCount(set.size());
				}
				con.close();
				
			}
			
		}
	}
	
	/**
	 * logs the structure of an object.
	 * @param container the {@link ObjectContainer} to be used, or null
	 * to log any object.
	 * @param obj the object to be analysed.
	 */
	public static void log(ObjectContainer container, Object obj){
		if(obj == null){
			log("[NULL]");
		}else{
			log(obj.getClass().getName());
			log(container, obj, 0, new Collection4());
		}
	}
	
	/**
	 * logs the structure of an object.
	 * @param obj the object to be analysed.
	 */
	public static void log(Object obj){
		log(null, obj);
	}
	
	
	/**
	 * logs all objects in the passed ObjectContainer.
	 * @param container the {@link ObjectContainer} to be used.
	 */
	public static void logAll(ObjectContainer container){
		ObjectSet set = container.queryByExample(null);
		while(set.hasNext()){
			log(container, set.next());
		}
	}
	
	/**
	 * redirects output to a different PrintStream.
	 * @param ps the Printstream to be used.
	 */
	public static void setOut(java.io.PrintStream ps){
		out = ps;
	}
	
	/**
	 * limits logging to a maximum depth.
	 * @param depth the maximum depth.
	 */
	public static void setMaximumDepth(int depth){
		maximumDepth = depth;
	}
	
	private static void msgCount(int count){
		System.out.println("\n\nLog complete.\nObjects: " + count);
		if(count > MAXIMUM_OBJECTS){
			System.out.println("Displayed due to setting of " + Logger.class.getName() + "#MAXIMUM_OBJECTS: " + MAXIMUM_OBJECTS);
		}
	}
	

	private static void log(ObjectContainer a_container, Object a_object, int a_depth, Collection4 a_list){
		if (a_list.contains(a_object) || a_depth > maximumDepth) {
			return;
		}
		Class clazz = a_object.getClass();
		for (int i = 0; i < IGNORE.length; i++) {
			if(clazz.isAssignableFrom(IGNORE[i])){
				return;
			}
		}
		if(Platform4.isSimple(clazz)){
			log(a_depth + 1,a_object.getClass().getName(), a_object.toString());
			return;
		}
		
		a_list.add(a_object);
		
		Class[] classes = getClassHierarchy(a_object);

		String spaces = "";
		for(int i = classes.length - 1; i >= 0; i--){
			spaces = spaces + sp;

			String className = spaces;
			int pos = classes[i].getName().lastIndexOf(".");
			if(pos > 0){
				className += classes[i].getName().substring(pos);
			}else{
				className += classes[i].getName();
			}
			
			if(classes[i] == java.util.Date.class){
				String fieldName = className + ".getTime";
				Object obj = new Long(((java.util.Date)a_object).getTime());
				log(a_container, obj, fieldName, a_depth + 1, -1, a_list);
				
			}else{
				Field[] fields = classes[i].getDeclaredFields();
				for (int j = 0; j < fields.length; j++){
					Platform4.setAccessible(fields[j]);
					String fieldName = className + "." + fields[j].getName();
					try{
						Object obj = fields[j].get(a_object);
						if(obj.getClass().isArray()){
							obj = normalizeNArray(obj);
							int len = Array.getLength(obj);
							for (int k = 0 ; k < len; k ++){
								Object element = Array.get(obj,k);
								log(a_container, element,fieldName, a_depth + 1, k, a_list);
							}
						}else{
							log(a_container, obj, fieldName, a_depth + 1, -1, a_list);
						}
					}catch(Exception e){
					}
				}
			}
		}
	}

	private static void log(ObjectContainer a_container, Object a_object, String a_fieldName, int a_depth, int a_arrayElement, Collection4 a_list){
		if(a_depth > maximumDepth){
			return;
		}
		String fieldName = (a_arrayElement > -1) ? a_fieldName + sp + sp + a_arrayElement: a_fieldName;
		if(a_object != null){
			if((a_container == null) || a_container.ext().isStored(a_object)){
				if(a_container == null || a_container.ext().isActive(a_object)){
					log(a_depth, fieldName, "");
					Class clazz = a_object.getClass();
					boolean found = false;
					if(Platform4.isSimple(clazz)){
						log(a_depth + 1,a_object.getClass().getName(), a_object.toString());
						found = true;
					}
					if(!found){
						log(a_container, a_object, a_depth, a_list);
					}
				}else{
					log(a_depth, fieldName, "DEACTIVATED " + a_object.getClass().getName());
				}
				return;
			}
			log(a_depth, fieldName, a_object.toString());
		}else{
			log(a_depth, fieldName, "[NULL]");
		}
	}

    private static void log (String a_msg) {
		if(! silent){
			out.println(a_msg);
		}
    }

    private static void log (int indent, String a_property, String a_value) {
        for (int i = 0; i < indent; i++) {
            a_property = sp + sp + a_property;
        }
        log(a_property, a_value);
    }

    private static void log (String a_property, String a_value) {
        if (a_value == null)
            a_value = "[NULL]";
        log(a_property + ": " + a_value);
    }

	private static Class[] getClassHierarchy(Object a_object){
		Class[] classes = new Class[] {a_object.getClass()};
		return getClassHierarchy(classes);
	}

	private static Class[] getClassHierarchy(Class[] a_classes){
		Class clazz = a_classes[a_classes.length -1].getSuperclass();
		if(clazz.equals(Object.class)){
			return a_classes;
		}
		Class[] classes = new Class[a_classes.length + 1];
		System.arraycopy(a_classes,0,classes,0,a_classes.length);
		classes[a_classes.length] = clazz;
		return getClassHierarchy(classes);
	}
	
	private static Object normalizeNArray(Object a_object) {
		if (Array.getLength(a_object) > 0) {
			Object first = Array.get(a_object, 0);
			if (first != null && first.getClass().isArray()) {
				int dim[] = arrayDimensions(a_object);
				Object all = new Object[arrayElementCount(dim)];
				normalizeNArray1(a_object, all, 0, dim, 0);
				return all;
			}
		}
		return a_object;
	}

	private static int normalizeNArray1(
		Object a_object,
		Object a_all,
		int a_next,
		int a_dim[],
		int a_index) {
		if (a_index == a_dim.length - 1) {
			for (int i = 0; i < a_dim[a_index]; i++) {
				Array.set(a_all, a_next++, Array.get(a_object, i));
			}
		} else {
			for (int i = 0; i < a_dim[a_index]; i++) {
				a_next =
					normalizeNArray1(
						Array.get(a_object, i),
						a_all,
						a_next,
						a_dim,
						a_index + 1);
			}

		}
		return a_next;
	}
	
	private static int[] arrayDimensions(Object a_object) {
		int count = 0;
		for (Class clazz = a_object.getClass();
			clazz.isArray();
			clazz = clazz.getComponentType()) {
			count++;
		}
		int dim[] = new int[count];
		for (int i = 0; i < count; i++) {
			dim[i] = Array.getLength(a_object);
			a_object = Array.get(a_object, 0);
		}
		return dim;
	}
	
	private static int arrayElementCount(int a_dim[]) {
		int elements = a_dim[0];
		for (int i = 1; i < a_dim.length; i++) {
			elements *= a_dim[i];
		}
		return elements;
	}
	
	private static final Class[] IGNORE ={
		Class.class
	};
	
	private static int maximumDepth = Integer.MAX_VALUE;
	private static java.io.PrintStream out = System.out;
	private static String sp = " ";
	private static boolean silent;
	
	/**
	 * static use only
	 */
	private Logger(){
	}
}
