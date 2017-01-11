package com.sptci.prevayler.model;

import com.sptci.prevayler.PrevalentObject;
import com.sptci.prevayler.annotations.ForeignKey;
import com.sptci.prevayler.annotations.Index;
import com.sptci.prevayler.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * The first model object used for testing.  Contains references to {@link
 * Two} and {@link Three}.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans Pareil Technologies, Inc.</a></p>
 * @author Rakesh Vidyadharan 2008-05-23
 * @version $Id: One.java 22 2008-11-24 19:04:25Z sptrakesh $
 */
public class One extends PrevalentObject<Long>
{
  private static final long serialVersionUID = 1l;

  @NotNull
  @Index( unique=true )
  private String name;
  @ForeignKey
  private Two two;
  private Collection<Three> three = new ArrayList<Three>();

  public One() {}
  public One( final Long oid ) { super( oid ); }

  public Long getObjectId( final String oid )
  {
    return Long.parseLong( oid );
  }

  public String getName()
  {
    return name;
  }

  public void setName( final String name )
  {
    this.name = name;
  }

  public Two getTwo()
  {
    return two;
  }

  public void setTwo( final Two two )
  {
    this.two = two;
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
}
