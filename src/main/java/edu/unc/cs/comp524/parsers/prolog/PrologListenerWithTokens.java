package edu.unc.cs.comp524.parsers.prolog;

import java.util.*;

import org.antlr.v4.runtime.*;

/**
 * A {@link PrologListener} that saves the {@link BufferedTokenStream} of tokens
 * for access during the tree-walk.
 */
public class PrologListenerWithTokens extends PrologBaseListener {
  /**
   * The tokens stream
   */
  protected final BufferedTokenStream tokens;

  public PrologListenerWithTokens(BufferedTokenStream tokens) {
    this.tokens = tokens;
  }

  /**
   * The {@link Token} nodes of comments to the left of the givent context
   */
  protected final List<Token> commentsToLeft(ParserRuleContext ctx) {
    return tokens.getHiddenTokensToLeft(
        ctx.getStart().getTokenIndex(),
        PrologLexer.COMMENTCH);
  }

  /**
   * True iff the comment tokens end on the line before the start of the given
   * context.
   */
  protected final boolean areTokensOnNode(ParserRuleContext ctx, List<Token> tokens) {
    int nodeLine = ctx.getStart().getLine();
    Token lastToken = tokens.get(tokens.size()-1);
    int lastTokenLineStart = lastToken.getLine();
    int lastTokenLine =
      lastTokenLineStart + ParserUtils.countLines(lastToken);
    return nodeLine == lastTokenLine + 1;
  }

}
