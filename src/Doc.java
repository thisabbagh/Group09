import java.util.ArrayList;

public class Doc {

	private ArrayList<Integer> term;
	private ArrayList<Integer> docID;
	private ArrayList<Integer> tf;
	private ArrayList<Double> invertf;
	private int id;
	private String className;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Doc(int i, String className) {
		this.className = className; 
		this.id = i;
		this.term = new ArrayList<Integer>();
		this.tf = new ArrayList<Integer>();
		this.invertf = new ArrayList<Double>();
	}

	public void SetTerm(int x) {
		term.add(x);
	}

	public void SetTF(int x) {
		tf.add(x);
	}
	
	public void SetTInvert(double x) {
		invertf.add(x);
	}
		
	
	
	public void GetTerm(Doc doc) {
		System.out.print("Doc " + doc.getId() + ":");
		for (int g : term) { 
			System.out.print(" " + g );
		}
		System.out.println("-" );		
	}

	public ArrayList<String> writxt(Doc doc){
		ArrayList<String> vector = new ArrayList<String>() ;
		for (int g : doc.term) { 
			vector.add (Integer.toString(g) + ",") ;
		}	
		return  vector;
	}
	
	public void setId(int id) {
		this.id = id;
	}


	public int getId() {
		return id;
	}

	public ArrayList<Integer> getDocID() {
		return docID;
	}

	public void setDocID(Integer docID) {
		this.docID.add(docID);
	}

	
}

