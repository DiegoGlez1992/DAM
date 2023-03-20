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
import com.db4o.internal.classindex.*;
import com.db4o.internal.diagnostic.*;
import com.db4o.internal.fieldindex.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.marshall.*;
import com.db4o.typehandlers.*;


/**
 * Holds the tree of {@link QCandidate} objects and the list of {@link QCon} during query evaluation.
 * The query work (adding and removing nodes) happens here.
 * Candidates during query evaluation. {@link QCandidate} objects are stored in i_root
 * 
 * @exclude
 */
public final class QCandidates implements Visitor4 {

    // Transaction necessary as reference to stream
    public final LocalTransaction i_trans;

    // root of the QCandidate tree
    public Tree i_root;

    // collection of all constraints
    private List4 _constraints;

    // possible class information
    ClassMetadata i_classMetadata;

    // possible field information
    private QField _field;

    // current executing constraint, only set where needed
    QCon i_currentConstraint;

    private IDGenerator _idGenerator;
    
    private boolean _loadedFromClassIndex;

    QCandidates(LocalTransaction a_trans, ClassMetadata a_classMetadata, QField a_field) {
    	i_trans = a_trans;
    	i_classMetadata = a_classMetadata;
    	_field = a_field;
   
    	if (a_field == null
    			|| a_field._fieldMetadata == null
				|| !(a_field._fieldMetadata.getHandler() instanceof StandardReferenceTypeHandler)
    	) {
    		return;
    	}

    	ClassMetadata yc = ((StandardReferenceTypeHandler) a_field._fieldMetadata.getHandler()).classMetadata();
    	if (i_classMetadata == null) {
    		i_classMetadata = yc;
    	} else {
    		yc = i_classMetadata.getHigherOrCommonHierarchy(yc);
    		if (yc != null) {
    			i_classMetadata = yc;
    		}
    	}
    }

    public QCandidate add(QCandidate candidate) {
        if(Debug4.queries){
            String msg = "Candidate added ID: " + candidate._key;
            QCandidate root = candidate.getRoot();
            if(root != null){
            	msg += " root: " + root._key;
            }
			System.out.println(msg);
        }
        i_root = Tree.add(i_root, candidate);
        if(candidate._size == 0){
        	
        	// This means that the candidate was already present
        	// and QCandidate does not allow duplicates.
        	
        	// In this case QCandidate#isDuplicateOf will have
        	// placed the existing QCandidate in the i_root
        	// variable of the new candidate. We return it here: 
        	
        	return candidate.getRoot();
        
        }
        return candidate;
    }

    void addConstraint(QCon a_constraint) {
        _constraints = new List4(_constraints, a_constraint);
    }
    
    public QCandidate readSubCandidate(QueryingReadContext context, TypeHandler4 handler){
        ObjectID objectID = ObjectID.NOT_POSSIBLE;
        try {
            int offset = context.offset();
            if(handler instanceof ReadsObjectIds){
                objectID = ((ReadsObjectIds)handler).readObjectID(context);
            }
            if(objectID.isValid()){
                return new QCandidate(this, null, objectID._id);
            }
            if(objectID == ObjectID.NOT_POSSIBLE){
                context.seek(offset);
                Object obj = context.read(handler);
                if(obj != null){
                	QCandidate candidate = new QCandidate(this, obj, context.container().getID(context.transaction(), obj));
                	candidate.classMetadata(context.container().classMetadataForObject(obj));
                    return candidate;
                }
            }
            
        } catch (Exception e) {
            
            // FIXME: Catchall
            
        }
        return null;
    }
    
	void collect(final QCandidates a_candidates) {
		Iterator4 i = iterateConstraints();
		while(i.moveNext()){
			QCon qCon = (QCon)i.current();
			setCurrentConstraint(qCon);
			qCon.collect(a_candidates);
		}
		setCurrentConstraint(null);
    }

    void execute() {
        if(DTrace.enabled){
            DTrace.QUERY_PROCESS.log();
        }
        final FieldIndexProcessorResult result = processFieldIndexes();
        if(result.foundIndex()){
        	i_root = result.toQCandidate(this);
        }else{
        	loadFromClassIndex();
        }
        evaluate();
    }
    
    public Iterator4 executeSnapshot(Collection4 executionPath){
    	IntIterator4 indexIterator = new IntIterator4Adaptor(iterateIndex(processFieldIndexes()));
    	Tree idRoot = TreeInt.addAll(null, indexIterator);
    	Iterator4 snapshotIterator = new TreeKeyIterator(idRoot);
    	Iterator4 singleObjectQueryIterator  = singleObjectSodaProcessor(snapshotIterator);
		return mapIdsToExecutionPath(singleObjectQueryIterator, executionPath);
    }
    
    private Iterator4 singleObjectSodaProcessor(Iterator4 indexIterator){
    	return Iterators.map(indexIterator, new Function4() {
			public Object apply(Object current) {
				int id = ((Integer)current).intValue();
				QCandidate candidate = new QCandidate(QCandidates.this, null, id); 
				i_root = candidate; 
				evaluate();
				if(! candidate.include()){
					return Iterators.SKIP;
				}
				return current;
			}
		});
    }
    
    public Iterator4 executeLazy(Collection4 executionPath){
    	Iterator4 indexIterator = iterateIndex(processFieldIndexes());
    	Iterator4 singleObjectQueryIterator  = singleObjectSodaProcessor(indexIterator);
		return mapIdsToExecutionPath(singleObjectQueryIterator, executionPath);
    }
    
    private Iterator4 iterateIndex (FieldIndexProcessorResult result ){
    	if(result.noMatch()){
    		return Iterators.EMPTY_ITERATOR;
    	}
    	if(result.foundIndex()){
    		return result.iterateIDs();
    	}
    	if(!i_classMetadata.hasClassIndex()) {
    		return Iterators.EMPTY_ITERATOR;
    	}
    	return BTreeClassIndexStrategy.iterate(i_classMetadata, i_trans);
    }

	private Iterator4 mapIdsToExecutionPath(Iterator4 singleObjectQueryIterator, Collection4 executionPath) {
		
		if(executionPath == null){
			return singleObjectQueryIterator;
		}
		
		Iterator4 res = singleObjectQueryIterator;
		
		Iterator4 executionPathIterator = executionPath.iterator();
		while(executionPathIterator.moveNext()){
			
			final String fieldName = (String) executionPathIterator.current();
			
			res = Iterators.concat(Iterators.map(res, new Function4() {
				
				public Object apply(Object current) {
					int id = ((Integer)current).intValue();
                    CollectIdContext context = CollectIdContext.forID(i_trans, id);
                    if(context == null){
                    	return Iterators.SKIP;
                    }
                    context.classMetadata().collectIDs(context, fieldName);
					return new TreeKeyIterator(context.ids());
				}
			}));
			
		}
		return res;
	}
    
	public ObjectContainerBase stream() {
		return i_trans.container();
	}

	public int classIndexEntryCount() {
		return i_classMetadata.indexEntryCount(i_trans);
	}

	private FieldIndexProcessorResult processFieldIndexes() {
		if(_constraints == null){
			return FieldIndexProcessorResult.NO_INDEX_FOUND;
		}
		return new FieldIndexProcessor(this).run();
	}

    void evaluate() {
    	
    	if (_constraints == null) {
    		return;
    	}
    	
    	forEachConstraint(new Procedure4() {
			public void apply(Object arg) {
	            QCon qCon = (QCon)arg;
	            qCon.setCandidates(QCandidates.this);
	    		qCon.evaluateSelf();
			}
		});
    	
    	forEachConstraint(new Procedure4() {
			public void apply(Object arg) {
	            ((QCon)arg).evaluateSimpleChildren();
			}
		});
    	
    	forEachConstraint(new Procedure4() {
			public void apply(Object arg) {
	            ((QCon)arg).evaluateEvaluations();
			}
		});

    	forEachConstraint(new Procedure4() {
			public void apply(Object arg) {
	            ((QCon)arg).evaluateCreateChildrenCandidates();
			}
		});
    	
    	forEachConstraint(new Procedure4() {
			public void apply(Object arg) {
	            ((QCon)arg).evaluateCollectChildren();
			}
		});

    	forEachConstraint(new Procedure4() {
			public void apply(Object arg) {
	            ((QCon)arg).evaluateChildren();
			}
		});

    }
    
    private void forEachConstraint(Procedure4 proc){
    	Iterator4 i = iterateConstraints();
    	while(i.moveNext()){
    		QCon constraint = (QCon)i.current();
    		if(! constraint.processedByIndex()){
    			proc.apply(constraint);
    		} 
    	}
    }

    boolean isEmpty() {
        final boolean[] ret = new boolean[] { true };
        traverse(new Visitor4() {
            public void visit(Object obj) {
                if (((QCandidate) obj)._include) {
                    ret[0] = false;
                }
            }
        });
        return ret[0];
    }

    boolean filter(Visitor4 a_host) {
        if (i_root != null) {
            i_root.traverse(a_host);
            i_root = i_root.filter(new Predicate4() {
                public boolean match(Object a_candidate) {
                    return ((QCandidate) a_candidate)._include;
                }
            });
        }
        return i_root != null;
    }
    
    int generateCandidateId(){
        if(_idGenerator == null){
            _idGenerator = new IDGenerator();
        }
        return - _idGenerator.next();
    }
    
    public Iterator4 iterateConstraints(){
        if(_constraints == null){
            return Iterators.EMPTY_ITERATOR;
        }
        return new Iterator4Impl(_constraints);
    }
    
    final static class TreeIntBuilder {
    	public TreeInt tree;
    	
    	public void add(TreeInt node) {
    		tree = (TreeInt)Tree.add(tree, node);
    	}
    }

    void loadFromClassIndex() {
    	if (!isEmpty()) {
    		return;
    	}
    	
    	final TreeIntBuilder result = new TreeIntBuilder();
    	final ClassIndexStrategy index = i_classMetadata.index();
		index.traverseAll(i_trans, new Visitor4() {
    		public void visit(Object obj) {
    			result.add(new QCandidate(QCandidates.this, null, ((Integer)obj).intValue()));
    		}
    	});
    
		i_root = result.tree;
        
        DiagnosticProcessor dp = i_trans.container()._handlers.diagnosticProcessor();
        if (dp.enabled() && !isClassOnlyQuery()){
            dp.loadedFromClassIndex(i_classMetadata);
        }
        
        _loadedFromClassIndex = true;
        
    }

	void setCurrentConstraint(QCon a_constraint) {
        i_currentConstraint = a_constraint;
    }

    void traverse(Visitor4 a_visitor) {
        if(i_root != null){
            i_root.traverse(a_visitor);
        }
    }

    // FIXME: This method should go completely.
    //        We changed the code to create the QCandidates graph in two steps:
    //        (1) call fitsIntoExistingConstraintHierarchy to determine whether
    //            or not we need more QCandidates objects
    //        (2) add all constraints
    //        This method tries to do both in one, which results in missing
    //        constraints. Not all are added to all QCandiates.
    //        Right methodology is in 
    //        QQueryBase#createCandidateCollection
    //        and
    //        QQueryBase#createQCandidatesList
    boolean tryAddConstraint(QCon a_constraint) {

        if (_field != null) {
            QField qf = a_constraint.getField();
            if (qf != null) {
                if (_field.name()!=null&&!_field.name().equals(qf.name())) {
                    return false;
                }
            }
        }

        if (i_classMetadata == null || a_constraint.isNullConstraint()) {
            addConstraint(a_constraint);
            return true;
        }
        ClassMetadata yc = a_constraint.getYapClass();
        if (yc != null) {
            yc = i_classMetadata.getHigherOrCommonHierarchy(yc);
            if (yc != null) {
                i_classMetadata = yc;
                addConstraint(a_constraint);
                return true;
            }
        }
        addConstraint(a_constraint);
        return false;
    }

    public void visit(Object a_tree) {
    	final QCandidate parent = (QCandidate) a_tree;
    	if (parent.createChild(this)) {
    		return;
    	}
    	
    	// No object found.
    	// All children constraints are necessarily false.
    	// Check immediately.
		Iterator4 i = iterateConstraints();
		while(i.moveNext()){
			((QCon)i.current()).visitOnNull(parent.getRoot());
		}
    		
    }
    
    public String toString() {
    	final StringBuffer sb = new StringBuffer();
    	i_root.traverse(new Visitor4() {
			public void visit(Object obj) {
				QCandidate candidate = (QCandidate) obj;
				sb.append(" ");
				sb.append(candidate._key);
			}
		});
    	return sb.toString();
    }
	
	public final Transaction transaction(){
	    return i_trans;
	}
	
	public boolean wasLoadedFromClassIndex(){
		return _loadedFromClassIndex;
	}

	public boolean fitsIntoExistingConstraintHierarchy(QCon constraint) {
        if (_field != null) {
            QField qf = constraint.getField();
            if (qf != null) {
                if (_field.name()!=null&&!_field.name().equals(qf.name())) {
                    return false;
                }
            }
        }

        if (i_classMetadata == null || constraint.isNullConstraint()) {
            return true;
        }
        ClassMetadata classMetadata = constraint.getYapClass();
        if (classMetadata == null) {
        	return false;
        }
        classMetadata = i_classMetadata.getHigherOrCommonHierarchy(classMetadata);
        if (classMetadata == null) {
        	return false;
        }
        i_classMetadata = classMetadata;
        return true;
	}
	
	private boolean isClassOnlyQuery() {
		if(_constraints._next != null) {
			return false;
		}
		if(!(_constraints._element instanceof QConClass)) {
			return false;
		}
		return !((QCon)_constraints._element).hasChildren();
	}
}
