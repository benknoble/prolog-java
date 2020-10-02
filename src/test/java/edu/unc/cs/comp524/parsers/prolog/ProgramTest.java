package edu.unc.cs.comp524.parsers.prolog;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import static org.junit.Assert.*;
import org.junit.*;
import static org.hamcrest.CoreMatchers.*;
import org.hamcrest.collection.*;

import java.util.stream.*;

public class ProgramTest {
  private static Program program;
  @BeforeClass
  public static void setup() {
    var input = CharStreams.fromString(String.join("\n"
          , "% a sample program"
          , ""
          , "fact(true)."
          , "% foo if bar"
          , "foo :- bar(1)."
          , "foo(1)."
          , "baz(X) :- X."
          , "rec(0)."
          , "rec(X) :- X > 0, XN is X-1, rec(XN)."
          , "recIndirect(X) :- X > 0, rec(X)."
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
    assertThat(program.names(),
        IsIterableContainingInAnyOrder.containsInAnyOrder(
          "fact",
          "foo",
          "rec",
          "recIndirect",
          "baz"));
  }

  @Test
  public void testRelationsWithComments() {
    assertThat(program
        .relationsWithComments()
        .stream()
        .map(Relation::name)
        .collect(Collectors.toList()),
        IsIterableContainingInAnyOrder.containsInAnyOrder("foo"));
  }

  @Test
  public void testIsRecursive() {
    assertFalse(program.isRecursive("fact"));
    assertFalse(program.isRecursive("foo"));
    assertFalse(program.isRecursive("baz"));
    assertTrue(program.isRecursive("rec"));
  }

  @Test
  public void testContainsRecursive() {
    assertFalse(program.containsRecursive("fact"));
    assertFalse(program.containsRecursive("foo"));
    assertFalse(program.containsRecursive("baz"));
    // rec contains rec, which is recursive
    assertTrue(program.containsRecursive("rec"));
    assertTrue(program.containsRecursive("recIndirect"));
  }

  @Test
  public void testUndefined() {
    assertThat(program
        .undefined()
        .stream()
        .map(RuleInvocation::name)
        .collect(Collectors.toList()),
        IsIterableContainingInAnyOrder.containsInAnyOrder(
          ">",
          ">", // appears twice
          "is",
          "-",
          "bar"));

  }

}