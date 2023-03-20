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
public class IntArrayList implements Iterable4 {
    
    protected int[] i_content;
    
    private int i_count;
    
    public IntArrayList(){
        this(10);
    }
    
    public IntArrayList(int initialSize){
        i_content = new int[initialSize];
    }
    
    public void add(int a_value){
        ensureCapacity();
        i_content[i_count++] = a_value;
    }
        
    public void add(int index, int a_value) {
        ensureCapacity();
        System.arraycopy(i_content, index, i_content, index + 1, i_count - index);
        i_content[index] = a_value;
        i_count++;
    }
    
    private void ensureCapacity() {
        if(i_count >= i_content.length){
            int inc = i_content.length / 2;
            if(inc < 10){
                inc = 10;
            }
            int[] temp = new int[i_content.length + inc];
            System.arraycopy(i_content, 0, temp, 0, i_content.length);
            i_content = temp;
        }
    }

    public int indexOf(int a_value) {
        for (int i = 0; i < i_count; i++) {
            if (i_content[i] == a_value){
                return i;
            }
        }
        return -1;
    }
    
    public int size(){
        return i_count;
    }
    
    public long[] asLong(){
        long[] longs = new long[i_count];
        for (int i = 0; i < i_count; i++) {
            longs[i] = i_content[i]; 
        }
        return longs;
    }

	public IntIterator4 intIterator() {
		return IntIterators.forInts(i_content, i_count);
	}
	
	public Iterator4 iterator() {
		return intIterator();
	}
	
	public int get(int index) {
		return i_content[index];
	}
	
	public void swap(int left, int right) {
		if(left!=right) {
			int swap=i_content[left];
			i_content[left]=i_content[right];
			i_content[right]=swap;
		}
	}

}
