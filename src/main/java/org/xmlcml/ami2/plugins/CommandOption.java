package org.xmlcml.ami2.plugins;

import java.util.ArrayList;
import java.util.List;

/** contains a name and optionally an argument list.
 * 
 * @author pm286
 *
 *  // COLLAPSE this into arguments
 */
public class CommandOption {

	private String name;
	private List<Argument> argumentList;

	public CommandOption(String name) {
		CMineParser.checkAlphabeticName(name);
		this.name = name;
		this.argumentList = new ArrayList<Argument>();
	}

	public void setArgumentList(List<Argument> argumentList) {
		this.argumentList = argumentList;
	}

	public List<Argument> getArgumentList() {
		return argumentList;
	}
	public String getName() {
		return name;
	}

	public String toString() {
		return "(opt: "+name+"; "+argumentList+")";
	}

	public Argument getArgumentByName(String name) {
		return Argument.getArgumentByName(name, argumentList);
	}

}
