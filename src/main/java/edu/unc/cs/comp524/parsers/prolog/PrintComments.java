package edu.unc.cs.comp524.parsers.prolog;

import java.io.*;
import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.tree.pattern.*;

/**
 * A sample program using {@link PrologParser} and {@link
 * PrologListenerWithTokens} to print out comment nodes
 */
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
      public void enterFact(PrologParser.FactContext ctx) {
        // get tokens from comment channel
        List<Token> comments = commentsToLeft(ctx);

        // get clause name
        var factMatch = ParserUtils.factPattern(parser).match(ctx);
        String clauseName = null;
        if (factMatch.succeeded())
          clauseName = factMatch.get("atom").getText();

        // check line numbers
        if (clauseName != null && comments != null
            && areTokensOnNode(ctx, comments))
        {
          System.out.println(String.format("%s: %s", clauseName, comments));
        }
      }

      @Override
      public void enterPredicate(PrologParser.PredicateContext ctx) {
        List<Token> comments = commentsToLeft(ctx);

        var ruleMatch = ParserUtils.rulePattern(parser).match(ctx);
        var rule0Match = ParserUtils.rule0Pattern(parser).match(ctx);
        String clauseName = null;
        if (ruleMatch.succeeded())
          clauseName = ruleMatch.get("atom").getText();
        else if (rule0Match.succeeded())
          clauseName = rule0Match.get("atom").getText();

        if (clauseName != null && comments != null
            && areTokensOnNode(ctx, comments))
        {
          System.out.println(String.format("%s: %s", clauseName, comments));
        }
      }
    }, tree);

  }
}
