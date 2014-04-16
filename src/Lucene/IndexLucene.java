package Lucene;

import java.io.*;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import org.apache.lucene.document.*;

import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.pdfbox.searchengine.lucene.LucenePDFDocument;

public class IndexLucene {

	static final File INDEX_DIR = new File("indexlucene"); //\\test
	static final String DIR_TO_INDEX = "../20_newsgroups_subset";

	public IndexLucene() {


        try {
          Directory dir = FSDirectory.open(new File("indexlucene"));



        final File docDir = new File(DIR_TO_INDEX);
		if (!docDir.exists() || !docDir.canRead()) {
			System.out
					.println("Document directory '"
							+ docDir.getAbsolutePath()
							+ "' does not exist or is not readable, please check the path");
			System.exit(1);
		}

		Date start = new Date();

            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_47);
		//	IndexWriter writer = new IndexWriter(FSDirectory.open(INDEX_DIR),
          //          new IndexWriterConfig(Version.LUCENE_47, analyzer));

			   IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_47, analyzer);
            indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            IndexWriter writer = new IndexWriter(dir,indexWriterConfig);

		    System.out.println("Indexing to directory '" + INDEX_DIR + "'...");
			indexDocs(writer, docDir);
			System.out.println("Optimizing...");
			//writer.optimize();
			writer.close();

			Date end = new Date();
			System.out.println(end.getTime() - start.getTime()
					+ " total milliseconds");

		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass()
                    + "\n with message: " + e.getMessage());

        }
	}

	static void indexDocs(IndexWriter writer, File file) throws IOException {

        // do not try to index files that cannot be read
        if (file.canRead()) {
            if (file.isDirectory()) {
                String[] files = file.list();
                // an IO error could occur
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        indexDocs(writer, new File(file, files[i]));
                    }
                }
            } else {

                FileInputStream fis;
                try {
                    fis = new FileInputStream(file);
                } catch (FileNotFoundException fnfe) {
                    // at least on windows, some temporary files raise this exception with an "access denied" message
                    // checking if the file can be read doesn't help
                    return;
                }

                try {

                    // make a new, empty document
                    Document doc = new Document();

                    // Add the path of the file as a field named "path".  Use a
                    // field that is indexed (i.e. searchable), but don't tokenize
                    // the field into separate words and don't index term frequency
                    // or positional information:
                    Field pathField = new StringField("path", file.getPath(), Field.Store.YES);
                    doc.add(pathField);

                    // Add the last modified date of the file a field named "modified".
                    // Use a LongField that is indexed (i.e. efficiently filterable with
                    // NumericRangeFilter).  This indexes to milli-second resolution, which
                    // is often too fine.  You could instead create a number based on
                    // year/month/day/hour/minutes/seconds, down the resolution you require.
                    // For example the long value 2011021714 would mean
                    // February 17, 2011, 2-3 PM.
                    doc.add(new LongField("modified", file.lastModified(), Field.Store.NO));

                    // Add the contents of the file to a field named "contents".  Specify a Reader,
                    // so that the text of the file is tokenized and indexed, but not stored.
                    // Note that FileReader expects the file to be in UTF-8 encoding.
                    // If that's not the case searching for special characters will fail.
                    doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(fis, "UTF-8"))));

                    if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
                        // New index, so we just add the document (no old document can be there):
                        System.out.println("adding " + file);
                        writer.addDocument(doc);
                    } else {
                        // Existing index (an old copy of this document may have been indexed) so
                        // we use updateDocument instead to replace the old one matching the exact
                        // path, if present:
                        System.out.println("updating " + file);
                        writer.updateDocument(new Term("path", file.getPath()), doc);
                    }

                } finally {
                    fis.close();
                }
            }
        }
        /*FileInputStream fis;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException fnfe) {
            return;
        }

        Document doc = new Document();
        Field pathField = new StringField("path", file.getPath(), Field.Store.YES);
        doc.add(pathField);

        doc.add(new LongField("modified", file.lastModified(), Field.Store.NO));

        doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(fis, "UTF-8"))));

        if (file.canRead()) {
			if (file.isDirectory()) {
				String[] files = file.list();
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						indexDocs(writer, new File(file, files[i]));
					}
				}
			} else {
				System.out.println("adding " + file);
				try {
					if (!file.getPath().endsWith(".svn-base")) {
						if (file.getPath().endsWith(".pdf")) {
							addPDFToIndex(writer, file);
						} else {
							writer.addDocument(doc);
						}
					}
				} catch (FileNotFoundException fnfe) {

				}
			}
		}                */
	}

	public static void addPDFToIndex(IndexWriter openIndex, File pdfFile)
			throws IOException {
		Document document = LucenePDFDocument.getDocument(pdfFile);
		openIndex.addDocument(document);
	}

    public static void main(String[] args) throws Exception {

        IndexLucene createindex = new IndexLucene();

    }

}
