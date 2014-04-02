import java.io.*;
import java.util.*;

public class Search {


    static final String DIR_TO_INDEX = "../topics";

    static final String DIR_TO_BOW_IVERTED_INDEX = "../bow_InvertedIndex.txt";
    static final String DIR_TO_BG_IVERTED_INDEX = "../bg_InvertedIndex.txt";

    final static Map<String,Integer > topic = new HashMap<String,Integer >();


    final static HashMap<String, List<Posting>> invertedIndex_bow = new HashMap<String, List<Posting>>();
    final static HashMap<String, List<Posting>> invertedIndex_bg = new HashMap<String, List<Posting>>();


    final static HashMap< Float, String > scoreMap = new HashMap<Float, String>();


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

               if (normalizedStr != null && !normalizedStr.isEmpty() ){
                Integer frequency = topic.get(normalizedStr);
                    if (frequency == null){
                        frequency = 1;
                        topic.put(normalizedStr, frequency);
                    } else
                        topic.put(normalizedStr, frequency + 1);

            }
            }
        }
        in.close();
    }



    /*
    	 * reads generated index files from file system.
    	 * useful when we have already generated the file, therefore there is no need to parse all the collection again
    	 */
    public static void readInvertedIndexFile()
    {
        IndexStrategy strategy=IndexStrategy.BAG_OF_WORDS;

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

                if(strategy==IndexStrategy.BAG_OF_WORDS)          {
               //  System.out.println(retrievedTerm + " " + postings );
                    invertedIndex_bow.put(retrievedTerm, postings);}

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

    public static void score(){
        //weight term query
        int wtq = 0;
        float score= 0;
        String docID = "";
        float score_lenght = 0;
        List<Posting> listPosting;
        //For every term in the Topic
        for (Map.Entry<String, Integer> entry : topic.entrySet()) {
            //if the term is in the invertedIndex
            if (invertedIndex_bow.containsKey(entry.getKey()))   {

               wtq = entry.getValue();

               listPosting =  invertedIndex_bow.get(entry.getKey());

                if(listPosting != null){
                    for (Posting p : listPosting)
                        {

                           score = p.getFrequency() * wtq;
                           score_lenght = score_lenght + p.getFrequency() ;
                           docID = p.getDocName();

                        }
                           score = score/score_lenght;

                scoreMap.put( score,docID);
        }

        } 
        }

    }

    public static void printResult(String topic) {

        int rank = 0;

        Map< Float, String> treeMap = new TreeMap<Float, String>(scoreMap);

        NavigableSet<Float> navig = ((TreeMap)treeMap ).descendingKeySet();

        for (Iterator<Float> iter=navig.iterator();iter.hasNext();) {
            rank = rank +1;
            Float key = iter.next();


            System.out.println(topic + " Q0 "+ treeMap.get(key) + " " + rank + " " +  String.format("%.7f",key)  + " group09_experiment1 "  );
            if (rank >= 100)
                break;
        }



    }

    public static void main(String[] args) {
        String topicNumber = "topic1";
        Search i = new Search(topicNumber);

        readInvertedIndexFile();

        score();


        printResult(topicNumber);

      //  for (String entry : scoreMap.keySet()) {
       //     System.out.println("Score " +entry);

       // }

    }




}
