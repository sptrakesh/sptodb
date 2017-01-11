package com.sptci.prevayler.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to configure all the foreign key fields in a prevalent
 * object in a single block at the class level.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans Pareil Technologies, Inc.</a></p>
 * @author Rakesh Vidyadharan 2008-05-23
 * @version $Id: ForeignKeys.java 11 2008-06-30 21:33:41Z sptrakesh $
 */
@Documented
@Inherited
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface ForeignKeys
{
  /**
   * The array of foreign key annotations for the various fields in the
   * prevalent object.
   *
   * @return The array of foreign key annotations.
   */
  ForeignKey[] value();
}
