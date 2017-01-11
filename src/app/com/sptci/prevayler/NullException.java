package com.sptci.prevayler;

/**
 * A custom exception used to indicate that a field marked as {@link
 * com.sptci.prevayler.annotations.NotNull} is <code>null</code>.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans Pareil Technologies, Inc.</a></p>
 * @author Rakesh 2008-05-23
 * @version $Id: NullException.java 4345 2008-06-30 21:22:03Z rakesh $
 */
public class NullException extends ConstraintException
{
  private static final long serialVersionUID = 1L;

  /** The pattern for displaying constraint violation. */
  private static final String FIELD_PATTERN =
      "NULL value on field: $FIELD$ in class: $CLASS$ prohibited!";

  /**
   * Create a new instance of the exception with the specified message.
   *
   * @param message The message to associate with the exception.
   */
  public NullException( final String message )
  {
    super( message );
  }

  /**
   * Create a new instance of the exception using {@link #FIELD_PATTERN} to
   * display the message for unique constraint on specified fields being
   * violated.
   *
   * @param object The prevalent object for which the violation was raised.
   * @param field The name of the field whose not-null constraint was
   *   violated.
   */
  public NullException( final PrevalentObject object, final String field )
  {
    super( FIELD_PATTERN.replace( "$CLASS$", object.getClass().getName() ).
        replace( "$FIELD$", field ) );
  }
}
