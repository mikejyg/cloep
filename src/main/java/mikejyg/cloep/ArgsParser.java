package mikejyg.cloep;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

import mikejyg.cloep.ArgsIterator.ArgType;
import mikejyg.cloep.ParseUtils.KeyValuePair;

/**
 * This class parses the command line arguments, as passed by jvm as args.
 * It iterates through the arguments and calls registered handlers.
 * 
 * The following argument types are recognized:
 * 
 * POSIX short options, clustered or single. e.g. tar -zxvf foo.tar.gz
 *   short options with value attached. e.g. gcc -O2 foo.c
 * 
 * GNU long options. e.g. du --human-readable --max-depth=1
 * 
 * Java properties like: hello prompt=>
 * 
 * and, of course, a handler for non-option argument.
 * 
 * @author jgu
 *
 */
public class ArgsParser {

	public static class ParseException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public ParseException(String msg) {
			super(msg);
		}
	}
	
	private List<OptionStruct> options;
	
	private Map<Character, OptionStruct> shortOptionsMap;
	private Map<String, OptionStruct> longOptionsMap;

	private Consumer<Character> unknownShortOptionHandler;
	
	private Consumer<String> unknownLongOptionHandler;
	
	private Consumer<String> nonOptionHandler;
	
	// working variables
	
	private ArgsIterator argsIt;
	
	private ShortOptionIterator shortOptionIterator;
	
	private String optArg;
	
	private boolean terminateFlag;
	
	private String args[];
	
	///////////////////////////////////////////////////////////////////////////
	
	public ArgsParser() {
		options = new ArrayList<>();
		shortOptionsMap = new TreeMap<>();
		longOptionsMap = new TreeMap<>();
	}
	
	/**
	 * 
	 * @param optChar The short option character. If null, then it is not used.
	 * @param optStr The long option string. If null, then it is not used.
	 * @param description 
	 * @param optArgDescription Not null. (internally) If it is null, then this option has no arg.
	 * @param handler
	 */
	public void addOptionWithArg(Character optChar, String optStr, String description, String optArgDescription, Consumer<String> handler) {
		OptionStruct optionStruct=new OptionStruct();

		optionStruct.setShortOption(optChar);
		optionStruct.setLongOption(optStr);
		
		if (optArgDescription!=null)
			optionStruct.setOptArgFlag(true);
		
		optionStruct.setDescription(description);
		optionStruct.setOptArgDescription(optArgDescription);
		
		optionStruct.setHandler(handler);
		
		options.add(optionStruct);
		
		if ( optChar != null )
			shortOptionsMap.put(optChar, optionStruct);
		
		if ( optStr != null )
			longOptionsMap.put(optStr, optionStruct);
		
	}

	/**
	 * Though the handler has no argument, the closest thing Java offers is Consumer<>, so it is chosen,
	 *   and the argument passed to the handler will be null.
	 *   
	 * @param optChar
	 * @param optStr
	 * @param description
	 * @param handler
	 */
	public void addOptionWithoutArg(Character optChar, String optStr, String description, Consumer<String> handler) {
		addOptionWithArg(optChar, optStr, description, null, handler);
	}
	
	public void parse(String args[]) throws ParseException {
		this.args = args;
		
		terminateFlag = false;
		
		argsIt = new ArgsIterator(args);
		
		while ( !terminateFlag && argsIt.hasNext() ) {
			shortOptionIterator = null;
			
			String arg=argsIt.next();
		
			ArgType argType = ArgsIterator.getType(arg);
			
			OptionStruct optionStruct;
			
			switch (argType) {
			case SHORT_OPTION:
				
				// process all short options in the cluster
				
				shortOptionIterator=new ShortOptionIterator(arg);
				
				while (shortOptionIterator.hasNext()) {
					char opt = shortOptionIterator.next();
					
					optionStruct = shortOptionsMap.get( opt );
					
					if (optionStruct==null) {
						if (unknownShortOptionHandler!=null) {
							unknownShortOptionHandler.accept(opt);
							break;
							
						} else {
							throw new ParseException("illegal option: " + opt);
						}
					}
					
					optArg=null;
					if (optionStruct.isOptArgFlag()) {
						if (shortOptionIterator.hasNext()) {
							optArg=shortOptionIterator.nextRemainingString();
						} else {
							if ( ! argsIt.hasNext() ) {
								throw new ParseException("missing argument for option: " + opt);
							}
							optArg = argsIt.next();
						}
					}
					optionStruct.getHandler().accept(optArg);
				}
				
				break;
					
			case LONG_OPTION: {
				arg = arg.substring(2);	// skip "--"
				
				optArg = null;
				
				// check to see if there is a =
				int eqIdx = arg.indexOf('=');
				if (eqIdx!=-1) {
					optArg = arg.substring(eqIdx+1);
					arg = arg.substring(0, eqIdx);
				}
				
				optionStruct = longOptionsMap.get( arg );
				if (optionStruct==null) {
					if (unknownLongOptionHandler!=null) {
						unknownLongOptionHandler.accept(arg);
						break;
						
					} else {
						throw new ParseException("illegal option: " + arg);
					}
				}
				
				if (optionStruct.isOptArgFlag()) {
					if (optArg==null) {
						if ( ! argsIt.hasNext() ) {
							throw new ParseException("missing argument for option: " + arg);
						}
						optArg = argsIt.next();
					}
					
				} else {
					if (optArg!=null) {
						throw new ParseException("unexpected option argument: " + optArg);
					}
				}
				
				optionStruct.getHandler().accept(optArg);
			}
			
			break;
				
			case NON_OPTION: {
				try {
					KeyValuePair kv = ParseUtils.toKeyValuePair(arg);
					
					optArg = kv.key;
					arg = kv.value;
				
					optionStruct = longOptionsMap.get( arg );
					if (optionStruct==null) {
						if (unknownLongOptionHandler!=null) {
							unknownLongOptionHandler.accept(arg);
							break;
						} else {
							throw new ParseException("illegal option: " + arg);
						}
					}
	
					if (!optionStruct.isOptArgFlag())
						throw new ParseException("unexpected option argument: " + optArg);
					
					optionStruct.getHandler().accept(optArg);
					
					break;

				} catch (java.text.ParseException e) {
					// not an Java property.
				}
				
				if (nonOptionHandler!=null)
					nonOptionHandler.accept(arg);
				else
					throw new ParseException("illegal argument: " + arg);
				
			}
			
			break;
				
			default:
				throw new Error("program error: unknown ArgsType from ArgsIterator.getType(): " + argType);
				
			}
			
		}	// while ( argsIt.hasNext())
		
	}
	
	public ArgsIterator getArgsIt() {
		return argsIt;
	}

	public ShortOptionIterator getShortOptionIterator() {
		return shortOptionIterator;
	}

	public void setUnknownShortOptionHandler(Consumer<Character> unknownShortOptionHandler) {
		this.unknownShortOptionHandler = unknownShortOptionHandler;
	}

	public void setUnknownLongOptionHandler(Consumer<String> unknownLongOptionHandler) {
		this.unknownLongOptionHandler = unknownLongOptionHandler;
	}

	public void setNonOptionHandler(Consumer<String> nonOptionHandler) {
		this.nonOptionHandler = nonOptionHandler;
	}

	public void terminate() {
		terminateFlag=true;
	}
	
	public void printHelp() {
		printHelp(System.out);
	}	
	
	public void printHelp(PrintStream out) {
		for (OptionStruct opt : options) {
			out.println(opt.toString());
		}
	}

	/**
	 * return the remaining(unparsed) args in a string array.
	 * @return
	 */
	public String [] getRemainingArgs() {
		return Arrays.copyOfRange(args, getArgsIt().getArgIdx(), args.length);
	}
	
}
