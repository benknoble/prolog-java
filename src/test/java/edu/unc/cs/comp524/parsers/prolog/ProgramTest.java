package edu.unc.cs.comp524.parsers.prolog;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import static org.junit.Assert.*;
import org.junit.*;
import static org.hamcrest.CoreMatchers.*;
import org.hamcrest.collection.*;

public class ProgramTest {
  private static Program program;
  @BeforeClass
  public static void setup() {
    var input = CharStreams.fromString(String.join("\n"
          , "% a sample program"
          , "foo :- bar."
          , ""));
    var lexer = new PrologLexer(input);
    var tokens = new CommonTokenStream(lexer);
    var parser = new PrologParser(tokens);
    var tree = parser.p_text();

    var collector = new RelationCollectorListener(tokens, parser);
    ParseTreeWalker.DEFAULT.walk(collector, tree);
    program = collector.program();
  }

  @Test
  public void testClauses() {
    assertThat(program.clauses(), allOf(
        IsMapContaining.hasEntry(is("foo"), anything())));
  }
}
