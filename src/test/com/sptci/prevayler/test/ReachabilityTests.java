package com.sptci.prevayler.test;

import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import com.sptci.prevayler.model.Three;
import com.sptci.prevayler.model.One;

import java.util.ArrayList;

/**
 * Tests on persistence by reachability.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans Pareil
 * Technologies, Inc.</a></p>
 *
 * @author Rakesh Vidyadharan 2008-06-10
 * @version $Id: ReachabilityTests.java 4345 2008-06-30 21:22:03Z rakesh $
 */
public class ReachabilityTests
{
  One one = CreateTestObjects.one;

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

    for ( Three t3 : CreateTestObjects.one.getTwo().getChildren() )
    {
      assertNotNull( "Ensuring three2 persisted by reachability",
	  t3.getObjectId() );
    }
  }

  @Test
  public void oneThree() throws Exception
  {
    assertEquals( "Ensuring two entries in One.three",
        one.getThree().size(), 2 );

    final Three three1 = (
        (ArrayList<Three>) CreateTestObjects.one.getThree() ).get( 0 );
    assertEquals( "Ensuring One.Three1 is same as Two.Three1",
        three1, one.getTwo().getParent() );
    assertEquals( "Ensuring relation between one and three",
        one, three1.getOne() );

    final Three three2 = ( (ArrayList<Three>) one.getThree() ).get( 1 );
    final Three three2a = ( (ArrayList<Three>) one.getTwo().getChildren() ).get( 0 );
    assertEquals( "Ensuring One.Three2 is same as Two.Three2", three2, three2a );
  }
}
