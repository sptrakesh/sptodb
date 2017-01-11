package com.sptci.prevayler;

import com.sptci.ReflectionUtility;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * A base class for prevalent systems that will be managed by Prevayler and
 * wrapped in a {@link org.prevayler.Prevayler} instance.  This class may
 * be sub-classed to extend features or customise the implementation of the
 * various methods used to implement the rules of the database engine.  We
 * hope that you will not need to sub-class this instance.
 *
 * <p>Note that all the  methods provided by this class ensure
 * encapsulation of the data stored in the prevalent system.  All objects
 * stored are clones of the prevalent objects passed in, and all prevalent
 * object instances returned by transactional or query methods are clones of
 * the instances stored in the system.  This does increase the memory
 * footprint of the system, however it is more critical to preserve
 * encapsulation of data.  To improve performance client code may utilise
 * various caching products to cache the results of query execution to reduce
 * the number of copies of the prevalent objects in the JVM heap space.</p>.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans
 *   Pareil Technologies, Inc.</a></p>
 * @see PrevalentSystemFactory
 * @author Rakesh Vidyadharan 2008-05-22
 * @version $Id: PrevalentSystem.java 22 2008-11-24 19:04:25Z sptrakesh $
 */
public class PrevalentSystem extends ObjectGraphSystem
{
  private static final long serialVersionUID = 2L;

  /** The name of the object id field in {@link PrevalentObject}. */
  private static final String OBJECT_ID = "objectId";

  /** The name of the meta data field in {@link PrevalentObject}. */
  private static final String META_DATA = "_sptodbMetaData";

  /** Default constructor. */
  protected PrevalentSystem() {}

  /** {@inheritDoc} */
  public PrevalentObject save( final PrevalentObject object,
      final Date executionTime ) throws PrevalentException
  {
    return ( ( object.isPersistent() ) ? update( object, executionTime ) :
        add( object, executionTime ) );
  }

  /** {@inheritDoc} */
  public PrevalentObject delete( final PrevalentObject object,
      final Date executionTime ) throws PrevalentException
  {
    preDelete( object, executionTime );
    final Object objectId = object.getObjectId();

    if ( getTaskQueue().contains( object ) ) return object;

    try
    {
      getTaskQueue().add( object );

      final PrimaryStorage primaryStorage = getPrimaryStorage( object.getClass() );
      primaryStorage.remove( object );

      remove( object );

      getTaskQueue().remove( object );
      final Field field = ReflectionUtility.fetchField( OBJECT_ID, object );
      field.set( object, null );
      object.set_sptodbMetaData( null );
    }
    catch ( Throwable t )
    {
      logger.log( Level.SEVERE, "Error setting objectId: " + objectId +
          " to null for class: " + object.getClass().getName(), t );
    }
    finally
    {
      getTaskQueue().remove( object );
    }

    return object;
  }

  /** {@inheritDoc} */
  public int count( final Class cls )
  {
    final PrimaryStorage primaryStorage = getPrimaryStorage( cls );
    return primaryStorage.size();
  }

  /**
   * Fetch the prevalent object of the specified type with the specified
   * object id from the prevalent system.
   *
   * @see #compose
   * @param cls The type of the prevalent object.
   * @param oid The object id for the prevalent object to retrieve.
   * @return The prevalent object instance.  Returns <code>null</code> if no
   *   such object is stored in the prevalent system.
   * @throws PrevalentException If errors are encountered while reconstituting
   *   the prevalent object.
   */
  public PrevalentObject fetch( final Class cls, final Object oid )
      throws PrevalentException
  {
    final PrimaryStorage primaryStorage = getPrimaryStorage( cls );
    return compose( primaryStorage.get( oid ) );
  }

  /** {@inheritDoc} */
  public Collection<PrevalentObject> fetch( final Class cls,
      final String field, final Object object )
    throws PrevalentException
  {
    final Collection<PrevalentObject> results =
        new LinkedHashSet<PrevalentObject>();

    final IndexStorage indexStorage = getIndexStorage( cls );
    final Collection<IndexedObject> collection =
        indexStorage.get( field, object );

    for ( IndexedObject io : collection )
    {
      final PrevalentObject po = fetch( io.type, io.objectId );
      results.add( po );
    }

    return results;
  }

  /** {@inheritDoc} */
  public Collection<PrevalentObject> fetchUnion( final Class cls,
      final Map<String,?> parameters ) throws PrevalentException
  {
    final Collection<PrevalentObject> results =
        new LinkedHashSet<PrevalentObject>();

    for ( Map.Entry<String,?> entry : parameters.entrySet() )
    {
      results.addAll( fetch( cls, entry.getKey(), entry.getValue() ) );
    }

    return results;
  }

  /** {@inheritDoc} */
  public Collection<PrevalentObject> fetchIntersection( final Class cls,
      final Map<String,?> parameters ) throws PrevalentException
  {
    final Collection<PrevalentObject> results =
        new LinkedHashSet<PrevalentObject>();

    List<Collection<PrevalentObject>> matches =
        new ArrayList<Collection<PrevalentObject>>( parameters.size() );

    for ( Map.Entry<String,?> entry : parameters.entrySet() )
    {
      final Collection<PrevalentObject> coll =
          fetch( cls, entry.getKey(), entry.getValue() );
      matches.add( coll );
    }

    for ( int i = 0; i < matches.size(); ++i )
    {
      for ( PrevalentObject po : matches.get( i ) )
      {
        boolean add = true;

        for ( int j = 0; j < matches.size(); ++j )
        {
          if ( i != j )
          {
            if ( ! matches.get( j ).contains( po ) )
            {
              add = false;
            }
          }
        }

        if ( add ) results.add( po );
      }
    }

    return results;
  }

  /** {@inheritDoc} */
  public Collection<PrevalentObject> fetch( final Class cls,
      final long start, final long end ) throws PrevalentException
  {
    final Collection<PrevalentObject> results =
        new LinkedHashSet<PrevalentObject>( (int) ( end - start ) );

    final PrimaryStorage primaryStorage = getPrimaryStorage( cls );

    for ( PrevalentObject obj : primaryStorage.get( start, end ) )
    {
      results.add( compose( obj ) );
    }

    return results;
  }

  /** {@inheritDoc} */
  public Collection<PrevalentObject> search( final Query query,
      final Filter filter, final int count, final Sort sort )
      throws PrevalentException
  {
    Collection<PrevalentObject> collection =
        new LinkedHashSet<PrevalentObject>( count );

    try
    {
      search( query, filter, count, sort, collection );
    }
    catch ( PrevalentException e )
    {
      throw e;
    }
    catch ( Throwable t )
    {
      throw new PrevalentException( t );
    }

    return collection;
  }

  /**
   * Add a new prevalent object to the prevalent system.  It is recommended
   * that you over-ride the methods invoked by this method rather than this
   * method itself.
   *
   * @see #preAdd
   * @see #getPrimaryStorage
   * @see #decompose
   * @see #setOid
   * @see #index
   * @param object The object to be added to the system.
   * @param executionTime The time at which the transaction was executed.
   * @return The potentially modified <code>object</code> passed in.
   * @throws PrevalentException If errors are encountered while adding the
   *   object.
   */
  protected PrevalentObject add( final PrevalentObject object,
      final Date executionTime ) throws PrevalentException
  {
    if ( object == null ) return null;
    if ( getTaskQueue().contains( object ) ) return object;

    preAdd( object );

    final PrimaryStorage primaryStorage =
        getPrimaryStorage( object.getClass() );
    setOid( object );
    object.set_sptodbMetaData( new MetaData( executionTime.getTime() ) );

    try
    {
      getTaskQueue().add( object );
      final PrevalentObject obj = decompose( object, executionTime );

      primaryStorage.add( obj );
      index( obj );
    }
    finally
    {
      getTaskQueue().remove( object );
    }

    return object;
  }

  /**
   * Update the specified prevalent object in the prevalent system.  If the
   * specified object does not represent a persistent instance, it is added
   * to the system.  In this regard it is possible to directly use this method
   * always instead of {@link #add}.
   *
   * <p>Note that this method invokes itself recursively to update any objects
   * in the object graph that represent prevalent objects that also need
   * updating.</p>
   *
   * @see #fetch( Class, Object )
   * @see #add
   * @see #update( Field, PrevalentObject, Date )
   * @param object The prevalent object to update in the system.
   * @param executionTime The datetime at which the transaction was executed.
   * @return The potentially modified prevalent object.  The returned object
   *   is modified only if the object is added to the system and when datastore
   *   object id is in use.
   * @throws PrevalentException If errors are encountered while updating
   *   (or adding) the prevalent object.
   */
  protected PrevalentObject update( final PrevalentObject object,
      final Date executionTime ) throws PrevalentException
  {
    if ( object == null ) return null;
    if ( getTaskQueue().contains( object ) ) return object;

    final PrimaryStorage primaryStorage = getPrimaryStorage( object.getClass() );
    final PrevalentObject po = primaryStorage.get( object.getObjectId() );

    try
    {
      getTaskQueue().add( object );

      for ( Field field : ReflectionUtility.fetchFields( object ).values() )
      {
        final Object source = field.get( object );
        final Object destination = field.get( po );

        if ( PrevalentObject.class.isAssignableFrom( field.getType() ) )
        {
          update( field, object, executionTime );
        }
        else if ( source instanceof Collection )
        {
          boolean prevalentObject = false;
          for ( Object obj : (Collection) source )
          {
            if ( obj instanceof PrevalentObject )
            {
              prevalentObject = true;
            }

            break;
          }

          if ( prevalentObject )
          {
            // special handling
          }
          else
          {
            field.set( po, ReflectionUtility.execute( source, "clone" ) );
          }
        }
        else
        {
          updateOrdinaryField( po, field, source, destination );
        }
      }

      object.get_sptodbMetaData().modified = executionTime.getTime();
      po.get_sptodbMetaData().modified = executionTime.getTime();
    }
    catch ( PrevalentException pex )
    {
      throw pex;
    }
    catch ( Throwable t )
    {
      throw new PrevalentException( t );
    }
    finally
    {
      getTaskQueue().remove( object );
    }

    return object;
  }

  /**
   * Set the {@link PrevalentObject#objectId} field to a new value if
   * not already set.
   *
   * @see #generateOid
   * @param object The prevalent object whose primary key is to be set.
   * @throws PrevalentException If errors are encountered while setting the
   *   privary key field.
   */
  protected void setOid( final PrevalentObject object )
      throws PrevalentException
  {
    final Object oid = generateOid( object );

    try
    {
      final Field field = ReflectionUtility.fetchField( OBJECT_ID, object );
      field.set( object, oid );
    }
    catch ( Throwable t )
    {
      throw new PrevalentException( t );
    }
  }

  /**
   * Update an ordinary field (non prevalent object or collection).  De-index
   * and re-index the field value if necessary.
   *
   * @param prevalentObject The prevalent object that is being updated.
   * @param field The field in the prevalent object that is being updated.
   * @param source The new value that is being set.
   * @param destination The old value in the field.
   * @throws IllegalAccessException Reflection error while setting field.
   * @throws InvocationTargetException Reflection error while setting field.
   */
  protected void updateOrdinaryField( final PrevalentObject prevalentObject,
      final Field field, final Object source, final Object destination )
      throws IllegalAccessException, InvocationTargetException
  {
    if ( OBJECT_ID.equals( field.getName() ) ) return;
    if ( META_DATA.equals( field.getName() ) ) return;
    if ( "serialVersionUID".equals( field.getName() ) ) return;

    final IndexStorage indexStorage = getIndexStorage( prevalentObject.getClass() );

    if ( source == null )
    {
      field.set( prevalentObject, source );
      indexStorage.remove( field.getName(), destination, prevalentObject );
    }
    else if ( ! source.equals( destination ) )
    {
      if ( source instanceof Cloneable )
      {
        field.set( prevalentObject, ReflectionUtility.execute( source, "clone" ) );
      }
      else
      {
        field.set( prevalentObject, source );
      }

      if ( indexStorage.isFieldIndexed( field.getName() ) )
      {
        indexStorage.remove( field.getName(), destination, prevalentObject );
        indexStorage.add( field.getName(), source, prevalentObject );
      }
    }
  }
}
