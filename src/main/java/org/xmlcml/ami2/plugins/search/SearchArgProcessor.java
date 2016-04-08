package org.xmlcml.ami2.plugins.search;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.AMISearcher;
import org.xmlcml.ami2.plugins.word.WordResultsElement;
import org.xmlcml.ami2.plugins.word.WordResultsElementList;
import org.xmlcml.cmine.args.ArgIterator;
import org.xmlcml.cmine.args.ArgumentOption;
import org.xmlcml.cmine.files.CTree;
import org.xmlcml.cmine.files.ContentProcessor;
import org.xmlcml.cmine.files.ResultContainerElement;
import org.xmlcml.cmine.files.ResultsElementList;
import org.xmlcml.cmine.lookup.DefaultStringDictionary;

/** 
 * Processes commandline arguments.
 * 
 * @author pm286
 */
public class SearchArgProcessor extends AMIArgProcessor {
	
	public static final Logger LOG = Logger.getLogger(SearchArgProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	

	static final double MIN_FONT = 10;
	static final double MAX_FONT = 30;
	
	private Map<String, ResultContainerElement> resultsByDictionary;
	
	public SearchArgProcessor() {
		super();
	}

	public SearchArgProcessor(String args) {
		this();
		parseArgs(args);
	}

	public SearchArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}

	// =============== METHODS ==============

	
	public void parseSearch(ArgumentOption option, ArgIterator argIterator) {
		ensureSearcherList();
		List<String> dictionarySources = argIterator.createTokenListUpToNextNonDigitMinus(option);
		createAndAddDictionaries(dictionarySources);
		for (DefaultStringDictionary dictionary : this.getDictionaryList()) {
			AMISearcher wordSearcher = new SearchSearcher(this, dictionary);
			searcherList.add(wordSearcher);
			wordSearcher.setName(dictionary.getTitle());
		}
//		wordSearcher.setDictionaryList(this.getDictionaryList());
	}
	
	/** refactor output option.
	 * 
	 * @param option
	 */
//	@Deprecated 
	// this 
	public void outputWords(ArgumentOption option) {
		ContentProcessor currentContentProcessor = getOrCreateContentProcessor();
		ResultsElementList resultsElementList = currentContentProcessor.getOrCreateResultsElementList();
		for (int i = 0; i < resultsElementList.size(); i++) {
			File outputDirectory = currentContentProcessor.createResultsDirectoryAndOutputResultsElement(
					option, resultsElementList.get(i)/*, CTree.RESULTS_XML*/);
			File htmlFile = new File(outputDirectory, CTree.RESULTS_HTML);
			((WordResultsElement) resultsElementList.get(i)).writeResultsElementAsHTML(htmlFile, this);
		}
	}
	
	public void parseSummary(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextNonDigitMinus(option);
		if (tokens.size() == 0) {
			LOG.error("parseSummary needs a list of actions");
		} else {
//			summaryMethods = tokens;
			LOG.warn("no-op");
		}
	}
	
	public void finalSummary(ArgumentOption option) {
		LOG.warn("finalSummary no-op");
	}

	public void runSearch(ArgumentOption option) {
		ensureResultsByDictionary();
		ensureSearcherList();
		for (AMISearcher searcher : searcherList) {
			SearchSearcher wordSearcher = (SearchSearcher)searcher;
			String title = wordSearcher.getTitle();
			ResultContainerElement resultsElement = wordSearcher.searchWordList();
			resultsElement.setTitle(title);
			resultsByDictionary.put(title, resultsElement);
		}
	}
	
	public void outputSearch(ArgumentOption option) {
		outputResultsElements(option.getName());
	}

	private void outputResultsElements(String name) {
		ContentProcessor currentContentProcessor = currentCTree.getOrCreateContentProcessor();
		currentContentProcessor.clearResultsElementList();

		for (String title : resultsByDictionary.keySet()) {
			ResultContainerElement resultsElement = resultsByDictionary.get(title);
			resultsElement.setTitle(title);
			currentContentProcessor.addResultsElement(resultsElement);
		}
		currentContentProcessor.createResultsDirectoriesAndOutputResultsElement(name);
	}
	

	// =============================

	private void ensureResultsByDictionary() {
		if (resultsByDictionary == null) {
			resultsByDictionary = new HashMap<String, ResultContainerElement>();
		}
	}


	public WordResultsElementList aggregateOverCMDirList(String pluginName, String methodName) {
		WordResultsElementList resultsElementList = new WordResultsElementList();
		for (CTree cTree : cTreeList) {
			ResultContainerElement resultsElement = cTree.getResultsElement(pluginName, methodName);
			if (resultsElement == null) {
				LOG.error("Null results element, skipped "+cTree.getDirectory());
			} else {
				WordResultsElement wordResultsElement = new WordResultsElement(cTree.getResultsElement(pluginName, methodName));
				resultsElementList.add(wordResultsElement);
			}
		}
		return resultsElementList;
	}

}
