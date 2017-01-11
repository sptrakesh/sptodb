package com.sptci.prevayler.transaction;

import com.sptci.prevayler.PrevalentObject;

/**
 * The transaction object for deleting a prevalent object from the prevalent
 * system.  This is purely a convenience class that makes invocation easier
 * than directly using {@link Transaction}.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans Pareil
 *   Technologies, Inc.</a></p>
 * @author Rakesh Vidyadharan 2008-05-24
 * @version $Id: Delete.java 4345 2008-06-30 21:22:03Z rakesh $
 */
public class Delete<P extends PrevalentObject> extends Transaction<P>
{
  private static final long serialVersionUID = 1l;

  /**
   * The name of the method on {@link com.sptci.prevayler.PrevalentSystem}
   * that is invoked.
   */
  private static final String METHOD = "delete";

  /**
   * Create a new instance of the transaction using the specified prevalent
   * object.
   *
   * @param object The prevalent object to be deleted from the system.
   */
  public Delete( final P object )
  {
    super( METHOD, new Parameter( PrevalentObject.class, object ) );
  }
}
