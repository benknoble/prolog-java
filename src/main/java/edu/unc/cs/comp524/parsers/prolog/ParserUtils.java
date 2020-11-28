package edu.unc.cs.comp524.parsers.prolog;

import java.util.*;
import java.util.stream.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.tree.pattern.*;

/**
 * Static utilities for the package
 */
public class ParserUtils {

  /**
   * A {@link ParseTreePattern} that matches fact definitions.
   */
  public static final ParseTreePattern factPattern(PrologParser parser) {
    return parser.compileParseTreePattern(
        "<atom>(<termlist>).",
        PrologParser.RULE_clause);
  }

  /**
   * A {@link ParseTreePattern} that matches rule definitions.
   */
  public static final ParseTreePattern rulePattern(PrologParser parser) {
    return parser.compileParseTreePattern(
        "<atom>(<termlist>) :- <term>.",
        PrologParser.RULE_clause);
  }

  /**
   * A {@link ParseTreePattern} that matches rule definitions with no arguments.
   */
  public static final ParseTreePattern rule0Pattern(PrologParser parser) {
    return parser.compileParseTreePattern(
        "<atom> :- <term>.",
        PrologParser.RULE_clause);
  }

  /**
   * A {@link ParseTreePattern} that matches rule invocations.
   */
  public static final ParseTreePattern invocationPattern(PrologParser parser) {
    return parser.compileParseTreePattern(
        "<atom>(<termlist>)",
        PrologParser.RULE_base_term);
  }

  /**
   * The number of lines in a token's {@link Token#getText}.
   */
  public static int countLines(final Token t) {
    return t.getText().split("\n").length - 1;
  }

  /**
   * Concatentates all of the lists.
   * <p>
   * Would be written as {@code join = reduce (++) []} in pseudo-Haskell
   */
  public static <T> List<T> join(List<T>... lists) {
    return
      Stream.of(lists)
      .flatMap(Collection::stream)
      .collect(Collectors.toList());
  }
}
