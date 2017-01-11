package com.sptci.prevayler.test;

import com.sptci.prevayler.ConstraintException;
import com.sptci.prevayler.DeleteException;
import com.sptci.prevayler.NullException;
import com.sptci.prevayler.PrevalentManager;
import com.sptci.prevayler.model.One;
import com.sptci.prevayler.model.Two;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

import java.util.Collection;

/**
 * Tests for valid exception conditions when interacting with the system.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans Pareil
 * Technologies, Inc.</a></p>
 *
 * @author Rakesh Vidyadharan 2008-06-10
 * @version $Id: InvalidTests.java 22 2008-11-24 19:04:25Z sptrakesh $
 */
public class InvalidTests
{
  final One one = CreateTestObjects.one;

  @Test
  public void invalidFetch() throws Exception
  {
    PrevalentManager<One> pm = new PrevalentManager<One>();
    final One o = pm.fetch( One.class, "-1" );
    assertNull( "Ensuring object not found", o );
  }

  @Test
  @SuppressWarnings( {"unchecked"} )
  public void invalidFetchRange() throws Exception
  {
    PrevalentManager<One> pm = new PrevalentManager<One>();
    final Collection<One> collection = pm.fetch( One.class, 10, 20 );
    assertNotNull( "Ensuring collection not null", collection );
    assertTrue( "Ensuring collection has no data", collection.isEmpty() );
  }

  @Test
  public void invalidFetchByName() throws Exception
  {
    PrevalentManager<One> pm = new PrevalentManager<One>();
    final String field = "name";
    final String value = String.valueOf( System.currentTimeMillis() );
    Collection<One> collection =
        pm.fetch( One.class, field, value );
    assertNotNull( "Ensuring non-null collection of one", collection );
    assertTrue( "Ensuring collection has not data", collection.isEmpty() );
  }

  @Test
  public void invalidOid() throws Exception
  {
    One o2 = null;
    PrevalentManager<One> pm = new PrevalentManager<One>();

    try
    {
      final One o1 = new One( one.getObjectId() );
      o2 = pm.save( o1 );
      fail( "ObjectId constraint violated!" );
    }
    catch ( ConstraintException cex )
    {
      assertNull( "Ensuring o2 not added", o2 );
    }
  }

  @Test
  public void invalidName() throws Exception
  {
    One o2 = null;
    PrevalentManager<One> pm = new PrevalentManager<One>();

    try
    {
      final One o1 = new One();
      o1.setName( CreateTestObjects.oneName );
      o2 = pm.save( o1 );
      fail( "One.name unique constraint violated!" );
    }
    catch ( ConstraintException cex )
    {
      assertNull( "Ensuring o2 not added", o2 );
    }
  }

  @Test
  public void nullName() throws Exception
  {
    One o2 = null;
    PrevalentManager<One> pm = new PrevalentManager<One>();

    try
    {
      final One o1 = new One();
      o2 = pm.save( o1 );
      fail( "One.name not-null constraint violated!" );
    }
    catch ( NullException nex )
    {
      assertNull( "Ensuring o2 not added", o2 );
    }
  }

  @Test
  public void constraintDelete() throws Exception
  {
    try
    {
      PrevalentManager<Two> pm = new PrevalentManager<Two>();
      pm.delete( one.getTwo() );
      fail( "Deleting One#two must fail due to default constraint" );
    }
    catch ( DeleteException dex )
    {
      assertNotNull( "Ensuring One#objectId unaffected", one.getObjectId() );
    }
  }
}
