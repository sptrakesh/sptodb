package com.sptci.prevayler.test;

import com.sptci.prevayler.PrevalentManager;
import com.sptci.prevayler.model.Article;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;

/**
 * Unit test suite for the full-text indexing and searching features exposed
 * by the database. <p/> <p>&copy; Copyright 2008 <a href='http://sptci.com/'
 * target='_top'>Sans Pareil Technologies, Inc.</a></p>
 *
 * @author Rakesh Vidyadharan 2008-11-23
 * @version $Id: SearchTest.java 23 2008-11-24 19:49:55Z sptrakesh $
 */
public class SearchTest
{
  private static Article article;

  private static Article[] dummy = new Article[20];

  private static final String title = "SPT Object Database";

  private static final String synopsis = "SPT Object Database is a simple " +
      "API built around Prevayler.  The goal is to provide a simpler and more " +
      "natural object oriented API around the Prevayler API.  Also supports " +
      "full-text search capabilities on annotated fields.";

  private static final String content = "SPT Object Database (SPTODB) is a " +
      "database engine built using the Prevayler serialisation engine. " +
      "SPTODB attempts to privide the common features necessary in a " +
      "object database system.  It also provides an easier and more object " +
      "oriented interface than that provided by Prevayler. " +
      "SPTODB supports persistence by reachability, indexed fields for fast " +
      "retrieval of persisted objects, full-text searches on annotated " +
      "fields, etc.  Managed relationships are also provided with constraint " +
      "semantics and rules.";

  @BeforeClass
  public static void init() throws Exception
  {
    final PrevalentManager<Article> pm = new PrevalentManager<Article>();

    article = new Article();
    article.setTitle( title );
    article.setSynopsis( synopsis );
    article.setContent( content );
    article = pm.save( article );

    for ( int i = 0; i < 20; ++i )
    {
      dummy[i] = new Article();
      dummy[i].setTitle( "blah" );
      dummy[i].setSynopsis( "blah blah" );
      dummy[i].setContent( "blah blah blah" );
      dummy[i] = pm.save( dummy[i] );
    }
  }

  @Test
  public void objectId()
  {
    assertNotNull( "Ensuring objectId set", article.getObjectId() );
  }

  /**
   * Unit test for a {@link com.sptci.prevayler.annotations.Searchable}
   * annotation at the field level.
   *
   * @throws Exception If errors are encountered
   */
  @Test
  public void simpleSearch() throws Exception
  {
    final PrevalentManager<Article> pm = new PrevalentManager<Article>();
    final Query query = new TermQuery( new Term( "title", "spt" ) );
    final Collection<Article> collection = pm.search( query, 10 );
    assertEquals( "Ensure that only one article is found", 1,
        collection.size() );
  }

  /**
   * Unit test for the {@link com.sptci.prevayler.annotations.Searchables}
   * annotation as the class level.  Tests the first
   * {@link com.sptci.prevayler.annotations.Searchable} member.
   *
   * @throws Exception If errors are encountered.
   */
  @Test
  public void contentFields() throws Exception
  {
    final PrevalentManager<Article> pm = new PrevalentManager<Article>();
    final Query query = new TermQuery( new Term( "contentFields", "annotated" ) );
    final Collection<Article> collection = pm.search( query, 10 );
    assertEquals( "Ensure that only one article is found", 1,
        collection.size() );
  }

  /**
   * Unit test for the {@link com.sptci.prevayler.annotations.Searchables}
   * annotation as the class level.  Tests the second
   * {@link com.sptci.prevayler.annotations.Searchable} member.
   *
   * @throws Exception If errors are encountered.
   */
  @Test
  public void allFields() throws Exception
  {
    final PrevalentManager<Article> pm = new PrevalentManager<Article>();
    final Query query = new TermQuery( new Term( "allFields", "sptodb" ) );
    final Collection<Article> collection = pm.search( query, 10 );
    assertEquals( "Ensure that only one article is found", 1,
        collection.size() );
  }

  @AfterClass
  public static void finish() throws Exception
  {
    PrevalentManager<Article> pm = new PrevalentManager<Article>();
    article = pm.delete( article );
    assertNull( "Ensuring objectId unset", article.getObjectId() );

    final Query query = new TermQuery( new Term( "allFields", "sptodb" ) );
    final Collection<Article> collection = pm.search( query, 10 );
    assertEquals( "Ensure that no articles are found", 0,
        collection.size() );

    for ( Article d : dummy )
    {
      pm.delete( d );
    }
  }
}
