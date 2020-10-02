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

  public default List<String> names() {
    return new ArrayList(clauses().keySet());
  }

  public default List<Relation> relationsWithComments() {
    return
      clauses()
      .values()
      .stream()
      .flatMap(rs -> rs.stream())
      .filter(r -> r.comment().isPresent())
      .collect(Collectors.toList());
  }

}
