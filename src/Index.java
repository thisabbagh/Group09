
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeMap;

/*
 * a class responisble for creating inverted index files and structures
 */
public class Index {


	static final String DIR_TO_INDEX = "../20_newsgroups_subset";
	static final String DIR_TO_BOW_IVERTED_INDEX = "../bow_InvertedIndex.txt";
	static final String DIR_TO_BG_IVERTED_INDEX = "../bg_InvertedIndex.txt";

	public String docId;


    final static HashMap<String, List<Posting>> invertedIndex_bow = new HashMap<String, List<Posting>>();
    final static HashMap<String, List<Posting>> invertedIndex_bg = new HashMap<String, List<Posting>>();
    final static HashMap<String, Integer> docLength = new HashMap<String, Integer>();

    private IndexStrategy strategy;
    boolean doCaseFold;
    boolean doStopWords;
    boolean doStemming;
	public Index(IndexStrategy strg,boolean adoCaseFold,boolean adoStopWords,boolean adoStemming) {
		
		strategy=strg;
		
	    doCaseFold=adoCaseFold;
	    doStopWords=adoStopWords;
	    doStemming=adoStemming;

		final File docDir = new File(DIR_TO_INDEX);
		if (!docDir.exists() || !docDir.canRead()) {
			System.out.println("Document directory '"+ docDir.getAbsolutePath()+ "' does not exist or is not readable, please check the path");
			System.exit(1);
		}

       Date start = new Date();
            
          /*  File indexFile=(strategy== IndexStrategy.BAG_OF_WORDS)?new File(DIR_TO_BOW_IVERTED_INDEX):new File(DIR_TO_BG_IVERTED_INDEX) ;
            if(indexFile.exists())
            {
                System.out.println("The inverted index file already exists on file system. reading index from file ...");// to be more efficient as it is requested in assignment spec.
                readInvertedIndexFile();
                if(strategy== IndexStrategy.BAG_OF_WORDS)
                    printInvertedIndex(invertedIndex_bow);
                else
                    printInvertedIndex(invertedIndex_bg);
            }
            else{*/
			
		try {
                createIndex(docDir);
                
                if(strategy==IndexStrategy.BAG_OF_WORDS)
                	saveInvertedIndex( DIR_TO_BOW_IVERTED_INDEX);
                else
                	saveInvertedIndex( DIR_TO_BG_IVERTED_INDEX);

                saveDocsLength();
                saveNormalizationOptions();
                
				} catch (IOException e) {
				System.out.println(" caught a " + e.getClass()
				+ "\n with message: " + e.getMessage());
				}
		//} //end of else if(indexFile.exists())
		Date end = new Date();
		System.out.println(end.getTime() - start.getTime() + " total milliseconds");
		}

	public void createIndex(File file) throws IOException {
		
		if (file.canRead()) {
		if (file.isDirectory()) {
		String[] files = file.list();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
            createIndex(new File(file, files[i]));
			}
			}
			} else {
	        docId = file.toString();
	        
	        if(strategy==IndexStrategy.BAG_OF_WORDS)
	        	createTermsBOW(file);
	        else
	        	createTermsBG(file);


	}
	}
	}

	public static String tokenString( String currentWord){
		currentWord = currentWord.replaceAll("[^\\w]","");
	 	currentWord = currentWord.replaceAll("[_]","");
	 	currentWord = currentWord.replaceAll("[0-9]", "");
	 	currentWord.trim();
	 	currentWord.split(currentWord);
	return currentWord;
	}


    //create index Bag of Words
	private  void createTermsBOW(File file) throws IOException {

		String normalizedStr=null;
		
		BufferedReader in = new BufferedReader(new FileReader(file));
		String currentLine;
			while ((currentLine = in.readLine()) != null) {
					//Delimit words based on whitespace, punctuation, and quotes
					final StringTokenizer parser = new StringTokenizer(currentLine, "[_] ([0-9]) [^\\] \t\n\r\f.,;:!?'"); 
						while (parser.hasMoreTokens()) {
						 String currentWord = parser.nextToken();
						 currentWord = tokenString(currentWord);

					 		if(!docLength.containsKey(docId.substring(24)))
					 			docLength.put(docId.substring(24),1);
					 		else
					 			docLength.put(docId.substring(24),docLength.get(docId.substring(24))+1);
						 
						 normalizedStr=Normalizer.normalize(currentWord,doCaseFold,doStopWords,doStemming); 
						 if (normalizedStr!=null && normalizedStr!="" ){
						 		addToBowInvertedIndex(normalizedStr ,docId.substring(24));	
						 		System.out.println(normalizedStr +"  "+docId.substring(24));
						 		

						 		
						 }

						}
							
			}
		in.close();
	}


    //create index Bi-Gram Index
    private  void createTermsBG(File file) throws IOException {

        String normalizedStr=null;

        BufferedReader in = new BufferedReader(new FileReader(file));
        String currentLine;
        while ((currentLine = in.readLine()) != null) {
            //Delimit words based on whitespace, punctuation, and quotes
            final StringTokenizer parser = new StringTokenizer(currentLine, "[_] ([0-9]) [^\\] \t\n\r\f.,;:!?'");
            String twoterms = "";
            String currentWord;
            int count = 1;
            while (parser.hasMoreTokens()) {
                 if (count == 2){
                currentWord = parser.nextToken();
                currentWord = tokenString(currentWord);
                twoterms = twoterms + " "+ currentWord;

		 		if(!docLength.containsKey(docId.substring(24)))
		 			docLength.put(docId.substring(24),1);
		 		else
		 			docLength.put(docId.substring(24),docLength.get(docId.substring(24))+1);
                
                normalizedStr=Normalizer.normalize(twoterms, doCaseFold,doStopWords,doStemming);
                if (normalizedStr!=null&& normalizedStr!=""){
                	addToBgInvertedIndex(normalizedStr ,docId.substring(24));
                	System.out.println(normalizedStr +"  "+docId.substring(24));
                	
                }

                count = 1;
                }
                else {
                      currentWord = parser.nextToken();
                      currentWord = tokenString(currentWord);
                      twoterms = currentWord;
                     count = count + 1;

                  }

            }

        }
        in.close();
    }


    	public void addToBowInvertedIndex(String termStr, String docStr)
    	{
    		if(!invertedIndex_bow.containsKey(termStr)) // if term not exist in inverted index -->add it
			{
				List<Posting> postings=new ArrayList<Posting>();
				postings.add(new Posting(docStr));
                if (!postings.isEmpty())
				invertedIndex_bow.put(termStr, postings);
				
			}
			else //term already exists in inverted index --> checks if the doc (posting) does not exist-->add it. if doc already exist-->increment freq of the doc
			{ 
				List<Posting> relatedPostings=invertedIndex_bow.get(termStr);
				boolean alreadyExist=false;
				for (Posting p : relatedPostings)
				{
					if(docStr.equals(p.getDocName()))//posting already exist
					{
						alreadyExist=true;
						p.increaseFreq();
						break;
								
					}	
	
				}
				
				if(!alreadyExist)
				{
					relatedPostings.add(new Posting(docStr));
				}

                if (!relatedPostings.isEmpty())
				invertedIndex_bow.put(termStr, relatedPostings);
				
				
			}				
    	
    }
    	
    	
    	public void addToBgInvertedIndex(String termStr, String docStr)
    	{
    		if(!invertedIndex_bg.containsKey(termStr)) // if term not exist in inverted index -->add it
			{
				List<Posting> postings=new ArrayList<Posting>();
				postings.add(new Posting(docStr));
				invertedIndex_bg.put(termStr, postings);
				
			}
			else //term already exists in inverted index --> checks if the doc (posting) does not exist-->add it. if doc already exist-->increment freq of the doc
			{ 
				List<Posting> relatedPostings=invertedIndex_bg.get(termStr);
				boolean alreadyExist=false;
				for (Posting p : relatedPostings)
				{
					if(docStr.equals(p.getDocName()))//posting already exist
					{
						alreadyExist=true;
						p.increaseFreq();
						break;
								
					}	
	
				}
				
				if(!alreadyExist)
				{
					relatedPostings.add(new Posting(docStr));
				}
				
				invertedIndex_bg.put(termStr, relatedPostings);
				
				
			}				
    	
    }
    	
    	
    	public void saveInvertedIndex( String fileName){
    		// to sort inverted index we can use treeMap. but with our implementation sorting is not necessary

    		Map <String, List<Posting>> sortedIndex=(strategy== IndexStrategy.BAG_OF_WORDS)? new TreeMap<String, List<Posting>>(invertedIndex_bow):new TreeMap<String, List<Posting>>(invertedIndex_bg);
    		  try {
    		 BufferedWriter bowDoc = new BufferedWriter(new FileWriter(fileName));
    			
			    for (Entry<String, List<Posting>> entry : sortedIndex.entrySet()) {
					bowDoc.write( entry.getKey() +" ");
					List<Posting> relatedPostings=entry.getValue();
					
					for (Posting p : relatedPostings)
					{
						bowDoc.write(p+" ");
					}
			
					bowDoc.write("\n");
			    }
			    
			    bowDoc.close();
			    
    			} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
             
    	}
    	
    	/*
    	 * reads generated index files from file system.
    	 * useful when we have already generated the file, therefore there is no need to parse all the collection again
    	 */
    	public void readInvertedIndexFile()
    	{
    		String fileName=(strategy== IndexStrategy.BAG_OF_WORDS)?DIR_TO_BOW_IVERTED_INDEX:DIR_TO_BG_IVERTED_INDEX;
    			
    		 try {
        		 BufferedReader idxDoc = new BufferedReader(new FileReader(fileName));
        			
        		 String currentLine;
        		 String retrievedTerm;
        		 String possibleNextTerm;
        		 List<Posting> postings = null;
     			while ((currentLine = idxDoc.readLine()) != null) {
     				retrievedTerm=null;
     				postings=new ArrayList<Posting>();
     					final StringTokenizer parser = new StringTokenizer(currentLine);
     						while(parser.hasMoreTokens()) {
     							String currentWord = parser.nextToken();
     							//System.out.println("currentWord: "+currentWord);
     							if(retrievedTerm==null)
     								if(strategy== IndexStrategy.BAG_OF_WORDS){
     									retrievedTerm=currentWord;
     									currentWord = parser.nextToken();
     									//System.out.println("currentWord.next: "+currentWord);   						
     								}
     								else
     								{
     									retrievedTerm=currentWord;
     									currentWord = parser.nextToken();
     									if(!currentWord.startsWith("<")){
     										retrievedTerm=retrievedTerm+" "+currentWord;
     										currentWord = parser.nextToken();
     										}
     								
     								}
     						 
     						 
						
     						final StringTokenizer postingParser= new StringTokenizer(currentWord,"<,>");
     						while (postingParser.hasMoreTokens()) {
     							String retDoc = postingParser.nextToken();
     							String retFreq = postingParser.nextToken();
     							
     							postings.add(new Posting(retDoc,Integer.parseInt(retFreq)));
     							//System.out.println("doc: "+retDoc+" freq: "+retFreq);
     						}
     						}
     						
     						if(strategy==IndexStrategy.BAG_OF_WORDS)
     							invertedIndex_bow.put(retrievedTerm, postings);
     						else
     							invertedIndex_bg.put(retrievedTerm, postings);
     						
     			}
     						
     						
     			
    			    
     						idxDoc.close();
     						//System.out.println("printing invertedindex");
     						//printInvertedIndex(invertedIndex_bow);
     			}			
        			 catch (IOException e) {
        				 System.out.println(" caught a " + e.getClass()
        							+ "\n with message: " + e.getMessage());
    				}
                 
    	}
    	
    	
    	
    	/*
    	 * Prints inverted index hashmap on screen
    	 * 
    	 */
    	public void printInvertedIndex(Map <String, List<Posting>> indexMap){
    		
    		//TODO remove count
    		System.out.println("printing a small subset of map ... ");
    			int count=0; // just to limit the number of outputs
			    for (Entry<String, List<Posting>> entry : indexMap.entrySet()) {
               count++;
               if (count==20)
            	   break;
			    	System.out.print( entry.getKey() +" ");
					List<Posting> relatedPostings=entry.getValue();
					
					for (Posting p : relatedPostings)
					{
						System.out.print(p+" ");
					}
			
					System.out.println("");
			    }
			    
       
    	}
    	
    	
    	public void saveDocsLength(){
    		
    		  try {
    		 BufferedWriter bowDoc = new BufferedWriter(new FileWriter("../docsLength.txt"));
    			
			    for (Entry<String, Integer> entry : docLength.entrySet()) {
					bowDoc.write( entry.getKey() +" "+ entry.getValue());
					bowDoc.write("\n");
			    }
			    
			    bowDoc.close();
			    
    			} catch (IOException e) {
    				System.out.println(" caught a " + e.getClass()
							+ "\n with message: " + e.getMessage());
				}
             
    	}
    	
    	public void saveNormalizationOptions(){
    		 try {
        		 BufferedWriter bowDoc = new BufferedWriter(new FileWriter("../NormOptions.txt"));
        			
    			   
    					bowDoc.write(doCaseFold?"true":"false");
    					bowDoc.write("\n");
    					
    					bowDoc.write(doStopWords?"true":"false");
    					bowDoc.write("\n");
    					
    					bowDoc.write(doStemming?"true":"false");
    					bowDoc.write("\n");
    			    
    			    bowDoc.close();
    			    
        			} catch (IOException e) {
        				System.out.println(" caught a " + e.getClass()
    							+ "\n with message: " + e.getMessage());
    				}
    	}
    

}
