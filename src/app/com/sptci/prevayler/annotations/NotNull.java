package com.sptci.prevayler.annotations;

import java.lang.annotation.*;

/**
 * An annotation used to indicate that a field in a prevalent object cannot
 * be null.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans Pareil Technologies, Inc.</a></p>
 * @author Rakesh Vidyadharan 2008-05-22
 * @version $Id: NotNull.java 11 2008-06-30 21:33:41Z sptrakesh $
 */
@Documented
@Inherited
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.TYPE, ElementType.FIELD } )
public @interface NotNull
{
  /** A constant to present <code>null</code> or no members. */
  static final String NULL = "";

  /**
   * The names of the fields for which the constraint is to be applied.  This
   * is required when annotating a class as opposed to the appropriate fields.
   *
   * @return The name of the field in the prevalent object.
   */
  String[] members() default NULL;
}
