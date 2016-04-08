package org.xmlcml.ami2.plugins.word;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIPluginOption;
import org.xmlcml.ami2.plugins.phylotree.ArgProcessorRunnable;
import org.xmlcml.cmine.args.DefaultArgProcessor;
import org.xmlcml.cmine.util.CellRenderer;

public class WordPluginOption extends AMIPluginOption {

	private static final Logger LOG = Logger.getLogger(WordPluginOption.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static final String TAG = "word";

	public WordPluginOption() {
		super(TAG);
	}

	public WordPluginOption(List<String> options, List<String> flags) {
		super(TAG, options, flags);
	}

	protected void run() {
		StringBuilder commandString = createCoreCommandStringBuilder();
		commandString.append(" --w.words "+optionString);
		// This is horribly messy. replace optionFlags by ArgumentList
		commandString.append(this.getArgumentString());
		String sw = getOptionFlagString("w.stopwords", " ");
		commandString.append(sw);
		LOG.debug("WORD "+commandString);
		System.out.print("WS: "+projectDir+"  ");
		LOG.trace(" running "+commandString);
		new WordArgProcessor(commandString.toString()).runAndOutput();
	}

	protected String getPluginName(String plugin) {
		return plugin;
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
