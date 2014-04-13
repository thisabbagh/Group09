
public class Posting {
	private String docName;
	private int frequency;
	
	public Posting(String name)
	{
		docName=name;
		frequency=1;
	}
	public Posting(String name, int freq)
	{
		docName=name;
		frequency=freq;
	}
	public void setDocName(String name){
		docName=name;
	}
	public String getDocName()
	{
		return docName;
	}
	
	public void setFrequency(int freq)
	{
		frequency=freq;
	}
	
	public int getFrequency()
	{
		return frequency;
	}
	
	public void increaseFreq()
	{
		frequency++;
		
	}
	
	public String toString(){
		return "<"+docName+","+frequency+">" ;
		
	}

}
