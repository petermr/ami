package org.xmlcml.ami2.plugins;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.vafer.jdeb.shaded.compress.io.FileUtils;
import org.xmlcml.ami2.AMIFixtures;
import org.xmlcml.cmine.files.CProject;
import org.xmlcml.cmine.files.CTreeList;
import org.xmlcml.cmine.files.PluginOption;
import org.xmlcml.cmine.files.PluginSnippetsTree;
import org.xmlcml.cmine.files.SnippetsTree;
import org.xmlcml.cmine.util.CMineTestFixtures;

public class CommandParserTest {

	private static final Logger LOG = Logger.getLogger(CommandParserTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String TEST1 = ""
			+ "fooDir --bar b1 b2 --foo f1 --baz --plugh p \n"
			+ "_ norma nlm2html \n"
			+ "_ search inn \n"
			+ "_ word frequencies --stopword pmctxt.txt";

	@Test
	public void testCommandLineSyntax() throws IOException {
		CMineOld cmine = new CMineParserOld().parseArgs(TEST1);
		Assert.assertEquals("Cmine: fooDir; ["
				+ "--bar [b1, b2], "
				+ "--foo [f1], "
				+ "--baz [], "
				+ "--plugh [p]"
				+ "];"
				+ " ["
				+ "{norma: option: (opt: nlm2html; [])}, "
				+ "{search: option: (opt: inn; [])}, "
				+ "{word: option: (opt: frequencies; [--stopword [pmctxt.txt]])}"
				+ "]", cmine.toString());
	}
	
	@Test 
	public void testArguments() {
		CMineOld cmine = new CMineParserOld().parseArgs(TEST1);
		List<Argument> argumentList = cmine.getArgumentList();
		Assert.assertEquals(4,  argumentList.size());
		Argument argument0 = argumentList.get(0);
		Assert.assertEquals("--bar [b1, b2]", argument0.toString());
		Assert.assertEquals("--bar", argument0.getName());
		List<String> argValues0 = argument0.getValues();
		Assert.assertEquals(2,  argValues0.size());
		Assert.assertEquals("b1",  argValues0.get(0));
		Assert.assertEquals("b2",  argValues0.get(1));
		
	}
	
	@Test 
	public void testEmptyArguments() {
		CMineOld cmine = new CMineParserOld().parseArgs(TEST1);
		List<Argument> argumentList = cmine.getArgumentList();
		Argument argument2 = argumentList.get(2);
		Assert.assertEquals("--baz []", argument2.toString());
		Assert.assertEquals("--baz", argument2.getName());
		List<String> argValues2 = argument2.getValues();
		Assert.assertEquals(0,  argValues2.size());
	}
	
	@Test 
	public void testGetArgumentsByName() {
		CMineOld cmine = new CMineParserOld().parseArgs(TEST1);
		Argument bar = cmine.getArgumentByName("--bar");
		Assert.assertNotNull(bar);
		Assert.assertEquals("--bar [b1, b2]", bar.toString());;
		bar = cmine.getArgumentByName("-bar");
		Assert.assertNull(bar);
	}
	
	@Test 
	public void testCommands() {
		CMineOld cmine = new CMineParserOld().parseArgs(TEST1);
		List<CMineCommandOld> commandList = cmine.getCommandList();
		Assert.assertEquals(3,  commandList.size());
		CMineCommandOld command0 = commandList.get(0);
		Assert.assertEquals("{norma: option: (opt: nlm2html; [])}", command0.toString());
		Assert.assertEquals(command0.getName(), "norma");
	}
	
	@Test 
	public void testOptions() {
		CMineOld cmine = new CMineParserOld().parseArgs(TEST1);
		List<CMineCommandOld> commandList = cmine.getCommandList();
		CMineCommandOld command0 = commandList.get(0);
		CommandOption option = command0.getOption();
		Assert.assertEquals("(opt: nlm2html; [])", option.toString());
		Assert.assertNotNull(option);
		Assert.assertEquals(option.getName(), "nlm2html");
	}
	
	@Test 
	public void testGetCommandByName() {
		CMineOld cmine = new CMineParserOld().parseArgs(TEST1);
		CMineCommandOld command = cmine.getCommandByName("norma");
		Assert.assertNotNull(command);
		Assert.assertEquals("{norma: option: (opt: nlm2html; [])}", command.toString());
	}
	
	@Test 
	public void testGetOptionByName() {
		CMineOld cmine = new CMineParserOld().parseArgs(TEST1);
		CMineCommandOld command = cmine.getCommandByName("norma");
		CommandOption option = command.getOptionByName("nlm2html");
		Assert.assertNotNull(command);
		Assert.assertEquals("{norma: option: (opt: nlm2html; [])}", command.toString());
	}
	
	@Test 
	public void testGetOptionArgumentsByName() {
		CMineOld cmine = new CMineParserOld().parseArgs(TEST1);
		CMineCommandOld command = cmine.getCommandByName("word");
		CommandOption option = command.getOptionByName("frequencies");
		Argument argument = option.getArgumentByName("--stopword");
		Assert.assertNotNull(argument);
		Assert.assertEquals("pmctxt.txt", argument.getValues().get(0));
	}
	
	@Test 
	public void testCommandOptionArguments() {
		CMineOld cmine = new CMineParserOld().parseArgs(TEST1);
		List<CMineCommandOld> commandList = cmine.getCommandList();
		CMineCommandOld command0 = commandList.get(0);
		CommandOption option = command0.getOption();
		List<Argument> arguments0 = option.getArgumentList();
		Assert.assertEquals(0,  arguments0.size());
		CMineCommandOld command2 = commandList.get(2);
		CommandOption option2 = command2.getOption();
		List<Argument> arguments2 = option2.getArgumentList();
		Assert.assertEquals(1,  arguments2.size());
		Argument argument0 = arguments2.get(0);
		Assert.assertEquals("--stopword", argument0.getName());
		List<String> values0 = argument0.getValues();
		Assert.assertEquals("[pmctxt.txt]", values0.toString());
		Assert.assertEquals(1, values0.size());
		Assert.assertEquals("pmctxt.txt", values0.get(0));

	}
	
	@Test
	public void testBadSyntax() throws IOException {
		CMineParserOld commandParser = new CMineParserOld();
		String args = "fooDir badarg \n";
		try {
			commandParser.parseArgs(args);
			Assert.fail("Should catch bad arguments");
		} catch (RuntimeException e) {
			Assert.assertEquals("Unexpected token; expecting command or EOI: badarg", e.getMessage());
		}
	}
	
	@Test
	public void testBadSyntax1() throws IOException {
		CMineParserOld commandParser = new CMineParserOld();
		String args = "fooDir _ snork foo badopt \n";
		try {
			commandParser.parseArgs(args);
			Assert.fail("Should catch bad arguments");
		} catch (RuntimeException e) {
			Assert.assertEquals("Currently only one Option allowed for each Command: badopt", e.getMessage());
		}
	}
	
	@Test
	public void testOptionless() throws IOException {
		CMineParserOld commandParser = new CMineParserOld();
		String args = "fooDir _ snork foo _ barf _ plugh zzz \n";
		commandParser.parseArgs(args);
	}
	
	@Test
	public void testCommandLineSyntax1() throws IOException {
//		String args = "fooDir _ norma --transform nlm2html _ search inn _ word frequencies --stopword pmctxt.txt";
//		CMine cmine = new CommandParser().parseArgs(args);
//		List<ChainedCommand> chainedCommandList = cmine.getCommandList();
//		Assert.assertEquals("commands" , 2, chainedCommandList.size());
//		List<Argument> argumentList = commandProcessor.getArgumentList();
//		Assert.assertEquals("args" , 1, argumentList.size());
	}

	@Test
	public void testCommandLineSearch() throws IOException {
		String project = "zika10old";
		File projectDir = new File("target/tutorial1/"+project);
		File rawDir = new File(AMIFixtures.TEST_AMI_DIR, project);
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		String cmd = ""
				+ projectDir
//				+ " _ search tropicalVirus"
//				+ " _ species binomial"
//				+ " _ search inn"
				+ " _ gene human"
				+ " _ summary datatables --filter snippets"
//				+ " search(disease)"
	    ;
		String[] args = cmd.split("\\s+");
		LOG.debug("CMD OLD "+Arrays.asList(args));
		CMineParserOld.main(args);
		CProject cproject = new CProject(projectDir);
		CTreeList cTreeList = cproject.getCTreeList();
		LOG.debug("testCommandLineSearch");
		Assert.assertNotNull("cTreeList not null", cTreeList);
		Assert.assertEquals("cTree count", 9, cTreeList.size());
		PluginOption pluginOption = new AMIPluginOption("search", "disease");
		PluginSnippetsTree pluginSnippetsTree = cproject.getOrCreateCurrentPluginSnippetsTree();
		Assert.assertNotNull("pluginSnippetsTree not null", pluginSnippetsTree);
		List<SnippetsTree> snippetsTreeList = pluginSnippetsTree.getOrCreateSnippetsTreeList();
		LOG.debug("SL"+snippetsTreeList);
//		Assert.assertEquals(10, snippetsTreeList.size());
	}

	@Test
	public void testCommandLineShort() throws IOException {
		String project = "zika10";
		File projectDir = new File("target/tutorial/"+project);
		File rawDir = new File(AMIFixtures.TEST_AMI_DIR, project);
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		String cmd = ""
		+ " _ word frequencies --xpath:@count>20 --stopwords pmcstop.txt stopwords.txt"
		+ " _ sequence dnaprimer"
		+ " _ gene human "
		+ " _ search tropicalVirus"
	    ;
		CMineParserOld.main((projectDir+" "+cmd).split("\\s+"));
	}

	@Test
	//@Ignore
	// runs defaults
	public void testCommandLineShortEmpty() throws IOException {
		String project = "zika10";
		File projectDir = new File("target/tutorial/"+project);
		File rawDir = new File(AMIFixtures.TEST_AMI_DIR, project);
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		CMineParserOld.main(new String[]{projectDir.toString()});
	}

	@Test
	// SHOWCASE
	public void testCommandLineForMultipleCommands() throws IOException {
		String project = "zika10old";
		File projectDir = new File("target/tutorial/"+project);
		File rawDir = new File(AMIFixtures.TEST_AMI_DIR, project);
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		String cmd = ""+projectDir
		+ " _ word frequencies --w.stopwords pmcstop.txt stopwords.txt --minfreq 20"
		+ " _ sequence dnaprimer"
		+ " _ species binomial"
		+ " _ gene human "
		+ " _ search disease --dictionary org/xmlcml/ami2/plugins/dictionary/disease.xml"
		+ " _ search tropicalVirus --dictionary org/xmlcml/ami2/plugins/dictionary/tropicalVirus.xml"
		+ " _ search tropicalCountry --dictionary org/xmlcml/ami2/plugins/dictionary/tropicalCountry.xml"
		+ " _ summary datatables --filter snippets "
//		+ " _ summary frequencies"
	    ;
		CMineOld cmine = new CMineParserOld().parseArgs(cmd);
		cmine.runCommands();
		// analysis
		CProject cproject = new CProject(projectDir);
		CTreeList cTreeList = cproject.getCTreeList();
		Assert.assertEquals(9, cTreeList.size());
		List<File> reservedDirectories = cproject.getReservedChildDirectoryList();
		Assert.assertEquals(1, reservedDirectories.size());
		File summaryFile = cproject.getReservedChildDirectory(CProject.SUMMARY);
		Assert.assertNotNull(summaryFile);
		List<File> files = new ArrayList<File>(FileUtils.listFiles(summaryFile, new String[]{"xml"}, true));
		Collections.sort(files);
		Assert.assertTrue(files.size() == 14); // because we change frequently
		Assert.assertEquals("["
				+ "target/tutorial/zika10old/summary/gene/human/documents.xml,"
				+ " target/tutorial/zika10old/summary/gene/human/snippets.xml,"
				+ " target/tutorial/zika10old/summary/search/disease/documents.xml,"
				+ " target/tutorial/zika10old/summary/search/disease/snippets.xml," 
				+ " target/tutorial/zika10old/summary/search/tropicalCountry/documents.xml,"
				+ " target/tutorial/zika10old/summary/search/tropicalCountry/snippets.xml," 
				+ " target/tutorial/zika10old/summary/search/tropicalVirus/documents.xml,"
				+ " target/tutorial/zika10old/summary/search/tropicalVirus/snippets.xml,"
				+ " target/tutorial/zika10old/summary/sequence/dnaprimer/documents.xml,"
				+ " target/tutorial/zika10old/summary/sequence/dnaprimer/snippets.xml,"
				+ " target/tutorial/zika10old/summary/species/binomial/documents.xml,"
				+ " target/tutorial/zika10old/summary/species/binomial/snippets.xml,"
				+ " target/tutorial/zika10old/summary/word/frequencies/documents.xml,"
				+ " target/tutorial/zika10old/summary/word/frequencies/snippets.xml"
				+ "]",
				
				files.toString());
		
	}

	@Test
	@Ignore // large
	public void testDataTables() throws IOException {
//		String project = "cattlelame";
		String project = "cattlelame";
		File projectDir = new File("target/tutorial/"+project);
//		File rawDir = new File(AMIFixtures.TEST_AMI_DIR, project);
//		File rawDir = new File(AMIFixtures.TEST_AMI_DIR, project);
		File rawDir = new File("../../workspace", project);
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		//
		String cmd = ""+projectDir
				+ " _ word frequencies --w.stopwords pmcstop.txt stopwords.txt --minfreq 20"
//				+ " _ sequence dnaprimer"
				+ " _ species binomial"
				+ " _ gene human "
				+ " _ search disease --dictionary org/xmlcml/ami2/plugins/dictionary/disease.xml"
//		+ " _ search tropicalVirus --dictionary org/xmlcml/ami2/plugins/dictionary/tropicalVirus.xml"
//		+ " _ summary datatables --filter file **/summary/**/snippets.xml xpath //result"
		+ " _ summary datatables --filter snippets "
//		+ " _ summary datatables --filter **/snippets.xml "
//		+ " _ summary frequencies"
	    ;
		CMineOld cmine = new CMineParserOld().parseArgs(cmd);
		cmine.runCommands();
		
		CProject cproject = new CProject(projectDir);
		CTreeList cTreeList = cproject.getCTreeList();
	}

	@Test
	public void testGene() throws IOException {
		String project = "zika10old";
		File projectDir = new File("target/tutorial/gene/"+project);
		File rawDir = new File(AMIFixtures.TEST_AMI_DIR, project);
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		String cmd = ""+projectDir
		+ " _ gene human "
	    ;
		CMineOld cmine = new CMineParserOld().parseArgs(cmd);
		CProject cproject = new CProject(projectDir);
		
		cmine.runCommands();
	}

	public void testCommandLineRun() throws IOException {
		String project = "zika10";
		File projectDir = new File("target/tutorial/"+project);
		File rawDir = new File(AMIFixtures.TEST_AMI_DIR, project);
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		String cmd = ""+projectDir
		+ " _ word frequencies --xpath @count>20 --w.stopwords pmcstop.txt stopwords.txt"
		+ " _ sequence dnaprimer"
		+ " _ species binomial"
		+ " _ gene human "
		+ " _ search tropicalVirus --dictionary org/xmlcml/ami2/plugins/dictionary/tropicalVirus.xml"
	    ;
		CMineOld cmine = new CMineParserOld().parseArgs(cmd);
		CMineCommandOld command = cmine.getCommandByName("_word");
		CommandOption option = command.getOptionByName("frequencies");
		Argument argument = option.getArgumentByName("--xpath");
		cmine.runCommands();
		LOG.debug(argument);
	}

	@Test
	@Ignore // LONG
	public void testWiskott() throws IOException {
		String project = "wiskott";
		File projectDir = new File("target/tutorial/"+project);
		File rawDir = new File("../projects/", project);
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		String cmd = ""
		+ "word(frequencies)"
		+ " sequence(dnaprimer)"
		+ " gene(human) "
	    ;
		CMineParserOld.main((projectDir+" "+cmd).split("\\s+"));
	}

}
