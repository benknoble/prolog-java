package edu.unc.cs.comp524.parsers.prolog;

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

  public static int countLines(final Token t) {
    return t.getText().split("\n").length - 1;
  }
}
