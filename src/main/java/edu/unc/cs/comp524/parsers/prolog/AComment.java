package edu.unc.cs.comp524.parsers.prolog;

import java.util.*;
import java.util.stream.*;

import org.antlr.v4.runtime.*;

public class AComment implements Comment {

  private final String text;

  public AComment(final String s) {
    text = s;
  }

  public AComment(final Token t) {
    this(t.getText());
  }

  public AComment(final List<Token> ts) {
    this(ts.stream()
        .map(t -> t.getText())
        .collect(Collectors.joining()));
  }

  @Override
  public String text() {
    return text;
  }
}
