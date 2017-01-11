package com.sptci.prevayler.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for specifying all the full-text search indices for a prevalent
 * class.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans
 * Pareil Technologies, Inc.</a></p>
 * @author Rakesh 2008-11-12
 * @since Release 0.3.0
 * @version $Id: Searchables.java 22 2008-11-24 19:04:25Z sptrakesh $
 */
@Documented
@Inherited
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface Searchables
{
  /**
   * The array of {@link Searchable} annotations that represent all the full
   * text search index definitions for the prevalent object.
   *
   * @return The array of search index definitions.
   */
  Searchable[] value();
}
