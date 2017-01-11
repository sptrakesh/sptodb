package com.sptci.prevayler.transaction;

import com.sptci.prevayler.PrevalentObject;

/**
 * The transaction used to update an existing prevalent object in the
 * prevalent system.  This is purely a convenience class that makes it
 * easier to update a prevalent object than directly using {@link
 * Transaction}.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans Pareil
 *   Technologies, Inc.</a></p>
 * @author Rakesh Vidyadharan 2008-05-24
 * @version $Id: Update.java 11 2008-06-30 21:33:41Z sptrakesh $
 */
public class Update<P extends PrevalentObject> extends Transaction<P>
{
  private static final long serialVersionUID = 1L;

  /**
   * The name of the method in {@link com.sptci.prevayler.PrevalentSystem}
   * that is invoked by this transaction.
   */
  private static final String METHOD = "update";

  /**
   * Create a new instance of the transaction using the specified prevalent
   * object.
   *
   * @param object The prevalent object to be updated in the system.
   */
  public Update( final P object )
  {
    super( METHOD, new Parameter( PrevalentObject.class, object ) );
  }
}
