package org.xmlcml.ami2.plugins.regex;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIPluginOption;
import org.xmlcml.cmine.files.ResourceLocation;
import org.xmlcml.xml.XMLUtil;

import nu.xom.Document;

public class RegexPluginOption extends AMIPluginOption {

	private static final Logger LOG = Logger.getLogger(RegexPluginOption.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static final String TAG = "regex";
	private CompoundRegex compoundRegex;

	public RegexPluginOption() {
		super(TAG);
	}
	
	public RegexPluginOption(List<String> options, List<String> flags) {
		super(TAG, options, flags);
		if (options != null && options.size() != 0) {
			readRegexFile(options.get(0));
		}
	}

	private void readRegexFile(String regexFilename) {
		ResourceLocation location = new ResourceLocation();
		InputStream is = location.getInputStreamHeuristically(regexFilename);
		Document doc = XMLUtil.parseQuietlyToDocument(is);
		RegexArgProcessor regexArgProcessor = new RegexArgProcessor();
		compoundRegex = new CompoundRegex(regexArgProcessor, doc.getRootElement());
		options = Arrays.asList(new String[]{compoundRegex.getTitle()});
		LOG.debug("OPT "+options);
	}

	protected void run() {
		StringBuilder commandString = createCoreCommandStringBuilder();
		commandString.append(" --r.regex "+optionString);
		LOG.trace(">>>>>>>"+commandString);
		new RegexArgProcessor(commandString.toString()).runAndOutput();
	}

	protected String getPluginName(String plugin) {
		return plugin;
	}

	protected String getOption(String option) {
		String opt = option;
		return opt;
	}

}
