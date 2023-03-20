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
import com.db4o.internal.handlers.array.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;
import com.db4o.query.*;
import com.db4o.reflect.*;
import com.db4o.typehandlers.*;

/**
 * Represents an actual object in the database. Forms a tree structure, indexed
 * by id. Can have dependents that are doNotInclude'd in the query result when
 * this is doNotInclude'd.
 * 
 * @exclude
 */
public class QCandidate extends TreeInt implements Candidate {

	// db4o ID is stored in _key;

	// db4o byte stream storing the object
	ByteArrayBuffer _bytes;

	final QCandidates _candidates;

	// Dependent candidates
	private List4 _dependants;

	// whether to include in the result set
	// may use id for optimisation ???
	boolean _include = true;

	private Object _member;

	// Possible pending joins on children
	private Tree _pendingJoins;

	// The evaluation root to compare all ORs
	private QCandidate _root;

	// the ClassMetadata of this object
	private ClassMetadata _classMetadata;

	// temporary field and member for one field during evaluation
	private FieldMetadata _fieldMetadata; // null denotes null object
    
    private int _handlerVersion;

	private QCandidate(QCandidates qcandidates) {
		super(0);
		_candidates = qcandidates;
	}

	public QCandidate(QCandidates candidates, Object member, int id) {
		super(id);
		if (DTrace.enabled) {
			DTrace.CREATE_CANDIDATE.log(id);
		}
        _candidates = candidates;
		_member = member;
		_include = true;
        
        if(id == 0){
            _key = candidates.generateCandidateId();
        }
        if(Debug4.queries){
            System.out.println("Candidate identified ID:" + _key );
        }
	}

	public Object shallowClone() {
		QCandidate qcan = new QCandidate(_candidates);
        qcan.setBytes(_bytes);
		qcan._dependants = _dependants;
		qcan._include = _include;
		qcan._member = _member;
		qcan._pendingJoins = _pendingJoins;
		qcan._root = _root;
		qcan._classMetadata = _classMetadata;
		qcan._fieldMetadata = _fieldMetadata;

		return super.shallowCloneInternal(qcan);
	}

	void addDependant(QCandidate a_candidate) {
		_dependants = new List4(_dependants, a_candidate);
	}

	private void checkInstanceOfCompare() {
		if (_member instanceof Compare) {
			_member = ((Compare) _member).compare();
			LocalObjectContainer stream = container();
			_classMetadata = stream.classMetadataForReflectClass(stream.reflector().forObject(_member));
			_key = stream.getID(transaction(), _member);
			if (_key == 0) {
				setBytes(null);
			} else {
				setBytes(stream.readBufferById(transaction(), _key));
			}
		}
	}
	
	boolean createChild(final QCandidates a_candidates) {
		if (!_include) {
			return false;
		}

		if (_fieldMetadata != null) {
			TypeHandler4 handler = _fieldMetadata.getHandler();
			if (handler != null) {
			    final QueryingReadContext queryingReadContext = new QueryingReadContext(transaction(), marshallerFamily().handlerVersion(), _bytes, _key); 
				final TypeHandler4 arrayElementHandler = Handlers4.arrayElementHandler(handler, queryingReadContext);
				if (arrayElementHandler != null) {

					final int offset = queryingReadContext.offset();
					boolean outerRes = true;

					// The following construct is worse than not ideal.
					// For each constraint it completely reads the
					// underlying structure again. The structure could b
					// kept fairly easy. TODO: Optimize!

					Iterator4 i = a_candidates.iterateConstraints();
					while (i.moveNext()) {

						QCon qcon = (QCon) i.current();
						QField qf = qcon.getField();
						if (qf == null || qf.name().equals(_fieldMetadata.getName())) {

							QCon tempParent = qcon.parent();
							qcon.setParent(null);

							final QCandidates candidates = new QCandidates(
									a_candidates.i_trans, null, qf);
							candidates.addConstraint(qcon);

							qcon.setCandidates(candidates);
							
							readArrayCandidates(handler, queryingReadContext.buffer(), arrayElementHandler,
                                candidates);
							
							queryingReadContext.seek(offset);

							final boolean isNot = qcon.isNot();
							if (isNot) {
								qcon.removeNot();
							}

							candidates.evaluate();

							final ByRef<Tree> pending = ByRef.newInstance();
							final boolean[] innerRes = { isNot };
							candidates.traverse(new Visitor4() {
								public void visit(Object obj) {

									QCandidate cand = (QCandidate) obj;

									if (cand.include()) {
										innerRes[0] = !isNot;
									}

									// Collect all pending subresults.

									if (cand._pendingJoins != null) {
										cand._pendingJoins
												.traverse(new Visitor4() {
													public void visit(
															Object a_object) {
														QPending newPending = ((QPending) a_object).internalClonePayload();

														// We need to change
														// the
														// constraint here, so
														// our
														// pending collector
														// uses
														// the right
														// comparator.
														newPending
																.changeConstraint();
														QPending oldPending = (QPending) Tree
																.find(
																		pending.value,
																		newPending);
														if (oldPending != null) {

															// We only keep one
															// pending result
															// for
															// all array
															// elements.
															// and memorize,
															// whether we had a
															// true or a false
															// result.
															// or both.

															if (oldPending._result != newPending._result) {
																oldPending._result = QPending.BOTH;
															}

														} else {
															pending.value = Tree
																	.add(
																			pending.value,
																			newPending);
														}
													}
												});
									}
								}
							});

							if (isNot) {
								qcon.not();
							}

							// In case we had pending subresults, we
							// need to communicate
							// them up to our root.
							if (pending.value != null) {
								pending.value.traverse(new Visitor4() {
									public void visit(Object a_object) {
										getRoot().evaluate((QPending) a_object);
									}
								});
							}

							if (!innerRes[0]) {

								if (Debug4.queries) {
									System.out
											.println("  Array evaluation false. Constraint:"
													+ qcon.id());
								}

								// Again this could be double triggering.
								// 
								// We want to clean up the "No route"
								// at some stage.

								qcon.visit(getRoot(), qcon.evaluator().not(false));

								outerRes = false;
							}

							qcon.setParent(tempParent);

						}
					}

					return outerRes;
				}

				// We may get simple types here too, if the YapField was null
				// in the higher level simple evaluation. Evaluate these
				// immediately.

				if (Handlers4.isQueryLeaf(handler)) {
					a_candidates.i_currentConstraint.visit(this);
					return true;
				}
			}
		}
        
        if(_fieldMetadata == null) {
            return false;
        }
        
        if (_fieldMetadata instanceof NullFieldMetadata) {
        	return false;
        }
        
        _classMetadata.seekToField(transaction(), _bytes, _fieldMetadata);
        QCandidate candidate = readSubCandidate(a_candidates); 
		if (candidate == null) {
			return false;
		}

		// fast early check for ClassMetadata
		if (a_candidates.i_classMetadata != null
				&& a_candidates.i_classMetadata.isStronglyTyped()) {
			
			TypeHandler4 handler = _fieldMetadata.getHandler();
			if (Handlers4.isUntyped(handler)){
				handler = typeHandlerFor(candidate);
			}
            if(handler == null){
                return false;
            }
		}

		addDependant(a_candidates.add(candidate));
		return true;
	}

	private TypeHandler4 typeHandlerFor(QCandidate candidate) {
	    ClassMetadata classMetadata = candidate.readClassMetadata();
	    if (classMetadata != null) {
	    	return classMetadata.typeHandler();
	    }
	    return null;
    }

	private void readArrayCandidates(TypeHandler4 typeHandler, final ReadBuffer buffer,
        final TypeHandler4 arrayElementHandler, final QCandidates candidates) {
        if(! Handlers4.isCascading(arrayElementHandler)){
            return;
        }
        final SlotFormat slotFormat = SlotFormat.forHandlerVersion(_handlerVersion);
        slotFormat.doWithSlotIndirection(buffer, typeHandler, new Closure4() {
            public Object run() {
                
                QueryingReadContext context = null;
                
                if(Handlers4.handleAsObject(arrayElementHandler)){
                    // TODO: Code is similar to FieldMetadata.collectIDs. Try to refactor to one place.
                    int collectionID = buffer.readInt();
                    ByteArrayBuffer arrayElementBuffer = container().readBufferById(transaction(), collectionID);
                    ObjectHeader objectHeader = ObjectHeader.scrollBufferToContent(container(), arrayElementBuffer);
                    context = new QueryingReadContext(transaction(), candidates, _handlerVersion, arrayElementBuffer, collectionID);
                    objectHeader.classMetadata().collectIDs(context);
                    
                }else{
                    context = new QueryingReadContext(transaction(), candidates, _handlerVersion, buffer, 0);
                    ((CascadingTypeHandler)arrayElementHandler).collectIDs(context);
                }
                
                Tree.traverse(context.ids(), new Visitor4() {
                    public void visit(Object obj) {
                        TreeInt idNode = (TreeInt) obj;
                        candidates.add(new QCandidate(candidates, null, idNode._key));
                    }
                });
                
                Iterator4 i = context.objectsWithoutId();
                while(i.moveNext()){
                    Object obj = i.current();
                    candidates.add(new QCandidate(candidates, obj, 0));
                }
                
                return null;
            }
        
        });
    }

	void doNotInclude() {
		include(false);
		if (_dependants != null) {
			Iterator4 i = new Iterator4Impl(_dependants);
			_dependants = null;
			while (i.moveNext()) {
				((QCandidate) i.current()).doNotInclude();
			}
		}
	}
	
	boolean evaluate(final QConObject a_constraint, final QE a_evaluator) {
		if (a_evaluator.identity()) {
			return a_evaluator.evaluate(a_constraint, this, null);
		}
		if (_member == null) {
			_member = value();
		}
		return a_evaluator.evaluate(a_constraint, this, a_constraint
				.translate(_member));
	}

	boolean evaluate(QPending a_pending) {

		if (Debug4.queries) {
			System.out.println("Pending arrived Join: " + a_pending._join.id()
					+ " Constraint:" + a_pending._constraint.id() + " res:"
					+ a_pending._result);
		}

		QPending oldPending = (QPending) Tree.find(_pendingJoins, a_pending);

		if (oldPending == null) {
			a_pending.changeConstraint();
			_pendingJoins = Tree.add(_pendingJoins, a_pending.internalClonePayload());
			return true;
		} 
		_pendingJoins = _pendingJoins.removeNode(oldPending);
		oldPending._join.evaluatePending(this, oldPending, a_pending._result);
		return false;
	}

	ReflectClass classReflector() {
		readClassMetadata();
		if (_classMetadata == null) {
			return null;
		}
		return _classMetadata.classReflector();
	}
	
	boolean fieldIsAvailable(){
		return classReflector() != null;
	}

	// / ***<Candidate interface code>***

	public ObjectContainer objectContainer() {
		return container();
	}

	public Object getObject() {
		Object obj = value(true);
		if (obj instanceof ByteArrayBuffer) {
			ByteArrayBuffer reader = (ByteArrayBuffer) obj;
			int offset = reader._offset;
            obj = readString(reader); 
			reader._offset = offset;
		}
		return obj;
	}
	
	public String readString(ByteArrayBuffer buffer){
	    return StringHandler.readString(transaction().context(), buffer);
	}

	QCandidate getRoot() {
		return _root == null ? this : _root;
	}

	final LocalObjectContainer container() {
		return transaction().localContainer();
	}

	final LocalTransaction transaction() {
		return _candidates.i_trans;
	}

	public boolean include() {
		return _include;
	}

	/**
	 * For external interface use only. Call doNotInclude() internally so
	 * dependancies can be checked.
	 */
	public void include(boolean flag) {
		// TODO:
		// Internal and external flag may need to be handled seperately.
		_include = flag;
		if(Debug4.queries){
		    System.out.println("Candidate include " + flag + " ID: " + _key);
	    }

	}

	@Override
	public Tree onAttemptToAddDuplicate(Tree oldNode) {
		_size = 0;
		_root = (QCandidate) oldNode;
		return oldNode;
	}

	private ReflectClass memberClass() {
		return transaction().reflector().forObject(_member);
	}

	
	PreparedComparison prepareComparison(ObjectContainerBase container, Object constraint) {
	    Context context = container.transaction().context();
	    
		if (_fieldMetadata != null) {
			return _fieldMetadata.prepareComparison(context, constraint);
		}
		if (_classMetadata != null) {
			return _classMetadata.prepareComparison(context, constraint);
		}
		Reflector reflector = container.reflector();
		ClassMetadata classMetadata = null;
		if (_bytes != null) {
			classMetadata = container.produceClassMetadata(reflector.forObject(constraint));
		} else {
			if (_member != null) {
				classMetadata = container.classMetadataForReflectClass(reflector.forObject(_member));
			}
		}
		if (classMetadata != null) {
			if (_member != null && _member.getClass().isArray()) {
				TypeHandler4 arrayElementTypehandler = classMetadata.typeHandler(); 
				if (reflector.array().isNDimensional(memberClass())) {
					MultidimensionalArrayHandler mah = 
						new MultidimensionalArrayHandler(arrayElementTypehandler, false);
					return mah.prepareComparison(context, _member);
				} 
				ArrayHandler ya = new ArrayHandler(arrayElementTypehandler, false);
				return ya.prepareComparison(context, _member);
			} 
			return classMetadata.prepareComparison(context, constraint);
		}
		return null;
	}


	private void read() {
		if (_include) {
			if (_bytes == null) {
				if (_key > 0) {
					if (DTrace.enabled) {
						DTrace.CANDIDATE_READ.log(_key);
					}
                    setBytes(container().readBufferById(transaction(), _key));
					if (_bytes == null) {
						include(false);
					}
				} else {
				    include(false);
				}
			}
		}
	}
	
	private int currentOffSet(){
	    return _bytes._offset;
	}

	private QCandidate readSubCandidate(QCandidates candidateCollection) {
		read();
		if (_bytes == null || _fieldMetadata == null) {
		    return null;
		}
		final int offset = currentOffSet();
        QueryingReadContext context = newQueryingReadContext();
        TypeHandler4 handler = HandlerRegistry.correctHandlerVersion(context, _fieldMetadata.getHandler());
        QCandidate subCandidate = candidateCollection.readSubCandidate(context, handler);
		seek(offset);
		if (subCandidate != null) {
			subCandidate._root = getRoot();
			return subCandidate;
		}
		return null;
	}
	
	private void seek(int offset){
	    _bytes._offset = offset;
	}

    private QueryingReadContext newQueryingReadContext() {
        return new QueryingReadContext(transaction(), _handlerVersion, _bytes, _key);
    }

	private void readThis(boolean a_activate) {
		read();

		final ObjectContainerBase container = transaction().container();
		
		_member = container.tryGetByID(transaction(), _key);
			
		if (_member != null && (a_activate || _member instanceof Compare)) {
			container.activate(transaction(), _member);
			checkInstanceOfCompare();
		}
	}

	ClassMetadata readClassMetadata() {
		if (_classMetadata == null) {
			read();
			if (_bytes != null) {
			    seek(0);
                ObjectContainerBase stream = container();
                ObjectHeader objectHeader = new ObjectHeader(stream, _bytes);
				_classMetadata = objectHeader.classMetadata();
                
				if (_classMetadata != null) {
					if (stream._handlers.ICLASS_COMPARE
							.isAssignableFrom(_classMetadata.classReflector())) {
						readThis(false);
					}
				}
			}
		}
		return _classMetadata;
	}

	public String toString() {
		String str = "QCandidate ";
		if (_classMetadata != null) {
			str += "\n   YapClass " + _classMetadata.getName();
		}
		if (_fieldMetadata != null) {
			str += "\n   YapField " + _fieldMetadata.getName();
		}
		if (_member != null) {
			str += "\n   Member " + _member.toString();
		}
		if (_root != null) {
			str += "\n  rooted by:\n";
			str += _root.toString();
		} else {
			str += "\n  ROOT";
		}
		return str;
	}

	void useField(QField a_field) {
		read();
		if (_bytes == null) {
			_fieldMetadata = null;
            return;
		} 
		readClassMetadata();
		_member = null;
		if (a_field == null) {
			_fieldMetadata = null;
            return;
		} 
		if (_classMetadata == null) {
			_fieldMetadata = null;
            return;
		} 
		_fieldMetadata = fieldMetadataFrom(a_field, _classMetadata);
		if(_fieldMetadata == null){
		    fieldNotFound();
		    return;
		}
        
		HandlerVersion handlerVersion = _classMetadata.seekToField(transaction(), _bytes, _fieldMetadata);
        
		if (handlerVersion == HandlerVersion.INVALID ) {
		    fieldNotFound();
		    return;
		}
		
		_handlerVersion = handlerVersion._number;
	}
	
	private FieldMetadata fieldMetadataFrom(QField qField, ClassMetadata type) {
		final FieldMetadata existingField = qField.getFieldMetadata();
		if(existingField != null){
			return existingField;
		}
		FieldMetadata field = type.fieldMetadataForName(qField.name());
		if(field != null){
		    field.alive();
		}
		return field;
	}
		

	private void fieldNotFound(){
        if (_classMetadata.holdsAnyClass()) {
            // retry finding the field on reading the value 
            _fieldMetadata = null;
        } else {
            // we can't get a value for the field, comparisons should definitely run against null
            _fieldMetadata = new NullFieldMetadata();
        }
        _handlerVersion = HandlerRegistry.HANDLER_VERSION;  
	}
	

	Object value() {
		return value(false);
	}

	// TODO: This is only used for Evaluations. Handling may need
	// to be different for collections also.
	Object value(boolean a_activate) {
		if (_member == null) {
			if (_fieldMetadata == null) {
				readThis(a_activate);
			} else {
				int offset = currentOffSet();
				_member = _fieldMetadata.read(newQueryingReadContext());
				seek(offset);
				checkInstanceOfCompare();
			}
		}
		return _member;
	}
    
    void setBytes(ByteArrayBuffer bytes){
        _bytes = bytes;
    }
    
    private MarshallerFamily marshallerFamily(){
        return MarshallerFamily.version(_handlerVersion);
    }
    
    @Override
    public boolean duplicates() {
    	return _root != null;
    }

	public void classMetadata(ClassMetadata classMetadata) {
		_classMetadata = classMetadata;
	}    
}
