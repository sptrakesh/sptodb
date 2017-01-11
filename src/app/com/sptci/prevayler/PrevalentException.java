package com.sptci.prevayler;

/**
 * A custom exception used to indicate errors while interacting with the
 * prevalent system.
 *
 * <p>Copyright 2008, <a href='http://sptci.com/' target='_top'>Sans Pareil Technologies, Inc.</a></p>
 * @author Rakesh Vidyadharan 2008-05-22
 * @version $Id: PrevalentException.java 4345 2008-06-30 21:22:03Z rakesh $
 */
public class PrevalentException extends Exception
{
  private static final long serialVersionUID = 1L;

  /**
   * Default constructor.  Create a new exception with a <code>null</code>
   * message.
   */
  public PrevalentException()
  {
    super();
  }

  /**
   * Create a new exception with the specified message.
   *
   * @param message The message that describes the problem.
   */
  public PrevalentException( final String message )
  {
    super( message );
  }

  /**
   * Create a new exception with instance of {@link java.lang.Throwable}
   * that caused the problem.
   *
   * @param throwable The exception that caused this instance of the exception
   *   to be thrown.
   */
  public PrevalentException( final Throwable throwable )
  {
    super( throwable );
  }

  /**
   * Create a new exception with the specified message and instance of
   * {@link java.lang.Throwable} that caused the problem.
   *
   * @param message The message that describes the problem.
   * @param throwable The exception that caused this instance of the exception
   *   to be thrown.
   */
  public PrevalentException( final String message, final Throwable throwable )
  {
    super( message, throwable );
  }
}
