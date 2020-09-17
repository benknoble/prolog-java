package edu.unc.cs.comp524.parsers;

import java.io.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class PrintFacts
{
  public static void main(String[] args)
    throws IOException
  {
    // create a CharStream that reads from standard input
    CharStream input = CharStreams.fromStream(System.in);
    // create a lexer that feeds off of input CharStream
    PrologLexer lexer = new PrologLexer(input);
    // create a buffer of tokens pulled from the lexer
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    // create a parser that feeds off the tokens buffer
    PrologParser parser = new PrologParser(tokens);
    ParseTree tree = parser.p_text(); // begin parsing at init rule
    System.out.println(tree.toStringTree(parser)); // print LISP-style tree
  }
}
