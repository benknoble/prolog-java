package edu.unc.cs.comp524.parsers.prolog;

import java.util.*;

public interface Program {
  public Map<String, List<Relation>> clauses();
}
