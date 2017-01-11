package com.sptci.prevayler;

import static com.sptci.prevayler.PrevalentSystemFactory.getPrevayler;
import com.sptci.prevayler.query.Count;
import com.sptci.prevayler.query.Fetch;
import com.sptci.prevayler.query.FetchByIndex;
import com.sptci.prevayler.query.FetchByIndices;
import com.sptci.prevayler.query.FetchRange;
import com.sptci.prevayler.query.Search;
import com.sptci.prevayler.transaction.Delete;
import com.sptci.prevayler.transaction.Save;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

import java.util.Collection;
import java.util.Map;

/**
 * A facade around the prevalent system used to present a more natural
 * programming interface than that provided by prevayler.
 *
 * <p>The following shows sample use of this class:</p>
 * <pre>
 *   import com.sptci.prevayler.PrevalentException;
 *   import com.sptci.prevayler.PrevalentManager;
 *
 *     ...
 *     MyPrevalentObject po = new MyPrevalentObject( ... );
 *     po.setXxx( ... );
 *
 *     final PrevalentManager<MyPrevalentObject> pm = new PrevalentManager<MyPrevalentObject>();
 *     try
 *     {
 *       po = pm.save( po );
 *       System.out.format( "Total MyPrevalentObjects in system: %d%n",
 *           po.count( MyPrevalentObject.class ) );
 *     }
 *     catch ( PrevalentException pex )
 *     {
 *       // handle error
 *     }
 * </pre>
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans
 * Pareil Technologies, Inc.</a></p>
 *
 * @see PrevalentSystemFactory
 * @author Rakesh Vidyadharan 2008-07-12
 * @version $Id: PrevalentManager.java 22 2008-11-24 19:04:25Z sptrakesh $
 */
public class PrevalentManager<P extends PrevalentObject> implements Database<P>
{
  private static final long serialVersionUID = 1l;

  /**
   * Save the specified prevalent object to the prevalent system.  If the
   * object does not exist, it is created.  It it exists, the persisted
   * object is updated.
   *
   * @param object The prevalent object to save to the prevalent system.
   * @return The updated prevalent object instance.
   * @throws PrevalentException If errors are encountered while saving the
   *   prevalent object.
   */
  @SuppressWarnings( {"unchecked"} )
  public P save( final P object ) throws PrevalentException
  {
    try
    {
      final Save<P> save = new Save<P>( object );
      return (P) getPrevayler().execute( save );
    }
    catch ( PrevalentException pex )
    {
      throw pex;
    }
    catch ( Throwable t )
    {
      throw new PrevalentException( "Error saving object of type: " +
          object.getClass().getName(), t );
    }
  }

  /**
   * Delete the specified prevalent object from the prevalent system.  Note
   * that constraint rules may cause an exception to be raised leaving the
   * object still persisted.
   *
   * @param object The prevalent object to delete from the prevalent system.
   * @return The modified prevalent object.  In particular the {@link
   *   com.sptci.prevayler.PrevalentObject#getObjectId()} will return
   *   <code>null</code> if the object is deleted.
   * @throws PrevalentException If errors are encountered while deleting the
   *   object.  More specific sub-classes such as {@link DeleteException}
   *   are thrown when configured rules are violated.
   */
  @SuppressWarnings( {"unchecked"} )
  public P delete( final P object ) throws PrevalentException
  {
    try
    {
      final Delete<P> delete = new Delete<P>( object );
      return (P) getPrevayler().execute( delete );
    }
    catch ( PrevalentException pex )
    {
      throw pex;
    }
    catch ( Throwable t )
    {
      throw new PrevalentException( "Error deleting object with oid: " +
          object.getObjectId() + " and type: " +
          object.getClass().getName(), t );
    }
  }

  /**
   * Return the total number of persistent instance of the specified type
   * in the prevalent system.  This method is typically used to implement
   * paginated views of the persistent data.
   *
   * @param type The type of object whose persistent instance count is to
   *   be retrieved.
   * @return The total number of persistent instances of the specified type.
   * @throws PrevalentException If errors are encountered while fetching the
   *   object count.
   */
  @SuppressWarnings( {"unchecked"} )
  public int count( final Class type ) throws PrevalentException
  {
    try
    {
      return (Integer) getPrevayler().execute( new Count( type ) );
    }
    catch ( PrevalentException pex )
    {
      throw pex;
    }
    catch ( Throwable t )
    {
      throw new PrevalentException(
          "Error retrieving object count for type: " + type, t );
    }
  }

  /**
   * Return the prevalent object identified by the object id specified.
   *
   * @param type The type of the persisted object which has the specified
   *   object id.
   * @param objectId The object id of the prevalent object.
   * @return The prevalent object if found or <code>null</code>.
   * @throws PrevalentException If errors are encountered while fetching the
   *   persistent object.
   */
  @SuppressWarnings( {"unchecked"} )
  public P fetch( final Class type, final Object objectId )
      throws PrevalentException
  {
    try
    {
      final Fetch fetch = new Fetch( type, objectId );
      return (P) getPrevayler().execute( fetch );
    }
    catch ( PrevalentException pex )
    {
      throw pex;
    }
    catch ( Throwable t )
    {
      throw new PrevalentException(
          "Error fetching prevalent object with objectId: " +
          objectId + " and type: " + type.getName(), t );
    }
  }

  /**
   * Return the collection of prevalent objects in the specified range.
   * Objects are returned in insertion order.
   *
   * @param type The type of the persisted objects which are to be fetched.
   * @param start The starting index (inclusive) of the range of objects
   *   to fetch.
   * @param end The ending index (exclusive) of the range of objects to
   *   fetch.
   * @return The collection of persistent objects.  Returns an empty
   *   collection if no objects are found in the specified range.
   * @throws PrevalentException If errors are encountered while retrieving
   *   the persisted objects.
   */
  @SuppressWarnings( {"unchecked"} )
  public Collection<P> fetch( final Class type, final long start,
      final long end ) throws PrevalentException
  {
    try
    {
      final FetchRange range = new FetchRange( type, start, end );
      return (Collection<P>) getPrevayler().execute( range );
    }
    catch ( PrevalentException pex )
    {
      throw pex;
    }
    catch ( Throwable t )
    {
      throw new PrevalentException(
          "Error fetching prevalent objects in range: " +
              start + "-" + end + " and type: " + type.getName(), t );
    }
  }

  /**
   * Retrieve the collection of prevelant objects of the specified type
   * that are indexed by the specified field and value.
   *
   * @param type The type of the persisted objects which are to be fetched.
   * @param field The name of the indexed field in the prevalent class.
   * @param value The value of the indexed field in the prevalent class.
   * @return The collection of persistent objects.  Returns an empty
   *   collection if no objects are found with the specified indexed value.
   * @throws PrevalentException If errors are encountered while retrieving
   *   the persisted objects.
   */
  @SuppressWarnings( {"unchecked"} )
  public Collection<P> fetch( final Class type, final String field,
      final Object value ) throws PrevalentException
  {
    try
    {
      final FetchByIndex index = new FetchByIndex( type, field, value );
      return (Collection<P>) getPrevayler().execute( index );
    }
    catch ( PrevalentException pex )
    {
      throw pex;
    }
    catch ( Throwable t )
    {
      throw new PrevalentException(
          "Error fetching prevalent objects for indexed field: " + field +
              " with value: " + value + " and type: " + type.getName(), t );
    }
  }

  /**
   * Retrieve the collection of prevelant objects of the specified type
   * that are indexed by the specified fields and values.  The results
   * represent a <code>union</code> of the prevalent objects matching each
   * parameter in the collection of parameters (an <code>or</code> query).
   *
   * @see #fetchByIndices
   * @param type The type of the persisted objects which are to be fetched.
   * @param parameters The map of field-name to value mappings to use
   *   to fetch instances.
   * @return The collection of persistent objects.  Returns an empty
   *   collection if no objects are found with the specified indexed value.
   * @throws PrevalentException If errors are encountered while retrieving
   *   the persisted objects.
   */
  public Collection<P> fetchUnion( final Class type,
      final Map<String,?> parameters ) throws PrevalentException
  {
    return fetchByIndices( type, parameters,
        FetchByIndices.AggregationType.UNION );
  }

  /**
   * Retrieve the collection of prevelant objects of the specified type
   * that are indexed by the specified fields and values.  The results
   * represent an <code>intersection</code> of the prevalent objects matching
   * each parameter in the collection of parameters (an <code>or</code> query).
   *
   * @see #fetchByIndices
   * @param type The type of the persisted objects which are to be fetched.
   * @param parameters The map of field-name to value mappings to use
   *   to fetch instances.
   * @return The collection of persistent objects.  Returns an empty
   *   collection if no objects are found with the specified indexed value.
   * @throws PrevalentException If errors are encountered while retrieving
   *   the persisted objects.
   */
  public Collection<P> fetchIntersection( final Class type,
      final Map<String,?> parameters ) throws PrevalentException
  {
    return fetchByIndices( type, parameters,
        FetchByIndices.AggregationType.INTERSECTION );
  }

  /** {@inheritDoc} */
  @SuppressWarnings( {"unchecked"} )
  public Collection<P> search( final Query query, final int count ) throws PrevalentException
  {
    try
    {
      final Search search = new Search( query, count );
      return (Collection<P>) getPrevayler().execute( search );
    }
    catch ( PrevalentException pex )
    {
      throw pex;
    }
    catch ( Throwable t )
    {
      throw new PrevalentException( "Error executing query: " + query, t );
    }
  }

  /** {@inheritDoc} */
  @SuppressWarnings( {"unchecked"} )
  public Collection<P> search( final Query query, final Filter filter,
      final int count ) throws PrevalentException
  {
    try
    {
      final Search search = new Search( query, filter, count );
      return (Collection<P>) getPrevayler().execute( search );
    }
    catch ( PrevalentException pex )
    {
      throw pex;
    }
    catch ( Throwable t )
    {
      throw new PrevalentException( "Error executing query: " + query +
          " with filter: " + filter, t );
    }
  }

  /** {@inheritDoc} */
  @SuppressWarnings( {"unchecked"} )
  public Collection<P> search( final Query query, final Filter filter,
      final int count, final Sort sort ) throws PrevalentException
  {
    try
    {
      final Search search = new Search( query, filter, count, sort );
      return (Collection<P>) getPrevayler().execute( search );
    }
    catch ( PrevalentException pex )
    {
      throw pex;
    }
    catch ( Throwable t )
    {
      throw new PrevalentException( "Error executing query: " + query +
          " with filter: " + filter + " and sort: " + sort, t );
    }
  }


  /**
   * Retrieve the collection of prevelant objects of the specified type
   * that are indexed by the specified fields and values.
   *
   * @param type The type of the persisted objects which are to be fetched.
   * @param parameters The map of field-name to value mappings to use
   *   to fetch instances.
   * @param resultType The type of aggregation to be used for the result set.
   * @return The collection of persistent objects.  Returns an empty
   *   collection if no objects are found with the specified indexed value.
   * @throws PrevalentException If errors are encountered while retrieving
   *   the persisted objects.
   */
  @SuppressWarnings( {"unchecked"} )
  protected Collection<P> fetchByIndices( final Class type,
      final Map<String,?> parameters,
      final FetchByIndices.AggregationType resultType )
    throws PrevalentException
  {
    try
    {
      final FetchByIndices indices =
          new FetchByIndices( type, parameters, resultType );
      return (Collection<P>) getPrevayler().execute( indices );
    }
    catch ( PrevalentException pex )
    {
      throw pex;
    }
    catch ( Throwable t )
    {
      throw new PrevalentException(
          "Error fetching prevalent objects of type: " + type.getName(), t );
    }
  }
}
