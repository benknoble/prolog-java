package edu.unc.cs.comp524.parsers.prolog;

import java.util.*;
import java.util.stream.*;

import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.tree.pattern.*;

/**
 * A matcher against invocations of rules that knows how to process invocations
 * and create {@link RuleInvocation}s.
 * <p>
 * Primarily used in {@link RelationCollectorListener} to match invocations in
 * the body of predicates.
 * <p>
 * Implementation notes
 * <p>
 * See the source; there are many un-exposed classes that implement matchers for
 * all the operators described in the grammer (cf. {@link #invocationMatchers}).
 * <p>
 * We use a custom {@link ParseTreePatternMatcher} with delimiters set by {@link
 * ParseTreePatternMatcher#setDelimiters} to allow the use of slashes in the
 * patterns.
 * <p>
 * The names of classes use a bit of shorthand:
 * <ul>
 * <li>C: colon (:)
 * <li>H: hyphen (-)
 * <li>Q: question mark (?)
 * <li>SEMI: semi-colon (;)
 * <li>BAR: bar or pipe (|)
 * <li>HARROW: -&gt;
 * <li>S: star (*)
 * <li>EQ: equals (=)
 * <li>NOT: prolog's not (\+)
 * <li>LT: less-than (&lt;)
 * <li>LEQ: less-than-equal (=&lt;)
 * <li>D: dot (.)
 * <li>AT: aerobase (@)
 * <li>GT: greater-than (&gt;)
 * <li>GEQ: greater-than-equal (&gt;=)
 * <li>NEQ: not-equal (\=)
 * <li>AND: and (/\)
 * <li>OR: or (\/)
 * <li>SLASH: slash (/)
 * <li>CARET: caret (^)
 * <li>BSLASH: backslash (\)
 * <li>DOLLAR: dollar-sign ($)
 * </ul>
 * <p>
 * Most others that are not built from this shorthand are represented by name
 * directly (e.g., MOD for mod, DYNAMIC for dynamic).
 */
public interface InvocationMatcher {

  /**
   * The name of the rule the invocation matches
   */
  public String name();

  /**
   * Computes the arguments of the invocation given a {@link
   * org.antlr.v4.runtime.tree.pattern.ParseTreeMatch} created from {@link
   * #pattern()}.
   */
  public List<ParseTree> getArgs(ParseTreeMatch m);

  /**
   * The pattern to be used to match with
   */
  public ParseTreePattern pattern();

  /**
   * Find all invocations in {@code tree}.
   * <p>
   * The default implementation is equivalent to {@link #invocations(ParseTree,
   * String)}{@code (tree, "//*")}.
   */
  public default List<RuleInvocation> invocations(ParseTree tree) {
    return invocations(tree, "//*");
  }

  /**
   * Find all invocations in {@code tree} that match {@code xpath}.
   *
   * @see ParseTreePattern#findAll
   */
  public default List<RuleInvocation> invocations(ParseTree tree, String xpath) {
    return pattern().findAll(tree, xpath)
      .stream()
      .map(m -> new ARuleInvocation(name(), getArgs(m)))
      .collect(Collectors.toList());
  }

  /**
   * The default matchers for all operators defined in the grammar.
   * <p>
   * See implementation notes above and the source for their definition.
   */
  public static List<InvocationMatcher> invocationMatchers(PrologParser p, PrologLexer l) {
    var m = new ParseTreePatternMatcher(l, p);
    m.setDelimiters("<", ">", "`");
    return List.of(

        // binary1200
        new BinaryCH(m),
        new HHARROW(m),

        // unary1200
        new UnaryCH(m),
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

        // binaryRight1000
        new COMMA(m),

        // binary990
        new CEQ(m),

        // unary900
        new NOT(m),

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
        new CLT(m),

        // binaryRight600
        new C(m),

        // binaryLeft500
        new BinaryPLUS(m),
        new BinaryH(m),
        new AND(m),
        new OR(m),
        new XOR(m),

        // unary500
        new Q(m),

        // binaryLeft400
        new S(m),
        new SLASH(m),
        new SLASHSLASH(m),
        new DIV(m),
        new RDIV(m),
        new LTLT(m),
        new GTGT(m),
        new REM(m),
        new MOD(m),

        // binary200
        new SS(m),

        // binaryRight200
        new CARET(m),

        // unary200
        new UnaryPLUS(m),
        new UnaryH(m),
        new BSLASH(m),

        // unary1
        new DOLLAR(m)

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

class BinaryCH extends Binary1200 {
  BinaryCH(ParseTreePatternMatcher m) {
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

class UnaryCH extends Unary1200 {
  UnaryCH(ParseTreePatternMatcher m) {
    super(":-", m);
  }
}

class QH extends Unary1200 {
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
    super("multifile", m);
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
        String.format("<lhs:binaryRight1050> %s <rhs:binaryRight1100>", name),
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
        String.format("<lhs:binaryRight1000> %s <rhs:binaryRight1050>", name),
        PrologParser.RULE_binaryRight1050,
        m);
  }

  BinaryRight1050(String name, String operator, ParseTreePatternMatcher m) {
    super(
        name,
        String.format("<lhs:binaryRight1000> %s <rhs:binaryRight1050>", operator),
        PrologParser.RULE_binaryRight1050,
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

// binaryRight1000 {{{
abstract class BinaryRight1000 extends BaseInvocationMatcher {
  BinaryRight1000(String name, ParseTreePatternMatcher m) {
    super(
        name,
        String.format("<lhs:binary990> %s <rhs:binaryRight1000>", name),
        PrologParser.RULE_binaryRight1000,
        m);
  }

  @Override
  public List<ParseTree> getArgs(ParseTreeMatch m) {
    return List.of(m.get("lhs"), m.get("rhs"));
  }
}

class COMMA extends BinaryRight1000 {
  COMMA(ParseTreePatternMatcher m) {
    super(",", m);
  }
}
// binaryRight1000 }}}

// binary990 {{{
abstract class Binary990 extends BaseInvocationMatcher {
  Binary990(String name, ParseTreePatternMatcher m) {
    super(
        name,
        String.format("<lhs:unary900> %s <rhs:unary900>", name),
        PrologParser.RULE_binary990,
        m);
  }

  @Override
  public List<ParseTree> getArgs(ParseTreeMatch m) {
    return List.of(m.get("lhs"), m.get("rhs"));
  }
}

class CEQ extends Binary990 {
  CEQ(ParseTreePatternMatcher m) {
    super(":=", m);
  }
}
// binary990 }}}

// unary900 {{{
abstract class Unary900 extends BaseInvocationMatcher {
  Unary900(String name, ParseTreePatternMatcher m) {
    super(
        name,
        String.format("%s <rhs:unary900>", name),
        PrologParser.RULE_unary900,
        m);
  }

  @Override
  public List<ParseTree> getArgs(ParseTreeMatch m) {
    return List.of(m.get("rhs"));
  }
}

class NOT extends Unary900 {
  NOT(ParseTreePatternMatcher m) {
    super("\\+", m);
  }
}

// unary900 }}}

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
// binary700 }}}

// binaryRight600 {{{
abstract class BinaryRight600 extends BaseInvocationMatcher {
  BinaryRight600(String name, ParseTreePatternMatcher m) {
    super(
        name,
        String.format("<lhs:binaryLeft500> %s <rhs:binaryRight600>", name),
        PrologParser.RULE_binaryRight600,
        m);
  }

  @Override
  public List<ParseTree> getArgs(ParseTreeMatch m) {
    return List.of(m.get("lhs"), m.get("rhs"));
  }
}

class C extends BinaryRight600 {
  C(ParseTreePatternMatcher m) {
    super(":", m);
  }
}
// binaryRight600 }}}

// binaryLeft500 {{{
abstract class BinaryLeft500 extends BaseInvocationMatcher {
  BinaryLeft500(String name, ParseTreePatternMatcher m) {
    super(
        name,
        String.format("<lhs:binaryLeft500> %s <rhs:unary500>", name),
        PrologParser.RULE_binaryLeft500,
        m);
  }

  @Override
  public List<ParseTree> getArgs(ParseTreeMatch m) {
    return List.of(m.get("lhs"), m.get("rhs"));
  }
}

class BinaryPLUS extends BinaryLeft500 {
  BinaryPLUS(ParseTreePatternMatcher m) {
    super("+", m);
  }
}

class BinaryH extends BinaryLeft500 {
  BinaryH(ParseTreePatternMatcher m) {
    super("-", m);
  }
}

class AND extends BinaryLeft500 {
  AND(ParseTreePatternMatcher m) {
    super("/\\", m);
  }
}

class OR extends BinaryLeft500 {
  OR(ParseTreePatternMatcher m) {
    super("\\/", m);
  }
}

class XOR extends BinaryLeft500 {
  XOR(ParseTreePatternMatcher m) {
    super("xor", m);
  }
}
// binaryLeft500 }}}

// unary500 {{{
abstract class Unary500 extends BaseInvocationMatcher {
  Unary500(String name, ParseTreePatternMatcher m) {
    super(
        name,
        String.format("%s <rhs:binaryLeft400>", name),
        PrologParser.RULE_unary500,
        m);
  }

  @Override
  public List<ParseTree> getArgs(ParseTreeMatch m) {
    return List.of(m.get("rhs"));
  }
}

class Q extends Unary500 {
  Q(ParseTreePatternMatcher m) {
    super("?", m);
  }
}
// unary500 }}}

// binaryLeft400 {{{
abstract class BinaryLeft400 extends BaseInvocationMatcher {
  BinaryLeft400(String name, ParseTreePatternMatcher m) {
    super(
        name,
        String.format("<lhs:binaryLeft400> %s <rhs:binary200>", name),
        PrologParser.RULE_binaryLeft400,
        m);
  }

  BinaryLeft400(String name, String operator, ParseTreePatternMatcher m) {
    super(
        name,
        String.format("<lhs:binaryLeft400> %s <rhs:binary200>", operator),
        PrologParser.RULE_binaryLeft400,
        m);
  }

  @Override
  public List<ParseTree> getArgs(ParseTreeMatch m) {
    return List.of(m.get("lhs"), m.get("rhs"));
  }
}

class S extends BinaryLeft400 {
  S(ParseTreePatternMatcher m) {
    super("*", m);
  }
}

class SLASH extends BinaryLeft400 {
  SLASH(ParseTreePatternMatcher m) {
    super("/", m);
  }
}

class SLASHSLASH extends BinaryLeft400 {
  SLASHSLASH(ParseTreePatternMatcher m) {
    super("//", m);
  }
}

class DIV extends BinaryLeft400 {
  DIV(ParseTreePatternMatcher m) {
    super("div", m);
  }
}

class RDIV extends BinaryLeft400 {
  RDIV(ParseTreePatternMatcher m) {
    super("rdiv", m);
  }
}

class LTLT extends BinaryLeft400 {
  LTLT(ParseTreePatternMatcher m) {
    super("<<", "`<`<", m);
  }
}

class GTGT extends BinaryLeft400 {
  GTGT(ParseTreePatternMatcher m) {
    super(">>", "`>`>", m);
  }
}

class REM extends BinaryLeft400 {
  REM(ParseTreePatternMatcher m) {
    super("rem", m);
  }
}

class MOD extends BinaryLeft400 {
  MOD(ParseTreePatternMatcher m) {
    super("mod", m);
  }
}
// binaryLeft400 }}}

// binary200 {{{
abstract class Binary200 extends BaseInvocationMatcher {
  Binary200(String name, ParseTreePatternMatcher m) {
    super(
        name,
        String.format("<lhs:binaryRight200> %s <rhs:binaryRight200>", name),
        PrologParser.RULE_binary200,
        m);
  }

  @Override
  public List<ParseTree> getArgs(ParseTreeMatch m) {
    return List.of(m.get("lhs"), m.get("rhs"));
  }
}

class SS extends Binary200 {
  SS(ParseTreePatternMatcher m) {
    super("**", m);
  }
}
// binary200 }}}

// binaryRight200 {{{
abstract class BinaryRight200 extends BaseInvocationMatcher {
  BinaryRight200(String name, ParseTreePatternMatcher m) {
    super(
        name,
        String.format("<lhs:unary200> %s <rhs:binaryRight200>", name),
        PrologParser.RULE_binaryRight200,
        m);
  }

  @Override
  public List<ParseTree> getArgs(ParseTreeMatch m) {
    return List.of(m.get("lhs"), m.get("rhs"));
  }
}

class CARET extends BinaryRight200 {
  CARET(ParseTreePatternMatcher m) {
    super("^", m);
  }
}
// binaryRight200 }}}

// unary200 {{{
abstract class Unary200 extends BaseInvocationMatcher {
  Unary200(String name, ParseTreePatternMatcher m) {
    super(
        name,
        String.format("%s <rhs:unary200>", name),
        PrologParser.RULE_unary200,
        m);
  }

  @Override
  public List<ParseTree> getArgs(ParseTreeMatch m) {
    return List.of(m.get("rhs"));
  }
}

class UnaryPLUS extends Unary200 {
  UnaryPLUS(ParseTreePatternMatcher m) {
    super("+", m);
  }
}

class UnaryH extends Unary200 {
  UnaryH(ParseTreePatternMatcher m) {
    super("-", m);
  }
}

class BSLASH extends Unary200 {
  BSLASH(ParseTreePatternMatcher m) {
    super("\\", m);
  }
}
// unary200 }}}

// unary1 {{{
abstract class Unary1 extends BaseInvocationMatcher {
  Unary1(String name, ParseTreePatternMatcher m) {
    super(
        name,
        String.format("%s <rhs:base_term>", name),
        PrologParser.RULE_unary1,
        m);
  }

  @Override
  public List<ParseTree> getArgs(ParseTreeMatch m) {
    return List.of(m.get("rhs"));
  }
}

class DOLLAR extends Unary1 {
  DOLLAR(ParseTreePatternMatcher m) {
    super("$", m);
  }
}
// unary1 }}}
