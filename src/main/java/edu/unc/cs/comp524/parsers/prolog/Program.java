package edu.unc.cs.comp524.parsers.prolog;

import java.util.*;
import java.util.stream.*;
import java.util.function.*;

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
      .flatMap(Collection::stream)
      .filter(r -> r.comment().isPresent())
      .collect(Collectors.toList());
  }

  public default boolean isRecursive(String name) {
    var clauses = clauses().getOrDefault(name, List.of());
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

  public default List<RuleInvocation> undefined() {
    var invocations =
      clauses()
      .values()
      .stream()
      .flatMap(Collection::stream)
      .filter(c -> c instanceof Rule)
      .map(f -> (Rule)f)
      .flatMap(r -> r.rhs().stream());
    var defined =
      clauses()
      .values()
      .stream()
      .flatMap(Collection::stream)
      /* collection necessary here to avoid issues with re-using a consumed
       * stream
       *
       * each stream can be consumed exactly once, and we need to use this one
       * many times (defined is captured in the isInvocationOfAnyRule closure)
       *
       * so we collect it and stream it anew at each use
       */
      .collect(Collectors.toList());
    Predicate<RuleInvocation> isInvocationOfAnyRule =
      ri -> defined.stream().anyMatch(r -> ri.isInvocationOf(r));
    return
      invocations
      .filter(isInvocationOfAnyRule.negate())
      .collect(Collectors.toList());
  }

}
