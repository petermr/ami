package org.xmlcml.ami2.dictionary;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;


public class BloomFilterTest {

   
   private static final String M = "M";
   private static final String L = "L";
   private static final String JUNK = "Junk";
   
	Funnel<String> stringFunnel = new Funnel<String>() {
		   public void funnel(String person, PrimitiveSink into) {
		       into.putUnencodedChars(person);
		   }
		 };
	private BloomFilter<String> f;




	@Test
	public void testFunctionality() {
		
		// high number makes high probabilities but larger resources reqd
		int expectedInsertions = 28;
		f = BloomFilter.create(stringFunnel, expectedInsertions);
		f.put(JUNK);
		f.put(M);
		// add lowercase characters
		for (int i = 96; i <= 122; i++) {
			String s = String.valueOf((char) i);
			f.put(s);
		}
		

		// probability of false positives
		Assert.assertEquals(0.012171090726042166, f.expectedFpp(), 1.0E-10);
		// probably contains M
		Assert.assertTrue(f.mightContain(M));
		// probably contains m
		Assert.assertTrue(f.mightContain("m"));
		Assert.assertTrue(f.mightContain(JUNK));
		// definitely does not contain "fred"
		Assert.assertFalse(f.mightContain("fred"));
		// definitely does not contain "N"
		Assert.assertFalse(f.mightContain("N"));
	}
	
	
}
