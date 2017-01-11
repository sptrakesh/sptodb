package com.sptci.prevayler.test;

import com.sptci.prevayler.PrevalentManager;
import com.sptci.prevayler.model.One;
import com.sptci.prevayler.model.Simple;
import com.sptci.prevayler.model.Two;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.util.Collection;

/**
 * Basic unit test using {@link com.sptci.prevayler.model.One}.  Tests
 * persistence by reachability.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans Pareil
 *   Technologies, Inc.</a></p>
 * @author Rakesh Vidyadharan 2008-05-24
 * @version $Id: SystemTest.java 22 2008-11-24 19:04:25Z sptrakesh $
 */
public class SystemTest
{
  static One one = CreateTestObjects.one;
  static String oneName = CreateTestObjects.oneName;

  @Test
  public void count() throws Exception
  {
    PrevalentManager<One> pm = new PrevalentManager<One>();
    final int count = pm.count( One.class );
    assertEquals( "Ensuring persistent count is 1", count, 1 );
  }

  @Test
  public void fetchOne() throws Exception
  {
    PrevalentManager<One> pm = new PrevalentManager<One>();
    final One o = pm.fetch( One.class, one.getObjectId() );
    assertNotNull( "Ensuring one retrieved", o );
    assertEquals( "Ensuring identical oid", o.getObjectId(), one.getObjectId() );
    assertEquals( "Ensuring equivalent objects", o, one );
  }

  @Test
  public void fetchTwo() throws Exception
  {
    PrevalentManager<Two> pm = new PrevalentManager<Two>();
    final Two two = pm.fetch( Two.class, one.getTwo().getObjectId() );
    assertNotNull( "Ensuring one retrieved", two );
    assertEquals( "Ensuring identical oid", two.getObjectId(),
        one.getTwo().getObjectId() );
    assertEquals( "Ensuring equivalent objects", two, one.getTwo() );
  }

  @Test
  @SuppressWarnings( {"unchecked"} )
  public void fetchRange() throws Exception
  {
    PrevalentManager<One> pm = new PrevalentManager<One>();
    Collection<One> collection = pm.fetch( One.class, 0, 10 );
    assertNotNull( "Ensuring non-null collection of one", collection );
    assertFalse( "Ensuring non-empty collection of one", collection.isEmpty() );
  }

  @Test
  public void fetchByName() throws Exception
  {
    PrevalentManager<One> pm = new PrevalentManager<One>();
    final String field = "name";
    Collection<One> collection =
        pm.fetch( One.class, field, oneName );
    assertNotNull( "Ensuring non-null collection of one", collection );
    assertFalse( "Ensuring non-empty collection of one", collection.isEmpty() );
    assertEquals( "Ensuring collection has size 1", collection.size(), 1 );

    for ( One o : collection )
    {
      assertEquals( "Ensuring equivalent objects", o, one );
    }
  }

  @Test
  public void update() throws Exception
  {
    final PrevalentManager<One> pm = new PrevalentManager<One>();
    final String oldName = one.getName();
    final String name = one.getName() + " modified.";

    one.setName( name );
    One o = pm.save( one );
    assertEquals( "Ensuring equivalent object returned", o, one );
    assertEquals( "Ensuring name was modified", name, o.getName() );
    one = o;

    Collection<One> collection = pm.fetch( One.class, "name", one.getName() );
    assertFalse( "Ensuring index updated", collection.isEmpty() );

    collection = pm.fetch( One.class, "name", oldName );
    assertTrue( "Ensuring old index deleted", collection.isEmpty() );

    one.setName( oldName );
    o = pm.save( one );
    assertEquals( "Ensuring equivalent object returned", o, one );
    assertEquals( "Ensuring name was modified", oldName, o.getName() );
    one = o;
  }

  @Test
  public void complexUpdate() throws Exception
  {
    final PrevalentManager<One> pm = new PrevalentManager<One>();
    final String name = one.getTwo().getName() + " modified.";
    final Two t = one.getTwo();
    one.setName( oneName );
    t.setName( name );

    final One o = pm.save( one );
    assertEquals( "Ensuring equivalent object returned", o, one );
    assertEquals( "Ensuring name was modified", oneName, o.getName() );
    assertEquals( "Ensuring two is equivalent", t, o.getTwo() );
    assertEquals( "Ensuring two name was modified", name, o.getTwo().getName() );

    one = o;
  }

  @Test
  public void nullIndex() throws Exception
  {
    final PrevalentManager<Simple> pm = new PrevalentManager<Simple>();
    Simple simple = new Simple();
    simple.setField1( "field1" );
    simple.setField2( null );
    simple = pm.save( simple );

    assertTrue( "Ensuring object saved", simple.isPersistent() );
    final Collection<Simple> coll = pm.fetch( Simple.class, "field2", null );
    assertFalse( "Ensuring null indexed field works", coll.isEmpty() );

    simple = pm.delete( simple );
    assertFalse( "Ensuring object deleted", simple.isPersistent() );
  }
}
