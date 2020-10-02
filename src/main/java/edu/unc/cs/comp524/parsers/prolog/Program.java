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
      relations()
      .filter(r -> r.comment().isPresent())
      .collect(Collectors.toList());
  }

  public default boolean isRecursive(String name) {
    var clauses = clauses().getOrDefault(name, List.of());
    return
      rules(clauses.stream())
      .anyMatch(r -> r.rhs().stream().anyMatch(ri -> ri.isInvocationOf(r)));
  }

  public default boolean containsRecursive(String name) {
    var clauses = clauses().getOrDefault(name, List.of());
    return
      invocations(clauses.stream())
      .map(RuleInvocation::name)
      .anyMatch(this::isRecursive);
  }

  public default List<RuleInvocation> undefined() {
    var invocations = invocations(relations());
    /* collection necessary here to avoid issues with re-using a consumed
     * stream
     *
     * each stream can be consumed exactly once, and we need to use this one
     * many times (defined is captured in the isInvocationOfAnyRule closure)
     *
     * so we collect it and stream it anew at each use
     */
    var defined = relations().collect(Collectors.toList());
    Predicate<RuleInvocation> isInvocationOfAnyRule =
      ri -> defined.stream().anyMatch(r -> ri.isInvocationOf(r));
    return
      invocations
      .filter(isInvocationOfAnyRule.negate())
      .collect(Collectors.toList());
  }

  private static Stream<Rule> rules(Stream<Relation> relations) {
    return
      relations
      .filter(c -> c instanceof Rule)
      .map(r -> (Rule)r);
  }

  private static Stream<RuleInvocation> invocations(Stream<Relation> relations) {
    return
      rules(relations)
      .flatMap(r -> r.rhs().stream());
  }

  private Stream<Relation> relations() {
    return
      clauses()
      .values()
      .stream()
      .flatMap(Collection::stream);
  }

}
