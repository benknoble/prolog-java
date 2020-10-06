package edu.unc.cs.comp524.parsers.prolog;

import java.io.*;
import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.tree.pattern.*;

public class PrintComments
{
  public static void main(String[] args)
    throws IOException
  {
    CharStream input = new ANTLRInputStream(System.in);
    PrologLexer lexer = new PrologLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    PrologParser parser = new PrologParser(tokens);
    ParseTree tree = parser.p_text();

    ParseTreeWalker.DEFAULT.walk(new PrologListenerWithTokens(tokens) {

      @Override
      public void enterClause(PrologParser.ClauseContext ctx) {
        // get tokens from comment channel
        List<Token> comments = commentsToLeft(ctx);

        // get clause name
        var factMatch = ParserUtils.factPattern(parser).match(ctx);
        var ruleMatch = ParserUtils.rulePattern(parser).match(ctx);
        String clauseName = null;
        if (factMatch.succeeded())
          clauseName = factMatch.get("atom").getText();
        else if (ruleMatch.succeeded())
          clauseName = ruleMatch.get("atom").getText();

        // check line numbers
        if (clauseName != null && comments != null
            && areTokensOnNode(ctx, comments))
        {
          System.out.println(String.format("%s: %s", clauseName, comments));
        }
      }
    }, tree);

  }
}
