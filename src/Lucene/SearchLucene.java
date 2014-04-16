package Lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.ibm.icu.util.StringTokenizer;


public class SearchLucene {

	private SearchLucene() {}


	public static void main(String[] args) throws Exception {


		String index = "indexlucene";
		String field = "contents";
		String topic_fullPath = "../topics/";
		String topicName=null;
		int totalResCount = 100;
		BufferedReader in = null;
		///////////////////////////////////////////////
		topicName="topic1";
		topic_fullPath+=topicName;
		///////////////////////////////////////////////


		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(index)));
		IndexSearcher searcher = new IndexSearcher(reader);

		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_47);

		in = new BufferedReader(new InputStreamReader(new FileInputStream(topic_fullPath), "UTF-8"));

		QueryParser parser = new QueryParser(Version.LUCENE_47, field, analyzer);


		String currentLine;
		String line="";
		StringTokenizer tokenizer;

		//escaping special character, otherwise lucene by default will take this special characters as query arguments
		//http://lucene.apache.org/core/4_0_0/queryparser/org/apache/lucene/queryparser/classic/package-summary.html#Escaping_Special_Characters
		while ((currentLine = in.readLine()) != null) {	        	
			tokenizer= new StringTokenizer(currentLine, "+ - && || ! ( ) { } [ ] ^ \" ~ * ? : \\ /"); 
			while (tokenizer.hasMoreTokens()) {
				String currentWord = tokenizer.nextToken();	                
				line+=currentWord+" ";	      	           
			}
		}



		Query query = parser.parse(line);
		System.out.println("Searching for: " + query.toString(field));


		TopDocs results = searcher.search(query, totalResCount);
		ScoreDoc[] hits = results.scoreDocs;
		int numTotalHits = results.totalHits;
		System.out.println(numTotalHits + " total matching documents");

		int end = Math.min(numTotalHits, totalResCount);

		for (int i = 0; i < end; i++) {

			Document doc = searcher.doc(hits[i].doc);
			String path = doc.get("path");
			
			if (path != null) {
				System.out.println(topicName + " Q0 "+ path.substring(21) + " " + (i+1) + " " +  hits[i].score  + " group09_experiment2 "  );//String.format("%.7f",hits[i].score)
			}
			else {
				System.out.println((i+1) + ". " + "No path for this document");
			}

		}

		in.close();
		reader.close();
	}


}
