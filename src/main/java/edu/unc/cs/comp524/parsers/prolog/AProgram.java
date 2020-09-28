package edu.unc.cs.comp524.parsers.prolog;

import java.util.*;
import java.util.stream.*;

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
}
