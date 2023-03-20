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
package com.db4o.internal;

import com.db4o.foundation.*;


/**
 * @exclude
 */
public final class TreeReader  
{
	private final Readable i_template;
	private final ByteArrayBuffer i_bytes;
	private int i_current = 0;
	private int i_levels = 0;
	private int i_size;
	private boolean i_orderOnRead;
	
	public TreeReader(ByteArrayBuffer a_bytes, Readable a_template) {
		this(a_bytes, a_template, false);
	}
	
	public TreeReader(ByteArrayBuffer a_bytes, Readable a_template, boolean a_orderOnRead) {
		i_template = a_template;
		i_bytes = a_bytes;
		i_orderOnRead = a_orderOnRead;
	}
	
	public Tree read() {
	    return read(i_bytes.readInt());
	}
	
	public Tree read(int a_size){
	    i_size = a_size;
		if(i_size > 0){
			if(i_orderOnRead){
				Tree tree = null;
				for (int i = 0; i < i_size; i++) {
				    tree = Tree.add(tree, (Tree)i_template.read(i_bytes));
                }
                return tree;
			}
			while ((1 << i_levels) < (i_size + 1)){
				i_levels ++;
			}
			return linkUp(null, i_levels);
		}
		return null;
	}
	
	private final Tree linkUp(Tree a_preceding, int a_level){
		Tree node = (Tree)i_template.read(i_bytes);
		i_current++;
		node._preceding = a_preceding;
		node._subsequent = linkDown(a_level + 1);
		node.calculateSize();
		if(i_current < i_size){
			return linkUp(node, a_level - 1);
		}
		return node;

	}

	private final Tree linkDown(int a_level){
		if(i_current < i_size){
			i_current++;
			if(a_level < i_levels) {
				Tree preceding = linkDown(a_level + 1);
				Tree node = (Tree)i_template.read(i_bytes);
				node._preceding = preceding;
				node._subsequent = linkDown(a_level + 1);
				node.calculateSize();
				return node;
			}
			return (Tree)i_template.read(i_bytes);
		}
		return null;
	}
}
