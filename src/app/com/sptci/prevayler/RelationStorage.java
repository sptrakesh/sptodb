package com.sptci.prevayler;

import com.sptci.prevayler.annotations.ForeignKey.DeleteAction;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A store for maintaining the relations between a prevalent object and
 * the other prevalent objects in the prevalent system.  This store is
 * consulted for determining the actions to be performed when deleting
 * prevalent objects.  This store is mainly responsible for enforcing
 * managed relations between prevalent objects.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans Pareil Technologies, Inc.</a></p>
 * @author Rakesh Vidyadharan 2008-05-23
 * @version $Id: RelationStorage.java 4345 2008-06-30 21:22:03Z rakesh $
 */
public class RelationStorage implements Serializable
{
  private static final long serialVersionUID = 1L;

  /**
   * A map used to store the meta data associated with a prevalent object.
   * The <code>key</code> to the map is the type of prevalent object, while
   * the <code>value</code> is a {@link
   * com.sptci.prevayler.annotations.ForeignKey.DeleteAction} object that
   * defines the action to be applied.
   */
  private final Map<String,DeleteRule> map
      = new LinkedHashMap<String,DeleteRule>();

  /** The name of the prevalent object whose relations are being mapped. */
  private final String className;

  /**
   * Create a new instance of the storage for the specified class name.
   *
   * @param name The {@link #className} value to use.
   */
  public RelationStorage( final String name )
  {
    this.className = name;
  }

  /**
   * A a new relationship to the store for the specified prevalent object.
   *
   * @param object The prevalent object that is related to the prevalent
   *   object identified by {@link #className}.
   * @param field The name of the field in <code>object</code> that is an
   *   instance of type {@link #className}.
   * @param action The delete action to use on the <code>fiele</code> when
   *   the instance of type {@link #className} is deleted.
   */
  public void add( final PrevalentObject object, final String field,
      final DeleteAction action )
  {
    final String name = object.getClass().getName();

    if ( ! map.containsKey( name) )
    {
      map.put( name, new DeleteRule() );
    }

    map.get( name ).add( field, action );
  }

  /**
   * Return the names of the classes that hold a reference to the prevalent
   * class that is being managed.
   *
   * @return The collection of class names that hold references to
   *   instances of the managed class.
   */
  public Collection<String> getRelations()
  {
    return Collections.unmodifiableCollection( map.keySet() );
  }

  /**
   * Return a map of field name of delete actions for the specified
   * prevalent class with which the class being managed has relations.
   *
   * @see com.sptci.prevayler.RelationStorage.DeleteRule#getRules
   * @param name The name of prevalent class with which relations are
   *   maintained.
   * @return A map of field-delete rule mappings.  Returns an empty
   *   map if no rules exist for the specified class.
   */
  public Map<String,DeleteAction> getDeleteRules( final String name )
  {
    final Map<String,DeleteAction> results = new LinkedHashMap<String,DeleteAction>();
    final DeleteRule rule = map.get( name );
    results.putAll( rule.getRules() );
    return results;
  }

  /**
   * Return the fully qualified class name of the prevalent object for which
   * the reference relations are being stored.
   *
   * @return The {@link #className} value.
   */
  public String getClassName()
  {
    return className;
  }

  /**
   * A class used to capture the names of the fields in a prevalent object
   * that are related to the prevalent object being managed by this
   * store.
   */
  private class DeleteRule implements Serializable
  {
    private static final long serialVersionUID = 1L;

    /**
     * The map used to maintain the field-name to delete rule mapping.
     */
    private Map<String,DeleteAction> actionMap =
        new LinkedHashMap<String,DeleteAction>();

    /**
     * Add a mapping for the specified field name with the associated
     * deleted rule.
     *
     * @param field The name of the field for the mapping.
     * @param action The delete rule to apply for the field.
     */
    private void add( final String field, final DeleteAction action )
    {
      if ( ! actionMap.containsKey( field ) )
      {
        actionMap.put( field, action );
      }
    }

    /**
     * Return the {@link #actionMap} as an unmodifiable view.
     *
     * @return The mappings in {@link #actionMap}.
     */
    private Map<String,DeleteAction> getRules()
    {
      return Collections.unmodifiableMap( actionMap );
    }
  }
}
