package com.sptci.prevayler;

import java.io.Serializable;

/**
 * A simple value object used to represent the class of a prevalent
 * object and its objectId.  Instances of this class are used to represent
 * indexed prevalent objects.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans
 * Pareil Technologies, Inc.</a></p>
 * @author Rakesh Vidyadharan 2008-07-10
 * @version $Id: IndexedObject.java 4379 2008-07-11 02:21:18Z rakesh $
 */
public class IndexedObject implements Serializable
{
  private static final long serialVersionUID = 1l;

  /** The field that stores the class of the prevalent object. */
  public final Class type;

  /** The field that stores the objectId of the prevalent object. */
  public final Object objectId;

  /**
   * Create a new instance of the value object with the specified values
   * for the instance members.
   *
   * @param type The {@link IndexedObject#type}
   *   value to use.
   * @param objectId The {@link IndexedObject#objectId}
   *   value to use.
   */
  protected IndexedObject( final Class type, final Object objectId )
  {
    this.type = type;
    this.objectId = objectId;
  }

  /**
   * Getter for property {@link #type}.
   *
   * @return Value for property {@link #type}.
   */
  public Class getType()
  {
    return type;
  }

  /**
   * Getter for property {@link #objectId}.
   *
   * @return Value for property {@link #objectId}.
   */
  public Object getObjectId()
  {
    return objectId;
  }

  /**
   * Compare the specified object with this instance for equality.  The
   * specified object is equal if its is of the same type and have
   * equivalent members.
   *
   * @param object The object that is to be compared for equality.
   * @return Returns <code>true</code> if the object is of the same type
   *   and has equivalent fields.
   */
  public boolean equals( final Object object )
  {
    if ( this == object ) return true;
    if ( object == null || getClass() != object.getClass() ) return false;

    IndexedObject that = (IndexedObject) object;

    if ( objectId != null ? !objectId.equals( that.objectId ) : that.objectId != null )
    {
      return false;
    }
    if ( type != null ? !type.equals( that.type ) : that.type != null )
    {
      return false;
    }

    return true;
  }

  /**
   * Return a hash code for this instance.  Computes the hash code based
   * upon the hash codes for the fields.
   *
   * @return The hash code for this object.
   */
  public int hashCode()
  {
    int result;
    result = ( type != null ? type.hashCode() : 0 );
    result = 31 * result + ( objectId != null ? objectId.hashCode() : 0 );
    return result;
  }
}
