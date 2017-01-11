package com.sptci.prevayler.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for class fields that are to be indexed for full-text search.
 * When specified at the class level, it is possible to specify a number of
 * fields that are to be indexed as a combination making for easier search
 * queries.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans
 * Pareil Technologies, Inc.</a></p>
 * @author Rakesh 2008-11-12
 * @since Release 0.3.0
 * @version $Id: Searchable.java 22 2008-11-24 19:04:25Z sptrakesh $
 */
@Documented
@Inherited
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.TYPE, ElementType.FIELD } )
public @interface Searchable
{
  /** The default value for {@link #members} indicating a null value. */
  static String NULL = "";

  /**
   * The name of the field within the lucene document that is represented
   * by this index.  Specify only for composite indices (specified at class
   * level.  When annotated at the field level, the name of the field is
   * taken as the indexed field name. If not specified a name that is the
   * concatenation of the field names in {@link #members} will be used.
   * Using the default value will make it harder to compile queries, so it
   * is recommended that you specify this value as well.
   *
   * @return The name of the indexed field.
   */
  String name() default NULL;

  /**
   * The name of the fields in the prevalent object to index.  This need only
   * be specified when specified at the class level.  Multiple fields may be
   * specified to indicate a composite search index (tokenised and stored
   * as a common index for easy search). Defaults to {@link #NULL}.
   *
   * @return The name of the fields.
   */
  String[] members() default NULL;
}
