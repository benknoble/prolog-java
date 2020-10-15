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

  public static List<InvocationMatcher> invocationMatchers(PrologParser p, PrologLexer l) {
    var m = new ParseTreePatternMatcher(l, p);
    m.setDelimiters("<", ">", "`");
    return List.of(

        // binary700
        new LT(m),
        new EQ(m),
        new EQDD(m),
        new EQATEQ(m),
        new NEQATEQ(m),
        new EQCEQ(m),
        new LEQ(m),
        new EQEQ(m),
        new EQNEQ(m),
        new GT(m),
        new GEQ(m),
        new ATLT(m),
        new ATLEQ(m),
        new ATGT(m),
        new ATGEQ(m),
        new NEQ(m),
        new NEQEQ(m),
        new AS(m),
        new IS(m),
        new GTCLT(m),
        new CLT(m)

        );
  }

}

abstract class BaseInvocationMatcher implements InvocationMatcher {
  private final String name;
  private final ParseTreePattern pattern;

  BaseInvocationMatcher(String name, String pattern, int rule, ParseTreePatternMatcher m) {
    this.name = name;
    this.pattern = m.compile(pattern, rule);
  }

  @Override
  public String name() { return name; }

  @Override
  public ParseTreePattern pattern() { return pattern; }

}

// binary700 {{{
abstract class Binary700 extends BaseInvocationMatcher {
  Binary700(String name, ParseTreePatternMatcher m) {
    super(
        name,
        String.format("<lhs:binaryRight600> %s <rhs:binaryRight600>", name),
        PrologParser.RULE_binary700,
        m);
  }

  Binary700(String name, String operator, ParseTreePatternMatcher m) {
    super(
        name,
        String.format("<lhs:binaryRight600> %s <rhs:binaryRight600>", operator),
        PrologParser.RULE_binary700,
        m);
  }

  @Override
  public List<ParseTree> getArgs(ParseTreeMatch m) {
    return List.of(m.get("lhs"), m.get("rhs"));
  }
}

class LT extends Binary700 {
  LT(ParseTreePatternMatcher m) {
    super("<", "`<", m);
  }
}

class EQ extends Binary700 {
  EQ(ParseTreePatternMatcher m) {
    super("=", m);
  }
}

class EQDD extends Binary700 {
  EQDD(ParseTreePatternMatcher m) {
    super("=..", m);
  }
}

class EQATEQ extends Binary700 {
  EQATEQ(ParseTreePatternMatcher m) {
    super("=@=", m);
  }
}

class NEQATEQ extends Binary700 {
  NEQATEQ(ParseTreePatternMatcher m) {
    super("\\=@=", m);
  }
}

class EQCEQ extends Binary700 {
  EQCEQ(ParseTreePatternMatcher m) {
    super("=:=", m);
  }
}

class LEQ extends Binary700 {
  LEQ(ParseTreePatternMatcher m) {
    super("=<", "=`<", m);
  }
}

class EQEQ extends Binary700 {
  EQEQ(ParseTreePatternMatcher m) {
    super("==", m);
  }
}

class EQNEQ extends Binary700 {
  EQNEQ(ParseTreePatternMatcher m) {
    super("=\\=", m);
  }
}

class GT extends Binary700 {
  GT(ParseTreePatternMatcher m) {
    super(">", "`>", m);
  }
}

class GEQ extends Binary700 {
  GEQ(ParseTreePatternMatcher m) {
    super(">=", "`>=", m);
  }
}

class ATLT extends Binary700 {
  ATLT(ParseTreePatternMatcher m) {
    super("@<", "@`<", m);
  }
}

class ATLEQ extends Binary700 {
  ATLEQ(ParseTreePatternMatcher m) {
    super("@=<", "@=`<", m);
  }
}

class ATGT extends Binary700 {
  ATGT(ParseTreePatternMatcher m) {
    super("@>", "@`>", m);
  }
}

class ATGEQ extends Binary700 {
  ATGEQ(ParseTreePatternMatcher m) {
    super("@>=", "@`>=", m);
  }
}

class NEQ extends Binary700 {
  NEQ(ParseTreePatternMatcher m) {
    super("\\=", m);
  }
}

class NEQEQ extends Binary700 {
  NEQEQ(ParseTreePatternMatcher m) {
    super("\\==", m);
  }
}

class AS extends Binary700 {
  AS(ParseTreePatternMatcher m) {
    super("as", m);
  }
}

class IS extends Binary700 {
  IS(ParseTreePatternMatcher m) {
    super("is", m);
  }
}

class GTCLT extends Binary700 {
  GTCLT(ParseTreePatternMatcher m) {
    super(">:<", "`>:`<", m);
  }
}

class CLT extends Binary700 {
  CLT(ParseTreePatternMatcher m) {
    super(":<", ":`<", m);
  }
}
// }}}
