package com.sptci.prevayler.query;

import com.sptci.prevayler.PrevalentException;
import com.sptci.prevayler.PrevalentObject;
import com.sptci.prevayler.PrevalentSystem;

import java.util.Collection;
import java.util.Date;

/**
 * A query used to fetch prevalent objects that are indexed by the specified
 * field.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans
 * Pareil Technologies, Inc.</a></p>
 *
 * @author Rakesh Vidyadharan 2008-07-13
 * @version $Id: FetchByIndex.java 22 2008-11-24 19:04:25Z sptrakesh $
 */
public class FetchByIndex<P extends Collection<PrevalentObject>, S extends PrevalentSystem>
  extends AbstractQuery<P,S>
{
  /** The type of the prevalent object that is to be queried. */
  private final Class type;

  /** The name of the indexed field in the prevalent objects to query. */
  private final String field;

  /** The value of the indexed field to use to query the system. */
  private final Object value;

  /**
   * Create a new instance of the query with the specified values.
   *
   * @param type The {@link #type} to use for the query.
   * @param field The {@link #field} to use for the query.
   * @param value The {@link #value} to use for the query.
   */
  public FetchByIndex( final Class type, final String field, final Object value )
  {
    this.type = type;
    this.field = field;
    this.value = value;
  }

  /**
   * Execute the query on the prevalent system and return the collection of
   *
   * @param system The prevalent system that is to be acted upon.
   * @param timestamp The timestamp for the query.
   * @return The collection of prevalent objects matching the specified
   *   indexed field.
   * @throws PrevalentException If errors are encountered while querying
   *   the system.
   */
  @Override
  @SuppressWarnings( {"unchecked"} )
  protected P query( final S system, final Date timestamp )
      throws PrevalentException
  {
    return (P) system.fetch( type, field, value );
  }
}
