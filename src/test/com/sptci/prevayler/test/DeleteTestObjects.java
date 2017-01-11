package com.sptci.prevayler.test;

import com.sptci.prevayler.PrevalentManager;
import com.sptci.prevayler.model.One;
import com.sptci.prevayler.model.Three;
import com.sptci.prevayler.model.Two;

import org.junit.AfterClass;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Delete the test objects created as part of the test suite.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans Pareil
 * Technologies, Inc.</a></p>
 *
 * @author Rakesh Vidyadharan 2008-06-10
 * @version $Id: DeleteTestObjects.java 4383 2008-07-12 06:36:01Z rakesh $
 */
public class DeleteTestObjects
{
  static final One one = CreateTestObjects.one;

  @Test
  public void nullDelete() throws Exception
  {
    PrevalentManager<Three> pm3 = new PrevalentManager<Three>();
    final Three t = pm3.delete( one.getTwo().getParent() );
    assertNull( "Ensuring three1 deleted", t.getObjectId() );

    PrevalentManager<Two> pm2 = new PrevalentManager<Two>();
    final Two t2 = pm2.fetch( Two.class, one.getTwo().getObjectId() );
    System.out.format(  "Running nullDelete children: %s%n", t2.getChildren() );
    assertNull( "Ensuring Two#parent set to null", t2.getParent() );
  }

  @Test
  public void nullCollectionDelete() throws Exception
  {
    System.out.println(  "Running nullCollectionDelete" );
    Three t = ( (ArrayList<Three>) one.getTwo().getChildren() ).get( 0 );

    PrevalentManager<Three> pm3 = new PrevalentManager<Three>();
    t = pm3.delete( t );
    assertNull( "Ensuring three2 deleted", t.getObjectId() );

    PrevalentManager<Two> pm2 = new PrevalentManager<Two>();
    final Two t2 = pm2.fetch( Two.class, one.getTwo().getObjectId() );
    assertTrue( "Ensuring Two#children is empty", t2.getChildren().isEmpty() );
  }

  @AfterClass
  public static void delete() throws Exception
  {
    final Object oid = one.getObjectId();
    PrevalentManager<One> pm = new PrevalentManager<One>();
    final One o = pm.delete( one );
    assertNull( "Ensuring objectId was unset", o.getObjectId() );

    final One o1 = pm.fetch( One.class, oid );
    assertNull( "Ensuring deleted object not found", o1 );
  }
}
