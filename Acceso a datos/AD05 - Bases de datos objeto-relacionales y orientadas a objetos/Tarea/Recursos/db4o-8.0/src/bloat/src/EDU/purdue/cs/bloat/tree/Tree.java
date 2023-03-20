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
package EDU.purdue.cs.bloat.tree;

import java.util.*;

import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.reflect.*;
import EDU.purdue.cs.bloat.util.*;

/**
 * Tree represents the expression tree of a basic Block. It consists of an
 * operand (expression) stack comprised of expressions and a list of statements
 * contained in the block.
 * 
 * @see Block
 * @see Expr
 * @see OperandStack
 * @see Stmt see StmtList
 */
public class Tree extends Node implements InstructionVisitor, Opcode {
	public static boolean DEBUG = false;

	public static boolean FLATTEN = false;

	public static boolean USE_STACK = true; // Do we use stack vars?

	public static boolean AUPDATE_FIX_HACK = false;

	public static boolean AUPDATE_FIX_HACK_CHANGED = false;

	public static boolean USE_PERSISTENT = false; // Insert UCExpr by default

	Block block; // Block that is represented by this Tree

	Subroutine sub; // The Subroutine that we're currently in

	Block next; // The Block after this one

	OperandStack stack; // The program stack for this block

	StmtList stmts; // The statements in the basic block

	Stack savedStack;

	static int stackpos = 0;

	boolean saveValue; // Do we push a StoreExpr on operand stack?

	// Some dup instruction combinations cause saveStack to generate
	// temporaries that clobber other temporaries. So, we use nextindex
	// to ensure that a uniquely-name temporary is created by
	// saveStack. Yes, this will introduce a lot of new temporaries,
	// but expression propagation should eliminate a lot of them.
	private int nextIndex = 0;

	private void db(final String s) {
		if (Tree.DEBUG) {
			System.out.println(s);
		}
	}

	/**
	 * Constructor.
	 * 
	 * @param block
	 *            The basic Block of code represented in this Tree.
	 * @param predStack
	 *            The contents of the operand stack from the previous basic
	 *            Block.
	 */
	public Tree(final Block block, final OperandStack predStack) {
		this.block = block;

		if (Tree.DEBUG) {
			System.out.println("    new tree for " + block);
		}

		stack = new OperandStack();

		stmts = new StmtList();

		// The first statement in the Tree is the label indicating the start
		// of the basic Block.
		appendStmt(new LabelStmt(block.label()));

		// Make a copy of predStack
		for (int i = 0; i < predStack.size(); i++) {
			final Expr expr = predStack.get(i);
			final Expr copy = (Expr) expr.clone();
			copy.setDef(null);
			stack.push(copy);
		}
	}

	/**
	 * Cleans up this node. Does nothing in this case.
	 */
	public void cleanupOnly() {
	}

	/**
	 * Add a Collection of local variables to the block. Add an InitStmt to the
	 * statement list.
	 * 
	 * @see LocalExpr
	 * @see InitStmt
	 */
	public void initLocals(final Collection locals) {
		final LocalExpr[] t = new LocalExpr[locals.size()];

		if (t.length == 0) {
			return;
		}

		final Iterator iter = locals.iterator();

		for (int i = 0; iter.hasNext(); i++) {
			t[i] = (LocalExpr) iter.next();
		}

		addStmt(new InitStmt(t));
	}

	/**
	 * Removes a statement from the statement list.
	 * 
	 * @param stmt
	 *            The statement to remove
	 */
	public void removeStmt(final Stmt stmt) {
		stmts.remove(stmt);
	}

	/**
	 * Removes the last non-Label statement from the statement list.
	 */
	public void removeLastStmt() {
		final ListIterator iter = stmts.listIterator(stmts.size());

		while (iter.hasPrevious()) {
			final Stmt s = (Stmt) iter.previous();

			if (s instanceof LabelStmt) {
				continue;
			}

			iter.remove();
			return;
		}
	}

	/**
	 * @return The statement list
	 */
	public List stmts() {
		return stmts;
	}

	/**
	 * StmtList is a linked list of statements. A number of methods are
	 * overridden because some adjustments may need to be made to the Nodes in
	 * the tree when certain operations are performed.
	 */
	class StmtList extends LinkedList {
		/**
		 * Clear the contents of this statement list.
		 */
		public void clear() {
			final Iterator iter = iterator();

			while (iter.hasNext()) {
				((Stmt) iter.next()).cleanup();
			}

			super.clear();
		}

		/**
		 * Remove a statement from the list and clean up afterwards.
		 */
		public boolean remove(final Object o) {
			if (super.remove(o)) {
				((Stmt) o).cleanup();
				return true;
			}

			return false;
		}

		/**
		 * Remove all of the statements in a Collection from this statement
		 * list.
		 * 
		 * @param c
		 *            A Collection containing the statements to remove.
		 * @return True, if the contents of this statement list changed.
		 */
		public boolean removeAll(final Collection c) {
			boolean changed = false;

			if (c == this) {
				changed = size() > 0;
				clear();
			} else {
				final Iterator iter = c.iterator();

				while (iter.hasNext()) {
					changed = remove(iter.next()) || changed;
				}
			}

			return changed;
		}

		/**
		 * Remove all statements in this list except those that are in a
		 * specified Collection.
		 * 
		 * @param c
		 *            The statements to keep.
		 * @return True, if the contents of this statement list changed.
		 */
		public boolean retainAll(final Collection c) {
			boolean changed = false;

			if (c == this) {
				return false;
			}

			final Iterator iter = iterator();

			while (iter.hasNext()) {
				if (!c.contains(iter.next())) {
					changed = true;
					iter.remove();
				}
			}

			return changed;
		}

		/**
		 * Set the value of a given statement.
		 * 
		 * @param index
		 *            Index of the statement to change.
		 * @param element
		 *            New value of statement at index.
		 * 
		 * @return Statement previously at position index.
		 */
		public Object set(final int index, final Object element) {
			if (index < size()) {
				final Stmt s = (Stmt) get(index);

				if (s != element) {
					s.cleanup();
				}
			}

			return super.set(index, element);
		}

		/**
		 * Removes the statement at index
		 * 
		 * @return Statement previously at position index.
		 */
		public Object remove(final int index) {
			final Object o = super.remove(index);

			if (o != null) {
				((Stmt) o).cleanup();
			}

			return o;
		}

		/**
		 * Removes statements in a given index range.
		 */
		/*
		 * public void removeRange(int fromIndex, int toIndex) { int remaining =
		 * toIndex - fromIndex;
		 * 
		 * ListIterator iter = listIterator(fromIndex);
		 * 
		 * while (iter.hasNext() && remaining-- > 0) { ((Stmt)
		 * iter.next()).cleanup(); }
		 * 
		 * super.removeRange(fromIndex, toIndex); }
		 */

		/**
		 * @return A ListIterator starting with the first statement in the
		 *         statement list.
		 */
		public ListIterator listIterator() {
			return listIterator(0);
		}

		/**
		 * @return A ListIterator starting a given index.
		 */
		public ListIterator listIterator(final int index) {
			final ListIterator iter = super.listIterator(index);

			return new ListIterator() {
				Object last = null;

				public boolean hasNext() {
					return iter.hasNext();
				}

				public Object next() {
					last = iter.next();
					return last;
				}

				public boolean hasPrevious() {
					return iter.hasPrevious();
				}

				public Object previous() {
					last = iter.previous();
					return last;
				}

				public int nextIndex() {
					return iter.nextIndex();
				}

				public int previousIndex() {
					return iter.previousIndex();
				}

				public void add(final Object obj) {
					Assert.isTrue(obj instanceof Stmt);
					((Stmt) obj).setParent(Tree.this);
					last = null;
					iter.add(obj);
				}

				public void set(final Object obj) {
					if (last == null) {
						throw new NoSuchElementException();
					}

					Assert.isTrue(obj instanceof Stmt);
					((Stmt) obj).setParent(Tree.this);

					((Stmt) last).cleanup();
					last = null;

					iter.set(obj);
				}

				public void remove() {
					if (last == null) {
						throw new NoSuchElementException();
					}

					((Stmt) last).cleanup();
					last = null;

					iter.remove();
				}
			};
		}

		/**
		 * @return An Iterator over this statement list.
		 */
		public Iterator iterator() {
			final Iterator iter = super.iterator();

			return new Iterator() {
				Object last = null;

				public boolean hasNext() {
					return iter.hasNext();
				}

				public Object next() {
					last = iter.next();
					return last;
				}

				public void remove() {
					if (last == null) {
						throw new NoSuchElementException();
					}

					((Stmt) last).cleanup();
					last = null;

					iter.remove();
				}
			};
		}
	}

	/**
	 * Returns the last non-Label statement in the statement list.
	 */
	public Stmt lastStmt() {
		final ListIterator iter = stmts.listIterator(stmts.size());

		while (iter.hasPrevious()) {
			final Stmt s = (Stmt) iter.previous();

			if (s instanceof LabelStmt) {
				continue;
			}

			return s;
		}

		return null;
	}

	/**
	 * Returns the operand stack.
	 */
	public OperandStack stack() {
		return stack;
	}

	/**
	 * Inserts a statement into the statement list after another given
	 * statement.
	 * 
	 * @param stmt
	 *            The statement to add.
	 * @param after
	 *            The statement after which to add stmt.
	 */
	public void addStmtAfter(final Stmt stmt, final Stmt after) {
		if (Tree.DEBUG) {
			System.out.println("insert: " + stmt + " after " + after);
		}

		final ListIterator iter = stmts.listIterator();

		while (iter.hasNext()) {
			final Stmt s = (Stmt) iter.next();

			if (s == after) {
				iter.add(stmt);
				stmt.setParent(this);
				return;
			}
		}

		throw new RuntimeException(after + " not found");
	}

	/**
	 * Inserts a statement into the statement list before a specified statement.
	 * 
	 * @param stmt
	 *            The statement to insert
	 * @param before
	 *            The statement before which to add stmt.
	 */
	public void addStmtBefore(final Stmt stmt, final Stmt before) {
		if (Tree.DEBUG) {
			System.out.println("insert: " + stmt + " before " + before);
		}

		final ListIterator iter = stmts.listIterator();

		while (iter.hasNext()) {
			final Stmt s = (Stmt) iter.next();

			if (s == before) {
				iter.previous();
				iter.add(stmt);
				stmt.setParent(this);
				return;
			}
		}

		throw new RuntimeException(before + " not found");
	}

	/**
	 * Add an statement to the statement list before the first non-Label
	 * statement.
	 * 
	 * @param stmt
	 *            The statement to add.
	 */
	public void prependStmt(final Stmt stmt) {
		if (Tree.DEBUG) {
			System.out.println("prepend: " + stmt + " in " + block);
		}

		final ListIterator iter = stmts.listIterator();

		while (iter.hasNext()) {
			final Stmt s = (Stmt) iter.next();

			if (!(s instanceof LabelStmt)) {
				iter.previous();
				iter.add(stmt);
				stmt.setParent(this);
				return;
			}
		}

		appendStmt(stmt);
	}

	/**
	 * When we build the expression tree, there may be items left over on the
	 * operand stack. We want to save these items. If USE_STACK is true, then we
	 * place these items into stack variables. If USE_STACK is false, then we
	 * place the items into local variables.
	 */
	private void saveStack() {
		int height = 0;

		for (int i = 0; i < stack.size(); i++) {
			final Expr expr = stack.get(i);

			if (Tree.USE_STACK) {
				// Save to a stack variable only if we'll create a new
				// variable there.
				if (!(expr instanceof StackExpr)
						|| (((StackExpr) expr).index() != height)) {

					final StackExpr target = new StackExpr(height, expr.type());

					// Make a new statement to store the expression that was
					// part of the stack into memory.
					final Stmt store = new ExprStmt(new StoreExpr(target, expr,
							expr.type()));

					appendStmt(store);

					final StackExpr copy = (StackExpr) target.clone();
					copy.setDef(null);
					stack.set(i, copy);
				}

			} else {
				if (!(expr instanceof LocalExpr)
						|| !((LocalExpr) expr).fromStack()
						|| (((LocalExpr) expr).index() != height)) {

					final LocalExpr target = newStackLocal(nextIndex++, expr
							.type());

					final Stmt store = new ExprStmt(new StoreExpr(target, expr,
							expr.type()));

					appendStmt(store);

					final LocalExpr copy = (LocalExpr) target.clone();
					copy.setDef(null);
					stack.set(i, copy);
				}
			}

			height += expr.type().stackHeight();
		}
	}

	/**
	 * Add a statement to this Tree statement list and specify that this is the
	 * statement's parent.
	 */
	private void appendStmt(final Stmt stmt) {
		if (Tree.DEBUG) {
			System.out.println("      append: " + stmt);
		}

		stmt.setParent(this);
		stmts.add(stmt);
	}

	/**
	 * Save the contents of the stack and add stmt to the statement list.
	 * 
	 * @param stmt
	 *            A statement to add to the statement list.
	 */
	public void addStmt(final Stmt stmt) {
		saveStack();
		appendStmt(stmt);
	}

	/**
	 * Adds a statement to the statement list before the last jump statement. It
	 * is assumed that the last statement in the statement list is a jump
	 * statement.
	 * 
	 * @see JumpStmt
	 */
	public void addStmtBeforeJump(final Stmt stmt) {
		final Stmt last = lastStmt();
		Assert.isTrue(last instanceof JumpStmt, "Last statement of " + block
				+ " is " + last + ", not a jump");
		addStmtBefore(stmt, last);
	}

	/**
	 * Throw a new ClassFormatException with information about method and class
	 * that this basic block is in.
	 * 
	 * @param msg
	 *            String description of the exception.
	 * 
	 * @see ClassFormatException
	 */
	private void throwClassFormatException(final String msg) {
		final MethodEditor method = block.graph().method();

		throw new ClassFormatException("Method "
				+ method.declaringClass().type().className() + "."
				+ method.name() + " " + method.type() + ": " + msg);
	}

	/**
	 * The last instruction we saw. addInst(Instruction) needs this information.
	 */
	Instruction last = null;

	/**
	 * Adds an instruction that jumps to another basic block.
	 * 
	 * @param inst
	 *            The instruction to add.
	 * @param next
	 *            The basic block after the jump. Remember that a jump ends a
	 *            basic block.
	 * 
	 * @see Instruction#isJsr
	 * @see Instruction#isConditionalJump
	 */
	public void addInstruction(final Instruction inst, final Block next) {
		Assert.isTrue(inst.isJsr() || inst.isConditionalJump(),
				"Wrong addInstruction called with " + inst);
		Assert.isTrue(next != null, "Null next block for " + inst);

		this.next = next;
		addInst(inst);
	}

	/**
	 * Adds an instruction that does not change the control flow (a normal
	 * instruction).
	 * 
	 * @param inst
	 *            Instruction to add.
	 */
	public void addInstruction(final Instruction inst) {
		Assert.isTrue(!inst.isJsr() && !inst.isConditionalJump(),
				"Wrong addInstruction called with " + inst);

		this.next = null;
		addInst(inst);
	}

	/**
	 * Add an instruction such as <i>ret</i> or <i>astore</i> that may involve
	 * a subroutine.
	 * 
	 * @param inst
	 *            Instruction to add.
	 * @param sub
	 *            Subroutine in which inst resides. The <i>ret</i> instruction
	 *            always resides in a Subroutine. An <i>astore</i> may store
	 *            the return address of a subroutine in a local variable.
	 * 
	 * @see Instruction#isRet
	 */
	public void addInstruction(final Instruction inst, final Subroutine sub) {
		Assert.isTrue(inst.isRet()
				|| (inst.opcodeClass() == Opcode.opcx_astore),
				"Wrong addInstruction called with " + inst);

		this.sub = sub;
		this.next = null;
		addInst(inst);
	}

	/**
	 * Add a label to the statement list. A label is inserted before a dup
	 * statement, but after any other statement.
	 * 
	 * @param label
	 *            Label to add.
	 */
	public void addLabel(final Label label) {
		// Add before a dup, but after any other instruction.
		if (last != null) {
			switch (last.opcodeClass()) {
			case opcx_dup:
			case opcx_dup2:
			case opcx_dup_x1:
			case opcx_dup2_x1:
			case opcx_dup_x2:
			case opcx_dup2_x2:
				break;
			default:
				addInst(last, false);
				last = null;
				break;
			}
		}

		addStmt(new LabelStmt(label));
	}

	/**
	 * Private method that adds an instruction to the Tree. The visit method of
	 * the instruction is called with this tree as the visitor. The instruction,
	 * in turn, calls the appropriate method of this for adding the instruction
	 * to the statement list.
	 * 
	 * @param inst
	 *            The <tt>Instruction</tt> to add to the <tt>Tree</tt>
	 * @param saveValue
	 *            Do we save expressions on the operand stack to stack variables
	 *            or local variables.
	 */
	private void addInst(final Instruction inst, final boolean saveValue) {
		if (Tree.DEBUG) {
			// Print the contents of the stack
			for (int i = 0; i < stack.size(); i++) {
				final Expr exp = stack.peek(i);
				System.out.println((i > 0 ? "-" + i : " " + i) + ": " + exp);
			}
		}

		if (Tree.DEBUG) {
			System.out.println("    add " + inst + " save=" + saveValue);
		}

		try {
			this.saveValue = saveValue;

			if (Tree.FLATTEN) {
				saveStack();
			}

			inst.visit(this);

		} catch (final EmptyStackException e) {
			throwClassFormatException("Empty operand stack at " + inst);
			return;
		}
	}

	/**
	 * Private method that adds an instruction to the tree. Before dispatching
	 * the addition off to addInst(Instruction, boolean), it optimizes(?) dup
	 * instructions as outlined below:
	 */
	// (S0, S1) := dup(X)
	// L := S1 --> use (L := X)
	// use S0
	//
	// (S0, S1, S2) := dup_x1(X, Y)
	// S1.f := S2 --> use (X.f := Y)
	// use S0
	//
	// (S0, S1, S2, S3) := dup_x2(X, Y, Z)
	// S1[S2] := S3 --> use (X[Y] := Z)
	// use S0
	//
	private void addInst(final Instruction inst) {
		// Build the tree, trying to convert dup-stores

		if (last == null) {
			last = inst;

		} else {
			switch (last.opcodeClass()) {
			case opcx_dup:
				switch (inst.opcodeClass()) {
				case opcx_astore:
				case opcx_fstore:
				case opcx_istore:
				case opcx_putstatic:
					addInst(inst, true);
					last = null;
					break;
				}
				break;
			case opcx_dup2:
				switch (inst.opcodeClass()) {
				case opcx_dstore:
				case opcx_lstore:
				case opcx_putstatic:
					addInst(inst, true);
					last = null;
					break;
				}
				break;
			case opcx_dup_x1:
				switch (inst.opcodeClass()) {
				case opcx_putfield:
				case opcx_putfield_nowb:
					addInst(inst, true);
					last = null;
					break;
				}
				break;
			case opcx_dup2_x1:
				switch (inst.opcodeClass()) {
				case opcx_putfield:
				case opcx_putfield_nowb:
					addInst(inst, true);
					last = null;
					break;
				}
				break;
			case opcx_dup_x2:
				switch (inst.opcodeClass()) {
				case opcx_aastore:
				case opcx_bastore:
				case opcx_castore:
				case opcx_fastore:
				case opcx_iastore:
				case opcx_sastore:
					addInst(inst, true);
					last = null;
					break;
				}
				break;
			case opcx_dup2_x2:
				switch (inst.opcodeClass()) {
				case opcx_dastore:
				case opcx_lastore:
					addInst(inst, true);
					last = null;
					break;
				}
				break;
			}

			if (last != null) {
				addInst(last, false);
				last = inst;
			}
		}

		// We should have dealt with the old last instruction
		Assert.isTrue((last == null) || (last == inst));

		if (inst.isJump() || inst.isSwitch() || inst.isThrow()
				|| inst.isReturn() || inst.isJsr() || inst.isRet()) {
			addInst(inst, false);
			last = null;
		}
	}

	/**
	 * Returns a new StackExpr for the top of the operand stack.
	 */
	public StackExpr newStack(final Type type) {
		return new StackExpr(Tree.stackpos++, type);
	}

	/**
	 * Returns a new LocalExpr that represents an element of the stack. They are
	 * created when the USE_STACK flag is not set.
	 * 
	 * @param index
	 *            Stack index of variable.
	 * @param type
	 *            The type of the LocalExpr
	 */
	public LocalExpr newStackLocal(final int index, final Type type) {
		if (index >= nextIndex) {
			nextIndex = index + 1;
		}
		return new LocalExpr(index, true, type);
	}

	/**
	 * Returns a new LocalExpr that is not allocated on the stack.
	 * 
	 * @param index
	 *            Stack index of variable.
	 * @param type
	 *            The type of the LocalExpr
	 */
	public LocalExpr newLocal(final int index, final Type type) {
		return new LocalExpr(index, false, type);
	}

	/**
	 * Returns a local variable (<tt>LocalExpr</tt>) located in this method.
	 * 
	 * @param type
	 *            The type of the new LocalExpr.
	 */
	public LocalExpr newLocal(final Type type) {
		final LocalVariable var = block.graph().method().newLocal(type);
		return new LocalExpr(var.index(), type);
	}

	/**
	 * Returns a String representation of this Tree.
	 */
	public String toString() {
		String x = "(TREE " + block + " stack=";

		for (int i = 0; i < stack.size(); i++) {
			final Expr expr = stack.get(i);
			x += expr.type().shortName();
		}

		return x + ")";
	}

	/**
	 * Adds no statements to the statement list.
	 */
	public void visit_nop(final Instruction inst) {
	}

	/**
	 * Pushes a ConstantExpr onto the operand stack.
	 * 
	 * @see ConstantExpr
	 */
	public void visit_ldc(final Instruction inst) {
		final Object value = inst.operand();
		Type type;

		if (value == null) {
			type = Type.NULL;
		} else if (value instanceof Integer) {
			type = Type.INTEGER;
		} else if (value instanceof Long) {
			type = Type.LONG;
		} else if (value instanceof Float) {
			type = Type.FLOAT;
		} else if (value instanceof Double) {
			type = Type.DOUBLE;
		} else if (value instanceof String) {
			type = Type.STRING;
		}
		// FIXME this won't work - check usages
		else if (value instanceof Type) {
			type = Type.CLASS;
		} else {
			throwClassFormatException("Illegal constant type: "
					+ value.getClass().getName() + ": " + value);
			return;
		}

		final Expr top = new ConstantExpr(value, type);
		stack.push(top);
	}

	/**
	 * All <tt>visit_<i>x</i>load</tt> push a LocalExpr onto the operand
	 * stack.
	 * 
	 * @see LocalExpr
	 */
	public void visit_iload(final Instruction inst) {
		final LocalVariable operand = (LocalVariable) inst.operand();
		final Expr top = new LocalExpr(operand.index(), Type.INTEGER);
		stack.push(top);
	}

	public void visit_lload(final Instruction inst) {
		final LocalVariable operand = (LocalVariable) inst.operand();
		final Expr top = new LocalExpr(operand.index(), Type.LONG);
		stack.push(top);
	}

	public void visit_fload(final Instruction inst) {
		final LocalVariable operand = (LocalVariable) inst.operand();
		final Expr top = new LocalExpr(operand.index(), Type.FLOAT);
		stack.push(top);
	}

	public void visit_dload(final Instruction inst) {
		final LocalVariable operand = (LocalVariable) inst.operand();
		final Expr top = new LocalExpr(operand.index(), Type.DOUBLE);
		stack.push(top);
	}

	public void visit_aload(final Instruction inst) {
		final LocalVariable operand = (LocalVariable) inst.operand();
		final Expr top = new LocalExpr(operand.index(), Type.OBJECT);
		stack.push(top);

		db("      aload: " + top);
	}

	/**
	 * All <tt>visit_<i>x</i>aload</tt> push an ArrayRefExpr onto the
	 * operand stack.
	 */
	public void visit_iaload(final Instruction inst) {
		final Expr index = stack.pop(Type.INTEGER);
		final Expr array = stack.pop(Type.INTEGER.arrayType());
		final Expr top = new ArrayRefExpr(array, index, Type.INTEGER,
				Type.INTEGER);
		stack.push(top);
	}

	public void visit_laload(final Instruction inst) {
		final Expr index = stack.pop(Type.INTEGER);
		final Expr array = stack.pop(Type.LONG.arrayType());
		final Expr top = new ArrayRefExpr(array, index, Type.LONG, Type.LONG);
		stack.push(top);
	}

	public void visit_faload(final Instruction inst) {
		final Expr index = stack.pop(Type.INTEGER);
		final Expr array = stack.pop(Type.FLOAT.arrayType());
		final Expr top = new ArrayRefExpr(array, index, Type.FLOAT, Type.FLOAT);
		stack.push(top);
	}

	public void visit_daload(final Instruction inst) {
		final Expr index = stack.pop(Type.INTEGER);
		final Expr array = stack.pop(Type.DOUBLE.arrayType());
		final Expr top = new ArrayRefExpr(array, index, Type.DOUBLE,
				Type.DOUBLE);
		stack.push(top);
	}

	public void visit_aaload(final Instruction inst) {
		final Expr index = stack.pop(Type.INTEGER);
		final Expr array = stack.pop(Type.OBJECT.arrayType());
		final Expr top = new ArrayRefExpr(array, index, Type.OBJECT,
				Type.OBJECT);
		stack.push(top);
	}

	public void visit_baload(final Instruction inst) {
		final Expr index = stack.pop(Type.INTEGER);
		final Expr array = stack.pop(Type.BYTE.arrayType());
		final Expr top = new ArrayRefExpr(array, index, Type.BYTE, Type.BYTE);
		stack.push(top);
	}

	public void visit_caload(final Instruction inst) {
		final Expr index = stack.pop(Type.INTEGER);
		final Expr array = stack.pop(Type.CHARACTER.arrayType());
		final Expr top = new ArrayRefExpr(array, index, Type.CHARACTER,
				Type.CHARACTER);
		stack.push(top);
	}

	public void visit_saload(final Instruction inst) {
		final Expr index = stack.pop(Type.INTEGER);
		final Expr array = stack.pop(Type.SHORT.arrayType());
		final Expr top = new ArrayRefExpr(array, index, Type.SHORT, Type.SHORT);
		stack.push(top);
	}

	/**
	 * Deals with an expression that stores a value. It either pushes it on the
	 * operand stack or adds a statement depending on the value of saveValue.
	 * 
	 * @param target
	 *            The location to where we are storing the value.
	 * @param expr
	 *            The expression whose value we are storing.
	 */
	private void addStore(final MemExpr target, final Expr expr) {
		if (saveValue) {
			stack.push(new StoreExpr(target, expr, expr.type()));

		} else {
			addStmt(new ExprStmt(new StoreExpr(target, expr, expr.type())));
		}
	}

	/**
	 * All <tt>visit_<i>x</i>store</tt> add a LocalExpr statement to the
	 * statement list.
	 */
	public void visit_istore(final Instruction inst) {
		final LocalVariable operand = (LocalVariable) inst.operand();
		final Expr expr = stack.pop(Type.INTEGER);
		final LocalExpr target = new LocalExpr(operand.index(), expr.type());
		addStore(target, expr);
	}

	public void visit_lstore(final Instruction inst) {
		final LocalVariable operand = (LocalVariable) inst.operand();
		final Expr expr = stack.pop(Type.LONG);
		final LocalExpr target = new LocalExpr(operand.index(), expr.type());
		addStore(target, expr);
	}

	public void visit_fstore(final Instruction inst) {
		final LocalVariable operand = (LocalVariable) inst.operand();
		final Expr expr = stack.pop(Type.FLOAT);
		final LocalExpr target = new LocalExpr(operand.index(), expr.type());
		addStore(target, expr);
	}

	public void visit_dstore(final Instruction inst) {
		final LocalVariable operand = (LocalVariable) inst.operand();
		final Expr expr = stack.pop(Type.DOUBLE);
		final LocalExpr target = new LocalExpr(operand.index(), expr.type());
		addStore(target, expr);
	}

	/**
	 * Visit an <i>astore</i> instruction. If the type of the operand to the
	 * instruction is an address add an AddressStoreStmt to the tree, else add a
	 * StoreStmt to the tree consisting of a LocalExpr and the top Expr on the
	 * operand stack.
	 * 
	 * @see AddressStoreStmt
	 * @see LocalExpr
	 * @see StoreExpr
	 */
	public void visit_astore(final Instruction inst) {
		final LocalVariable operand = (LocalVariable) inst.operand();

		Expr expr = stack.peek();

		if (expr.type().isAddress()) {
			Assert.isTrue(sub != null);
			Assert.isTrue(!saveValue);
			expr = stack.pop(Type.ADDRESS);
			sub.setReturnAddress(operand);
			addStmt(new AddressStoreStmt(sub));
		} else {
			expr = stack.pop(Type.OBJECT);
			final LocalExpr target = new LocalExpr(operand.index(), expr.type());
			addStore(target, expr);
		}
	}

	public void visit_iastore(final Instruction inst) {
		final Expr value = stack.pop(Type.INTEGER);
		final Expr index = stack.pop(Type.INTEGER);
		final Expr array = stack.pop(Type.INTEGER.arrayType());
		final ArrayRefExpr target = new ArrayRefExpr(array, index,
				Type.INTEGER, Type.INTEGER);
		addStore(target, value);
	}

	public void visit_lastore(final Instruction inst) {
		final Expr value = stack.pop(Type.LONG);
		final Expr index = stack.pop(Type.INTEGER);
		final Expr array = stack.pop(Type.LONG.arrayType());
		final ArrayRefExpr target = new ArrayRefExpr(array, index, Type.LONG,
				Type.LONG);
		addStore(target, value);
	}

	public void visit_fastore(final Instruction inst) {
		final Expr value = stack.pop(Type.FLOAT);
		final Expr index = stack.pop(Type.INTEGER);
		final Expr array = stack.pop(Type.FLOAT.arrayType());
		final ArrayRefExpr target = new ArrayRefExpr(array, index, Type.FLOAT,
				Type.FLOAT);
		addStore(target, value);
	}

	public void visit_dastore(final Instruction inst) {
		final Expr value = stack.pop(Type.DOUBLE);
		final Expr index = stack.pop(Type.INTEGER);
		final Expr array = stack.pop(Type.DOUBLE.arrayType());
		final ArrayRefExpr target = new ArrayRefExpr(array, index, Type.DOUBLE,
				Type.DOUBLE);
		addStore(target, value);
	}

	public void visit_aastore(final Instruction inst) {
		final Expr value = stack.pop(Type.OBJECT);
		final Expr index = stack.pop(Type.INTEGER);
		final Expr array = stack.pop(Type.OBJECT.arrayType());
		final ArrayRefExpr target = new ArrayRefExpr(array, index, Type.OBJECT,
				Type.OBJECT);
		addStore(target, value);
	}

	public void visit_bastore(final Instruction inst) {
		final Expr value = stack.pop(Type.BYTE);
		final Expr index = stack.pop(Type.INTEGER);
		final Expr array = stack.pop(Type.BYTE.arrayType());
		final ArrayRefExpr target = new ArrayRefExpr(array, index, Type.BYTE,
				Type.BYTE);
		addStore(target, value);
	}

	public void visit_castore(final Instruction inst) {
		final Expr value = stack.pop(Type.CHARACTER);
		final Expr index = stack.pop(Type.INTEGER);
		final Expr array = stack.pop(Type.CHARACTER.arrayType());
		final ArrayRefExpr target = new ArrayRefExpr(array, index,
				Type.CHARACTER, Type.CHARACTER);
		addStore(target, value);
	}

	public void visit_sastore(final Instruction inst) {
		final Expr value = stack.pop(Type.SHORT);
		final Expr index = stack.pop(Type.INTEGER);
		final Expr array = stack.pop(Type.SHORT.arrayType());
		final ArrayRefExpr target = new ArrayRefExpr(array, index, Type.SHORT,
				Type.SHORT);
		addStore(target, value);
	}

	/**
	 * Pop the expression off the top of the stack and add it as an ExprStmt to
	 * the statement list.
	 * 
	 * @see ExprStmt
	 */
	public void visit_pop(final Instruction inst) {
		final Expr expr = stack.pop1();
		addStmt(new ExprStmt(expr));
	}

	public void visit_pop2(final Instruction inst) {
		final Expr[] expr = stack.pop2();

		if (expr.length == 1) {
			addStmt(new ExprStmt(expr[0]));
		} else {
			addStmt(new ExprStmt(expr[0]));
			addStmt(new ExprStmt(expr[1]));
		}
	}

	/**
	 * When processing the <i>dup</i> instructions one of two situations can
	 * occur. If the <tt>USE_STACK</tt> flag is set, then a StackManipStmt is
	 * created to represent the transformation that the <i>dup</i> instruction
	 * performs on the stack. If the <tt>USE_STACK</tt> flag is not set, then
	 * the transformation is simulated by creating new local variables
	 * containing the appropriate element of the stack.
	 * 
	 * @see LocalExpr
	 * @see StackExpr
	 * @see StackManipStmt
	 */
	public void visit_dup(final Instruction inst) {
		// 0 -> 0 0

		db("      dup");

		if (Tree.USE_STACK) {
			saveStack();

			final StackExpr s0 = (StackExpr) stack.pop1();

			final StackExpr[] s = new StackExpr[] { s0 };
			manip(s, new int[] { 0, 0 }, StackManipStmt.DUP);

		} else {
			final Expr s0 = stack.pop1();

			final LocalExpr t0 = newStackLocal(stack.height(), s0.type());

			db("        s0: " + s0);
			db("        t0: " + t0);

			if (!t0.equalsExpr(s0)) {
				db("          t0 <- s0");
				addStore(t0, s0);
			}

			Expr copy = (Expr) t0.clone();
			copy.setDef(null);
			stack.push(copy);

			copy = (Expr) t0.clone();
			copy.setDef(null);
			stack.push(copy);
		}
	}

	public void visit_dup_x1(final Instruction inst) {
		// 0 1 -> 1 0 1

		if (Tree.USE_STACK) {
			saveStack();

			final StackExpr s1 = (StackExpr) stack.pop1();
			final StackExpr s0 = (StackExpr) stack.pop1();

			final StackExpr[] s = new StackExpr[] { s0, s1 };
			manip(s, new int[] { 1, 0, 1 }, StackManipStmt.DUP_X1);

		} else {
			final Expr s1 = stack.pop1();
			final Expr s0 = stack.pop1();

			final LocalExpr t0 = newStackLocal(stack.height(), s0.type());
			final LocalExpr t1 = newStackLocal(stack.height() + 1, s1.type());

			if (!t0.equalsExpr(s0)) {
				addStore(t0, s0);
			}

			if (!t1.equalsExpr(s1)) {
				addStore(t1, s1);
			}

			Expr copy = (Expr) t1.clone();
			copy.setDef(null);
			stack.push(copy);

			copy = (Expr) t0.clone();
			copy.setDef(null);
			stack.push(copy);

			copy = (Expr) t1.clone();
			copy.setDef(null);
			stack.push(copy);
		}
	}

	public void visit_dup_x2(final Instruction inst) {
		// 0 1 2 -> 2 0 1 2
		// 0-1 2 -> 2 0-1 2

		db("      dup_x2");

		if (Tree.USE_STACK) {
			saveStack();

			final StackExpr s2 = (StackExpr) stack.pop1();
			final Expr[] s01 = stack.pop2();

			if (s01.length == 2) {
				// 0 1 2 -> 2 0 1 2
				final StackExpr[] s = new StackExpr[] { (StackExpr) s01[0],
						(StackExpr) s01[1], s2 };
				manip(s, new int[] { 2, 0, 1, 2 }, StackManipStmt.DUP_X2);
			} else {
				// 0-1 2 -> 2 0-1 2
				final StackExpr[] s = new StackExpr[] { (StackExpr) s01[0], s2 };
				manip(s, new int[] { 1, 0, 1 }, StackManipStmt.DUP_X2);
			}

		} else {
			final Expr s2 = stack.pop1();
			final Expr[] s01 = stack.pop2();

			db("        s2: " + s2);
			db("        s01: " + s01[0] + (s01.length > 1 ? " " + s01[1] : ""));

			if (s01.length == 2) {
				// 0 1 2 -> 2 0 1 2
				final LocalExpr t0 = newStackLocal(stack.height(), s01[0]
						.type());
				final LocalExpr t1 = newStackLocal(stack.height() + 1, s01[1]
						.type());
				final LocalExpr t2 = newStackLocal(stack.height() + 2, s2
						.type());

				db("        t0: " + t0);
				db("        t1: " + t1);
				db("        t2: " + t2);

				if (!t0.equalsExpr(s01[0])) {
					db("          t0 <- s01[0]");
					addStore(t0, s01[0]);
				}

				if (!t1.equalsExpr(s01[1])) {
					db("          t1 <- s01[1]");
					addStore(t1, s01[1]);
				}

				if (!t2.equalsExpr(s2)) {
					db("          t2 <- s2");
					addStore(t2, s2);
				}

				Expr copy = (Expr) t2.clone();
				copy.setDef(null);
				stack.push(copy);

				copy = (Expr) t0.clone();
				copy.setDef(null);
				stack.push(copy);

				copy = (Expr) t1.clone();
				copy.setDef(null);
				stack.push(copy);

				copy = (Expr) t2.clone();
				copy.setDef(null);
				stack.push(copy);

			} else {
				// 0-1 2 -> 2 0-1 2
				final LocalExpr t0 = newStackLocal(stack.height(), s01[0]
						.type());
				final LocalExpr t2 = newStackLocal(stack.height() + 2, s2
						.type());

				if (!t0.equalsExpr(s01[0])) {
					addStore(t0, s01[0]);
				}

				if (!t2.equalsExpr(s2)) {
					addStore(t2, s2);
				}

				Expr copy = (Expr) t2.clone();
				copy.setDef(null);
				stack.push(copy);

				copy = (Expr) t0.clone();
				copy.setDef(null);
				stack.push(copy);

				copy = (Expr) t2.clone();
				copy.setDef(null);
				stack.push(copy);
			}
		}
	}

	public void visit_dup2(final Instruction inst) {
		// 0 1 -> 0 1 0 1
		// 0-1 -> 0-1 0-1

		if (Tree.USE_STACK) {
			saveStack();

			final Expr[] s01 = stack.pop2();

			if (s01.length == 1) {
				// 0-1 -> 0-1 0-1
				final StackExpr[] s = new StackExpr[] { (StackExpr) s01[0] };
				manip(s, new int[] { 0, 0 }, StackManipStmt.DUP2);
			} else {
				// 0 1 -> 0 1 0 1

				Assert.isTrue(s01.length == 2);

				final StackExpr[] s = new StackExpr[] { (StackExpr) s01[0],
						(StackExpr) s01[1] };
				manip(s, new int[] { 0, 1, 0, 1 }, StackManipStmt.DUP2);
			}
		} else {
			final Expr[] s01 = stack.pop2();

			if (s01.length == 1) {
				// 0-1 -> 0-1 0-1
				final LocalExpr t0 = newStackLocal(stack.height(), s01[0]
						.type());

				if (!t0.equalsExpr(s01[0])) {
					addStore(t0, s01[0]);
				}

				Expr copy = (Expr) t0.clone();
				copy.setDef(null);
				stack.push(copy);

				copy = (Expr) t0.clone();
				copy.setDef(null);
				stack.push(copy);
			} else {
				// 0 1 -> 0 1 0 1
				final LocalExpr t0 = newStackLocal(stack.height(), s01[0]
						.type());
				final LocalExpr t1 = newStackLocal(stack.height() + 1, s01[1]
						.type());

				if (!t0.equalsExpr(s01[0])) {
					addStore(t0, s01[0]);
				}

				if (!t1.equalsExpr(s01[1])) {
					addStore(t1, s01[1]);
				}

				Expr copy = (Expr) t0.clone();
				copy.setDef(null);
				stack.push(copy);

				copy = (Expr) t1.clone();
				copy.setDef(null);
				stack.push(copy);

				copy = (Expr) t0.clone();
				copy.setDef(null);
				stack.push(copy);

				copy = (Expr) t1.clone();
				copy.setDef(null);
				stack.push(copy);
			}
		}
	}

	public void visit_dup2_x1(final Instruction inst) {
		// 0 1 2 -> 1 2 0 1 2
		// 0 1-2 -> 1-2 0 1-2

		if (Tree.USE_STACK) {
			saveStack();

			final Expr[] s12 = stack.pop2();
			final StackExpr s0 = (StackExpr) stack.pop1();

			if (s12.length == 2) {
				// 0 1 2 -> 1 2 0 1 2
				final StackExpr[] s = new StackExpr[] { s0, (StackExpr) s12[0],
						(StackExpr) s12[1] };
				manip(s, new int[] { 1, 2, 0, 1, 2 }, StackManipStmt.DUP2_X1);
			} else {
				// 0 1-2 -> 1-2 0 1-2
				final StackExpr[] s = new StackExpr[] { s0, (StackExpr) s12[0] };
				manip(s, new int[] { 1, 0, 1 }, StackManipStmt.DUP2_X1);
			}
		} else {
			final Expr[] s12 = stack.pop2();
			final StackExpr s0 = (StackExpr) stack.pop1();

			if (s12.length == 2) {
				// 0 1 2 -> 1 2 0 1 2
				final LocalExpr t0 = newStackLocal(stack.height(), s0.type());
				final LocalExpr t1 = newStackLocal(stack.height() + 1, s12[0]
						.type());
				final LocalExpr t2 = newStackLocal(stack.height() + 2, s12[1]
						.type());

				if (!t0.equalsExpr(s0)) {
					addStore(t0, s0);
				}

				if (!t1.equalsExpr(s12[0])) {
					addStore(t1, s12[0]);
				}

				if (!t2.equalsExpr(s12[1])) {
					addStore(t2, s12[1]);
				}

				Expr copy = (Expr) t1.clone();
				copy.setDef(null);
				stack.push(copy);

				copy = (Expr) t2.clone();
				copy.setDef(null);
				stack.push(copy);

				copy = (Expr) t0.clone();
				copy.setDef(null);
				stack.push(copy);

				copy = (Expr) t1.clone();
				copy.setDef(null);
				stack.push(copy);

				copy = (Expr) t2.clone();
				copy.setDef(null);
				stack.push(copy);
			} else {
				// 0 1-2 -> 1-2 0 1-2
				final LocalExpr t0 = newStackLocal(stack.height(), s0.type());
				final LocalExpr t1 = newStackLocal(stack.height() + 1, s12[0]
						.type());

				if (!t0.equalsExpr(s0)) {
					addStore(t0, s0);
				}

				if (!t1.equalsExpr(s12[0])) {
					addStore(t1, s12[0]);
				}

				Expr copy = (Expr) t1.clone();
				copy.setDef(null);
				stack.push(copy);

				copy = (Expr) t0.clone();
				copy.setDef(null);
				stack.push(copy);

				copy = (Expr) t1.clone();
				copy.setDef(null);
				stack.push(copy);
			}
		}
	}

	public void visit_dup2_x2(final Instruction inst) {
		// 0 1 2 3 -> 2 3 0 1 2 3
		// 0 1 2-3 -> 2-3 0 1 2-3
		// 0-1 2 3 -> 2 3 0-1 2 3
		// 0-1 2-3 -> 2-3 0-1 2-3

		if (Tree.USE_STACK) {
			saveStack();

			final Expr[] s23 = stack.pop2();
			final Expr[] s01 = stack.pop2();

			if ((s01.length == 2) && (s23.length == 2)) {
				// 0 1 2 3 -> 2 3 0 1 2 3
				final StackExpr[] s = new StackExpr[] { (StackExpr) s01[0],
						(StackExpr) s01[1], (StackExpr) s23[0],
						(StackExpr) s23[1] };
				manip(s, new int[] { 2, 3, 0, 1, 2, 3 }, StackManipStmt.DUP2_X2);
			} else if ((s01.length == 2) && (s23.length == 1)) {
				// 0 1 2-3 -> 2-3 0 1 2-3
				final StackExpr[] s = new StackExpr[] { (StackExpr) s01[0],
						(StackExpr) s01[1], (StackExpr) s23[0] };
				manip(s, new int[] { 2, 0, 1, 2 }, StackManipStmt.DUP2_X2);
			} else if ((s01.length == 1) && (s23.length == 2)) {
				// 0-1 2 3 -> 2 3 0-1 2 3
				final StackExpr[] s = new StackExpr[] { (StackExpr) s01[0],
						(StackExpr) s23[0], (StackExpr) s23[1] };
				manip(s, new int[] { 1, 2, 0, 1, 2 }, StackManipStmt.DUP2_X2);
			} else if ((s01.length == 1) && (s23.length == 2)) {
				// 0-1 2-3 -> 2-3 0-1 2-3
				final StackExpr[] s = new StackExpr[] { (StackExpr) s01[0],
						(StackExpr) s23[0] };
				manip(s, new int[] { 1, 0, 1 }, StackManipStmt.DUP2_X2);
			}
		} else {
			final Expr[] s23 = stack.pop2();
			final Expr[] s01 = stack.pop2();

			if ((s01.length == 2) && (s23.length == 2)) {
				// 0 1 2 3 -> 2 3 0 1 2 3
				final LocalExpr t0 = newStackLocal(stack.height(), s01[0]
						.type());
				final LocalExpr t1 = newStackLocal(stack.height() + 1, s01[1]
						.type());
				final LocalExpr t2 = newStackLocal(stack.height() + 2, s23[0]
						.type());
				final LocalExpr t3 = newStackLocal(stack.height() + 3, s23[1]
						.type());

				if (!t0.equalsExpr(s01[0])) {
					addStore(t0, s01[0]);
				}

				if (!t1.equalsExpr(s01[1])) {
					addStore(t1, s01[1]);
				}

				if (!t2.equalsExpr(s23[0])) {
					addStore(t2, s23[0]);
				}

				if (!t3.equalsExpr(s23[1])) {
					addStore(t3, s23[1]);
				}

				Expr copy = (Expr) t2.clone();
				copy.setDef(null);
				stack.push(copy);

				copy = (Expr) t3.clone();
				copy.setDef(null);
				stack.push(copy);

				copy = (Expr) t0.clone();
				copy.setDef(null);
				stack.push(copy);

				copy = (Expr) t1.clone();
				copy.setDef(null);
				stack.push(copy);

				copy = (Expr) t2.clone();
				copy.setDef(null);
				stack.push(copy);

				copy = (Expr) t3.clone();
				copy.setDef(null);
				stack.push(copy);
			} else if ((s01.length == 2) && (s23.length == 1)) {
				// 0 1 2-3 -> 2-3 0 1 2-3
				final LocalExpr t0 = newStackLocal(stack.height(), s01[0]
						.type());
				final LocalExpr t1 = newStackLocal(stack.height() + 1, s01[1]
						.type());
				final LocalExpr t2 = newStackLocal(stack.height() + 2, s23[0]
						.type());

				if (!t0.equalsExpr(s01[0])) {
					addStore(t0, s01[0]);
				}

				if (!t1.equalsExpr(s01[1])) {
					addStore(t1, s01[1]);
				}

				if (!t2.equalsExpr(s23[0])) {
					addStore(t2, s23[0]);
				}

				Expr copy = (Expr) t2.clone();
				copy.setDef(null);
				stack.push(copy);

				copy = (Expr) t0.clone();
				copy.setDef(null);
				stack.push(copy);

				copy = (Expr) t1.clone();
				copy.setDef(null);
				stack.push(copy);

				copy = (Expr) t2.clone();
				copy.setDef(null);
				stack.push(copy);
			} else if ((s01.length == 1) && (s23.length == 2)) {
				// 0-1 2 3 -> 2 3 0-1 2 3
				final LocalExpr t0 = newStackLocal(stack.height(), s01[0]
						.type());
				final LocalExpr t2 = newStackLocal(stack.height() + 2, s23[0]
						.type());
				final LocalExpr t3 = newStackLocal(stack.height() + 3, s23[1]
						.type());

				if (!t0.equalsExpr(s01[0])) {
					addStore(t0, s01[0]);
				}

				if (!t2.equalsExpr(s23[0])) {
					addStore(t2, s23[0]);
				}

				if (!t3.equalsExpr(s23[1])) {
					addStore(t3, s23[1]);
				}

				Expr copy = (Expr) t2.clone();
				copy.setDef(null);
				stack.push(copy);

				copy = (Expr) t3.clone();
				copy.setDef(null);
				stack.push(copy);

				copy = (Expr) t0.clone();
				copy.setDef(null);
				stack.push(copy);

				copy = (Expr) t2.clone();
				copy.setDef(null);
				stack.push(copy);

				copy = (Expr) t3.clone();
				copy.setDef(null);
				stack.push(copy);
			} else if ((s01.length == 1) && (s23.length == 2)) {
				// 0-1 2-3 -> 2-3 0-1 2-3
				final LocalExpr t0 = newStackLocal(stack.height(), s01[0]
						.type());
				final LocalExpr t2 = newStackLocal(stack.height() + 2, s23[0]
						.type());

				if (!t0.equalsExpr(s01[0])) {
					addStore(t0, s01[0]);
				}

				if (!t2.equalsExpr(s23[0])) {
					addStore(t2, s23[0]);
				}

				Expr copy = (Expr) t2.clone();
				copy.setDef(null);
				stack.push(copy);

				copy = (Expr) t0.clone();
				copy.setDef(null);
				stack.push(copy);

				copy = (Expr) t2.clone();
				copy.setDef(null);
				stack.push(copy);
			}
		}
	}

	public void visit_swap(final Instruction inst) {
		// 0 1 -> 1 0

		if (Tree.USE_STACK) {
			saveStack();

			final StackExpr s1 = (StackExpr) stack.pop1();
			final StackExpr s0 = (StackExpr) stack.pop1();

			final StackExpr[] s = new StackExpr[] { s0, s1 };
			manip(s, new int[] { 1, 0 }, StackManipStmt.SWAP);
		} else {
			final Expr s1 = stack.pop1();
			final Expr s0 = stack.pop1();

			final LocalExpr t0 = newStackLocal(stack.height(), s0.type());
			final LocalExpr t1 = newStackLocal(stack.height() + 1, s1.type());

			if (!t0.equalsExpr(s0)) {
				addStore(t0, s0);
			}

			if (!t1.equalsExpr(s1)) {
				addStore(t1, s1);
			}

			Expr copy = (Expr) t1.clone();
			copy.setDef(null);
			stack.push(copy);

			copy = (Expr) t0.clone();
			copy.setDef(null);
			stack.push(copy);
		}
	}

	/**
	 * Produces a StackManipStmt that represents how the stack is changed as a
	 * result of a dup instruction. It should only be used when USE_STACK is
	 * true.
	 * <p>
	 * dup instructions change the top n elements of the JVM stack. This method
	 * takes the original top n elements of the stack, an integer array
	 * representing the transformation (for instance, if s[0] = 1, then the top
	 * element of the new stack should contain the second-from-the-top element
	 * of the old stack), and integer representing the dup instruction.
	 * 
	 * @param source
	 *            The interesting part of the stack before the dup instruction
	 *            is executed.
	 * @param s
	 *            An integer array representing the new order of the stack.
	 * @param kind
	 *            The kind of stack manipulation taking place. (e.g.
	 *            StackManipStmt.DUP_X1)
	 * 
	 * @see StackManipStmt
	 */
	private void manip(final StackExpr[] source, final int[] s, final int kind) {
		Assert.isTrue(Tree.USE_STACK);

		int height = 0; // Height of the stack

		// Calculate current height of the stack. Recall that the stack
		// elements in source have already been popped off the stack.
		for (int i = 0; i < stack.size(); i++) {
			final Expr expr = stack.get(i);
			height += expr.type().stackHeight();
		}

		// Create the new portion of the stack. Make new StackExpr
		// representing the stack after the dup instruction. Push those
		// new StackExprs onto the operand stack. Finally, create a
		// StackManipStmt that represent the transformation of the old
		// stack (before dup instruction) to the new stack (after dup
		// instruction).
		final StackExpr[] target = new StackExpr[s.length];

		for (int i = 0; i < s.length; i++) {
			target[i] = new StackExpr(height, source[s[i]].type());
			final StackExpr copy = (StackExpr) target[i].clone();
			copy.setDef(null);
			stack.push(copy);
			height += target[i].type().stackHeight();
		}

		appendStmt(new StackManipStmt(target, source, kind));
	}

	/**
	 * All <tt>visit_<i>x</i>add</tt>, <tt>visit_<i>x</i>sub</tt>,
	 * <tt>visit_<i>x</i>mul</tt>, <tt>visit_<i>x</i>div</tt>, etc.
	 * push an ArithExpr onto the operand stack.
	 * 
	 * @see ArithExpr
	 */
	public void visit_iadd(final Instruction inst) {
		final Expr right = stack.pop(Type.INTEGER);
		final Expr left = stack.pop(Type.INTEGER);
		final Expr top = new ArithExpr(ArithExpr.ADD, left, right, Type.INTEGER);
		stack.push(top);
	}

	public void visit_ladd(final Instruction inst) {
		final Expr right = stack.pop(Type.LONG);
		final Expr left = stack.pop(Type.LONG);
		final Expr top = new ArithExpr(ArithExpr.ADD, left, right, Type.LONG);
		stack.push(top);
	}

	public void visit_fadd(final Instruction inst) {
		final Expr right = stack.pop(Type.FLOAT);
		final Expr left = stack.pop(Type.FLOAT);
		final Expr top = new ArithExpr(ArithExpr.ADD, left, right, Type.FLOAT);
		stack.push(top);
	}

	public void visit_dadd(final Instruction inst) {
		final Expr right = stack.pop(Type.DOUBLE);
		final Expr left = stack.pop(Type.DOUBLE);
		final Expr top = new ArithExpr(ArithExpr.ADD, left, right, Type.DOUBLE);
		stack.push(top);
	}

	public void visit_isub(final Instruction inst) {
		final Expr right = stack.pop(Type.INTEGER);
		final Expr left = stack.pop(Type.INTEGER);
		final Expr top = new ArithExpr(ArithExpr.SUB, left, right, Type.INTEGER);
		stack.push(top);
	}

	public void visit_lsub(final Instruction inst) {
		final Expr right = stack.pop(Type.LONG);
		final Expr left = stack.pop(Type.LONG);
		final Expr top = new ArithExpr(ArithExpr.SUB, left, right, Type.LONG);
		stack.push(top);
	}

	public void visit_fsub(final Instruction inst) {
		final Expr right = stack.pop(Type.FLOAT);
		final Expr left = stack.pop(Type.FLOAT);
		final Expr top = new ArithExpr(ArithExpr.SUB, left, right, Type.FLOAT);
		stack.push(top);
	}

	public void visit_dsub(final Instruction inst) {
		final Expr right = stack.pop(Type.DOUBLE);
		final Expr left = stack.pop(Type.DOUBLE);
		final Expr top = new ArithExpr(ArithExpr.SUB, left, right, Type.DOUBLE);
		stack.push(top);
	}

	public void visit_imul(final Instruction inst) {
		final Expr right = stack.pop(Type.INTEGER);
		final Expr left = stack.pop(Type.INTEGER);
		final Expr top = new ArithExpr(ArithExpr.MUL, left, right, Type.INTEGER);
		stack.push(top);
	}

	public void visit_lmul(final Instruction inst) {
		final Expr right = stack.pop(Type.LONG);
		final Expr left = stack.pop(Type.LONG);
		final Expr top = new ArithExpr(ArithExpr.MUL, left, right, Type.LONG);
		stack.push(top);
	}

	public void visit_fmul(final Instruction inst) {
		final Expr right = stack.pop(Type.FLOAT);
		final Expr left = stack.pop(Type.FLOAT);
		final Expr top = new ArithExpr(ArithExpr.MUL, left, right, Type.FLOAT);
		stack.push(top);
	}

	public void visit_dmul(final Instruction inst) {
		final Expr right = stack.pop(Type.DOUBLE);
		final Expr left = stack.pop(Type.DOUBLE);
		final Expr top = new ArithExpr(ArithExpr.MUL, left, right, Type.DOUBLE);
		stack.push(top);
	}

	public void visit_idiv(final Instruction inst) {
		final Expr right = stack.pop(Type.INTEGER);
		final Expr left = stack.pop(Type.INTEGER);
		final Expr check = new ZeroCheckExpr(right, Type.INTEGER);
		final Expr top = new ArithExpr(ArithExpr.DIV, left, check, Type.INTEGER);
		stack.push(top);
	}

	public void visit_ldiv(final Instruction inst) {
		final Expr right = stack.pop(Type.LONG);
		final Expr left = stack.pop(Type.LONG);
		final Expr check = new ZeroCheckExpr(right, Type.LONG);
		final Expr top = new ArithExpr(ArithExpr.DIV, left, check, Type.LONG);
		stack.push(top);
	}

	public void visit_fdiv(final Instruction inst) {
		final Expr right = stack.pop(Type.FLOAT);
		final Expr left = stack.pop(Type.FLOAT);
		final Expr top = new ArithExpr(ArithExpr.DIV, left, right, Type.FLOAT);
		stack.push(top);
	}

	public void visit_ddiv(final Instruction inst) {
		final Expr right = stack.pop(Type.DOUBLE);
		final Expr left = stack.pop(Type.DOUBLE);
		final Expr top = new ArithExpr(ArithExpr.DIV, left, right, Type.DOUBLE);
		stack.push(top);
	}

	public void visit_irem(final Instruction inst) {
		final Expr right = stack.pop(Type.INTEGER);
		final Expr left = stack.pop(Type.INTEGER);
		final Expr check = new ZeroCheckExpr(right, Type.INTEGER);
		final Expr top = new ArithExpr(ArithExpr.REM, left, check, Type.INTEGER);
		stack.push(top);
	}

	public void visit_lrem(final Instruction inst) {
		final Expr right = stack.pop(Type.LONG);
		final Expr left = stack.pop(Type.LONG);
		final Expr check = new ZeroCheckExpr(right, Type.LONG);
		final Expr top = new ArithExpr(ArithExpr.REM, left, check, Type.LONG);
		stack.push(top);
	}

	public void visit_frem(final Instruction inst) {
		final Expr right = stack.pop(Type.FLOAT);
		final Expr left = stack.pop(Type.FLOAT);
		final Expr top = new ArithExpr(ArithExpr.REM, left, right, Type.FLOAT);
		stack.push(top);
	}

	public void visit_drem(final Instruction inst) {
		final Expr right = stack.pop(Type.DOUBLE);
		final Expr left = stack.pop(Type.DOUBLE);
		final Expr top = new ArithExpr(ArithExpr.REM, left, right, Type.DOUBLE);
		stack.push(top);
	}

	/**
	 * All <tt>visit_<i>x</i>neg</tt> push a NegExpr onto the stack.
	 * 
	 * @see NegExpr
	 */
	public void visit_ineg(final Instruction inst) {
		final Expr expr = stack.pop(Type.INTEGER);
		final Expr top = new NegExpr(expr, Type.INTEGER);
		stack.push(top);
	}

	public void visit_lneg(final Instruction inst) {
		final Expr expr = stack.pop(Type.LONG);
		final Expr top = new NegExpr(expr, Type.LONG);
		stack.push(top);
	}

	public void visit_fneg(final Instruction inst) {
		final Expr expr = stack.pop(Type.FLOAT);
		final Expr top = new NegExpr(expr, Type.FLOAT);
		stack.push(top);
	}

	public void visit_dneg(final Instruction inst) {
		final Expr expr = stack.pop(Type.DOUBLE);
		final Expr top = new NegExpr(expr, Type.DOUBLE);
		stack.push(top);
	}

	/**
	 * All <tt>visit_<i>x</i>sh<i>d</i></tt> push a ShiftExpr onto the
	 * operand stack.
	 * 
	 * @see ShiftExpr
	 */
	public void visit_ishl(final Instruction inst) {
		final Expr right = stack.pop(Type.INTEGER);
		final Expr left = stack.pop(Type.INTEGER);
		final Expr top = new ShiftExpr(ShiftExpr.LEFT, left, right,
				Type.INTEGER);
		stack.push(top);
	}

	public void visit_lshl(final Instruction inst) {
		final Expr right = stack.pop(Type.INTEGER);
		final Expr left = stack.pop(Type.LONG);
		final Expr top = new ShiftExpr(ShiftExpr.LEFT, left, right, Type.LONG);
		stack.push(top);
	}

	public void visit_ishr(final Instruction inst) {
		final Expr right = stack.pop(Type.INTEGER);
		final Expr left = stack.pop(Type.INTEGER);
		final Expr top = new ShiftExpr(ShiftExpr.RIGHT, left, right,
				Type.INTEGER);
		stack.push(top);
	}

	public void visit_lshr(final Instruction inst) {
		final Expr right = stack.pop(Type.INTEGER);
		final Expr left = stack.pop(Type.LONG);
		final Expr top = new ShiftExpr(ShiftExpr.RIGHT, left, right, Type.LONG);
		stack.push(top);
	}

	public void visit_iushr(final Instruction inst) {
		final Expr right = stack.pop(Type.INTEGER);
		final Expr left = stack.pop(Type.INTEGER);
		final Expr top = new ShiftExpr(ShiftExpr.UNSIGNED_RIGHT, left, right,
				Type.INTEGER);
		stack.push(top);
	}

	public void visit_lushr(final Instruction inst) {
		final Expr right = stack.pop(Type.INTEGER);
		final Expr left = stack.pop(Type.LONG);
		final Expr top = new ShiftExpr(ShiftExpr.UNSIGNED_RIGHT, left, right,
				Type.LONG);
		stack.push(top);
	}

	/**
	 * All <tt>visit_<i>x op</i></tt> push an ArithExpr onto the stack.
	 * 
	 * @see ArithExpr
	 */
	public void visit_iand(final Instruction inst) {
		final Expr right = stack.pop(Type.INTEGER);
		final Expr left = stack.pop(Type.INTEGER);
		final Expr top = new ArithExpr(ArithExpr.AND, left, right, Type.INTEGER);
		stack.push(top);
	}

	public void visit_land(final Instruction inst) {
		final Expr right = stack.pop(Type.LONG);
		final Expr left = stack.pop(Type.LONG);
		final Expr top = new ArithExpr(ArithExpr.AND, left, right, Type.LONG);
		stack.push(top);
	}

	public void visit_ior(final Instruction inst) {
		final Expr right = stack.pop(Type.INTEGER);
		final Expr left = stack.pop(Type.INTEGER);
		final Expr top = new ArithExpr(ArithExpr.IOR, left, right, Type.INTEGER);
		stack.push(top);
	}

	public void visit_lor(final Instruction inst) {
		final Expr right = stack.pop(Type.LONG);
		final Expr left = stack.pop(Type.LONG);
		final Expr top = new ArithExpr(ArithExpr.IOR, left, right, Type.LONG);
		stack.push(top);
	}

	public void visit_ixor(final Instruction inst) {
		final Expr right = stack.pop(Type.INTEGER);
		final Expr left = stack.pop(Type.INTEGER);
		final Expr top = new ArithExpr(ArithExpr.XOR, left, right, Type.INTEGER);
		stack.push(top);
	}

	public void visit_lxor(final Instruction inst) {
		final Expr right = stack.pop(Type.LONG);
		final Expr left = stack.pop(Type.LONG);
		final Expr top = new ArithExpr(ArithExpr.XOR, left, right, Type.LONG);
		stack.push(top);
	}

	/**
	 * Visiting an iinc involves creating a ConstantExpr, LocalExpr, ArithExpr
	 * StoreExpr, and a ExprStmt.
	 */
	public void visit_iinc(final Instruction inst) {
		final IncOperand operand = (IncOperand) inst.operand();
		int incr = operand.incr();

		if (incr < 0) {
			final Expr right = new ConstantExpr(new Integer(-incr),
					Type.INTEGER);
			final Expr left = new LocalExpr(operand.var().index(), Type.INTEGER);

			final Expr top = new ArithExpr(ArithExpr.SUB, left, right,
					Type.INTEGER);

			final LocalExpr copy = (LocalExpr) left.clone();
			copy.setDef(null);
			addStmt(new ExprStmt(new StoreExpr(copy, top, left.type())));
		} else if (incr > 0) {
			final Expr right = new ConstantExpr(new Integer(incr), Type.INTEGER);
			final Expr left = new LocalExpr(operand.var().index(), Type.INTEGER);

			final Expr top = new ArithExpr(ArithExpr.ADD, left, right,
					Type.INTEGER);

			final LocalExpr copy = (LocalExpr) left.clone();
			copy.setDef(null);
			addStmt(new ExprStmt(new StoreExpr(copy, top, left.type())));
		}
	}

	/**
	 * All cast visitors push a CastExpr onto the operand stack.
	 */
	public void visit_i2l(final Instruction inst) {
		final Expr expr = stack.pop(Type.INTEGER);
		final Expr top = new CastExpr(expr, Type.LONG, Type.LONG);
		stack.push(top);
	}

	public void visit_i2f(final Instruction inst) {
		final Expr expr = stack.pop(Type.INTEGER);
		final Expr top = new CastExpr(expr, Type.FLOAT, Type.FLOAT);
		stack.push(top);
	}

	public void visit_i2d(final Instruction inst) {
		final Expr expr = stack.pop(Type.INTEGER);
		final Expr top = new CastExpr(expr, Type.DOUBLE, Type.DOUBLE);
		stack.push(top);
	}

	public void visit_l2i(final Instruction inst) {
		final Expr expr = stack.pop(Type.LONG);
		final Expr top = new CastExpr(expr, Type.INTEGER, Type.INTEGER);
		stack.push(top);
	}

	public void visit_l2f(final Instruction inst) {
		final Expr expr = stack.pop(Type.LONG);
		final Expr top = new CastExpr(expr, Type.FLOAT, Type.FLOAT);
		stack.push(top);
	}

	public void visit_l2d(final Instruction inst) {
		final Expr expr = stack.pop(Type.LONG);
		final Expr top = new CastExpr(expr, Type.DOUBLE, Type.DOUBLE);
		stack.push(top);
	}

	public void visit_f2i(final Instruction inst) {
		final Expr expr = stack.pop(Type.FLOAT);
		final Expr top = new CastExpr(expr, Type.INTEGER, Type.INTEGER);
		stack.push(top);
	}

	public void visit_f2l(final Instruction inst) {
		final Expr expr = stack.pop(Type.FLOAT);
		final Expr top = new CastExpr(expr, Type.LONG, Type.LONG);
		stack.push(top);
	}

	public void visit_f2d(final Instruction inst) {
		final Expr expr = stack.pop(Type.FLOAT);
		final Expr top = new CastExpr(expr, Type.DOUBLE, Type.DOUBLE);
		stack.push(top);
	}

	public void visit_d2i(final Instruction inst) {
		final Expr expr = stack.pop(Type.DOUBLE);
		final Expr top = new CastExpr(expr, Type.INTEGER, Type.INTEGER);
		stack.push(top);
	}

	public void visit_d2l(final Instruction inst) {
		final Expr expr = stack.pop(Type.DOUBLE);
		final Expr top = new CastExpr(expr, Type.LONG, Type.LONG);
		stack.push(top);
	}

	public void visit_d2f(final Instruction inst) {
		final Expr expr = stack.pop(Type.DOUBLE);
		final Expr top = new CastExpr(expr, Type.FLOAT, Type.FLOAT);
		stack.push(top);
	}

	public void visit_i2b(final Instruction inst) {
		final Expr expr = stack.pop(Type.INTEGER);
		final Expr top = new CastExpr(expr, Type.BYTE, Type.INTEGER);
		stack.push(top);
	}

	public void visit_i2c(final Instruction inst) {
		final Expr expr = stack.pop(Type.INTEGER);
		final Expr top = new CastExpr(expr, Type.CHARACTER, Type.INTEGER);
		stack.push(top);
	}

	public void visit_i2s(final Instruction inst) {
		final Expr expr = stack.pop(Type.INTEGER);
		final Expr top = new CastExpr(expr, Type.SHORT, Type.INTEGER);
		stack.push(top);
	}

	/**
	 * All <tt>visit_<i>x</i>cmp</tt> push an ArithExpr onto the stack.
	 * 
	 * @see ArithExpr
	 */
	public void visit_lcmp(final Instruction inst) {
		final Expr right = stack.pop(Type.LONG);
		final Expr left = stack.pop(Type.LONG);
		final Expr top = new ArithExpr(ArithExpr.CMP, left, right, Type.INTEGER);
		stack.push(top);
	}

	public void visit_fcmpl(final Instruction inst) {
		final Expr right = stack.pop(Type.FLOAT);
		final Expr left = stack.pop(Type.FLOAT);
		final Expr top = new ArithExpr(ArithExpr.CMPL, left, right,
				Type.INTEGER);
		stack.push(top);
	}

	public void visit_fcmpg(final Instruction inst) {
		final Expr right = stack.pop(Type.FLOAT);
		final Expr left = stack.pop(Type.FLOAT);
		final Expr top = new ArithExpr(ArithExpr.CMPG, left, right,
				Type.INTEGER);
		stack.push(top);
	}

	public void visit_dcmpl(final Instruction inst) {
		final Expr right = stack.pop(Type.DOUBLE);
		final Expr left = stack.pop(Type.DOUBLE);
		final Expr top = new ArithExpr(ArithExpr.CMPL, left, right,
				Type.INTEGER);
		stack.push(top);
	}

	public void visit_dcmpg(final Instruction inst) {
		final Expr right = stack.pop(Type.DOUBLE);
		final Expr left = stack.pop(Type.DOUBLE);
		final Expr top = new ArithExpr(ArithExpr.CMPG, left, right,
				Type.INTEGER);
		stack.push(top);
	}

	/**
	 * All <tt>visit_<i>x</i>eg</tt> add an IfZeroStmt to the statement
	 * list.
	 * 
	 * @see IfZeroStmt
	 */
	public void visit_ifeq(final Instruction inst) {
		// It isn't necessary to saveStack since removing critical edges
		// guarantees that no code will be inserted before the branch
		// since there cannot be more than one predecessor of either of
		// the successor nodes (and hence no phi statements, even in SSAPRE).
		// saveStack();

		final Expr left = stack.pop(Type.INTEGER);
		final Block t = (Block) block.graph().getNode(inst.operand());
		Assert.isTrue(t != null, "No block for " + inst);

		addStmt(new IfZeroStmt(IfStmt.EQ, left, t, next));
	}

	public void visit_ifne(final Instruction inst) {
		// It isn't necessary to saveStack since removing critical edges
		// guarantees that no code will be inserted before the branch
		// since there cannot be more than one predecessor of either of
		// the successor nodes (and hence no phi statements, even in SSAPRE).
		// saveStack();

		final Expr left = stack.pop(Type.INTEGER);
		final Block t = (Block) block.graph().getNode(inst.operand());
		Assert.isTrue(t != null, "No block for " + inst);

		addStmt(new IfZeroStmt(IfStmt.NE, left, t, next));
	}

	public void visit_iflt(final Instruction inst) {
		// It isn't necessary to saveStack since removing critical edges
		// guarantees that no code will be inserted before the branch
		// since there cannot be more than one predecessor of either of
		// the successor nodes (and hence no phi statements, even in SSAPRE).
		// saveStack();

		final Expr left = stack.pop(Type.INTEGER);
		final Block t = (Block) block.graph().getNode(inst.operand());
		Assert.isTrue(t != null, "No block for " + inst);

		addStmt(new IfZeroStmt(IfStmt.LT, left, t, next));
	}

	public void visit_ifge(final Instruction inst) {
		// It isn't necessary to saveStack since removing critical edges
		// guarantees that no code will be inserted before the branch
		// since there cannot be more than one predecessor of either of
		// the successor nodes (and hence no phi statements, even in SSAPRE).
		// saveStack();

		final Expr left = stack.pop(Type.INTEGER);
		final Block t = (Block) block.graph().getNode(inst.operand());
		Assert.isTrue(t != null, "No block for " + inst);

		addStmt(new IfZeroStmt(IfStmt.GE, left, t, next));
	}

	public void visit_ifgt(final Instruction inst) {
		// It isn't necessary to saveStack since removing critical edges
		// guarantees that no code will be inserted before the branch
		// since there cannot be more than one predecessor of either of
		// the successor nodes (and hence no phi statements, even in SSAPRE).
		// saveStack();

		final Expr left = stack.pop(Type.INTEGER);
		final Block t = (Block) block.graph().getNode(inst.operand());
		Assert.isTrue(t != null, "No block for " + inst);

		addStmt(new IfZeroStmt(IfStmt.GT, left, t, next));
	}

	public void visit_ifle(final Instruction inst) {
		// It isn't necessary to saveStack since removing critical edges
		// guarantees that no code will be inserted before the branch
		// since there cannot be more than one predecessor of either of
		// the successor nodes (and hence no phi statements, even in SSAPRE).
		// saveStack();

		final Expr left = stack.pop(Type.INTEGER);
		final Block t = (Block) block.graph().getNode(inst.operand());
		Assert.isTrue(t != null, "No block for " + inst);

		addStmt(new IfZeroStmt(IfStmt.LE, left, t, next));
	}

	/**
	 * All <tt>visit_if_<i>x</i>cmp<i>y</i></tt> add a IfCmpStmt to the
	 * statement list.
	 */
	public void visit_if_icmpeq(final Instruction inst) {
		// It isn't necessary to saveStack since removing critical edges
		// guarantees that no code will be inserted before the branch
		// since there cannot be more than one predecessor of either of
		// the successor nodes (and hence no phi statements, even in SSAPRE).
		// saveStack();

		final Expr right = stack.pop(Type.INTEGER);
		final Expr left = stack.pop(Type.INTEGER);
		final Block t = (Block) block.graph().getNode(inst.operand());
		Assert.isTrue(t != null, "No block for " + inst);

		addStmt(new IfCmpStmt(IfStmt.EQ, left, right, t, next));
	}

	public void visit_if_icmpne(final Instruction inst) {
		// It isn't necessary to saveStack since removing critical edges
		// guarantees that no code will be inserted before the branch
		// since there cannot be more than one predecessor of either of
		// the successor nodes (and hence no phi statements, even in SSAPRE).
		// saveStack();

		final Expr right = stack.pop(Type.INTEGER);
		final Expr left = stack.pop(Type.INTEGER);
		final Block t = (Block) block.graph().getNode(inst.operand());
		Assert.isTrue(t != null, "No block for " + inst);

		addStmt(new IfCmpStmt(IfStmt.NE, left, right, t, next));
	}

	public void visit_if_icmplt(final Instruction inst) {
		// It isn't necessary to saveStack since removing critical edges
		// guarantees that no code will be inserted before the branch
		// since there cannot be more than one predecessor of either of
		// the successor nodes (and hence no phi statements, even in SSAPRE).
		// saveStack();

		final Expr right = stack.pop(Type.INTEGER);
		final Expr left = stack.pop(Type.INTEGER);
		final Block t = (Block) block.graph().getNode(inst.operand());
		Assert.isTrue(t != null, "No block for " + inst);

		addStmt(new IfCmpStmt(IfStmt.LT, left, right, t, next));
	}

	public void visit_if_icmpge(final Instruction inst) {
		// It isn't necessary to saveStack since removing critical edges
		// guarantees that no code will be inserted before the branch
		// since there cannot be more than one predecessor of either of
		// the successor nodes (and hence no phi statements, even in SSAPRE).
		// saveStack();

		final Expr right = stack.pop(Type.INTEGER);
		final Expr left = stack.pop(Type.INTEGER);
		final Block t = (Block) block.graph().getNode(inst.operand());
		Assert.isTrue(t != null, "No block for " + inst);

		addStmt(new IfCmpStmt(IfStmt.GE, left, right, t, next));
	}

	public void visit_if_icmpgt(final Instruction inst) {
		// It isn't necessary to saveStack since removing critical edges
		// guarantees that no code will be inserted before the branch
		// since there cannot be more than one predecessor of either of
		// the successor nodes (and hence no phi statements, even in SSAPRE).
		// saveStack();

		final Expr right = stack.pop(Type.INTEGER);
		final Expr left = stack.pop(Type.INTEGER);
		final Block t = (Block) block.graph().getNode(inst.operand());
		Assert.isTrue(t != null, "No block for " + inst);

		addStmt(new IfCmpStmt(IfStmt.GT, left, right, t, next));
	}

	public void visit_if_icmple(final Instruction inst) {
		// It isn't necessary to saveStack since removing critical edges
		// guarantees that no code will be inserted before the branch
		// since there cannot be more than one predecessor of either of
		// the successor nodes (and hence no phi statements, even in SSAPRE).
		// saveStack();

		final Expr right = stack.pop(Type.INTEGER);
		final Expr left = stack.pop(Type.INTEGER);
		final Block t = (Block) block.graph().getNode(inst.operand());
		Assert.isTrue(t != null, "No block for " + inst);

		addStmt(new IfCmpStmt(IfStmt.LE, left, right, t, next));
	}

	public void visit_if_acmpeq(final Instruction inst) {
		// It isn't necessary to saveStack since removing critical edges
		// guarantees that no code will be inserted before the branch
		// since there cannot be more than one predecessor of either of
		// the successor nodes (and hence no phi statements, even in SSAPRE).
		// saveStack();

		final Expr right = stack.pop(Type.OBJECT);
		final Expr left = stack.pop(Type.OBJECT);
		final Block t = (Block) block.graph().getNode(inst.operand());
		Assert.isTrue(t != null, "No block for " + inst);

		addStmt(new IfCmpStmt(IfStmt.EQ, left, right, t, next));
	}

	public void visit_if_acmpne(final Instruction inst) {
		// It isn't necessary to saveStack since removing critical edges
		// guarantees that no code will be inserted before the branch
		// since there cannot be more than one predecessor of either of
		// the successor nodes (and hence no phi statements, even in SSAPRE).
		// saveStack();

		final Expr right = stack.pop(Type.OBJECT);
		final Expr left = stack.pop(Type.OBJECT);
		final Block t = (Block) block.graph().getNode(inst.operand());
		Assert.isTrue(t != null, "No block for " + inst);

		addStmt(new IfCmpStmt(IfStmt.NE, left, right, t, next));
	}

	/**
	 * Adds a GotoStmt to the statement list.
	 * 
	 * @see GotoStmt
	 */
	public void visit_goto(final Instruction inst) {
		final Block t = (Block) block.graph().getNode(inst.operand());
		Assert.isTrue(t != null, "No block for " + inst);
		addStmt(new GotoStmt(t));
	}

	/**
	 * Adds a JsrStmt to the statement list.
	 * 
	 * @see JsrStmt
	 */
	public void visit_jsr(final Instruction inst) {
		// Push the return address after we add the statement.
		// This prevents it from being saved to a local variable.
		// It's illegal to load a return address from a local variable,
		// so we can't save it.
		final Subroutine sub = block.graph().labelSub((Label) inst.operand());
		addStmt(new JsrStmt(sub, next));
		stack.push(new ReturnAddressExpr(Type.ADDRESS));
	}

	/**
	 * Adds a RetStmt to the statement list.
	 * 
	 * @see RetStmt
	 */
	public void visit_ret(final Instruction inst) {
		Assert.isTrue(sub != null);
		addStmt(new RetStmt(sub));
	}

	/**
	 * Add a SwitchStmt to the statement list.
	 * 
	 * @see SwitchStmt
	 */
	public void visit_switch(final Instruction inst) {
		// It isn't necessary to saveStack since removing critical edges
		// guarantees that no code will be inserted before the branch
		// since there cannot be more than one predecessor of either of
		// the successor nodes (and hence no phi statements, even in SSAPRE).
		// saveStack();

		final Expr index = stack.pop(Type.INTEGER);

		final Switch sw = (Switch) inst.operand();

		final Block defaultTarget = (Block) block.graph().getNode(
				sw.defaultTarget());
		Assert.isTrue(defaultTarget != null, "No block for " + inst);

		final Block[] targets = new Block[sw.targets().length];

		for (int i = 0; i < targets.length; i++) {
			targets[i] = (Block) block.graph().getNode(sw.targets()[i]);
			Assert.isTrue(targets[i] != null, "No block for " + inst);
		}

		addStmt(new SwitchStmt(index, defaultTarget, targets, sw.values()));
	}

	/**
	 * All <tt>visit_<i>x</i>return</tt> add a ReturnExprStmt to the
	 * statement list.
	 * 
	 * @see ReturnExprStmt
	 */
	public void visit_ireturn(final Instruction inst) {
		final Expr expr = stack.pop(Type.INTEGER);
		addStmt(new ReturnExprStmt(expr));
	}

	public void visit_lreturn(final Instruction inst) {
		final Expr expr = stack.pop(Type.LONG);
		addStmt(new ReturnExprStmt(expr));
	}

	public void visit_freturn(final Instruction inst) {
		final Expr expr = stack.pop(Type.FLOAT);
		addStmt(new ReturnExprStmt(expr));
	}

	public void visit_dreturn(final Instruction inst) {
		final Expr expr = stack.pop(Type.DOUBLE);
		addStmt(new ReturnExprStmt(expr));
	}

	public void visit_areturn(final Instruction inst) {
		final Expr expr = stack.pop(Type.OBJECT);
		addStmt(new ReturnExprStmt(expr));
	}

	/**
	 * Adds a ReturnStmt to the statement list.
	 */
	public void visit_return(final Instruction inst) {
		addStmt(new ReturnStmt());
	}

	/**
	 * Pushes a StaticFieldExpr onto the operand stack.
	 */
	public void visit_getstatic(final Instruction inst) {
		final MemberRef field = (MemberRef) inst.operand();
		final Type type = field.nameAndType().type();

		try {
			final EditorContext context = block.graph().method()
					.declaringClass().context();
			final FieldEditor e = context.editField(field);

			if (e.isFinal()) {
				if (e.constantValue() != null) {
					final Expr top = new ConstantExpr(e.constantValue(), type);
					stack.push(top);
					context.release(e.fieldInfo());
					return;
				}
			}

			context.release(e.fieldInfo());
		} catch (final NoSuchFieldException e) {
			// No field found. Silently assume non-final.
		}

		final Expr top = new StaticFieldExpr(field, type);
		stack.push(top);
	}

	public void visit_putstatic(final Instruction inst) {
		final MemberRef field = (MemberRef) inst.operand();
		final Type type = field.nameAndType().type();
		final Expr value = stack.pop(type);
		final StaticFieldExpr target = new StaticFieldExpr(field, type);
		addStore(target, value);
	}

	public void visit_putstatic_nowb(final Instruction inst) {
		visit_putstatic(inst);
	}

	/**
	 * Pushes a FieldExpr onto the operand stack.
	 */
	public void visit_getfield(final Instruction inst) {
		final MemberRef field = (MemberRef) inst.operand();
		final Type type = field.nameAndType().type();
		final Expr obj = stack.pop(Type.OBJECT);
		final Expr check = new ZeroCheckExpr(obj, obj.type());
		final Expr top = new FieldExpr(check, field, type);
		stack.push(top);
	}

	public void visit_putfield(final Instruction inst) {
		final MemberRef field = (MemberRef) inst.operand();
		final Type type = field.nameAndType().type();
		final Expr value = stack.pop(type);
		final Expr obj = stack.pop(Type.OBJECT);
		Expr ucCheck = obj;

		if (Tree.USE_PERSISTENT) {
			ucCheck = new UCExpr(obj, UCExpr.POINTER, obj.type());
		}

		final Expr check = new ZeroCheckExpr(ucCheck, obj.type());
		final FieldExpr target = new FieldExpr(check, field, type);
		addStore(target, value);
	}

	// Don't insert UCExpr
	public void visit_putfield_nowb(final Instruction inst) {
		final MemberRef field = (MemberRef) inst.operand();
		final Type type = field.nameAndType().type();
		final Expr value = stack.pop(type);
		final Expr obj = stack.pop(Type.OBJECT);
		final Expr check = new ZeroCheckExpr(obj, obj.type());
		final FieldExpr target = new FieldExpr(check, field, type);
		addStore(target, value);
	}

	/**
	 * All <tt>visit_invoke<i>x</i></tt> deal with a CallMethodExpr or a
	 * CallStaticExpr.
	 * 
	 * @see CallMethodExpr
	 * @see CallStaticExpr
	 */
	public void visit_invokevirtual(final Instruction inst) {
		addCall(inst, CallMethodExpr.VIRTUAL);
	}

	public void visit_invokespecial(final Instruction inst) {
		addCall(inst, CallMethodExpr.NONVIRTUAL);
	}

	public void visit_invokestatic(final Instruction inst) {
		addCall(inst, 0);
	}

	public void visit_invokeinterface(final Instruction inst) {
		addCall(inst, CallMethodExpr.INTERFACE);
	}

	/**
	 * Creates either a CallMethodExpr or a CallStaticExpr to represent a method
	 * call. After obtaining some information about the method. The parameters
	 * to the methods are popped from the operand stack. If the method is not
	 * static, the "this" object is popped from the operand stack. A
	 * CallMethodExpr is created for a non-static method and a CallStaticExpr is
	 * created for a static method. If the method returns a value, the Call*Expr
	 * is pushed onto the stack. If the method has no return value, it is
	 * wrapped in an ExprStmt and is added to the statement list.
	 */
	private void addCall(final Instruction inst, final int kind) {
		final MemberRef method = (MemberRef) inst.operand();
		final Type type = method.nameAndType().type();

		final Type[] paramTypes = type.paramTypes();
		final Expr[] params = new Expr[paramTypes.length];

		for (int i = paramTypes.length - 1; i >= 0; i--) {
			params[i] = stack.pop(paramTypes[i]);
		}

		Expr top;

		if (inst.opcodeClass() != Opcode.opcx_invokestatic) {
			final Expr obj = stack.pop(Type.OBJECT);

			top = new CallMethodExpr(kind, obj, params, method, type
					.returnType());

		} else {
			top = new CallStaticExpr(params, method, type.returnType());
		}

		if (type.returnType().equals(Type.VOID)) {
			addStmt(new ExprStmt(top));

		} else {
			stack.push(top);
		}
	}

	/**
	 * Pushes a NewExpr onto the operand stack.
	 * 
	 * @see NewExpr
	 */
	public void visit_new(final Instruction inst) {
		final Type type = (Type) inst.operand();
		final Expr top = new NewExpr(type, Type.OBJECT);
		stack.push(top);

		db("      new: " + top);
	}

	/**
	 * Pushes a NewArrayExpr onto the operand stack.
	 */
	public void visit_newarray(final Instruction inst) {
		final Type type = (Type) inst.operand();
		final Expr size = stack.pop(Type.INTEGER);
		final Expr top = new NewArrayExpr(size, type, type.arrayType());
		stack.push(top);
	}

	/**
	 * Pushes an ArrayLengthExpr onto the operand stack.
	 * 
	 * @see ArrayLengthExpr
	 */
	public void visit_arraylength(final Instruction inst) {
		final Expr array = stack.pop(Type.OBJECT);
		final Expr top = new ArrayLengthExpr(array, Type.INTEGER);
		stack.push(top);
	}

	/**
	 * Adds a ThrowStmt to the statement list.
	 * 
	 * @see ThrowStmt
	 */
	public void visit_athrow(final Instruction inst) {
		final Expr expr = stack.pop(Type.THROWABLE);
		addStmt(new ThrowStmt(expr));
	}

	/**
	 * Pushes a CastExpr onto the operand stack.
	 * 
	 * @see CastExpr
	 */
	public void visit_checkcast(final Instruction inst) {
		final Expr expr = stack.pop(Type.OBJECT);
		final Type type = (Type) inst.operand();
		final Expr top = new CastExpr(expr, type, type);
		stack.push(top);
	}

	/**
	 * Pushes an InstanceOfExpr onto the operand stack.
	 * 
	 * @see InstanceOfExpr
	 */
	public void visit_instanceof(final Instruction inst) {
		final Type type = (Type) inst.operand();
		final Expr expr = stack.pop(Type.OBJECT);
		final Expr top = new InstanceOfExpr(expr, type, Type.INTEGER);
		stack.push(top);
	}

	/**
	 * Both <tt>monitor</tt> visitors add a MonitorStmt to the statement list.
	 * 
	 * @see MonitorStmt
	 */
	public void visit_monitorenter(final Instruction inst) {
		final Expr obj = stack.pop(Type.OBJECT);
		addStmt(new MonitorStmt(MonitorStmt.ENTER, obj));
	}

	public void visit_monitorexit(final Instruction inst) {
		final Expr obj = stack.pop(Type.OBJECT);
		addStmt(new MonitorStmt(MonitorStmt.EXIT, obj));
	}

	/**
	 * Push a NewMultiArrayExpr onto the operand stack.
	 * 
	 * @see NewMultiArrayExpr
	 */
	public void visit_multianewarray(final Instruction inst) {
		final MultiArrayOperand operand = (MultiArrayOperand) inst.operand();

		final Expr[] dim = new Expr[operand.dimensions()];

		for (int i = dim.length - 1; i >= 0; i--) {
			dim[i] = stack.pop(Type.INTEGER);
		}

		final Type type = operand.type();

		final Expr top = new NewMultiArrayExpr(dim, type
				.elementType(dim.length), type);

		stack.push(top);
	}

	/**
	 * Both <tt>visit_<i>x</i>null</tt> add an IfZeroStmt to the statement
	 * list.
	 * 
	 * ssee IfZeroStmt
	 */
	public void visit_ifnull(final Instruction inst) {
		// It isn't necessary to saveStack since removing critical edges
		// guarantees that no code will be inserted before the branch
		// since there cannot be more than one predecessor of either of
		// the successor nodes (and hence no phi statements, even in SSAPRE).
		// saveStack();

		final Expr left = stack.pop(Type.OBJECT);
		final Block t = (Block) block.graph().getNode(inst.operand());
		Assert.isTrue(t != null, "No block for " + inst);

		addStmt(new IfZeroStmt(IfStmt.EQ, left, t, next));
	}

	public void visit_ifnonnull(final Instruction inst) {
		// It isn't necessary to saveStack since removing critical edges
		// guarantees that no code will be inserted before the branch
		// since there cannot be more than one predecessor of either of
		// the successor nodes (and hence no phi statements, even in SSAPRE).
		// saveStack();

		final Expr left = stack.pop(Type.OBJECT);
		final Block t = (Block) block.graph().getNode(inst.operand());
		Assert.isTrue(t != null, "No block for " + inst);

		addStmt(new IfZeroStmt(IfStmt.NE, left, t, next));
	}

	/**
	 * Replaces the expression on the top of the stack with an RCExpr.
	 * 
	 * @see RCExpr
	 */
	public void visit_rc(final Instruction inst) {
		final Integer depth = (Integer) inst.operand();
		final Expr object = stack.peek(depth.intValue());
		stack.replace(depth.intValue(), new RCExpr(object, object.type()));
	}

	/**
	 * 
	 */
	public void visit_aupdate(final Instruction inst) {
		Integer depth = (Integer) inst.operand();

		if (Tree.AUPDATE_FIX_HACK) {
			// Hack to fix a bug in old bloat-generated code:

			if (depth.intValue() == 1) {
				final Expr object = stack.peek();

				if (object.type().isWide()) {
					depth = new Integer(2);
					inst.setOperand(depth);
					Tree.AUPDATE_FIX_HACK_CHANGED = true;
				}
			}
		}

		final Expr object = stack.peek(depth.intValue());
		stack.replace(depth.intValue(), new UCExpr(object, UCExpr.POINTER,
				object.type()));
	}

	/**
	 * Replace the expression at the stack depth specified in the instruction
	 * with a UCExpr.
	 * 
	 * @see UCExpr
	 */
	public void visit_supdate(final Instruction inst) {
		final Integer depth = (Integer) inst.operand();
		final Expr object = stack.peek(depth.intValue());
		stack.replace(depth.intValue(), new UCExpr(object, UCExpr.SCALAR,
				object.type()));
	}

	/**
	 * Add a SCStmt to the statement list
	 * 
	 * @see SCStmt
	 */
	public void visit_aswizzle(final Instruction inst) {
		final Expr index = stack.pop(Type.INTEGER);
		final Expr array = stack.pop(Type.OBJECT.arrayType());

		addStmt(new SCStmt(array, index));

	}

	/**
	 * Add a SRStmt to the statement list.
	 * 
	 * @see SRStmt
	 */
	public void visit_aswrange(final Instruction inst) {
		final Expr end = stack.pop(Type.INTEGER);
		final Expr start = stack.pop(Type.INTEGER);
		final Expr array = stack.pop(Type.OBJECT.arrayType());

		addStmt(new SRStmt(array, start, end));
	}

	/**
	 * Visit all the statements in the statement list.
	 */
	public void visitForceChildren(final TreeVisitor visitor) {
		final LinkedList list = new LinkedList(stmts);

		if (visitor.reverse()) {
			final ListIterator iter = list.listIterator(stmts.size());

			while (iter.hasPrevious()) {
				final Stmt s = (Stmt) iter.previous();
				s.visit(visitor);
			}
		} else {
			final ListIterator iter = list.listIterator();

			while (iter.hasNext()) {
				final Stmt s = (Stmt) iter.next();
				s.visit(visitor);
			}
		}
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitTree(this);
	}

	public Node parent() {
		return null;
	}

	public Block block() {
		return block;
	}
}
