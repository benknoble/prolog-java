package edu.unc.cs.comp524.parsers.prolog.f20.a2;

import edu.unc.cs.comp524.parsers.prolog.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class PrintRubric {
  public static void main(String[] args)
    throws IOException
  {
    if (args.length != 1) {
      System.err.println("usage: PrintRubric <filename>");
      System.exit(1);
    }

    var input = new ANTLRFileStream(args[0]);
    var lexer = new PrologLexer(input);
    var tokens = new CommonTokenStream(lexer);
    var parser = new PrologParser(tokens);
    var tree = parser.p_text();

    var collector = new RelationCollectorListener(tokens, parser);
    ParseTreeWalker.DEFAULT.walk(collector, tree);
    var program = collector.program();

    // either is or isn't recursive, no degree there
    report("listGivenSafe is recursive",
        3,
        (program.isRecursive("listGivenSafe") ||
         program.containsRecursive("listGivenSafe")) ? 1 : 0);

    report("printGivenCombinations is recursive",
        3,
        (program.isRecursive("printGivenCombinations") ||
         program.containsRecursive("printGivenCombinations")) ? 1 : 0);

    report("listGenerateSafeDistancesAndDurations is recursive",
        3,
        (program.isRecursive("listGenerateSafeDistancesAndDurations") ||
         program.containsRecursive("listGenerateSafeDistancesAndDurations"))
        ? 1 : 0);

    report("no magic numbers",
        5,
        // we would need to do more work to determine how many magic numbers
        // there are and weight accordingly
        program.noMagicNumbers() ? 1 : 0);

    Set<String> allowed = Set.of(
        "write",
        ";",
        ",",
        "is",
        ">",
        "<",
        ">=",
        "=<",
        "-",
        "=",
        "\\="
        );
    report("no use of undefined predicates (except those allowed)",
        5,
        // Set::containsAll, when applied to another set, is a subset test
        // so allowed.containsAll(s) ⇔ s ⊆ allowed
        allowed.containsAll(
          program.undefined().stream()
          .map(RuleInvocation::name)
          .collect(Collectors.toSet()))
        // one *could* count the total number of disallowed (via set subtraction,
        // for instance, which would need to be implemented) and weight the
        // points accordingly
        //
        // but I figured using disallowed predicates should not earn any points…
        ? 1 : 0);

    report("rules are commented",
        5,
        // start simple and divide the number of commented relations by the
        // total number
        //
        // it is possible to determine if a specific relation is commented,
        // either by using
        //
        // clauses().getOrDefault(name, List.of()).stream()
        // .map(Relation::comment)
        // .anyMatch(Optional::isPresent)
        //
        // or
        //
        // relationsWithComments().stream()
        // .map(Relation::name)
        // .anyMatch(n -> n.equals(name))
        (double)(program.relationsWithComments().size())
        /
        program.clauses().values().stream().flatMap(Collection::stream).count());

    // depth report
    // program.names().stream()
    //   .forEach(n -> System.out.println(String.format(
    //           "%s: %d",
    //           n,
    //           program.depth(n))));

    /*
     * here's my depth report, from the above statement:
     *
     * largeExhalation: 0
     * interpolateDistance: 3
     * mediumExhalation: 0
     * printGivenCombinations: 3
     * listGivenSafe: 3
     * interpolateHigh: 2
     * filterSafe: 2
     * givenSafeTable: 2
     * mediumDuration: 0
     * derivedSafe: 3
     * smallDistance: 0
     * saferDistance: 2
     * largeDistance: 0
     * interpolateExhalation: 3
     * interpolateDuration: 3
     * member: 1
     * mediumDistance: 0
     * smallDuration: 0
     * smallExhalation: 0
     * maxInterpolatedValue: 0
     * givenSizes: 2
     * interpolatedSafe: 4
     * saferDuration: 2
     * minInterpolatedValue: 0
     * smallSizes: 1
     * interpolateLow: 2
     * mediumSizes: 1
     * saferExhalation: 2
     * comma: 2
     * generateSafeDistancesAndDurations: 4
     * largeDuration: 0
     * givenSafe: 2
     * largeSizes: 1
     * listGenerateSafeDistancesAndDurations: 4
     */

    // for these I'm checking only the required rules and the ratio of their
    // depth to mine
    //
    // this could easily be gamed by adding arbitrarily deep rules that do
    // nothing
    //
    // one solution might be to weight based on number of total rules, or based
    // on the maximum depth (which is easy to calculate using
    //
    // program.names().stream().mapInt(program::depth).max();
    report("givenSizes depth",
        2,
        program.depth("givenSizes") / 2.0);

    report("givenSafe depth",
        2,
        program.depth("givenSafe") / 2.0);

    report("derivedSafe depth",
        3,
        program.depth("derivedSafe") / 3.0);

    report("interpolatedSafe depth",
        4,
        program.depth("interpolatedSafe") / 4.0);

    report("generateSafeDistancesAndDurations depth",
        4,
        program.depth("generateSafeDistancesAndDurations") / 4.0);

    report("listGivenSafe depth",
        3,
        program.depth("listGivenSafe") / 3.0);

    report("printGivenCombinations depth",
        3,
        program.depth("printGivenCombinations") / 3.0);

    report("listGenerateSafeDistancesAndDurations depth",
        4,
        program.depth("listGenerateSafeDistancesAndDurations") / 4.0);

    // arity checks: don't worry about these if local checks already does them
    report("givenSizes arity",
        1,
        program.arity("givenSizes").stream().collect(Collectors.toSet())
        .containsAll(Set.of(3))
        ? 1 : 0);

    report("givenSafe arity",
        1,
        program.arity("givenSafe").stream().collect(Collectors.toSet())
        .containsAll(Set.of(3))
        ? 1 : 0);

    report("derivedSafe arity",
        1,
        program.arity("derivedSafe").stream().collect(Collectors.toSet())
        .containsAll(Set.of(3))
        ? 1 : 0);

    report("interpolatedSafe arity",
        1,
        program.arity("interpolatedSafe").stream().collect(Collectors.toSet())
        .containsAll(Set.of(3, 2, 1))
        ? 1 : 0);

    report("generateSafeDistancesAndDurations arity",
        1,
        program.arity("generateSafeDistancesAndDurations").stream().collect(Collectors.toSet())
        .containsAll(Set.of(3))
        ? 1 : 0);

    report("listGivenSafe arity",
        1,
        program.arity("listGivenSafe").stream().collect(Collectors.toSet())
        .containsAll(Set.of(1))
        ? 1 : 0);

    report("printGivenCombinations arity",
        1,
        program.arity("printGivenCombinations").stream().collect(Collectors.toSet())
        .containsAll(Set.of(1))
        ? 1 : 0);

    report("listGenerateSafeDistancesAndDurations arity",
        1,
        program.arity("listGenerateSafeDistancesAndDurations").stream().collect(Collectors.toSet())
        .containsAll(Set.of(2))
        ? 1 : 0);

    report("interpolatedSafe/1 -> interpolatedSafe/2",
        5,
        Program.invocations(
          program.clauses().getOrDefault("interpolatedSafe", List.of())
          .stream()
          .filter(r -> r.arity() == 1))
        .anyMatch(ri -> ri.isInvocationOf("interpolatedSafe", 2))
        ? 1 : 0);

    report("interpolatedSafe/2 -> interpolatedSafe/3",
        5,
        Program.invocations(
          program.clauses().getOrDefault("interpolatedSafe", List.of())
          .stream()
          .filter(r -> r.arity() == 2))
        .anyMatch(ri -> ri.isInvocationOf("interpolatedSafe", 3))
        ? 1 : 0);

    var givenSizesSubrules =
      // ∀ invocations
      Program.invocations(
          // in clauses of givenSizes/3
          program.clauses().getOrDefault("givenSizes", List.of())
          .stream()
          .filter(r -> r.arity() == 3))
      // that themselves have arity 3
      .filter(ri -> ri.arity() == 3)
      // the names of such invocations
      .map(RuleInvocation::name)
      .collect(Collectors.toList());
    report("givenSizes calls column rules",
        3,
        // check that there are 3 sub-rules of arity 3
        givenSizesSubrules.size() == 3 ? 1 : 0);
    report("givenSizes' column rules call cell rules",
        9,
        // ∀ names above (subrules in givenSizes)
        givenSizesSubrules
        .stream()
        // ∀ clauses associated with a name
        .map(name -> program.clauses().getOrDefault(name, List.of()))
        // the following holds
        .allMatch(rs ->
          // ∀ the rules
          Program.rules(
            rs.stream()
            // in the clauses of arity 3
            .filter(r -> r.arity() == 3))
          // in the right-hand-side
          .map(Rule::rhs)
          // ∃ a collection of rule-invocations
          .anyMatch(ris ->
            ris.stream()
            // of arity 1
            .filter(ri -> ri.arity() == 1)
            // such that there are 3 of them
            .count() == 3))
        ? 1 : 0);

     report("derivedSafe calls safety-checking rules",
         3,
         // ∀ invocations
         Program.invocations(
           // that are in clauses of derivedSafe
           program.clauses().getOrDefault("derivedSafe", List.of())
           .stream()
           // which has arity 3
           .filter(r -> r.arity() == 3))
        // ∀ names of rule-invocations of such clauses
        .map(RuleInvocation::name)
        // where
        .filter(ri ->
          // their invocations
          Program.invocations(
            program.clauses().getOrDefault(ri, List.of())
            .stream())
          // have names
          .map(RuleInvocation::name)
          // matching =< or >=
          .anyMatch(n -> "=<".equals(n) || ">=".equals(n)))
        // the number of such clauses is 3
        .count() == 3
        ? 1 : 0);

  }

  private static void report(String name, int points, double degree) {
    System.out.println(String.format("rubric_item: %s", name));
    System.out.println(String.format("points: %d", points));
    System.out.println(String.format("degree: %f", degree));
  }
}
