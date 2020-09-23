package edu.unc.cs.comp524.parsers.prolog;

import org.antlr.v4.runtime.*;

public class PrologListenerWithTokens extends PrologBaseListener {
  protected final BufferedTokenStream tokens;

  public PrologListenerWithTokens(BufferedTokenStream tokens) {
    this.tokens = tokens;
  }

}
