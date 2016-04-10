package org.xmlcml.ami2.plugins;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** processes commandline , higher level functions
 * 
 * @author pm286
 *
 */
public class EmmaParser {

	private static final Logger LOG = Logger.getLogger(EmmaParser.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static final String ARGUMENT_PREFIX = "-";
	public static final String COMMAND_SEPARATOR = "-c";
	
	private Queue<String> argQueue;
	private EmmaElement emmaElement;
	
	EmmaParser() {
		init();
	}

	private void init() {
		argQueue = new LinkedList<String>();
	}

	public EmmaElement parseArgs(String args) {
		List<String> argList = args == null ? new ArrayList<String>() : Arrays.asList(args.trim().split("\\s+"));
		return parseArgs(argList);
	}

	public EmmaElement parseArgs(String[] args) {
		return parseArgs(Arrays.asList(args));
	}

	EmmaElement parseArgs(List<String> argList) {
		emmaElement = new EmmaElement();
		this.argQueue.addAll(argList);
		parseArgs();
		return emmaElement;
	}

	void parseArgs() {
		if (argQueue.isEmpty()) {
			throw new RuntimeException("No args given");
		}
		List<Argument> argumentList = readArguments();
		emmaElement.setArgumentList(argumentList);
		if (!argQueue.isEmpty()) {
			readCommands();
		}
	}

	private void readCommands() {
		while (!argQueue.isEmpty()) {
			String token = argQueue.peek();
			if (token.equals(COMMAND_SEPARATOR)) {
				argQueue.poll();
				AbstractEmmaElement command = readCommand();
				emmaElement.appendChild(command);
			} else {
				throw new RuntimeException("Unexpected token; expecting command or EOI: "+token);
			}
		}
	}

	private AbstractEmmaElement readCommand() {
		AbstractEmmaElement commandElement = new CommandElement(argQueue.remove());
		List<Argument> argumentList = readArguments();
		commandElement.setArgumentList(argumentList);
		return commandElement;
	}

	private List<Argument> readArguments() {
		List<Argument> argumentList = new ArrayList<Argument>();
		while (!argQueue.isEmpty()) {
			Argument argument = readArgument();
			if (argument == null) {
				break;
			}
			argumentList.add(argument);
		}
		return argumentList;
	}
	private Argument readArgument() {
		String token = argQueue.peek();
		if (!token.startsWith(ARGUMENT_PREFIX)) {
			return null;
		}
		if (token.equals(ARGUMENT_PREFIX)) {
			throw new RuntimeException("Argument "+ARGUMENT_PREFIX+" not yet supported");
		}
		if (token.equals(COMMAND_SEPARATOR)) {
			return null;
		}
		Argument argument = new Argument(token);
		argQueue.remove();
		while(!argQueue.isEmpty()) {
			token = argQueue.peek();
			if (token == null || token.startsWith(ARGUMENT_PREFIX)) {
				break; 
			}
			argument.add(argQueue.remove());
		}
		LOG.trace("read argument: "+argument);
		return argument;
	}
	
	static void help() {
		System.err.println("Command processor: \n"
				+ "   cmine projectDir [command [command]...]");
	}

	public static void main(String[] args) throws IOException {
		EmmaParser parser = new EmmaParser();
		if (args.length == 0) {
			help();
		} else {
			EmmaElement emmaElement = parser.parseArgs(Arrays.asList(args));
		}
	}

	/** checks name for null, empty and non-alpha leading character.
	 * 
	 * @param name
	 */
	public static void checkAlphabeticName(String name) {
		if (name == null || name.length() == 0 || !Character.isAlphabetic(name.charAt(0))) {
			throw new RuntimeException("Bad name for command/option/argument: "+name);
		}
	}


}
