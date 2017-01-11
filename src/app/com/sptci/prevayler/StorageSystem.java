package com.sptci.prevayler;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The base class for the prevalent system that provides the storage engine
 * for storing the various prevalent object maintained by the system.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans Pareil Technologies, Inc.</a></p>
 * @author Rakesh 2008-05-22
 * @version $Id: StorageSystem.java 22 2008-11-24 19:04:25Z sptrakesh $
 */
abstract class StorageSystem implements DatabaseSystem
{
  private static final long serialVersionUID = 1L;

  /** The logger to use to log messages/errors. */
  protected static transient final Logger logger =
      Logger.getLogger( "SPTODBLogger" );

  /**
   * A map used to manage the various prevalent objects that may be managed
   * by this prevalent system.  This map provides the primary storage for
   * the prevalent objects maintained in the system.  The <code>key</code>
   * for the map are the various classes that are stored in the prevalent
   * system while the values are the {@link PrimaryStorage} instances that
   * store the prevalent objects of each type.
   */
  private final Map<String,PrimaryStorage> classMap =
      new LinkedHashMap<String,PrimaryStorage>();

  /**
   * A map used to maintain indices for the various prevalent object types
   * that are managed by this prevalent system.  The <code>key</code> for
   * the map are the various classes that are stored in the prevalent
   * system while the values are the {@link IndexStorage} instances that
   * store the indices for the indexed fields in the prevalent objects of
   * each type.
   */
  private final Map<String,IndexStorage> indexMap =
      new LinkedHashMap<String,IndexStorage>();

  /**
   * A map used to maintain the references in a prevalent object to other
   * prevalent objects.  This is used to ensure that references are
   * properly reconstituted when de-serialised.   The <code>key</code> for
   * the map are the various classes that are stored in the prevalent
   * system while the values are the {@link ReferenceStorage} instances that
   * store the references for the prevalent objects referenced by the
   * prevalent object being managed.
   */
  private final Map<String,ReferenceStorage> referenceMap =
      new LinkedHashMap<String,ReferenceStorage>();

  /**
   * A map used to maintain the reverse relationships from prevalent objects
   * to their parents.  This is used to implement configured actions when
   * deleting a prevalent object.
   */
  private final Map<String,RelationStorage> relationMap =
      new LinkedHashMap<String,RelationStorage>();

  /**
   * A task queue used when persisting object graphs to avoid infinite loops
   * due to the recursive nature of following object graphs.
   */
  private static transient TaskQueue taskQueue = new TaskQueue();

  /** The sequence used to generate object ids. */
  private long sequence = 0;

  /**
   * Generate the oid to assign to the specified prevalent object. Default
   * implementation returns an incremented {@link #sequence} value if the
   * prevalent object does not already have an object id.
   *
   * @param object The prevalent object for which an oid is to be generated.
   * @return The oid to assign to the prevalent object.
   */
  @SuppressWarnings( value = "unchecked" )
  protected Object generateOid( final PrevalentObject object )
  {
    Object oid = object.getObjectId();
    if ( oid == null )
    {
      oid = ++sequence;
    }

    return oid;
  }

  /**
   * Return the map used to maintain instances of the specified type of
   * prevalent objects by its object id.
   *
   * @see #getPrimaryStorage( String )
   * @param cls The class whose primary storage is to be retrieved.
   * @return The primary storage used for the specified prevalent type.
   */
  protected PrimaryStorage getPrimaryStorage( final Class cls )
  {
    return getPrimaryStorage( cls.getName() );
  }

  /**
   * Return the map used to maintain instances of the specified type of
   * prevalent objects by its object id.
   *
   * @param name The fully qualified name of the class whose primary storage
   *   is to be retrieved.
   * @return The primary storage used for the specified prevalent type.
   */
  protected PrimaryStorage getPrimaryStorage( final String name )
  {
    if ( ! classMap.containsKey( name ) )
    {
      classMap.put( name, new PrimaryStorage() );
    }

    return classMap.get( name );
  }

  /**
   * Return the map in which the indices for the prevalent class are
   * stored with the indexed field name.
   *
   * @see #getIndexStorage( String )
   * @param cls The class whose index storage is to be retrieved.
   * @return The index storage for the specified prevalent type.
   */
  protected IndexStorage getIndexStorage( final Class cls )
  {
    return getIndexStorage( cls.getName() );
  }

  /**
   * Return the map in which the indices for the prevalent class are
   * stored with the indexed field name.
   *
   * @param name The fully qualified name of the class whose index storage
   *   is to be retrieved.
   * @return The index storage for the specified prevalent type.
   */
  protected IndexStorage getIndexStorage( final String name )
  {
    if ( ! indexMap.containsKey( name ) )
    {
      indexMap.put( name, new IndexStorage() );
    }

    return indexMap.get( name );
  }

  /**
   * Return the map used to manage the references to other prevalent objects
   * for the specified prevalent class.
   *
   * @see #getReferenceStorage( String )
   * @param cls The class whose reference storage is to be retrieved.
   * @return The reference storage for the prevalent class.
   */
  protected ReferenceStorage getReferenceStorage( final Class cls )
  {
    return getReferenceStorage( cls.getName() );
  }

  /**
   * Return the map used to manage the references to other prevalent objects
   * for the specified prevalent class.
   *
   * @param name The fully qualified name of the class whose reference storage
   *   is to be retrieved.
   * @return The reference storage for the prevalent class.
   */
  protected ReferenceStorage getReferenceStorage( final String name )
  {
    if ( ! referenceMap.containsKey( name ) )
    {
      referenceMap.put( name, new ReferenceStorage() );
    }

    return referenceMap.get( name );
  }

  /**
   * Return the map used to manage the relations to other prevalent objects
   * for the specified prevalent class.
   *
   * @see #getRelationStorage( String )
   * @param cls The class whose relations storage is to be retrieved.
   * @return The relations storage for the prevalent class.
   */
  protected RelationStorage getRelationStorage( final Class cls )
  {
    return getRelationStorage( cls.getName() );
  }

  /**
   * Return the map used to manage the relations to other prevalent objects
   * for the specified prevalent class.
   *
   * @param name The fully qualified name of the class whose relation storage
   *   is to be retrieved.
   * @return The relations storage for the prevalent class.
   */
  protected RelationStorage getRelationStorage( final String name )
  {
    if ( ! relationMap.containsKey( name ) )
    {
      relationMap.put( name, new RelationStorage( name ) );
    }

    return relationMap.get( name );
  }

  /**
   * Return the task queue used when persisting objects by reachability.
   *
   * @return The queue of prevalent objects in the object graph.
   */
  protected Collection<PrevalentObject> getTaskQueue()
  {
    return taskQueue.get();
  }

  /**
   * The task queue used to ensure that recursive loops when persisting
   * inter-related object graphs do not result in infinite loops.
   */
  protected static class TaskQueue extends ThreadLocal<Collection<PrevalentObject>>
  {
    protected synchronized Collection<PrevalentObject> initialValue()
    {
      return new LinkedHashSet<PrevalentObject>();
    }
  }
}
