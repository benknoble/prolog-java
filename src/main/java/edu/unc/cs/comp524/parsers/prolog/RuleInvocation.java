package edu.unc.cs.comp524.parsers.prolog;

import java.util.*;

public interface RuleInvocation {
  public String name();
  public List<PrologParser.TermContext> args();
  public int arity();
  public boolean isInvocationOf(Relation r);
  public boolean isInvocationOf(String name, int arity);
}
