package org.xmlcml.ami2.plugins;

import java.io.File;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami2.AMIFixtures;
import org.xmlcml.cmine.files.CProject;
import org.xmlcml.cmine.files.CTreeList;
import org.xmlcml.cmine.files.PluginOption;
import org.xmlcml.cmine.files.PluginSnippetsTree;
import org.xmlcml.cmine.files.SnippetsTree;
import org.xmlcml.cmine.util.CMineTestFixtures;

public class CommandProcessorOldTest {

	private static final Logger LOG = Logger.getLogger(CommandProcessorOldTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testCommandLineSyntax() throws IOException {
		String args = "fooDir bar(plugh)";
		CommandProcessorOld.main(args.split("\\s+"));
	}
	
	@Test
	@Ignore
	// SHOWCASE 2016-03-30
	public void testCommandLineSearch() throws IOException {
		String project = "zika10old";
		File projectDir = new File("target/tutorial1/"+project);
		File rawDir = new File(AMIFixtures.TEST_AMI_DIR, project);
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		String cmd = ""
				+ "search(tropicalVirus)"
				+ " gene(human)"
				+ " search(inn)"
				+ " species(binomial)"
//				+ " search(phytochemicals)"
				+ " search(disease)"
//                + " word(frequencies)"
	    ;
		String[] args = (projectDir+" "+cmd).split("\\s+");
		CommandProcessorOld.main(args);
		CProject cproject = new CProject(projectDir);
		CTreeList cTreeList = cproject.getCTreeList();
		Assert.assertNotNull("cTreeList not null", cTreeList);
		Assert.assertEquals("cTree count", 9, cTreeList.size());
		PluginOption pluginOption = new AMIPluginOption("search", "disease");
		PluginSnippetsTree pluginSnippetsTree = cproject.getOrCreateCurrentPluginSnippetsTree();
		List<SnippetsTree> snippetsTreeList = pluginSnippetsTree.getOrCreateSnippetsTreeList();
		Assert.assertEquals(10, snippetsTreeList.size());
	}

	@Test
	public void testCommandLineShort() throws IOException {
		String project = "zika10";
		File projectDir = new File("target/tutorial/"+project);
		File rawDir = new File(AMIFixtures.TEST_AMI_DIR, project);
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		String cmd = ""
		+ "word(frequencies)xpath:@count>20~w.stopwords:pmcstop.txt_stopwords.txt"
		+ " sequence(dnaprimer)"
		+ " gene(human) "
		+ " search(tropicalVirus)"
	    ;
		CommandProcessorOld.main((projectDir+" "+cmd).split("\\s+"));
	}

	@Test
	//@Ignore
	// runs defaults
	public void testCommandLineShortEmpty() throws IOException {
		String project = "zika10";
		File projectDir = new File("target/tutorial/"+project);
		File rawDir = new File(AMIFixtures.TEST_AMI_DIR, project);
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		CommandProcessorOld.main(new String[]{projectDir.toString()});
	}

	@Test
	@Ignore // LONG
	public void testCommandLine() throws IOException {
		String project = "zika";
		File projectDir = new File("target/tutorial/"+project);
		File rawDir = new File(AMIFixtures.TEST_AMI_DIR, project);
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		String cmd = "word(frequencies)xpath:@count>20~w.stopwords:pmcstop.txt_stopwords.txt"
		+ " sequence(dnaprimer)"
		+ " gene(human) "
		+ " word(search)w.search:/org/xmlcml/ami2/plugins/dictionary/tropicalVirus.xml"
	    ;
		CommandProcessorOld.main((projectDir+" "+cmd).split("\\s+"));
	}

}
