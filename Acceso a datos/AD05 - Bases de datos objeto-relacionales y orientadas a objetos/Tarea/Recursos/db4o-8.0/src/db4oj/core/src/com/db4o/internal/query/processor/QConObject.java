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
import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.handlers.*;
import com.db4o.marshall.*;
import com.db4o.query.*;
import com.db4o.reflect.*;


/**
 * Object constraint on queries
 *
 * @exclude
 */
public class QConObject extends QCon {

    // the constraining object
	@decaf.Public
    private Object                        i_object;

    // cache for the db4o object ID
	@decaf.Public
    private int                           i_objectID;

    // the YapClass
    transient ClassMetadata            _classMetadata;

    // needed for marshalling the request
    @decaf.Public
    private int                           i_classMetadataID;

    @decaf.Public
    private QField                        i_field;

    transient PreparedComparison _preparedComparison;

    @decaf.Public
    private ObjectAttribute               i_attributeProvider;

    private transient boolean     _checkClassMetadataOnly = false;

    public QConObject() {
        // C/S only
    }

    public QConObject(Transaction a_trans, QCon a_parent, QField a_field,
        Object a_object) {
        super(a_trans);
        i_parent = a_parent;
        if (a_object instanceof Compare) {
            a_object = ((Compare) a_object).compare();
        }
        i_object = a_object;
        i_field = a_field;
    }

    private void associateYapClass(Transaction a_trans, Object a_object) {
        if (a_object == null) {
            //It seems that we need not result the following field
            //i_object = null;
            //i_comparator = Null.INSTANCE;
            //i_classMetadata = null;
            
            // FIXME: Setting the YapClass to null will prevent index use
            // If the field is typed we can guess the right one with the
            // following line. However this does break some SODA test cases.
            // Revisit!
            
//            if(i_field != null){
//                i_classMetadata = i_field.getYapClass();
//            }
            
        } else {
            _classMetadata = a_trans.container()
                .produceClassMetadata(a_trans.reflector().forObject(a_object));
            if (_classMetadata != null) {
                i_object = _classMetadata.getComparableObject(a_object);
                if (a_object != i_object) {
                    i_attributeProvider = _classMetadata.config().queryAttributeProvider();
                    _classMetadata = a_trans.container().produceClassMetadata(a_trans.reflector().forObject(i_object));
                }
                if (_classMetadata != null) {
                    _classMetadata.collectConstraints(a_trans, this, i_object,
                        new Visitor4() {

                            public void visit(Object obj) {
                                addConstraint((QCon) obj);
                            }
                        });
                } else {
                    associateYapClass(a_trans, null);
                }
            } else {
                associateYapClass(a_trans, null);
            }
        }
    }
    
    public boolean canBeIndexLeaf(){
        return i_object == null || ((_classMetadata != null && _classMetadata.isValueType()) || evaluator().identity());
    }
    
    public boolean canLoadByIndex(){
        if(i_field == null){
            return false;
        }
        if(i_field._fieldMetadata == null){
            return false;
        }
        if(! i_field._fieldMetadata.hasIndex()){
            return false;
        }
        if (!i_evaluator.supportsIndex()) {
        	return false;
        }
        
        return i_field._fieldMetadata.canLoadByIndex();
    }

    boolean evaluate(QCandidate a_candidate) {
        try {
            return a_candidate.evaluate(this, i_evaluator);
        } catch (Exception e) {
        	if (Debug4.atHome) {
				e.printStackTrace();
			}
            return false;
        }
    }

    void evaluateEvaluationsExec(final QCandidates a_candidates,
        boolean rereadObject) {
        if (i_field.isQueryLeaf()) {
            boolean hasEvaluation = false;
            Iterator4 i = iterateChildren();
            while (i.moveNext()) {
                if (i.current() instanceof QConEvaluation) {
                    hasEvaluation = true;
                    break;
                }
            }
            if (hasEvaluation) {
                a_candidates.traverse(i_field);
                Iterator4 j = iterateChildren();
                while (j.moveNext()) {
                    ((QCon) j.current()).evaluateEvaluationsExec(a_candidates,false);
                }
            }
        }
    }

    void evaluateSelf() {
        if(DTrace.enabled){
            DTrace.EVALUATE_SELF.log(id());
        }
        if (_classMetadata != null) {
            if (!(_classMetadata instanceof PrimitiveTypeMetadata)) {
            	if (!i_evaluator.identity() && (_classMetadata.typeHandler() instanceof StandardReferenceTypeHandler) ) {
            		_checkClassMetadataOnly = true;
            	}
            	Object transactionalObject = _classMetadata.wrapWithTransactionContext(transaction(), i_object);
                _preparedComparison = _classMetadata.prepareComparison(context(), transactionalObject);
            }
        }
        super.evaluateSelf();
        _checkClassMetadataOnly = false;
    }

    private Context context() {
        return transaction().context();
    }

    void collect(QCandidates a_candidates) {
        if (i_field.isClass()) {
            a_candidates.traverse(i_field);
            a_candidates.filter(i_candidates);
        }
    }

    void evaluateSimpleExec(QCandidates a_candidates) {
    	
    	// TODO: The following can be skipped if we used the index on
    	//       this field to load the objects, if hasOrdering() is false
    	
    	if (i_field.isQueryLeaf() || isNullConstraint()) {
        	a_candidates.traverse(i_field);
            prepareComparison(i_field);
            a_candidates.filter(this);
    	}
    }
    
    PreparedComparison prepareComparison(QCandidate candidate){
    	if(_preparedComparison != null){
    		return _preparedComparison; 
    	}
    	return candidate.prepareComparison(container(), i_object);
    }

    ClassMetadata getYapClass() {
        return _classMetadata;
    }

    public QField getField() {
        return i_field;
    }

    int getObjectID() {
        if (i_objectID == 0) {
            i_objectID = i_trans.container().getID(i_trans, i_object);
            if (i_objectID == 0) {
                i_objectID = -1;
            }
        }
        return i_objectID;
    }

    public boolean hasObjectInParentPath(Object obj) {
        if (obj == i_object) {
            return true;
        }
        return super.hasObjectInParentPath(obj);
    }

    public int identityID() {
        if (i_evaluator.identity()) {
            int id = getObjectID();
            if (id != 0) {
                if( !(i_evaluator instanceof QENot) ){
                    return id;
                }
            }
        }
        return 0;
    }
    
    boolean isNullConstraint() {
        return i_object == null;
    }

    void log(String indent) {
        if (Debug4.queries) {
            super.log(indent);
        }
    }

    String logObject() {
        if (Debug4.queries) {
            if (i_object != null) {
                return i_object.toString();
            }
            return "[NULL]";
        } 
        return "";
    }

    void marshall() {
        super.marshall();
        getObjectID();
        if (_classMetadata != null) {
            i_classMetadataID = _classMetadata.getID();
        }
    }
    
    public boolean onSameFieldAs(QCon other){
        if(! (other instanceof QConObject)){
            return false;
        }
        return i_field == ((QConObject)other).i_field;
    }

    void prepareComparison(QField a_field) {
        if (isNullConstraint() & !a_field.isArray()) {
            _preparedComparison = Null.INSTANCE;
        } else {
            _preparedComparison = a_field.prepareComparison(context(), i_object);
        }
    }

    @Override
    QCon shareParent(Object a_object, BooleanByRef removeExisting) {
        if(i_parent == null){
            return null;
        }
        Object obj = i_field.coerce(a_object);
        if(obj == No4.INSTANCE){
            return null;
        }
        return i_parent.addSharedConstraint(i_field, obj);
    }

    @Override
    QConClass shareParentForClass(ReflectClass a_class, BooleanByRef removeExisting) {
        if(i_parent == null){
            return null;
        }
        QConClass newConstraint = new QConClass(i_trans, i_parent,i_field, a_class);
        i_parent.addConstraint(newConstraint);
        return newConstraint;
    }

    final Object translate(Object candidate) {
        if (i_attributeProvider != null) {
            i_candidates.i_trans.container().activate(i_candidates.i_trans, candidate);
            return i_attributeProvider.attribute(candidate);
        }
        return candidate;
    }

    void unmarshall(Transaction trans) {
        if (i_trans != null) {
        	return;
        }
        super.unmarshall(trans);

        if (i_object == null) {
            _preparedComparison = Null.INSTANCE;
        }
        if (i_classMetadataID != 0) {
            _classMetadata = trans.container().classMetadataForID(i_classMetadataID);
        }
        if (i_field != null) {
            i_field.unmarshall(trans);
        }
        
        if(i_objectID > 0){
            Object obj = trans.container().tryGetByID(trans, i_objectID);
            if(obj != null){
                i_object = obj;
            }
        }
    }

    public void visit(Object obj) {
        QCandidate qc = (QCandidate) obj;
        boolean res = true;
        boolean processed = false;
        if (_checkClassMetadataOnly) {
            ClassMetadata yc = qc.readClassMetadata();
            if (yc != null) {
                res = i_evaluator.not(_classMetadata.getHigherHierarchy(yc) == _classMetadata);
                processed = true;
            }
        }
        if (!processed) {
            res = evaluate(qc);
        }
        visit1(qc.getRoot(), this, res);
    }

    public Constraint contains() {
        synchronized (streamLock()) {
            i_evaluator = i_evaluator.add(new QEContains(true));
            return this;
        }
    }

    public Constraint equal() {
        synchronized (streamLock()) {
            i_evaluator = i_evaluator.add(new QEEqual());
            return this;
        }
    }

    public Object getObject() {
    	return i_object;
    }

    public Constraint greater() {
        synchronized (streamLock()) {
            i_evaluator = i_evaluator.add(new QEGreater());
            return this;
        }
    }

    public Constraint identity() {
        synchronized (streamLock()) {

        	if(i_object==null) {
        		return this;
        	}
        	
            getObjectID();
            i_evaluator = i_evaluator.add(new QEIdentity());
            return this;
        }
    }    

    public Constraint byExample() {
        synchronized (streamLock()) {
            associateYapClass(i_trans, i_object);
            return this;
        }
    }
    
    /*
     * if the i_object is stored in db4o, set the evaluation mode as identity, 
     * otherwise, set the evaluation mode as example.
     */
    void setEvaluationMode() {
        if ((i_object == null) || evaluationModeAlreadySet()) {
            return;
        }

        int id = getObjectID();
        if (id < 0) {
            byExample();
        } else {
            _classMetadata = i_trans.container().produceClassMetadata(
                    i_trans.reflector().forObject(i_object));
            identity();
        }
    }
    
    boolean evaluationModeAlreadySet(){
        return _classMetadata != null;
    }
    
    public Constraint like() {
        synchronized (streamLock()) {
            i_evaluator = i_evaluator.add(new QEContains(false));
            return this;
        }
    }

    public Constraint smaller() {
        synchronized (streamLock()) {
            i_evaluator = i_evaluator.add(new QESmaller());
            return this;
        }
    }

    public Constraint startsWith(boolean caseSensitive) {
        synchronized (streamLock()) {
            i_evaluator = i_evaluator.add(new QEStartsWith(caseSensitive));
            return this;
        }
    }

    public Constraint endsWith(boolean caseSensitive) {
        synchronized (streamLock()) {
            i_evaluator = i_evaluator.add(new QEEndsWith(caseSensitive));
            return this;
        }
    }

    public String toString() {
        String str = "QConObject ";
        if (i_object != null) {
            str += i_object.toString();
        }
        return str;
    }
}