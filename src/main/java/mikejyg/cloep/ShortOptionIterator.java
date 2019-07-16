package mikejyg.cloep;

/**
 * to iterate through clustered short options in a single string, such as "-xvf".
 * @author jgu
 *
 */
public class ShortOptionIterator {
	private String optStr;
	private int optIdx=1;
	
	/////////////////////////////////////////////////
	
	/**
	 * optStr is the complete short option string, including the proceeding '-'. 
	 * @param optStr
	 */
	public ShortOptionIterator(String optStr) {
		this.optStr=optStr;
	}
	
	public boolean hasNext() {
		return optStr.length()>optIdx;
	}
	
	public char next() {
		return optStr.charAt(optIdx++);
	}
	
	/**
	 * get the remaining string past the current index.
	 * this function is to handle a short option with an attached value, as in "gcc -O2 foo.c"
	 * @return
	 */
	public String nextRemainingString() {
		String str = optStr.substring(optIdx);
		optIdx = optStr.length();
		return str;
	}
	
	public int getOptIdx() {
		return optIdx;
	}

	public char peek() {
		return optStr.charAt(optIdx);
	}

	
}
