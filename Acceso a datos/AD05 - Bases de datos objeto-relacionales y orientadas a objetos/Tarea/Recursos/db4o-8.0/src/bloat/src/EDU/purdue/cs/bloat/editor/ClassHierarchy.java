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

import java.io.*;
import java.util.*;

import EDU.purdue.cs.bloat.reflect.*;
import EDU.purdue.cs.bloat.util.*;

/**
 * ClassHierarchy maintains a graph of the subclass relationships of the classes
 * loaded by the ClassInfoLoader.
 * 
 * @see ClassInfoLoader
 */
public class ClassHierarchy {
	public static final Type POS_SHORT = Type.getType("L+short!;");

	public static final Type POS_BYTE = Type.getType("L+byte!;");

	static final int MAX_INT = 8;

	static final int MAX_SHORT = 7;

	static final int MAX_CHAR = 6;

	static final int MAX_BYTE = 5;

	static final int MAX_BOOL = 4;

	static final int MIN_CHAR = 3;

	static final int MIN_BOOL = 3;

	static final int ZERO = 3;

	static final int MIN_BYTE = 2;

	static final int MIN_SHORT = 1;

	static final int MIN_INT = 0;

	public static boolean DEBUG = false;

	public static boolean RELAX = false;

	Set classes; // The Types of the classes in hierarchy

	Graph extendsGraph; // "Who extends who" graph

	Graph implementsGraph; // "Who implements what" graph

	boolean closure; // Do we visit all referenced classes?

	// Maps methods to the methods they may resolve to
	private Map resolvesToCache;

	// These are only needed during construction.
	EditorContext context;

	LinkedList worklist;

	Set inWorklist;

	private void db(final String s) {
		if (ClassHierarchy.DEBUG) {
			System.out.println(s);
		}
	}

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            The context in which to access an <Tt>Editor</tt> and other
	 *            such things.
	 * @param initial
	 *            The names of the classes that initially constitue the
	 *            hierarchy.
	 * @param closure
	 *            Do we get the maximum amount of class information?
	 */
	public ClassHierarchy(final EditorContext context,
			final Collection initial, final boolean closure) {
		this.context = context;
		this.closure = closure;

		classes = new HashSet();
		extendsGraph = new Graph();
		implementsGraph = new Graph();

		worklist = new LinkedList();
		inWorklist = new HashSet();

		this.resolvesToCache = new HashMap();

		// Need new ArrayList to avoid ConcurrentModificationException
		final Iterator iter = new ArrayList(initial).iterator();

		while (iter.hasNext()) {
			final String name = (String) iter.next();
			addClass(name);
		}
	}

	/**
	 * Adds a class of a given name to the ClassHierarchy.
	 */
	public void addClassNamed(final String name) {
		addClass(name);
	}

	/**
	 * Returns the immediate subclasses of a given <tt>Type</tt> as a
	 * <tt>Collection</tt> of <tt>Type</tt>s.
	 * 
	 * <p>
	 * 
	 * The subclass relationship at the classfile level is a little screwy with
	 * respect to interfaces. An interface that extends another interface is
	 * compiled into an interface that extends java.lang.Object and implements
	 * the superinterface. As a result, the interface-subinterface is not
	 * captured in <tt>subclasses</tt> as one may expect. Instead, you have to
	 * look at <tt>implementors</tt> and filter out the classes.
	 */
	public Collection subclasses(final Type type) {
		final TypeNode node = getExtendsNode(type);

		if (node != null) {
			final List list = new ArrayList(extendsGraph.preds(node));

			final ListIterator iter = list.listIterator();

			while (iter.hasNext()) {
				final TypeNode v = (TypeNode) iter.next();
				iter.set(v.type);
			}

			return list;
		}

		return new ArrayList();
	}

	/**
	 * Returns the superclass of a given <tt>Type</tt>. If the <tt>Type</tt>
	 * has no superclass (that is it is <tt>Type.OBJECT</tt>), then null is
	 * returned.
	 */
	public Type superclass(final Type type) {
		final TypeNode node = getExtendsNode(type);

		if (node != null) {
			final Collection succs = extendsGraph.succs(node);

			final Iterator iter = succs.iterator();

			if (iter.hasNext()) {
				final TypeNode v = (TypeNode) iter.next();
				return v.type;
			}
		}

		return null;
	}

	/**
	 * Returns the interfaces that a given <tt>Type</tt> implements as a
	 * <tt>Collection</tt> of <tt>Types</tt>
	 */
	public Collection interfaces(final Type type) {
		final TypeNode node = getImplementsNode(type);

		if (node != null) {
			final List list = new ArrayList(implementsGraph.succs(node));

			final ListIterator iter = list.listIterator();

			while (iter.hasNext()) {
				final TypeNode v = (TypeNode) iter.next();
				iter.set(v.type);
			}

			return list;
		}

		return new ArrayList();
	}

	/**
	 * Returns the classes (<tt>Type</tt>s) that implement a given interface
	 * as a <tt>Collection</tt> of <Tt>Type</tt>s.
	 * 
	 * <p>
	 * 
	 * See note in <tt>subclasses</tt> for information about the interface
	 * hierarchy.
	 */
	public Collection implementors(final Type type) {
		final TypeNode node = getImplementsNode(type);

		if (node != null) {
			final List list = new ArrayList(implementsGraph.preds(node));

			final ListIterator iter = list.listIterator();

			while (iter.hasNext()) {
				final TypeNode v = (TypeNode) iter.next();
				iter.set(v.type);
			}

			return list;
		}

		return new ArrayList();
	}

	/**
	 * Returns whether or not a is a subclass of b.
	 */
	public boolean subclassOf(final Type a, final Type b) {
		// Is a <= b?
		Assert.isTrue(a.isReference() && b.isReference(), "Cannot compare " + a
				+ " and " + b);

		// a <= a: true
		if (a.equals(b)) {
			return true;
		}

		// a <= java.lang.Object: true
		if (b.equals(Type.OBJECT)) {
			return true;
		}

		// null <= null: true
		// a <= null: false
		if (b.isNull()) {
			return a.isNull();
		}

		if (a.isArray()) {
			if (b.isArray()) {
				// Both reference arrays.
				// a <= b -> a[] <= b[]
				if (a.elementType().isReference()
						&& b.elementType().isReference()) {

					return subclassOf(a.elementType(), b.elementType());
				}

				// a[] <= a[]: true
				return a.elementType().equals(b.elementType());
			}

			// Only one is an array (and b is not Object--tested above).
			return false;
		}

		// a <= b[]: false
		if (b.isArray()) {
			// Only one is an array.
			return false;
		}

		// Neither is an array. Look at all of the superclasses of a. If
		// one of those superclasses is b, then a is a subclass of b.
		for (Type t = a; t != null; t = superclass(t)) {
			if (t.equals(b)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns (the <tt>Type</tt>s of) all of the classes and interfaces in
	 * the hierarchy.
	 */
	public Collection classes() {
		Assert.isTrue(classes != null);
		return classes;
	}

	/**
	 * Returns <tt>true</tt> if class closure has been computed
	 */
	public boolean closure() {
		return (this.closure);
	}

	/**
	 * Obtains a node from the extends graph. If it is not in the graph, we try
	 * to "bring it in".
	 */
	private TypeNode getExtendsNode(final Type type) {
		final GraphNode node = extendsGraph.getNode(type);

		if ((node == null) && type.isObject()) {
			this.addClassNamed(type.className());
		}

		return ((TypeNode) extendsGraph.getNode(type));
	}

	/**
	 * Obtains a node from the class graph. If it is not in the graph, we try to
	 * "bring it in".
	 */
	private TypeNode getImplementsNode(final Type type) {
		final GraphNode node = implementsGraph.getNode(type);

		if ((node == null) && type.isObject()) {
			this.addClassNamed(type.className());
		}

		return ((TypeNode) implementsGraph.getNode(type));
	}

	/**
	 * Adds a type (and all types it references) to the extends and implements
	 * graphs.
	 */
	private void addClass(final String name) {
		Type type = Type.getType(Type.classDescriptor(name));

		if (classes.contains(type)) {
			return;
		}

		if (inWorklist.contains(type)) {
			return;
		}

		db("ClassHierarchy: Adding " + name + " to hierarchy");

		worklist.add(type);
		inWorklist.add(type);

		while (!worklist.isEmpty()) {
			type = (Type) worklist.removeFirst();
			inWorklist.remove(type);

			if (classes.contains(type)) {
				continue;
			}

			classes.add(type);

			// Add a node in the extends graph for the type of interest
			TypeNode extendsNode = getExtendsNode(type);

			if (extendsNode == null) {
				// Add a new node to the class graph
				extendsNode = new TypeNode(type);
				extendsGraph.addNode(type, extendsNode);
			}

			// TypeNode implementsNode = (TypeNode) getImplementsNode(type);

			// if (implementsNode == null) {
			// // Add a new node to the interface graph
			// implementsNode = new TypeNode(type);
			// implementsGraph.addNode(type, implementsNode);
			// }

			// Obtain a ClassEditor for the class
			ClassEditor c;

			try {
				c = context.editClass(type.className());

			} catch (final ClassNotFoundException ex) {
				if (ClassHierarchy.RELAX) {
					continue;
				}

				throw new RuntimeException("Class not found: "
						+ ex.getMessage());
			}

			final Type[] interfaces = c.interfaces();

			if (c.superclass() != null) {
				// Add an edge from the superclass to the class in the extends
				// graph.

				if (!c.isInterface() || (interfaces.length == 0)) {
					// Ignore interfaces that implement (really extend, see
					// below) other interfaces. This way interfaces are put in
					// the extends graph twice.

					TypeNode superNode = getExtendsNode(c.superclass());

					if (superNode == null) {
						superNode = new TypeNode(c.superclass());
						extendsGraph.addNode(c.superclass(), superNode);
					}

					// Make sure that we're not making java.lang.Object a
					// superclass of itself. We assume that the java.lang.Object
					// has no successors in the extendsGraph.
					if (!extendsNode.type.equals(Type.OBJECT)) {
						extendsGraph.addEdge(extendsNode, superNode);
					}
				}

			} else {
				// Only java.lang.Object has no superclass
				if (!type.equals(Type.OBJECT) && !ClassHierarchy.RELAX) {
					throw new RuntimeException("Null superclass for " + type);
				}
			}

			// Consider the interfaces c implements
			if (c.isInterface()) {
				// Interfaces that extend other interfaces are compiled into
				// classes that implement those other interfaces. So,
				// interfaces that implement other interfaces really extend
				// them. Yes, this makes the extends graph an inverted tree.
				for (int i = 0; i < interfaces.length; i++) {
					final Type iType = interfaces[i];
					TypeNode iNode = getExtendsNode(iType);
					if (iNode == null) {
						iNode = new TypeNode(iType);
						extendsGraph.addNode(iType, iNode);
					}
					extendsGraph.addEdge(extendsNode, iNode);
				}

			} else {
				// Class c implements its interfaces
				TypeNode implementsNode = null;
				if (interfaces.length > 0) {
					implementsNode = getImplementsNode(type);
					if (implementsNode == null) {
						implementsNode = new TypeNode(type);
						implementsGraph.addNode(type, implementsNode);
					}
				}
				for (int i = 0; i < interfaces.length; i++) {
					final Type iType = interfaces[i];
					TypeNode iNode = getImplementsNode(iType);
					if (iNode == null) {
						iNode = new TypeNode(iType);
						implementsGraph.addNode(iType, iNode);
					}
					implementsGraph.addEdge(implementsNode, iNode);
				}
			}

			if (c.superclass() != null) {
				// Add the super type to the worklist

				// db("typeref " + type + " -> " + c.superclass());

				addType(c.superclass());
			}

			for (int i = 0; i < c.interfaces().length; i++) {
				// Add all of the interface types to the worklist

				// db("typeref " + type + " -> " + c.interfaces()[i]);

				addType(c.interfaces()[i]);
			}

			if (!this.closure) {
				context.release(c.classInfo());
				continue;
			}

			for (int i = 0; i < c.methods().length; i++) {
				// TODO: Add an enumeration to ClassEditor to get this list.

				// Add all of the methods types. Actually, we only add the
				// type involved with the methods.
				final MethodInfo m = c.methods()[i];
				final int typeIndex = m.typeIndex();

				final String desc = (String) c.constants()
						.constantAt(typeIndex);
				final Type t = Type.getType(desc);

				// db("typeref " + type + " -> " + t);

				addType(t);
			}

			for (int i = 0; i < c.fields().length; i++) {
				// Add the types of all of the fields

				final FieldInfo f = c.fields()[i];
				final int typeIndex = f.typeIndex();

				final String desc = (String) c.constants()
						.constantAt(typeIndex);
				final Type t = Type.getType(desc);

				// db("typeref " + type + " -> " + t);

				addType(t);
			}

			for (int i = 0; i < c.constants().numConstants(); i++) {
				// Look through the constant pool for interesting (non-LONG
				// and non-DOUBLE) constants and add them to the worklist.

				final int tag = c.constants().constantTag(i);

				if ((tag == Constant.LONG) || (tag == Constant.DOUBLE)) {
					i++;

				} else if (tag == Constant.CLASS) {
					final Type t = (Type) c.constants().constantAt(i);

					// db("typeref " + type + " -> " + t);

					addType(t);

				} else if (tag == Constant.NAME_AND_TYPE) {
					final NameAndType t = (NameAndType) c.constants()
							.constantAt(i);

					// db("typeref " + type + " -> " + t.type());

					addType(t.type());
				}
			}

			// We're done editing the class
			context.release(c.classInfo());

		}

		/*
		 * // Now that we've entered the class (and at least all of its //
		 * subclasses) into the hierarchy, notify superclasses that // they've
		 * been subclassses. This will invalidate the TypeNodes // in the
		 * dependence graph. DependenceGraph dg =
		 * BloatContext.getContext().dependenceGraph();
		 * 
		 * for(Type superclass = superclass(type); superclass != null;
		 * superclass = superclass(superclass)) { db("ClassHierarchy:
		 * Invalidating superclass " + superclass);
		 * 
		 * EDU.purdue.cs.bloat.depend.TypeNode typeNode =
		 * dg.getTypeNode(superclass); typeNode.invalidate(); }
		 */
	}

	// Adds a Type to the worklist. If the type is a method, then all
	// of the parameters types and the return types are added.
	private void addType(final Type type) {
		if (type.isMethod()) {
			// Add all of the types of the parameters and the return type

			final Type[] paramTypes = type.paramTypes();

			for (int i = 0; i < paramTypes.length; i++) {
				// db("typeref " + type + " -> " + paramTypes[i]);

				addType(paramTypes[i]);
			}

			final Type returnType = type.returnType();

			// db("typeref " + type + " -> " + returnType);

			addType(returnType);

			return;
		}

		if (type.isArray()) {
			// TODO: Add the supertypes of the array and make it implement
			// SERIALIZABLE and CLONEABLE. Since we're only concerned with
			// fields and since arrays have no fields, we can ignore this
			// for now.

			// db("typeref " + type + " -> " + type.elementType());

			addType(type.elementType());

			return;
		}

		if (!type.isObject()) {
			return;
		}

		if (classes.contains(type)) {
			return;
		}

		if (inWorklist.contains(type)) {
			return;
		}

		worklist.add(type);
		inWorklist.add(type);
	}

	/**
	 * Returns the intersection of two types. Basically, the interstion of two
	 * types is the type (if any) to which both types may be assigned. So, if a
	 * is a subtype of b, a is returned. Otherwise, <tt>Type.NULL</tt> is
	 * returned.
	 */
	public Type intersectType(final Type a, final Type b) {
		Assert.isTrue(a.isReference() && b.isReference(), "Cannot intersect "
				+ a + " and " + b);

		if (a.equals(b)) {
			return a;
		}

		if (a.isNull() || b.isNull()) {
			return Type.NULL;
		}

		if (a.equals(Type.OBJECT)) {
			return b;
		}

		if (b.equals(Type.OBJECT)) {
			return a;
		}

		if (a.isArray()) {
			if (b.isArray()) {
				// Both reference arrays.
				if (a.elementType().isReference()
						&& b.elementType().isReference()) {

					final Type t = intersectType(a.elementType(), b
							.elementType());

					if (t.isNull()) {
						return Type.NULL;
					}

					return t.arrayType();
				}

				// Only one is a reference array.
				if (a.elementType().isReference()
						|| b.elementType().isReference()) {
					return Type.NULL;
				}

				// Both primitive arrays, not equal.
				return Type.NULL;
			}

			// Only one is an array.
			return Type.NULL;
		}

		if (b.isArray()) {
			// Only one is an array.
			return Type.NULL;
		}

		// Neither is an array.

		for (Type t = a; t != null; t = superclass(t)) {
			if (t.equals(b)) {
				// If a is a subtype of b, then return a.
				return a;
			}
		}

		for (Type t = b; t != null; t = superclass(t)) {
			if (t.equals(a)) {
				// If b is a subtype of a, then return b
				return b;
			}
		}

		return Type.NULL;
	}

	/**
	 * Returns the most refined common supertype for a bunch of <tt>Type</tt>s.
	 */
	public Type unionTypes(final Collection types) {
		if (types.size() <= 0) {
			return (Type.OBJECT);
		}

		final Iterator ts = types.iterator();
		Type type = (Type) ts.next();

		while (ts.hasNext()) {
			type = this.unionType(type, (Type) ts.next());
		}

		return (type);
	}

	/**
	 * Returns the union of two types. The union of two types is their most
	 * refined common supertype. At worst, the union is <tt>Type.OBJECT</tt>
	 */
	public Type unionType(final Type a, final Type b) {

		if (a.equals(b)) {
			return a;
		}

		if (a.equals(Type.OBJECT) || b.equals(Type.OBJECT)) {
			return Type.OBJECT;
		}

		if (a.isNull()) {
			return b;
		}

		if (b.isNull()) {
			return a;
		}

		// Handle funky integral types introduced during type inferencing.
		if ((a.isIntegral() || a.equals(ClassHierarchy.POS_BYTE) || a
				.equals(ClassHierarchy.POS_SHORT))
				&& (b.isIntegral() || b.equals(ClassHierarchy.POS_BYTE) || b
						.equals(ClassHierarchy.POS_SHORT))) {

			final BitSet v1 = ClassHierarchy.typeToSet(a);
			final BitSet v2 = ClassHierarchy.typeToSet(b);
			v1.or(v2);
			return (ClassHierarchy.setToType(v1));
		}

		Assert.isTrue(a.isReference() && b.isReference(), "Cannot union " + a
				+ " and " + b);

		if (a.isArray()) {
			if (b.isArray()) {
				// Both reference arrays.
				if (a.elementType().isReference()
						&& b.elementType().isReference()) {

					final Type t = unionType(a.elementType(), b.elementType());
					return t.arrayType();
				}

				// Only one is a reference array.
				if (a.elementType().isReference()
						|| b.elementType().isReference()) {
					return Type.OBJECT;
				}

				// Both primitive arrays, not equal.
				return Type.OBJECT;
			}

			// Only one is an array.
			return Type.OBJECT;
		}

		if (b.isArray()) {
			// Only one is an array.
			return Type.OBJECT;
		}

		// Neither is an array.
		final Set superOfA = new HashSet();
		final Set superOfB = new HashSet();

		for (Type t = a; t != null; t = superclass(t)) {
			// if(TypeInference.DEBUG)
			// System.out.println(" Superclass of " + a + " is " + t);
			superOfA.add(t);
		}

		for (Type t = b; t != null; t = superclass(t)) {
			if (superOfA.contains(t)) {
				return t;
			}

			// if(TypeInference.DEBUG)
			// System.out.println(" Superclass of " + b + " is " + t);

			superOfB.add(t);
		}

		// if(TypeInference.DEBUG) {
		// System.out.println("Superclasses of A: " + superOfA);
		// System.out.println("Superclasses of B: " + superOfB);
		// }

		for (Type t = a; t != null; t = superclass(t)) {
			if (superOfB.contains(t)) {
				// Found a common superclass...
				return t;
			}
		}

		throw new RuntimeException("No common super type for " + a + " ("
				+ superOfA + ")" + " and " + b + " (" + superOfB + ")");
	}

	class TypeNode extends GraphNode {
		Type type;

		public TypeNode(final Type type) {
			// if(DEBUG)
			// System.out.println("Creating TypeNode for: " + type);
			this.type = type;
		}

		public String toString() {
			return ("[" + type + "]");
		}
	}

	/**
	 * Prints the class hierarchy (i.e. the "extends" hierarchy, interfaces may
	 * extends other interfaces) to a <tt>PrintWriter</tt>.
	 */
	public void printClasses(final PrintWriter out, final int indent) {
		final TypeNode objectNode = this.getExtendsNode(Type.OBJECT);
		indent(out, indent);
		out.println(objectNode.type);
		printSubclasses(objectNode.type, out, true, indent + 2);
	}

	/**
	 * Prints the implements hierarchy to a <tt>PrintWriter</tt>.
	 */
	public void printImplements(final PrintWriter out, int indent) {
		// There are multiple roots to the implements graph.
		indent += 2;
		final Iterator roots = this.implementsGraph.roots().iterator();
		while (roots.hasNext()) {
			final TypeNode iNode = (TypeNode) roots.next();
			indent(out, indent);
			out.println(iNode.type);
			printImplementors(iNode.type, out, true, indent + 2);
		}
	}

	/**
	 * Print the implementors of a given interface. Do we even have more than
	 * one level?
	 */
	private void printImplementors(final Type iType, final PrintWriter out,
			final boolean recurse, final int indent) {
		final Iterator implementors = this.implementors(iType).iterator();
		while (implementors.hasNext()) {
			final Type implementor = (Type) implementors.next();
			indent(out, indent);
			out.println(implementor);
			if (recurse) {
				printImplementors(implementor, out, recurse, indent + 2);
			}
		}
	}

	/**
	 * Prints a bunch of spaces to a PrintWriter.
	 */
	private void indent(final PrintWriter out, final int indent) {
		for (int i = 0; i < indent; i++) {
			out.print(" ");
		}
	}

	/**
	 * Prints the subclasses of a given to a PrintWriter.
	 * 
	 * @param recurse
	 *            Are all subclasses printed or only direct ones?
	 */
	private void printSubclasses(final Type classType, final PrintWriter out,
			final boolean recurse, final int indent) {
		final Iterator iter = this.subclasses(classType).iterator();
		while (iter.hasNext()) {
			final Type subclass = (Type) iter.next();
			indent(out, indent);
			out.println(subclass);
			if (recurse) {
				printSubclasses(subclass, out, recurse, indent + 2);
			}

		}
	}

	/**
	 * Determines whether or not a class's method is overriden by any of its
	 * subclasses.
	 */
	public boolean methodIsOverridden(final Type classType,
			final NameAndType nat) {
		final String methodName = nat.name();
		final Type methodType = nat.type();

		db("ClassHierarchy: Is " + classType + "." + methodName + methodType
				+ " overridden?");

		final Collection subclasses = this.subclasses(classType);

		final Iterator iter = subclasses.iterator();
		while (iter.hasNext()) {
			final Type subclass = (Type) iter.next();

			db("Examining subclass " + subclass);

			// Obtain a ClassEditor for the class
			ClassEditor ce = null;

			try {
				ce = context.editClass(subclass.className());

			} catch (final ClassNotFoundException ex) {
				db(ex.getMessage());
				return (true);
			}

			// Examine each method in the subclass
			final MethodInfo[] methods = ce.methods();
			for (int i = 0; i < methods.length; i++) {
				final MethodEditor me = context.editMethod(methods[i]);
				if (me.name().equals(methodName)
						&& me.type().equals(methodType)) {
					db("  " + methodName + methodType + " is overridden by "
							+ me.name() + me.type());
					context.release(ce.classInfo());
					return (true); // Method is overridden
				}
			}

			// Recurse over subclasses
			if (methodIsOverridden(subclass, nat)) {
				context.release(ce.classInfo());
				return (true);
			}

			context.release(ce.classInfo());
		}

		// Got through all subclasses and method was not overridden
		db("  NO!");

		return (false);
	}

	/**
	 * Returns the <tt>MemberRef</tt> of the method that would be invoked if a
	 * given method of a given type was invoked. Basically, dynamic dispatch is
	 * simulated.
	 */
	public MemberRef methodInvoked(final Type receiver, final NameAndType method) {
		// Search up class hierarchy for a class that implements the
		// method
		for (Type type = receiver; type != null; type = superclass(type)) {
			// Construct a MemberRef for the possible method
			final MemberRef m = new MemberRef(type, method);
			try {
				context.editMethod(m);
				return (m);

			} catch (final NoSuchMethodException ex) {
				continue; // Try superclass
			}
		}

		// Hmm. No superclass method was found!
		throw new IllegalArgumentException("No implementation of " + receiver
				+ "." + method);
	}

	/**
	 * Given a set of bits representing the range of values some type has,
	 * determines what that Type is.
	 */
	public static Type setToType(final BitSet v) {
		if (v.get(ClassHierarchy.MAX_INT)) {
			return Type.INTEGER;
		}

		if (v.get(ClassHierarchy.MAX_CHAR)) {
			if (v.get(ClassHierarchy.MIN_INT)
					|| v.get(ClassHierarchy.MIN_SHORT)
					|| v.get(ClassHierarchy.MIN_BYTE)) {
				return Type.INTEGER;

			} else {
				return Type.CHARACTER;
			}
		}

		if (v.get(ClassHierarchy.MAX_SHORT)) {
			if (v.get(ClassHierarchy.MIN_INT)) {
				return Type.INTEGER;

			} else if (v.get(ClassHierarchy.MIN_SHORT)
					|| v.get(ClassHierarchy.MIN_BYTE)) {
				return Type.SHORT;

			} else {
				return ClassHierarchy.POS_SHORT;
			}
		}

		if (v.get(ClassHierarchy.MAX_BYTE)) {
			if (v.get(ClassHierarchy.MIN_INT)) {
				return Type.INTEGER;

			} else if (v.get(ClassHierarchy.MIN_SHORT)) {
				return Type.SHORT;

			} else if (v.get(ClassHierarchy.MIN_BYTE)) {
				return Type.BYTE;

			} else {
				return ClassHierarchy.POS_BYTE;
			}
		}

		if (v.get(ClassHierarchy.MAX_BOOL)) {
			if (v.get(ClassHierarchy.MIN_INT)) {
				return Type.INTEGER;

			} else if (v.get(ClassHierarchy.MIN_SHORT)) {
				return Type.SHORT;

			} else if (v.get(ClassHierarchy.MIN_BYTE)) {
				return Type.BYTE;

			} else {
				return Type.BOOLEAN;
			}
		}

		if (v.get(ClassHierarchy.MIN_INT)) {
			return Type.INTEGER;
		}

		if (v.get(ClassHierarchy.MIN_SHORT)) {
			return Type.SHORT;
		}

		if (v.get(ClassHierarchy.MIN_BYTE)) {
			return Type.BYTE;
		}

		return Type.BOOLEAN;
	}

	/**
	 * Returns a BitSet representing the possible values of a given integral
	 * type.
	 */
	public static BitSet typeToSet(final Type type) {
		final BitSet v = new BitSet(ClassHierarchy.MAX_INT);
		int lo;
		int hi;

		if (type.equals(Type.INTEGER)) {
			lo = ClassHierarchy.MIN_INT;
			hi = ClassHierarchy.MAX_INT;

		} else if (type.equals(Type.CHARACTER)) {
			lo = ClassHierarchy.MIN_CHAR;
			hi = ClassHierarchy.MAX_CHAR;

		} else if (type.equals(Type.SHORT)) {
			lo = ClassHierarchy.MIN_SHORT;
			hi = ClassHierarchy.MAX_SHORT;

		} else if (type.equals(ClassHierarchy.POS_SHORT)) {
			lo = ClassHierarchy.ZERO;
			hi = ClassHierarchy.MAX_SHORT;

		} else if (type.equals(Type.BYTE)) {
			lo = ClassHierarchy.MIN_BYTE;
			hi = ClassHierarchy.MAX_BYTE;

		} else if (type.equals(ClassHierarchy.POS_BYTE)) {
			lo = ClassHierarchy.ZERO;
			hi = ClassHierarchy.MAX_BYTE;

		} else if (type.equals(Type.BOOLEAN)) {
			lo = ClassHierarchy.ZERO;
			hi = ClassHierarchy.MAX_BOOL;

		} else {
			throw new RuntimeException();
		}

		for (int i = lo; i <= hi; i++) {
			v.set(i);
		}

		return v;
	}

	/**
	 * Represents a method and a set of <tt>Type</tt>s. When the method is
	 * invoked on a receiver of any of these types, the method will resolve to
	 * that method.
	 */
	public class ResolvesToWith {
		/**
		 * The method to which a call resolves
		 */
		public MemberRef method;

		/**
		 * The types with which the call resolves to the above method
		 */
		public HashSet rTypes;
	}

	/**
	 * Returns a set of <tt>ResolvesToWith</tt> that represent all subclass
	 * methods that override a given method and the subclasses that when used as
	 * receivers resolve to that method.
	 * 
	 * @see ResolvesToWith
	 */
	public Set resolvesToWith(final MemberRef method) {
		Set resolvesTo = (Set) this.resolvesToCache.get(method);

		if (resolvesTo == null) {
			db("Resolving " + method);

			resolvesTo = new HashSet(); // All methods it could resolve to
			final ResolvesToWith rtw = new ResolvesToWith();
			rtw.method = method;
			rtw.rTypes = new HashSet();

			// Remember that the method may be abstract, so the declaring
			// class is not necessarily a resolving type.

			// Basically, we go down the class and/or interface hierarchies
			// looking for concrete implementations of this method.
			MethodEditor me = null;
			try {
				me = context.editMethod(method);

			} catch (final NoSuchMethodException ex1) {
				// A method may not necessarily be implemented by its
				// declaring class. For instance, an abstract class that
				// implements an interface need not implement every method of
				// the interface. Really?
				db("  Hmm. Method is not implemented in declaring class");
			}

			// If the method is static or is a constructor, then it can only
			// resolve to itself.
			if ((me != null) && (me.isStatic() || me.isConstructor())) {
				rtw.rTypes.add(method.declaringClass());
				resolvesTo.add(rtw);
				db("  Static method or constructor, resolves to itself");

			} else {
				// Now let's play with types. Examine every type that could
				// implement this method. If it does, add it to the resolvesTo
				// set. Make sure to take things like interfaces into account.
				// When we find a overriding method, make a recursive call so
				// we'll have that information in the cache.
				final List types = new LinkedList();
				types.add(method.declaringClass());
				while (!types.isEmpty()) {
					final Type type = (Type) types.remove(0);

					db("  Examining type " + type);

					ClassEditor ce = null;
					try {
						ce = context.editClass(type);

					} catch (final ClassNotFoundException ex1) {
						System.err.println("** Class not found: "
								+ ex1.getMessage());
						ex1.printStackTrace(System.err);
						System.exit(1);
					}

					if (ce.isInterface()) {
						// Consider all subinterfaces of this interface and all
						// classes that implement this interface.
						final Iterator subinterfaces = this.subclasses(type)
								.iterator();
						while (subinterfaces.hasNext()) {
							final Type subinterface = (Type) subinterfaces
									.next();
							types.add(subinterface);
							db("  Noting subinterface " + subinterface);
						}

						final Iterator implementors = this.implementors(type)
								.iterator();
						while (implementors.hasNext()) {
							final Type implementor = (Type) implementors.next();
							types.add(implementor);
							db("  Noting implementor " + implementor);
						}

					} else {
						// We've got a class. Does it override the method?
						final NameAndType nat = method.nameAndType();
						final MethodInfo[] methods = ce.methods();
						boolean overridden = false;
						for (int i = 0; i < methods.length; i++) {
							final MethodEditor over = context
									.editMethod(methods[i]);
							final MemberRef ref = over.memberRef();
							if (ref.nameAndType().equals(nat)) {
								// This class implements the method.
								if (!method.declaringClass().equals(type)) {
									// Make a recursive call.
									db("  Class " + type + " overrides "
											+ method);
									resolvesTo.addAll(resolvesToWith(ref));
									overridden = true;
								}
							}
						}

						if (!overridden) {
							db("  " + rtw.method + " called with " + type);
							rtw.rTypes.add(type);
							resolvesTo.add(rtw);

							// Examine all subclasses of this class. They may
							// override
							// the method also.
							final Iterator subclasses = this.subclasses(type)
									.iterator();
							while (subclasses.hasNext()) {
								final Type subclass = (Type) subclasses.next();
								types.add(subclass);
								db("  Noting subclass " + subclass);
							}
						}
					}
				}
			}

			resolvesToCache.put(method, resolvesTo);
		}

		return (resolvesTo);
	}
}
