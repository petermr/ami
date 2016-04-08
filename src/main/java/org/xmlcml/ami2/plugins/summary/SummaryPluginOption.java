package org.xmlcml.ami2.plugins.summary;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIPluginOption;
import org.xmlcml.ami2.plugins.Argument;
import org.xmlcml.cmine.args.FileXPathSearcher;

public class SummaryPluginOption extends AMIPluginOption {

	public static final String TAG = "summary";
	private static final Logger LOG = Logger.getLogger(SummaryPluginOption.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	/** these are called by reflection.
	 * 
	 */
	public SummaryPluginOption() {
		super(TAG);
	}

	public SummaryPluginOption(List<String> options, List<String> flags) {
		super(TAG, options, flags);
	}

	public void run() {
//		String summaryType = "snippets";
		String filterString = getFilterString(argumentList);
		String cmd = "--project "+projectDir+" --type "+optionName+" --filter "+filterString;
		LOG.debug("option string "+optionString+" || "+cmd);
		SummaryArgProcessor summaryArgProcessor = new SummaryArgProcessor(cmd);
		summaryArgProcessor.runSummary();
		summaryArgProcessor.outputSummary();
	}

	private String getFilterString(List<Argument> argumentList) {
		Argument argument = Argument.getArgumentByName("--filter", argumentList);
		List<String> arguments = argument.getValues();
		String filterExpression = FileXPathSearcher.createFilterExpression(arguments);
		LOG.debug("FE "+filterExpression);
		return filterExpression;
	}
}
