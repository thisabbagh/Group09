
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;


public class Index {
	
	private static ArrayList<Doc> listDoc;
	static final File INDEX_DIR = new File("../");
	static final String DIR_TO_INDEX = "../20_newsgroups_subset";
	public static int docCcount; 
	public int docId;
	//Mapping of String->Integer (word -> frequency)
	final static  HashMap<String, Integer> dictionary = new HashMap<String, Integer>();
	
	final static  Map <String,Integer> termID = new HashMap<String,Integer>();

	public Index() {
		listDoc = new ArrayList<Doc>();
		final File docDir = new File(DIR_TO_INDEX);
		if (!docDir.exists() || !docDir.canRead()) {
			System.out.println("Document directory '"+ docDir.getAbsolutePath()+ "' does not exist or is not readable, please check the path");
			System.exit(1);
		}

		Date start = new Date();
		try {
			docId = 1;
				createTems(docDir);
				Set<String> strinterm = dictionary.keySet();
		
			    BufferedWriter termdoc = new BufferedWriter(new FileWriter("../term.txt"));
		
			    for (Map.Entry<String, Integer> entry : termID.entrySet()) {
			    	termdoc.write( entry.getKey() +" "+ entry.getValue() + "  \n");   
			    	//System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
			    }
			    
				//for(String nome : strinterm){
		//			termdoc.write(nome + "  \n");   
		//		}  
				termdoc.close();
			
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

	public void createTems(File file) throws IOException {
		
		if (file.canRead()) {
		if (file.isDirectory()) {
		String[] files = file.list();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {		
			createTems( new File(file, files[i]));
			}
			}
			} else {
				
			createDictionary(file);
			System.out.println("adding " + file + " docID " + docId );
			docId = docId + 1;
	}
	}
	}

	public static String tokenString( String currentWord){
		currentWord = currentWord.replaceAll("[^\\w]"," ");
	 	currentWord = currentWord.replaceAll("[_]"," ");
	 	currentWord = currentWord.replaceAll("[0-9]", " ");
	 	currentWord.trim();
	 	currentWord.split(currentWord);
	return currentWord;
	}

	private  void createDictionary(File file) throws IOException {
	
		BufferedReader in = new BufferedReader(new FileReader(file));
		String currentLine;
			while ((currentLine = in.readLine()) != null) {
					// Remove this line if you want words to be case sensitive
					currentLine = currentLine.toLowerCase();
					//Iterate through each word of the current line
					//Delimit words based on whitespace, punctuation, and quotes
					final StringTokenizer parser = new StringTokenizer(currentLine, "[_] ([0-9]) [^\\w] \t\n\r\f.,;:!?'");
						while (parser.hasMoreTokens()) {
						 String currentWord = parser.nextToken();
						 currentWord = tokenString(currentWord);
						 termID.put(currentWord,docId);		
						
						}
							
			}
		in.close();
	}


}
