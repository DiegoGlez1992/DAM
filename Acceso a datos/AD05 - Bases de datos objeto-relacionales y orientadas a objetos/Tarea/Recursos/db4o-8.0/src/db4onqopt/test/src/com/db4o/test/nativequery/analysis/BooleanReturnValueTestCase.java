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
package com.db4o.test.nativequery.analysis;

import EDU.purdue.cs.bloat.editor.*;

import com.db4o.nativequery.expr.*;
import com.db4o.nativequery.expr.cmp.*;

public class BooleanReturnValueTestCase extends
		NQOptimizationByteCodeTestCaseBase {

	private static final String FIELDNAME = "bool";

	protected void assertOptimization(Expression expression) throws Exception {
		NQOptimizationAssertUtil.assertComparison(expression, new String[]{ FIELDNAME }, Boolean.TRUE, ComparisonOperator.VALUE_EQUALITY, false);
	}

	protected void generateMethodBody(MethodEditor method) {
		Label falseLabel = createLabel();
		Label trueLabel = createLabel();
		Label returnLabel = createLabel(); 
		method.addInstruction(Opcode.opc_aload, new LocalVariable(1));
		method.addInstruction(Opcode.opc_getfield, createFieldReference(Type.getType(Data.class), FIELDNAME, Type.BOOLEAN));
		method.addInstruction(Opcode.opc_ldc, new Integer(1));
		method.addInstruction(Opcode.opc_if_icmpne, falseLabel);
		method.addLabel(trueLabel);
		method.addInstruction(Opcode.opc_ldc, new Integer(1));
		method.addInstruction(Opcode.opc_goto, returnLabel);
		method.addLabel(falseLabel);
		method.addInstruction(Opcode.opc_ldc, new Integer(0));
		method.addLabel(returnLabel);
		method.addInstruction(Opcode.opc_ireturn);
	}

}
