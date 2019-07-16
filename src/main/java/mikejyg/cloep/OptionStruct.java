package mikejyg.cloep;

import java.util.function.Consumer;

/**
 * this class holds information of an option.
 * 
 * @author jgu
 *
 */
public class OptionStruct {
	private Character shortOption;
	
	private String longOption;
	
	private boolean optArgFlag;
	
	private String description;
	
	private String optArgDescription;
	
	private Consumer<String> handler;

	//////////////////////////////////////////////////////
	
	public Character getShortOption() {
		return shortOption;
	}

	public void setShortOption(Character shortOption) {
		this.shortOption = shortOption;
	}

	public String getLongOption() {
		return longOption;
	}

	public void setLongOption(String longOption) {
		this.longOption = longOption;
	}

	public boolean isOptArgFlag() {
		return optArgFlag;
	}

	public void setOptArgFlag(boolean optArgFlag) {
		this.optArgFlag = optArgFlag;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Consumer<String> getHandler() {
		return handler;
	}

	public void setHandler(Consumer<String> handler) {
		this.handler = handler;
	}

	public String getOptArgDescription() {
		return optArgDescription;
	}

	public void setOptArgDescription(String optArgDescription) {
		this.optArgDescription = optArgDescription;
	}

	@Override
	public String toString() {
		String str="";
		
		if (shortOption!=null) {
			str += "-" + shortOption;
		}
		
		if (longOption!=null) {
			if (str.isEmpty())
				str = "--" + longOption;
			else
				str += ", --" + longOption;
		}
		
		if (optArgFlag)
			str += " parameter";
		
		if (description!=null)
			str += "\t: " + description;
		
		if (optArgDescription!=null)
			str += System.lineSeparator() + "\tparameter: " + optArgDescription;
		
		return str;
		
	}
	
	
}
