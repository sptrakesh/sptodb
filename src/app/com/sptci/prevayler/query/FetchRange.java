package com.sptci.prevayler.query;

import com.sptci.prevayler.PrevalentException;
import com.sptci.prevayler.PrevalentObject;
import com.sptci.prevayler.PrevalentSystem;

import java.util.Collection;
import java.util.Date;

/**
 * The query used to retrieve prevalent objects of the specified type in
 * the range specified.  This is used to retrieve paginated view of the
 * prevalent objects stored in the prevalent system.
 *
 * @see com.sptci.prevayler.PrevalentManager#fetch(Class, long, long)
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans
 *   Pareil Technologies, Inc.</a></p>
 * @author Rakesh Vidyadharan 2008-05-27
 * @version $Id: FetchRange.java 22 2008-11-24 19:04:25Z sptrakesh $
 */
public class FetchRange<P extends PrevalentObject, S extends PrevalentSystem>
    extends AbstractQuery<Collection<P>,S>
{
  /** The type of prevalent objects to retrieve. */
  private final Class cls;

  /**
   * The starting index (inclusive) of the range from which to fetch
   * objects.
   */
  private final long start;

  /**
   * The ending index (exclusive) of the range to which to fetch
   * objects.
   */
  private final long end;

  /**
   * Create a new instance of the query for the specified parameters.
   * @param cls The {@link #cls} value to use.
   * @param start The {@link #start} value to use.
   * @param end The {@link #end} value to use.
   */
  public FetchRange( final Class cls, final long start, final long end )
  {
    this.cls = cls;
    this.start = start;
    this.end = end;
  }

  /**
   * Execute the query on the prevalent system and return the prevalent
   *  objects that fall in the specified range.
   *
   * @param system The prevalent system that is to be acted upon.
   * @param timestamp The timestamp for the query.
   * @return The collection of prevalent objects in the specified range.
   *   Return an empty collection if no objects are in the range.
   * @throws PrevalentException If errors are encountered while fetching
   *   the required prevalent objects.
   */
  @SuppressWarnings( {"unchecked"} )
  protected Collection<P> query( final S system, final Date timestamp )
      throws PrevalentException
  {
    return (Collection<P>) system.fetch( cls, start, end );
  }
}
