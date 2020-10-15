package edu.unc.cs.comp524.parsers.prolog;

import java.util.*;

import org.antlr.v4.runtime.tree.*;

public interface Relation {
  public String name();
  public List<ParseTree> args();
  public int arity();
  public Optional<Comment> comment();
}
