package mikejyg.cloep;

import java.util.Arrays;

/**
 * This class provides the functionality to iterate through args from the command line.
 * 
 * @author jgu
 *
 */
public class ArgsIterator {
	public enum ArgType {
		SHORT_OPTION,
		LONG_OPTION,
		NON_OPTION
	}
	
	private String[] args;
	
	private int argIdx=0;
	
	/////////////////////////////////////////////////////////////
	
	public ArgsIterator(String[] args) {
		this.args=args;
	}
	
	public boolean hasNext() {
		return args.length>argIdx;
	}
	
	public String next() {
		return args[argIdx++];
	}
	
	public String peek() {
		return args[argIdx];
	}
	
	static public ArgType getType(String arg) {
		if (arg.startsWith("--")) {
			return ArgType.LONG_OPTION;
		} else if (arg.startsWith("-")) {
			return ArgType.SHORT_OPTION;
		} else
			return ArgType.NON_OPTION;
	}	
	
	public int getArgIdx() {
		return argIdx;
	}

	public String[] getRemaining() {
		return Arrays.copyOfRange(args, argIdx, args.length);
	}
	
}
