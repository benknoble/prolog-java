package edu.unc.cs.comp524.parsers.prolog;

import java.io.*;
import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.tree.pattern.*;

public class PrintInvocations
{
  public static void main(String[] args)
    throws IOException
  {
    CharStream input = CharStreams.fromStream(System.in);
    PrologLexer lexer = new PrologLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    PrologParser parser = new PrologParser(tokens);
    ParseTree tree = parser.p_text();

    System.out.println("invocations of rules");
    var pattern = ParserUtils.invocationPattern(parser);
    ParseTreeWalker.DEFAULT.walk(new PrologBaseListener() {
      @Override
      public void enterConjuncts(PrologParser.ConjunctsContext ctx) {
        var rhs = ctx.termlist(1);
        pattern.findAll(rhs, "//*").stream()
          .forEach(m -> System.out.println(m.getTree().getText()));
      }
    }, tree);

  }
}
