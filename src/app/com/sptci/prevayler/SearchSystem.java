package com.sptci.prevayler;

import com.sptci.ReflectionUtility;
import com.sptci.prevayler.annotations.Searchable;
import com.sptci.prevayler.annotations.Searchables;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.NIOFSDirectory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.logging.Level;

/**
 * Abstracts all the full-text search indexing and de-indexing operations
 * for the prevalent system.  Indexing is performed using
 * <a href='http://lucene.apache.org/java/docs/index.html' target='_top'>Lucene</a>.
 *
 * <p><b>Note:</b>The field values are converted to {@link String} using
 * the {@link Object#toString()} method to retrieve the content to index.
 * To ensure meaningful indices ensure that the fields annotated return
 * meaningful values.</p>
 *
 * <p>&copy; Copyright 2008 <a href='http://sptci.com/' target='_top'>Sans
 * Pareil Technologies, Inc.</a></p>
 * @author Rakesh Vidyadharan 2008-11-12
 * @since Release 0.3.0
 * @version $Id: SearchSystem.java 23 2008-11-24 19:49:55Z sptrakesh $
 */
abstract class SearchSystem extends ConstraintSystem
{
  private static final long serialVersionUID = 1l;

  /** The index writer instance to use to create/delete documents. */
  protected static transient final IndexWriter writer;

  /** The index reader instance to use to read the index. */
  protected static transient IndexReader reader;

  /** The index searcher instance to use to search documents. */
  protected static transient IndexSearcher searcher;

  /**
   * The name of the field in the indexed document that stores the unique
   * document id.
   */
  static final String DOCUMENT_ID_FIELD = "documentId";

  /**
   * The name of the field in the indexed document that stores the class name.
   *
   * {@value}
   */
  public static final String CLASS_FIELD = "class";

  /**
   * The name of the field in the indexed document that stores the object id.
   *
   * {@value}
   */
  public static final String OBJECT_ID_FIELD = "objectId";

  /**
   * The separator character used to delimit composite index field names.
   *
   * {@value}
   */
  public static final char SEPARATOR_CHAR = '#';

  /**
   * The number or index updates before a {@link
   * org.apache.lucene.index.IndexWriter#commit()} is invoked.  Invoked only
   * in batches to avoid expensive operation on each transaction.
   */
  public static final int SAVE_COUNT =
      PrevalentSystemFactory.getSearchBatchSize();

  /**
   * A counter to ensure that the {@link org.apache.lucene.index.IndexWriter#commit()}
   * method is called periodically.
   */
  private int saveCount;

  /**
   * Initialise {@link #writer}, {@link #reader}, and {@link #searcher}
   * instances.  Register {@link Closer} as a JVM shutdown hook to ensure
   * that the indices are properly optimised and closed on exit.
   */
  static
  {
    try
    {
      writer = new IndexWriter(
          PrevalentSystemFactory.getSearchDirectory( PrevalentObject.class ),
          new StandardAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED );
      reader = IndexReader.open(  NIOFSDirectory.getDirectory(
          PrevalentSystemFactory.getSearchDirectory( PrevalentObject.class ) ),
          true );
      searcher = new IndexSearcher( reader );
      Runtime.getRuntime().addShutdownHook( new Closer() );
    }
    catch ( IOException e )
    {
      throw new RuntimeException( e );
    }
  }

  /**
   * Over-ridden to process search annotations for the object.  If full=text
   * search has been specified, create a {@link
   * org.apache.lucene.document.Document} that holds all the indexed fields
   * in the prevalent object.
   *
   * @param object The prevalent object to add query support for.
   * @throws com.sptci.prevayler.PrevalentException If errors are encountered
   *   while fetching the values of the fields in prevalent object.
   */
  @Override
  protected void index( final PrevalentObject object ) throws PrevalentException
  {
    if ( hasIndices( object ) )
    {
      final Document document = createDocument( object );

      try
      {
        indexFields( object, document );
        indexClass( object, document );
        save( object, document );
      }
      catch ( Exception e )
      {
        throw new PrevalentException( e );
      }
    }

    super.index( object );
  }

  /**
   * Over-ridden to remove the search index for the specified prevalent object.
   *
   * {@inheritDoc}
   */
  @Override
  protected void remove( final PrevalentObject object ) throws PrevalentException
  {
    super.remove( object );

    try
    {
      writer.deleteDocuments(
        new Term( DOCUMENT_ID_FIELD, getDocumentId( object ) ) );
      ++saveCount;

      if ( ( saveCount % SAVE_COUNT ) == 0 ) commit();
    }
    catch ( IOException e )
    {
      throw new PrevalentException( e );
    }
  }

  /**
   * Determine whether the prevalent object has any full-text search indices
   * specified.
   *
   * @param object The prevalent object to check.
   * @return Return <code>true</code> if any search indices have been specified.
   * @throws com.sptci.prevayler.PrevalentException If errors are encountered
   *   while reflecting upon the annotations in the prevalent object.
   */
  private boolean hasIndices( final PrevalentObject object )
      throws PrevalentException
  {
    try
    {
      Searchables indices = object.getClass().getAnnotation( Searchables.class );
      if ( indices != null ) return true;
      Searchable index = object.getClass().getAnnotation( Searchable.class );
      if ( index != null ) return true;

      for ( Field field : ReflectionUtility.fetchFields( object ).values() )
      {
        index = field.getAnnotation( Searchable.class );
        if ( index != null ) return true;
      }
    }
    catch ( Throwable t )
    {
      throw new PrevalentException( t );
    }

    return false;
  }

  /**
   * Create a document to store the full-text indices for the specified
   * object.
   *
   * @see #getDocumentId(PrevalentObject)
   * @param object The prevalent object to index.
   * @return The new document instance.
   */
  private Document createDocument( final PrevalentObject object )
  {
    final Document document = new Document();
    document.add( new org.apache.lucene.document.Field( DOCUMENT_ID_FIELD,
        getDocumentId( object ),
        org.apache.lucene.document.Field.Store.NO,
        org.apache.lucene.document.Field.Index.NOT_ANALYZED ) );
    document.add( new org.apache.lucene.document.Field( CLASS_FIELD,
        object.getClass().getName(), org.apache.lucene.document.Field.Store.YES,
        org.apache.lucene.document.Field.Index.NOT_ANALYZED ) );
    document.add( new org.apache.lucene.document.Field( OBJECT_ID_FIELD,
        object.getObjectId().toString(),
        org.apache.lucene.document.Field.Store.YES,
        org.apache.lucene.document.Field.Index.NOT_ANALYZED ) );
    return document;
  }

  /**
   * Return the unique document id for the index document representing the
   * specified prevalent object.
   *
   * @param object The prevalent object for which the document id is to be
   *   retrieved.
   * @return The unique document identifier.
   */
  private String getDocumentId( final PrevalentObject object )
  {
    return object.getClass().getName() + SEPARATOR_CHAR +
        object.getObjectId().toString();
  }

  /**
   * Add full=text search indices for any fields annotated as searchable in
   * the prevalent object.
   *
   * @param object The prevalent object whose fields are to be indexed.
   * @param doc The document to which indices are to be added.
   * @throws IllegalAccessException If errors are encountered
   *   while reflecting upon the fields in the prevalent object.
   */
  private void indexFields( final PrevalentObject object,
      final Document doc ) throws IllegalAccessException
  {
    for ( Field field : ReflectionUtility.fetchFields( object ).values() )
    {
      final Searchable index = field.getAnnotation( Searchable.class );
      if ( index != null )
      {
        final Object value = field.get( object );
        if ( value != null )
        {
          doc.add( new org.apache.lucene.document.Field( field.getName(),
              value.toString(), org.apache.lucene.document.Field.Store.NO,
              org.apache.lucene.document.Field.Index.ANALYZED ) );
        }
      }
    }
  }

  /**
   * Add full-text search indices for composite indices defined at the
   * class level.  Composite index field names are assigned as a concatenation
   * of the names of the fields that comprise the index if no value is
   * specified for {@link com.sptci.prevayler.annotations.Searchable#name()}.
   * The concatenated names are delimited by the {@link #SEPARATOR_CHAR}.
   *
   * @see #indexSearchables(PrevalentObject, org.apache.lucene.document.Document)
   * @see #indexSearchable(PrevalentObject, org.apache.lucene.document.Document,
   *   com.sptci.prevayler.annotations.Searchable)
   * @param object The prevalent object whose composite fields are to be indexed.
   * @param doc The document to which indices are to be added.
   * @throws IllegalAccessException If errors are encountered
   *   while reflecting upon the fields in the prevalent object.
   */
  private void indexClass( final PrevalentObject object,
      final Document doc ) throws IllegalAccessException
  {
    indexSearchables( object, doc );
    Searchable index = object.getClass().getAnnotation( Searchable.class );
    if ( index != null ) indexSearchable( object, doc, index );
  }

  /**
   * Process the {@link com.sptci.prevayler.annotations.Searchables} annotation
   * for the specified prevalent object class.  Create the composite
   * indices for the specified array of composite indices.
   *
   * @param object The prevalent object whose composite fields are to be indexed.
   * @param doc The document to which indices are to be added.
   * @throws IllegalAccessException If errors are encountered while fetching
   *   the field values of the prevalent object.
   */
  private void indexSearchables( final PrevalentObject object,
      final Document doc ) throws IllegalAccessException
  {
    Searchables indices = object.getClass().getAnnotation( Searchables.class );
    if ( indices != null )
    {
      for ( Searchable index : indices.value() )
      {
        indexSearchable( object, doc, index );
      }
    }
  }

  /**
   * Process the {@link com.sptci.prevayler.annotations.Searchable} annotation
   * for the specified prevalent object class.  Create the composite index
   * for the array of fields.
   *
   * @param object The prevalent object whose composite fields are to be indexed.
   * @param doc The document to which indices are to be added.
   * @param index The searchable index annotation to process.
   * @throws IllegalAccessException If errors are encountered while fetching
   *   the field values of the prevalent object.
   */
  private void indexSearchable( final PrevalentObject object,
      final Document doc, final Searchable index ) throws IllegalAccessException
  {
    final StringBuilder name = new StringBuilder( 64 );
    final StringBuilder value = new StringBuilder( 1024 );
    boolean separator = false;

    for ( String field : index.members() )
    {
      if ( separator ) name.append( SEPARATOR_CHAR );
      name.append( field );

      final Field f = ReflectionUtility.fetchField( field, object );
      final Object v = f.get( object );
      if ( v != null )
      {
        value.append( v.toString() );
        value.append( " " );
      }

      separator = true;
    }

    String fieldName = index.name();
    if ( Searchable.NULL.equals( fieldName ) ) fieldName = name.toString();
    doc.add( new org.apache.lucene.document.Field( fieldName,
        value.toString(), org.apache.lucene.document.Field.Store.NO,
        org.apache.lucene.document.Field.Index.ANALYZED ) );
  }

  /**
   * Save the specified document to the index writer.  Delete an existing
   * document instance if it exists.
   *
   * @see org.apache.lucene.index.IndexWriter#updateDocument(
   *   org.apache.lucene.index.Term, org.apache.lucene.document.Document)
   * @see #commit
   * @param object The prevalent object that is being indexed.
   * @param document The new document to add to the index.
   * @throws java.io.IOException If errors are encountered while saving the
   *   document.
   */
  private void save( final PrevalentObject object,
      final Document document ) throws IOException
  {
    writer.updateDocument(
        new Term( DOCUMENT_ID_FIELD, getDocumentId( object ) ), document );
    ++saveCount;

    if ( ( saveCount % SAVE_COUNT ) == 0 ) commit();
  }

  /**
   * Commit pending writes to the index and refresh the reader.
   *
   * @throws java.io.IOException If errors are encountered while saving the
   *   document.
   */
  private void commit() throws IOException
  {
    writer.commit();
    final IndexReader ir = reader.reopen();

    if ( ir != reader )
    {
      new IndexCloser( reader, searcher ).start();
      reader = ir;
      searcher = new IndexSearcher( reader );
    }

    logger.fine( "Commited search index writes" );
  }

  /**
   * Search the search indices and retrieve all the objects (regardless of type)
   * that match the specified query and return up to the specified number of
   * results.
   *
   * @see #fetch(Class, Object)
   * @param query The query to execute.
   * @param filter The filter to apply to the search.
   * @param count The maximum number of results to return.
   * @param sort The sort fields to apply to the results.
   * @param collection The collection to which matching objects are added.
   * @throws Exception If errors are encountered while reconstituting the
   *   prevalent objects or executing the search.
   */
  protected void search( final Query query, final Filter filter,
      final int count, final Sort sort,
      final Collection<PrevalentObject> collection ) throws Exception
  {
    try
    {
      reader.incRef();
      final TopDocs docs = searcher.search( query, filter, count,
          ( ( sort == null ) ? new Sort() : sort ) );
      for ( ScoreDoc sd : docs.scoreDocs )
      {
        final Document doc = searcher.doc( sd.doc );
        final Class prevalentClass = Class.forName( doc.get( CLASS_FIELD ) );
        final PrevalentObject obj =
            (PrevalentObject) prevalentClass.newInstance();
        collection.add( fetch(
            prevalentClass, obj.getObjectId( doc.get( OBJECT_ID_FIELD ) ) ) );
      }
    }
    finally
    {
      reader.decRef();
    }
  }

  /**
   * A thread used as a shutdown hook to the JVM to close the {@link
   * SearchSystem#writer} and other lucene resources.
   */
  private static class Closer extends Thread
  {
    /**
     * Close the {@link SearchSystem#writer}, {@link SearchSystem#reader}
     * and {@link SearchSystem#searcher} instances.
     */
    @Override
    public void run()
    {
      try
      {
        logger.info( "Optimising search index" );
        writer.optimize();
        writer.close();
        logger.info( "Closed index writer" );

        reader.close();
        searcher.close();
      }
      catch ( IOException e )
      {
        logger.log( Level.WARNING, "Error while closing index writer.", e );
      }
    }
  }
}
