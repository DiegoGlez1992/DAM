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

import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.query.*;
import com.db4o.internal.query.SodaQueryComparator.*;
import com.db4o.internal.query.result.*;
import com.db4o.query.*;
import com.db4o.reflect.*;
import com.db4o.types.*;


/**
 * QQuery is the users hook on our graph.
 * 
 * A QQuery is defined by it's constraints.
 * 
 * NOTE: This is just a 'partial' base class to allow for variant implementations
 * in db4oj and db4ojdk1.2. It assumes that itself is an instance of QQuery
 * and should never be used explicitly.
 * 
 * @exclude
 */
public abstract class QQueryBase implements InternalQuery, Unversioned {

    transient Transaction _trans;
    
    @decaf.Public
    private Collection4 i_constraints = new Collection4();

    @decaf.Public
    private QQuery i_parent;
    
    @decaf.Public
    private String i_field;
    
    private transient QueryEvaluationMode _evaluationMode;
    
    @decaf.Public
    private int _prefetchDepth;
    
    @decaf.Public
    private int _prefetchCount;
    
    @decaf.Public
    private int _evaluationModeAsInt;
    
    @decaf.Public
    private QueryComparator _comparator;
    
    private transient final QQuery _this;

    @decaf.Public
	private List<SodaQueryComparator.Ordering> _orderings;
    
    protected QQueryBase() {
        // C/S only
    	_this = cast(this);
    }

    protected QQueryBase(Transaction a_trans, QQuery a_parent, String a_field) {
    	_this = cast(this);
        _trans = a_trans;
        i_parent = a_parent;
        i_field = a_field;
    }

	public void captureQueryResultConfig() {
	    final Config4Impl config = _trans.container().config();
		_evaluationMode = config.evaluationMode();
		_prefetchDepth = config.prefetchDepth();
        _prefetchCount = config.prefetchObjectCount();
    }

    void addConstraint(QCon a_constraint) {
        i_constraints.add(a_constraint);
    }

    private void addConstraint(Collection4 col, Object obj) {
        if(attachToExistingConstraints(col, obj, true)){
            return;
        }
        if(attachToExistingConstraints(col, obj, false)){
            return;
        }
        QConObject newConstraint = new QConObject(_trans, null, null, obj);
        addConstraint(newConstraint);
        col.add(newConstraint);
    }

    private boolean attachToExistingConstraints(Collection4 newConstraintsCollector, Object obj, boolean onlyForPaths) {
        boolean found = false;
        final Iterator4 j = iterateConstraints();
        while (j.moveNext()) {
            QCon existingConstraint = (QCon)j.current();
            final BooleanByRef removeExisting = new BooleanByRef(false);
            if(! onlyForPaths || (existingConstraint instanceof QConPath) ){
                QCon newConstraint = existingConstraint.shareParent(obj, removeExisting);
                if (newConstraint != null) {
                	newConstraintsCollector.add(newConstraint);
                    addConstraint(newConstraint);
                    if (removeExisting.value) {
                        removeConstraint(existingConstraint);
                    }
                    found = true;
                    if(! onlyForPaths){
                        break;
                    }
                }
            }
        }
        return found;
    }

    /**
	 * Search for slot that corresponds to class. <br>If not found add it.
	 * <br>Constrain it. <br>
	 */
    public Constraint constrain(Object example) {
        synchronized (streamLock()) {
            ReflectClass claxx = reflectClassForClass(example);             
            if (claxx != null) {                
                return addClassConstraint(claxx);
            }
            
            QConEvaluation eval = Platform4.evaluationCreate(_trans, example);
			if (eval != null) {
                return addEvaluationToAllConstraints(eval);
            }
			
            Collection4 constraints = new Collection4();
            addConstraint(constraints, example);
            return toConstraint(constraints);
        }
    }

	private Constraint addEvaluationToAllConstraints(QConEvaluation eval) {

	    if(i_constraints.size() == 0){
	        _trans.container().classCollection().iterateTopLevelClasses(new Visitor4() {
                public void visit(Object obj) {
                    ClassMetadata classMetadata = (ClassMetadata) obj;
                    QConClass qcc = new QConClass(_trans,classMetadata.classReflector());
                    addConstraint(qcc);
                    toConstraint(i_constraints).or(qcc);
                }
            });
	    }
	    
		Iterator4 i = iterateConstraints();
		while (i.moveNext()) {
		    ((QCon)i.current()).addConstraint(eval);
		}
		
		// FIXME: should return valid Constraint object
		return null;
	}

	private Constraint addClassConstraint(ReflectClass claxx) {
		if (isTheObjectClass(claxx)) {
			return null;
		}
		
		if (claxx.isInterface()) {
		    return addInterfaceConstraint(claxx);
		}

		final Collection4 newConstraints = introduceClassConstrain(claxx);
		if (newConstraints.isEmpty()) {
		    QConClass qcc = new QConClass(_trans,claxx);
		    addConstraint(qcc);
		    return qcc;
		}

		return toConstraint(newConstraints);
	}

	private Collection4 introduceClassConstrain(ReflectClass claxx) {
	    final Collection4 newConstraints = new Collection4();
		final Iterator4 existingConstraints = iterateConstraints();
		while (existingConstraints.moveNext()) {
		    final QCon existingConstraint = (QConObject)existingConstraints.current();
		    final BooleanByRef removeExisting = new BooleanByRef(false);
		    final QCon newConstraint =
		        existingConstraint.shareParentForClass(claxx, removeExisting);
		    if (newConstraint != null) {
		    	newConstraints.add(newConstraint);
		        addConstraint(newConstraint);
		        if (removeExisting.value) {
		            removeConstraint(existingConstraint);
		        }
		    }
		}
	    return newConstraints;
    }

	private boolean isTheObjectClass(ReflectClass claxx) {
	    return claxx.equals(stream()._handlers.ICLASS_OBJECT);
    }

	private Constraint addInterfaceConstraint(ReflectClass claxx) {
		Collection4 classes = stream().classCollection().forInterface(claxx);
		if (classes.size() == 0) {
		    QConClass qcc = new QConClass(_trans, null, null, claxx);
		    addConstraint(qcc);
		    return qcc;
		}
		Iterator4 i = classes.iterator();
		Constraint constr = null;
		while (i.moveNext()) {
		    ClassMetadata classMetadata = (ClassMetadata)i.current();
		    ReflectClass classMetadataClaxx = classMetadata.classReflector();
		    if(classMetadataClaxx != null){
		        if(! classMetadataClaxx.isInterface()){
		            if(constr == null){
		                constr = constrain(classMetadataClaxx);
		            }else{
		                constr = constr.or(constrain(classMetadata.classReflector()));
		            }
		        }
		    }
		    
		}
		return constr;
	}

	private ReflectClass reflectClassForClass(Object example) {		
		if(example instanceof ReflectClass){
			return (ReflectClass)example;
		}
		if(example instanceof Class) {
			return _trans.reflector().forClass((Class)example);
		}
		return null;
	}

    public Constraints constraints() {
        synchronized (streamLock()) {
            Constraint[] constraints = new Constraint[i_constraints.size()];
            i_constraints.toArray(constraints);
            return new QConstraints(_trans, constraints);
        }
    }

    public Query descend(final String a_field) {
        synchronized (streamLock()) {
            final QQuery query = new QQuery(_trans, _this, a_field);
            IntByRef run = new IntByRef(1);
            if (!descend1(query, a_field, run)) {

                // try to add unparented nodes on the second run,
                // if not added in the first run and a descendant
                // was not found

                if (run.value == 1) {
                    run.value = 2;
                    if (!descend1(query, a_field, run)) {
                        new QConUnconditional(_trans, false).attach(query, a_field);
                    }
                }
            }
            return query;
        }
    }

    private boolean descend1(final QQuery query, final String fieldName, IntByRef run) {
        if (run.value == 2 || i_constraints.size() == 0) {

            // On the second run we are really creating a second independant
            // query network that is not joined to other higher level
			// constraints.
            // Let's see how this works out. We may need to join networks.

            run.value = 0; // prevent a double run of this code

            stream().classCollection().attachQueryNode(fieldName, new Visitor4() {
            	
            	boolean untypedFieldConstraintCollected = false;

                public void visit(Object obj) {

                    Object[] pair = ((Object[]) obj);
                    ClassMetadata containingClass = (ClassMetadata)pair[0];
                    FieldMetadata field = (FieldMetadata)pair[1];
                    
                    if (isTyped(field)) {
                    	addFieldConstraint(containingClass, field);
                    	return;
                    }
                    
                    if (untypedFieldConstraintCollected)
                    	return;
                    
                    addFieldConstraint(containingClass, field);
                    untypedFieldConstraintCollected = true;
                }

				private boolean isTyped(FieldMetadata field) {
					return !Handlers4.isUntyped(field.getHandler());
				}

				private void addFieldConstraint(ClassMetadata containingClass,
						FieldMetadata field) {
					QConClass qcc =
					    new QConClass(
					        _trans,
					        null,
					        field.qField(_trans),
					        containingClass.classReflector());
					addConstraint(qcc);
					toConstraint(i_constraints).or(qcc);
				}

            });

        }
        
        checkConstraintsEvaluationMode();
        
        final BooleanByRef foundClass = new BooleanByRef(false);
        Iterator4 i = iterateConstraints();
        while (i.moveNext()) {
            if (((QCon)i.current()).attach(query, fieldName)) {
                foundClass.value = true;
            }
        }
        return foundClass.value;
    }

    public ObjectSet execute() {
        synchronized (streamLock()) {
        	return triggeringQueryEvents(new Closure4<ObjectSet>() { public ObjectSet run() {
        		return new ObjectSetFacade(getQueryResult());
        	}});
        }
	}
    
    public void executeLocal(final IdListQueryResult result) {
    	checkConstraintsEvaluationMode();
		
		CreateCandidateCollectionResult r = createCandidateCollection();
		
		boolean checkDuplicates = r.checkDuplicates;
		boolean topLevel = r.topLevel;
		List4 candidateCollection = r.candidateCollection;
		
		if (Debug4.queries) {
			logConstraints();
		}
		
		if (candidateCollection != null) {
			
			final Collection4 executionPath = topLevel ? null : fieldPathFromTop();
		
			Iterator4 i = new Iterator4Impl(candidateCollection);
		    while (i.moveNext()) {
		        ((QCandidates)i.current()).execute();
		    }
		
		    if (candidateCollection._next != null) {
		        checkDuplicates = true;
		    }
		
		    if (checkDuplicates) {
		        result.checkDuplicates();
		    }
		
		    final ObjectContainerBase stream = stream();
		    i = new Iterator4Impl(candidateCollection);
		    while (i.moveNext()) {
		        QCandidates candidates = (QCandidates)i.current();
		        if (topLevel) {
		            candidates.traverse(result);
		        } else {
		            candidates.traverse(new Visitor4() {
		                public void visit(Object a_object) {
		                    QCandidate candidate = (QCandidate)a_object;
		                    if (candidate.include()) {
		                        TreeInt ids = new TreeInt(candidate._key);
		                        final ByRef<TreeInt> idsNew = new ByRef<TreeInt>();
		                        Iterator4 itPath = executionPath.iterator();
		                        while (itPath.moveNext()) {
		                            idsNew.value = null;
		                            final String fieldName = (String) (itPath.current());
		                            if (ids != null) {
		                                ids.traverse(new Visitor4() {
		                                    public void visit(Object treeInt) {
		                                        int id = ((TreeInt)treeInt)._key;
		                                        StatefulBuffer reader =
		                                            stream.readStatefulBufferById(_trans, id);
		                                        if (reader != null) {
		                                            ObjectHeader oh = new ObjectHeader(stream, reader);
		                                            CollectIdContext context = new CollectIdContext(_trans, oh, reader);
		                                            oh.classMetadata().collectIDs(context, fieldName);
		                                            Tree.traverse(context.ids(), new Visitor4<TreeInt>() {
														public void visit(TreeInt node) {
															idsNew.value = TreeInt.add(idsNew.value, node._key);
														}
		                                            });
		                                        }
		                                    }
		                                });
		                            }
		                            ids = (TreeInt) idsNew.value;
		                        }
		                        if(ids != null){
		                            ids.traverse(new Visitor4() {
		                                public void visit(Object treeInt) {
		                                    result.addKeyCheckDuplicates(((TreeInt)treeInt)._key);
		                                }
		                            });
		                        }
		                    }
		                }
		            });
		        }
		    }
		}
		sort(result);
    }

	private void triggerQueryOnFinished() {
		stream().callbacks().queryOnFinished(_trans, cast(this));
	}

	private void triggerQueryOnStarted() {
		stream().callbacks().queryOnStarted(_trans, cast(this));
	}
    
    public Iterator4 executeLazy(){
    	checkConstraintsEvaluationMode();
		
		final CreateCandidateCollectionResult r = createCandidateCollection();
		
		final Collection4 executionPath = executionPath(r);
		
		Iterator4 candidateCollection = new Iterator4Impl(r.candidateCollection);
		
		MappingIterator executeCandidates = new MappingIterator(candidateCollection){
			protected Object map(Object current) {
				return ((QCandidates)current).executeLazy(executionPath);
			}
		};
		
		CompositeIterator4 resultingIDs = new CompositeIterator4(executeCandidates);
		
		if(!r.checkDuplicates){
			return resultingIDs;
		}
		
		return checkDuplicates(resultingIDs);
    }

	public QueryResult getQueryResult() {
		synchronized (streamLock()) {
	        if(i_constraints.size() == 0){
	            return executeAllObjectsQuery();
	        }
			QueryResult result = executeClassOnlyQuery();
			if(result != null) {
				return result;
			}
    		optimizeJoins();
	        return executeQuery();
	    }
	}

	protected final QueryResult executeQuery() {
		return stream().executeQuery(_this);
	}

	private QueryResult executeAllObjectsQuery() {
		return stream().queryAllObjects(_trans);
	}

	protected ObjectContainerBase stream() {
		return _trans.container();
	}
	
	public InternalObjectContainer container() {
		return stream();
	}

	private QueryResult executeClassOnlyQuery() {
        
		final ClassMetadata clazz = singleClassConstraint();
		if (null == clazz) {
			return null;
		}
		
		QueryResult queryResult = stream().classOnlyQuery(QQueryBase.this, clazz);
		sort(queryResult);
		return queryResult;
	}

	private ClassMetadata singleClassConstraint() {
		if(requiresSort()) {
			return null;
		}
		QConClass clazzconstr = classConstraint();
		if (clazzconstr == null) {
			return null;
		}
		ClassMetadata clazz=clazzconstr._classMetadata;
		if(clazz==null) {
			return null;
		}
		if(clazzconstr.hasChildren() || clazz.isArray()) {
			return null;
		}
		return clazz;
	}

	private QConClass classConstraint() {
		if (i_constraints.size()!=1) {
			return null;
		}
		Constraint constr=singleConstraint(); 
		if(constr.getClass()!=QConClass.class) {
			return null;
		}
		return (QConClass)constr;
	}

	private Constraint singleConstraint() {
		return (Constraint)i_constraints.singleElement();
	}

    public static class CreateCandidateCollectionResult {
    	public final boolean checkDuplicates;
        public final boolean topLevel;
        public final List4 candidateCollection;
        
    	public CreateCandidateCollectionResult(List4 candidateCollection_, boolean checkDuplicates_, boolean topLevel_) {
    		candidateCollection = candidateCollection_;
    		topLevel = topLevel_;
    		checkDuplicates = checkDuplicates_;
		}
    }
    
    public Iterator4 executeSnapshot(){
		final CreateCandidateCollectionResult r = createCandidateCollection();
		
		final Collection4 executionPath = executionPath(r);
		
		Iterator4 candidatesIterator = new Iterator4Impl(r.candidateCollection);
		
		Collection4 snapshots = new Collection4();
		while(candidatesIterator.moveNext()){
			QCandidates candidates = (QCandidates) candidatesIterator.current(); 
			snapshots.add( candidates.executeSnapshot(executionPath));
		}
		
		Iterator4 snapshotsIterator = snapshots.iterator();
		final CompositeIterator4 resultingIDs = new CompositeIterator4(snapshotsIterator);
		
		if(!r.checkDuplicates){
			return resultingIDs;
		}
		
		return checkDuplicates(resultingIDs);
    }
    
    public <T> T triggeringQueryEvents(Closure4<T> closure) {
    	triggerQueryOnStarted();
    	try {
    		return closure.run();
    	} finally {
    		triggerQueryOnFinished();
    	}
    }

	private Iterator4 checkDuplicates(CompositeIterator4 executeAllCandidates) {
		return Iterators.filter(executeAllCandidates, new Predicate4() {
        	private TreeInt ids = new TreeInt(0);
			public boolean match(Object current) {
				int id = ((Integer)current).intValue();
				if(ids.find(id) != null){
					return false;
				}
				ids = (TreeInt)ids.add(new TreeInt(id));
				return true;
			}
		});
	}

	private Collection4 executionPath(final CreateCandidateCollectionResult r) {
		return r.topLevel ? null : fieldPathFromTop();
	}

	public void checkConstraintsEvaluationMode() {
	    Iterator4 constraints = iterateConstraints();
        while (constraints.moveNext()) {
            ((QConObject)constraints.current()).setEvaluationMode();
        }
	}
	
	private Collection4 fieldPathFromTop(){
        QQueryBase q = this;
        final Collection4 fieldPath = new Collection4();
        while (q.i_parent != null) {
            fieldPath.prepend(q.i_field);
            q = q.i_parent;
        }
        return fieldPath;
    }

	private void logConstraints() {
		if (Debug4.queries) {
			Iterator4 i = iterateConstraints();
			while (i.moveNext()) {
			    ((QCon)i.current()).log("");
			}
		}
	}

	public CreateCandidateCollectionResult createCandidateCollection() {
		List4 candidatesList = createQCandidatesList();
		boolean checkDuplicates = false;
        boolean topLevel = true;
        Iterator4 i = iterateConstraints();
        while (i.moveNext()) {
            QCon constraint = (QCon)i.current();
            QCon old = constraint;            
            constraint = constraint.getRoot();
            if (constraint != old) {
                checkDuplicates = true;
                topLevel = false;
            }
            ClassMetadata classMetadata = constraint.getYapClass();
            if (classMetadata == null) {
            	break;
            }
            addConstraintToCandidatesList(candidatesList, constraint);
            
        }
		return new CreateCandidateCollectionResult(candidatesList, checkDuplicates, topLevel);
	}
	
	private void addConstraintToCandidatesList(List4 candidatesList, QCon qcon) {
		if (candidatesList == null) {
			return;
		}
		Iterator4 j = new Iterator4Impl(candidatesList);
		while (j.moveNext()) {
		    QCandidates candidates = (QCandidates)j.current();
		    candidates.addConstraint(qcon);
		}
	}
	
	private List4 createQCandidatesList(){
        List4 candidatesList = null;
        Iterator4 i = iterateConstraints();
        while (i.moveNext()) {
            QCon constraint = (QCon)i.current();
            constraint = constraint.getRoot();
            ClassMetadata classMetadata = constraint.getYapClass();
            if (classMetadata == null) {
            	continue;
            }
            if(constraintCanBeAddedToExisting(candidatesList, constraint)){
            	continue;
            }
    		QCandidates candidates = new QCandidates((LocalTransaction) _trans, classMetadata, null);
    		candidatesList = new List4(candidatesList, candidates);
        }
		return candidatesList;
	}
	
	private boolean constraintCanBeAddedToExisting(List4 candidatesList, QCon constraint){
		Iterator4 j = new Iterator4Impl(candidatesList);
		while (j.moveNext()) {
		    QCandidates candidates = (QCandidates)j.current();
		    if (candidates.fitsIntoExistingConstraintHierarchy(constraint)) {
		        return true;
		    }
		}
		return false;
	}

    public final Transaction transaction() {
        return _trans;
    }
    
    public Iterator4 iterateConstraints(){
    	// clone the collection first to avoid
    	// InvalidIteratorException as i_constraints might be 
    	// modified during the execution of callee
        return new Collection4(i_constraints).iterator();
    }

    public Query orderAscending() {
    	if(i_parent == null) {
    		throw new IllegalStateException("Cannot apply ordering at top level.");
    	}
        synchronized (streamLock()) {
            addOrdering(SodaQueryComparator.Direction.ASCENDING);
            return _this;
        }
    }

    public Query orderDescending() {
    	if(i_parent == null) {
    		throw new IllegalStateException("Cannot apply ordering at top level.");
    	}
        synchronized (streamLock()) {
        	addOrdering(SodaQueryComparator.Direction.DESCENDING);
            return _this;
        }
    }
    
    private void addOrdering(Direction direction) {
    	addOrdering(direction, new ArrayList<String>());
	}
    
    protected final void addOrdering(Direction direction, List<String> path) {
    	
    	if (i_field != null) {
    		path.add(i_field);
    	}
        
    	if (i_parent != null) {
    		i_parent.addOrdering(direction, path);
    		return;
    	}
    		
    	final String[] fieldPath = reverseFieldPath(path);
    	removeExistingOrderingFor(fieldPath);
		orderings().add(new SodaQueryComparator.Ordering(direction, fieldPath));
    }

	private void removeExistingOrderingFor(String[] fieldPath) {
		for (Ordering ordering : orderings()) {
			if (Arrays.equals(ordering.fieldPath(), fieldPath)) {
				orderings().remove(ordering);
				break;
			}
		}
	}

	/**
	 * Public so it can be used by the LINQ test cases.
	 */
	public final List<Ordering> orderings() {
		if (null == _orderings) {
    		_orderings = new ArrayList<SodaQueryComparator.Ordering>();
    	}
		return _orderings;
	}

	private String[] reverseFieldPath(List<String> path) {
		final String[] reversedPath = new String[path.size()];
		for (int i = 0; i < reversedPath.length; i++) {
			reversedPath[i] = path.get(path.size() - i - 1);
		}
		return reversedPath;
	}

	public void marshall() {
        checkConstraintsEvaluationMode();
        
    	_evaluationModeAsInt = _evaluationMode.asInt();
        Iterator4 i = iterateConstraints();
        while (i.moveNext()) {
            ((QCon)i.current()).getRoot().marshall();
        }
    }

    public void unmarshall(final Transaction a_trans) {
    	_evaluationMode = QueryEvaluationMode.fromInt(_evaluationModeAsInt);
        _trans = a_trans;
        Iterator4 i = iterateConstraints();
        while (i.moveNext()) {
            ((QCon)i.current()).unmarshall(a_trans);
        }
    }

    void removeConstraint(QCon a_constraint) {
        i_constraints.remove(a_constraint);
    }

    Constraint toConstraint(final Collection4 constraints) {       
        if (constraints.size() == 1) {
        	return (Constraint) constraints.singleElement();
        } else if (constraints.size() > 0) {
            Constraint[] constraintArray = new Constraint[constraints.size()];
            constraints.toArray(constraintArray);
            return new QConstraints(_trans, constraintArray);
        }
        return null;
    }
    
	protected Object streamLock() {
        return stream().lock();
    }

	public Query sortBy(QueryComparator comparator) {
		_comparator=comparator;
		return _this;
	}
	
	private void sort(QueryResult result) {
		if (_orderings != null) {
			if (result.size() == 0) {
				return;
			}
			result.sortIds(newSodaQueryComparator());
		}
        if(_comparator!=null) {
			if (result.size() == 0) {
				return;
			}
        	result.sort(_comparator);
        }
	}
	
    private IntComparator newSodaQueryComparator() {
    	return new SodaQueryComparator(
    			(LocalObjectContainer)this.transaction().container(),
    			extentType(),
    			_orderings.toArray(new SodaQueryComparator.Ordering[_orderings.size()]));
	}

	private ClassMetadata extentType() {
		return classConstraint().getYapClass();
	}

	// cheat emulating '(QQuery)this'
	private static QQuery cast(QQueryBase obj) {
		return (QQuery)obj;
	}
	
	public boolean requiresSort() {
		if (_comparator != null || _orderings != null){
			return true;
		}
		return false;
	}
	
	public QueryComparator comparator() {
		return _comparator;
	}
	
	public QueryEvaluationMode evaluationMode(){
		return _evaluationMode;
	}
	
	public void evaluationMode(QueryEvaluationMode mode){
		_evaluationMode = mode;
	}
	
    private void optimizeJoins() {
    	if(!hasOrJoins()) {
    		removeJoins();
    	}
    }

	private boolean hasOrJoins() {
		return forEachConstraintRecursively(new Function4() {
			public Object apply(Object obj) {
				QCon constr = (QCon) obj;
				Iterator4 joinIter = constr.iterateJoins();
				while(joinIter.moveNext()) {
					QConJoin join = (QConJoin) joinIter.current();
					if(join.isOr()) {
						return Boolean.TRUE;
					}
				}
				return Boolean.FALSE;
			}
		});
	}

	private void removeJoins() {
		forEachConstraintRecursively(new Function4() {
			public Object apply(Object obj) {
				QCon constr = (QCon) obj;
				constr.i_joins = null;
				return Boolean.FALSE;
			}
		});
	}

	private boolean forEachConstraintRecursively(Function4 block) {
		Queue4 queue = new NoDuplicatesQueue(new NonblockingQueue());
		Iterator4 constrIter = iterateConstraints();
		while(constrIter.moveNext()) {
			queue.add(constrIter.current());
		}
		while(queue.hasNext()) {
    		QCon constr = (QCon) queue.next();
    		Boolean cancel = (Boolean) block.apply(constr);
    		if(cancel.booleanValue()) {
    			return true;
    		}
    		
    		Iterator4 childIter = constr.iterateChildren();
    		while(childIter.moveNext()) {
    			queue.add(childIter.current());
    		}
    		Iterator4 joinIter = constr.iterateJoins();
    		while(joinIter.moveNext()) {
    			queue.add(joinIter.current());
    		}
		}
		return false;
	}

	public int prefetchDepth() {
		return _prefetchDepth;
    }
	
	public int prefetchCount() {
		return _prefetchCount;
    }

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("QQueryBase\n");
		Iterator4 i = iterateConstraints();
		while(i.moveNext()){
			QCon constraint = (QCon) i.current();
			sb.append(constraint);
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public QQuery parent() {
		return i_parent;
	}

}
