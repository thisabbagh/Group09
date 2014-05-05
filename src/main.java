import Lucene.IndexLucene;

import java.util.Scanner;

/*
 * run this first in order to create index
 */
public class main {

	public static void main(String[] args) {

//        IndexLucene createindex = new IndexLucene();

  //      System.out.println("Lucene");


		IndexStrategy strategy = IndexStrategy.BI_GRAM;
		boolean doCaseFold=false;
		boolean doStopWords=false;
		boolean doStemming=false;
		String answer="";
		
		System.out.println("Please select the index type : ");

        System.out.println("1. For Bag of Words");
        System.out.println("2. For Bi Gram");
        System.out.println("3. Exit");
        
        Scanner scan = new Scanner(System.in);
        int input = scan.nextInt();
        while (true){
        //selects the option that the user input
     
            if(input==1){
                System.out.print("For Bag of words indexing ");
                strategy = IndexStrategy.BAG_OF_WORDS;
                break;
            }
            
            else if(input==2){
                System.out.print("For bi gram indexing ");
                strategy = IndexStrategy.BI_GRAM;
                break;
            }
            else if(input==3){
                System.out.println("Goodbye!");
                System.exit(0);
                break;
            }
            else{
                System.out.println("The option you entered is invalid. Please enter a valid option");
                //System.exit(0);
                input = scan.nextInt();
              

        }
}
        
        System.out.println("please select the normalization technique(s) you wish to apply on vocabulary: ");

        System.out.println("1. Case folding? (y/n)");
        answer=scan.next();
        if(answer.toLowerCase().equals("yes")||answer.toLowerCase().equals("y") )
        	doCaseFold=true;
        else if(answer.toLowerCase().equals("no")||answer.toLowerCase().equals("n") )
        	doCaseFold=false;
        else
        	System.out.println("not a valid option.The default value for this option which is 'no' will be used");
        	
        System.out.println("2. Removing stop words? (y/n)");
        answer=scan.next();
        if(answer.toLowerCase().equals("yes")||answer.toLowerCase().equals("y") )
        	doStopWords=true;
        else if(answer.toLowerCase().equals("no")||answer.toLowerCase().equals("n") )
        	doStopWords=false;
        else
        	System.out.println("not a valid option.The default value for this option which is 'no' will be used");
        
        System.out.println("3. Stemming? (y/n)");        
        answer=scan.next();
        if(answer.toLowerCase().equals("yes")||answer.toLowerCase().equals("y") )
        	doStemming=true;
        else if(answer.toLowerCase().equals("no")||answer.toLowerCase().equals("n") )
        	doStemming=false;
        else
        	System.out.println("not a valid option.The default value for this option which is 'no' will be used");
        

        
        
		//call indexing function with user defined parameters
	      Index i = new Index(strategy, doCaseFold, doStopWords, doStemming);
	
	}

}
