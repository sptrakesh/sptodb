package com.sptci.prevayler;

import java.util.Date;

/**
 * An interface that captures the interactions supported by the object
 * database system.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans
 * Pareil Technologies, Inc.</a></p>
 *
 * @author Rakesh 2008-11-19
 * @version $Id: DatabaseSystem.java 22 2008-11-24 19:04:25Z sptrakesh $
 */
public interface DatabaseSystem extends AbstractDatabase<PrevalentObject>
{
  /**
   * Save the specified prevalent object to the prevalent system.  Objects
   * not already persistent are added to the system, while already persistent
   * objects are updated.
   *
   * @param object The prevalent object to be saved in the system.
   * @param executionTime The time at which the transaction was executed.
   * @return The potentially modified prevalent object.
   * @throws PrevalentException If errors are encountered while
   *   adding/updating the object.  Errors thrown could indicate constraint
   *   violations.
   */
  PrevalentObject save( final PrevalentObject object,
      final Date executionTime ) throws PrevalentException;

  /**
   * Delete the specified prevalent object from the prevalent system.
   * Processes cascading delete rules if so configured.
   *
   * @param object The prevalent object to delete.
   * @param executionTime The datetime at which the transaction was executed.
   * @return The deleted object with potential modifications.
   * @throws com.sptci.prevayler.PrevalentException If errors are encountered while deleting the
   *   prevalent object.
   */
  PrevalentObject delete( PrevalentObject object, Date executionTime )
      throws PrevalentException;
}
