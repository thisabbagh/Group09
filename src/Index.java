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
    public String docId;
    //Mapping of String->Integer (word -> frequency)
    final static HashMap<String, Integer> dictionary = new HashMap<String, Integer>();

    final static Map <String,String> bow = new HashMap<String,String>();

    final static Map <String,String> bg = new HashMap<String,String>();

    public Index() {
        listDoc = new ArrayList<Doc>();
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

            for (Map.Entry<String, String> entry : bow.entrySet()) {
                bowDoc.write( entry.getKey() +" "+ entry.getValue() + " \n");
//System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            }

//for(String nome : strinterm){
// termdoc.write(nome + " \n");
// }
            bowDoc.close();


            BufferedWriter bgDoc = new BufferedWriter(new FileWriter("../bg.txt"));

            for (Map.Entry<String, String> entry : bg.entrySet()) {
                bgDoc.write( entry.getKey() +" "+ entry.getValue() + " \n");
                //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            }

            //for(String nome : strinterm){
            // termdoc.write(nome + " \n");
            // }
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
                createTermsBOW(file);
                createTermsBG(file);
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
    private void createTermsBOW(File file) throws IOException {

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
                if (normalizedStr!=null);
                bow.put(normalizedStr ,docId.substring(24));	//old: termID.put(currentWord,docId);
//sami end
            }

        }
        in.close();
    }


    //create index Bi-Gram Index
    private void createTermsBG(File file) throws IOException {

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
                    bg.put(normalizedStr ,docId.substring(24));	//old: termID.put(currentWord,docId);
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


}


