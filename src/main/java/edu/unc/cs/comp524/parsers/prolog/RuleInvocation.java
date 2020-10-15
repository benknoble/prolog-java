package edu.unc.cs.comp524.parsers.prolog;

import java.util.*;

import org.antlr.v4.runtime.tree.*;

public interface RuleInvocation {
  public String name();
  public List<ParseTree> args();
  public int arity();
  public boolean isInvocationOf(Relation r);
  public boolean isInvocationOf(String name, int arity);
}
