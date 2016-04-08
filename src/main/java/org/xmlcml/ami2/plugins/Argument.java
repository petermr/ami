package org.xmlcml.ami2.plugins;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pm286
 *
 */
public class Argument {

	private String name;
	private List<String> values;

	public Argument(String arg) {
//		CMineParser.checkAlphabeticName(name);
		this.name = arg;
		values = new ArrayList<String>();
	}

	public String getName() {
		return name;
	}

	public List<String> getValues() {
		return values;
	}
	
	public String toString() {
		return "arg: "+name+": "+values;
	}

	public String getArgumentString() {
		StringBuilder sb = new StringBuilder();
		sb.append(" "+name);
		for (String value : values) {
			sb.append(" "+value);
		}
		return sb.toString();
	}

	public void add(String token) {
		values.add(token);
	}

	/** returns first arguments with name.
	 * 
	 * @param name
	 * @param argumentList
	 * @return first argument or null if no match or name == null or argumentList == null
	 */
	public static Argument getArgumentByName(String name, List<Argument> argumentList) {
		if (name != null && argumentList != null) {
			for (Argument arg : argumentList) {
				if (name.equals(arg.getName())) {
					return arg;
				}
			}
		}
		return null;
	}
	

	
}
