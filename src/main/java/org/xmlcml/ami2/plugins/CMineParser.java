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
public class CMineParser {

	private static final Logger LOG = Logger.getLogger(CMineParser.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static final String ARGUMENT_PREFIX = "-";
	public static final String COMMAND_SEPARATOR = "_";
	
	private List<Argument> argumentList;
	private CMine cmine;
	private Queue<String> argQueue;
	
	CMineParser() {
		init();
	}

	private void init() {
		argQueue = new LinkedList<String>();
	}

	public CMine parseArgs(String args) {
		List<String> argList = Arrays.asList(args.split("\\s+"));
		return parseArgs(argList);
	}

	private CMine parseArgs(List<String> argList) {
		cmine = new CMine();
		this.argQueue.addAll(argList);
		parseArgs();
		return cmine;
	}

	void parseArgs() {
		LOG.trace(argQueue);
		if (argQueue.isEmpty()) {
			throw new RuntimeException("No args given");
		}
		cmine = new CMine();
		String token = argQueue.remove();
		cmine.setProjectDirName(token);
		readArguments();
		cmine.setArgumentList(argumentList);
		if (!argQueue.isEmpty()) {
			readCommands();
		}
		LOG.trace("CM: "+cmine);
	}

	private void readCommands() {
		while (!argQueue.isEmpty()) {
			String token = argQueue.peek();
			if (token.equals(COMMAND_SEPARATOR)) {
				argQueue.poll();
				CMineCommand command = readCommand();
				cmine.add(command);
				LOG.debug(command);
			} else {
				throw new RuntimeException("Unexpected token; expecting command or EOI: "+token);
			}
		}
	}

	private CMineCommand readCommand() {
		CMineCommand command = new CMineCommand(cmine, argQueue.remove());
		if (!COMMAND_SEPARATOR.equals(argQueue.peek())) {
			CommandOption option = readOption();
			if (option != null) {
				command.setOption(option);
			}
		}
		return command;
	}

	private List<Argument> readArguments() {
		argumentList = new ArrayList<Argument>();
		while (!argQueue.isEmpty()) {
			readAndAddArgument();
			String token = argQueue.peek();
			if (token == null || !token.startsWith(ARGUMENT_PREFIX)) {
				break; 
			}
		}
		return argumentList;
	}
	private void readAndAddArgument() {
		String token = argQueue.peek();
		if (!token.startsWith(ARGUMENT_PREFIX)) {
			return;
		}
		if (token.equals(ARGUMENT_PREFIX)) {
			throw new RuntimeException("Argument "+ARGUMENT_PREFIX+" not yet supported");
		}
		Argument argument = new Argument(token);
		argQueue.remove();
		while(!argQueue.isEmpty()) {
			token = argQueue.peek();
			if (token.equals(COMMAND_SEPARATOR) || token.startsWith(ARGUMENT_PREFIX)) {
				break; 
			}
			argument.add(argQueue.remove());
		}
		argumentList.add(argument);
		LOG.trace("read argument: "+argument);
	}
	
	private CommandOption readOption() {
		String token = argQueue.remove();
		LOG.debug("option: "+token);
		CommandOption option = new CommandOption(token);
		while (!argQueue.isEmpty()) {
			token = argQueue.peek();
			if (token.equals(COMMAND_SEPARATOR)) {
				break;
			} else if (token.startsWith(ARGUMENT_PREFIX)) {
				readArguments();
				option.setArgumentList(argumentList);
				break;
			} else {
				throw new RuntimeException("Currently only one Option allowed for each Command: "+token);
			}
		}
		return option;
	}

	public List<Argument> getArgumentList() {
		return argumentList;
	}


	private static void help() {
		System.err.println("Command processor: \n"
				+ "   cmine projectDir [command [command]...]");
	}

	public CMine getCMine() {
		return cmine;
	}

	public static void main(String[] args) throws IOException {
		CMineParser commandProcessor = new CMineParser();
		if (args.length == 0) {
			help();
		} else {
			commandProcessor.parseArgs(Arrays.asList(args));
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
