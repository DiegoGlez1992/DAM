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
package com.db4o.nativequery.analysis;

import java.util.*;

import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.tree.*;

import com.db4o.activation.*;
import com.db4o.instrumentation.api.*;
import com.db4o.instrumentation.bloat.*;
import com.db4o.instrumentation.core.*;
import com.db4o.instrumentation.util.*;
import com.db4o.nativequery.*;
import com.db4o.nativequery.expr.*;
import com.db4o.nativequery.expr.build.*;
import com.db4o.nativequery.expr.cmp.*;
import com.db4o.nativequery.expr.cmp.operand.*;
import com.db4o.ta.*;

public class BloatExprBuilderVisitor extends TreeVisitor {

	// TODO discuss: drop or make configurable
	private final static int MAX_DEPTH = 10;

	private final static ExpressionBuilder EXP_BUILDER = new ExpressionBuilder();
	private final static ComparisonExpressionFactory CMP_BUILDER = new ComparisonExpressionFactory(EXP_BUILDER);

	private final Map<Block,ExpressionPart> _seenBlocks = new HashMap<Block,ExpressionPart>();
	private final BloatLoaderContext _context;
	private final LinkedList<MemberRef> _methodStack;
	private final List<ComparisonOperand> _locals;
	
	private Expression _expr;
	private ExpressionPart _retval;
	private int _retCount = 0;
	private int _blockCount = 0;
	private int _topLevelStmtCount = 0;

	public BloatExprBuilderVisitor(BloatLoaderContext bloatUtil) {
		this(bloatUtil, new LinkedList<MemberRef>(), Arrays.<ComparisonOperand>asList(PredicateFieldRoot.INSTANCE, CandidateFieldRoot.INSTANCE));
	}

	private BloatExprBuilderVisitor(BloatLoaderContext bloatUtil, LinkedList<MemberRef> methodStack, List<ComparisonOperand> locals) {
		_context = bloatUtil;
		_methodStack = methodStack;
		_locals = locals;
	}

	private ExpressionPart purgeReturnValue() {
		ExpressionPart expr = _retval;
		retval(null);
		return expr;
	}

	private void expression(Expression expr) {
		retval(expr);
		_expr = expr;
	}

	private void retval(ExpressionPart expr) {
		_retval = expr;
	}

	public Expression expression() {
		if (_expr == null && isSingleReturn() && _retval instanceof ConstValue) {
			expression(asExpression(_retval));
		}
		// COR-2292
		if(_expr == BoolConstExpression.FALSE) {
			return null;
		}
		return (checkComparisons(_expr) ? _expr : null);
	}

	public ExpressionPart returnValue() {
		return purgeReturnValue();
	}
	
	private boolean isSingleReturn() {
		return _retCount == 1 && _blockCount == 4; // one plus source,init,sink
	}

	private boolean checkComparisons(Expression expr) {
		if (expr == null) {
			return true;
		}
		final boolean[] result = { true };
		expr.accept(new TraversingExpressionVisitor() {
			public void visit(ComparisonExpression expression) {
				if (expression.left().root() != CandidateFieldRoot.INSTANCE) {
					result[0] = false;
				}
			}
		});
		return result[0];
	}

	public void visitIfZeroStmt(IfZeroStmt stmt) {
		enterStatement();
		ExpressionPart retval = descend(stmt.expr());
		exitStatement();
		boolean cmpNull = false;
		if (retval instanceof FieldValue) {
			// TODO: merge boolean and number primitive handling
			Expression forced = identityOrBoolComparisonOrNull(retval);
			if (forced != null) {
				retval = forced;
			} 
			else {
				FieldValue fieldVal = (FieldValue) retval;
				Object constVal=null;
				if(fieldVal.field().type().isPrimitive()) {
					constVal=new Integer(0);
				}
				retval = comparisonExpression(fieldVal,
						new ConstValue(constVal), ComparisonOperator.VALUE_EQUALITY);
				cmpNull = true;
			}
		}
		if (retval instanceof Expression) {
			Expression expr = (Expression) retval;
			if (stmt.comparison() == IfStmt.EQ && !cmpNull || stmt.comparison() == IfStmt.NE && cmpNull) {
				expr = EXP_BUILDER.not(expr);
			}
			expression(buildComparison(stmt, expr));
			return;
		}
		if (!(retval instanceof ThreeWayComparison)) {
			throw new EarlyExitException();
		}
		ThreeWayComparison cmp = (ThreeWayComparison) retval;
		Expression expr = null;
		int comparison = stmt.comparison();
		if (cmp.swapped()) {
			comparison = ((Integer) OpSymmetryUtil.counterpart(comparison)).intValue();
		}
		switch (comparison) {
		case IfStmt.EQ:
			expr = comparisonExpression(cmp.left(), cmp.right(),
					ComparisonOperator.VALUE_EQUALITY);
			break;
		case IfStmt.NE:
			expr = EXP_BUILDER.not(comparisonExpression(cmp.left(),
					cmp.right(), ComparisonOperator.VALUE_EQUALITY));
			break;
		case IfStmt.LT:
			expr = comparisonExpression(cmp.left(), cmp.right(),
					ComparisonOperator.SMALLER);
			break;
		case IfStmt.GT:
			expr = comparisonExpression(cmp.left(), cmp.right(),
					ComparisonOperator.GREATER);
			break;
		case IfStmt.LE:
			expr = EXP_BUILDER.not(comparisonExpression(cmp.left(),
					cmp.right(), ComparisonOperator.GREATER));
			break;
		case IfStmt.GE:
			expr = EXP_BUILDER.not(comparisonExpression(cmp.left(),
					cmp.right(), ComparisonOperator.SMALLER));
			break;
		default:
			break;
		}
		expression(buildComparison(stmt, expr));
	}

	private void exitStatement() {
		if(_topLevelStmtCount > 1) {
			if(_retval != IgnoredExpression.INSTANCE) {
				throw new EarlyExitException();
			}
		}
		if(_retval == IgnoredExpression.INSTANCE) {
			_topLevelStmtCount--;
		}
	}

	private void enterStatement() {
			_topLevelStmtCount++;
	}
	
	public void visitIfCmpStmt(IfCmpStmt stmt) {
		enterStatement();
		ExpressionPart left = descend(stmt.left());
		ExpressionPart right = descend(stmt.right());
		exitStatement();
		int op = stmt.comparison();
		if ((left instanceof ComparisonOperand) && (right instanceof FieldValue)) {
			FieldValue rightField = (FieldValue) right;
			if (rightField.root() == CandidateFieldRoot.INSTANCE) {
				ExpressionPart swap = left;
				left = right;
				right = swap;
				op = OpSymmetryUtil.counterpart(op);
			}
		}
		if (!(left instanceof FieldValue) || !(right instanceof ComparisonOperand)) {
			throw new EarlyExitException();
		}
		FieldValue fieldExpr = (FieldValue) left;
		ComparisonOperand valueExpr = (ComparisonOperand) right;

        boolean isPrimitive = isPrimitiveExpr(stmt.left());
        Expression cmp = buildComparison(stmt, CMP_BUILDER.buildComparison(op, isPrimitive, fieldExpr, valueExpr));
		expression(cmp);
	}

	public void visitExprStmt(ExprStmt stmt) {
		enterStatement();
		super.visitExprStmt(stmt);
		exitStatement();
	}

	public void visitCallExpr(CallExpr expr) {
		boolean isStatic = (expr instanceof CallStaticExpr);
		if (!isStatic && expr.method().name().equals("<init>")) {
			throw new EarlyExitException();
		}
		if (!isStatic && expr.method().name().equals("equals")) {
			CallMethodExpr call = (CallMethodExpr) expr;
			if (TypeRefUtil.isPrimitiveWrapper(call.receiver().type())) {
				processEqualsCall(call, ComparisonOperator.VALUE_EQUALITY);
			}
			return;
		}
		if (!isStatic && isActivateMethod(expr.method())) {
			retval(IgnoredExpression.INSTANCE);
			return;
		}
		if(expr.method().declaringClass().equals(Type.STRING)) {
			if(applyStringHandling(expr)) {
				return;
			}
			else {
				throw new EarlyExitException();
			}
		}
		if(isCollectionClass(expr.method().declaringClass())) {
			if(applyCollectionHandling(expr)) {
				return;
			}
		}

		ComparisonOperandAnchor receiver = null;
		if (!isStatic) {
			receiver = descend(((CallMethodExpr) expr).receiver());
		}
		if(TypeRefUtil.isPrimitiveWrapper(expr.method().declaringClass())) {
			if(applyPrimitiveWrapperHandling(expr,receiver)) {
				return;
			}
		}
		MemberRef methodRef = expr.method();
		if (_methodStack.contains(methodRef) || _methodStack.size() > MAX_DEPTH) {
			throw new EarlyExitException();
		}
		_methodStack.addLast(methodRef);
		try {
			List<ComparisonOperand> params = collectMethodParams(expr, receiver);

			if(handledAsSafeMethod(expr, receiver, params)) {
				return;
			}

			ExpressionPart methodRetval = descendIntoMethodCall(expr, params);
			if(methodRetval==null) {
				throw new EarlyExitException();
			}
			if(methodRetval != IgnoredExpression.INSTANCE && containsCandidateAsParam(params)) {
				throw new EarlyExitException();
			}
			
			retval(methodRetval);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			Object last = _methodStack.removeLast();
			if (!last.equals(methodRef)) {
				throw new RuntimeException("method stack inconsistent: push=" + methodRef + " , pop=" + last);
			}
		}
	}

	private ExpressionPart descendIntoMethodCall(CallExpr expr, List<ComparisonOperand> params)
			throws ClassNotFoundException {
		Type declaringClass = detectDeclaringClass(expr);
		MemberRef methodRef = expr.method();
		FlowGraph flowGraph = _context.flowGraph(declaringClass.className(), methodRef.name(),methodRef.type().paramTypes());
		if (flowGraph == null) {
			throw new EarlyExitException();
		}
		if (NQDebug.LOG) {
			System.out
					.println("METHOD:" + flowGraph.method().nameAndType());
			flowGraph.visit(new PrintVisitor());
		}
		BloatExprBuilderVisitor visitor = new BloatExprBuilderVisitor(_context, _methodStack, params);
		flowGraph.visit(visitor);
		return visitor.returnValue();
	}

	private boolean containsCandidateAsParam(List<ComparisonOperand> params) {
		for (ComparisonOperand param : excludeReceiverParam(params)) {
			if ((param instanceof ComparisonOperandAnchor)
					&& (((ComparisonOperandAnchor) param).root() == CandidateFieldRoot.INSTANCE)) {
				return true;
			}
		}
		return false;
	}

	private List<ComparisonOperand> excludeReceiverParam(List<ComparisonOperand> params) {
		return params.subList(1, params.size());
	}

	private Type detectDeclaringClass(CallExpr expr) throws ClassNotFoundException {
		Type declaringClass = expr.method().declaringClass();
		// Nice try, but doesn't help, since the receiver's type always seems to be reported as java.lang.Object.
		if(expr instanceof CallMethodExpr) {
			Expr receiverExpr=((CallMethodExpr)expr).receiver();
			Type receiverType=receiverExpr.type();
			if(isSuperType(declaringClass,receiverType)) {
				declaringClass=receiverType;
			}
		}
		return declaringClass;
	}

	private List<ComparisonOperand> collectMethodParams(CallExpr expr, ComparisonOperandAnchor receiver) {
		List<ComparisonOperand> params = new ArrayList<ComparisonOperand>(expr.params().length + 1);
		params.add(receiver);
		for (int idx = 0; idx < expr.params().length; idx++) {
			ComparisonOperand curparam = descend(expr.params()[idx]);
			params.add(curparam);
		}
		return params;
	}

	private boolean handledAsSafeMethod(CallExpr expr, ComparisonOperandAnchor rcvRetval, List<ComparisonOperand> params) {
		if(!isSafe(rcvRetval)) {
			return false;
		}
		for (ComparisonOperand param : params) {
			if(!isSafe(param)) {
				return false;
			}
		}
		if (rcvRetval == null) {
			rcvRetval = new StaticFieldRoot(typeRef(expr.method().declaringClass()));
		}
		params.remove(0);
		retval(
			new MethodCallValue(
				methodRef(expr.method()),
				callingConvention(expr),
				rcvRetval,
				(ComparisonOperand[]) params.toArray(new ComparisonOperand[params.size()])));
		return true;
	}
	
	private boolean isSafe(ComparisonOperand op) {
		if(!(op instanceof ComparisonOperandAnchor)) {
			return true;
		}
		ComparisonOperandAnchor anchor = (ComparisonOperandAnchor) op;
		if(anchor.root() == CandidateFieldRoot.INSTANCE) {
			return false;
		}
		if(!(anchor instanceof MethodCallValue)) {
			return true;
		}
		MethodCallValue call = (MethodCallValue) anchor;
		for (ComparisonOperand arg : call.args()) {
			if(!isSafe(arg)) {
				return false;
			}
		}
		return true;
	}
	
	private boolean isActivateMethod(MemberRef method) {
		if (!method.name().equals("activate")) {
			return false;
		}
		Type[] params = method.type().paramTypes();
		if(params.length != 1) {
			return false;
		}
		if(!ActivationPurpose.class.getName().equals(BloatUtil.normalizeClassName(params[0]))) {
			return false;
		}
		try {
			ClassEditor activateClazz = _context.classEditor(method.declaringClass());
			return BloatUtil.implementsInHierarchy(activateClazz, Activatable.class, _context);
		} 
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private CallingConvention callingConvention(CallExpr expr) {
		if (expr instanceof CallStaticExpr) {
			return CallingConvention.STATIC;
		}
		CallMethodExpr cme = (CallMethodExpr)expr;
		if (cme.kind() == CallMethodExpr.INTERFACE) {
			return CallingConvention.INTERFACE;
		}
		return CallingConvention.VIRTUAL;
	}

	private MethodRef methodRef(MemberRef method) {
		return references().forBloatMethod(method);
	}

	private boolean isSuperType(Type declaringClass, Type receiverType) throws ClassNotFoundException {
		if(declaringClass.className().equals(receiverType.className())) {
			return false;
		}
		ClassEditor receiverEditor=_context.classEditor(receiverType.className());
		Type superClass = receiverEditor.superclass();
		if(superClass!=null) {
			if(superClass.className().equals(declaringClass.className())) {
				return true;
			}
			if(isSuperType(declaringClass,superClass)) {
				return true;
			}
		}
		Type[] interfaces=receiverEditor.interfaces();
		for (int interfaceIdx = 0; interfaceIdx < interfaces.length; interfaceIdx++) {
			if(interfaces[interfaceIdx].className().equals(declaringClass.className())) {
				return true;
			}
			if(isSuperType(declaringClass, interfaces[interfaceIdx])) {
				return true;
			}
		}
		return false;
	}

	private boolean applyPrimitiveWrapperHandling(CallExpr expr,ComparisonOperandAnchor rcvRetval) {
		String methodName = expr.method().name();
		if(methodName.endsWith("Value")) {
			return handlePrimitiveWrapperValueCall(rcvRetval);
		}
		if(methodName.equals("compareTo")) {
			return handlePrimitiveWrapperCompareToCall(expr, rcvRetval);
		}
		return false;
	}

	private boolean handlePrimitiveWrapperCompareToCall(CallExpr expr, ComparisonOperandAnchor receiver) {
		ComparisonOperand left=receiver;
		ComparisonOperand right = descend(expr.params()[0]);
		retval(new ThreeWayComparison((FieldValue)left,right,false));
		return true;
	}

	private boolean handlePrimitiveWrapperValueCall(ComparisonOperandAnchor rcvRetval) {
		retval(rcvRetval);
		if(rcvRetval instanceof FieldValue) {
			FieldValue fieldval=(FieldValue)rcvRetval;
			if(TypeRefUtil.isBooleanField(fieldval)) {
				retval(comparisonExpression(fieldval,new ConstValue(Boolean.TRUE),ComparisonOperator.VALUE_EQUALITY, true));
			}
			if(fieldval.root().equals(CandidateFieldRoot.INSTANCE)) {
				return true;
			}
		}
		return false;
	}

	private boolean applyStringHandling(CallExpr expr) {
		if (expr.method().name().equals("contains")) {
			processEqualsCall((CallMethodExpr) expr,
					ComparisonOperator.CONTAINS);
			return true;
		}
		if (expr.method().name().equals("startsWith")) {
			processEqualsCall((CallMethodExpr) expr,
					ComparisonOperator.STARTS_WITH);
			return true;
		}
		if (expr.method().name().equals("endsWith")) {
			processEqualsCall((CallMethodExpr) expr,
					ComparisonOperator.ENDS_WITH);
			return true;
		}
		return false;
	}

	private boolean isCollectionClass(Type type) {
		try {
			ClassEditor editor = _context.classEditor(type);
			return BloatUtil.implementsInHierarchy(editor, Collection.class, _context) ||
					BloatUtil.implementsInHierarchy(editor, Map.class, _context);
		} 
		catch (ClassNotFoundException exc) {
			exc.printStackTrace();
			return false;
		}
	}

	private boolean applyCollectionHandling(CallExpr expr) {
		String methodName = expr.method().name();
		if (methodName.equals("size")) {
			throw new EarlyExitException();
		}
		if (methodName.equals("isEmpty")) {
			throw new EarlyExitException();
		}
		return false;
	}

	private boolean isPrimitiveExpr(Expr expr) {
		return expr.type().isPrimitive();
	}

	private void processEqualsCall(CallMethodExpr expr, ComparisonOperator op) {
		Expr left = expr.receiver();
		Expr right = expr.params()[0];
		if (!isComparableExprOperand(left) || !isComparableExprOperand(right)) {
			throw new EarlyExitException();
		}
		ExpressionPart leftObj = descend(left);
		if (!(leftObj instanceof ComparisonOperand)) {
			throw new EarlyExitException();
		}
		ComparisonOperand leftOp = (ComparisonOperand) leftObj;
		ComparisonOperand rightOp = descend(right);
		if (op.isSymmetric() && isCandidateFieldValue(rightOp) && !isCandidateFieldValue(leftOp)) {
			ComparisonOperand swap = leftOp;
			leftOp = rightOp;
			rightOp = swap;
		}
		if (!isCandidateFieldValue(leftOp) || rightOp == null) {
			throw new EarlyExitException();
		}
		expression(comparisonExpression((FieldValue) leftOp, rightOp, op));
	}

	private static boolean isCandidateFieldValue(ComparisonOperand op) {
		return ((op instanceof FieldValue) && ((FieldValue) op).root() == CandidateFieldRoot.INSTANCE);
	}

	private boolean isComparableExprOperand(Expr expr) {
		return (expr instanceof FieldExpr) || (expr instanceof StaticFieldExpr)
				|| (expr instanceof CallMethodExpr)
				|| (expr instanceof CallStaticExpr)
				|| (expr instanceof ConstantExpr)
				|| (expr instanceof LocalExpr);
	}

	public void visitFieldExpr(FieldExpr expr) {
		ExpressionPart fieldObj = descend(expr.object());
		if (fieldObj instanceof ComparisonOperandAnchor) {
			retval(fieldValue((ComparisonOperandAnchor) fieldObj,
					fieldRef(expr.field())));
		}
	}

	public void visitStaticFieldExpr(StaticFieldExpr expr) {
		MemberRef field = expr.field();
		retval(fieldValue(new StaticFieldRoot(typeRef(field
				.declaringClass())), fieldRef(field)));
	}

	private FieldRef fieldRef(MemberRef field) {
		return references().forBloatField(field);
	}

	private TypeRef typeRef(Type type) {
		return references().forBloatType(type);
	}

	private BloatReferenceProvider references() {
		return _context.references();
	}

	public void visitConstantExpr(ConstantExpr expr) {
		super.visitConstantExpr(expr);
		retval(new ConstValue(expr.value()));
	}

	public void visitLocalExpr(LocalExpr expr) {
		super.visitLocalExpr(expr);
		if (expr.index() >= _locals.size()) {
			throw new EarlyExitException();
		}
		retval(_locals.get(expr.index()));
	}

	public void visitBlock(Block block) {
		if (_seenBlocks.containsKey(block)) {
			retval(_seenBlocks.get(block));
			return;
		} 
		_topLevelStmtCount = 0;
		super.visitBlock(block);
		_seenBlocks.put(block, _retval);
		_blockCount++;
	}

	public void visitFlowGraph(FlowGraph graph) {
		try {
			super.visitFlowGraph(graph);
			if (_expr == null) {
				Expression forced = identityOrBoolComparisonOrNull(_retval);
				if (forced != null) {
					expression(forced);
				}
			}
		} 
		catch (EarlyExitException exc) {
			expression(null);
		}
	}

	private Expression identityOrBoolComparisonOrNull(Object val) {
		if (val instanceof Expression) {
			return (Expression) val;
		}
		if (!(val instanceof FieldValue)) {
			return null;
		}
		FieldValue fieldVal = (FieldValue) val;
		if (fieldVal.root() != CandidateFieldRoot.INSTANCE) {
			return null;
		}
		TypeRef fieldType = fieldVal.field().type();
		if (!fieldType.isPrimitive()) {
			return null;
		}
		 
		if (!TypeRefUtil.isPrimitiveBoolean(fieldType)) {
			return null;
		}
		return comparisonExpression(
						fieldVal,
						new ConstValue(Boolean.TRUE),
						ComparisonOperator.VALUE_EQUALITY);
	}

	public void visitArithExpr(ArithExpr expr) {
		ExpressionPart leftObj = descend(expr.left());
		if (!(leftObj instanceof ComparisonOperand)) {
			throw new EarlyExitException();
		}
		ComparisonOperand left = (ComparisonOperand) leftObj;
		ExpressionPart rightObj = descend(expr.right());
		if (!(rightObj instanceof ComparisonOperand)) {
			throw new EarlyExitException();
		}
		ComparisonOperand right = (ComparisonOperand) rightObj;
		boolean swapped = false;
		if (right instanceof FieldValue) {
			FieldValue rightField = (FieldValue) right;
			if (rightField.root() == CandidateFieldRoot.INSTANCE) {
				ComparisonOperand swap = left;
				left = right;
				right = swap;
				swapped = true;
			}
		}
		ArithmeticOperator arithOp = arithmeticOperator(expr.operation());
		if(arithOp != null) {
			retval(new ArithmeticExpression(left, right, arithOp));
			return;
		}
		switch (expr.operation()) {
			case ArithExpr.CMP:
			case ArithExpr.CMPG:
			case ArithExpr.CMPL:
				if (left instanceof FieldValue) {
					retval(new ThreeWayComparison((FieldValue) left, right, swapped));
				}
				break;
			case ArithExpr.XOR:
				if (left instanceof FieldValue) {
					retval(EXP_BUILDER.not(comparisonExpression((FieldValue) left,
							right, ComparisonOperator.VALUE_EQUALITY)));
				}
				break;
			default:
				throw new EarlyExitException();
		}
	}

	public void visitArrayRefExpr(ArrayRefExpr expr) {
		ComparisonOperandDescendant arrayOp = descend(expr.array());
		ComparisonOperand idxOp = descend(expr.index());
		if (arrayOp == null || idxOp == null
				|| arrayOp.root() == CandidateFieldRoot.INSTANCE) {
			throw new EarlyExitException();
		}
		retval(new ArrayAccessValue(arrayOp, idxOp));
	}

	public void visitReturnExprStmt(ReturnExprStmt stat) {
		enterStatement();
		stat.expr().visit(this);
		exitStatement();
		_retCount++;
	}

	private ArithmeticOperator arithmeticOperator(int bloatOp) {
		switch (bloatOp) {
			case ArithExpr.ADD:
				return ArithmeticOperator.ADD;
			case ArithExpr.SUB:
				return ArithmeticOperator.SUBTRACT;
			case ArithExpr.MUL:
				return ArithmeticOperator.MULTIPLY;
			case ArithExpr.DIV:
				return ArithmeticOperator.DIVIDE;
			case ArithExpr.REM:
				return ArithmeticOperator.MODULO;
			default:
				return null;
		}
	}

	private Expression buildComparison(IfStmt stmt, Expression cmp) {
		stmt.trueTarget().visit(this);
		ExpressionPart trueVal = purgeReturnValue();
		stmt.falseTarget().visit(this);
		ExpressionPart falseVal = purgeReturnValue();
		Expression trueExpr = asExpression(trueVal);
		Expression falseExpr = asExpression(falseVal);
		if (trueExpr == null || falseExpr == null) {
			return null;
		}
		return EXP_BUILDER.ifThenElse(cmp, trueExpr, falseExpr);
	}

	private Expression asExpression(Object obj) {
		if (obj instanceof Expression) {
			return (Expression) obj;
		}
		if (obj instanceof ConstValue) {
			Object val = ((ConstValue) obj).value();
			return asExpression(val);
		}
		if (obj instanceof Boolean) {
			return BoolConstExpression.expr(((Boolean) obj).booleanValue());
		}
		if (obj instanceof Integer) {
			int exprval = ((Integer) obj).intValue();
			if (exprval == 0 || exprval == 1) {
				return BoolConstExpression.expr(exprval == 1);
			}
		}
		return null;
	}
	
	public void visitStoreExpr(StoreExpr expr) {
		if(!(expr.target() instanceof StackExpr)) {
			throw new EarlyExitException();
		}
		super.visitStoreExpr(expr);
	}
	
	private static ComparisonExpression comparisonExpression(FieldValue left, ComparisonOperand right, ComparisonOperator op) {
		return comparisonExpression(left, right, op, false);
	}

	private static ComparisonExpression comparisonExpression(FieldValue left, ComparisonOperand right, ComparisonOperator op, boolean lenient) {
		if (!lenient && !isCandidateFieldValue(left) || right == null) {
			throw new EarlyExitException();
		}
		return new ComparisonExpression((FieldValue) left, right, op);
	}
	
	private static ComparisonOperand fieldValue(ComparisonOperandAnchor parent, FieldRef field) {
		if(parent instanceof ComparisonOperandDescendant) {
			ComparisonOperandDescendant descendant = (ComparisonOperandDescendant) parent;
			if("value".equals(field.name()) && TypeRefUtil.isPrimitiveWrapper(descendant.type())) {
				return parent;
			}
		}
		return new FieldValue(parent, field);
	}

	private <T extends ExpressionPart> T descend(Node node) {
		node.visit(this);
		return (T)purgeReturnValue();
	}
	
	private static class EarlyExitException extends RuntimeException {
	}
}
