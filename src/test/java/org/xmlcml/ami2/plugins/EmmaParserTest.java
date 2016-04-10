package org.xmlcml.ami2.plugins;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami2.AMIFixtures;
import org.xmlcml.cmine.files.CProject;
import org.xmlcml.cmine.files.CTreeList;
import org.xmlcml.cmine.files.PluginSnippetsTree;
import org.xmlcml.cmine.files.SnippetsTree;
import org.xmlcml.cmine.util.CMineTestFixtures;

public class EmmaParserTest {

	private static final Logger LOG = Logger.getLogger(EmmaParserTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String TEST1 = ""
			+ " -p fooDir --bar b1 b2 --foo f1 --baz --plugh p"
			+ " -c norma --type nlm2html"
			+ " -c search --type inn"
			+ " -c word --type frequencies --stopword pmctxt.txt";

	@Test
	public void testCommandLineSyntax() throws IOException {
		EmmaElement emmaElement = new EmmaParser().parseArgs(TEST1);
		Assert.assertEquals(""
			+ "<emma project=\"fooDir\" bar=\"b1 b2\" foo=\"f1\" baz=\"\" plugh=\"p\">"
			+   "<command _name=\"norma\" type=\"nlm2html\" />"
			+   "<command _name=\"search\" type=\"inn\" />"
			+   "<command _name=\"word\" type=\"frequencies\" stopword=\"pmctxt.txt\" />"
			+ "</emma>",
			emmaElement.toString());
	}
	
	@Test 
	public void testArguments() {
		EmmaElement emmaElement = new EmmaParser().parseArgs(TEST1);
		Assert.assertEquals(5,  emmaElement.getAttributeCount());
		Argument arg = new Argument("bar", emmaElement.getAttributeValue("bar"));
		Assert.assertEquals("bar [b1, b2]", arg.toString());
		arg = new Argument("p", emmaElement.getAttributeValue(Argument.PROJECT));
		Assert.assertEquals("p [fooDir]", arg.toString());
		arg = new Argument("baz", emmaElement.getAttributeValue("baz"));
		Assert.assertEquals("baz []", arg.toString());
		arg = new Argument("foo", emmaElement.getAttributeValue("foo"));
		Assert.assertEquals("foo [f1]", arg.toString());
		arg = new Argument("plugh", emmaElement.getAttributeValue("plugh"));
		Assert.assertEquals("plugh [p]", arg.toString());
	}
	
	@Test
	public void testArgumentString() {
		EmmaElement emmaElement = new EmmaParser().parseArgs(TEST1);
		Assert.assertEquals(" --bar b1 b2 --baz  --foo f1 --plugh p --project fooDir", 
				emmaElement.getArgumentString());
		
	}
	
	@Test
	public void testCommandString() {
		EmmaElement emmaElement = new EmmaParser().parseArgs(TEST1);
		CommandElement commandElement = emmaElement.getCommand("norma");
		Assert.assertNotNull(commandElement);
		Assert.assertEquals("-c norma --type nlm2html", 
				commandElement.getCommandString());
		
	}
	
	@Test 
	public void testGetCommandByName() {
		EmmaElement emmaElement = new EmmaParser().parseArgs(TEST1);
		AbstractEmmaElement command = emmaElement.getCommand("norma");
		Assert.assertNotNull(command);
		Assert.assertEquals("-c norma --type nlm2html", command.toString());
	}
	
		
	@Test
	public void testBadSyntax() throws IOException {
		EmmaParser commandParser = new EmmaParser();
		String args = "fooDir badarg \n";
		try {
			commandParser.parseArgs(args);
			Assert.fail("Should catch bad arguments");
		} catch (RuntimeException e) {
			Assert.assertEquals("Unexpected token; expecting command or EOI: fooDir", e.getMessage());
		}
	}
	
	@Test
	public void testCommandLineSearch() throws IOException {
		String project = "zika10old";
		File projectDir = new File("target/tutorial1/"+project);
		File rawDir = new File(AMIFixtures.TEST_AMI_DIR, project);
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		String cmd = ""
				+ "-p "+projectDir
				+ " -c search --type tropicalVirus"
				+ " -c species --type binomial"
				+ " -c search --type inn"
//				+ " -c summary --type datatables --filter snippets "
//				+ " search(disease)"
	    ;
		String[] args = cmd.trim().split("\\s+");
		EmmaParser.main(args);
		CProject cproject = new CProject(projectDir);
		CTreeList cTreeList = cproject.getCTreeList();
		Assert.assertNotNull("cTreeList not null", cTreeList);
		Assert.assertEquals("cTree count", 9, cTreeList.size());
		PluginSnippetsTree pluginSnippetsTree = cproject.getOrCreateCurrentPluginSnippetsTree();
		Assert.assertNotNull("pluginSnippetsTree not null", pluginSnippetsTree);
		List<SnippetsTree> snippetsTreeList = pluginSnippetsTree.getOrCreateSnippetsTreeList();
//		Assert.assertEquals(10, snippetsTreeList.size());
	}

	@Test
	public void testCommandLineShort() throws IOException {
		String project = "zika10";
		File projectDir = new File("target/tutorial/"+project);
		File rawDir = new File(AMIFixtures.TEST_AMI_DIR, project);
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		String cmd = "-p "+projectDir
		+ " -c word --type frequencies --xpath @count>20 --stopwords pmcstop.txt stopwords.txt"
		+ " -c sequence --type dnaprimer"
		+ " -c gene --type human "
		+ " -c search --type tropicalVirus"
//		+ " -c summary --type datatables --filter snippets "
	    ;
		EmmaParser.main((cmd).split("\\s+"));
	}


	@Test
	// SHOWCASE
	public void testCommandLineForMultipleCommands() throws IOException {
		String project = "zika10old";
		File projectDir = new File("target/tutorial/"+project);
		File rawDir = new File(AMIFixtures.TEST_AMI_DIR, project);
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		String cmd = "-p "+projectDir
		+ " -c word --type frequencies --w.stopwords pmcstop.txt stopwords.txt --minfreq 20"
		+ " -c sequence --type dnaprimer"
		+ " -c species --type binomial"
		+ " -c gene --type human "
		+ " -c search --type disease --dictionary org/xmlcml/ami2/plugins/dictionary/disease.xml"
		+ " -c search --type tropicalVirus --dictionary org/xmlcml/ami2/plugins/dictionary/tropicalVirus.xml"
		+ " -c search --type tropicalCountry --dictionary org/xmlcml/ami2/plugins/dictionary/tropicalCountry.xml"
		+ " -c summary --type datatables --filter snippets "
//		+ " -c summary --type frequencies"
	    ;
		EmmaElement emmaElement = new EmmaParser().parseArgs(cmd);
		emmaElement.runCommands();
		// analysis
		CProject cproject = new CProject(projectDir);
		CTreeList cTreeList = cproject.getCTreeList();
		Assert.assertEquals(9, cTreeList.size());
		List<File> reservedDirectories = cproject.getReservedChildDirectoryList();
//		Assert.assertEquals(1, reservedDirectories.size());
//		File summaryFile = cproject.getReservedChildDirectory(CProject.SUMMARY);
//		Assert.assertNotNull(summaryFile);
//		List<File> files = new ArrayList<File>(FileUtils.listFiles(summaryFile, new String[]{"xml"}, true));
//		Collections.sort(files);
//		Assert.assertTrue(files.size() == 14); // because we change frequently
//		Assert.assertEquals("["
//				+ "target/tutorial/zika10old/summary/gene/human/documents.xml,"
//				+ " target/tutorial/zika10old/summary/gene/human/snippets.xml,"
//				+ " target/tutorial/zika10old/summary/search/disease/documents.xml,"
//				+ " target/tutorial/zika10old/summary/search/disease/snippets.xml," 
//				+ " target/tutorial/zika10old/summary/search/tropicalCountry/documents.xml,"
//				+ " target/tutorial/zika10old/summary/search/tropicalCountry/snippets.xml," 
//				+ " target/tutorial/zika10old/summary/search/tropicalVirus/documents.xml,"
//				+ " target/tutorial/zika10old/summary/search/tropicalVirus/snippets.xml,"
//				+ " target/tutorial/zika10old/summary/sequence/dnaprimer/documents.xml,"
//				+ " target/tutorial/zika10old/summary/sequence/dnaprimer/snippets.xml,"
//				+ " target/tutorial/zika10old/summary/species/binomial/documents.xml,"
//				+ " target/tutorial/zika10old/summary/species/binomial/snippets.xml,"
//				+ " target/tutorial/zika10old/summary/word/frequencies/documents.xml,"
//				+ " target/tutorial/zika10old/summary/word/frequencies/snippets.xml"
//				+ "]",
//				
//				files.toString());
		
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
		String cmd = "-p "+projectDir
				+ " -c word --type frequencies --w.stopwords pmcstop.txt stopwords.txt --minfreq 20"
//				+ " -c sequence dnaprimer"
				+ " -c species --type binomial"
				+ " -c gene --type human "
				+ " -c search --type disease --dictionary org/xmlcml/ami2/plugins/dictionary/disease.xml"
//		+ " -c search tropicalVirus --dictionary org/xmlcml/ami2/plugins/dictionary/tropicalVirus.xml"
//		+ " -c summary datatables --filter file **/summary/**/snippets.xml xpath //result"
		+ " -c summary datatables --filter snippets "
//		+ " -c summary datatables --filter **/snippets.xml "
//		+ " -c summary frequencies"
	    ;
		EmmaElement emmaElement = new EmmaParser().parseArgs(cmd);
		emmaElement.runCommands();
		
		CProject cproject = new CProject(projectDir);
		CTreeList cTreeList = cproject.getCTreeList();
	}

	@Test
	public void testGene() throws IOException {
		String project = "zika10old";
		File projectDir = new File("target/tutorial/gene/"+project);
		File rawDir = new File(AMIFixtures.TEST_AMI_DIR, project);
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		String cmd = "-p "+projectDir
		+ " -c gene --type human "
		+ " -c summary --type datatables --filter snippets"
	    ;
		EmmaElement emmaElement = new EmmaParser().parseArgs(cmd);
//		CProject cproject = new CProject(projectDir);
		emmaElement.runCommands();
	}

	@Test
	public void testCommandLineRun() throws IOException {
		String project = "zika10";
		File projectDir = new File("target/tutorial/"+project);
		File rawDir = new File(AMIFixtures.TEST_AMI_DIR, project);
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		String cmd = "-p "+projectDir
//				+ " -c word --type frequencies --xpath @count>20 --w.stopwords pmcstop.txt stopwords.txt"
				+ " -c word --type frequencies --w.stopwords pmcstop.txt stopwords.txt"
		+ " -c sequence --type dnaprimer"
		+ " -c species --type binomial"
		+ " -c gene --type human "
		+ " -c search --type tropicalVirus --dictionary org/xmlcml/ami2/plugins/dictionary/tropicalVirus.xml"
		+ " -c summary --type datatables --filter snippets"
	    ;
		EmmaElement emmaElement = new EmmaParser().parseArgs(cmd);
		AbstractEmmaElement command = emmaElement.getCommand("word");
//		CommandOption option = command.getType("frequencies");
		Argument argument = command.getArgument("xpath");
		emmaElement.runCommands();
		LOG.debug(argument);
	}

}
