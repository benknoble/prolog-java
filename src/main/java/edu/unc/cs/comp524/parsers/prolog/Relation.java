package edu.unc.cs.comp524.parsers.prolog;

import java.util.*;

import org.antlr.v4.runtime.tree.*;

/**
 * A prolog relation between arguments. Examples:
 * <p>
 * {@code foo(1, 2).}
 * <p>
 * {@code bar(X, Y) :- foo(X, 1), Y = X.}
 * <p>
 * &amp;c.
 */
public interface Relation {

  /**
   * The name of the relation
   */
  public String name();

  /**
   * The arguments of the relation
   */
  public List<ParseTree> args();

  /**
   * How many arguments the relation takes
   * <p>
   * Should be equivalent to {@code args().size()}
   */
  public int arity();

  /**
   * The {@link Comment}, if available
   */
  public Optional<Comment> comment();
}
