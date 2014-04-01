
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
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;


public class Index {
	
	private static ArrayList<Doc> listDoc;
	static final File INDEX_DIR = new File("../");
	static final String DIR_TO_INDEX = "../20_newsgroups_subset";
	
	static final String DIR_TO_BOW_IVERTED_INDEX = "../bow_InvertedIndex.txt";
	static final String DIR_TO_BG_IVERTED_INDEX = "../bg_InvertedIndex.txt";
	public static int docCcount; 
	public String docId;
	//Mapping of String->Integer (word -> frequency)
	final static  HashMap<String, Integer> dictionary = new HashMap<String, Integer>();
	
	final static  Map <String,String> bow = new HashMap<String,String>();

    final static  Map <String,String> bg = new HashMap<String,String>();
   
    
    final static HashMap<String, List<Posting>> invertedIndex_bow = new HashMap<String, List<Posting>>();
    final static HashMap<String, List<Posting>> invertedIndex_bg = new HashMap<String, List<Posting>>();

    private IndexStrategy strategy;
	public Index(IndexStrategy strg) {
		
		strategy=strg;
		
		listDoc = new ArrayList<Doc>();
		final File docDir = new File(DIR_TO_INDEX);
		if (!docDir.exists() || !docDir.canRead()) {
			System.out.println("Document directory '"+ docDir.getAbsolutePath()+ "' does not exist or is not readable, please check the path");
			System.exit(1);
		}

		Date start = new Date();
		//TODO this part does not work properly. because there are still some empty terms in vocabulary. need to remove those first..
		File indexFile=(strategy== IndexStrategy.BAG_OF_WORDS)?new File(DIR_TO_BOW_IVERTED_INDEX):new File(DIR_TO_BG_IVERTED_INDEX) ;
		if(indexFile.exists())
		{
			System.out.println("The inverted index file already exists on file system. reading index from file ...");// to be more efficient as it is requested in assignment spec.
			readInvertedIndexFile();
			if(strategy== IndexStrategy.BAG_OF_WORDS)
				printInvertedIndex(invertedIndex_bow);
			else
				printInvertedIndex(invertedIndex_bg);
		}
		else{
			
		try {
                createIndex(docDir);
                
                if(strategy==IndexStrategy.BAG_OF_WORDS)
                	saveInvertedIndex( DIR_TO_BOW_IVERTED_INDEX);
                else
                	saveInvertedIndex( DIR_TO_BG_IVERTED_INDEX);


					
				Set<String> strinterm = dictionary.keySet();
		
			    BufferedWriter bowDoc = new BufferedWriter(new FileWriter("../bow.txt"));
		
			    for (Map.Entry<String, String> entry : bow.entrySet()) {
                    bowDoc.write( entry.getKey() +" "+ entry.getValue() + "  \n");
			    	//System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
			    }
			    
				//for(String nome : strinterm){
		//			termdoc.write(nome + "  \n");   
		//		}  
                bowDoc.close();


            BufferedWriter bgDoc = new BufferedWriter(new FileWriter("../bg.txt"));

            for (Map.Entry<String, String> entry : bg.entrySet()) {
                bgDoc.write( entry.getKey() +" "+ entry.getValue() + "  \n");
                //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            }

            //for(String nome : strinterm){
            //			termdoc.write(nome + "  \n");
            //		}
            bgDoc.close();

				
				
				} catch (IOException e) {
				System.out.println(" caught a " + e.getClass()
				+ "\n with message: " + e.getMessage());
				}
		} //end of else if(indexFile.exists())
		Date end = new Date();
		System.out.println(end.getTime() - start.getTime() + " total milliseconds");
		}


	public interface Iterator{
		boolean hasNext();
		Object next();
		void remove();
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
	        
          //  System.out.println("adding " + file + " docID " + docId.substring(24)); //****************


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
					// Remove this line if you want words to be case sensitive
					//currentLine = currentLine.toLowerCase(); --> sami: case sensitivity is handled in Normalizer
					//Iterate through each word of the current line
					//Delimit words based on whitespace, punctuation, and quotes
					final StringTokenizer parser = new StringTokenizer(currentLine, "[_] ([0-9]) [^\\] \t\n\r\f.,;:!?'"); //sami: I removed w from [^\\w]. because what it does is that it splits based on letter 'w' which is not the thing that you meant, right?
						while (parser.hasMoreTokens()) {
						 String currentWord = parser.nextToken();
						 currentWord = tokenString(currentWord);
						 //sami start
						 normalizedStr=Normalizer.normalize(currentWord, true, true, true); // TODO: need to pass parameters
						 if (normalizedStr!=null && normalizedStr!="" ){
						 		bow.put(normalizedStr ,docId.substring(24));	//old: termID.put(currentWord,docId);
						 		addToBowInvertedIndex(normalizedStr ,docId.substring(24));	
						 		System.out.println(normalizedStr +"  "+docId.substring(24));
						 		
						 }
						//sami end
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
            // Remove this line if you want words to be case sensitive
            //currentLine = currentLine.toLowerCase(); --> sami: case sensitivity is handled in Normalizer
            //Iterate through each word of the current line
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

                //sami start
                normalizedStr=Normalizer.normalize(twoterms, true, false, false); // TODO: need to pass parameters. and think obout how to implement stemming and stop word for bi-gram terms
                if (normalizedStr!=null&& normalizedStr!=""){
                	bg.put(normalizedStr ,docId.substring(24));	//old: termID.put(currentWord,docId);
                	addToBgInvertedIndex(normalizedStr ,docId.substring(24));
                	System.out.println(normalizedStr +"  "+docId.substring(24));
                	
                }
                //sami end
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
				List<Posting> postings=new ArrayList<>();
				postings.add(new Posting(docStr));
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
				
				invertedIndex_bow.put(termStr, relatedPostings);
				
				
			}				
    	
    }
    	
    	
    	public void addToBgInvertedIndex(String termStr, String docStr)
    	{
    		if(!invertedIndex_bg.containsKey(termStr)) // if term not exist in inverted index -->add it
			{
				List<Posting> postings=new ArrayList<>();
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
    
    	/*
    	public void writeToFile(String term,String doc)
    	{
    		try{
    			  BufferedWriter bowDoc = new BufferedWriter(new FileWriter("../term_doc.txt",true));
                  bowDoc.write( term+" "+doc + "\n");  			    	  			    
  			 
                  bowDoc.close();
    		} catch (IOException e) {
				 System.out.println(" caught a " + e.getClass()
							+ "\n with message: " + e.getMessage());
			}
    	}
    	*/
}
