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

abstract class BaseInvocationMatcher implements InvocationMatcher {
  private final String name;
  private final ParseTreePattern pattern;

  BaseInvocationMatcher(String name, String pattern, int rule, PrologParser parser) {
    this.name = name;
    this.pattern = parser.compileParseTreePattern(pattern, rule);
  }

  @Override
  public String name() { return name; }

  @Override
  public ParseTreePattern pattern() { return pattern; }

}

class LEQ extends BaseInvocationMatcher {
  LEQ(PrologParser parser) {
    super(
        "=<",
        "<lhs:binaryRight600> =\\< <rhs:binaryRight600>",
        PrologParser.RULE_binary700,
        parser);
  }

  @Override
  public List<ParseTree> getArgs(ParseTreeMatch m) {
    return List.of(m.get("lhs"), m.get("rhs"));
  }
}
