package edu.unc.cs.comp524.parsers.prolog;

import java.util.*;

/**
 * A more complicated {@link Relation} that has sub-rules ({@link
 * RuleInvocation}s) on the right-hand-side ({@link #rhs}).
 */
public interface Rule extends Relation {

  /**
   * The sub-rules or sub-clauses that make up the right-hand side of a {@link
   * Relation}
   */
  public List<RuleInvocation> rhs();
}
