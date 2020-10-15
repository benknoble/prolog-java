package edu.unc.cs.comp524.parsers.prolog;

import java.util.*;
import java.util.stream.*;

import org.antlr.v4.runtime.tree.*;

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
      final List<ParseTree> args)
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
  public List<ParseTree> args(){
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
  public boolean isInvocationOf(String name, int arity){
    return name() != null
      && name != null
      && name().equals(name)
      && arity() == arity;
  }

  @Override
  public String toString() {
    return String.format(
        "{\nfunctor: %s/%d,\nargs: %s\n}",
        fact.name(), fact.arity(),
        fact.args().stream()
        .map(ParseTree::getText)
        .collect(Collectors.toList()));
  }
}
