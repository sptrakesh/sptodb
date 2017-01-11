package com.sptci.prevayler.transaction;

import com.sptci.ReflectionUtility;
import com.sptci.prevayler.PrevalentException;
import org.prevayler.TransactionWithQuery;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;

/**
 * A general purpose transaction used to execute transactional methods on
 * the prevalent system.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com' target='_top'>Sans
 *   Pareil Technologies, Inc.</a></p>
 * @author Rakesh Vidyadharan 2008-05-28
 * @version $Id: Transaction.java 4345 2008-06-30 21:22:03Z rakesh $
 */
public class Transaction<P> implements TransactionWithQuery
{
  private static final long serialVersionUID = 1l;

  /** The name of the transactional method being invoked. */
  private final String method;

  /** The types of the parameters for the transactional method. */
  private final Class[] types;

  /** The values for {@link #types} to specify in the method invocation. */
  private final Object[] values;

  /**
   * Create a new instance of the transaction with the specified values.
   *
   * @param method The {@link #method} name to use.
   * @param parameter The parameter for the {@link #method}.
   */
  public Transaction( final String method, final Parameter parameter )
  {
    this.method = method;
    types = new Class[2];
    types[0] = parameter.getType();

    values = new Object[2];
    values[0] = parameter.getValue();
  }

  /**
   * Create a new instance of the transaction with the specified values.
   *
   * @param method The {@link #method} name to use.
   * @param parameters The parameters for the {@link #method}.
   */
  public Transaction( final String method, final Collection<Parameter> parameters )
  {
    this.method = method;
    types = new Class[ parameters.size() + 1 ];
    values = new Object[ parameters.size() + 1 ];

    int index = 0;
    for ( Parameter parameter : parameters )
    {
      types[index] = parameter.getType();
      values[index++] = parameter.getValue();
    }
  }

  /**
   * Create a new instance of the transaction with the specified values.
   *
   * @param method The {@link #method} name to use.
   * @param parameters The array of parameters for the {@link #method}.
   */
  public Transaction( final String method, final Parameter[] parameters )
  {
    this.method = method;
    types = new Class[ parameters.length + 1 ];
    values = new Object[ parameters.length + 1 ];

    int index = 0;
    for ( Parameter parameter : parameters )
    {
      types[index] = parameter.getType();
      values[index++] = parameter.getValue();
    }
  }

  /**
   * Implementation of the interface method.  Invokes the {@link #method}
   * on the prevalent system with the specified {@link #values}.
   *
   * @param prevalentSystem The prevalent system on which the transaction
   *   is to be performed and the query executed.
   * @param executionTime The timestamp for the transaction.
   * @return The results of exeecuting the transaction.
   * @throws PrevalentException Usually thrown when the prevalent system
   *   traps errors that are checked by the system.
   * @throws Exception If unknown errors are encountered while executing the
   *   transaction or query.
   */
  @SuppressWarnings( value = "unchecked" )
  public P executeAndQuery( final Object prevalentSystem,
      final Date executionTime ) throws Exception
  {
    P result;

    try
    {
      types[ types.length - 1 ] = Date.class;
      values[ values.length - 1 ] = executionTime;

      final Method m =
	  ReflectionUtility.fetchMethod( prevalentSystem, method, types );
      result = (P) m.invoke( prevalentSystem, values );
    }
    catch ( Exception ex )
    {
      if ( ex instanceof InvocationTargetException )
      {
	final Throwable cause = ex.getCause();
	if ( cause instanceof PrevalentException )
	{
	  throw (PrevalentException) cause;
	}
	else
	{
	  throw new PrevalentException( cause );
	}
      }

      throw ex;
    }

    return result;
  }

  /**
   * A mapping object used to capture the class type and instance of a
   * parameter to a method defined on the prevalent system.
   */
  public static class Parameter implements Serializable
  {
    private static final long serialVersionUID = 1l;

    /** The class of the parameter. */
    private final Class type;

    /** The instance of the parameter. */
    private final Object value;

    /**
     * Craete a new instance of the parameter with the specified values.
     *
     * @param type The {@link #type} to use.
     * @param value The {@link #value} to use.
     */
    public Parameter( final Class type, final Object value )
    {
      this.type = type;
      this.value = value;
    }

    /**
     * Create a new instance using the specified parameter value.  The
     * method declaration should use the class of the specified value and
     * not a super-class.
     *
     * @param value The {@link #value} to use.  The {@link #type} is set
     *   from {@link java.lang.Object#getClass} method.
     */
    public Parameter( final Object value )
    {
      this( value.getClass(), value );
    }

    /**
     * Return the {@link #type} field.
     *
     * @return The reference to {@link #type}.
     */
    public Class getType()
    {
      return type;
    }

    /**
     * Return the {@link #value} field.
     *
     * @return The reference to {@link #value}.
     */
    public Object getValue()
    {
      return value;
    }
  }
}