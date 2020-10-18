package edu.unc.cs.comp524.parsers.prolog;

import java.util.*;
import java.util.stream.*;
import java.util.function.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.pattern.*;

/**
 * Use one instance of the listener per parse-tree you want to get the Program
 * of.
 *
 * Run the listener (e.g., via ParseTreeWalker.DEFAULT.walk) before calling
 * program()---otherwise you will get garbage.
 */
public class RelationCollectorListener extends PrologListenerWithTokens {
  private final PrologLexer lexer;
  private final PrologParser parser;

  private final List<InvocationMatcher> matchers;

  private final ParseTreePattern factPattern;
  private final ParseTreePattern rulePattern;
  private final ParseTreePattern rule0Pattern;
  private final ParseTreePattern invocationPattern;

  private List<Relation> relations;

  public RelationCollectorListener(
      BufferedTokenStream tokens,
      PrologLexer lexer,
      PrologParser parser)
  {
    super(tokens);
    this.lexer = lexer;
    this.parser = parser;

    matchers = InvocationMatcher.invocationMatchers(parser, lexer);

    factPattern = ParserUtils.factPattern(parser);
    rulePattern = ParserUtils.rulePattern(parser);
    rule0Pattern = ParserUtils.rule0Pattern(parser);
    invocationPattern = ParserUtils.invocationPattern(parser);

    relations = new ArrayList<>();
  }

  public Program program() {
    return new AProgram(relations);
  }

  @Override
  public void enterFact(PrologParser.FactContext ctx) {
    var factMatch = factPattern.match(ctx);
    handleFact(
        factMatch,
        ctx.term(),
        comment(ctx));
  }

  @Override
  public void enterPredicate(PrologParser.PredicateContext ctx) {
    var ruleMatch = rulePattern.match(ctx);
    var rule0Match = rule0Pattern.match(ctx);
    if (ruleMatch.succeeded())
      handleRule(
          ruleMatch,
          ctx,
          comment(ctx));
    else if (rule0Match.succeeded())
      handleRule0(
          rule0Match,
          ctx,
          comment(ctx));
  }

  private void handleFact(
      ParseTreeMatch match,
      PrologParser.TermContext ctx,
      Optional<Comment> comment)
  {
    var name = (PrologParser.AtomContext)(match.get("atom"));
    var args = (PrologParser.TermlistContext)(match.get("termlist"));
    relations.add(new AFact(name, args, comment));
  }

  private void handleRule(
      ParseTreeMatch match,
      PrologParser.PredicateContext ctx,
      Optional<Comment> comment)
  {
    var name = (PrologParser.AtomContext)(match.get("atom"));
    var args = (PrologParser.TermlistContext)(match.get("termlist"));
    var body = (PrologParser.TermContext)(match.get("term")); // ctx.term(1);
    relations.add(new ARule(
          name,
          args,
          comment,
          invocations(body)));
  }

  private void handleRule0(
      ParseTreeMatch match,
      PrologParser.PredicateContext ctx,
      Optional<Comment> comment)
  {
    var name = (PrologParser.AtomContext)(match.get("atom"));
    var body = (PrologParser.TermContext)(match.get("term")); // ctx.term(1);
    relations.add(new ARule(
          name.getText(),
          List.of(),
          comment,
          invocations(body)));
  }

  private Optional<Comment> comment(PrologParser.ClauseContext ctx) {
    var comments = commentsToLeft(ctx);
    return
      (comments != null
       && areTokensOnNode(ctx, comments))
      ? Optional.of(new AComment(comments))
      : Optional.empty();
  }

  private List<RuleInvocation> matchPattern(
      ParseTreePattern pattern,
      PrologParser.TermContext body,
      Function<ParseTreeMatch, ARuleInvocation> f)
  {
    return
      (pattern
       .findAll(body, "//*")
       .stream()
       .map(m -> {
         try {
           return f.apply(m);
         } catch (ClassCastException e) {
           return null;
         }
       })
       .filter(r -> r != null)
       .collect(Collectors.toList()));
  }

  private List<RuleInvocation> invocations(PrologParser.TermContext body) {
    return
      ParserUtils.join(
          // rule invocations
          matchPattern(
            invocationPattern,
            body,
            m -> new ARuleInvocation(
              (PrologParser.AtomContext)m.get("atom"),
              (PrologParser.TermlistContext)m.get("termlist"))),
          // InvocationMatchers invocations
          matchers
          .stream()
          .flatMap(m -> m.invocations(body).stream())
          .collect(Collectors.toList()));
  }

}
