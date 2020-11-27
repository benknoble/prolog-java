package edu.unc.cs.comp524.parsers.prolog;

/**
 * A source-level comment
 * <p>
 * Prolog has two kinds of comments:
 * <p>
 * {@code % line comments}
 * <p>
 * and multi-line java-and-C-like comments
 */
public interface Comment {

  /**
   * The comment text
   */
  public String text();
}
