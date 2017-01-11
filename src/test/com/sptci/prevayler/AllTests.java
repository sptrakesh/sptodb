package com.sptci.prevayler;

import com.sptci.prevayler.test.CreateTestObjects;
import com.sptci.prevayler.test.DeleteTestObjects;
import com.sptci.prevayler.test.InvalidTests;
import com.sptci.prevayler.test.ReachabilityTests;
import com.sptci.prevayler.test.SearchTest;
import com.sptci.prevayler.test.SystemTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Unit test case suite for the SPT Object Database engine.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans Pareil
 * Technologies, Inc.</a></p>
 * @author Rakesh Vidyadharan 2008-05-23
 * @version $Id: AllTests.java 22 2008-11-24 19:04:25Z sptrakesh $
 */
@RunWith( Suite.class )
@Suite.SuiteClasses
(
  {
    //SimplePersistenceTest.class,
    CreateTestObjects.class,
    ReachabilityTests.class,
    SystemTest.class,
    InvalidTests.class,
    DeleteTestObjects.class,
    SearchTest.class
  }
)
public class AllTests {}
