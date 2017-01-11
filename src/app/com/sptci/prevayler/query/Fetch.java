package com.sptci.prevayler.query;

import com.sptci.prevayler.PrevalentException;
import com.sptci.prevayler.PrevalentObject;
import com.sptci.prevayler.PrevalentSystem;

import java.util.Date;

/**
 * The query for retrieving a prevalent object from the system identified
 * by its {@link com.sptci.prevayler.PrevalentObject#objectId} field.
 *
 * @see com.sptci.prevayler.PrevalentManager#fetch(Class, Object)
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans Pareil
 *   Technologies, Inc.</a></p>
 * @author Rakesh Vidyadharan 2008-05-27
 * @version $Id: Fetch.java 22 2008-11-24 19:04:25Z sptrakesh $
 */
public class Fetch<P extends PrevalentObject, S extends PrevalentSystem>
    extends AbstractQuery<P,S>
{
  /** The type of object that is to be retrieved. */
  private final Class cls;

  /**
   * The {@link com.sptci.prevayler.PrevalentObject#objectId} to use to
   * fetch the prevalent object instance.
   */
  private final Object objectId;

  /**
   * Create a new instance of the query for fetching a prevalent object
   * the specified object id.
   *
   * @param cls The {@link #cls} to use.
   * @param objectId The {@link #objectId} to use.
   */
  public Fetch( final Class cls, final Object objectId )
  {
    this.cls = cls;
    this.objectId = objectId;
  }

  /**
   * Execute the query on the prevalent system and return the prevalent
   *  object uniquely identified by the specified {@link #objectId}.
   *
   * @param system The prevalent system that is to be acted upon.
   * @param timestamp The timestamp for the query.
   * @return The required prevalent object or <code>null</code> if no
   *   object with the specified {@link #objectId} exists in the prevalent
   *   system.
   * @throws PrevalentException If errors are encountered while fetching
   *   the required prevalent object.
   */
  @SuppressWarnings( {"unchecked"} )
  protected P query( final S system, final Date timestamp )
      throws PrevalentException
  {
    return (P) system.fetch( cls, objectId );
  }
}
