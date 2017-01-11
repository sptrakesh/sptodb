package com.sptci.prevayler;

import java.util.Date;

/**
 * Abstract base class whose instances are stored in the prevalent system.
 * At present sptodb does not support persisting other types of classes.
 * This restriction was introduced since we need a {@link #objectId} field
 * in the class that can be managed by the prevalent system.  Making this
 * an interface would not guarantee us the availability of such a field.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans
 *   Pareil Technologies, Inc.</a></p>
 * @author Rakesh Vidyadharan 2008-05-22
 * @version $Id: PrevalentObject.java 22 2008-11-24 19:04:25Z sptrakesh $
 */
public abstract class PrevalentObject<K>
    implements java.io.Serializable, Cloneable, Comparable<PrevalentObject<K>>
{
  private static final long serialVersionUID = 1L;

  /** The object id field for the instance. */
  private K objectId;

  /**
   * The metadata for the persistent object.  This field will be maintained
   * by the {@link PrevalentSystem} when persistent operations on the object
   * are performed.
   */
  private MetaData _sptodbMetaData;

  /** Default constructor. Note that a no-arg constructor is mandatory. */
  protected PrevalentObject() {}

  /**
   * Create a new instance of the prevalent object with the specified object
   * id.  Use this form only when you wish to control the object id (using
   * custom object id such as {@link java.lang.String}.
   *
   * @param oid The {@link #objectId} value to use.
   */
  protected PrevalentObject( final K oid )
  {
    this.objectId = oid;
  }

  /**
   * Compare the specified object with this instance for equality.  Two
   * objects are considered equal if they have the same class and have the
   * same value of {@link #objectId}.  Sub-classes are strongly discouraged
   * from over-riding this method, since this method compares strictly based
   * on {@link #objectId} equality.  In case you need to over-ride this
   * method it is strongly recommended that you apply the additional rules
   * after having invoked this implementation.
   *
   * @param object The object to be compared for equality with this object.
   * @return Return <code>true</code> if the specified object is equivalent
   *   to this object.
   */
  @Override
  public boolean equals( final Object object )
  {
    if ( this == object ) return true;
    if ( object == null ) return false;
    boolean result = false;

    if ( ( getClass() == object.getClass() ) && ( objectId != null ) )
    {
      PrevalentObject po = (PrevalentObject) object;
      result = objectId.equals( po.getObjectId() );
    }

    return result;
  }

  /**
   * Default implementation of {@link java.lang.Object#hashCode}.  Over-ridden
   * to return the hash code based upon {@link #objectId}.  Similar to
   * {@link #equals}, sub-classes are discouraged from over-riding this
   * default implementation.
   *
   * @return The hash code for this object.
   */
  @Override
  public int hashCode()
  {
    int hash = 7;

    hash += ( 31 * 7 ) + getClass().getName().hashCode();
    hash += ( 31 * 7 ) + ( ( objectId == null ) ? 0 : objectId.hashCode() );

    return hash;
  }

  /**
   * Compare the specified prevalent object with this instance for ordering.
   * Returns a positive integer if the specified object is considered greater
   * than this instance, 0 if equal and negative is less.   Default
   * implementation compares based upon {@link #objectId}.  Sub-classes may
   * need to over-ride this method for special handling.
   *
   * @param prevalentObject The object to be compared with this instance.
   * @return A positive, 0, or negative number.
   */
  @SuppressWarnings( {"unchecked"} )
  public int compareTo( final PrevalentObject prevalentObject )
  {
    int result = 0;

    if ( ( objectId != null ) && ( objectId instanceof Comparable ) )
    {
      result = ( (Comparable) objectId ).compareTo(
          prevalentObject.getObjectId() );
    }

    return result;
  }

  /**
   * Return a string representation of this object.  The default
   * implementation just return the class name and {@link #objectId}.
   *
   * @return The string representation of this object.
   */
  @Override
  public String toString()
  {
    final StringBuilder builder = new StringBuilder( 64 );
    builder.append( getClass().getName() );
    builder.append( " objectId=" ).append( objectId );

    if ( _sptodbMetaData != null )
    {
      builder.append( " created=" ).append( getCreationDate() );
      builder.append( " updated=" ).append( getModificationDate() );
    }

    return builder.toString();
  }

  /**
   * Over-ridden to make publicly accessible.
   *
   * @return The cloned instance of this object.
   * @throws RuntimeException If the object cannot be cloned.
   */
  @Override
  public Object clone() throws RuntimeException
  {
    try
    {
      return super.clone();
    }
    catch ( CloneNotSupportedException cex )
    {
      throw new RuntimeException( cex );
    }
  }

  /**
   * Getter for property {@link #_sptodbMetaData}.
   *
   * @return Value for property {@link #_sptodbMetaData}.
   */
  final MetaData get_sptodbMetaData()
  {
    return _sptodbMetaData;
  }

  /**
   * Setter for property {@link #_sptodbMetaData}.
   *
   * @see #get_sptodbMetaData
   * @param metadata Value to set for property {@link #_sptodbMetaData}.
   */
  final void set_sptodbMetaData( final MetaData metadata )
  {
    this._sptodbMetaData = metadata;
  }

  /**
   * Return the object id for the business object.
   *
   * @return The {@link #objectId} value.
   */
  public final K getObjectId()
  {
    return objectId;
  }

  /**
   * Return the date at which this object was created.  Note that the date
   * will return different values until the object is initially persisted
   * to the prevalent system.
   *
   * @return The date at which the object was persisted.
   */
  public final Date getCreationDate()
  {
    return ( _sptodbMetaData == null ) ? new Date() :
        new Date( _sptodbMetaData.created );
  }

  /**
   * Return the date at which this object was last updated in the prevalent
   * system.  Note that the date returned will return different values until
   * the object has been persisted initially.
   *
   * @return The date at which the object was last updated.
   */
  public final Date getModificationDate()
  {
    return ( _sptodbMetaData == null ) ? new Date() :
        new Date( _sptodbMetaData.modified );
  }

  /**
   * Return a flag indicating whether this instance represents a persistent
   * instance or not.
   *
   * @return Return <code>true</code> if the instance is perstent in the
   *   prevalent system.
   */
  public final boolean isPersistent()
  {
    return ( _sptodbMetaData != null );
  }

  /**
   * Return the {@code objectId} value that may be used to retrieve the
   * prevalent instance from the specified string representation.  This is
   * mandated by the necessity for storing the {@code objectId} values as
   * {@link String} instances in the lucene index.
   *
   * <p><b>Note:</b> The string value of {@code objectId} stored in the
   * lucene index is the value returned by {@link Object#toString()}.  Your
   * primary key class must return a meaningful value to be able to
   * reconstruct the primary key instance.</p>
   *
   * @since Version 0.3
   * @param oid The string value that is to be converted to the proper {@code
   *   objectId} value.
   * @return The primary key instance.
   */
  public abstract K getObjectId( final String oid );
}
