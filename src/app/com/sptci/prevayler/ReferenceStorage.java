package com.sptci.prevayler;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * A class used as the storage mechanism for storing the references to
 * other prevalent objects from prevalent objects stored in the system.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans Pareil Technologies, Inc.</a></p>
 * @author Rakesh Vidyadharan 2008-05-23
 * @version $Id: ReferenceStorage.java 4345 2008-06-30 21:22:03Z rakesh $
 */
public class ReferenceStorage implements Serializable
{
  private static final long serialVersionUID = 1L;

  /**
   * A map used to maintain the references for a prevalent object identified
   * by its object id.
   */
  private final Map<Object,FieldStorage> storage =
    new LinkedHashMap<Object,FieldStorage>();

  /**
   * Add the specified reference information for the prevalent object to
   * the store.
   *
   * @see ReferenceStorage.FieldStorage#add
   * @param object The prevalent object whose reference information is being
   *   added to the store.
   * @param name The name of the field that contains a reference.
   * @param oid The oid for the referenced field.
   */
  public void add( final PrevalentObject object, final String name,
      final Object oid )
  {
    if ( ! storage.containsKey( object.getObjectId() ) )
    {
      storage.put( object.getObjectId(), new FieldStorage() );
    }

    storage.get( object.getObjectId() ).add( name, oid );
  }

  /**
   * Return a collection of field names that represent the references to
   * other prevalent objects in the store for the specified prevalent
   * object.
   *
   * @see ReferenceStorage.FieldStorage#getFields
   * @param object The prevalent object whose references are to be fetched.
   * @return The collection of referenced field names.
   */
  public Collection<String> getFields( final PrevalentObject object )
  {
    final Collection<String> collection = new LinkedHashSet<String>();
    if ( object == null ) return collection;

    final FieldStorage store = storage.get( object.getObjectId() );
    if ( store != null ) collection.addAll( store.getFields() );

    return collection;
  }

  /**
   * Return the value associated with the specified prevalent object and
   * reference field name.
   *
   * @see ReferenceStorage.FieldStorage#getValue
   * @param object The prevalent object in which the referenced field exists
   * @param name The name of the reference field.
   * @return The value associated with the object in the store.
   */
  public Object getValue( final PrevalentObject object, final String name )
  {
    Object value = null;
    if ( object == null ) return value;

    final FieldStorage store = storage.get( object.getObjectId() );
    if ( store != null ) value = store.getValue( name );

    return value;
  }

  /**
   * Remove the specified prevalent object from the store.
   *
   * @param object The prevalent object to remove from the store.
   */
  public void remove( final PrevalentObject object )
  {
    if ( object == null ) return;
    storage.remove( object.getObjectId() );
  }

  /**
   * Remove the reference(s) for the specified field in the specified
   * prevalent object.
   *
   * @param object The prevalent object whose field reference(s) are to be
   *   removed.
   * @param field The field whose reference(s) are to be removed.
   */
  public void remove( final PrevalentObject object, final String field )
  {
    if ( object == null ) return;
    final FieldStorage store = storage.get( object.getObjectId() );
    if ( store != null ) store.remove( field );
  }

  /**
   * Remove the reference for the specified prevalent object from the
   * specified field.
   *
   * @param object The prevalent object whose reference is to be removed.
   * @param field The field in the prevalent object whose reference is to
   *   be removed.
   * @param objectId The reference that is to be removed.
   */
  public void remove( final PrevalentObject object, final String field,
      final Object objectId )
  {
    if ( object == null ) return;
    final FieldStorage store = storage.get( object.getObjectId() );
    if ( store != null ) store.remove( field, objectId );
  }

  /**
   * The storage for all the references fields in a prevalent object.
   */
  private class FieldStorage implements Serializable
  {
    private static final long serialVersionUID = 1L;

    /** The map used to maintain the field name to object id mappings. */
    private final Map<String,Object> fieldStorage =
      new LinkedHashMap<String,Object>();

    /**
     * Add the specified mapping to the store.
     *
     * @param name The name of the reference field.
     * @param oid The oid value for the referenced object.
     */
    private void add( final String name, final Object oid )
    {
      fieldStorage.put( name, oid );
    }

    /**
     * Remove the mapping for the specified field from {@link #fieldStorage}.
     *
     * @param name The name of the reference field whose mapping is to be
     *   removed.
     */
    private void remove( final String name )
    {
      fieldStorage.remove( name );
    }

    /**
     * Remove the specified reference from the field.  If the field contains
     * a collection of references, then the specified reference is removed
     * from the collection.  Otherwise this method is identical to {@link
     * #remove( String )}.
     *
     * @param name The name of the reference field whose mapping is to be
     *   removed.
     * @param objectId The object id that is to be removed from the mapping.
     */
    private void remove( final String name, final Object objectId )
    {
      final Object value = fieldStorage.get( name );
      if ( value instanceof Collection )
      {
        ( (Collection) value ).remove( objectId );
      }
      else
      {
        remove( name );
      }
    }

    /**
     * Return a collection of field names that are stored in in this store.
     *
     * @return The collection of field names.
     */
    private Collection<String> getFields()
    {
      return fieldStorage.keySet();
    }

    /**
     * Return the value associated with the field with the specified name.
     *
     * @param name The name of the field whose value is to be retrieved.
     * @return The value associated with the name.
     */
    private Object getValue( final String name )
    {
      return fieldStorage.get( name );
    }
  }
}
