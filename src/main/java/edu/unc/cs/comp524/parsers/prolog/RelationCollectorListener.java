package edu.unc.cs.comp524.parsers.prolog;

import java.util.*;
import java.util.stream.*;

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
  private final PrologParser parser;
  private final ParseTreePattern factPattern;
  private final ParseTreePattern rulePattern;
  private final ParseTreePattern invocationPattern;
  private final ParseTreePattern binopPattern;
  private final ParseTreePattern unopPattern;
  private List<Relation> relations;

  public RelationCollectorListener(
      BufferedTokenStream tokens,
      PrologParser parser)
  {
    super(tokens);
    this.parser = parser;
    factPattern = ParserUtils.factPattern(parser);
    rulePattern = ParserUtils.rulePattern(parser);
    invocationPattern = ParserUtils.invocationPattern(parser);
    binopPattern = ParserUtils.binopPattern(parser);
    unopPattern = ParserUtils.unopPattern(parser);
    relations = new ArrayList<>();
  }

  public Program program() {
    return new AProgram(relations);
  }

  @Override
  public void enterClause(PrologParser.ClauseContext ctx) {
    var factMatch = factPattern.match(ctx);
    var ruleMatch = rulePattern.match(ctx);
    if (factMatch.succeeded())
      handleFact(
          factMatch,
          (PrologParser.Compound_termContext)ctx.term(),
          comment(ctx));
    else if (ruleMatch.succeeded())
      handleRule(
          ruleMatch,
          (PrologParser.ConjunctsContext)ctx.term(),
          comment(ctx));
    // else: clause is a term, but not a fact or a rule, so it's probably not
    // even valid prolog
  }

  private void handleFact(
      ParseTreeMatch match,
      PrologParser.Compound_termContext ctx,
      Optional<Comment> comment)
  {
    var name = ctx.atom();
    var args = ctx.termlist();
    relations.add(new AFact(name, args, comment));
  }

  private void handleRule(
      ParseTreeMatch match,
      PrologParser.ConjunctsContext ctx,
      Optional<Comment> comment)
  {
    var name = ctx.atom();
    var args = ctx.termlist(0);
    var body = ctx.termlist(1);
    relations.add(new ARule(
          name,
          args,
          comment,
          ParserUtils.join(
            invocations(body),
            binops(body),
            unops(body))));
  }

  private Optional<Comment> comment(PrologParser.ClauseContext ctx) {
    var comments = commentsToLeft(ctx);
    return
      (comments != null
       && areTokensOnNode(ctx, comments))
      ? Optional.of(new AComment(comments))
      : Optional.empty();
  }

  private List<RuleInvocation> invocations(PrologParser.TermlistContext body) {
    return
      (invocationPattern
       .findAll(body, "//*")
       .stream()
       .map(m -> {
         try {
           return new ARuleInvocation(
               (PrologParser.AtomContext)m.get("atom"),
               (PrologParser.TermlistContext)m.get("termlist"));
         } catch (ClassCastException e) {
           return null;
         }
       })
       .filter(r -> r != null)
       .collect(Collectors.toList()));
  }

  private List<RuleInvocation> binops(PrologParser.TermlistContext body) {
    return
      (binopPattern
       .findAll(body, "//*")
       .stream()
       .map(m -> {
         try {
           return new ARuleInvocation(
               m.get("operator").getText(),
               m.getAll("term").stream()
               .map(tree -> (PrologParser.TermContext)tree)
               .collect(Collectors.toList()));
         } catch (ClassCastException e) {
           return null;
         }
       })
       .filter(r -> r != null)
       .collect(Collectors.toList()));
  }

  private List<RuleInvocation> unops(PrologParser.TermlistContext body) {
    return
      (unopPattern
       .findAll(body, "//*")
       .stream()
       .map(m -> {
         try {
           return new ARuleInvocation(
               m.get("operator").getText(),
               List.of((PrologParser.TermContext)m.get("term")));
         } catch (ClassCastException e) {
           return null;
         }
       })
       .filter(r -> r != null)
       .collect(Collectors.toList()));
  }

}
