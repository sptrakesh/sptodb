package com.sptci.prevayler;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;

import java.io.IOException;
import java.util.logging.Level;

/**
 * A thread used to close an {@link org.apache.lucene.index.IndexReader} and
 * its associated {@link org.apache.lucene.search.IndexSearcher} after all
 * references to the index reader have been cleared.
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans
 * Pareil Technologies, Inc.</a></p>
 *
 * @author Rakesh 2008-11-20
 * @version $Id: IndexCloser.java 22 2008-11-24 19:04:25Z sptrakesh $
 */
class IndexCloser extends Thread
{
  /** The index reader that is to be closed. */
  private final IndexReader reader;

  /** The index search to be closed after the reader is closed. */
  private final IndexSearcher searcher;

  /**
   * Create a new index closer to close the specified reader and searchers.
   *
   * @param reader The reader instance to close.
   * @param searcher The searcher instance to close.
   */
  IndexCloser( final IndexReader reader, final IndexSearcher searcher )
  {
    this.reader = reader;
    this.searcher = searcher;
  }

  /**
   * Close the index reader and then sleep until the reader has really been
   * closed.  Once closed close the searcher as well.
   *
   * @see #closeReader()
   * @see #checkClosed()
   */
  @Override
  public void run()
  {
    closeReader();
    checkClosed();
    closeSearcher();
  }

  /** Close the {@link #reader} instance. */
  private void closeReader()
  {
    try
    {
      reader.close();
    }
    catch ( IOException ex )
    {
      StorageSystem.logger.log( Level.WARNING,
          "Error closing index reader", ex );
    }
  }

  /** Loop through until the {@link #reader} has been closed. */
  private void checkClosed()
  {
    boolean notClosed = true;
    while ( notClosed )
    {
      try
      {
        reader.isCurrent();
        sleep( 100 );
      }
      catch ( InterruptedException e )
      {
        // Ignore
      }
      catch ( Exception e )
      {
        notClosed = false;
      }
    }
  }

  /** Close the {@link #searcher} instance. */
  private void closeSearcher()
  {
    try
    {
      searcher.close();
    }
    catch ( IOException ex )
    {
      StorageSystem.logger.log( Level.WARNING,
          "Error closing index searcher", ex );
    }
  }
}
