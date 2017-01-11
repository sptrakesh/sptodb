package com.sptci.prevayler;

/**
 * An exception used to indicate that a constraint violations has been
 * encountered while persisting the prevalent object.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans
 *   Pareil Technologies, Inc.</a></p>
 * @author Rakesh Vidyadharan 2008-05-22
 * @version $Id: ConstraintException.java 4345 2008-06-30 21:22:03Z rakesh $
 */
public class ConstraintException extends PrevalentException
{
  private static final long serialVersionUID = 1L;

  /** The pattern for displaying constraint violation. */
  private static final String FIELD_PATTERN =
      "Unique constraint on class: $CLASS$ field(s): $FIELD$ violated!";

  /** The pattern used to generate the message for a oid violation. */
  private static final String OID_PATTERN =
      "Object of type: $CLASS$ already exists with objectId: $OID$!";

  /** Default constructor.  Cannot be instantiated. */
  protected ConstraintException() {}

  /**
   * Create a new instance of the exception with the specified message.
   *
   * @param message The message to associate with the exception.
   */
  public ConstraintException( final String message )
  {
    super( message );
  }

  /**
   * Create a new instance of the exception using {@link #FIELD_PATTERN} to
   * display the message for unique constraint on specified fields being
   * violated.
   *
   * @param object The prevalent object for which the violation was raised.
   * @param fields A string representing the fields on which the constraint
   *   was defined.
   */
  public ConstraintException( final PrevalentObject object,
      final String fields )
  {
    super( FIELD_PATTERN.replace( "$CLASS$", object.getClass().getName() ).
        replace( "$FIELD$", fields ) );
  }

  /**
   * Create a new instance of the exception using {@link #OID_PATTERN} to
   * display the message for the unique object id constraint being violated.
   *
   * @param object The prevalent object for which the violation was raised.
   */
  public ConstraintException( final PrevalentObject object )
  {
    super( OID_PATTERN.replace( "$CLASS$", object.getClass().getName() ).
        replace( "$OID$", object.getObjectId().toString() ) );
  }
}
