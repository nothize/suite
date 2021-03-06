package org.suite;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.suite.kb.RuleSet;

public class ProverTest {

	@Test
	public void testProve1() {
		RuleSet rs = new RuleSet();
		SuiteUtil.addRule(rs, "mem ([.e, _], .e)");
		SuiteUtil.addRule(rs, "mem ([_, .remains], .e) :- mem (.remains, .e)");

		assertTrue(SuiteUtil.proveThis(rs,
				".l = [1, 2,], find.all .v (mem (.l, .v)) .l"));
	}

	@Test
	public void testProve() {
		RuleSet rs = new RuleSet();
		SuiteUtil.addRule(rs, "a");
		SuiteUtil.addRule(rs, "b");
		SuiteUtil.addRule(rs, "c");
		SuiteUtil.addRule(rs, "a b .v :- fail");
		SuiteUtil.addRule(rs, "a b c");
		SuiteUtil.addRule(rs, ".var is a man");

		assertTrue(SuiteUtil.proveThis(rs, ""));
		assertTrue(SuiteUtil.proveThis(rs, "a"));
		assertTrue(SuiteUtil.proveThis(rs, "a, b"));
		assertTrue(SuiteUtil.proveThis(rs, "a, b, c"));
		assertTrue(SuiteUtil.proveThis(rs, "a, fail; b"));
		assertTrue(SuiteUtil.proveThis(rs, "a b c"));
		assertTrue(SuiteUtil.proveThis(rs, "abc is a man"));
		assertTrue(SuiteUtil.proveThis(rs, ".v = a, .v = b; .v = c"));
		assertTrue(SuiteUtil.proveThis(rs, "[1, 2, 3] = [1, 2, 3]"));

		assertFalse(SuiteUtil.proveThis(rs, "fail"));
		assertFalse(SuiteUtil.proveThis(rs, "d"));
		assertFalse(SuiteUtil.proveThis(rs, "a, fail"));
		assertFalse(SuiteUtil.proveThis(rs, "fail, a"));
		assertFalse(SuiteUtil.proveThis(rs, "a b d"));
		assertFalse(SuiteUtil.proveThis(rs, "a = b"));
		assertFalse(SuiteUtil.proveThis(rs, ".v = a, .v = b"));
	}

	@Test
	public void testMember() {
		RuleSet rs = new RuleSet();
		SuiteUtil.addRule(rs, "mem ([.e, _], .e)");
		SuiteUtil.addRule(rs, "mem ([_, .remains], .e) :- mem (.remains, .e)");

		assertTrue(SuiteUtil.proveThis(rs, "mem ([a, ], a)"));
		assertTrue(SuiteUtil.proveThis(rs, "mem ([a, b, c, ], .v)"));
		assertTrue(SuiteUtil.proveThis(rs,
				".l = [1, 2, 3,], find.all .v (mem (.l, .v)) .l"));
		assertFalse(SuiteUtil.proveThis(rs, "mem ([a, b, c, ], d)"));
	}

	@Test
	public void testAppend() {
		RuleSet rs = new RuleSet();
		SuiteUtil.addRule(rs, "app () .l .l");
		SuiteUtil.addRule(rs, "app (.h, .r) .l (.h, .r1) :- app .r .l .r1");

		assertTrue(SuiteUtil.proveThis(rs,
				"app (a, b, c,) (d, e,) (a, b, c, d, e,)"));
	}

	@Test
	public void testFindAll() {
		assertTrue(proveThis("find.all .v (.v = a; .v = b; .v = c) .results"
				+ ", .results = (a, b, c, )"));
	}

	@Test
	public void testCut() {
		RuleSet rs = new RuleSet();
		SuiteUtil.addRule(rs, "a :- !, fail");
		SuiteUtil.addRule(rs, "a");
		assertFalse(SuiteUtil.proveThis(rs, "a"));
	}

	@Test
	public void testWrite() {
		assertTrue(proveThis("write (1 + 2 * 3), nl"));
		assertTrue(proveThis("write \"Don\"\"t forget%0A4 Jun 1989\", nl"));
	}

	private boolean proveThis(String s) {
		return SuiteUtil.proveThis(new RuleSet(), s);
	}

}
