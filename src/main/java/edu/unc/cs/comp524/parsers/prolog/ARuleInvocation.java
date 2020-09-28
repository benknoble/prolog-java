package edu.unc.cs.comp524.parsers.prolog;

import java.util.*;

public class ARuleInvocation implements RuleInvocation {
  private final Fact fact;

  public ARuleInvocation(
      final PrologParser.AtomContext atom,
      final PrologParser.TermlistContext termlist)
  {
    fact = new AFact(atom, termlist, Optional.empty());
  }

  public ARuleInvocation(
      final String name,
      final List<PrologParser.TermContext> args)
  {
    fact = new AFact(name, args, Optional.empty());
  }

  public ARuleInvocation(Fact f) {
    fact = f;
  }

  @Override
  public String name(){
    return fact.name();
  }

  @Override
  public List<PrologParser.TermContext> args(){
    return fact.args();
  }

  @Override
  public int arity(){
    return fact.arity();
  }

  @Override
  public boolean isInvocationOf(Relation r){
    return name() != null
      && r.name() != null
      && name().equals(r.name())
      && arity() == r.arity();
  }
}
