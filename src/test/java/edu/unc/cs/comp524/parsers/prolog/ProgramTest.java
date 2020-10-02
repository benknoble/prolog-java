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
          , "fact(true)."
          , "foo :- bar."
          , "foo(1)."
          , "baz(X) :- X."
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
        IsMapContaining.hasEntry(is("fact"), anything()),
        IsMapContaining.hasEntry(is("foo"), anything()),
        IsMapContaining.hasEntry(is("baz"), anything())));
  }

  @Test
  public void testArity() {
    assertThat(program.arity("fact"),
          IsIterableContainingInAnyOrder.containsInAnyOrder(1));
    assertThat(program.arity("foo"),
          IsIterableContainingInAnyOrder.containsInAnyOrder(0, 1));
    assertThat(program.arity("baz"),
          IsIterableContainingInAnyOrder.containsInAnyOrder(1));
    assertThat(program.arity("dne"), IsEmptyCollection.empty());
  }

  @Test
  public void testNames() {
    assertThat(program.names(), hasItems("fact", "foo", "baz"));
  }
}
