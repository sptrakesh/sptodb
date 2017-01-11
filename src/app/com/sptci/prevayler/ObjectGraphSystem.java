package com.sptci.prevayler;

import com.sptci.ReflectionUtility;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;

/**
 * Abstracts all the code for decomposing and reconstituting object graphs
 * for the prevalent system.  Due to the inherent limitations in object
 * serialisation, this step is necessary to ensure that de-serialised objects
 * contain proper references to other prevalent objects and not local copies.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans
 *   Pareil Technologies, Inc.</a></p>
 * @author Rakesh Vidyadharan 2008-05-23
 * @version $Id: ObjectGraphSystem.java 22 2008-11-24 19:04:25Z sptrakesh $
 */
abstract class ObjectGraphSystem extends SearchSystem
{
  private static final long serialVersionUID = 1L;

  /**
   * Create a clone of the specified prevalent object and reconstitute object
   * references to other prevalent objects.  Reads in the references from
   * {@link #referenceMap} and reconstitutes the references.  Recursively
   * invokes this method on prevalent objects to ensure that the entire
   * object graph is replicated.
   *
   * @see #populateReference
   * @param object The object that is to be cloned and reconstituted.
   * @return The reconstituted prevalent object that represents a persisted
   *   prevalent object.
   * @throws PrevalentException If errors are encountered while reconsituting
   *   the prevalent object.
   */
  @SuppressWarnings( {"unchecked"} )
  protected PrevalentObject compose( final PrevalentObject object )
      throws PrevalentException
  {
    if ( object == null ) return null;
    if ( getTaskQueue().contains( object ) )
    {
      for ( PrevalentObject o : getTaskQueue() )
      {
        if ( o.equals( object ) ) return o;
      }
    }

    final PrevalentObject obj = (PrevalentObject) object.clone();

    try
    {
      getTaskQueue().add( obj );
      populateReference( obj );
    }
    catch ( IllegalAccessException iex )
    {
      throw new PrevalentException( iex );
    }
    finally
    {
      getTaskQueue().remove( obj );
    }

    return obj;
  }

  /**
   * Populate the references to other prevalent objects in the specified
   * prevalent object.
   *
   * @param object The prevalent object that is being reconstituted.
   * @throws PrevalentException If errors are encountered while fetching
   *   the references to the other prevalent objects.
   * @throws IllegalAccessException If errors are encountered while setting
   *   the field values.
   */
  private void populateReference( final PrevalentObject object )
      throws PrevalentException, IllegalAccessException
  {
    final ReferenceStorage referenceStorage =
        getReferenceStorage( object.getClass() );

    for ( String name : referenceStorage.getFields( object ) )
    {
      final Field field =
          ReflectionUtility.fetchField( name, object );
      final PrimaryStorage primaryStorage =
        getPrimaryStorage( field.getType() );

      if ( Collection.class.isAssignableFrom( field.getType() ) )
      {
        final Collection<PrevalentObject> objects =
            new LinkedHashSet<PrevalentObject>();
        final Collection oids =
            (Collection) referenceStorage.getValue( object, name );

        if ( oids != null )
        {
          for ( Object id : oids )
          {
            PrevalentObject value = primaryStorage.get( id );
            if ( "com.sptci.prevayler.model.Two".equals( object.getClass().getName() ) &&
                "children".equals( field.getName() ) )
            {
              System.out.format( "Referenced object before: %s%n", value );
            }
            value = compose( value );
            if ( "com.sptci.prevayler.model.Two".equals( object.getClass().getName() ) &&
                "children".equals( field.getName() ) )
            {
              System.out.format( "Referenced object after: %s%n", value );
            }
            if ( value != null ) objects.add( value );
            if ( "com.sptci.prevayler.model.Two".equals( object.getClass().getName() ) &&
                "children".equals( field.getName() ) )
            {
              System.out.format( "Still found child with oid: %s in Two: %s%n", id, object.getObjectId() );
            }
          }
        }

        field.set( object, objects );
      }
      else if ( PrevalentObject.class.isAssignableFrom( field.getType() ) )
      {
        final Object oid = referenceStorage.getValue( object, name );
        final PrevalentObject value = primaryStorage.get( oid );
        field.set( object, compose( value ) );
      }
    }
  }

  /**
   * Clone the specified object and decouple references to other prevalent
   * object to make suitable for storage in the prevalent system.  Updates
   * {@link #referenceMap} with references that are decomposed.
   *
   * <p>If a referenced persistent object is not yet persistent, then it is
   * made persistent following persistence by reachability principle.  This
   * addition is affected by invoked {@link #save} on the referenced object
   * resulting in persisting the entire object graph through reachability.</p>
   *
   * @see #fetch( Class, Object )
   * @param object The object that is to be cloned and decoupled.
   * @param executionTime The datetime at which the transaction was executed.
   * @return The decomposed prevalent object.
   * @throws PrevalentException If errors are encountered while processing
   *   the class fields.
   */
  @SuppressWarnings( {"unchecked"} )
  protected PrevalentObject decompose( final PrevalentObject object,
      final Date executionTime ) throws PrevalentException
  {
    final PrevalentObject obj = (PrevalentObject) object.clone();

    for ( Field field : ReflectionUtility.fetchFields( object ).values() )
    {
      if ( PrevalentObject.class.isAssignableFrom( field.getType() ) )
      {
        decomposeObject( obj, field, executionTime );
      }
      else if ( Collection.class.isAssignableFrom( field.getType() ) )
      {
        decomposeCollection( obj, field, executionTime );
      }
    }


    return obj;
  }

  /**
   * Decompose a direct reference represented by the specified field in the
   * prevalent object.
   *
   * @param object The prevalent object to decompose.
   * @param field The field that contains a direct reference to another
   *   prevalent object.
   * @param executionTime The datetime at which the transaction was executed.
   * @throws PrevalentException If errors are encountered while accessing
   *   the field.
   */
  private void decomposeObject( final PrevalentObject object,
      final Field field, Date executionTime ) throws PrevalentException
  {
    try
    {
      final ReferenceStorage referenceStorage =
          getReferenceStorage( object.getClass() );
      PrevalentObject po = (PrevalentObject) field.get( object );
      if ( po == null ) return;

      if ( ( po.getObjectId() == null ) ||
          ( fetch( po.getClass(), po.getObjectId() ) == null ) )
      {
        if ( ! getTaskQueue().contains( po ) ) save( po, executionTime );
      }

      referenceStorage.add( object, field.getName(), po.getObjectId() );
      field.set( object, null );
    }
    catch ( PrevalentException pex )
    {
      throw pex;
    }
    catch ( Throwable t )
    {
      throw new PrevalentException( t );
    }
  }

  /**
   * Decompose a collection or references to other prevalent objects in the
   * prevalent object being managed.
   *
   * @param object The prevalent object that is to be decomposed prior to
   *   storage in the system.
   * @param field The field that contains a collection of references to other
   *   prevalent objects.
   * @param executionTime The datetime at which the transaction was executed.
   * @throws PrevalentException If errors are encountered while fetching the
   *   fields of the prevalent object.
   */
  @SuppressWarnings( {"unchecked"} )
  private void decomposeCollection( final PrevalentObject object,
      final Field field, final Date executionTime ) throws PrevalentException
  {
    final ReferenceStorage referenceStorage =
        getReferenceStorage( object.getClass() );

    try
    {
      Collection collection = (Collection) field.get( object );
      if ( collection == null ) return;

      // Clone the collection to leave the original prevalent object
      // collection untouched.
      if ( collection instanceof Cloneable )
      {
        collection = (Collection) ReflectionUtility.execute( collection, "clone" );
        field.set( object, collection );
      }

      final Collection oids = new LinkedHashSet( collection.size() );
      boolean clearCollection = false;

      for ( Object obj : collection )
      {
        if ( obj instanceof PrevalentObject )
        {
          clearCollection = true;
          PrevalentObject po = (PrevalentObject) obj;

          if ( ( po.getObjectId() == null ) ||
              ( fetch( po.getClass(), po.getObjectId() ) == null ) )
          {
            save( po, executionTime );
          }

          oids.add( po.getObjectId() );
        }
      }

      if ( clearCollection ) collection.clear();
      referenceStorage.add( object, field.getName(), oids );
    }
    catch ( Throwable t )
    {
      throw new PrevalentException( t );
    }
  }

  /**
   * Replace the prevalent object in the field specified from the specified
   * <code>object</code> prevalent object to the <code>po</code> object
   * that exists in the system.
   *
   * @see #checkUnique
   * @param field The field whose value is being updated.
   * @param object The prevalent object that is being updated.
   * @param executionTime The datetime at which the transaction was executed.
   * @throws ConstraintException If the field is marked as unique and the
   *   newObject specified is already associated with another prevalent
   *   object of the same type.
   * @throws PrevalentException If errors are encountered while setting
   *   the value of the field.
   */
  protected void update( final Field field, final PrevalentObject object,
      final Date executionTime ) throws PrevalentException
  {
    final ReferenceStorage referenceStorage = getReferenceStorage( object.getClass() );
    final Object oid = referenceStorage.getValue( object, field.getName() );
    final PrimaryStorage primaryStorage = getPrimaryStorage( field.getType() );
    final IndexStorage indexStorage = getIndexStorage( object.getClass() );

    try
    {
      PrevalentObject source = (PrevalentObject) field.get( object );
      final PrevalentObject destination = primaryStorage.get( oid );

      if ( ( source != null ) && ! getTaskQueue().contains( source ) )
      {
        source = save( source, executionTime );
      }

      if ( ( source == null ) || ! source.equals(  destination ) )
      {
        checkUnique( field, object, source );

        indexStorage.remove( field.getName(), destination, object );
        referenceStorage.remove( object, field.getName() );

        if ( source != null )
        {
          indexStorage.add( field.getName(), source, object );
          referenceStorage.add( object, field.getName(), source.getObjectId() );
        }
      }
    }
    catch ( PrevalentException pex )
    {
      throw pex;
    }
    catch ( Throwable t )
    {
      throw new PrevalentException( t );
    }
  }
}
