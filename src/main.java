import java.util.Scanner;


public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		 
	      //Scanner in = new Scanner(System.in);	 
	      //System.out.println("Enter Indexing command in form of IndexingType(bg/bow) case folding(true/false) stop words(true/false) stemming(true/false) \n hint: bow true true true");
	     // String userInput = in.nextLine(); //TODO implementing user command handling
	      
	      IndexStrategy strategy=IndexStrategy.BAG_OF_WORDS; //for test 
	      Index i = new Index(strategy);
	
	}

}
