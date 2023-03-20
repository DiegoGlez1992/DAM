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
package com.db4o.internal.query.processor;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;


/**
 * 
 * Join constraint on queries
 * 
 * @exclude
 */
public class QConJoin extends QCon {
	
	// FIELDS MUST BE PUBLIC TO BE REFLECTED ON UNDER JDK <= 1.1

	@decaf.Public
    private boolean i_and;
	
	@decaf.Public
    private QCon i_constraint1;
	
	@decaf.Public
    private QCon i_constraint2;
	
	
	public QConJoin(){
		// C/S
	}

	QConJoin(Transaction a_trans, QCon a_c1, QCon a_c2, boolean a_and) {
		super(a_trans);
		i_constraint1 = a_c1;
		i_constraint2 = a_c2;
		i_and = a_and;
	}

	public QCon constraint2() {
	    return i_constraint2;
    }

	public QCon constraint1() {
	    return i_constraint1;
    }

	void doNotInclude(QCandidate a_root) {
		constraint1().doNotInclude(a_root);
		constraint2().doNotInclude(a_root);
	}

	void exchangeConstraint(QCon a_exchange, QCon a_with) {
		super.exchangeConstraint(a_exchange, a_with);
		if (a_exchange == constraint1()) {
			i_constraint1 = a_with;
		}
		if (a_exchange == constraint2()) {
			i_constraint2 = a_with;
		}
	}

	void evaluatePending(
		QCandidate a_root,
		QPending a_pending,
		int a_secondResult) {

		boolean res =
			i_evaluator.not(
				i_and
					? ((a_pending._result + a_secondResult) > 0)
					: (a_pending._result + a_secondResult) > QPending.FALSE);
					
		if (hasJoins()) {
			Iterator4 i = iterateJoins();
			while (i.moveNext()) {
				QConJoin qcj = (QConJoin) i.current();
				if (Debug4.queries) {
					System.out.println(
						"QConJoin creates pending this:"
							+ id()
							+ " Join:"
							+ qcj.id()
							+ " res:"
							+ res);
				}
				a_root.evaluate(new QPending(qcj, this, res));
			}
		} else {
			if (!res) {
				if (Debug4.queries) {
					System.out.println(
						"QConJoin evaluatePending FALSE "
							+ id()
							+ " doNotInclude: "
							+ constraint1().id()
							+ ", "
							+ constraint2().id());
				}
				constraint1().doNotInclude(a_root);
				constraint2().doNotInclude(a_root);
			}else{
				if (Debug4.queries) {
					System.out.println(
						"QConJoin evaluatePending TRUE "
							+ id()
							+ " keeping constraints: "
							+ constraint1().id()
							+ ", "
							+ constraint2().id());
				}
			}

		}
	}

	public QCon getOtherConstraint(QCon a_constraint) {
		if (a_constraint == constraint1()) {
			return constraint2();
		} else if (a_constraint == constraint2()) {
			return constraint1();
		}
		throw new IllegalArgumentException();
	}
	
	String logObject(){
		if (Debug4.queries) {
			String msg = i_and ? "&" : "|";
			return " " + constraint1().id() + msg + constraint2().id();
		}
		return "";
	}
	
	public String toString(){
		String str = "QConJoin " + (i_and ? "AND ": "OR");
		if(constraint1() != null){
			str += "\n   " + constraint1();  
		}
		if(constraint2() != null){
			str += "\n   " + constraint2();  
		}
		return str;
	}

	public boolean isOr() {
		return !i_and;
	}
	
	public void setProcessedByIndex() {
		if(processedByIndex()){
			return;
		}
		super.setProcessedByIndex();
		constraint1().setProcessedByIndex();
		constraint2().setProcessedByIndex();
	}

}
