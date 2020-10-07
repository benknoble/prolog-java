package edu.unc.cs.comp524.parsers.prolog;

import java.util.*;
import java.util.stream.*;
import java.util.function.*;
import java.util.regex.*;

import org.antlr.v4.runtime.*;

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

  public default int depth(String name) {
    var undefined =
      undefined()
      .stream()
      .map(RuleInvocation::name)
      .collect(Collectors.toSet());
    return depth(name, new Stack(), undefined);
  }

  private int depth(String name, Stack<String> ignore, Set<String> undefined) {
    if (undefined.contains(name)) return 1;
    else if (ignore.contains(name)) return 0;
    else return
      clauses()
      .getOrDefault(name, List.of())
      .stream()
      .mapToInt(r -> {
        if (r instanceof Fact) return 0;
        else if (r instanceof Rule) {
          var rule = (Rule)r;
          ignore.push(name);
          int depth = 1 +
            rule.rhs().stream()
            .map(RuleInvocation::name)
            .mapToInt(n -> depth(n, ignore, undefined))
            .max()
            .orElse(0);
          ignore.pop();
          return depth;
        }
        // shouldn't be the case
        else return 0;
      })
      .max()
      .orElse(0);
  }

  public default boolean noMagicNumbers() {
    return
      rules(relations())
      .allMatch(r ->
          r.args()
          .stream()
          .map(ParserRuleContext::getText)
          .flatMap(Program::getAllNums)
          .allMatch(Program::allowedNumber)

          &&

          r.rhs()
          .stream()
          .flatMap(ri -> ri.args().stream())
          .map(ParserRuleContext::getText)
          .flatMap(Program::getAllNums)
          .allMatch(Program::allowedNumber));
  }

  private static boolean allowedNumber(int i) {
    return Set.of(0,1,2).contains(i);
  }

  private static Stream<Integer> getAllNums(String s) {
    return Pattern.compile("(?:[^_a-zA-Z]|^)(\\d+)(?:[^_a-zA-Z]|$)")
      .matcher(s)
      .results()
      .map(mr -> mr.group(1))
      .map(Integer::parseInt);
  }

  public static Stream<Rule> rules(Stream<Relation> relations) {
    return
      relations
      .filter(c -> c instanceof Rule)
      .map(r -> (Rule)r);
  }

  public static Stream<RuleInvocation> invocations(Stream<Relation> relations) {
    return
      rules(relations)
      .flatMap(r -> r.rhs().stream());
  }

  public default Stream<Relation> relations() {
    return
      clauses()
      .values()
      .stream()
      .flatMap(Collection::stream);
  }

}
