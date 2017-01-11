package com.sptci.prevayler.model;

import com.sptci.prevayler.PrevalentObject;
import com.sptci.prevayler.annotations.Searchable;
import com.sptci.prevayler.annotations.Searchables;

/**
 * A simple model object used to execute search tests.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans
 * Pareil Technologies, Inc.</a></p>
 *
 * @author Rakesh Vidyadharan 2008-11-23
 * @version $Id: Article.java 22 2008-11-24 19:04:25Z sptrakesh $
 */
@Searchables
(
  value =
  {
    @Searchable( name = "contentFields", members = { "synopsis",
        "content" } ),
    @Searchable( name = "allFields", members = { "title",
        "synopsis", "content" } )
  }
)
public class Article extends PrevalentObject<Integer>
{
  private static final long serialVersionUID = 1l;

  @Searchable
  private String title;

  private String synopsis;

  private String content;

  public Integer getObjectId( final String oid )
  {
    return Integer.parseInt( oid );
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle( final String title )
  {
    this.title = title;
  }

  public String getSynopsis()
  {
    return synopsis;
  }

  public void setSynopsis( final String synopsis )
  {
    this.synopsis = synopsis;
  }

  public String getContent()
  {
    return content;
  }

  public void setContent( final String content )
  {
    this.content = content;
  }
}
