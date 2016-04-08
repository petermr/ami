package org.xmlcml.ami2.plugins.search;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.dictionary.DefaultAMIDictionary;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.AMIPluginOption;
import org.xmlcml.cmine.args.DefaultArgProcessor;
import org.xmlcml.cmine.util.CellRenderer;

public class SearchPluginOption extends AMIPluginOption {

	private static final Logger LOG = Logger.getLogger(SearchPluginOption.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static final String TAG = "search";
	private String searchDictionary;
	private String dictionary;

	public SearchPluginOption() {
		super(TAG);
	}

	public SearchPluginOption(List<String> options, List<String> flags) {
		super(TAG, options, flags);
	}

	protected void run() {
		StringBuilder commandString = createCoreCommandStringBuilder();
		String sw = getOptionFlagString("w.stopwords", " ");
		commandString.append(sw);
		searchDictionary = optionString;
		if (searchDictionary == null) {
			LOG.warn("no dictionary given); no search");
			return;
		}
		commandString.append(" --sr.search");
		commandString.append(" "+AMIArgProcessor.DICTIONARY_RESOURCE+"/"+searchDictionary+".xml");
		pluginName = "search";
//		commandString.append(plugin);
//		commandString.append(searchDictionary);
//		dictionary = getOption(null);
		optionString = dictionary;
		System.out.print("SR: "+projectDir+"  ");
		LOG.debug(">>>"+this.getClass()+" running "+commandString);
		new SearchArgProcessor(commandString.toString()).runAndOutput();
	}

	protected String getPluginName(String plugin) {
		return plugin;
	}

	protected String getOption(String option) {
		String opt = option;
		if (searchDictionary != null && !searchDictionary.trim().equals("")) {
			String[] ss = searchDictionary.split("/");
			String sss = ss[ss.length-1];
			sss = sss.split("\\.")[0];
			opt = sss;
		}
		return opt;
	}

	@Override
	public CellRenderer getNewCellRenderer() {
		CellRenderer cellRenderer = super.getNewCellRenderer();
		cellRenderer.setHref0(AMIPluginOption.WIKIPEDIA_HREF0);
		cellRenderer.setHref1(AMIPluginOption.WIKIPEDIA_HREF1);
		cellRenderer.setUseHrefWords(1, "_");
		return cellRenderer;
	}

}
