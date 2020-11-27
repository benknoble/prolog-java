package edu.unc.cs.comp524.parsers.prolog;

import java.util.*;

import org.antlr.v4.runtime.tree.*;

/**
 * The default concrete {@link Fact} implementation.
 */
public final class AFact extends ARelation implements Fact {
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
