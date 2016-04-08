package org.xmlcml.ami2.plugins.summary;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIPlugin;

/** test plugin.
 * 
 * Very simple tasks for testing and tutorials.
 * 
 * @author pm286
 *
 */
public class SummaryPlugin extends AMIPlugin {

	private static final Logger LOG = Logger.getLogger(SummaryPlugin.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public SummaryPlugin() {
		this.argProcessor = new SummaryArgProcessor();
	}

	public SummaryPlugin(String[] args) {
		super();
		this.argProcessor = new SummaryArgProcessor(args);
	}

	public SummaryPlugin(String args) {
		super();
		this.argProcessor = new SummaryArgProcessor(args);
	}

	public static void main(String[] args) {
		new SummaryArgProcessor(args).runAndOutput();		
	}
}
