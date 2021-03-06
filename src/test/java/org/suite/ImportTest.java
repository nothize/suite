package org.suite;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.suite.kb.RuleSet;

public class ImportTest {

	@Test
	public void testImport() throws IOException {
		RuleSet rs = new RuleSet();
		SuiteUtil.importResource(rs, "auto.sl");
		assertTrue(SuiteUtil.proveThis(rs, "list"));
		assertTrue(SuiteUtil.proveThis(rs, "list repeat"));
	}

}
