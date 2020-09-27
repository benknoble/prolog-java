package edu.unc.cs.comp524.parsers.prolog;

import java.util.*;

public interface Relation {
  public String name();
  public List<PrologParser.TermContext> args();
  public int arity();
  public Optional<Comment> comment();
}
