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

  public default boolean isRecursive(String name) {
    var clauses = clauses().get(name);
    if (clauses.isEmpty())
      return false;
    return
      clauses
      .stream()
      .filter(c -> c instanceof Rule)
      .map(f -> (Rule)f)
      .anyMatch(r -> r.rhs().stream().anyMatch(ri -> ri.isInvocationOf(r)));
  }

  public default boolean containsRecursive(String name) {
    var clauses = clauses().getOrDefault(name, List.of());
    if (clauses.isEmpty())
      return false;
    return
      clauses
      .stream()
      .filter(c -> c instanceof Rule)
      .map(f -> (Rule)f)
      .flatMap(r -> r.rhs().stream())
      .map(RuleInvocation::name)
      .anyMatch(this::isRecursive);
  }

}
