package edu.unc.cs.comp524.parsers.prolog;

import java.util.*;
import java.util.stream.*;
import java.util.function.*;
import java.util.regex.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

/**
 * A prolog program.
 * <p>
 * Conceptually, a set of clauses keyed by name (cf. {@link Program#clauses}).
 * <p>
 * Supports various interrogation methods useful for static source or style
 * checks.
 * <p>
 * Also provides various static utility methods for processing streams of {@link
 * Relation}s.
 */
public interface Program {

  /**
   * The program's clauses.
   * <p>
   * A clause is identified by name; since prolog clauses support overloading by
   * arity, each clause gives a list of possible {@link Relation}s.
   */
  public Map<String, List<Relation>> clauses();

  /**
   * The arities of all clauses with a given name
   */
  public default List<Integer> arity(String name) {
    return
      clauses()
      .getOrDefault(name, List.of())
      .stream()
      .map(Relation::arity)
      .collect(Collectors.toList());
  }

  /**
   * The names of all clauses
   */
  public default List<String> names() {
    return new ArrayList(clauses().keySet());
  }

  /**
   * All {@link Relation}s that have comments.
   *
   * @see Relation#comment
   */
  public default List<Relation> relationsWithComments() {
    return
      relations()
      .filter(r -> r.comment().isPresent())
      .collect(Collectors.toList());
  }

  /**
   * True iff any of the clauses named {@code name} directly contains an
   * invocation of itself.
   *
   * @see RuleInvocation#isInvocationOf
   * @see #containsRecursive
   */
  public default boolean isRecursive(String name) {
    var clauses = clauses().getOrDefault(name, List.of());
    return
      rules(clauses.stream())
      .anyMatch(r -> r.rhs().stream().anyMatch(ri -> ri.isInvocationOf(r)));
  }

  /**
   * True iff any of the clauses named {@code name} contains an invocation to a
   * recurisive clause.
   *
   * @see #isRecursive
   */
  public default boolean containsRecursive(String name) {
    var clauses = clauses().getOrDefault(name, List.of());
    return
      invocations(clauses.stream())
      .map(RuleInvocation::name)
      .anyMatch(this::isRecursive);
  }

  /**
   * A collection of {@link RuleInvocation}s of rules not defined anywhere in
   * the program.
   * <p>
   * This can be useful when looking at what "standard" library functions were
   * called, or disallowing them altogether.
   */
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

  /**
   * The "depth" of a named clause.
   * <p>
   * Implementations are free to define depth in any way.
   * <p>
   * The default implementation defines it thusly: the depth is an integer
   * counting how many subrules are invoked until the tree "bottoms out" by
   * invoking a fact with no subrules. Once a subrule is counted as invoked,
   * other invocations of that subrule are ignored (to circumvent infinite loops
   * in the case of recursion, mutual or otherwise) in that particular "path."
   * <p>
   * The actual depth is the maximum of all possible depths.
   * <p>
   * This is a stand-in measure for things like cyclomatic complexity and
   * maximum runtime-depth, the latter of which cannot be computed statically
   * (cf. Turing's Halting problem).
   */
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

  /**
   * True iff the program contains no magic numbers
   * <p>
   * The default implementation defines that as "no rules have numeric literals
   * that are not 0, 1, or 2; facts are permitted and encouraged to capture
   * needed numeric constants."
   */
  public default boolean noMagicNumbers() {
    return
      rules(relations())
      .allMatch(r ->
          r.args()
          .stream()
          .map(ParseTree::getText)
          .flatMap(Program::getAllNums)
          .allMatch(Program::allowedNumber)

          &&

          r.rhs()
          .stream()
          .flatMap(ri -> ri.args().stream())
          .map(ParseTree::getText)
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

  /**
   * Only the {@link Rule}s in the stream of {@link Relation}s
   */
  public static Stream<Rule> rules(Stream<Relation> relations) {
    return
      relations
      .filter(c -> c instanceof Rule)
      .map(r -> (Rule)r);
  }

  /**
   * All the {@link RuleInvocation}s on the right-hand-side of the
   * {@link Rule}s in the stream of {@link Relation}s
   * @see Rule#rhs
   */
  public static Stream<RuleInvocation> invocations(Stream<Relation> relations) {
    return
      rules(relations)
      .flatMap(r -> r.rhs().stream());
  }

  /**
   * All the {@link Relation}s in the program
   */
  public default Stream<Relation> relations() {
    return
      clauses()
      .values()
      .stream()
      .flatMap(Collection::stream);
  }

}
