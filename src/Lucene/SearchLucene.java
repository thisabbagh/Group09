package Lucene;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

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
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.ibm.icu.util.StringTokenizer;

enum SimilarityMode { DEFAULT , BM25 , BM25L }

public class SearchLucene {

	private SearchLucene() {}

	static ArrayList<String> listQuery = new ArrayList<String>();
	
	static SimilarityMode MODE; // change this line to get results for different similarity score calculations
	
	
	public static void main(String[] args) throws Exception {

        System.out.println("Please select the Similarty Mode: ");

        System.out.println("1. Lucene Default");
        System.out.println("2. BM25");
        System.out.println("3. BM25L");

        Scanner scan = new Scanner(System.in);
        int input = scan.nextInt();

        //selects the option that the user input
        switch (input)
        {
            case 1:
                System.out.println("Lucene Default");
                MODE= SimilarityMode.DEFAULT;
                break;
            case 2:
                System.out.println("BM25");
                MODE= SimilarityMode.BM25;
                break;
            case 3:
                System.out.println("BM25L!");
                MODE= SimilarityMode.BM25L;
                break;
            default:
                System.out.println("not a valid option. Exiting ...");
                System.exit(0);
                break;

        }

		String index = "indexlucene";
		String field = "contents";
		String topic_fullPath = null;
		String topicName=null;
		int totalResCount = 100;
		BufferedReader in = null;
		

		
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(index)));
		IndexSearcher searcher = new IndexSearcher(reader);
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_47);
		QueryParser parser = new QueryParser(Version.LUCENE_47, field, analyzer);
		
switch (MODE)
{
	case BM25:
		System.out.println("BM25 score calculator will be used");
		searcher.setSimilarity(new BM25Similarity());
		break;
		
	case BM25L:
		System.out.println("BM25L score calculator will be used");
		searcher.setSimilarity(new BM25LSimilarity());
		break;
		
	case DEFAULT:
		System.out.println("Lucene's default score calculator will be used");
		break;
		
}


		
        ArrayList<String> topicList = new ArrayList<String>();

        topicList.add("topic1");
        topicList.add("topic10");
        topicList.add("topic11");
        topicList.add("topic13");
        topicList.add("topic14");
        topicList.add("topic15");
        topicList.add("topic16");
        topicList.add("topic17");
        topicList.add("topic18");
        topicList.add("topic19");
        topicList.add("topic2");
        topicList.add("topic3");
        topicList.add("topic4");
        topicList.add("topic5");
        topicList.add("topic6");
        topicList.add("topic7");
        topicList.add("topic9");

        for(String topic : topicList)
        {
        	
		topicName=topic;
		topic_fullPath= "../topics/"+topicName;

		in = new BufferedReader(new InputStreamReader(new FileInputStream(topic_fullPath), "UTF-8"));
		
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
		//System.out.println("Searching for: " + query.toString(field));


		TopDocs results = searcher.search(query, totalResCount);
		ScoreDoc[] hits = results.scoreDocs;
		int numTotalHits = results.totalHits;
		System.out.println(numTotalHits + " total matching documents");

		int end = Math.min(numTotalHits, totalResCount);

		for (int i = 0; i < end; i++) {

			Document doc = searcher.doc(hits[i].doc);
			String path = doc.get("path");
			
			if (path != null) {
				System.out.println(topicName + " Q0 "+ path.substring(24) + " " + (i+1) + " " +  hits[i].score  + " group09_experiment2 "  );//String.format("%.7f",hits[i].score)
				listQuery.add(topicName + " Q0 "+ path.substring(24) + " " + (i+1) + " " +  hits[i].score  + " group09_experiment2 " );
			}
			else {
				System.out.println((i+1) + ". " + "No path for this document");
			}

		}

		
       
        
        
		in.close();
		
	}

    reader.close();
        
        printQuery();
        
 }
	
	public static void printQuery(){

        try {

            BufferedWriter topicDoc = null;

            topicDoc = new BufferedWriter(new FileWriter("../queryResult_"+ MODE +".txt"));

            for (String print : listQuery) {
                topicDoc.write(print + "\n");
            }

            topicDoc.close();


         } catch (IOException e) {
            e.printStackTrace();
        }

    }
	
}
