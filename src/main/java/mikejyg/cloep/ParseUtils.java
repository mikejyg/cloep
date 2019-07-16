package mikejyg.cloep;

/**
 * misc utilities.
 * 
 * @author jgu
 *
 */
public class ParseUtils {

	public static class KeyValuePair {
		public String key;
		public String value;
		
		public KeyValuePair() {}
		
		public KeyValuePair(String key, String value) {
			this.key=key;
			this.value=value;
		}
	}
	
	/**
	 * Tokenize a Java Property style string.
	 * 
	 * NOTE: the ability to escape the special character, in this case, '=', is intentionally NOT provided.
	 *   Sorry, please do not use = in a property name.
	 * 
	 * @param propertyString
	 * @return
	 * @throws java.text.ParseException
	 */
	public static KeyValuePair toKeyValuePair(String propertyString) throws java.text.ParseException {
		int eqIdx = propertyString.indexOf('=');
		
		if (eqIdx==-1)
			throw new java.text.ParseException("symbol = not found.", propertyString.length()-1);

		KeyValuePair kv=new KeyValuePair(propertyString.substring(eqIdx+1), propertyString.substring(0, eqIdx));
		
		return kv;
	}
	
	
}
