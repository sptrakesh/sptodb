package com.sptci.prevayler;

import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;

import java.util.Collection;

/**
 * An interface that defines the transactional features exposed by the
 * higher level database API.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans
 * Pareil Technologies, Inc.</a></p>
 *
 * @author Rakesh 2008-11-23
 * @version $Id: Database.java 22 2008-11-24 19:04:25Z sptrakesh $
 */
public interface Database<P extends PrevalentObject> extends AbstractDatabase<P>
{
  /**
   * Save the specified prevalent object to the prevalent system.  Objects
   * not already persistent are added to the system, while already persistent
   * objects are updated.
   *
   * @param object The prevalent object to be saved in the system.
   * @return The potentially modified prevalent object.
   * @throws PrevalentException If errors are encountered while
   *   adding/updating the object.  Errors thrown could indicate constraint
   *   violations.
   */
  P save( final P object ) throws PrevalentException;

  /**
   * Delete the specified prevalent object from the prevalent system.
   * Processes cascading delete rules if so configured.
   *
   * @param object The prevalent object to delete.
   * @return The deleted object with potential modifications.
   * @throws com.sptci.prevayler.PrevalentException If errors are encountered
   *   while deleting the prevalent object.
   */
  P delete( P object ) throws PrevalentException;

  /**
   * Execute the specified lucene query and return the collection of matching
   * prevalent objects.
   * @param query The lucene query that is to be executed to find matching
   *   prevalent object instances.
   * @param count The maximum number to top hits for the search to return.
   * @return The collection of matching instances or an empty collection.
   * @throws com.sptci.prevayler.PrevalentException If errors are encountered
   *   while reconstituting the prevalent objects being returned.
   */
  Collection<P> search( Query query, int count ) throws PrevalentException;

  /**
   * Execute the specified lucene query and return the collection of matching
   * prevalent objects.
   * @param query The lucene query that is to be executed to find matching
   *   prevalent object instances.
   * @param filter The filter to apply to restrict the query results.
   * @param count The maximum number to top hits for the search to return.
   * @return The collection of matching instances or an empty collection.
   * @throws com.sptci.prevayler.PrevalentException If errors are encountered
   *   while reconstituting the prevalent objects being returned.
   */
  Collection<P> search( Query query, Filter filter, int count )
      throws PrevalentException;
}
