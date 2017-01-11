package com.sptci.prevayler;

import com.sptci.ReflectionUtility;
import com.sptci.prevayler.annotations.ForeignKey;
import com.sptci.prevayler.annotations.ForeignKeys;
import com.sptci.prevayler.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * System that abstracts all constraint enforcing rules for the prevalent
 * system.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans Pareil Technologies, Inc.</a></p>
 * @author Rakesh Vidyadharan 2008-05-23
 * @version $Id: ConstraintSystem.java 22 2008-11-24 19:04:25Z sptrakesh $
 */
abstract class ConstraintSystem extends IndexSystem
{
  private static final long serialVersionUID = 1L;

  /**
   * Check the prevalent object specified to ensure that it may be safely
   * added to the prevalent system.
   *
   * @see #checkUnique
   * @param object The prevalent object to check.
   * @throws PrevalentException If the checking fails.
   */
  protected void preAdd( final PrevalentObject object )
    throws PrevalentException
  {
    checkUnique( object );
    checkNull( object );
  }

  /**
   * Process any delete constraints configured for the specified prevalent
   * object.
   *
   * @see #preDelete( String, PrevalentObject, Date )
   * @param object The prevalent object that is to be deleted from the system.
   * @param executionTime The datetime at which the transaction was executed.
   * @throws PrevalentException If the object cannot be deleted due to
   *   constraints or other considerations.
   */
  protected void preDelete( final PrevalentObject object,
      final Date executionTime ) throws PrevalentException
  {
    final RelationStorage relationStorage = getRelationStorage( object.getClass() );

    for ( String className : relationStorage.getRelations() )
    {
      preDelete( className, object, executionTime );
    }
  }

  /**
   * Check the foreign key relationship from instances of the specified
   * class to the prevalent object specified and process as necessary.  The
   * appropriate actions are defined by the {@link
   * com.sptci.prevayler.annotations.ForeignKey#deleteAction()} value.
   *
   * @see #cascadeDelete
   * @see #nullReference
   * @param className The prevalent class that holds references to the
   *   prevalent object to be deleted.
   * @param object The prevalent object to be deleted.
   * @param executionTime The datetime at which the transaction was executed.
   * @throws DeleteException If the object cannot be deleted due to references
   *   to it from other prevalent objects.
   * @throws PrevalentException If the prevalent object cannot be deleted.
   */
  protected void preDelete( final String className,
      final PrevalentObject object, final Date executionTime )
    throws PrevalentException
  {
    final RelationStorage relationStorage = getRelationStorage( object.getClass() );

    for ( Map.Entry<String, ForeignKey.DeleteAction> entry :
        relationStorage.getDeleteRules( className ).entrySet() )
    {
      switch ( entry.getValue() )
      {
        case CASCADE:
          cascadeDelete( className, entry.getKey(), object, executionTime );
          break;
        case NULL:
          nullReference( className, entry.getKey(), object );
          break;
        case EXCEPTION:
          throw new DeleteException( object );
      }
    }
  }

  /**
   * Cascade delete all objects that hold a reference to the specified
   * prevalent object.
   *
   * @see IndexStorage#get(String, Object)
   * @see #fetch
   * @see #delete
   * @param className The fully qualified class name of the prevalent object
   *   that holds a reference to the specified prevalent object that is
   *   being deleted.
   * @param field The name of the field in <code>className</code> that is
   *   a reference to the prevalent object being deleted.
   * @param object The prevalent object that is being deleted from the system.
   * @param executionTime The datetime at which the transaction was executed.
   * @throws PrevalentException If errors are encountered while deleting the
   *   referencing object.
   */
  private void cascadeDelete( final String className, final String field,
      final PrevalentObject object, final Date executionTime )
    throws PrevalentException
  {
    final IndexStorage indexStorage = getIndexStorage( className );

    for ( IndexedObject obj : indexStorage.get( field, object ) )
    {
      final PrevalentObject po = fetch( obj.type, obj.objectId );
      delete( po, executionTime );
    }
  }

  /**
   * Set the <code>field</code> in <code>className</code> that refers to
   * <code>object</code> to <code>null</code>.  If the <code>field</code>
   * is a {@link java.util.Collection}, then the <code>object</code> is
   * removed from it.
   *
   * @param className The fully qualified class name of the prevalent object
   *   that holds a reference to the specified prevalent object being deleted.
   * @param field The field in <code>className</code> that holds a reference
   *   to the prevalent object being deleted.
   * @param object The prevalent object being deleted from the system.
   * @throws PrevalentException If errors are encountered while setting
   *   the field to null.
   */
  private void nullReference( final String className, final String field,
      final PrevalentObject object ) throws PrevalentException
  {
    final IndexStorage indexStorage = getIndexStorage( className );

    try
    {
      for ( IndexedObject obj : indexStorage.get( field, object ) )
      {
        final PrevalentObject po = fetch( obj.type, obj.objectId );

        final ReferenceStorage referenceStorage =
            getReferenceStorage( obj.type );
        referenceStorage.remove( po, field, object );
        indexStorage.remove( field, object, po );
      }
    }
    catch ( Throwable t )
    {
      throw new PrevalentException( t );
    }
  }

  /**
   * Over-ridden to process any {@link com.sptci.prevayler.annotations.ForeignKeys}
   * and {@link com.sptci.prevayler.annotations.ForeignKey} annotations.
   *
   * @see IndexSystem#checkUnique
   * @see #checkForeignKeys
   * @see #checkForeignKey
   * @param object The prevalent object to check.
   * @throws ConstraintException If unique constraints are violated.
   * @throws PrevalentException If errors are encountered while processing
   *   the fields of the prevalent object.
   */
  @Override
  protected void checkUnique( final PrevalentObject object )
    throws PrevalentException
  {
    checkForeignKeys( object );

    final ForeignKey key = object.getClass().getAnnotation( ForeignKey.class );
    if ( key != null ) checkForeignKey( key, object );

    super.checkUnique( object );
  }

  /**
   * Check the {@link com.sptci.prevayler.annotations.ForeignKeys} annotation
   * for the prevalent class and perform actions as necessary.
   *
   * @param object The prevalent object whose annotation is to be used to
   *   check constraints.
   * @throws ConstraintException If a unique constraint is violated.
   * @throws PrevalentException If exceptions are encountered while processing
   *   the fields of the prevalent object.
   */
  private void checkForeignKeys( final PrevalentObject object )
    throws PrevalentException
  {
    final ForeignKeys keys = object.getClass().getAnnotation( ForeignKeys.class );
    if ( keys == null ) return;

    for ( ForeignKey key : keys.value() )
    {
      checkForeignKey( key, object );
    }
  }

  /**
   * Check the {@link com.sptci.prevayler.annotations.ForeignKey} annotation
   * and check for unique constraint violations.
   *
   * @see #addReference
   * @param key The annotation to check for unique constraint.
   * @param object The prevalent object being checked.
   * @throws ConstraintException If a unique constraint is violated.
   * @throws PrevalentException If exceptions are encountered while processing
   *   the fields of the prevalent object.
   */
  private void checkForeignKey( final ForeignKey key,
      final PrevalentObject object ) throws PrevalentException
  {
    addReference( object, key, key.member() );
    final IndexStorage indexStorage = getIndexStorage( object.getClass() );

    if ( key.unique() )
    {
      Object value;

      try
      {
        value = ReflectionUtility.fetchObject( key.member(), object );
      }
      catch ( Throwable t )
      {
        throw new PrevalentException( t );
      }

      if ( indexStorage.isIndexed( key.member(), value ) )
      {
        throw new ConstraintException( object, key.member() );
      }
    }
  }

  /**
   * Over-ridden to check for {@link com.sptci.prevayler.annotations.ForeignKey}
   * annotation and check for unique constraint violations. Super-class
   * implementation is also applied.
   *
   * @see #addReference
   * @see IndexSystem#checkFields
   * @param object The prevalent object to check.
   * @throws ConstraintException If a unique constraint is violated.
   * @throws PrevalentException If exceptions are encountered while processing
   *   the fields of the prevalent object.
   */
  @Override
  protected void checkFields( final PrevalentObject object )
    throws PrevalentException
  {
    final IndexStorage indexStorage = getIndexStorage( object.getClass() );

    for ( Field field : ReflectionUtility.fetchFields( object ).values() )
    {
      final ForeignKey key = field.getAnnotation( ForeignKey.class );
      if ( key != null )
      {
        addReference( object, key, field.getName() );

        if ( key.unique() )
        {
          Object value;

          try
          {
            value =
                ReflectionUtility.fetchObject( field.getName(), object );
          }
          catch ( Throwable t )
          {
            throw new PrevalentException( t );
          }

          if ( ( value != null ) && value instanceof PrevalentObject )
          {
            checkUnique( field, object, (PrevalentObject) value );
          }

          if ( indexStorage.isIndexed( field.getName(), value ) )
          {
            throw new ConstraintException( object, field.getName() );
          }
        }
      }
    }

    super.checkFields( object );
  }

  /**
   * Maintain the reference relationships between the specified prevalent
   * object and any other prevalent object the object references.  This method
   * adds support for managed relations.
   *
   * @param object The prevalent object whose references are to be processed.
   * @param key The foreign key annotation for the field.
   * @param name The name of the field that references a prevalent object.
   *   This is necessary since the <code>key</code> may not contain the
   *   name of the field it references if annotated at the field level.
   * @throws PrevalentException If errors are encountered while accessing the
   *   field in <code>object</code>.
   */
  private void addReference( final PrevalentObject object,
      final ForeignKey key, final String name ) throws PrevalentException
  {
    try
    {
      final Field field = ReflectionUtility.fetchField( name, object );
      Class cls = field.getType();
      if ( Collection.class.isAssignableFrom( cls ) )
      {
        cls = Class.forName( key.collectionEntry() );
      }

      final RelationStorage relationStorage = getRelationStorage( cls );
      relationStorage.add( object, name, key.deleteAction() );
    }
    catch ( Throwable t )
    {
      throw new PrevalentException( t );
    }
  }

  /**
   * Check the {@link com.sptci.prevayler.annotations.NotNull} annotation on
   * the prevalent object and its fields and throw exceptions if necessary.
   *
   * @see #checkNull( String, PrevalentObject )
   * @see #checkNullFields
   * @param object The prevalent object that is to be verified.
   * @throws NullException If the field value is <code>null</code>.
   * @throws PrevalentException If errors are encountered while fetching the
   *   field value.
   */
  private void checkNull( final PrevalentObject object )
      throws PrevalentException
  {
    final NotNull annotation = object.getClass().getAnnotation( NotNull.class );
    if ( annotation != null )
    {
      for ( String member : annotation.members() )
      {
        checkNull( member, object );
      }
    }

    checkNullFields( object );
  }

  /**
   * Check the value of the member identified by the name specified in the
   * prevalent object for null value.
   *
   * @param member The name of the field that is to be checked.
   * @param object The prevalent object to check.
   * @throws NullException If the field value is <code>null</code>.
   * @throws PrevalentException If errors are encountered while fetching the
   *   field value.
   */
  private void checkNull( final String member, final PrevalentObject object )
      throws PrevalentException
  {
    Object value;

    try
    {
      value = ReflectionUtility.fetchObject( member, object );
    }
    catch ( Throwable t )
    {
      throw new PrevalentException( t );
    }

    if ( value == null )
    {
      throw new NullException( object, member );
    }
  }

  /**
   * Check all the fields in the prevalent object to see if they have been
   * annotated with the {@link com.sptci.prevayler.annotations.NotNull}
   * annotation.  Annotated fields are checked to ensure that their value is
   * non-null.
   *
   * @param object The prevalent object whose fields are to be checked.
   * @throws NullException If the field value is <code>null</code>.
   * @throws PrevalentException If errors are encountered while fetching the
   *   field value.
   */
  private void checkNullFields( final PrevalentObject object )
      throws PrevalentException
  {
    try
    {
      for ( Field field : ReflectionUtility.fetchFields( object ).values() )
      {
        final NotNull ann = field.getAnnotation( NotNull.class );
        if ( ann != null )
        {
          Object value = field.get( object );
          if ( value == null ) throw new NullException( object, field.getName() );
        }
      }
    }
    catch ( NullException nex )
    {
      throw nex;
    }
    catch ( Throwable t )
    {
      throw new PrevalentException( t );
    }
  }

  /**
   * Check the specified child prevalent object to see if a unique constraint
   * is defined on the <code>parent</code> object.
   *
   * @param field The field in <code>parent</code> that the child represents.
   * @param parent The parent prevalent object.
   * @param child The referenced prevalent object in <code>parent</code>
   * @throws ConstraintException If a unique constratint violation occurs.
   */
  protected void checkUnique( final Field field, final PrevalentObject parent,
      final PrevalentObject child ) throws ConstraintException
  {
    if ( child == null ) return;
    final IndexStorage indexStorage = getIndexStorage( parent.getClass() );
    boolean unique = false;

    ForeignKey key = field.getAnnotation( ForeignKey.class );
    if ( key == null )
    {
      key = parent.getClass().getAnnotation( ForeignKey.class );
      if ( field.getName().equals( key.member() ) )
      {
        unique = key.unique();
      }
      else
      {
        ForeignKeys keys = parent.getClass().getAnnotation( ForeignKeys.class );
        for ( ForeignKey k : keys.value() )
        {
          if ( field.getName().equals( k.member() ) )
          {
            unique = k.unique();
          }
        }
      }
    }

    if ( unique && indexStorage.isIndexed( field.getName(), child ) )
    {
      throw new ConstraintException( parent, field.getName() );
    }
  }

  /**
   * Over-ridden to process {@link com.sptci.prevayler.annotations.ForeignKey}
   * annotations on the fields of the specified prevalent object and manage
   *  the {@link #indexMap} as appropriate.  Also processes references from
   * {@link ReferenceStorage} to ensure that foreign keys are indexed.
   * Super-class impelementation is also invoked.
   *
   * @see IndexSystem#indexFields
   * @param object The prevalent object to index.
   * @throws PrevalentException If errors are encountered while processing
   *   the object fields.
   */
  @Override
  protected void indexFields( final PrevalentObject object )
    throws PrevalentException
  {
    final IndexStorage indexStorage = getIndexStorage( object.getClass() );
    final ReferenceStorage referenceStorage =
        getReferenceStorage( object.getClass() );

    try
    {
      for ( Field field : ReflectionUtility.fetchFields( object ).values() )
      {
        final ForeignKey key = field.getAnnotation( ForeignKey.class );
        if ( key == null ) continue;

        final Object value = referenceStorage.getValue( object, field.getName() );
        if ( value != null )
        {
          if ( value instanceof Collection )
          {
            Collection collection = (Collection) value;
            final PrimaryStorage primaryStorage =
                getPrimaryStorage( key.collectionEntry() );

            for ( Object oid : collection )
            {
              final PrevalentObject po = primaryStorage.get( oid );
              indexStorage.add( field.getName(), po, object );
            }
          }
          else
          {
            indexStorage.add( field.getName(), value, object );
          }
        }
      }
    }
    catch ( Throwable t )
    {
      throw new PrevalentException( t );
    }

    super.indexFields( object );
  }

  /**
   * Over-ridden to process the foreign key annotations on the prevalent
   * object and manage the @link StorageSystsem#indexMap} as appropriate.  The
   * {@link ReferenceStorage} is used to fetch the appropriate referenced
   * prevalent object.  The super-class implementation is also invoked.
   *
   * @see #processForeignKeys
   * @see #processForeignKey
   * @see IndexSystem#indexClass
   * @param object The prevalent object to process.
   * @throws com.sptci.prevayler.PrevalentException If errors are encountered
   *   while processing the field values of the prevalent object.
   */
  @Override
  protected void indexClass( final PrevalentObject object )
    throws PrevalentException
  {
    processForeignKeys( object );

    final ForeignKey key = object.getClass().getAnnotation( ForeignKey.class );
    if ( key != null ) processForeignKey( key, object );

    super.indexClass( object );
  }

  /**
   * Process the foreign keys annotation for the prevalent class.
   *
   * @see #processForeignKey(com.sptci.prevayler.annotations.ForeignKey, PrevalentObject)
   * @param object The prevalent object that is to be indexed if necessary.
   * @throws PrevalentException If errors are encountered while fetching the
   *   field value.
   */
  private void processForeignKeys( final PrevalentObject object )
    throws PrevalentException
  {
    final ForeignKeys keys = object.getClass().getAnnotation( ForeignKeys.class );
    if ( keys == null ) return;

    for ( ForeignKey key : keys.value() )
    {
      processForeignKey( key, object );
    }
  }

  /**
   * Process the foreign key annotation for a field declared at the prevalent
   * class level and manage the {@link StorageSystem#indexMap} as appropriate.
   *
   * @param key The annotation being processed.
   * @param object The prevalent object that is to be indexed if necessary.
   * @throws PrevalentException If errors are encountered while fetching the
   *   field value.
   */
  private void processForeignKey( final ForeignKey key,
      final PrevalentObject object ) throws PrevalentException
  {
    final IndexStorage indexStorage = getIndexStorage( object.getClass() );
    final ReferenceStorage referenceStorage = getReferenceStorage( object.getClass() );

    try
    {
      final Object value = referenceStorage.getValue( object, key.member() );
      if ( value == null ) return;

      if ( value instanceof Collection )
      {
        Collection collection = (Collection) value;
        final PrimaryStorage primaryStorage =
            getPrimaryStorage( key.collectionEntry() );

        for ( Object oid : collection )
        {
          final PrevalentObject po = primaryStorage.get( oid );
          indexStorage.add( key.member(), po, object );
        }
      }
      else
      {
        indexStorage.add( key.member(), value, object );
      }
    }
    catch ( Throwable t )
    {
      throw new PrevalentException( t );
    }
  }

  /**
   * Over-ridden to remove the references to the prevalent object being
   * removed from the system from the reference storage container.
   *
   * {@inheritDoc}
   */
  @Override
  protected void remove( final PrevalentObject object )
      throws PrevalentException
  {
    super.remove( object );
    final ReferenceStorage referenceStorage =
        getReferenceStorage( object.getClass() );
    referenceStorage.remove( object );
  }
}
