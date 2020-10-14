package edu.unc.cs.comp524.parsers.prolog;

import java.util.*;
import java.util.stream.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.tree.pattern.*;

public class ParserUtils {
  public static final ParseTreePattern factPattern(PrologParser parser) {
    return parser.compileParseTreePattern(
        "<atom>(<termlist>).",
        PrologParser.RULE_clause);
  }

  public static final ParseTreePattern rulePattern(PrologParser parser) {
    return parser.compileParseTreePattern(
        "<atom>(<termlist>) :- <term>.",
        PrologParser.RULE_clause);
  }

  public static final ParseTreePattern rule0Pattern(PrologParser parser) {
    return parser.compileParseTreePattern(
        "<atom> :- <term>.",
        PrologParser.RULE_clause);
  }

  public static final ParseTreePattern invocationPattern(PrologParser parser) {
    return parser.compileParseTreePattern(
        "<atom>(<termlist>)",
        PrologParser.RULE_term);
  }

  public static final ParseTreePattern binopPattern(PrologParser parser) {
    return parser.compileParseTreePattern(
        "<term> <operator> <term>",
        PrologParser.RULE_term);
  }

  public static final ParseTreePattern unopPattern(PrologParser parser) {
    return parser.compileParseTreePattern(
        "<operator> <term>",
        PrologParser.RULE_term);
  }

  public static int countLines(final Token t) {
    return t.getText().split("\n").length - 1;
  }

  public static <T> List<T> join(List<T>... lists) {
    return
      Stream.of(lists)
      .flatMap(Collection::stream)
      .collect(Collectors.toList());
  }
}
