package edu.unc.cs.comp524.parsers.prolog;

import java.io.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class PrintProgram {
  public static void main(String[] args)
    throws IOException
  {
    var input = CharStreams.fromStream(System.in);
    var lexer = new PrologLexer(input);
    var tokens = new CommonTokenStream(lexer);
    var parser = new PrologParser(tokens);
    var tree = parser.p_text();

    var collector = new RelationCollectorListener(tokens, parser);
    ParseTreeWalker.DEFAULT.walk(collector, tree);
    System.out.println(collector.program());
  }
}
