package ml2.logic.games.dsl;

import org.junit.Test;

public class DSLCompilerTest {

	@Test
	public void test() {
		String s = "domain \"cars\"\n"
				+ "concept \"top\" = \"blue\", \"green\", \"red\"\n"
				+ "concept \"body\" = \"blue\", \"green\", \"red\"\n"
				;
		
		new DSLCompiler().compile(s);
		
	}

	@Test
	public void testWithIds() {
		String s = "domain cars\n"
				+ "concept top = \"blue\", \"green\", \"red\"\n"
				+ "concept body = \"blue\", \"green\", \"red\"\n"
				;
		
		new DSLCompiler().compile(s);
		
	}
}
