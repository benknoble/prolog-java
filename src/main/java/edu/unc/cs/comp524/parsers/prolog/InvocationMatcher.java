package edu.unc.cs.comp524.parsers.prolog;

import java.util.*;
import java.util.stream.*;

import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.tree.pattern.*;

public interface InvocationMatcher {
  public String name();
  public List<ParseTree> getArgs(ParseTreeMatch m);
  public ParseTreePattern pattern();

  public default List<RuleInvocation> invocations(ParseTree tree) {
    return invocations(tree, "//*");
  }

  public default List<RuleInvocation> invocations(ParseTree tree, String xpath) {
    return pattern().findAll(tree, xpath)
      .stream()
      .map(m -> new ARuleInvocation(name(), getArgs(m)))
      .collect(Collectors.toList());
  }

  public static List<InvocationMatcher> invocationMatchers(PrologParser p) {
    return List.of(
        new LEQ(p)
        );
  }

}

class LEQ implements InvocationMatcher {
  private final static String name = "=<";
  private final static String pattern =
    "<lhs:binaryRight600> =\\< <rhs:binaryRight600>";

  private ParseTreePattern compiled;
  LEQ(PrologParser parser) {
    compiled = parser.compileParseTreePattern(
        pattern,
        PrologParser.RULE_binary700);
  }

  @Override
  public String name() { return name; }

  @Override
  public ParseTreePattern pattern() { return compiled; }

  @Override
  public List<ParseTree> getArgs(ParseTreeMatch m) {
    return List.of(m.get("lhs"), m.get("rhs"));
  }
}
