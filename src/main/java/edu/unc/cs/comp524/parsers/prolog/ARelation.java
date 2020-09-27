package edu.unc.cs.comp524.parsers.prolog;

import java.util.*;

public abstract class ARelation implements Relation {

  private final String name;
  private final List<PrologParser.TermContext> args;
  private final Optional<Comment> comment;

  public ARelation(
      final PrologParser.AtomContext atom,
      final PrologParser.TermlistContext termlist,
      final Optional<Comment> comment)
  {
    name = atom.getText();
    args = Collections.unmodifiableList(termlist.term());
    this.comment = comment;
  }

  public ARelation(
      final String name,
      final List<PrologParser.TermContext> args,
      final Optional<Comment> comment)
  {
    this.name = name;
    this.args = Collections.unmodifiableList(args);
    this.comment = comment;
  }


  @Override
  public String name() {
    return name;
  }

  @Override
  public List<PrologParser.TermContext> args() {
    return args;
  }

  @Override
  public int arity() {
    return args().size();
  }

  @Override
  public Optional<Comment> comment() {
    return comment;
  }
}
