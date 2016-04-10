package org.xmlcml.ami2.plugins;

import java.io.File;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.ami2.AMIFixtures;
import org.xmlcml.cmine.files.CProject;
import org.xmlcml.cmine.files.CTree;
import org.xmlcml.cmine.files.CTreeList;
import org.xmlcml.cmine.files.ResultContainerElement;
import org.xmlcml.cmine.files.ResultElement;
import org.xmlcml.cmine.util.CMineTestFixtures;

/** use and generation of xpath attributes.
 * 
 * @author pm286
 *
 */
public class XPathTest {
	
	private static final Logger LOG = Logger.getLogger(XPathTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testXPathGenerator() {
		
	}
		
	@Test
	public void testXPath() {
		String project = "xpath";
		File projectDir = new File("target/xpath/"+project);
		File rawDir = new File(AMIFixtures.TEST_AMI_DIR, project);
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		String cmd = ""+projectDir
		+ " _ species binomial"
	    ;
		CMineOld cmine = new CMineParserOld().parseArgs(cmd);
		cmine.runCommands();
		CProject cproject = new CProject(projectDir);
		CTreeList cTreeList = cproject.getCTreeList();
		Assert.assertEquals(2, cTreeList.size());
		// find our project
		CTree cTree = cTreeList.getCTreeByName("minispecies");
		Assert.assertNotNull(cTree);
		// and the species/binomial results.xml
		ResultContainerElement resultContainer = cTree.getResultsElement("species", "binomial");
		Assert.assertNotNull(resultContainer);
		LOG.trace(resultContainer.toXML());
		List<ResultElement> resultElements = resultContainer.getOrCreateResultElementList();
		Assert.assertEquals(3, resultElements.size());
		String[] xpaths = {
				"/*[local-name()='html'][1]/*[local-name()='body'][1]/*[local-name()='div'][1]/*[local-name()='div'][1]/*[local-name()='p'][2]",
				"/*[local-name()='html'][1]/*[local-name()='body'][1]/*[local-name()='div'][1]/*[local-name()='div'][4]/*[local-name()='div'][5]/*[local-name()='p'][1]",
				"/*[local-name()='html'][1]/*[local-name()='body'][1]/*[local-name()='div'][1]/*[local-name()='div'][4]/*[local-name()='div'][5]/*[local-name()='p'][1]"
		};
		for (int i = 0; i < resultElements.size(); i++) {
			ResultElement result = resultElements.get(i);
			String xpath = result.getXPath();
			Assert.assertEquals("xpath", xpath, xpaths[i]);
		}
	}

}

	
