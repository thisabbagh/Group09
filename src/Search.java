import java.io.*;
import java.util.*;

public class Search {


    static final String DIR_TO_INDEX = "../topics";

    static final String DIR_TO_BOW_IVERTED_INDEX = "../bow_InvertedIndex.txt";
    static final String DIR_TO_BG_IVERTED_INDEX = "../bg_InvertedIndex.txt";

    final static Map<String,Integer > topic = new HashMap<String,Integer >();


    final static HashMap<String, List<Posting>> invertedIndex_bow = new HashMap<String, List<Posting>>();
    final static HashMap<String, List<Posting>> invertedIndex_bg = new HashMap<String, List<Posting>>();


   static HashMap< Float, String > scoreMap = new HashMap<Float, String>();


    public Search(String topicNumber , IndexStrategy strategy) {

        final File docDir = new File(DIR_TO_INDEX);
        if (!docDir.exists() || !docDir.canRead()) {
            System.out.println("Document directory '"+ docDir.getAbsolutePath()+ "' does not exist or is not readable, please check the path");
            System.exit(1);
        }


        try {
            if (strategy.equals(IndexStrategy.BAG_OF_WORDS))
            createQuery(docDir, topicNumber,strategy);
            else
                createQuery(docDir, topicNumber,strategy);

            BufferedWriter topicDoc = new BufferedWriter(new FileWriter("../"+ topicNumber +".txt"));

            for (Map.Entry<String, Integer> entry : topic.entrySet()) {
                topicDoc.write( entry.getKey() +" "+ entry.getValue() + "  \n");
                //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            }

            topicDoc.close();


        } catch (IOException e) {
            System.out.println(" caught a " + e.getClass()
                    + "\n with message: " + e.getMessage());
        }

    }

    public void createQuery(File file , String topicNumber, IndexStrategy strategy) throws IOException {

        if (file.canRead()) {
            if (file.isDirectory()) {
                String[] files = file.list();
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        createQuery(new File(file, files[i]), topicNumber,strategy);
                    }
                }
            } else {

              if (topicNumber.equals(file.toString().substring(10))){
                  createDictionary(file,strategy);
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

    private static void createDictionary(File file, IndexStrategy strategy) throws IOException {

        String normalizedStr=null;
        BufferedReader in = new BufferedReader(new FileReader(file));
        String currentLine;
        while ((currentLine = in.readLine()) != null) {
            final StringTokenizer parser = new StringTokenizer(currentLine, "[_] ([0-9]) [^\\w] \t\n\r\f.,;:!?'");

            if (strategy.equals(IndexStrategy.BAG_OF_WORDS)){
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
            }  }
            else        {
                String twoterms = "";
                int count = 1;
            while (parser.hasMoreTokens()) {
                if (count == 2){
                    String  currentWord = parser.nextToken();
                    currentWord = tokenString(currentWord);
                    twoterms = twoterms + " "+ currentWord;
                    normalizedStr=Normalizer.normalize(twoterms, true, false, false); // TODO: need to pass parameters. and think obout how to implement stemming and stop word for bi-gram terms
                    if (normalizedStr!=null&& normalizedStr!=""){
                        Integer frequency = topic.get(normalizedStr);
                        if (frequency == null){
                            frequency = 1;
                            topic.put(normalizedStr, frequency);
                        } else
                            topic.put(normalizedStr, frequency + 1);

                    }
                    count = 1;
                        }
                        else {
                            String  currentWord = parser.nextToken();
                            currentWord = tokenString(currentWord);
                            twoterms = currentWord;
                            count = count + 1;

                }

            }
            }







        }
        in.close();
    }



    /*
    	 * reads generated index files from file system.
    	 * useful when we have already generated the file, therefore there is no need to parse all the collection again
    	 */
    public static void readInvertedIndexFile(IndexStrategy strategy)
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

    public static void scoreBOW(){
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

    public static void scoreBG(){
        //weight term query
        int wtq = 0;
        float score= 0;
        String docID = "";
        float score_lenght = 0;
        List<Posting> listPosting;
        //For every term in the Topic
        for (Map.Entry<String, Integer> entry : topic.entrySet()) {
            //if the term is in the invertedIndex
            if (invertedIndex_bg.containsKey(entry.getKey()))   {

                wtq = entry.getValue();

                listPosting =  invertedIndex_bg.get(entry.getKey());

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

        Date start = new Date();

        IndexStrategy strategy = IndexStrategy.BI_GRAM;

        System.out.println("Please select the index type : ");

        System.out.println("1. For Bag of Words");
        System.out.println("2. For Bi Gram");
        System.out.println("3. Exit");

        Scanner scan = new Scanner(System.in);
        int input = scan.nextInt();

        //selects the option that the user input
        switch (input)
        {
            case 1:
                System.out.println("Index Bag of Words");
                strategy = IndexStrategy.BAG_OF_WORDS;
                readInvertedIndexFile(strategy);
                break;
            case 2:
                System.out.println("Index Bi Gram");
                strategy = IndexStrategy.BI_GRAM;
                readInvertedIndexFile(strategy);
            case 3:
                System.out.println("Goodbye!");
                System.exit(0);
                break;
            default:
                System.out.println("Please enter a valid option.");
                break;

        }
        System.out.println("-----------------------------------------------------");
        System.out.println("Please write the topic and the topic number:");
        System.out.println("For example topic1 or topic2:");
        System.out.println("To finish type quit");


        scan = new Scanner(System.in);

        String topic = scan.next();

        while (!topic.equals("quit")){

            Search i = new Search(topic,strategy);

           if(checkTopic(topic)) {
            if (strategy.equals(IndexStrategy.BAG_OF_WORDS))
                 scoreBOW();
               else
                 scoreBG();

                printResult(topic);
           }

            Date end = new Date();
            System.out.println(end.getTime() - start.getTime() + " total milliseconds");

            System.out.println("-----------------------------------------------------");
            System.out.println("Please write the topic and the topic number:");
            System.out.println("For example topic1 or topic2:");
            System.out.println("To finish type quit");


            scan = new Scanner(System.in);

            topic = scan.next();


        }



    }

        public static boolean checkTopic(String topic){

            if (topic.equals("topic1")||topic.equals("topic2")||
            topic.equals("topic2")||topic.equals("topic3")||
            topic.equals("topic4")||topic.equals("topic5")||
            topic.equals("topic6")||topic.equals("topic7")||
            topic.equals("topic8")||topic.equals("topic9") ||
            topic.equals("topic10")||topic.equals("topic11") ||
            topic.equals("topic12")||topic.equals("topic13")  ||
            topic.equals("topic14")||topic.equals("topic15")  ||
            topic.equals("topic16")||topic.equals("topic17") ||
            topic.equals("topic18")||topic.equals("topic19") ||
            topic.equals("topic20")

            )
             return true;
             else
                return false;


        }

}
