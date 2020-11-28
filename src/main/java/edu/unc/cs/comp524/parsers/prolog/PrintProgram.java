package edu.unc.cs.comp524.parsers.prolog;

import java.io.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

/**
 * A sample program using {@link PrologParser} and {@link
 * RelationCollectorListener} to print out the collected {@link Program}.
 */
public class PrintProgram {
  public static void main(String[] args)
    throws IOException
  {
    var input = new ANTLRInputStream(System.in);
    var lexer = new PrologLexer(input);
    var tokens = new CommonTokenStream(lexer);
    var parser = new PrologParser(tokens);
    var tree = parser.p_text();

    var collector = new RelationCollectorListener(tokens, lexer, parser);
    ParseTreeWalker.DEFAULT.walk(collector, tree);
    System.out.println(collector.program());
  }
}
