package com.sptci.prevayler.query;

import com.sptci.prevayler.PrevalentException;
import com.sptci.prevayler.PrevalentObject;
import com.sptci.prevayler.PrevalentSystem;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

import java.util.Collection;
import java.util.Date;

/**
 * The query for retrieving a collection of prevalent objects that match
 * the specified query and optional filter.  This query uses the full-text
 * indices to retrieve the matching prevalent objects.
 *
 * @see com.sptci.prevayler.PrevalentManager#search(org.apache.lucene.search.Query,
 *   org.apache.lucene.search.Filter, int, org.apache.lucene.search.Sort)
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans Pareil
 *   Technologies, Inc.</a></p>
 * @author Rakesh Vidyadharan 2008-05-27
 * @version $Id: Search.java 22 2008-11-24 19:04:25Z sptrakesh $
 */
public class Search<P extends Collection<PrevalentObject>, S extends PrevalentSystem>
    extends AbstractQuery<P,S>
{
  /** The query that is to be executed. */
  private final Query query;

  /** The optional filter clause to apply for the search results. */
  private final Filter filter;

  /** The maximum number of search results to return. */
  private final int count;

  /** The sort criteria to apply to the search results. */
  private final Sort sort;

  /**
   * Create a new instance of the query with the specified parameters.
   *
   * @param query The lucene query to execute.
   * @param count The maximum number of results.
   */
  public Search( final Query query, final int count )
  {
    this( query, null, count, null );
  }

  /**
   * Create a new instance of the query with the specified parameters.
   *
   * @param query The lucene query to execute.
   * @param filter The filter to apply to the results.
   * @param count The maximum number of results.
   */
  public Search( final Query query, final Filter filter, final int count )
  {
    this( query, filter, count, null );
  }

  /**
   * Create a new instance of the query with the specified parameters.  This
   * is the designated initialiser.
   *
   * @param query The lucene query to execute.
   * @param filter The filter to apply to the results.
   * @param count The maximum number of results.
   * @param sort The sort criteria for the results.
   */
  public Search( final Query query, final Filter filter, final int count,
      final Sort sort )
  {
    this.query = query;
    this.filter = filter;
    this.count = count;
    this.sort = sort;
  }

  /**
   * Execute the query on the prevalent system and return the prevalent
   *  object instances that match the specified query.
   *
   * @param system The prevalent system that is to be acted upon.
   * @param timestamp The timestamp for the query.
   * @return The collection of prevalent objects or an empty collection.
   * @throws com.sptci.prevayler.PrevalentException If errors are encountered
   *   while fetching the required prevalent object.
   */
  @SuppressWarnings( {"unchecked"} )
  protected P query( final S system, final Date timestamp )
      throws PrevalentException
  {
    return (P) system.search( query, filter, count, sort );
  }
}