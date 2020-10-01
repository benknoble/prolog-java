package edu.unc.cs.comp524.parsers.prolog;

import java.util.*;
import java.util.stream.*;

public interface Program {
  public Map<String, List<Relation>> clauses();

  public default List<Integer> arity(String name) {
    return
      clauses()
      .getOrDefault(name, List.of())
      .stream()
      .map(Relation::arity)
      .collect(Collectors.toList());
  }

}
