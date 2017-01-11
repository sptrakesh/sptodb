package com.sptci.prevayler;

import java.io.Serializable;

/**
 * A value object used to store persistent metadata for a {@link
 * PrevalentObject}.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans
 *   Pareil Technologies, Inc.</a></p>
 *
 * @author Rakesh Vidyadharan 2008-06-28
 * @version $Id: MetaData.java 4345 2008-06-30 21:22:03Z rakesh $
 */
class MetaData implements Serializable
{
  private static final long serialVersionUID = 1l;

  /** A field used to maintain the persistent state of an object. */
  final boolean persisted;

  /** A field used to store the date-time at which an object was created. */
  final long created;

  /** A field used to store the date-time at which an object was last saved. */
  long modified;

  /**
   * Create a new meta data instance.  Sets the field values to the specified
   * values.
   *
   * @param time The datetime at which the transaction was created.
   */
  MetaData( final long time )
  {
    persisted = true;
    created = time;
    modified = time;
  }
}
