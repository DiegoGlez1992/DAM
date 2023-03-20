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
package EDU.purdue.cs.bloat.editor;

/**
 * MemberRef represents a method or field (as a <tt>NameAndType</tt>) and the
 * class (as a <tt>Type</tt>) in which it is declared.
 * 
 * @author Nate Nystrom (<a
 *         href="mailto:nystrom@cs.purdue.edu">nystrom@cs.purdue.edu</a>)
 */
public class MemberRef {
	private Type declaringClass;

	private NameAndType nameAndType;

	/**
	 * Constructor.
	 * 
	 * @param declaringClass
	 *            The type of the class which declared the member.
	 * @param nameAndType
	 *            The name and type of the member.
	 */
	public MemberRef(final Type declaringClass, final NameAndType nameAndType) {
		this.declaringClass = declaringClass;
		this.nameAndType = nameAndType;
	}

	/**
	 * Get the type of the class which declared the member.
	 * 
	 * @return The type of the class which declared the member.
	 */
	public Type declaringClass() {
		return declaringClass;
	}

	/**
	 * Get the name of the member.
	 * 
	 * @return The name of the member.
	 */
	public String name() {
		return nameAndType.name();
	}

	/**
	 * Get the type of the member.
	 * 
	 * @return The type of the member.
	 */
	public Type type() {
		return nameAndType.type();
	}

	/**
	 * Get the name and type of the member.
	 * 
	 * @return The name and type of the member.
	 */
	public NameAndType nameAndType() {
		return nameAndType;
	}

	/**
	 * Convert the reference to a string.
	 * 
	 * @return A string representation of the reference.
	 */
	public String toString() {
		// Take advantage of PRINT_TRUNCATED in Type
		final String className = declaringClass.toString();
		return "<" + (type().isMethod() ? "Method" : "Field") + " " + className
				+ "." + name() + " " + type() + ">";
	}

	/**
	 * Check if an object is equal to this reference.
	 * 
	 * @param obj
	 *            The object to compare against.
	 * @return true if equal, false if not.
	 */
	public boolean equals(final Object obj) {
		return (obj instanceof MemberRef)
				&& ((MemberRef) obj).declaringClass.equals(declaringClass)
				&& ((MemberRef) obj).nameAndType.equals(nameAndType);
	}

	/**
	 * Hash the member reference.
	 * 
	 * @return The hash code.
	 */
	public int hashCode() {
		return declaringClass.hashCode() ^ nameAndType.hashCode();
	}
}
