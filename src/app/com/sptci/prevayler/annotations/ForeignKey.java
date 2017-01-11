package com.sptci.prevayler.annotations;

import java.lang.annotation.*;

/**
 * Annotation used to indicate a foreign key (direct reference to another
 * prevalent object).  Foreign keys are automatically indexed, hence it
 * is not necessary to declare single field indices for fields that are also
 * marked as foreign keys.  Delete actions are also configured using this
 * annotation.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans Pareil Technologies, Inc.</a></p>
 * @author Rakesh Vidyadharan 2008-05-23
 * @version $Id: ForeignKey.java 11 2008-06-30 21:33:41Z sptrakesh $
 */
@Documented
@Inherited
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.TYPE, ElementType.FIELD } )
public @interface ForeignKey
{
  /** The default value for {@link #member} indicating a null value. */
  static String NULL = "";

  /** Enumeration for the delete actions supported. */
  enum DeleteAction { CASCADE, NULL, EXCEPTION }

  /**
   * The name of the field in the prevalent object.  This is necessary only
   * when the prevalent class is annotated.  Defaults to {@link #NULL}.
   *
   * @return The name of the field that is a foreign key.
   */
  String member() default NULL;

  /**
   * Indicate whether the referenced prevalent object should be unique within
   * the extent of the prevalent object being annotated.  Defaults to
   * <code>false</code>.
   *
   * @return Return <code>true</code> if the referenced prevalent object
   *   should be unique.
   */
  boolean unique() default false;

  /**
   * The action to perform when the referenced prevalent object is deleted
   * from the prevalent system.  Defaults to {@link DeleteAction#EXCEPTION}.
   *
   * @return The delete action to apply.
   */
  DeleteAction deleteAction() default DeleteAction.EXCEPTION;

  /**
   * The fully qualified name of the prevalent object stored in collection
   * fields.  This is necessary only for collection fields since it is
   * difficult to infer the type of objects stored in them.
   *
   * @return The fully qualified class name stored in the collection.
   */
  String collectionEntry() default NULL;
}
