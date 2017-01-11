package com.sptci.prevayler.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to indicate that a field in a prevalent object is to be
 * indexed.  Note that this annotation is meant for indexing fields that are
 * not references to other prevalent object(s).  Use {@link ForeignKey} for
 * fields that are references to other prevalent objects.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans Pareil Technologies, Inc.</a></p>
 * @author Rakesh Vidyadharan 2008-05-22
 * @version $Id: Index.java 11 2008-06-30 21:33:41Z sptrakesh $
 */
@Documented
@Inherited
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.TYPE, ElementType.FIELD } )
public @interface Index
{
  /** The default value for {@link #members} indicating a null value. */
  static String NULL = "";

  /**
   * The name of the fields in the prevalent object to index.  This need only
   * be specified when specified at the class level.  Multiple fields may be
   * specified to indicate a composite index. Defaults to {@link #NULL}.
   *
   * @return The name of the fields.
   */
  String[] members() default NULL;

  /**
   * An optional flag used to indicate that the index represents a unique
   * index.  Defaults to <code>false</code>.
   *
   * @return The flag indicating whether the index is unique or not.
   */
  boolean unique() default false;
}
