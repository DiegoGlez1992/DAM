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
package com.db4o.internal.handlers;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.metadata.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;
import com.db4o.typehandlers.*;


/**
 * @exclude
 */
public class StandardReferenceTypeHandler implements FieldAwareTypeHandler, IndexableTypeHandler, ReadsObjectIds {
    
    private static final int HASHCODE_FOR_NULL = 72483944; 
    
    private ClassMetadata _classMetadata;

    public StandardReferenceTypeHandler(ClassMetadata classMetadata) {
        classMetadata(classMetadata);
    }
    
    public StandardReferenceTypeHandler(){
    }

    public void defragment(final DefragmentContext context) {
        traverseAllAspects(context, new MarshallingInfoTraverseAspectCommand(ensureFieldList(context)) {
        	
            @Override
            protected int internalDeclaredAspectCount(ClassMetadata classMetadata) {
                return context.readInt();
            }
            
            @Override
			protected void processAspect(ClassAspect aspect, int currentSlot, boolean isNull) {
                if (!isNull) {
                    aspect.defragAspect(context);
                } 
            }
            
            @Override
            public boolean accept(ClassAspect aspect) {
            	return aspect.isEnabledOn(context);
            }
        });
    }

    public void delete(DeleteContext context) throws Db4oIOException {
        context.deleteObject();
    }

    public final void activateAspects(final UnmarshallingContext context) {
    	
        final BooleanByRef schemaUpdateDetected = new BooleanByRef();
        
        ContextState savedState = context.saveState();
        
        TraverseAspectCommand command = new MarshallingInfoTraverseAspectCommand(ensureFieldList(context)) {
        	
        	@Override
        	public boolean accept(ClassAspect aspect) {
        		return aspect.isEnabledOn(context);
        	}
        	
        	@Override
            protected void processAspect(ClassAspect aspect, int currentSlot, boolean isNull) {
            	
			    if(aspect instanceof FieldMetadata){
                    FieldMetadata field = (FieldMetadata) aspect;
                    if(field.updating()){
                        schemaUpdateDetected.value = true;
                    }
                    // TODO: cant the aspect handle it itself?
                    // Probably no because old aspect versions might not be able
                    // to handle null...
                    if (isNull) {
                    	if(field.getStoredType() == null || !field.getStoredType().isPrimitive()) {
                    		field.set(context.persistentObject(), null);
                    	}
                		return;
                    }
                }

                aspect.activate(context);
            }
        };
        traverseAllAspects(context, command);
        
        if(schemaUpdateDetected.value){
            context.restoreState(savedState);
            command = new MarshallingInfoTraverseAspectCommand(ensureFieldList(context)) {
                protected void processAspect(ClassAspect aspect, int currentSlot, boolean isNull) {
                	FieldMetadata field = (FieldMetadata)aspect;
                    if (! isNull) {
						field.attemptUpdate(context);
                    }
                }
                
                public boolean accept(ClassAspect aspect){
                    return aspect instanceof FieldMetadata;
                }

            };
            traverseAllAspects(context, command);
        }
        
    }
    
    public void activate(ReferenceActivationContext context) {
        activateAspects((UnmarshallingContext) context);
    }

	public void write(WriteContext context, Object obj) {
        marshallAspects(obj, (MarshallingContext)context);
    }
    
    public void marshallAspects(final Object obj, final MarshallingContext context) {
    	final Transaction trans = context.transaction();
        final TraverseAspectCommand command = new MarshallingInfoTraverseAspectCommand(ensureFieldList(context)) {
        	
        	@Override
        	protected int internalDeclaredAspectCount(ClassMetadata classMetadata) {
                int aspectCount = classMetadata._aspects.length;
                context.writeDeclaredAspectCount(aspectCount);
                return aspectCount;
            }
            
            @Override
            public boolean accept(ClassAspect aspect) {
            	return aspect.isEnabledOn(context);
            }
            
            @Override
            protected void processAspect(ClassAspect aspect, int currentSlot, boolean isNull) {
            	Object marshalledObject = obj;
                if(aspect instanceof FieldMetadata){
                    FieldMetadata field = (FieldMetadata) aspect;
                    marshalledObject = field.getOrCreate(trans, obj);
                    if(marshalledObject == null) {
                        context.isNull(currentSlot, true);
                        field.addIndexEntry(trans, context.objectID(), null);
                        return;
                    }
                }
                
                aspect.marshall(context, marshalledObject);
            }
            
            @Override
            public void processAspectOnMissingClass(ClassAspect aspect, int currentSlot){            	
            	((MarshallingContext)context).isNull(currentSlot, true);
            }
        };
        traverseAllAspects(context, command);
    }


    public PreparedComparison prepareComparison(Context context, final Object source) {
        if(source == null){
        	return Null.INSTANCE;
        }
        
        if(source instanceof Integer){
            int id = ((Integer)source).intValue();
            return new PreparedComparisonImpl(id, null);
        } 
        
        if(source instanceof TransactionContext){
            TransactionContext tc = (TransactionContext)source;
            Object obj = tc._object;
            int id = idFor(obj, tc._transaction);
            return new PreparedComparisonImpl(id, reflectClassFor(obj));
        }
        
        return platformComparisonFor(source);
    }

    @decaf.RemoveFirst(decaf.Platform.JDK11)
	private PreparedComparison platformComparisonFor(final Object source) {
    	if(source == null) {
    		return new PreparedComparison() {
				public int compareTo(Object obj) {
					return obj == null ? 0 : -1;
				}
			};
    	}
    	//TODO: Move the comparable wrapping to a .Net specific StandardStructHandler
    	if (source instanceof Comparable) {
        	return new PreparedComparison(){
				public int compareTo(Object obj) {
					if(obj == null) {
						return 1;
					}
					Comparable self = (Comparable) source;
					return self.compareTo(obj);
				}        		
        	};
        }
        
        throw new IllegalComparisonException();
	}

	private ReflectClass reflectClassFor(Object obj) {
		return classMetadata().reflector().forObject(obj);
	}

	private int idFor(Object object, Transaction inTransaction) {
		return stream().getID(inTransaction, object);
	}

	private ObjectContainerBase stream() {
		return classMetadata().container();
	}
    
    public final static class PreparedComparisonImpl implements PreparedComparison {
		
		private final int _id;
		
		private final ReflectClass _claxx;
	
		public PreparedComparisonImpl(int id, ReflectClass claxx) {
			_id = id;
			_claxx = claxx;
		}
	
		public int compareTo(Object obj) {
		    if(obj instanceof TransactionContext){
		        obj = ((TransactionContext)obj)._object;
		    }
		    if(obj == null){
		    	return _id == 0 ? 0 : 1;
		    }
		    if(obj instanceof Integer){
				int targetInt = ((Integer)obj).intValue();
				return _id == targetInt ? 0 : (_id < targetInt ? - 1 : 1); 
		    }
		    if(_claxx != null){
		    	if(_claxx.isAssignableFrom(_claxx.reflector().forObject(obj))){
		    		return 0;
		    	}
		    }
		    throw new IllegalComparisonException();
		}
	}

	public final void traverseAllAspects(MarshallingInfo context, TraverseAspectCommand command) {
    	ClassMetadata classMetadata = classMetadata();
        assertClassMetadata(context.classMetadata());
        classMetadata.traverseAllAspects(command);
    }

	protected MarshallingInfo ensureFieldList(MarshallingInfo context) {
		return context;
	}

	private void assertClassMetadata(final ClassMetadata contextMetadata) {
//		if (contextMetadata != classMetadata()) {
//        	throw new IllegalStateException("expecting '" + classMetadata() + "', got '" + contextMetadata + "'");
//        }
	}
    public ClassMetadata classMetadata() {
        return _classMetadata;
    }
    
    public void classMetadata(ClassMetadata classMetadata) {
        _classMetadata = classMetadata;
    }
    
    public boolean equals(Object obj) {
        if(! (obj instanceof StandardReferenceTypeHandler)){
            return false;
        }
        StandardReferenceTypeHandler other = (StandardReferenceTypeHandler) obj;
        if(classMetadata() == null){
            return other.classMetadata() == null;
        }
        return classMetadata().equals(other.classMetadata());
    }
    
    public int hashCode() {
        if(classMetadata() != null){
            return classMetadata().hashCode();
        }
        return HASHCODE_FOR_NULL;
    }
    
    public TypeHandler4 unversionedTemplate() {
        return new StandardReferenceTypeHandler(null);
    }

    public Object deepClone(Object context) {
        TypeHandlerCloneContext typeHandlerCloneContext = (TypeHandlerCloneContext) context;
        StandardReferenceTypeHandler cloned = (StandardReferenceTypeHandler) Reflection4.newInstance(this);
        if(typeHandlerCloneContext.original instanceof StandardReferenceTypeHandler){
            StandardReferenceTypeHandler original = (StandardReferenceTypeHandler) typeHandlerCloneContext.original;
            cloned.classMetadata(original.classMetadata());
        }else{

        	// New logic: ClassMetadata takes the responsibility in 
        	//           #correctHandlerVersion() to set the 
        	//           ClassMetadata directly on cloned handler.
        	
//            if(_classMetadata == null){
//                throw new IllegalStateException();
//            }
        	
            cloned.classMetadata(_classMetadata);
        }
        return cloned;
    }
    
    public void collectIDs(final CollectIdContext context, final Predicate4<ClassAspect> predicate) {
        TraverseAspectCommand command = new MarshallingInfoTraverseAspectCommand(ensureFieldList(context)) {
        	
        	@Override
            protected void processAspect(ClassAspect aspect, int currentSlot, boolean isNull) {
                if(isNull) {
                    return;
                }
                if (predicate.match(aspect)) {
                    aspect.collectIDs(context);
                } else {
                    aspect.incrementOffset(context, context);
                }
            }
        };
        traverseAllAspects(context, command);
    }

    public void cascadeActivation(ActivationContext context) {
    	assertClassMetadata(context.classMetadata());
        context.cascadeActivationToTarget();
    }

    public TypeHandler4 readCandidateHandler(QueryingReadContext context) {
    	if (classMetadata().isArray()) {
    		return this;
    	}
    	return null;
    }

    public void collectIDs(final QueryingReadContext context) throws Db4oIOException {
    	if(collectIDsByTypehandlerAspect(context)){
    		return;
    	}
    	collectIDsByInstantiatingCollection(context);
    }
    
    private boolean collectIDsByTypehandlerAspect(QueryingReadContext context) throws Db4oIOException {
    	final BooleanByRef aspectFound = new BooleanByRef(false);
    	final CollectIdContext subContext =  CollectIdContext.forID(context.transaction(), context.collector(), context.collectionID());
        TraverseAspectCommand command = new MarshallingInfoTraverseAspectCommand(ensureFieldList(subContext)) {
        	
        	@Override
            protected void processAspect(ClassAspect aspect, int currentSlot, boolean isNull) {
                if(isNull) {
                    return;
                }
                if(isCollectIdTypehandlerAspect(aspect)){
                	aspectFound.value = true;
                	aspect.collectIDs(subContext);
                }else {
                	aspect.incrementOffset(subContext, subContext);
                }
            }
        };
        traverseAllAspects(subContext, command);
        return aspectFound.value;
    }
    
    private boolean isCollectIdTypehandlerAspect(ClassAspect aspect){
    	if(! (aspect instanceof TypeHandlerAspect)){
    		return false;
    	}
    	TypeHandler4 typehandler = ((TypeHandlerAspect)aspect)._typeHandler;
    	return  Handlers4.isCascading(typehandler);
    }
    
    private void collectIDsByInstantiatingCollection(final QueryingReadContext context) throws Db4oIOException {
        int id = context.collectionID();
        if (id == 0) {
            return;
        }
        final Transaction transaction = context.transaction();
        final ObjectContainerBase container = context.container();
        Object obj = container.getByID(transaction, id);
        if (obj == null) {
            return;
        }

        // FIXME: [TA] review activation depth
        int depth = DepthUtil.adjustDepthToBorders(2);
        container.activate(transaction, obj, container.activationDepthProvider().activationDepth(depth, ActivationMode.ACTIVATE));
        Platform4.forEachCollectionElement(obj, new Visitor4() {
            public void visit(Object elem) {
                context.add(elem);
            }
        });
    }
    
    public void readVirtualAttributes(final ObjectReferenceContext context){
        TraverseAspectCommand command = new MarshallingInfoTraverseAspectCommand(ensureFieldList(context)) {
        	
        	@Override
            protected void processAspect(ClassAspect aspect, int currentSlot, boolean isNull) {
                if (!isNull) {
                    if(aspect instanceof VirtualFieldMetadata){
                        ((VirtualFieldMetadata)aspect).readVirtualAttribute(context);
                    } else {
                        aspect.incrementOffset(context, context);
                    }
                }
            }
        };
        traverseAllAspects(context, command);
    }

    public void addFieldIndices(final ObjectIdContextImpl context) {
        TraverseAspectCommand command = new MarshallingInfoTraverseAspectCommand(ensureFieldList(context)) {
        	
        	@Override
            protected void processAspect(ClassAspect aspect, int currentSlot, boolean isNull) {
            	if(aspect instanceof FieldMetadata){
	                FieldMetadata field = (FieldMetadata)aspect;
	                if (isNull) {
	                    field.addIndexEntry(context.transaction(), context.objectId(), null);
	                } else {
	                    field.addFieldIndex(context);
	                }
            	}else{
            		aspect.incrementOffset(context.buffer(), context);
            	}
            }
        	
            @Override
            public boolean accept(ClassAspect aspect) {
            	return aspect.isEnabledOn(context);
            }
        	
        };
        traverseAllAspects(context, command);
    }
    
    public void deleteMembers(final DeleteContextImpl context, final boolean isUpdate) {
        TraverseAspectCommand command=new MarshallingInfoTraverseAspectCommand(ensureFieldList(context)) {
        	
        	@Override
            protected void processAspect(ClassAspect aspect, int currentSlot, boolean isNull) {
                if(isNull){
                	if(aspect instanceof FieldMetadata){
                		FieldMetadata field = (FieldMetadata)aspect;
                        field.removeIndexEntry(context.transaction(), context.objectId(), null);
                	}
                	return;
                }
                aspect.delete(context, isUpdate);
            }
        };
        traverseAllAspects(context, command);
    }

    public boolean seekToField(final ObjectHeaderContext context, final ClassAspect aspect) {
        final BooleanByRef found = new BooleanByRef(false);
        TraverseAspectCommand command=new MarshallingInfoTraverseAspectCommand(ensureFieldList(context)) {
        	
        	@Override
        	public boolean accept(ClassAspect aspect) {
        		return aspect.isEnabledOn(_marshallingInfo);
        	}
        	
        	@Override
            protected void processAspect(ClassAspect curField, int currentSlot, boolean isNull) {
                if (curField == aspect) {
                    found.value = !isNull;
                    cancel();
                    return;
                }
                if(!isNull){
                    curField.incrementOffset(_marshallingInfo.buffer(), context);
                }
            }
        };
        traverseAllAspects(context, command);
        return found.value;
    }
	
   public final Object indexEntryToObject(Context context, Object indexEntry){
        if(indexEntry == null){
            return null;
        }
        int id = ((Integer)indexEntry).intValue();
        return ((ObjectContainerBase)context.objectContainer()).getByID2(context.transaction(), id);
    }

	public final void defragIndexEntry(DefragmentContextImpl context) {
		context.copyID();
	}	

    public final Object readIndexEntry(Context context, ByteArrayBuffer a_reader) {
        return new Integer(a_reader.readInt());
    }
    
    public final Object readIndexEntryFromObjectSlot(MarshallerFamily mf, StatefulBuffer statefulBuffer) throws CorruptionException{
        return readIndexEntry(statefulBuffer.transaction().context(), statefulBuffer);
    }
    
    public Object readIndexEntry(ObjectIdContext context) throws CorruptionException, Db4oIOException{
        return new Integer(context.readInt());
    }
    
    public int linkLength() {
    	return Const4.ID_LENGTH;
    }

    public void writeIndexEntry(Context context, ByteArrayBuffer a_writer, Object a_object) {
        
        if(a_object == null){
            a_writer.writeInt(0);
            return;
        }
        
        a_writer.writeInt(((Integer)a_object).intValue());
    }
    
    public TypeHandler4 delegateTypeHandler(Context context){
    	return classMetadata().delegateTypeHandler(context);
    }
    
    public ObjectID readObjectID(InternalReadContext context){
        return ObjectID.read(context);
    }
}
