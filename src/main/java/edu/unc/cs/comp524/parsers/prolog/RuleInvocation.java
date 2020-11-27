package edu.unc.cs.comp524.parsers.prolog;

import java.util.*;

import org.antlr.v4.runtime.tree.*;

/**
 * An invocation of some relation; typically found in {@link Rule#rhs}
 */
public interface RuleInvocation {

  /**
   * The name of the relation being invoked
   */
  public String name();

  /**
   * The arguments of the relation being invoked
   */
  public List<ParseTree> args();

  /**
   * The arity of the relation being invoked
   * <p>
   * Should be equivalent to {@code args().size()}
   */
  public int arity();

  /**
   * Implementation-defined.
   * <p>
   * The default implementation is true iff the names and arity are equivalent.
   */
  public boolean isInvocationOf(Relation r);

  /**
   * Implementation-defined.
   * <p>
   * The default implementation is true iff the names and arity are equivalent.
   */
  public boolean isInvocationOf(String name, int arity);
}
