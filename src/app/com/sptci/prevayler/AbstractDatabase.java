package com.sptci.prevayler;

import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * A base interface that defines the public interface exposed by the database
 * system.  This interface defines the common non-transactional features
 * supported by the system.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans
 * Pareil Technologies, Inc.</a></p>
 *
 * @author Rakesh 2008-11-23
 * @version $Id: AbstractDatabase.java 22 2008-11-24 19:04:25Z sptrakesh $
 */
public interface AbstractDatabase<P extends PrevalentObject> extends Serializable
{
  /**
   * Return the total number of instances of the specified type in the
   * prevalent system.
   *
   * @param cls The type of the prevalent object whose count is desired.
   * @return The total number of prevalent objects of the specified type.
   * @throws PrevalentException If errors are encountered while interacting
   *   with the prevalent system.
   */
  int count( Class cls ) throws PrevalentException;

  /**
   * Retrieve the prevalent object of type with object id.
   *
   * @param cls The type of the prevalent object.
   * @param oid The object id for the prevalent object to retrieve.
   * @return The prevalent object instance.  Returns <code>null</code> if no
   *   such object is stored in the prevalent system.
   * @throws PrevalentException If errors are encountered while reconstituting
   *   the prevalent object.
   */
  P fetch( Class cls, Object oid ) throws PrevalentException;

  /**
   * Fetch the prevalent objects in the specified range of data.  This
   * method supports display of paginated view of the prevalent objects of
   * the type specified.  Note that the objects are returned in insertion
   * order.
   *
   * @see com.sptci.prevayler.PrimaryStorage#get( long, long )
   * @param cls The type of prevalent object to retrive.
   * @param start The starting index (inclusive) from which to fetch the
   *   prevalent objects.
   * @param end The ending index (exclusive) to which to fetch the
   *   prevalent objects.
   * @return The collection of prevalent objects.  Returns an empty
   *   collection if there are no objects in the specified range.
   * @throws com.sptci.prevayler.PrevalentException If errors are encountered while fetching the
   *   objects.
   */
  Collection<P> fetch( Class cls, long start, long end )
      throws PrevalentException;

  /**
   * Fetch the prevalent object(s) of the specified <code>cls</code> type
   * which has the specified object as the value of the specified
   * field.
   *
   * <p><b>Note:</b> Only indexed fields are searched.  If the specified
   * field is not indexed, this method returns an empty collection.</p>
   *
   * @param cls The type of prevalent object to query for.
   * @param field The name of the field in the prevalent object using which
   *   the results are to be queried.
   * @param object The value of the field.
   * @return The collection of prevalent objects who have the specified
   *   object in the field specified.  Returns an empty collection if
   *   no results are found.
   * @throws com.sptci.prevayler.PrevalentException If errors are encountered while reconstituting
   *   the prevalent objects being returned.
   */
  Collection<P> fetch( Class cls, String field, Object object )
      throws PrevalentException;

  /**
   * Fetch the prevalent object(s) of the specified <code>cls</code> type
   * which has the specified indexed field values.  The results
   * contain a union of the matching objects.
   *
   * <p><b>Note:</b> Only indexed fields are searched.  If the specified
   * field(s) are not indexed, this method ignores those field(s).</p>
   *
   * @see #fetch( Class, String, Object )
   * @param cls The type of prevalent object to query for.
   * @param parameters The map of parameters to use to filter the
   *   prevalent instances.
   * @return The collection of prevalent objects that matches the specified
   *   parameters.
   * @throws com.sptci.prevayler.PrevalentException If errors are encountered while reconstituting
   *   the prevalent objects being returned.
   */
  Collection<P> fetchUnion( Class cls, Map<String,?> parameters )
      throws PrevalentException;

  /**
   * Fetch the prevalent object(s) of the specified <code>cls</code> type
   * which has the specified indexed field values.  The results
   * contain an intersection of the matching objects.
   *
   * <p><b>Note:</b> Only indexed fields are searched.  If the specified
   * field(s) are not indexed, this method ignores those field(s).</p>
   *
   * @see #fetch( Class, String, Object )
   * @param cls The type of prevalent object to query for.
   * @param parameters The map of parameters to use to filter the
   *   prevalent instances.
   * @return The collection of prevalent objects that matches the specified
   *   parameters.
   * @throws com.sptci.prevayler.PrevalentException If errors are encountered while reconstituting
   *   the prevalent objects being returned.
   */
  Collection<P> fetchIntersection( Class cls, Map<String,?> parameters )
      throws PrevalentException;

  /**
   * Execute the specified lucene query and return the collection of matching
   * prevalent objects.
   *
   * <p><b>Notes:</b></p>
   * <ul>
   * <li>We use built-in lucene analysers which apply a lower-case
   * filter to the input content.  Hence make sure that you specify only
   * lower-case search clauses in your query.</li>
   * <li>The results may be restricted to only instances of specific
   * prevalent classes by adding
   * <a href='http://docs.rakeshv.org/java/lucene/org/apache/lucene/index/Term.html'>Term</a>
   * clauses for the class.  The
   * <a href='http://docs.rakeshv.org/java/lucene/org/apache/lucene/document/Document.html'>Document</a>
   * instance for a prevalent object contains a {@code class}
   * <a href='http://docs.rakeshv.org/java/lucene/org/apache/lucene/document/Field.html'>Field</a>
   * which contains the fully qualified class name of the prevalent object.</li>
   * </ul>
   *
   * @param query The lucene query that is to be executed to find matching
   *   prevalent object instances.
   * @param filter The filter to apply to restrict the query results.
   * @param count The maximum number to top hits for the search to return.
   * @param sort The sort criteria to use for the results.
   * @return The collection of matching instances or an empty collection.
   * @throws com.sptci.prevayler.PrevalentException If errors are encountered while reconstituting
   *   the prevalent objects being returned.
   */
  Collection<P> search( Query query, Filter filter, int count, Sort sort )
      throws PrevalentException;
}
