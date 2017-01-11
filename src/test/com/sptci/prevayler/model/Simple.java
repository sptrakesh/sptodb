package com.sptci.prevayler.model;

import com.sptci.prevayler.PrevalentObject;
import com.sptci.prevayler.annotations.Index;

/**
 * A simple prevalent object used to test queries.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans
 * Pareil Technologies, Inc.</a></p>
 *
 * @author Rakesh Vidyadharan 2008-7-19
 * @version $Id: Simple.java 22 2008-11-24 19:04:25Z sptrakesh $
 */
public class Simple extends PrevalentObject<Integer>
{
  private static final long serialVersionUID = 1l;

  @Index
  private String field1;

  @Index
  private String field2;

  @Index
  private String field3;

  @Index
  private String field4;

  public String getField1()
  {
    return field1;
  }

  public void setField1( final String field1 )
  {
    this.field1 = field1;
  }

  public String getField2()
  {
    return field2;
  }

  public void setField2( final String field2 )
  {
    this.field2 = field2;
  }

  public String getField3()
  {
    return field3;
  }

  public void setField3( final String field3 )
  {
    this.field3 = field3;
  }

  public String getField4()
  {
    return field4;
  }

  public void setField4( final String field4 )
  {
    this.field4 = field4;
  }

  public Integer getObjectId( final String oid )
  {
    return Integer.parseInt( oid );
  }
}
