package edu.unc.cs.comp524.parsers.prolog;

import java.util.*;

public interface Rule extends Relation {
  public List<RuleInvocation> rhs();
}
