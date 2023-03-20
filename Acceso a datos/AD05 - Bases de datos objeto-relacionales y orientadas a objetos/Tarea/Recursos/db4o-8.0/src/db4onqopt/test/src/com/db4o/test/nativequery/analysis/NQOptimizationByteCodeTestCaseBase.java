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

import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.file.*;

import com.db4o.instrumentation.core.*;
import com.db4o.instrumentation.util.*;
import com.db4o.nativequery.analysis.*;
import com.db4o.nativequery.expr.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.extensions.util.*;

public abstract class NQOptimizationByteCodeTestCaseBase implements TestLifeCycle, OptOutNoFileSystemData {

	public final static int NO_MODIFIERS = 0;
	public final static String CLASSNAME = "invalid.CandidateClass";
	private static final String METHODNAME = "sampleMethod";
	
	protected final LocalVariable THIS_VAR = new LocalVariable(0);
	protected final LocalVariable DATA_VAR = new LocalVariable(1);
	private final LabelGenerator _labelGenerator = new LabelGenerator();
	protected final Label START_LABEL = _labelGenerator.createLabel(true);
	
	public void setUp() throws Exception {
		
	}
	
	public void tearDown() throws Exception {
		IOUtil.deleteDir("invalid");
	}
	
	public void testOptimization() throws Exception {
		BloatLoaderContext bloatUtil = new BloatLoaderContext(new ClassFileLoader());
		ClassEditor clazz = bloatUtil.classEditor(NO_MODIFIERS, CLASSNAME, Type.OBJECT, new Type[]{});
		MethodEditor method = new MethodEditor(clazz, NO_MODIFIERS, Type.BOOLEAN, METHODNAME, new Type[]{ Type.getType(Data.class) }, new Type[]{});
		method.addLabel(START_LABEL);
		generateMethodBody(method);
		method.commit();
		clazz.commit();
		
		FlowGraph flowGraph = new FlowGraph(method);
		BloatExprBuilderVisitor visitor = new BloatExprBuilderVisitor(bloatUtil);
		flowGraph.visit(visitor);
		Expression expression = visitor.expression();
		assertOptimization(expression);
	}
	
	protected MemberRef createMethodReference(Type parent, String name, Type[] args, Type ret) {
		NameAndType nameAndType = new NameAndType(name, Type.getType(args, ret));
		return new MemberRef(parent, nameAndType);
	}

	protected MemberRef createFieldReference(Type parent, String name, Type type) {
		NameAndType nameAndType = new NameAndType(name, type);
		return new MemberRef(parent, nameAndType);
	}
	
	protected Label createLabel() {
		return createLabel(true);
	}
	
	protected Label createLabel(boolean startsBlock) {
		return _labelGenerator.createLabel(startsBlock);
	}
	
	protected abstract void generateMethodBody(MethodEditor method);
	
	protected abstract void assertOptimization(Expression expression) throws Exception;
}
