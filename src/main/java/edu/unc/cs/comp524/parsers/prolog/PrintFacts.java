package edu.unc.cs.comp524.parsers.prolog;

import java.io.*;
import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.tree.pattern.*;

public class PrintFacts
{
  public static void main(String[] args)
    throws IOException
  {
    // create a CharStream that reads from standard input
    CharStream input = new ANTLRInputStream(System.in);
    // create a lexer that feeds off of input CharStream
    PrologLexer lexer = new PrologLexer(input);
    // create a buffer of tokens pulled from the lexer
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    // create a parser that feeds off the tokens buffer
    PrologParser parser = new PrologParser(tokens);
    // begin parsing at init rule
    ParseTree tree = parser.p_text();

    System.out.println("facts");
    ParserUtils.factPattern(parser).findAll(tree, "//*").stream()
      .forEach(m -> System.out.println(m.getTree().getText()));

    System.out.println("rules");
    ParserUtils.rulePattern(parser).findAll(tree, "//*").stream()
      .forEach(m -> System.out.println(m.getTree().getText()));

  }
}
