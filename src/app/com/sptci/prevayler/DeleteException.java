package com.sptci.prevayler;

/**
 * An exception used to indicate that a prevalent object cannot be deleted
 * from the system since other prevalent objects hold references to it.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans Pareil
 * &nbsp;&nbsp;Technologies, Inc.</a></p>
 * @author Rakesh Vidyadharan 2008-05-28
 * @version $Id: DeleteException.java 4345 2008-06-30 21:22:03Z rakesh $
 */
public class DeleteException extends ConstraintException
{
  private static final long serialVersionUID = 1l;

  /** The pattern for the message to display for the exception. */
  private static final String PATTERN = "Cannot delete object of type: " +
      "$TYPE$ with objectId: " +
      "$OBJECTID$ due to references from other objects.";

  /**
   * Create a new instance of the exception for attempting to delete the
   * specified prevalent object.
   *
   * @param object The prevalent object that was to be deleted.
   */
  public DeleteException( final PrevalentObject object )
  {
    super( PATTERN.replace( "$TYPE$", object.getClass().getName() ).
        replace( "$OBJECTID$", object.getObjectId().toString() ) );
  }
}
