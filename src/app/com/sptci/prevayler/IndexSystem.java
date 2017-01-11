package com.sptci.prevayler;

import com.sptci.ReflectionUtility;
import com.sptci.prevayler.annotations.Index;
import com.sptci.prevayler.annotations.Indices;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Abstracts all index management operations for the prevalent system.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans Pareil Technologies, Inc.</a></p>
 * @author Rakesh Vidyadharan 2008-05-23
 * @version $Id: IndexSystem.java 22 2008-11-24 19:04:25Z sptrakesh $
 */
abstract class IndexSystem extends StorageSystem
{
  private static final long serialVersionUID = 1L;

  /**
   * Check all unique constraints (including object id) for the specified
   * prevalent object.
   *
   * @see #checkIndices
   * @see #checkIndex
   * @see #checkFields
   * @param object The prevalent object to check.
   * @throws com.sptci.prevayler.ConstraintException If unique constraints are violated.
   * @throws com.sptci.prevayler.PrevalentException If errors are encountered while processing
   *   the fields of the prevalent object.
   */
  protected void checkUnique( final PrevalentObject object )
    throws PrevalentException
  {
    if ( object.getObjectId() != null )
    {
      final PrimaryStorage primaryStorage =
        getPrimaryStorage( object.getClass() );
      if ( primaryStorage.isStored( object ) )
      {
        throw new ConstraintException( object );
      }
    }

    checkIndices( object );
    final Index index = object.getClass().getAnnotation( Index.class );
    if ( index != null ) checkIndex( index, object );
    checkFields( object );
  }

  /**
   * Check the {@link com.sptci.prevayler.annotations.Indices} annotations
   * on the specified prevalent object and check unique constraints.
   *
   * @see #checkIndex
   * @param object The prevalent object to check.
   * @throws com.sptci.prevayler.ConstraintException If a unique constraint is
   *   violated.
   * @throws com.sptci.prevayler.PrevalentException If exceptions are
   *   encountered while processing the fields of the prevalent object.
   */
  private void checkIndices( final PrevalentObject object )
    throws PrevalentException
  {
    final Indices indices = object.getClass().getAnnotation( Indices.class );

    if ( indices != null )
    {
      for ( Index index : indices.value() )
      {
        checkIndex( index, object );
      }
    }
  }

  /**
   * Check the index specified at the class level on the prevalent object.
   *
   * @param index The annotation for the prevalent object.
   * @param object The prevalent object to check.
   * @throws com.sptci.prevayler.ConstraintException If a unique constraint
   *   is violated.
   * @throws com.sptci.prevayler.PrevalentException If exceptions are
   *   encountered while processing the fields of the prevalent object.
   */
  @SuppressWarnings( {"unchecked"} )
  private void checkIndex( Index index, final PrevalentObject object )
    throws PrevalentException
  {
    final IndexStorage indexStorage = getIndexStorage( object.getClass() );

    if ( index.unique() )
    {
      final StringBuilder builder = new StringBuilder( 64 );
      final Collection collection = new LinkedHashSet( index.members().length );

      for ( String name : index.members() )
      {
        builder.append( name ).append( "#" );

        try
        {
          collection.add( ReflectionUtility.fetchObject( name, object ) );
        }
        catch ( Throwable t )
        {
          throw new PrevalentException( t );
        }
      }

      if ( indexStorage.isIndexed( index.members(), collection ) )
      {
        throw new ConstraintException( object,
            builder.toString().replace( "#", "," ) );
      }
    }
  }

  /**
   * Check all the fields in the prevalent object for {@link
   * com.sptci.prevayler.annotations.Index} annotation and check for
   * unique constraint violations.
   *
   * @param object The prevalent object to check.
   * @throws com.sptci.prevayler.ConstraintException If a unique constraint
   *   is violated.
   * @throws com.sptci.prevayler.PrevalentException If exceptions are
   *   encountered while processing the fields of the prevalent object.
   */
  protected void checkFields( final PrevalentObject object )
    throws PrevalentException
  {
    final IndexStorage indexStorage = getIndexStorage( object.getClass() );

    for ( Field field : ReflectionUtility.fetchFields( object ).values() )
    {
      final Index index = field.getAnnotation( Index.class );
      if ( index != null )
      {
        if ( index.unique() )
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

          if ( indexStorage.isIndexed( field.getName(), value ) )
          {
            throw new ConstraintException( object, field.getName() );
          }
        }
      }
    }
  }

  /**
   * Manage additional maps required to support qeries on the prevalent
   * object.
   *
   * @see #indexFields
   * @see #indexClass
   * @param object The prevalent object to add query support for.
   * @throws com.sptci.prevayler.PrevalentException If errors are encountered
   *   while fetching the values of the fields in prevalent object.
   */
  protected void index( final PrevalentObject object ) throws PrevalentException
  {
    indexFields( object );
    indexClass( object );
  }

  /**
   * Process {@link com.sptci.prevayler.annotations.Index} annotations on the
   * fields of the specified prevalent object and manage the {@link
   * StorageSystem#indexMap} as appropriate.
   *
   * @param object The prevalent object to index.
   * @throws com.sptci.prevayler.PrevalentException If errors are encountered
   *   while processing the object fields.
   */
  protected void indexFields( final PrevalentObject object )
    throws PrevalentException
  {
    final IndexStorage indexStorage = getIndexStorage( object.getClass() );

    try
    {
      for ( Field field : ReflectionUtility.fetchFields( object ).values() )
      {
        final Index index = field.getAnnotation( Index.class );

        if ( index != null )
        {
          final Object value = field.get( object );
          indexStorage.add( field.getName(), value, object );
        }
      }
    }
    catch ( Throwable t )
    {
      throw new PrevalentException( t );
    }
  }

  /**
   * Process the index annotations on the prevalent object and manage the
   * {@link StorageSystem#indexMap} as appropriate.
   *
   * @see #processIndices
   * @see #processIndex
   * @param object The prevalent object to process.
   * @throws com.sptci.prevayler.PrevalentException If errors are encountered
   *   while processing the field values of the prevalent object.
   */
  protected void indexClass( final PrevalentObject object )
    throws PrevalentException
  {
    processIndices( object );

    final Index index = object.getClass().getAnnotation( Index.class );
    if ( index != null ) processIndex( index, object );
  }

  /**
   * Process the {@link com.sptci.prevayler.annotations.Indices} annotation
   * and manage the {@link StorageSystem#indexMap} as appropriate.
   *
   * @see #processIndex
   * @param object The prevalent object whose class level index annotations
   *   are to be processed.
   * @throws com.sptci.prevayler.PrevalentException If errors are encountered
   *   while processing the fields of the prevalent object.
   */
  private void processIndices( final PrevalentObject object )
    throws PrevalentException
  {
    final Indices indices = object.getClass().getAnnotation( Indices.class );
    if ( indices != null )
    {
      for ( Index index : indices.value() )
      {
        processIndex( index, object );
      }
    }
  }

  /**
   * Process the {@link com.sptci.prevayler.annotations.Index} annotation
   * at the class level of the specified prevalent object.
   *
   * @param index The annotation to process.
   * @param object The prevalent object to process.
   * @throws com.sptci.prevayler.PrevalentException If errors are encountered while processing
   *   the fields in the prevalent object.
   */
  @SuppressWarnings( {"unchecked"} )
  private void processIndex( final Index index,
      final PrevalentObject object ) throws PrevalentException
  {
    final IndexStorage indexStorage = getIndexStorage( object.getClass() );

    try
    {
      final Collection collection = new LinkedHashSet( index.members().length );

      for ( String name : index.members() )
      {
        collection.add( ReflectionUtility.fetchObject( name, object ) );
      }

      indexStorage.add( index.members(), collection, object );
    }
    catch ( Throwable t )
    {
      throw new PrevalentException( t );
    }
  }

  /**
   * Remove the index for the specified prevalent object.  This is typically
   * called prior to deleting a prevalent object.
   *
   * @param object The prevalent object to de-index.
   * @throws PrevalentException If errors are encountered while removing the
   *   index for the prevalent object.
   */
  protected void remove( final PrevalentObject object )
      throws PrevalentException
  {
    final IndexStorage indexStorage = getIndexStorage( object.getClass() );
    indexStorage.remove( object );
  }
}
