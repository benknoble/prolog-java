package edu.unc.cs.comp524.parsers.prolog;

import java.util.*;
import java.util.stream.*;

import org.antlr.v4.runtime.tree.*;

/**
 * The default concrete {@link Rule} implementation.
 */
public final class ARule extends ARelation implements Rule {
  private final List<RuleInvocation> rhs;

  /**
   * Constructor for use with {@link PrologParser}-related objects.
   *
   * @param atom Context from which to derive {@link #name}
   * @param termlist Context from which to derive {@link #args}
   */
  public ARule(
      final PrologParser.AtomContext atom,
      final PrologParser.TermlistContext termlist,
      final Optional<Comment> comment,
      final List<RuleInvocation> rhs)
  {
    super(atom, termlist, comment);
    this.rhs = Collections.unmodifiableList(rhs);
  }

  public ARule(
      final String name,
      final List<ParseTree> args,
      final Optional<Comment> comment,
      final List<RuleInvocation> rhs)
  {
    super(name, args, comment);
    this.rhs = Collections.unmodifiableList(rhs);
  }

  @Override
  public List<RuleInvocation> rhs() {
    return rhs;
  }

  /**
   * A JSON-like debugging-aid {@link String} representation.
   */
  @Override
  public String toString() {
    return String.format(
        "{\nfunctor: %s/%d,\nargs: %s,\ncomment: %s,\nsubrules: %s\n}",
        name(), arity(),
        args().stream()
        .map(ParseTree::getText)
        .collect(Collectors.toList()),
        comment(),
        rhs());
  }
}
