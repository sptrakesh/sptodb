package com.sptci.prevayler.model;

import com.sptci.prevayler.PrevalentObject;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Four test class.  Maintains a M-N relationship with {@link Three}.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans Pareil Technologies, Inc.</a></p>
 * @author Rakesh Vidyadharan 2008-05-23
 * @version $Id: Four.java 22 2008-11-24 19:04:25Z sptrakesh $
 */
public class Four extends PrevalentObject<Long>
{
  private static final long serialVersionUID = 1l;
  private String name;
  private Collection<Three> three = new ArrayList<Three>();

  public String getName()
  {
    return name;
  }

  public void setName( final String name )
  {
    this.name = name;
  }

  public Collection<Three> getThree()
  {
    return three;
  }

  public void setThree( final Collection<Three> three )
  {
    this.three.clear();
    this.three.addAll( three );
  }

  public Long getObjectId( final String oid )
  {
    return Long.parseLong( oid );
  }
}
