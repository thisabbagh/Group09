
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;


public class Index {
	
	
	static final String DIR_TO_INDEX = "../20_newsgroups_subset";
	public static int docCcount; 
	public String docId;

	//Mapping of String->Integer (word -> frequency)
	final static  HashMap<String, Integer> dictionary = new HashMap<String, Integer>();
	
	final static  Map <String,ArrayList<String> > index = new HashMap<String,ArrayList<String> >();

    final static  Map <String,ArrayList<String>> bg = new HashMap<String,ArrayList<String> >();

	public Index() {
	
		final File docDir = new File(DIR_TO_INDEX);
		if (!docDir.exists() || !docDir.canRead()) {
			System.out.println("Document directory '"+ docDir.getAbsolutePath()+ "' does not exist or is not readable, please check the path");
			System.exit(1);
		}

		Date start = new Date();
		try {

                createIndex(docDir);
				Set<String> strinterm = dictionary.keySet();
		
			    BufferedWriter bowDoc = new BufferedWriter(new FileWriter("../bow.txt"));
		
			    for (Map.Entry<String, ArrayList<String>> entry : index.entrySet()) {
                    bowDoc.write( entry.getKey() +" "+ entry.getValue() + "  \n");
			  //  	System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
			    }
			    
				//for(String nome : strinterm){
		//			termdoc.write(nome + "  \n");   
		//		}  
                bowDoc.close();


            BufferedWriter bgDoc = new BufferedWriter(new FileWriter("../bg.txt"));

            for (Map.Entry<String, ArrayList<String>> entry : bg.entrySet()) {
                bgDoc.write( entry.getKey() +" "+ entry.getValue() + "  \n");
                //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            }

            //for(String nome : strinterm){
            //			termdoc.write(nome + "  \n");
            //		}
            bgDoc.close();

				Date end = new Date();
				System.out.println(end.getTime() - start.getTime() + " total milliseconds");
				
				} catch (IOException e) {
				System.out.println(" caught a " + e.getClass()
				+ "\n with message: " + e.getMessage());
				}
		
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
            // if parametro um ou outro
            createTermsBOW(file);
   //         createTermsBG(file);
            System.out.println("adding " + file + " docID " + docId.substring(24));

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
					final StringTokenizer parser = new StringTokenizer(currentLine, "[_] ([0-9]) [^\\w] \t\n\r\f.,;:!?'");
						while (parser.hasMoreTokens()) {
						 String currentWord = parser.nextToken();
						 currentWord = tokenString(currentWord);
						 //sami start
						  normalizedStr=Normalizer.normalize(currentWord, true, true, true); // TODO: need to pass parameters

                            if (normalizedStr != null && !normalizedStr.isEmpty() ){

                            if (index.containsKey(normalizedStr)){

                               ArrayList<String> temp =  index.get(normalizedStr);
                               temp.add(docId);
                               index.put(normalizedStr, temp);
                           }
                           else    {
                               ArrayList<String> temp = new ArrayList<String>();
                               temp.add(docId);
                                index.put(normalizedStr ,temp);
                            }
                            }
                           // listDoc.add(docId);
						 //	bow.put(normalizedStr ,docId.substring(24));	//old: termID.put(currentWord,docId);
                            	//old: termID.put(currentWord,docId);

                            //sami end
						}
							
			}
		in.close();
	}


    //create index Bi-Gram Index
   /* private  void createTermsBG(File file) throws IOException {

        String normalizedStr=null;

        BufferedReader in = new BufferedReader(new FileReader(file));
        String currentLine;
        while ((currentLine = in.readLine()) != null) {
            // Remove this line if you want words to be case sensitive
            //currentLine = currentLine.toLowerCase(); --> sami: case sensitivity is handled in Normalizer
            //Iterate through each word of the current line
            //Delimit words based on whitespace, punctuation, and quotes
            final StringTokenizer parser = new StringTokenizer(currentLine, "[_] ([0-9]) [^\\w] \t\n\r\f.,;:!?'");
            String twoterms = "";
            String currentWord;
            int count = 1;
            while (parser.hasMoreTokens()) {
                 if (count == 2){
                currentWord = parser.nextToken();
                currentWord = tokenString(currentWord);
                twoterms = twoterms + " "+ currentWord;

                //sami start
                normalizedStr=Normalizer.normalize(twoterms, true, true, true); // TODO: need to pass parameters
                if (normalizedStr!=null);
             //   bg.put(normalizedStr ,docId.substring(24));	//old: termID.put(currentWord,docId);
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
    }                   */

    private static void createDictionary(File file) throws IOException {

        String normalizedStr=null;
        BufferedReader in = new BufferedReader(new FileReader(file));
        String currentLine;
        while ((currentLine = in.readLine()) != null) {
          final StringTokenizer parser = new StringTokenizer(currentLine, "[_] ([0-9]) [^\\w] \t\n\r\f.,;:!?'");
            while (parser.hasMoreTokens()) {
                String currentWord = parser.nextToken();
                currentWord = tokenString(currentWord);
                normalizedStr=Normalizer.normalize(currentWord, true, true, true);
                Integer frequency = dictionary.get(normalizedStr);
                if (frequency == null){
                    frequency = 0;
                    dictionary.put(normalizedStr, frequency);
                } else
                    dictionary.put(normalizedStr, frequency + 1);
            }

        }
        in.close();
    }


}
