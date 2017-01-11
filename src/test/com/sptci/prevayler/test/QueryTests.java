package com.sptci.prevayler.test;

import com.sptci.prevayler.PrevalentManager;
import com.sptci.prevayler.model.Simple;

import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit test suite for the various types of query methods not covered by
 * {@link SystemTest}.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans
 * Pareil Technologies, Inc.</a></p>
 *
 * @author Rakesh Vidyadharan 2008-07-19
 * @version $Id: QueryTests.java 18 2008-07-20 03:35:47Z sptrakesh $
 */
public class QueryTests
{
  static Simple[] simples = new Simple[ 10 ];
  static final String field1 = "Field1 Value for index: ";
  static final String field2 = "Field2 Value for index: ";
  static final String field3 = "Field3 Value for index: ";
  static final String field4 = "Field4 Value";

  @BeforeClass
  public static void init() throws Exception
  {
    final PrevalentManager<Simple> manager = new PrevalentManager<Simple>();

    for ( int i = 0; i < simples.length - 2; ++i )
    {
      final Simple simple = new Simple();
      simple.setField1( field1 + i );
      simple.setField2( field2 + i );
      simple.setField3( field3 + i );
      simple.setField4( field4 );
      simples[i] = manager.save( simple );
      assertTrue( "Ensuring object was persisted", simples[i].isPersistent() );
    }

    final Simple simple1 = new Simple();
    simple1.setField1( field1 );
    simple1.setField2( field2 );
    simple1.setField3( field3 );
    simple1.setField4( field4 );
    simples[8] = manager.save( simple1 );
    assertTrue( "Ensuring object was persisted", simples[8].isPersistent() );

    final Simple simple2 = new Simple();
    simple2.setField1( field1 );
    simple2.setField2( field2 );
    simple2.setField3( field3 );
    simple2.setField4( field4 );
    simples[9] = manager.save( simple2 );
    assertTrue( "Ensuring object was persisted", simples[9].isPersistent() );
  }

  @Test
  public void union() throws Exception
  {
    final PrevalentManager<Simple> manager = new PrevalentManager<Simple>();

    Map<String,String> parameters = new HashMap<String,String>();
    parameters.put( "field1", field1 + 1 );
    parameters.put( "field2", field2 + 2 );
    parameters.put( "field3", field3 + 3 );

    Collection<Simple> results = manager.fetchUnion( Simple.class, parameters );
    assertEquals( "Ensuring three instances found", results.size(), 3 );
  }

  @Test
  public void intersection1() throws Exception
  {
    final PrevalentManager<Simple> manager = new PrevalentManager<Simple>();

    Map<String,String> parameters = new HashMap<String,String>();
    parameters.put( "field1", field1 );
    parameters.put( "field2", field2 );
    parameters.put( "field3", field3 );

    Collection<Simple> results = manager.fetchIntersection( Simple.class, parameters );
    assertEquals( "Ensuring two instances found", results.size(), 2 );
  }

  @Test
  public void intersection2() throws Exception
  {
    final PrevalentManager<Simple> manager = new PrevalentManager<Simple>();

    Map<String,String> parameters = new HashMap<String,String>();
    parameters.put( "field1", field1 );
    parameters.put( "field4", field4 );

    Collection<Simple> results = manager.fetchIntersection( Simple.class, parameters );
    assertEquals( "Ensuring two instances found", results.size(), 2 );
  }

  @AfterClass
  public static void finish() throws Exception
  {
    final PrevalentManager<Simple> manager = new PrevalentManager<Simple>();

    for ( Simple simple : simples )
    {
      manager.delete( simple );
      assertTrue( "Ensuring object was deleted", simple.isPersistent() );
    }
  }
}
