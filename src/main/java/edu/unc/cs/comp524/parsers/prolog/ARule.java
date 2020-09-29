package edu.unc.cs.comp524.parsers.prolog;

import java.util.*;

public final class ARule extends ARelation implements Rule {
  private final List<RuleInvocation> rhs;

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
      final List<PrologParser.TermContext> args,
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
}