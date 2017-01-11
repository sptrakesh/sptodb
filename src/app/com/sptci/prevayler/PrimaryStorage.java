package com.sptci.prevayler;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * A class used as the primary storage mechanism for persisting prevalent
 * objects in the prevalent system.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans Pareil Technologies, Inc.</a></p>
 * @author Rakesh Vidyadharan 2008-05-22
 * @version $Id: PrimaryStorage.java 4345 2008-06-30 21:22:03Z rakesh $
 */
public class PrimaryStorage implements Serializable
{
  private static final long serialVersionUID = 1L;

  /** The map used as the primary storage container. */
  private Map<Object,PrevalentObject> storage =
    new LinkedHashMap<Object,PrevalentObject>();

  /**
   * Add the specified prevalent object to the primary storage.
   *
   * @param object The prevalent object to store in the system.
   */
  public void add( final PrevalentObject object )
  {
    if ( object == null ) return;
    storage.put( object.getObjectId(), object );
  }

  /**
   * Remove the specified prevalent object from the primary storage.
   *
   * @param object The prevalent object to remove from the system.
   */
  public void remove( final PrevalentObject object )
  {
    if ( object == null ) return;
    storage.remove( object.getObjectId() );
  }

  /**
   * Check to see if the specified object exists in storage.  Note that
   * the checking only checks based upon the object id and not on a deep
   * equality check.
   *
   * @param object The prevalent object to check for.
   * @return Returns <code>true</code> if the object exists in the store.
   */
  public boolean isStored( final PrevalentObject object )
  {
    return ( object != null ) && storage.containsKey( object.getObjectId() );
  }

  /**
   * Return the total number of prevalent objects in {@link #storage}.
   *
   * @return The total number of objects stored.
   */
  public int size()
  {
    return storage.size();
  }

  /**
   * Return the prevalent object identified by its object id from the store.
   *
   * @param oid The object id to use to retrieve the prevalent object.
   * @return Returs the prevalent object.  Returns <code>null</code> if no
   *   matching object exists in the store.
   */
  public PrevalentObject get( final Object oid )
  {
    return storage.get( oid );
  }

  /**
   * Fetch the prevalent objects in the specified range of data.  This
   * method supports display of paginated view of the prevalent objects.
   * Note that the objects are returned in insertion order.
   *
   * @param start The starting index (inclusive) from which to fetch the
   *   prevalent objects.
   * @param end The ending index (exclusive) to which to fetch the
   *   prevalent objects.
   * @return The collection of prevalent objects.  Returns an empty
   *   collection if there are no objects in the specified range.
   */
  public Collection<PrevalentObject> get( final long start, final long end )
  {
    final Collection<PrevalentObject> collection =
	new LinkedHashSet<PrevalentObject>( (int) ( end - start ) );

    int index = 0;
    for ( Map.Entry<Object,PrevalentObject> entry : storage.entrySet() )
    {
      if ( ( index >= start ) && ( index < end ) )
      {
	collection.add( entry.getValue() );
      }

      if ( index >= end ) break;
    }

    return collection;
  }
}
