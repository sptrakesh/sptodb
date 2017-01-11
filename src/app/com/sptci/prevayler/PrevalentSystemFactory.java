package com.sptci.prevayler;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.foundation.serialization.JavaSerializer;
import org.prevayler.foundation.serialization.XStreamSerializer;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A factory class used to boot-strap {@link PrevalentSystem} instances.
 *
 * <p>This class may be configured using the following JVM system properties:</p>
 * <ol>
 *   <li><code>sptodb.data.dir</code> - The directory under which the database
 *     snapshot and journal files are stored.  The default value used if this
 *     property is not specified is <code>/var/data/sptodb</code>.</li>
 *   <li><code>sptodb.snapshot.interval</code> - The interval in seconds at
 *     which snapshots of the prevalent system are to be taken.  The default
 *     value used is <code>86400</code> (one day).</li>
 *   <li><code>sptodb.serialiser.format</code> - The format to use for taking
 *     snapshots of the prevalent system and creating transaction journals.
 *     The supported options are:
 *     <ol>
 *       <li><code>java</code> - Indicates that regular Java object
 *         serialisation be used to take the snapshot and write journals.
 *           This is the default unless otherwise specified.</li>
 *       <li><code>xml</code> - Indicates that the journals should be written
 *         and snapshot taken using
 *         <a href='http://xstream.codehaus.org/'>XStream</a>.  XML
 *         serialisation is slower (both serialisation and de-serialisation),
 *         however gives you more options processing the prevalent system
 *         for other purposes.  Also useful if you need to restore the
 *         system after heavy modifications (refactoring) to the object
 *         model.</li>
 *     </ol>
 * </ol>
 *
 * <p>The following code shows sample usage of this class</p>
 * <pre>
 *   import com.sptci.prevayler.PrevalentSystemFactory;
 *   import com.sptci.prevayler.transaction.Save;
 *
 *   ...
 *     // MyPrevalentObject is a sub-class of PrevalentObject
 *     final MyPrevalentObject obj1 = new MyPrevalentObject();
 *     obj1.setXXX();
 *     ...
 *     final Save&lt;MyPrevalentObject&gt; save = new Save&lt;MyPrevalentObject&gt;( obj1 );
 *     final MyPrevalentObject obj2 = PrevalentSystemFactory.getPrevayler().execute( save );
 *     System.out.format( "MyPrevalentObject created with OID: %s%n", obj2.getObjectId() );
 * </pre>
 *
 * @see PrevalentManager
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans Pareil Technologies, Inc.</a></p>
 * @author Rakesh Vidyadharan 2008-05-22
 * @version $Id: PrevalentSystemFactory.java 23 2008-11-24 19:49:55Z sptrakesh $
 */
public final class PrevalentSystemFactory
{
  /**
   * The system parameter used to configure the prevalent data directory.
   *
   * {@value}
   */
  public static final String DATA_DIRECTORY = "sptodb.data.dir";

  /**
   * The default value to use for the prevalent data directory.
   *
   * {@value}
   */
  public static final String DEFAULT_DIRECTORY = "/var/data/sptodb";

  /**
   * The directory under {@link #DATA_DIRECTORY} in which the prevalent
   * objects are stored.
   *
   * {@value}
   */
  public static final String OBJECT_STORAGE = "data";

  /**
   * The directory under {@link #DATA_DIRECTORY} under which lucene search
   * indices are stored.
   *
   * {@value}
   */
  public static final String SEARCH_STORAGE = "search";

  /**
   * The system property used to configure the interval at which a snapshot
   * of the database is taken.  Note that the value should be specified in
   * <b>seconds</b>.
   *
   * {@value}
   */
  public static final String SNAPSHOT_INTERVAL = "sptodb.snapshot.interval";

  /**
   * The default snapshot interval to use.  Default is 24 hours.
   *
   * {@value}
   */
  public static final String DEFAULT_SNAPSHOT_INTERVAL = "86400";

  /**
   * The JVM system property used to configure the serialisation technique
   * used for snapshots and transaction journals.
   *
   * {@value}
   */
  public static final String SERIALISER_FORMAT = "sptodb.serialiser.format";

  /**
   * The default value for the {@link #SERIALISER_FORMAT} property.  Defaults
   * to Java serialisation.
   *
   * {@value}
   */
  public static final String DEFAULT_SERIALISER_FORMAT = "java";

  /**
   * The JVM system property used to specify the size of the batches in
   * which the search index writer is to be committed.
   */
  public static final String SEARCH_BATCH_SIZE = "sptodb.search.batchSize";

  /**
   * The default value for the {@link #SEARCH_BATCH_SIZE} property.
   *
   * {@value}
   */
  public static final String DEFAULT_SEARCH_BATCH_SIZE = "20";

  /** The logger to use to log messages. */
  private static final Logger logger = Logger.getLogger( "SPTODBLogger" );

  /**
   * A map used to maintain the various prevalent systems maintained by the
   * factory.
   */
  private static final ConcurrentMap<Class,Prevayler> systems =
      new ConcurrentHashMap<Class,Prevayler>();

  /** Default constructor.  Cannot be instantiated. */
  private PrevalentSystemFactory() {}

  /**
   * Boot-strap a prevalent system using the default {@link PrevalentSystem}
   * class.
   *
   * @see #getPrevayler( Class )
   * @return The initialised prevayler instance to use.
   * @throws PrevalentException If errors are encountered while boot strapping
   *   the prevalent system.
   */
  public static Prevayler getPrevayler()
    throws PrevalentException
  {
    return getPrevayler( PrevalentSystem.class );
  }

  /**
   * Create a prevalent system for the specified system class.
   *
   * @see #getPrevayler( Class, String )
   * @param system The class that represents the prevalent system to be
   *   managed.
   * @return The initialised prevayler instance to use.
   * @throws PrevalentException If errors are encountered while boot strapping
   *   the prevalent system.
   */
  public static Prevayler getPrevayler( final Class system )
    throws PrevalentException
  {
    return getPrevayler( system, getDatabaseDirectory( system ) );
  }

  /**
   * Create a prevalent system for the specified system class.
   *
   * @see #getPrevayler( Class, String, String )
   * @param system The class that represents the prevalent system to be
   *   managed.
   * @param directory The directory in which serialised state of the
   *   prevalent system is to be stored.  Note that you must specify different
   *   directories if you are using this factory to boot-strap multiple
   *   prevalent system instances.
   * @return The initialised prevayler instance to use.
   * @throws PrevalentException If errors are encountered while boot strapping
   *   the prevalent system.  Also thrown if the <code>system</code> specified
   *   is not a sub-class of {@link PrevalentSystem}.
   */
  public static Prevayler getPrevayler( final Class system,
      final String directory ) throws PrevalentException
  {
    final String format =
            System.getProperty( SERIALISER_FORMAT, DEFAULT_SERIALISER_FORMAT );
    return getPrevayler( system, directory, format );
  }

  /**
   * Create a prevalent system for the specified system class.
   *
   * @see #snapshot
   * @param system The class that represents the prevalent system to be
   *   managed.
   * @param directory The directory in which serialised state of the
   *   prevalent system is to be stored.  Note that you must specify different
   *   directories if you are using this factory to boot-strap multiple
   *   prevalent system instances.
   * @param serialiser The serialiser to use for the transaction journals and
   *   snapshots.  Valid values are <code>java</code> or <code>xml</code>.
   * @return The initialised prevayler instance to use.
   * @throws PrevalentException If errors are encountered while boot strapping
   *   the prevalent system.  Also thrown if the <code>system</code> specified
   *   is not a sub-class of {@link PrevalentSystem}.
   */
  public static Prevayler getPrevayler( final Class system,
      final String directory, final String serialiser ) throws PrevalentException
  {
    if ( ! systems.containsKey( system ) )
    {
      if ( ! PrevalentSystem.class.isAssignableFrom( system ) )
      {
        throw new PrevalentException( "Class specified: " + system +
            " is not a sub-class of " + PrevalentSystem.class.getName() );
      }

      final long start = System.currentTimeMillis();

      try
      {
        final PrevaylerFactory factory = new PrevaylerFactory();
        factory.configurePrevalenceDirectory( directory );

        if ( DEFAULT_SERIALISER_FORMAT.equalsIgnoreCase( serialiser ) )
        {
          factory.configureSnapshotSerializer( new JavaSerializer() );
          factory.configureJournalSerializer( new JavaSerializer() );
        }
        else
        {
          factory.configureJournalSerializer( new XStreamSerializer( "UTF-8" ) );
          factory.configureSnapshotSerializer( new XStreamSerializer( "UTF-8" ) );
        }

        factory.configurePrevalentSystem( system.newInstance() );

        final Prevayler prevayler = factory.create();
        snapshot( prevayler );
        systems.putIfAbsent( system, prevayler );
      }
      catch ( Throwable t )
      {
        throw new PrevalentException( t );
      }

      final long end = System.currentTimeMillis();
      logger.info( "Initialised prevalent system of type: " + system +
          " in " + ( ( end - start ) / 1000.0 ) + " seconds." );
    }

    return systems.get( system );
  }

  /**
   * Return the root directory under which the entire database system is
   * stored.  Note that this is the value of the {@link #DATA_DIRECTORY}
   * as configured or the default value.
   *
   * @return The configured directory or the default value.
   */
  protected static String getDataDirectory()
  {
    return System.getProperty( DATA_DIRECTORY, DEFAULT_DIRECTORY );
  }

  /**
   * Return the directory under which serialised instances of the specified
   * class are to be stored.
   *
   * @param system The class whose instances are to be stored.
   * @return The directory under which the instances are to be serialised.
   */
  protected static String getDatabaseDirectory( final Class system )
  {
    final String separator = System.getProperty( "file.separator" );
    final String directory = getDataDirectory();
    return ( directory + separator + OBJECT_STORAGE +
        separator + system.getName() );
  }

  /**
   * Return the directory under which the lucene full-text search indices
   * are to be stored.
   *
   * @param system The class whose search index location is to be returned
   * @return The directory under which the search indices are stored.
   */
  protected static String getSearchDirectory( final Class system )
  {
    final String separator = System.getProperty( "file.separator" );
    final String directory = getDataDirectory();
    return ( directory + separator + SEARCH_STORAGE +
        separator + system.getName() );
  }

  /**
   * Return the size of the batch at which lucene index writer is to be
   * committed.
   *
   * @return The number of transactions after which the index writer is to
   *   be committed and index reader re-opened.
   */
  protected static int getSearchBatchSize()
  {
    return Integer.parseInt(
        System.getProperty( SEARCH_BATCH_SIZE, DEFAULT_SEARCH_BATCH_SIZE ) );
  }

  /**
   * Start a timer task for taking snapshots of the prevalent system.
   * This method will be enhanced to take snapshots at configured intervals.
   *
   * @param prevayler The prevalent system to snapshot.
   */
  private static void snapshot( final Prevayler prevayler )
  {
    final long interval = Long.parseLong( System.getProperty(
        SNAPSHOT_INTERVAL, DEFAULT_SNAPSHOT_INTERVAL ) ) * 1000;
    final TimerTask task = new SnapshotTask( prevayler );

    logger.info(
        "Scheduling prevalent system snapshot at interval " + interval );
    ( new Timer( true ) ).scheduleAtFixedRate( task, interval, interval );
  }

  /**
   * A {@link java.util.TimerTask} that is used to snapshot the {@link
   * #prevayler} periodically.
   */
  private static class SnapshotTask extends TimerTask
  {
    /** The prevalent system to snapshot. */
    private final Prevayler prevayler;

    /**
     *  Create a new instance of the task for the specified system.
     *
     * @param prevayler The prevayler instance to snapshot.
     */
    private SnapshotTask( final Prevayler prevayler )
    {
      this.prevayler = prevayler;
    }

    /**
     * The action to be performed by this task when run by a {@link
     * java.util.Timer}.
     */
    public void run()
    {
      logger.info( "Taking snapshot of prevalent system" );
      try
      {
        prevayler.takeSnapshot();
      }
      catch ( IOException ioex )
      {
        logger.log( Level.WARNING,
            "Error taking prevalent system snapshot", ioex );
      }
    }
  }
}
