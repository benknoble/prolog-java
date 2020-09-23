package edu.unc.cs.comp524.parsers;

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
    CharStream input = CharStreams.fromStream(System.in);
    PrologLexer lexer = new PrologLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    PrologParser parser = new PrologParser(tokens);
    ParseTree tree = parser.p_text();

    ParseTreeWalker.DEFAULT.walk(new PrologBaseListener() {

      @Override
      public void enterClause(PrologParser.ClauseContext ctx) {
        // get tokens from comment channel
        int clauseTokenPosition = ctx.getStart().getTokenIndex();
        List<Token> comments = tokens.getHiddenTokensToLeft(
            clauseTokenPosition,
            PrologLexer.COMMENTCH);

        // get clause name
        var factMatch = PrologParserUtils.factPattern(parser).match(ctx);
        var ruleMatch = PrologParserUtils.rulePattern(parser).match(ctx);
        String clauseName = null;
        if (factMatch.succeeded())
          clauseName = factMatch.get("atom").getText();
        else if (ruleMatch.succeeded())
          clauseName = ruleMatch.get("atom").getText();

        if (clauseName != null && comments != null) {
          // check line numbers
          int clauseLine = ctx.getStart().getLine();
          Token lastComment = comments.get(comments.size()-1);
          int lastCommentLineStart = lastComment.getLine();
          int lastCommentLine =
            lastCommentLineStart + PrologParserUtils.countLines(lastComment);
          if (clauseLine == lastCommentLine+1)
            System.out.println(String.format(
                  "%s(%d): %s(%d)",
                  clauseName,
                  clauseLine,
                  comments,
                  lastCommentLine));
        }
      }
    }, tree);

  }
}
