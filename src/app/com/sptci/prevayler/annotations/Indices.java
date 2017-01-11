package com.sptci.prevayler.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for specifying all the indexed fields in a prevalent class.
 * Note that this annotation should not be used (it won't cause any errors)
 * on fields that are references to other prevalent objects.  It is best to
 * use the {@link ForeignKeys} annotation for references to prevalent objects.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans Pareil Technologies, Inc.</a></p
 * @author User: rakesh 2008-05-22
 * @version $Id: Indices.java 11 2008-06-30 21:33:41Z sptrakesh $
 */
@Documented
@Inherited
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface Indices
{
  /**
   * The array of {@link Index} annotations that represent all the index
   * definitions for the prevalent object.
   *
   * @return The array of index definitions.
   */
  Index[] value();
}
