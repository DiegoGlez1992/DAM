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
package EDU.purdue.cs.bloat.tbaa;

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.tree.*;
import EDU.purdue.cs.bloat.util.*;

/**
 * Performs Type-Based Alias Analysis (TBAA) to determine if one expression can
 * alias another. An expression may alias another expression if there is the
 * possibility that they refer to the same location in memory. BLOAT models
 * expressions that may reference memory locations with <tt>MemRefExpr</tt>s.
 * There are two kinds of "access expressions" in Java: field accesses (<tt>FieldExpr</tt>
 * and <tt>StaticFieldExpr</tt>) and array references (<tt>ArrayRefExpr</tt>).
 * Access paths consist of one or more access expressions. For instance,
 * <tt>a.b[i].c</tt> is an access expression.
 * 
 * <p>
 * 
 * TBAA uses the FieldTypeDecl relation to determine whether or not two access
 * paths may refer to the same memory location.
 * 
 * <p>
 * 
 * <table>
 * <th>
 * <td>AP1 </td>
 * <td>AP2 </td>
 * <td>FieldTypeDecl(AP1, Ap2)</td>
 * </th>
 * <tr>
 * <Td>p </td>
 * <Td>p </td>
 * <td>true </td>
 * </tr>
 * <tr>
 * <td>p.f </td>
 * <td>q.g </td>
 * <td>(f = g) && FieldTypeDecl(p, q)</td>
 * </tr>
 * <tr>
 * <td>p.f </td>
 * <Td>q[i]</td>
 * <td>false </td>
 * </tr>
 * <tr>
 * <td>p[i]</td>
 * <td>q[j]</td>
 * <td>FieldTypeDecl(p, q) </td>
 * </tr>
 * <tr>
 * <td>p </td>
 * <td>q </td>
 * <td>TypeDecl(p, q) </td>
 * </tr>
 * </table>
 * 
 * <p>
 * 
 * The TypeDecl(AP1, AP2) relation is defined as:
 * <p align=center>
 * Subtypes(Type(AP1)) INTERSECT Subtypes(Type(AP2)) != EMPTY
 * </p>
 * 
 * The subtype relationships are determined by the <tt>ClassHierarchy</tt>.
 * 
 * @see ClassHierarchy
 * @see MemRefExpr
 * @see FieldExpr
 * @see StaticFieldExpr
 * @see ArrayRefExpr
 */
public class TBAA {

	/**
	 * Returns true, if expression <tt>a</tt> can alias expression <tt>b</tt>.
	 */
	public static boolean canAlias(final EditorContext context, final Expr a,
			final Expr b) {
		// Only memory reference expressions can have aliases.
		if (!(a instanceof MemRefExpr)) {
			return false;
		}

		// Only memory reference expressions can have aliases.
		if (!(b instanceof MemRefExpr)) {
			return false;
		}

		// Equal expressions can be aliases.
		if (a.equalsExpr(b)) {
			return true;
		}

		MemberRef af = null; // Field accessed by expression a
		MemberRef bf = null; // Field accessed by expression b

		if (a instanceof FieldExpr) {
			af = ((FieldExpr) a).field();
		}

		if (a instanceof StaticFieldExpr) {
			af = ((StaticFieldExpr) a).field();
		}

		if (b instanceof FieldExpr) {
			bf = ((FieldExpr) b).field();
		}

		if (b instanceof StaticFieldExpr) {
			bf = ((StaticFieldExpr) b).field();
		}

		// Arrays and fields cannot alias the same location.
		if ((a instanceof ArrayRefExpr) && (bf != null)) {
			return false;
		}

		// Arrays and fields cannot alias the same location.
		if ((b instanceof ArrayRefExpr) && (af != null)) {
			return false;
		}

		final ClassHierarchy hier = context.getHierarchy();

		// Only type-compatible arrays can alias the same location.
		if ((a instanceof ArrayRefExpr) && (b instanceof ArrayRefExpr)) {
			final ArrayRefExpr aa = (ArrayRefExpr) a;
			final ArrayRefExpr bb = (ArrayRefExpr) b;

			final Type aaIndexType = aa.index().type();
			final Type bbIndexType = bb.index().type();
			final Type aaArrayType = aa.array().type();
			final Type bbArrayType = bb.array().type();

			Assert.isTrue(aaIndexType.isIntegral(), aa.index() + " in " + aa
					+ " (" + aaIndexType + ") is not an integer");
			Assert.isTrue(bbIndexType.isIntegral(), bb.index() + " in " + bb
					+ " (" + bbIndexType + ") is not an integer");
			Assert.isTrue(aaArrayType.isArray()
					|| aaArrayType.equals(Type.OBJECT)
					|| aaArrayType.equals(Type.SERIALIZABLE)
					|| aaArrayType.equals(Type.CLONEABLE)
					|| aaArrayType.isNull(), aa.array() + " in " + aa + " ("
					+ aaArrayType + ") is not an array");
			Assert.isTrue(bbArrayType.isArray()
					|| bbArrayType.equals(Type.OBJECT)
					|| bbArrayType.equals(Type.SERIALIZABLE)
					|| bbArrayType.equals(Type.CLONEABLE)
					|| bbArrayType.isNull(), bb.array() + " in " + bb + " ("
					+ bbArrayType + ") is not an array");

			// Optimization: if constant indices. Only equal indices can
			// alias the same location.
			if ((aa.index() instanceof ConstantExpr)
					&& (bb.index() instanceof ConstantExpr)) {

				final ConstantExpr ai = (ConstantExpr) aa.index();
				final ConstantExpr bi = (ConstantExpr) bb.index();

				if ((ai.value() != null) && (bi.value() != null)) {
					if (!ai.value().equals(bi.value())) {
						return false;
					}
				}
			}

			return TBAA.intersects(hier, aaArrayType, bbArrayType);
		}

		try {
			if (af != null) {
				final FieldEditor e = context.editField(af);

				if (e.isVolatile()) {
					context.release(e.fieldInfo());
					return true;
				}

				if (e.isFinal()) {
					context.release(e.fieldInfo());
					return false;
				}

				context.release(e.fieldInfo());
			}

			if (bf != null) {
				final FieldEditor e = context.editField(bf);

				if (e.isVolatile()) {
					context.release(e.fieldInfo());
					return true;
				}

				if (e.isFinal()) {
					context.release(e.fieldInfo());
					return false;
				}

				context.release(e.fieldInfo());
			}

		} catch (final NoSuchFieldException e) {
			// A field wasn't found. Silently assume there is an alias.
			return true;
		}

		// Only fields with the same name can alias the same location.
		if ((af != null) && (bf != null)) {
			return af.equals(bf);
		}

		// Default case. This shouldn't happen.
		return TBAA.intersects(hier, a.type(), b.type());
	}

	/**
	 * Returns <tt>true</tt> if type a and type c intersect. That is, the two
	 * types have a non-null intersection.
	 */
	private static boolean intersects(final ClassHierarchy hier, final Type a,
			final Type b) {
		return !hier.intersectType(a, b).isNull();
	}
}
