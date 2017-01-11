package com.sptci.prevayler.query;

import com.sptci.prevayler.PrevalentException;
import com.sptci.prevayler.PrevalentObject;
import com.sptci.prevayler.PrevalentSystem;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * A query used to fetch prevalent instances by the specified indexed fields.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans
 * Pareil Technologies, Inc.</a></p>
 *
 * @author Rakesh Vidyadharan 2008-07-19
 * @version $Id: FetchByIndices.java 22 2008-11-24 19:04:25Z sptrakesh $
 */
public class FetchByIndices<P extends Collection<PrevalentObject>, S extends PrevalentSystem>
  extends AbstractQuery<P,S>
{
  /**
   * An enumeration used to indicate whether the query results should
   * represent a <code>union</code> or <code>intersection</code>.
   */
  public enum AggregationType { UNION, INTERSECTION }

  /** The type of the prevalent object that is to be queried. */
  private final Class type;

  /** The map of parameters to use to filter the prevalent instances. */
  private final Map<String,?> parameters;

  /** The aggregation type to use for the results of the query. */
  private final AggregationType resultType;

  /**
   * Create a new instance of the query with the specified values.
   *
   * @param type The {@link #type} to use for the query.
   * @param parameters The map of fields in the prevalent class to use
   *   to fetch matching objects.
   * @param resultType The aggregation type to use for the results.
   */
  public FetchByIndices( final Class type, final Map<String,?> parameters,
      final AggregationType resultType )
  {
    this.type = type;
    this.parameters = parameters;
    this.resultType = resultType;
  }

  /**
   * Execute the query on the prevalent system and return the collection of
   *
   * @see PrevalentSystem#fetchUnion(Class, java.util.Map)
   * @see PrevalentSystem#fetchIntersection(Class, java.util.Map)
   * @param system The prevalent system that is to be acted upon.
   * @param timestamp The timestamp for the query.
   * @return The collection of prevalent objects matching the specified
   *   indexed field.
   * @throws com.sptci.prevayler.PrevalentException If errors are encountered while querying
   *   the system.
   */
  @Override
  @SuppressWarnings( {"unchecked"} )
  protected P query( final S system, final Date timestamp )
      throws PrevalentException
  {
    switch ( resultType )
    {
      case UNION:
        return (P) system.fetchUnion( type, parameters );
      case INTERSECTION:
        return (P) system.fetchIntersection( type, parameters );
    }

    return null;
  }
}