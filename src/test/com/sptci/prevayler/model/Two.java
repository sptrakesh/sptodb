package com.sptci.prevayler.model;

import com.sptci.prevayler.PrevalentObject;
import com.sptci.prevayler.annotations.ForeignKey;

import java.util.ArrayList;
import java.util.Collection;

/**
 * The second model class used to test the prevalent system.  Maintains
 * relationships with {@link Three} (multiple fields).
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans Pareil Technologies, Inc.</a></p>
 * @author Rakesh Vidyadharan 2008-05-23
 * @version $Id: Two.java 22 2008-11-24 19:04:25Z sptrakesh $
 */
public class Two extends PrevalentObject<Long>
{
  private static final long serialVersionUID = 1l;
  private String name;

  @ForeignKey( deleteAction = ForeignKey.DeleteAction.NULL )
  private Three parent;

  @ForeignKey( deleteAction = ForeignKey.DeleteAction.NULL, collectionEntry = "com.sptci.prevayler.model.Three" )
  private Collection<Three> children = new ArrayList<Three>();

  public String getName()
  {
    return name;
  }

  public void setName( final String name )
  {
    this.name = name;
  }

  public Three getParent()
  {
    return parent;
  }

  public void setParent( final Three parent  )
  {
    this.parent = parent;
  }

  public Collection<Three> getChildren()
  {
    return children;
  }

  public void setChildren( final Collection<Three> children  )
  {
    this.children.clear();
    this.children.addAll( children );
  }

  public Long getObjectId( final String oid )
  {
    return Long.parseLong( oid );
  }
}
