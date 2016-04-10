package org.xmlcml.ami2.plugins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jsoup.helper.StringUtil;
import org.xmlcml.ami2.plugins.gene.GenePluginOption;
import org.xmlcml.ami2.plugins.regex.RegexPluginOption;
import org.xmlcml.ami2.plugins.search.SearchPluginOption;
import org.xmlcml.ami2.plugins.sequence.SequencePluginOption;
import org.xmlcml.ami2.plugins.species.SpeciesPluginOption;
import org.xmlcml.ami2.plugins.word.WordPluginOption;
import org.xmlcml.cmine.args.DefaultArgProcessor;
import org.xmlcml.cmine.files.CProject;
import org.xmlcml.cmine.files.OptionFlag;
import org.xmlcml.cmine.files.PluginOption;
import org.xmlcml.cmine.util.CellRenderer;

@Deprecated
public class AMIPluginOption extends PluginOption {

	private static final Logger LOG = Logger.getLogger(AMIPluginOption.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static Pattern COMMAND = Pattern.compile("(.*)\\((.*)\\)(.*)");
	
	public final static String WIKIPEDIA_HREF0 = "http://en.wikipedia.org/wiki/";
	public final static String WIKIPEDIA_HREF1 = "";
	protected List<Argument> argumentList;

	private CMineCommandOld cMineCommand;
	private EmmaElement emmaCommand;
	
	protected AMIPluginOption(String pluginName) {
		this.pluginName = pluginName;
		setXpath();
	}
	
	public AMIPluginOption(String pluginName, String option) {
		this(pluginName);
		this.optionName = option;
		setXpath();
	}
	
	public AMIPluginOption(String plugin, List<String> options, List<String> flags) {
		this(plugin);
		this.options = options;
		this.optionName = options.get(0);
		this.flags = flags;
		this.optionString = StringUtil.join(options, " ");
		LOG.trace("optionString: "+optionString);
		
		setXpath();

	}

	private void setXpath() {
		this.resultXPathBase = "//result";
//		this.resultXPathAttribute = "@exact";
		this.resultXPathAttribute = "";
	}

	/** this is where the subclassing is created.
	 * 
	 * // now replaced by reflection
	 * */
	public static AMIPluginOption createPluginOption(String cmd) {
		Matcher matcher = COMMAND.matcher(cmd);
		if (cmd == null || cmd.trim().equals("")) {
			throw new RuntimeException("Null/empty command");
		} else if (!matcher.matches()) {
			throw new RuntimeException("Command found: "+cmd+" must fit: "+matcher+""
					+ "...  plugin(option1[,option2...])[_flag1[_flag2...]]");
		}
		String command = matcher.group(1);
		List<String> options = Arrays.asList(matcher.group(2).split(","));
		String flagString = matcher.group(3);
		flagString = flagString.replaceAll("_",  " ");
		List<String>flags = Arrays.asList(flagString.split("~"));
		List<OptionFlag> optionFlags = OptionFlag.createOptionFlags(flags);
		LOG.trace("option flags: "+optionFlags);
		
		AMIPluginOption pluginOption = null;
		if (false) {
		} else if (command.equals(GenePluginOption.TAG)) {
			pluginOption = new GenePluginOption(options,flags);
		} else if (command.equals(RegexPluginOption.TAG)) {
			pluginOption = new RegexPluginOption(options,flags); 
		} else if (command.equals(SearchPluginOption.TAG)) {
			pluginOption = new SearchPluginOption(options,flags); 
		} else if (command.equals(SequencePluginOption.TAG)) {
			pluginOption = new SequencePluginOption(options,flags); 
		} else if (command.equals(SpeciesPluginOption.TAG)) {
			pluginOption = new SpeciesPluginOption(options,flags);
		} else if (command.equals(WordPluginOption.TAG)) {
			pluginOption = new WordPluginOption(options,flags);
		} else {
			LOG.error("unknown command: "+command);
//			LOG.info("commands: "+COMMANDS);
		}
		if (pluginOption != null) {
			pluginOption.setOptionFlags(optionFlags);
		}
		return pluginOption;
	}

	protected void run() {
		throw new RuntimeException("BUG: "+this.getClass()+" must override run()");
	}

	// create optionSnippets
	public void runFilterResultsXMLOptions() {
		if (options != null) {
			LOG.warn("LIST of options is deprecated");
			for (String optionName : options) {
				createAndRunPluginOption(optionName);
			}
		} else if (optionName != null) {
			createAndRunPluginOption(optionName);
		}
	}

	private void createAndRunPluginOption(String optionName) {
		AMIPluginOption pluginOption = new AMIPluginOption(this.pluginName, optionName);
		pluginOption.setProjectDir(projectDir);
		pluginOption.runFilterResultsXMLOptions1();
	}
	
	/** we use a new DefaultArgProcessor to run this.*/
	 // --project target/tutorial/zika10old 
	 //   --filter file(**/results/species/binomial/results.xml)xpath(//result)
	 //   -o summary/species/binomial/snippets.xml  
	private void runFilterResultsXMLOptions1() {
		LOG.trace("projDIR "+projectDir);
		String filterCommandString = createFilterCommandString(optionName);
		DefaultArgProcessor argProcessor = new DefaultArgProcessor(filterCommandString);
		LOG.trace("filter "+filterCommandString);
		argProcessor.getCProject().setDirectory(projectDir);
		argProcessor.runAndOutput();
		return;
	}

	protected String createFilterCommandString(String option) {
		String cmd = "--project "+projectDir;
		String xpathFlags = createXpathQualifier();
		String resultsFileName = getResultsFileName(option);
//		cmd += " --filter file(**/"+resultsFileName+")xpath("+resultXPathBase+xpathFlags+") ";
		cmd += " --filter file(**/results/"+resultsFileName+")xpath("+resultXPathBase;
		if (resultXPathAttribute.length() > 0) {
			cmd += "/"+resultXPathAttribute;
		}
		cmd += ") ";
		cmd += " -o "+createSnippetsFilename(option)+"  ";
		LOG.trace("runFilterResultsXMLOptions: "+cmd);
		return cmd;
	}

	private String getResultsFileName(String option) {
		return getPluginName(pluginName)+"/"+getOption(option)+"/results.xml";
	}

	protected String getPluginName(String plugin) {
		return plugin;
	}

	public String getPluginName() {
		return pluginName;
	}

	protected String createXpathQualifier() {
		String xpathFlags = getOptionFlagString("xpath", "");
		if (xpathFlags != null && !"".equals(xpathFlags)) {
			xpathFlags = "["+xpathFlags+"]";
		}
		return xpathFlags;
	}

	protected String getOptionFlagString(String key, String separator) {
		StringBuilder optionFlagString = new StringBuilder();
		List<OptionFlag> keyedOptionFlags = getKeyedOptionFlags(key);
		if (keyedOptionFlags.size() > 0) {
			if (!key.equals("xpath")) {
				optionFlagString.append(" --"+key);
			}
			for (int i = 0; i < keyedOptionFlags.size(); i++) {
				optionFlagString.append(separator);
				String ko = keyedOptionFlags.get(i).getValue();
				LOG.trace(">>>>>>>>>>>>>"+ko);
				optionFlagString.append(ko);
			}
		}
		return optionFlagString.toString();
	}

	private List<OptionFlag> getKeyedOptionFlags(String key) {
		List<OptionFlag> keyedOptionFlags = new ArrayList<OptionFlag>();
		for (OptionFlag optionFlag : optionFlags) {
			if (optionFlag.getKey().equals(key)) {
				LOG.trace("OF "+optionFlag+ " /// "+key);
				keyedOptionFlags.add(optionFlag);
			}
		}
		return keyedOptionFlags;
	}

	protected void runMatchSummaryAndCount(String option) {
		String xpath = "";
		if (resultXPathAttribute.startsWith("@")) {
			xpath = "/"+resultXPathAttribute;
		} else if (!resultXPathAttribute.equals("")) {
			xpath = "/"+resultXPathAttribute;
		}
		String snippetsFilename = createSnippetsFilename(option);
		String countFilename = createCountFilename(option);
		String documentCountFilename = createDocumentCountFilename(option);
		
		LOG.trace("Summary: "+countFilename+" / "+documentCountFilename);
		String cmd = "--project "+projectDir
				+ " -i "+ snippetsFilename
				+ " --xpath //result"+xpath
				+ " --summaryfile "+countFilename
				+ " --dffile "+documentCountFilename
				;
		LOG.trace("super.runMatchSummaryAndCount: "+cmd);
		//System.out.print("C: "+option);
		new DefaultArgProcessor(cmd).runAndOutput();
		return;
	}
	


	// analyze optionSnippets
	public void runSummaryAndCountOptions() {
		if (options != null) {
			LOG.warn("OPTIONS is obsolete");
			for (String option : options) {
				runMatchSummaryAndCount(option);
			}
		} else {
			runMatchSummaryAndCount(optionName);
		}
	}

	protected String createSnippetsFilename(String option) {
		String name = getSnippetsName()+"/"+CProject.SNIPPETS_XML;
		return name;
	}

	protected String createCountFilename(String option) {
		String name = getSnippetsName()+"/"+CProject.COUNT_XML;
		LOG.trace("Counts "+name);
		return name;
	}
	
	protected String createDocumentCountFilename(String option) {
		String name = getSnippetsName()+"/"+CProject.DOCUMENTS_XML;
		LOG.trace("Documents "+name);
		return name;
	}
	
	protected StringBuilder createCoreCommandStringBuilder() {
		StringBuilder commandStringBuilder = new StringBuilder("--project "+projectDir+" -i scholarly.html");
		commandStringBuilder.append(getOptionFlagString("context", " "));
		return commandStringBuilder;
	}
	
	public CellRenderer getNewCellRenderer() {
		CellRenderer cellRenderer = new CellRenderer(this);
		return cellRenderer;
	}

	protected boolean matches(String pluginOptionName) {
		String pluginOptionTag = pluginOptionName.split(":")[0];
		LOG.trace("TAG "+pluginOptionTag);
		return getPluginName().equals(pluginOptionTag);
	}

	public void setResultXPathAttribute(String resultXPathAttribute) {
		this.resultXPathAttribute = resultXPathAttribute;
	}

	public void setPluginName(String name) {
		this.pluginName = name;
	}

	public void setOptionName(String name) {
		this.optionName = name;
		this.optionString = name;
	}
	
	public String toString() {
		return pluginName+"("+options+"/"+optionName+")"+optionFlags;
	}

	public void setArgumentList(List<Argument> argumentList) {
		this.argumentList = argumentList;
	}

	protected String getArgumentString() {
		StringBuilder sb = new StringBuilder();
		if (argumentList != null) {
			for (Argument argument : argumentList) {
				sb.append(argument.getArgumentString());
			}
		}
		return sb.toString();
	}

	protected String createCommandString(String cmd) {
		cmd += this.getArgumentString();
		return cmd;
	}

	@Deprecated
	public void setCMineCommand(CMineCommandOld cMineCommand) {
		this.cMineCommand = cMineCommand;
		this.setPluginName(cMineCommand.getName());
		this.setOptionName(cMineCommand.getOptionName()); // this is obsolete but necessary
		this.setProjectDir(cMineCommand.getProjectDir());
		this.setArgumentList(cMineCommand.getArgumentList());
	}



}
