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
    var input = new ANTLRInputStream(String.join("\n"
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
          , "even(0)."
          , "even(X) :- X > 0, XN is X-1, odd(XN)."
          , "odd(1)."
          , "odd(X) :- X > 1, XN is X-1, even(XN)."
          , "recN(X) :- X > 0, XN is X-1, recN(XN, X)."
          , "recN(1, 0)."
          , "recN(X, N) :- N > 0, NN is N-1, recN(X, NN)."
          , ""));
    var lexer = new PrologLexer(input);
    var tokens = new CommonTokenStream(lexer);
    var parser = new PrologParser(tokens);
    var tree = parser.p_text();

    var collector = new RelationCollectorListener(tokens, lexer, parser);
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
          "even",
          "odd",
          "recN",
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
    assertFalse(program.isRecursive("recIndirect"));
    assertTrue(program.isRecursive("recN"));
  }

  @Test
  public void testContainsRecursive() {
    assertFalse(program.containsRecursive("fact"));
    assertFalse(program.containsRecursive("foo"));
    assertFalse(program.containsRecursive("baz"));
    // rec contains rec, which is recursive
    assertTrue(program.containsRecursive("rec"));
    assertTrue(program.containsRecursive("recIndirect"));
    assertTrue(program.containsRecursive("recN"));
  }

  @Test
  public void testUndefined() {
    assertThat(program
        .undefined()
        .stream()
        .map(RuleInvocation::name)
        .collect(Collectors.toSet()),
        IsIterableContainingInAnyOrder.containsInAnyOrder(
          ">",
          "is",
          "-",
          "bar"));

  }

  @Test
  public void testDepth() {
    assertThat(program.depth("fact"), is(0));
    assertThat(program.depth("foo"), is(2));
    assertThat(program.depth("baz"), is(1));
    assertThat(program.depth("rec"), is(2));
    assertThat(program.depth("recIndirect"), is(3));
    assertThat(program.depth("even"), is(3));
    assertThat(program.depth("odd"), is(3));
  }

  @Test
  public void testNoMagicNumbers() {
    assertTrue(program.noMagicNumbers());

    var input = new ANTLRInputStream(String.join("\n"
          , "someRule(123) :- true."
          , ""));
    var lexer = new PrologLexer(input);
    var tokens = new CommonTokenStream(lexer);
    var parser = new PrologParser(tokens);
    var tree = parser.p_text();

    var collector = new RelationCollectorListener(tokens, lexer, parser);
    ParseTreeWalker.DEFAULT.walk(collector, tree);
    var numProgram = collector.program();
    assertFalse(numProgram.noMagicNumbers());

    input = new ANTLRInputStream(String.join("\n"
          , "someRule(X) :- otherRule(123)."
          , ""));
    lexer = new PrologLexer(input);
    tokens = new CommonTokenStream(lexer);
    parser = new PrologParser(tokens);
    tree = parser.p_text();

    collector = new RelationCollectorListener(tokens, lexer, parser);
    ParseTreeWalker.DEFAULT.walk(collector, tree);
    numProgram = collector.program();
    assertFalse(numProgram.noMagicNumbers());

    input = new ANTLRInputStream(String.join("\n"
          , "someRule(X) :- X = [123]."
          , ""));
    lexer = new PrologLexer(input);
    tokens = new CommonTokenStream(lexer);
    parser = new PrologParser(tokens);
    tree = parser.p_text();

    collector = new RelationCollectorListener(tokens, lexer, parser);
    ParseTreeWalker.DEFAULT.walk(collector, tree);
    numProgram = collector.program();
    assertFalse(numProgram.noMagicNumbers());

    input = new ANTLRInputStream(String.join("\n"
          , "someRule(X1) :- X1 = []."
          , ""));
    lexer = new PrologLexer(input);
    tokens = new CommonTokenStream(lexer);
    parser = new PrologParser(tokens);
    tree = parser.p_text();

    collector = new RelationCollectorListener(tokens, lexer, parser);
    ParseTreeWalker.DEFAULT.walk(collector, tree);
    numProgram = collector.program();
    assertTrue(numProgram.noMagicNumbers());

  }

}
