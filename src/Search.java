import java.io.*;
import java.util.*;

public class Search {


    static final String DIR_TO_INDEX = "../topics";

    final static Map<String,Integer > topic = new HashMap<String,Integer >();

    public Search(String topicNumber) {

        final File docDir = new File(DIR_TO_INDEX);
        if (!docDir.exists() || !docDir.canRead()) {
            System.out.println("Document directory '"+ docDir.getAbsolutePath()+ "' does not exist or is not readable, please check the path");
            System.exit(1);
        }

        Date start = new Date();
        try {

            createIndex(docDir, topicNumber);

            BufferedWriter topicDoc = new BufferedWriter(new FileWriter("../topic.txt"));

            for (Map.Entry<String, Integer> entry : topic.entrySet()) {
                topicDoc.write( entry.getKey() +" "+ entry.getValue() + "  \n");
                //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            }


            topicDoc.close();

            Date end = new Date();
            System.out.println(end.getTime() - start.getTime() + " total milliseconds");

        } catch (IOException e) {
            System.out.println(" caught a " + e.getClass()
                    + "\n with message: " + e.getMessage());
        }

    }

    public void createIndex(File file , String topicNumber) throws IOException {

        if (file.canRead()) {
            if (file.isDirectory()) {
                String[] files = file.list();
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        createIndex(new File(file, files[i]),topicNumber);
                    }
                }
            } else {

              if (topicNumber.equals(file.toString().substring(10))){
                  createDictionary(file);
                System.out.println("adding " + topicNumber );
                }

            }
        }
    }

    public static void main(String[] args) {
        String topicNumber = "topic1";
        Search i = new Search(topicNumber);


    }

    public static String tokenString( String currentWord){
        currentWord = currentWord.replaceAll("[^\\w]","");
        currentWord = currentWord.replaceAll("[_]","");
        currentWord = currentWord.replaceAll("[0-9]", "");
        currentWord.trim();
        currentWord.split(currentWord);
        return currentWord;
    }

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
                Integer frequency = topic.get(normalizedStr);
                if (frequency == null){
                    frequency = 1;
                    topic.put(normalizedStr, frequency);
                } else
                    topic.put(normalizedStr, frequency + 1);
            }

        }
        in.close();
    }



}
