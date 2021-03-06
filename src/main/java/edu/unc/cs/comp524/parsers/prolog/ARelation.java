package edu.unc.cs.comp524.parsers.prolog;

import java.util.*;
import java.util.stream.*;

import org.antlr.v4.runtime.tree.*;

/**
 * Abstract super-class that handles most of the {@link Relation} details.
 */
public abstract class ARelation implements Relation {

  private final String name;
  private final List<ParseTree> args;
  private final Optional<Comment> comment;

  /**
   * Constructor for use with {@link PrologParser}-related objects.
   *
   * @param atom Context from which to derive {@link #name}
   * @param termlist Context from which to derive {@link #args}
   */
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
      final List<ParseTree> args,
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
  public List<ParseTree> args() {
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

  /**
   * A JSON-like debugging-aid {@link String} representation.
   */
  @Override
  public String toString() {
    return String.format(
        "{\nfunctor: %s/%d,\nargs: %s,\ncomment: %s\n}",
        name(), arity(),
        args().stream()
        .map(ParseTree::getText)
        .collect(Collectors.toList()),
        comment());
  }
}
