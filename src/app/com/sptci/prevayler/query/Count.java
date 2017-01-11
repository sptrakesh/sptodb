package com.sptci.prevayler.query;

import com.sptci.prevayler.PrevalentManager;
import com.sptci.prevayler.PrevalentSystem;

import java.util.Date;

/**
 * The query for retrieving the total number of objects of the specified
 * type in the prevalent system.
 *
 * @see PrevalentManager#count(Class)
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans Pareil
 *   Technologies, Inc.</a></p>
 * @author Rakesh Vidyadharan 2008-05-27
 * @version $Id: Count.java 18 2008-07-20 03:35:47Z sptrakesh $
 */
public class Count<S extends PrevalentSystem> extends AbstractQuery<Integer,S>
{
  /** The type of object whose total count is required. */
  private final Class cls;

  /**
   * Create a new instance of the query for the specified prevalent object
   * type.
   *
   * @param cls The {@link #cls} value to use.
   */
   public Count( final Class cls )
   {
     this.cls = cls;
   }

  /**
   * Execute the query on the prevalent system and return the
   * total number of objects of {@link #cls} in the system.
   *
   * @param system The prevalent system that is to be acted upon.
   * @param timestamp The timestamp for the query.
   * @return The count of prevalent objects of {@link #cls} in the system.
   */
  @Override
  protected Integer query( final S system, final Date timestamp )
  {
    return system.count( cls );
  }
}
