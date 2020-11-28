package edu.unc.cs.comp524.parsers.prolog;

import java.io.*;
import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.tree.pattern.*;

/**
 * A sample program using {@link PrologParser}, {@link PrologBaseListener}, and
 * {@link ParserUtils} to print out invocations of rules and operators.
 */
public class PrintInvocations
{
  public static void main(String[] args)
    throws IOException
  {
    CharStream input = new ANTLRInputStream(System.in);
    PrologLexer lexer = new PrologLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    PrologParser parser = new PrologParser(tokens);
    ParseTree tree = parser.p_text();

    System.out.println("invocations of rules");
    var applicationPattern = ParserUtils.invocationPattern(parser);
    ParseTreeWalker.DEFAULT.walk(new PrologBaseListener() {
      @Override
      public void enterPredicate(PrologParser.PredicateContext ctx) {
        System.out.println("------------------");
        System.out.println(ctx.term(0).getText());
        System.out.println("------------------");
        var rhs = ctx.term(1);
        System.err.println("[DEBUG] " + rhs.getText());
        System.out.println("== applications");
        applicationPattern.findAll(rhs, "//*").stream()
          .forEach(m -> System.out.println(m.getTree().getText()));
        System.out.println("== known operators");
        InvocationMatcher.invocationMatchers(parser, lexer)
          .stream()
          .flatMap(i -> i.invocations(rhs).stream())
          .forEach(ri -> System.out.println(ri));
      }
    }, tree);

  }
}
