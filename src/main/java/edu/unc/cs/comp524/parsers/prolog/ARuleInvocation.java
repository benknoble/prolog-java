package edu.unc.cs.comp524.parsers.prolog;

import java.util.*;
import java.util.stream.*;

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

  @Override
  public String toString() {
    return String.format(
        "{\nfunctor: %s/%d,\nargs: %s\n}",
        fact.name(), fact.arity(),
        fact.args().stream()
        .map(PrologParser.TermContext::getText)
        .collect(Collectors.toList()));
  }
}
