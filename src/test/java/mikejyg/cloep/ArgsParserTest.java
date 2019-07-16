package mikejyg.cloep;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.Consumer;

import org.junit.Test;

import mikejyg.cloep.ArgsParser;
import mikejyg.cloep.ArgsParser.ParseException;

/**
 * examples and tests for the ArgsParser class.
 * 
 * @author jgu
 *
 */
public class ArgsParserTest {
	private ArgsParser argsParser = new ArgsParser();
	
	private PrintStream out;

	//////////////////////////////////////////////
	
	static public String printToString(Consumer<PrintStream> printer) {
		try ( ByteArrayOutputStream baos = new ByteArrayOutputStream();
				PrintStream ps = new PrintStream(baos) ) {
			
			printer.accept(ps);
			return new String(baos.toByteArray(), StandardCharsets.UTF_8);			
		} catch (IOException e) {
			throw new Error("IOException: " + e.getMessage());
		}
	}
	
	//////////////////////////////////////////////

	public ArgsParserTest() {
		// long only
		argsParser.addOption(null, "help", "print help messages", null, str->{
			out.println("to print the help message...");
		});

		// short only
		argsParser.addOption('v', null, "verbose", null, str->{
			out.println("verbose is set.");
		});

		// long and short
		argsParser.addOption('o', "additionalOptions", "additionalOptions", "a comma separated list of addtional options", str->{
			out.println("additional options: " + str);
		});
		
		argsParser.setNonOptionHandler(args->{
			out.println("non-option arg: " + args);
			out.println( "next arg: " + argsParser.getArgsIt().peek() );
			out.println( "remaining args: " + Arrays.toString(argsParser.getArgsIt().getRemaining()) );
			argsParser.terminate();
		});
		
	}
	
	public void parse(String [] args) {
		try {
			argsParser.Parse(args);
			
		} catch (ParseException e) {
			out.println("ParseException: " + e.getMessage());
			
			String errArg = args[argsParser.getArgsIt().getArgIdx()-1];
			out.println( "at argument: " + errArg );
			
			if (argsParser.getShortOptionIterator()!=null) {
				out.println("at short option: " + errArg.charAt( argsParser.getShortOptionIterator().getOptIdx()-1 ) );
			}
			
		}
	
	}
	
	public void test(PrintStream out) {
		this.out = out;
		
		out.println("help messages: ");
		argsParser.printHelp(out);
		out.println();
		
		parse( new String[]{"--help"
				, "-ogood", "-o", "good"
				, "--additionalOptions=def", "--additionalOptions", "def", "additionalOptions=def"
				, "-vk"} );
		
		parse( new String[]{"--illegalOption"});
		
		parse( new String[]{"selfTest", "123", "456"});
		
		// test the 2 peek functions
		
		argsParser.setUnknownShortOptionHandler(c->{
			out.println("next short option: " + argsParser.getShortOptionIterator().peek());
		});

		argsParser.setUnknownLongOptionHandler(str->{
			out.println("unknown arg: " + str);
		});
		
		parse( new String[] {"-vkc", "--unknown"});
		
	}		

	@Test
	/**
	 * run the test, and compare it with the golden results.
	 * 
	 * @throws IOException
	 */
	public void test() throws IOException {
		String goldenOutput="help messages: " + System.lineSeparator() + 
				"--help	: print help messages" + System.lineSeparator() + 
				"-v	: verbose" + System.lineSeparator() + 
				"-o, --additionalOptions parameter	: additionalOptions" + System.lineSeparator() + 
				"	parameter: a comma separated list of addtional options" + System.lineSeparator() + 
				"" + System.lineSeparator() + 
				"to print the help message..." + System.lineSeparator() + 
				"additional options: good" + System.lineSeparator() + 
				"additional options: good" + System.lineSeparator() + 
				"additional options: def" + System.lineSeparator() + 
				"additional options: def" + System.lineSeparator() + 
				"additional options: def" + System.lineSeparator() + 
				"verbose is set." + System.lineSeparator() + 
				"ParseException: illegal option: k" + System.lineSeparator() + 
				"at argument: -vk" + System.lineSeparator() + 
				"at short option: k" + System.lineSeparator() + 
				"ParseException: illegal option: illegalOption" + System.lineSeparator() + 
				"at argument: --illegalOption" + System.lineSeparator() + 
				"non-option arg: selfTest" + System.lineSeparator() + 
				"next arg: 123" + System.lineSeparator() + 
				"remaining args: [123, 456]" + System.lineSeparator() + 
				"verbose is set." + System.lineSeparator() + 
				"next short option: c" + System.lineSeparator() + 
				"unknown arg: unknown" + System.lineSeparator();
			
		String output = printToString((ps)->{test(ps);});
	
		assert(goldenOutput.contentEquals(output));
		
	}
	
	//////////////////////////////////////////////////
	
	public static void main(String[] args) {
		new ArgsParserTest().test(System.out);
	}
	
	
}
