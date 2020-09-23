package edu.unc.cs.comp524.parsers.prolog;

import java.util.*;

import org.antlr.v4.runtime.*;

public class PrologListenerWithTokens extends PrologBaseListener {
  protected final BufferedTokenStream tokens;

  public PrologListenerWithTokens(BufferedTokenStream tokens) {
    this.tokens = tokens;
  }

  protected final List<Token> commentsToLeft(ParserRuleContext ctx) {
    return tokens.getHiddenTokensToLeft(
        ctx.getStart().getTokenIndex(),
        PrologLexer.COMMENTCH);
  }

  protected final boolean areTokensOnNode(ParserRuleContext ctx, List<Token> tokens) {
    int nodeLine = ctx.getStart().getLine();
    Token lastToken = tokens.get(tokens.size()-1);
    int lastTokenLineStart = lastToken.getLine();
    int lastTokenLine =
      lastTokenLineStart + ParserUtils.countLines(lastToken);
    return nodeLine == lastTokenLine + 1;
  }

}
