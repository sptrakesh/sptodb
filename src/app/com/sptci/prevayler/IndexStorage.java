package com.sptci.prevayler;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * A class used as the storage mechanism for storing the indices for
 * prevalent objects in the prevalent system.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans Pareil Technologies, Inc.</a></p>
 * @author Rakesh Vidyadharan 2008-05-22
 * @version $Id: IndexStorage.java 18 2008-07-20 03:35:47Z sptrakesh $
 */
public class IndexStorage implements Serializable
{
  private static final long serialVersionUID = 2L;

  /**
   * The constant used to index null keys.  This is useful to be able to
   * find instances where an indexed field is null.
   */
  private static final String NULL_VALUE = "SPTODB_NULL_FIELD_INDEX_KEY";

  /**
   * A map used to maintain indices for a prevalent object.  The <code>key
   * </code> to the map is the name(s) of the field(s) that are indexed,
   * and the values are {@link IndexStorage.FieldStorage} instances.
   */
  private final Map<String,FieldStorage> storage =
      new LinkedHashMap<String,FieldStorage>();

  /**
   * Add a new index for the specified field to the store.
   *
   * @see IndexStorage.FieldStorage#add
   * @param field The name of the field that is indexed.
   * @param index The index for the field specified.
   * @param object The prevalent object to associate with the index.
   */
  public void add( final String field, final Object index,
      final PrevalentObject object )
  {
    if ( ! storage.containsKey( field ) )
    {
      storage.put( field, new FieldStorage() );
    }

    if ( index instanceof Collection )
    {
      //System.out.format( "Indexing field: %s value: %s object: %s%n", field, index, object );
      final FieldStorage fieldStorage = storage.get( field );

      for ( Object key : (Collection) index )
      {
        fieldStorage.add( key, object );
      }
    }
    else
    {
      storage.get( field ).add( index, object );
    }
  }

  /**
   * Add a new index for the specified fields to the store.
   *
   * @see IndexStorage.FieldStorage#add
   * @param fields The array of field names that are being indexed.
   * @param index The index for the fields specified.
   * @param object The prevalent object to associate with the index.
   */
  public void add( final String[] fields, final Collection index,
      final PrevalentObject object )
  {
    final String name = getFieldName( fields );
    FieldStorage fieldStorage = storage.get( name );

    if ( fieldStorage == null )
    {
      fieldStorage = new FieldStorage();
      storage.put( getFieldName( fields ), fieldStorage );
    }

    for ( Object key : index )
    {
      fieldStorage.add( key, object );
    }
  }

  /**
   * Remove the indices for the specified prevalent object from the store.
   *
   * @see IndexStorage.FieldStorage#remove
   * @param object The prevalent object to remove from the store.
   */
  public void remove( final PrevalentObject object )
  {
    if ( object == null ) return;

    for ( FieldStorage store : storage.values() )
    {
      store.remove( object );
    }
  }

  /**
   * Remove the index entry for the specified reference object
   * that is refernced by the specified parent prevalent object.
   *
   * @param field The name of the field that was indexed.
   * @param key The referenced object that was indexed.
   * @param value The parent prevalent object that holds a reference to
   *   the indexed child prevalent object.
   */
  public void remove( final String field, final Object key,
      final PrevalentObject value )
  {
    final FieldStorage store = storage.get( field );
    if ( store == null ) return;

    store.remove( key, value );
  }

  /**
   * Return the collection of prevalent objects that match the specified
   * <code>index</code> value.
   *
   * @param field The name of the field that was indexed in the prevalent
   *   object.
   * @param index The value of the indexed field to use to retrieve the
   *   objects.
   * @return The collection of matching indexed objects that represent the
   *   prevalent objects.
   */
  public Collection<IndexedObject> get( final String field,
      final Object index )
  {
    final Collection<IndexedObject> collection =
        new LinkedHashSet<IndexedObject>();
    final FieldStorage fs = storage.get( field );

    if ( fs != null )
    {
      final Collection<IndexedObject> coll = fs.get( index );
      if ( coll != null ) collection.addAll( coll );
    }

    return collection;
  }

  /**
   * Determines when the specified index exists in the store.
   *
   * @see IndexStorage.FieldStorage#isIndexed
   * @param fields The array of field names that are indexed.
   * @param values The collection of values for the fields that are to be
   *   checked for existence in the store.
   * @return Returns <code>true</code> if the values are indexed in the
   *   store.
   */
  public boolean isIndexed( final String[] fields, final Object values )
  {
    final FieldStorage fieldStorage = storage.get( getFieldName( fields ) );
    return ( fieldStorage != null ) && fieldStorage.isIndexed( values );
  }

  /**
   * Determines when the specified index exists in the store.
   *
   * @see IndexStorage.FieldStorage#isIndexed
   * @param field The field name that is indexed.
   * @param value The value that is to be checked for existence.
   * @return Returns <code>true</code> if the value is indexed in the
   *   store.
   */
  public boolean isIndexed( final String field, final Object value )
  {
    final FieldStorage fieldStorage = storage.get( field );
    return ( fieldStorage != null ) && fieldStorage.isIndexed( value );
  }

  /**
   * Determine whether the specified field is indexed.  This may be used to
   * quickly determine whether a specified field in an already processed
   * object has been annotated as indexed or not.
   *
   * @param field The name of the field that is to be checked.
   * @return Returns <code>true</code> if the field is indexed.
   */
  public boolean isFieldIndexed( final String field )
  {
    return storage.containsKey( field );
  }

  /**
   * Return the name used to represent the specified array of field names.
   *
   * @param fields The array of field names being indexed.
   * @return The normalised name that represents the field names.
   */
  protected String getFieldName( final String[] fields )
  {
    final StringBuilder builder = new StringBuilder( 64 );
    for ( String name : fields )
    {
      builder.append( name ).append( "#" );
    }

    return builder.toString();
  }

  /**
   * The storage used to maintain the indices for a field or combination of
   * fields.
   */
  private class FieldStorage implements Serializable
  {
    private static final long serialVersionUID = 1L;

    /**
     * The map used to maintain the indexed values and the prevalent objects
     * that match the indexed values.  The <code>key</code> for the map are
     * the indexed field(s) value(s) and the <code>value</code> is a
     * collection that contains the prevalent object instances that match
     * the index.  If the index is unique the collection will have only
     * one instance in it.
     *
     * <p><b>Note:</b> The indexed values are not stored directly since that
     * will drastically increate memory requirements for the system.  The
     * values stored are instances of {@link IndexedObject} from which the
     * indexed object instance may be retrieved.</p>
     */
    private final Map<Object,Collection<IndexedObject>> fieldMap =
      new LinkedHashMap<Object,Collection<IndexedObject>>();

    /**
     * Add the specified index and corresponding prevalent object to the
     * store.
     *
     * @param index The index that is being added.
     * @param object The prevalent object associated with the index.
     */
    private void add( final Object index, final PrevalentObject object )
    {
      final Object key = ( index == null ) ? NULL_VALUE : index;

      if ( ! fieldMap.containsKey( key ) )
      {
        Collection<IndexedObject> collection =
          new LinkedHashSet<IndexedObject>();
        fieldMap.put( key, collection );
      }

      fieldMap.get( key ).add(
          new IndexedObject( object.getClass(), object.getObjectId() ) );
    }

    private void remove( final Object key, final PrevalentObject object )
    {
      final Object index = ( key == null ) ? NULL_VALUE : key;
      final Collection<IndexedObject> collection = fieldMap.get( index );

      if ( collection != null )
      {
        collection.remove(
            new IndexedObject( object.getClass(), object.getObjectId() ) );

        if ( collection.isEmpty() ) { fieldMap.remove( index ); }
      }
    }

    /**
     * Remove the specified prevalent object from the store.
     *
     * @param object The prevalent object to remove from the store.
     */
    private void remove( final PrevalentObject object )
    {
      final LinkedHashSet<Object> remove = new LinkedHashSet<Object>();

      for ( Map.Entry<Object,Collection<IndexedObject>> entry :
          fieldMap.entrySet() )
      {
        final Collection<IndexedObject> collection = entry.getValue();
        collection.remove(
            new IndexedObject( object.getClass(), object.getObjectId() ) );

        if ( collection.isEmpty() )
        {
          remove.add( entry.getKey() );
        }
      }

      for ( Object key : remove )
      {
        fieldMap.remove( key );
      }
    }

    /**
     * Return the objects stored in {@link #fieldMap} for the specified
     * index <code>key</code>.
     *
     * @param index The index whose matching values are to be returned.
     * @return The collection of indexed objects matching the specified
     *   index.
     */
    private Collection<IndexedObject> get( final Object index )
    {
      final Object key = ( index == null ) ? NULL_VALUE : index;
      return fieldMap.get( key );
    }

    /**
     * Determine whether the specified value is indexed in the store.
     *
     * @param values The object(s) that are indexed.
     * @return boolean Returns <code>true</code> if indexed.
     */
    private boolean isIndexed( final Object values )
    {
      final Object key = ( values == null ) ? NULL_VALUE : values;
      return fieldMap.containsKey( key );
    }
  }
}
