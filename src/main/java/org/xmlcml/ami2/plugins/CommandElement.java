package org.xmlcml.ami2.plugins;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import nu.xom.Attribute;

/** holds command + arguments for each chained command.
 * 
 * @author pm286
 *
 */
public class CommandElement extends AbstractEmmaElement {


	private static final String OLD_SEPARATOR = " _ ";
	private static final Logger LOG = Logger.getLogger(CommandElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static String TAG = "command";
	public static final String TYPE = "type";
	
	public CommandElement(String name) {
		super(TAG);
		this.addAttribute(new Attribute(NAME, name));
	}

	public String getCommandString() {
		String s = EmmaParser.COMMAND_SEPARATOR+" " + getName() + getArgumentString();
		return s;
	}
	
	public Emma getEmma() {
		return (Emma) ((EmmaElement)getParent()).getEmma();
	}

	public String getType() {
		return this.getAttributeValue(TYPE);
	}

	@Override
	public String toString() {
		return getCommandString();
	}

	public List<String> generateOldCommand() {
		List<String> ss = new ArrayList<String>();
		List<Argument> args = getArgumentList();
		ss.add(OLD_SEPARATOR.trim());
		ss.add(this.getName());
		Argument name = getArgument(NAME);
//		ss.add(EmmaParser.COMMAND_SEPARATOR);
//		ss.add(name.getName());
		Argument type = getArgument(TYPE);
		ss.add(type.getValues().get(0));
		for (Argument arg : args) {
			String argName = arg.getName();
			if (argName.equals(TYPE) || argName.equals(NAME)) {
				continue;
			}
			ss.addAll(arg.getMinussedNameAndValues());
		}
		return ss;
	}


	
}
