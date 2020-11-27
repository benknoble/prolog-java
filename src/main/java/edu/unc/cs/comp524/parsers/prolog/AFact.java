package edu.unc.cs.comp524.parsers.prolog;

import java.util.*;

import org.antlr.v4.runtime.tree.*;

/**
 * The default concrete {@link Fact} implementation.
 */
public final class AFact extends ARelation implements Fact {

  /**
   * Constructor for use with {@link PrologParser}-related objects.
   *
   * @param atom Context from which to derive {@link #name}
   * @param termlist Context from which to derive {@link #args}
   */
  public AFact(
      final PrologParser.AtomContext atom,
      final PrologParser.TermlistContext termlist,
      final Optional<Comment> comment)
  {
    super(atom, termlist, comment);
  }

  public AFact(
      final String name,
      final List<ParseTree> args,
      final Optional<Comment> comment)
  {
    super(name, args, comment);
  }
}
