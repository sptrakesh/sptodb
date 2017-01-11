package com.sptci.prevayler.query;

import com.sptci.prevayler.PrevalentSystem;
import org.prevayler.Query;

import java.util.Date;

/**
 * A base class for all simple queries against the prevalent system.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans Pareil
 *   Technologies, Inc.</a></p>
 * @author Rakesh Vidyadharan 2008-05-27
 * @version $Id: AbstractQuery.java 22 2008-11-24 19:04:25Z sptrakesh $
 */
public abstract class AbstractQuery<T,S extends PrevalentSystem>
    implements Query
{
  /**
   * Implementation of the interface method.  Properly type-casts the
   * object parameter to a {@link PrevalentSystem} and hands over to
   * {@link #query( PrevalentSystem, Date )}.
   *
   * @param prevalentSystem The prevalent system against which the query
   *   is to be performed.
   * @param executionTime The timestamp for the query.
   * @return The result of executing the query.
   * @throws Exception If errors are encountered while executing the query.
   */
  @SuppressWarnings( {"unchecked"} )
  public Object query( final Object prevalentSystem,
      final Date executionTime ) throws Exception
  {
    return query( (S) prevalentSystem, executionTime  );
  }

  /**
   * Execute the query on the prevalent system and return the results.
   *
   * @param system The prevalent system that is to be acted upon.
   * @param timestamp The timestamp for the query.
   * @return The results of the query.
   * @throws Exception If errors are encountered while executing the query.
   */
  protected abstract T query( final S system,
      final Date timestamp  ) throws Exception;
}
