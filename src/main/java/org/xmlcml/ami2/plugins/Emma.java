package org.xmlcml.ami2.plugins;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.summary.SummaryPluginOption;
import org.xmlcml.cmine.files.CProject;
import org.xmlcml.norma.Norma;

public class Emma {

	private static final Logger LOG = Logger.getLogger(Emma.class);

	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private List<EmmaElement> commandList;
	private String projectDirName;
	private List<Argument> argumentList;
	private File projectDir;
	private List<AMIPluginOption> pluginOptions;

	public Emma() {
		commandList = new ArrayList<EmmaElement>();
		
	}
	public List<EmmaElement> getCommandList() {
		return commandList;
	}
	public void setProjectDirName(String filename) {
		this.projectDirName = filename;
		this.projectDir = new File(projectDirName);
	}
	public void setArgumentList(List<Argument> argumentList) {
		this.argumentList = argumentList;
	}

	public void add(EmmaElement command) {
		commandList.add(command);
	}
	
//	public void runCommands() {
//		List<AMIPluginOption> pluginOptions = getOrCreatePluginOptions();
//		LOG.trace("PU "+pluginOptions);
//		runNormaIfNecessary();
//		SummaryPluginOption summaryPluginOption = null;
//		for (AMIPluginOption pluginOption : pluginOptions) {
//			if (pluginOption instanceof SummaryPluginOption) {
//				summaryPluginOption = (SummaryPluginOption) pluginOption;
//				LOG.debug("skipped summaryPlugin");
//				continue;
//			} else {
//				System.out.println("running pluginOption: "+pluginOption);
//				pluginOption.run();
//				System.out.println("filter: "+pluginOption+" | "+pluginOption.getSnippetsName());
//				pluginOption.setResultXPathAttribute("");
//				pluginOption.runFilterResultsXMLOptions();
//				System.out.println("summary: "+pluginOption);
//				pluginOption.runSummaryAndCountOptions(); 
//			}
//		}
//		LOG.trace(pluginOptions);
//		if (summaryPluginOption != null) {
//			LOG.debug("running summary");
//			summaryPluginOption.run();
//		}
//	}

	public void runNormaIfNecessary() {
		if (!new CProject(projectDir).hasScholarlyHTML()) {
			String args = "-i fulltext.xml -o scholarly.html --transform nlm2html --project "+projectDir;
			LOG.debug("running NORMA "+args);
			new Norma().run(args);
		}
	}

	public List<Argument> getArgumentList() {
		return argumentList;
	}
	public List<Argument> getOptions() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** gets first arguments with name.
	 * 
	 * @param name of argument (includes leading "-" or "--")
	 * @return null if name is null or no arguments
	 */
	public Argument getArgumentByName(String name) {
		return Argument.getArgumentByName(name, argumentList);
	}
//	/** gets first commands with name.
//	 * 
//	 * @param name of command (includes leading "_")
//	 * @return null if name is null or 
//	 */
//	public EmmaElement getTypeArgByName(String name) {
//		if (name != null && commandList != null) {
//			for (EmmaElement command : commandList) {
//				if (name.equals(command.getName())) {
//					return command;
//				}
//			}
//		}
//		return null;
//	}

	public File getProjectDir() {
		return projectDir;
	}

	public static void main(String[] args) {
		EmmaElement emmaElement = new EmmaParser().parseArgs(args);
		emmaElement.runCommands();
	}

	public String toString() {
		return "emma: "+Argument.toString(argumentList)+"; "+commandList;
	}


}
