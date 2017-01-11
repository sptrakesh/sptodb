package com.sptci.prevayler;

import com.sptci.prevayler.model.One;
import com.sptci.prevayler.model.Three;
import com.sptci.prevayler.model.Two;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * A simple unit test suite to test persistence by reachability of {@link
 * com.sptci.prevayler.model.One}.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans Pareil Technologies, Inc.</a></p>
 * @author Rakesh Vidyadharan 2008-05-23
 * @version $Id: SimplePersistenceTest.java 4345 2008-06-30 21:22:03Z rakesh $
 */
public class SimplePersistenceTest
{
  static PrevalentSystem system;
  static One one;

  static String oneName = "One";
  static String twoName = "Two";
  static String three1Name = "Three1";
  static String three2Name = "Three2";
  static String fourName = "Four";

  @BeforeClass
  public static void init() throws Exception
  {
    system = new PrevalentSystem();
    one = createOne();
    final One o = (One) system.add( one, new Date() );

    assertTrue( "Ensuring identical object returned", one == o );
  }

  @Test
  public void one() throws Exception
  {
    assertNotNull( "Ensuring objectId was set", one.getObjectId() );
  }

  @Test
  public void two() throws Exception
  {
    assertNotNull( "Ensuring Two persisted by reachability",
        one.getTwo().getObjectId() );
  }

  @Test
  public void twoThree() throws Exception
  {
    assertNotNull( "Ensuring three1 persisted by reachability",
        one.getTwo().getParent().getObjectId() );

    for ( Three t3 : one.getTwo().getChildren() )
    {
      assertNotNull( "Ensuring three2 persisted by reachability",
	  t3.getObjectId() );
    }
  }

  @Test
  public void oneThree() throws Exception
  {
    assertEquals( "Ensuring two entries in One.three", one.getThree().size(), 2 );

    final Three three1 = ( (ArrayList<Three>) one.getThree() ).get( 0 );
    assertEquals( "Ensuring One.Three1 is same as Two.Three1",
        three1, one.getTwo().getParent() );
    assertEquals( "Ensuring relation between one and three",
        one, three1.getOne() );

    final Three three2 = ( (ArrayList<Three>) one.getThree() ).get( 1 );
    final Three three2a = ( (ArrayList<Three>) one.getTwo().getChildren() ).get( 0 );
    assertEquals( "Ensuring One.Three2 is same as Two.Three2", three2, three2a );
  }

  @AfterClass
  public static void delete() throws Exception
  {
    final One o = (One) system.delete( one, new Date() );
    assertTrue( "Ensuring identical object returned", one == o );
    assertNull( "Ensuring objectId was unset", one.getObjectId() );
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
    Collection<Three> collection = new ArrayList<Three>();
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
