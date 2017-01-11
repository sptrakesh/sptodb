package com.sptci.prevayler.transaction;

import com.sptci.prevayler.PrevalentObject;

/**
 * A transaction for adding a new prevalent object instance to the prevalent
 * system.  This transaction merely makes it more convenient to add a new
 * prevalent object than directly invoking {@link Transaction}.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans Pareil
 *   Technologies, Inc.</a></p>
 * @author Rakesh Vidyadharan 2008-05-24
 * @version $Id: Add.java 11 2008-06-30 21:33:41Z sptrakesh $
 */
public class Add<P extends PrevalentObject> extends Transaction<P>
{
  private static final long serialVersionUID = 1L;

  /**
   * The name of the method in {@link com.sptci.prevayler.PrevalentSystem}
   * that is invoked in this transaction.
   */
  private static final String METHOD = "save";

  /**
   * Create a new instance of the transaction using the specified prevalent
   * object.
   *
   * @param object The new prevalent object to add to the system.
   */
  public Add( final P object )
  {
    super( METHOD, new Parameter( PrevalentObject.class, object ) );
  }
}