package Lucene;

import java.io.*;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.pdfbox.searchengine.lucene.LucenePDFDocument;

public class IndexLucene {

	static final File INDEX_DIR = new File("indexlucene"); //\\test
	static final String DIR_TO_INDEX = "../20_newsgroups_subset";

	public IndexLucene() {

		final File docDir = new File(DIR_TO_INDEX);
		if (!docDir.exists() || !docDir.canRead()) {
			System.out
					.println("Document directory '"
							+ docDir.getAbsolutePath()
							+ "' does not exist or is not readable, please check the path");
			System.exit(1);
		}

		Date start = new Date();
		try {
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_47);
			IndexWriter writer = new IndexWriter(FSDirectory.open(INDEX_DIR),
                    new IndexWriterConfig(Version.LUCENE_47, analyzer));
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

        FileInputStream fis;
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
					;
				}
			}
		}
	}

	public static void addPDFToIndex(IndexWriter openIndex, File pdfFile)
			throws IOException {
		Document document = LucenePDFDocument.getDocument(pdfFile);
		openIndex.addDocument(document);
	}

    public static void main(String[] args) throws Exception {

        IndexLucene createindex = new IndexLucene();

        System.out.println("Lucene");

    }

}
