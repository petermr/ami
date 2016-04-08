package org.xmlcml.ami2.plugins.summary;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.ResultsAnalysis;
import org.xmlcml.ami2.plugins.ResultsAnalysis.SummaryType;
import org.xmlcml.cmine.args.ArgIterator;
import org.xmlcml.cmine.args.ArgumentOption;
import org.xmlcml.cmine.files.CProject;
import org.xmlcml.cmine.util.CMineGlobber;
import org.xmlcml.cmine.util.CellRenderer;
import org.xmlcml.cmine.util.DataTablesTool;
import org.xmlcml.html.HtmlHtml;
import org.xmlcml.html.HtmlTable;
import org.xmlcml.html.HtmlTd;
import org.xmlcml.norma.biblio.json.EPMCConverter;
import org.xmlcml.xml.XMLUtil;

/** 
 * Processes commandline arguments.
 * 
 * @author pm286
 */
public class SummaryArgProcessor extends AMIArgProcessor {
	
	public static final Logger LOG = Logger.getLogger(SummaryArgProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static final String STAR2 = "**";
	private static final String SUMMARY = "summary";
	private static final String DOT_XML = ".xml";
	private static final String EPMCID = "EPMCID";
	
	private String type;
	private List<File> extractedFiles;
	
	public SummaryArgProcessor() {
		super();
	}

	public SummaryArgProcessor(String args) {
		this();
		parseArgs(args);
	}

	public SummaryArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}

	// =============== METHODS ==============

	public void parseSummary(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextNonDigitMinus(option);
		if (tokens.size() == 0) {
			LOG.error("parseSummary needs a list of actions");
		} else {
//			summaryMethods = tokens;
			LOG.warn("no-op");
		}
	}
	
	@Override
	public void parseFilter(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.getStrings(option);
		if (tokens.size() > 1) {
			super.parseFilter(option, argIterator);
		} else {
			String token = tokens.get(0);
			if (!token.contains(STAR2) && !token.contains("/")) {
				filterExpression = STAR2+"/"+token+DOT_XML;
			} else {
				filterExpression = token;
			}
		}
	}
	
	public void parseType(ArgumentOption option, ArgIterator argIterator) {
		type = argIterator.getString(option);
	}
	
	/** called directly, since not a CTree operation.
	 * 
	 */
	public void runSummary() {
		extractedFiles = extractGlobbedSummaryFiles();
	}

	/** called directly, since not a CTree operation.
	 * 
	 */
	public void outputSummary() {
		LOG.debug("ouput HTML"+extractedFiles.size()+" cols");
		try {
			createDataTables();
		} catch (IOException e) {
			throw new RuntimeException("cannot write table", e);
		}
	}

	public void runSummary(ArgumentOption option) {
		LOG.warn("runSummary no-op");
	}
	
	public void outputSummary(ArgumentOption option) {
		LOG.warn("outputSummary no-op");
	}

	public void finalSummary(ArgumentOption option) {
		LOG.warn("finalSummary no-op");
	}

	private List<File> extractGlobbedSummaryFiles() {
		String glob = CMineGlobber.GLOB + filterExpression;
		List<File> files = new ArrayList<File>();
		File summaryFile = new File(this.cProject.getDirectory(), SUMMARY);
		try {
			CMineGlobber globber = new CMineGlobber(glob, summaryFile);
			files = globber.listFiles();
		} catch (IOException e) {
			throw new RuntimeException("Cannot glob files, ", e);
		}
		return files;
	}

	public void createDataTables() throws IOException {
		DataTablesTool dataTablesTool = new DataTablesTool();
		dataTablesTool.setTitle(cProject.getName());
		ResultsAnalysis resultsAnalysis = new ResultsAnalysis(dataTablesTool);
//		resultsAnalysis.addDefaultSnippets(cProject.getDirectory());
		resultsAnalysis.addSnippetsFiles(extractedFiles);
		resultsAnalysis.setRemoteLink0(EPMCConverter.HTTP_EUROPEPMC_ORG_ARTICLES);
		resultsAnalysis.setRemoteLink1("");
		resultsAnalysis.setLocalLink0("");
		resultsAnalysis.setLocalLink1(ResultsAnalysis.SCHOLARLY_HTML);
		resultsAnalysis.setRowHeadingName(EPMCID);
		writeHtmlTableFiles(dataTablesTool, resultsAnalysis);
		LOG.trace(dataTablesTool.cellRendererList);
	
		// this isn't actually used yet
		createFoooterAndCaption(dataTablesTool);
	}

	private void createFoooterAndCaption(DataTablesTool dataTablesTool) {
		List<HtmlTd> footerList = new ArrayList<HtmlTd>();
		for (CellRenderer cellRenderer : dataTablesTool.cellRendererList) {
			HtmlTd td = new HtmlTd();
			td.appendChild(cellRenderer.getHeading());
			footerList.add(td);
		}
		HtmlTd caption = new HtmlTd();
		caption.appendChild("coun-ts");
		dataTablesTool.setFooterCaption(caption);
		dataTablesTool.setFooterCells(footerList);
	}

	private void writeHtmlTableFiles(DataTablesTool dataTablesTool, ResultsAnalysis resultsAnalysis) throws IOException {
		for (SummaryType cellType : ResultsAnalysis.SUMMARY_TYPES) {
			resultsAnalysis.setCellContentFlag(cellType);
			HtmlTable table = resultsAnalysis.makeHtmlDataTable();
			HtmlHtml html = dataTablesTool.createHtmlWithDataTable(table);
			File outfile = new File(cProject.getDirectory(), cellType.toString()+"."+CProject.DATA_TABLES_HTML);
			XMLUtil.debug(html, outfile, 1);
		}
	}

	
}
