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

        // binary1200
        new BinaryCEQ(m),
        new HHARROW(m),

        // unary1200
        new UnaryCEQ(m),
        new QH(m),

        // unary1150
        new DYNAMIC(m),
        new DISCONTIGUOUS(m),
        new INITIALIZATION(m),
        new METAPREDICATE(m),
        new MODULETRANSPARENT(m),
        new MULTIFILE(m),
        new PUBLIC(m),
        new THREADLOCAL(m),
        new THREADINITIALIZATION(m),
        new VOLATILE(m),

        // binaryRight1100
        new SEMI(m),
        new BAR(m),

        // binaryRight1050
        new HARROW(m),
        new SHARROW(m),

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

// binary1200 {{{
abstract class Binary1200 extends BaseInvocationMatcher {
  Binary1200(String name, ParseTreePatternMatcher m) {
    super(
        name,
        String.format("<lhs:unary1200> %s <rhs:unary1200>", name),
        PrologParser.RULE_binary1200,
        m);
  }

  Binary1200(String name, String operator, ParseTreePatternMatcher m) {
    super(
        name,
        String.format("<lhs:unary1200> %s <rhs:unary1200>", operator),
        PrologParser.RULE_binary1200,
        m);
  }

  @Override
  public List<ParseTree> getArgs(ParseTreeMatch m) {
    return List.of(m.get("lhs"), m.get("rhs"));
  }
}

class BinaryCEQ extends Binary1200 {
  BinaryCEQ(ParseTreePatternMatcher m) {
    super(":-", m);
  }
}

class HHARROW extends Binary1200 {
  HHARROW(ParseTreePatternMatcher m) {
    super("-->", "--`>", m);
  }
}
// binary1200 }}}

// unary1200 {{{
abstract class Unary1200 extends BaseInvocationMatcher {
  Unary1200(String name, ParseTreePatternMatcher m) {
    super(
        name,
        String.format("%s <rhs:unary1150>", name),
        PrologParser.RULE_unary1200,
        m);
  }

  Unary1200(String name, String operator, ParseTreePatternMatcher m) {
    super(
        name,
        String.format("%s <rhs:unary1150>", operator),
        PrologParser.RULE_unary1200,
        m);
  }

  @Override
  public List<ParseTree> getArgs(ParseTreeMatch m) {
    return List.of(m.get("rhs"));
  }
}

class UnaryCEQ extends Unary1200 {
  UnaryCEQ(ParseTreePatternMatcher m) {
    super(":-", m);
  }
}

class QH extends Binary1200 {
  QH(ParseTreePatternMatcher m) {
    super("?-", m);
  }
}
// unary1200 }}}

// unary1150 {{{
abstract class Unary1150 extends BaseInvocationMatcher {
  Unary1150(String name, ParseTreePatternMatcher m) {
    super(
        name,
        String.format("%s <rhs:binaryRight1100>", name),
        PrologParser.RULE_unary1150,
        m);
  }

  @Override
  public List<ParseTree> getArgs(ParseTreeMatch m) {
    return List.of(m.get("rhs"));
  }
}

class DYNAMIC extends Unary1150 {
  DYNAMIC(ParseTreePatternMatcher m) {
    super("dynamic", m);
  }
}

class DISCONTIGUOUS extends Unary1150 {
  DISCONTIGUOUS(ParseTreePatternMatcher m) {
    super("discontiguous", m);
  }
}

class INITIALIZATION extends Unary1150 {
  INITIALIZATION(ParseTreePatternMatcher m) {
    super("initialization", m);
  }
}

class METAPREDICATE extends Unary1150 {
  METAPREDICATE(ParseTreePatternMatcher m) {
    super("meta_predicate", m);
  }
}

class MODULETRANSPARENT extends Unary1150 {
  MODULETRANSPARENT(ParseTreePatternMatcher m) {
    super("module_transparent", m);
  }
}

class MULTIFILE extends Unary1150 {
  MULTIFILE(ParseTreePatternMatcher m) {
    super("multfile", m);
  }
}

class PUBLIC extends Unary1150 {
  PUBLIC(ParseTreePatternMatcher m) {
    super("public", m);
  }
}

class THREADLOCAL extends Unary1150 {
  THREADLOCAL(ParseTreePatternMatcher m) {
    super("thread_local", m);
  }
}

class THREADINITIALIZATION extends Unary1150 {
  THREADINITIALIZATION(ParseTreePatternMatcher m) {
    super("thread_initialization", m);
  }
}

class VOLATILE extends Unary1150 {
  VOLATILE(ParseTreePatternMatcher m) {
    super("volatile", m);
  }
}
// unary1150 }}}

// binaryRight1100 {{{
abstract class BinaryRight1100 extends BaseInvocationMatcher {
  BinaryRight1100(String name, ParseTreePatternMatcher m) {
    super(
        name,
        String.format("<lhs:binaryRight1050> %s <rhs:binaryRight1050>", name),
        PrologParser.RULE_binaryRight1100,
        m);
  }

  @Override
  public List<ParseTree> getArgs(ParseTreeMatch m) {
    return List.of(m.get("lhs"), m.get("rhs"));
  }
}

class SEMI extends BinaryRight1100 {
  SEMI(ParseTreePatternMatcher m) {
    super(";", m);
  }
}

class BAR extends BinaryRight1100 {
  BAR(ParseTreePatternMatcher m) {
    super("|", m);
  }
}
// binaryRight1100 }}}

// binaryRight1050 {{{
abstract class BinaryRight1050 extends BaseInvocationMatcher {
  BinaryRight1050(String name, ParseTreePatternMatcher m) {
    super(
        name,
        String.format("<lhs:binaryRight1000> %s <rhs:binaryRight1000>", name),
        PrologParser.RULE_binaryRight1050,
        m);
  }

  BinaryRight1050(String name, String operator, ParseTreePatternMatcher m) {
    super(
        name,
        String.format("<lhs:binaryRight1000> %s <rhs:binaryRight1000>", operator),
        PrologParser.RULE_unary1200,
        m);
  }

  @Override
  public List<ParseTree> getArgs(ParseTreeMatch m) {
    return List.of(m.get("lhs"), m.get("rhs"));
  }
}

class HARROW extends BinaryRight1050 {
  HARROW(ParseTreePatternMatcher m) {
    super("->", "-`>", m);
  }
}

class SHARROW extends BinaryRight1050 {
  SHARROW(ParseTreePatternMatcher m) {
    super("*->", "*-`>", m);
  }
}
// binaryRight1050 }}}

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
