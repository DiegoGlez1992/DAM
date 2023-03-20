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

import com.db4o.foundation.*;
import com.db4o.internal.*;


/**
 * @exclude
 */
public class QConEvaluation extends QCon {

	private transient Object i_evaluation;

	@decaf.Public
    private byte[] i_marshalledEvaluation;

	@decaf.Public
    private int i_marshalledID;

	public QConEvaluation() {
		// C/S only
	}

	public QConEvaluation(Transaction a_trans, Object a_evaluation) {
		super(a_trans);
		i_evaluation = a_evaluation;
	}

	void evaluateEvaluationsExec(QCandidates a_candidates, boolean rereadObject) {
		if (rereadObject) {
			a_candidates.traverse(new Visitor4() {
				public void visit(Object a_object) {
					((QCandidate) a_object).useField(null);
				}
			});
		}
		a_candidates.filter(this);
	}

    void marshall() {
        super.marshall();
		if(!Platform4.useNativeSerialization()){
			marshallUsingDb4oFormat();
		}else{
    		try{
    			i_marshalledEvaluation = Platform4.serialize(i_evaluation);
    		}catch (Exception e){
    			marshallUsingDb4oFormat();
    		}
		}
	}
    
    private void marshallUsingDb4oFormat(){
    	SerializedGraph serialized = Serializer.marshall(container(), i_evaluation);
    	i_marshalledEvaluation = serialized._bytes;
    	i_marshalledID = serialized._id;
    }

    void unmarshall(Transaction a_trans) {
    	if (i_trans == null) {
    		super.unmarshall(a_trans);
    		
            if(i_marshalledID > 0 || !Platform4.useNativeSerialization()){
            	i_evaluation = Serializer.unmarshall(container(), i_marshalledEvaluation, i_marshalledID);
            }else{
                i_evaluation = Platform4.deserialize(i_marshalledEvaluation);
            }
        }
    }

	public void visit(Object obj) {
		QCandidate candidate = (QCandidate) obj;
		
		// force activation outside the try block
		// so any activation errors bubble up
		forceActivation(candidate); 
		
		try {
			Platform4.evaluationEvaluate(i_evaluation, candidate);
		} catch (Exception e) {
			candidate.include(false);
			// TODO: implement Exception callback for the user coder
			// at least for test cases
		}
		if (!candidate._include) {
			doNotInclude(candidate.getRoot());
		}
	}

	private void forceActivation(QCandidate candidate) {
		candidate.getObject();
	}

	boolean supportsIndex() {
		return false;
	}
}
