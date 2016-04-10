package org.xmlcml.ami2.plugins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;

public abstract class AbstractEmmaElement extends Element{

	protected static final String NAME = "_name";

	protected AbstractEmmaElement(String tag) {
		super(tag);
	}
	
	public List<Argument> getArgumentList() {
		List<Argument> argumentList = new ArrayList<Argument>();
		for (int i = 0; i < this.getAttributeCount(); i++) {
			Attribute attribute = this.getAttribute(i);
			Argument argument = Argument.createArgument(attribute);
			argumentList.add(argument);
		}
		return argumentList;
	}
	
	public void setArgumentList(List<Argument> argumentList) {
		for (Argument argument : argumentList) {
			Attribute attribute = Argument.createAttribute(argument);
			this.addAttribute(attribute);
		}
	}

	/** space-separated arguments (includes leading space)
	 * 
	 * @return
	 */
	public String getArgumentString() {
		List<Argument> argumentList = getArgumentList();
		Collections.sort(argumentList);
		StringBuilder sb = new StringBuilder();
		for (Argument argument : argumentList) {
			if (!NAME.equals(argument.getName())) {
				sb.append(" ");
				sb.append(Argument.MINUS_MINUS);
				sb.append(argument.getName());
				for (String value : argument.getValues()) {
					sb.append(" ");
					sb.append(value);
				}
			}
		}
		return sb.toString();
	}

	public String getName() {
		return this.getAttributeValue(NAME);
	}

	public Argument getArgument(String name) {
		Argument argument = Argument.createArgument(this.getAttribute(name));
		return argument;
	}
}