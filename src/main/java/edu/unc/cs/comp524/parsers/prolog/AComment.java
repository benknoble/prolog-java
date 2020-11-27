package edu.unc.cs.comp524.parsers.prolog;

import java.util.*;
import java.util.stream.*;

import org.antlr.v4.runtime.*;

/**
 * The default concrete {@link Comment} implementation.
 */
public class AComment implements Comment {

  private final String text;

  public AComment(final String s) {
    text = s;
  }

  /**
   * Constructor for use with {@link PrologParser}-related objects.
   *
   * @param t Token from which to derive {@link #text}
   */
  public AComment(final Token t) {
    this(t.getText());
  }

  /**
   * Constructor for use with {@link PrologParser}-related objects.
   *
   * @param ts Tokens from which to derive {@link #text}
   */
  public AComment(final List<Token> ts) {
    this(ts.stream()
        .map(t -> t.getText())
        .collect(Collectors.joining()));
  }

  @Override
  public String text() {
    return text;
  }

  @Override
  public String toString() {
    return text();
  }
}
