package com.sptci.prevayler.test;

import com.sptci.prevayler.PrevalentManager;
import com.sptci.prevayler.model.One;
import com.sptci.prevayler.model.Three;
import com.sptci.prevayler.model.Two;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Test class for creating the test objects in the suite of tests in this
 * package.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans Pareil
 * Technologies, Inc.</a></p>
 *
 * @author Rakesh Vidyadharan 2008-06-10
 * @version $Id: CreateTestObjects.java 18 2008-07-20 03:35:47Z sptrakesh $
 */
public class CreateTestObjects
{
  static One one;

  static String oneName = "One";
  static String twoName = "Two";
  static String three1Name = "Three1";
  static String three2Name = "Three2";
  static String fourName = "Four";

  @BeforeClass
  public static void init() throws Exception
  {
    one = createOne();
    PrevalentManager<One> pm = new PrevalentManager<One>();
    one = pm.save( one );
  }

  @Test
  public void one() throws Exception
  {
    assertNotNull( "Ensuring objectId was set", CreateTestObjects.one.getObjectId() );
  }

  private static One createOne()
  {
    final One one = new One();
    one.setName( oneName );

    one.setTwo( createTwo() );
    one.getTwo().getParent().setOne( one );

    final ArrayList<Three> list = new ArrayList<Three>( 2 );
    list.add( one.getTwo().getParent() );

    list.addAll( one.getTwo().getChildren() );
    one.setThree( list );
    assertEquals( "Ensuring two entries in One.three", one.getThree().size(), 2 );

    return one;
  }

  private static Two createTwo()
  {
    final Two two = new Two();
    two.setName( twoName );
    two.setParent( createThree1() );

    final Collection<Three> collection = new ArrayList<Three>();
    collection.add( createThree2() );
    two.setChildren( collection );

    return two;
  }

  private static Three createThree1()
  {
    final Three three = new Three();
    three.setName( three1Name );

    return three;
  }

  private static Three createThree2()
  {
    final Three three = new Three();
    three.setName( three2Name );

    return three;
  }
}
