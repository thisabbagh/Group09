import java.util.Arrays;
import java.util.List;

/*
 * a class for doing vocabulary normalization (case folding, removing stop words, stemming)
 */
public class Normalizer {
	
	// stop word list, retrieved from "Journal of Machine Learning Research" http://jmlr.org/papers/volume5/lewis04a/a11-smart-stop-list/english.stop
	//static List<String> stopWords=Arrays.asList("a","a's","able","about","above","according","accordingly","actually","afterwards","again","ain't","allow","almost","along","although","among","an","another","any","anybody","anyhow","anyone","anything","anyway","anyways","anywhere","apart","appear","appreciate","appropriate","are","aren't","around","aside","asking","associated","at","available","away","awfully","b","be","became","because","become","becomes","becoming","been","before","beforehand","behind","being","believe","below","beside","besides","best","better","between","beyond","both","brief","but","by","c","c'mon","c's","came","can","can't","cannot","cant","cause","causes","certain","certainly","changes","clearly","co","com","come","comes","concerning","consequently","consider","considering","contain","containing","contains","corresponding","could","couldn't","course","currently","d","definitely","despite","did","didn't","different","do","doesn't","don't","done","down","during","e","each","edu","eg","eight","either","else","elsewhere","enough","entirely","especially","etc","even","ever","every","everybody","everyone","everything","everywhere","ex","exactly","except","f","far","few","fifth","first","five","followed","following","follows","for","former","formerly","forth","four","from","further","furthermore","g","get","gets","getting","given","gives","go","goes","going","gone","got","gotten","greetings","h","had","hadn't","hardly","hasn't","have","haven't","having","he","he's","hello","help","hence","her","here","here's","hereafter","hereby","herein","hereupon","hers","herself","hi","him","himself","his","hither","hopefully","how","howbeit","however","i","i'd","i'll","i'm","i've","ie","if","ignored","immediate","in","inasmuch","inc","indeed","indicate","indicated","indicates","inner","insofar","instead","into","inward","is","isn't","it","it'd","it'll","it's","its","itself","j","just","k","keep","keeps","kept","know","knows","known","l","last","lately","later","latter","latterly","least","less","lest","let","let's","like","liked","likely","little","look","looking","looks","ltd","m","mainly","many","may","me","meanwhile","merely","might","more","moreover","most","mostly","much","must","my","myself","n","name","namely","nd","near","nearly","necessary","need","needs","neither","never","nevertheless","new","next","nine","no","nobody","non","none","noone","nor","normally","not","nothing","novel","now","nowhere","o","obviously","of","off","often","oh","ok","okay","old","on","one","ones","only","onto","or","other","others","otherwise","ought","our","ours","ourselves","out","outside","over","overall","own","p","particular","particularly","per","perhaps","placed","please","plus","possible","presumably","probably","provides","q","que","quite","qv","r","rather","rd","re","really","reasonably","regarding","regardless","regards","relatively","respectively","right","s","said","same","saw","say","saying","says","second","secondly","see","seeing","seem","seemed","seeming","seems","seen","self","selves","sensible","sent","serious","seriously","seven","several","shall","she","should","shouldn't","since","six","so","some","somebody","somehow","someone","something","sometime","sometimes","somewhat","somewhere","soon","sorry","specified","specify","specifying","still","sub","such","sup","sure","t","t's","take","taken","tell","tends","th","than","thank","thanks","thanx","that","that's","thats","the","their","theirs","them","themselves","thence","there","there's","thereafter","thereby","therefore","therein","theres","thereupon","these","they","they'd","they'll","they're","they've","think","third","this","thorough","thoroughly","those","though","three","through","throughout","thru","thus","to","together","too","took","toward","towards","tried","tries","truly","try","trying","twice","two","u","un","unfortunately","unless","until","unto","up","upon","us","use","useful","uses","using","usually","uucp","v","value","various","very","via","viz","vs","w","want","wants","was","wasn't","way","we","we'd","we'll","we're","we've","welcome","well","went","were","weren't","what","what's","whatever","when","whence","whenever","where","where's","whereafter","whereas","whereby","wherein","whereupon","wherever","whether","which","while","whither","who","who's","whoever","whole","whom","whose","why","will","willing","wish","with","within","without","wonder","would","would","wouldn't","x","y","yes","yet","you","you'd","you'll","you're","you've","your","yours","yourself","yourselves","z","zero");
	
	//http://www.textfixer.com/resources/common-english-words.txt + single characters added to the list
	static List<String> stopWords=Arrays.asList("a","able","about","across","after","all","almost","also","am","among","an","and","any","are","as","at","be","because","been","but","by","can","cannot","could","dear","did","do","does","either","else","ever","every","for","from","get","got","had","has","have","he","her","hers","him","his","how","however","i","if","in","into","is","it","its","just","least","let","like","likely","may","me","might","most","must","my","neither","no","nor","not","of","off","often","on","only","or","other","our","own","rather","said","say","says","she","should","since","so","some","than","that","the","their","them","then","there","these","they","this","tis","to","too","twas","us","wants","was","we","were","what","when","where","which","while","who","whom","why","will","with","would","yet","you","your","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z");
	public static String normalize(String inputStr, boolean doCaseFold,boolean doStopWords,boolean doStemming)
	{
		if(inputStr=="" || inputStr==null)
			return null;
		
		inputStr=inputStr.trim();
		
		if(doCaseFold)
			inputStr=caseFolding(inputStr);
		//the order of stop word and stemming matters. but since our stop word is not stemmed we need to first apply 
		//the stop word on terms and then do the stemming
		if (doStopWords)
			inputStr=stopWordsRemover(inputStr);
		
		if(inputStr!=null && doStemming)
			inputStr=stemming(inputStr);	
		
		if (inputStr!=null){
			inputStr=inputStr.trim();
		if ( inputStr.equals(""))
			inputStr=null;
		}
		return inputStr;
			
			
		
	}
	
	public static String caseFolding(String inputStr)
	{
		return inputStr.toLowerCase();
	}
	
	public static String stemming(String inputStr)
	{
		char token[]=inputStr.toCharArray();
		Stemmer stemmer = new Stemmer();
		stemmer.add(token,token.length);
		stemmer.stem();
		return stemmer.toString();
	}
	
	public static String stopWordsRemover(String inputStr)
	{
		String result= inputStr;
		if(stopWords.contains(inputStr))
			result=null;
		
		return result;
			
		}
	
	  public static void main(String[] args)
	   {
		  System.out.println(stopWordsRemover("about us"));
	   }
	
	
	}


