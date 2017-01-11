package com.sptci.prevayler.model;

import com.sptci.prevayler.PrevalentObject;

import java.util.ArrayList;
import java.util.Collection;

/**
 * The third test class.  Maintains relations with {@link One} and
 * {@link Four}.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans Pareil Technologies, Inc.</a></p>
 * @author Rakesh Vidyadharan 2008-05-23
 * @version $Id: Three.java 22 2008-11-24 19:04:25Z sptrakesh $
 */
public class Three extends PrevalentObject<Long>
{
  private static final long serialVersionUID = 1l;
  private String name;
  private One one;
  private Collection<Four> four = new ArrayList<Four>();

  public static long getSerialVersionUID()
  {
    return serialVersionUID;
  }

  public String getName()
  {
    return name;
  }

  public void setName( final String name )
  {
    this.name = name;
  }

  public One getOne()
  {
    return one;
  }

  public void setOne( final One one )
  {
    this.one = one;
  }

  public Collection<Four> getFour()
  {
    return four;
  }

  public void setFour( final Collection<Four> four )
  {
    this.four.clear();
    this.four.addAll( four );
  }

  public Long getObjectId( final String oid )
  {
    return Long.parseLong( oid );
  }
}
