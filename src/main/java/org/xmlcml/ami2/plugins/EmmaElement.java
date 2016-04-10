package org.xmlcml.ami2.plugins;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.xml.XMLUtil;

import nu.xom.Element;

/** holds command + arguments for each chained command.
 * 
 * @author pm286
 *
 */
public class EmmaElement extends AbstractEmmaElement {


	private static final String CMINE = "cmine";
	private static final Logger LOG = Logger.getLogger(EmmaElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public final static String TAG = "emma";
	private static final String PROJECT = "project";
	
	private Emma emma;

	public EmmaElement() {
		super(TAG);
	}

	public EmmaElement(Emma emma) {
		this();
		this.emma = emma;
	}

	public String toString() {
		return this.toXML();
	}

	public File getProjectDir() {
		return new File(this.getProjectName());
	}

	public String getProjectName() {
		return this.getAttributeValue(PROJECT);
	}

	public Emma getEmma() {
		return emma;
	}

	public void runCommands() {
		String[] args = generateOldCommand();
		LOG.debug("EMMA CLI "+Arrays.asList(args));
//		try {
		CMineParserOld cMineParserOld = new CMineParserOld();
		LOG.debug("OLD ARGS"+Arrays.asList(args));
		cMineParserOld.parseAndRun(args);
//		} catch (IOException e) {
//			throw new RuntimeException("cannot run commands", e);
//		}

	}

	private String[] generateOldCommand() {
		List<String> ss = new ArrayList<String>();
//		ss.add(CMINE);
		ss.add(getArgument(PROJECT).getValues().get(0));
		for (Argument argument : getArgumentList()) {
			if (PROJECT.equals(argument.getName())) {
				continue;
			}
			ss.addAll(argument.getMinussedNameAndValues());
		}
		List<CommandElement> commands = getCommandList();
		for (CommandElement command : commands) {
			List<String> list1 = command.generateOldCommand();
			ss.addAll(list1);
		}
		return ss.toArray(new String[0]);

	}

	public CommandElement getCommand(String name) {
		return (CommandElement)XMLUtil.getSingleElement(this, "*[local-name()='"+CommandElement.TAG+"' and @"+NAME+"='"+name+"']");
	}

	public List<CommandElement> getCommandList() {
		List<Element> elementList = XMLUtil.getQueryElements(this, "*[local-name()='"+CommandElement.TAG+"']");
		List<CommandElement> commandList = createCommandElementList(elementList);
		return commandList;
	}

	private static List<CommandElement> createCommandElementList(List<Element> elementList) {
		List<CommandElement> commandList = new ArrayList<CommandElement>();
		for (Element element : elementList) {
			commandList.add((CommandElement)element);
		}
		return commandList;
	}

	public String getCommandString() {
		StringBuilder sb = new StringBuilder();
		sb.append(TAG);
		sb.append(" ");
		sb.append(getArgumentString());
		List<CommandElement> commands = getCommandList();
		for (AbstractEmmaElement command : commands) {
			sb.append(" ");
			sb.append(command.getArgumentString());
		}
		return sb.toString();
	}

	public static void main(String[] args) throws IOException {
		EmmaParser parser = new EmmaParser();
		if (args.length == 0) {
			EmmaParser.help();
		} else {
			EmmaElement emmaElement = parser.parseArgs(Arrays.asList(args));
			emmaElement.runCommands();
		}
	}


}
