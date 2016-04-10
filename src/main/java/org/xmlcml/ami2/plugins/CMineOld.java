package org.xmlcml.ami2.plugins;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.ResultsAnalysis.SummaryType;
import org.xmlcml.ami2.plugins.summary.SummaryPluginOption;
import org.xmlcml.cmine.files.CProject;
import org.xmlcml.cmine.util.CellRenderer;
import org.xmlcml.cmine.util.DataTablesTool;
import org.xmlcml.html.HtmlHtml;
import org.xmlcml.html.HtmlTable;
import org.xmlcml.html.HtmlTd;
import org.xmlcml.norma.Norma;
import org.xmlcml.norma.biblio.json.EPMCConverter;
import org.xmlcml.xml.XMLUtil;

@Deprecated
public class CMineOld {

	private static final Logger LOG = Logger.getLogger(CMineOld.class);

	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private List<CMineCommandOld> commandList;
	private String projectDirName;
	private List<Argument> argumentList;
	private File projectDir;
	private List<AMIPluginOption> pluginOptions;

	public CMineOld() {
		commandList = new ArrayList<CMineCommandOld>();
		
	}
	public List<CMineCommandOld> getCommandList() {
		return commandList;
	}
	public void setProjectDirName(String filename) {
		this.projectDirName = filename;
		this.projectDir = new File(projectDirName);
	}
	public void setArgumentList(List<Argument> argumentList) {
		this.argumentList = argumentList;
	}

	public void add(CMineCommandOld command) {
		commandList.add(command);
	}
	public String toString() {
		return "Cmine: "+projectDirName +"; "+argumentList+"; "+commandList;
	}
	
	public void runCommands() {
		List<AMIPluginOption> pluginOptions = getOrCreatePluginOptions();
		LOG.trace("PU "+pluginOptions);
		runNormaIfNecessary();
		SummaryPluginOption summaryPluginOption = null;
		for (AMIPluginOption pluginOption : pluginOptions) {
			if (pluginOption instanceof SummaryPluginOption) {
				summaryPluginOption = (SummaryPluginOption) pluginOption;
				LOG.debug("skipped summaryPlugin");
				continue;
			} else {
				System.out.println("running pluginOption: "+pluginOption);
				pluginOption.run();
				System.out.println("filter: "+pluginOption+" | "+pluginOption.getSnippetsName());
				pluginOption.setResultXPathAttribute("");
				pluginOption.runFilterResultsXMLOptions();
				System.out.println("summary: "+pluginOption);
				pluginOption.runSummaryAndCountOptions(); 
			}
		}
		LOG.trace(pluginOptions);
		if (summaryPluginOption != null) {
			LOG.debug("running summary");
			summaryPluginOption.run();
		}
	}

	private List<AMIPluginOption> getOrCreatePluginOptions() {
		if (pluginOptions == null) {
			List<CMineCommandOld> commands = this.getCommandList();
			pluginOptions = new ArrayList<AMIPluginOption>();
			for (CMineCommandOld command : commands) {
				AMIPluginOption pluginOption = command.getOrCreatePluginOption();
				pluginOptions.add(pluginOption);
			}
			LOG.debug("created Options: "+pluginOptions);
		}
		return pluginOptions;
	}
	
	public void runNormaIfNecessary() {
		if (!new CProject(projectDir).hasScholarlyHTML()) {
			String args = "-i fulltext.xml -o scholarly.html --transform nlm2html --project "+projectDir;
			LOG.debug("running NORMA "+args);
			new Norma().run(args);
		}
	}

//public void addCommand(String cmd) {
//	cmdList.add(cmd);
//}

//public void setDefaultCommands(String cmds) {
//	setDefaultCommands(Arrays.asList(cmds.split("\\s+")));
//}
//
//public void setDefaultCommands(List<String> cmds) {
//	List<String> commands = new ArrayList<String>();
//	boolean start = true;
//	for (String cmd : cmds) {
//		String command = lookup(cmd);
//		if (command == null) {
//			LOG.warn("abbreviation ignored: "+cmd);
//			continue;
//		}
//		commands.add(command);
//	}
//	this.processCommands(commands);
//}



//public void runDefaults(String[] args) throws IOException {
//	File projectDir = new File(args[0]);
//	setProjectDir(projectDir);
//	List<String> commands = CommandProcessor.getDefaultCommands();
//	if (args.length > 1) {
//		commands = new ArrayList<String>(Arrays.asList(args));
//		// remove projectDir
//		commands.remove(0);
//	}
//	LOG.debug(commands);
//	processCommands(commands);
//	createDataTables();
//}

//	private static List<String> getDefaultCommands() {
//		String[] cmds = {
//				"word(frequencies)xpath:@count>20~w.stopwords:pmcstop.txt_stopwords.txt",
//				"sequence(dnaprimer)",
//				"gene(human)",
//				"species(genus)",
//				"species(binomial)"
//		};
//		return Arrays.asList(cmds);
//	}
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
	/** gets first commands with name.
	 * 
	 * @param name of command (includes leading "_")
	 * @return null if name is null or 
	 */
	public CMineCommandOld getCommandByName(String name) {
		if (name != null && commandList != null) {
			for (CMineCommandOld command : commandList) {
				if (name.equals(command.getName())) {
					return command;
				}
			}
		}
		return null;
	}

	public File getProjectDir() {
		return projectDir;
	}

	public static void main(String[] args) {
		CMineOld cmine = new CMineParserOld().parseArgs(args);
		cmine.runCommands();
	}


}
