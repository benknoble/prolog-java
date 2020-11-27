package edu.unc.cs.comp524.parsers.prolog;

import java.util.*;
import java.util.stream.*;

/**
 * The default concrete {@link Program} implementation.
 */
public class AProgram implements Program {
  private final Map<String, List<Relation>> clauses;

  public AProgram(Map<String, List<Relation>> clauses) {
    this.clauses = Collections.unmodifiableMap(clauses);
  }

  public AProgram(List<Relation> clauses) {
    var map = clauses.stream().collect(Collectors.groupingBy(Relation::name));
    map.replaceAll((name,relations) -> Collections.unmodifiableList(relations));
    this.clauses = Collections.unmodifiableMap(map);
  }

  @Override
  public Map<String, List<Relation>> clauses() {
    return clauses;
  }

  /**
   * A JSON-like debugging-aid {@link String} representation.
   */
  @Override
  public String toString() {
    return String.format("{program: {\n%s\n}}",
        clauses().entrySet().stream()
        .map(kv -> String.format("%s: %s",
            kv.getKey(),
            kv.getValue().stream()
            .map(r -> String.format("%s", r))
            .collect(Collectors.toList())))
        .collect(Collectors.joining(",\n")));
  }
}
