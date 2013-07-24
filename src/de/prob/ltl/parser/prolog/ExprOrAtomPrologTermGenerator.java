package de.prob.ltl.parser.prolog;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.prob.ltl.parser.LtlBaseListener;
import de.prob.ltl.parser.LtlParser;
import de.prob.ltl.parser.LtlParser.ActionAtomContext;
import de.prob.ltl.parser.LtlParser.AndExprContext;
import de.prob.ltl.parser.LtlParser.BooleanAtomContext;
import de.prob.ltl.parser.LtlParser.EnabledAtomContext;
import de.prob.ltl.parser.LtlParser.FinallyExprContext;
import de.prob.ltl.parser.LtlParser.GloballyExprContext;
import de.prob.ltl.parser.LtlParser.HistoricallyExprContext;
import de.prob.ltl.parser.LtlParser.ImpliesExprContext;
import de.prob.ltl.parser.LtlParser.NextExprContext;
import de.prob.ltl.parser.LtlParser.NotExprContext;
import de.prob.ltl.parser.LtlParser.OnceExprContext;
import de.prob.ltl.parser.LtlParser.OrExprContext;
import de.prob.ltl.parser.LtlParser.PatternCallAtomContext;
import de.prob.ltl.parser.LtlParser.PredicateAtomContext;
import de.prob.ltl.parser.LtlParser.ReleaseExprContext;
import de.prob.ltl.parser.LtlParser.ScopeCallAtomContext;
import de.prob.ltl.parser.LtlParser.SinceExprContext;
import de.prob.ltl.parser.LtlParser.StateAtomContext;
import de.prob.ltl.parser.LtlParser.TriggerExprContext;
import de.prob.ltl.parser.LtlParser.UnaryCombinedExprContext;
import de.prob.ltl.parser.LtlParser.UntilExprContext;
import de.prob.ltl.parser.LtlParser.VariableCallAtomContext;
import de.prob.ltl.parser.LtlParser.WeakuntilExprContext;
import de.prob.ltl.parser.LtlParser.YesterdayExprContext;
import de.prob.ltl.parser.semantic.PatternCall;
import de.prob.ltl.parser.semantic.ScopeCall;
import de.prob.ltl.parser.semantic.VariableCall;
import de.prob.parserbase.ProBParseException;
import de.prob.parserbase.ProBParserBase;
import de.prob.prolog.output.IPrologTermOutput;

public class ExprOrAtomPrologTermGenerator extends LtlBaseListener {

	private final LtlParser parser;
	private final LtlPrologTermGenerator generator;

	private final IPrologTermOutput pto;
	private final String currentStateID;
	private final ProBParserBase specParser;

	protected ParseTreeProperty<Integer> numberOfTerms = new ParseTreeProperty<Integer>();
	protected ParserRuleContext blockingContext = null;

	public ExprOrAtomPrologTermGenerator(LtlParser parser, LtlPrologTermGenerator generator, IPrologTermOutput pto, String currentStateID, ProBParserBase parserBase) {
		this.parser = parser;
		this.generator = generator;
		this.pto = pto;
		this.currentStateID = currentStateID;
		this.specParser = parserBase;
	}

	@Override
	public void enterVariableCallAtom(VariableCallAtomContext ctx) {
		if (enterContext(ctx)) {
			VariableCall call = new VariableCall(parser, ctx);
			generator.generateVariableCall(call, pto);
		}
	}

	@Override
	public void enterPatternCallAtom(PatternCallAtomContext ctx) {
		if (enterContext(ctx)) {
			PatternCall call = new PatternCall(parser, ctx.pattern_call());
			generator.generatePatternCall(call, pto);
		}
	}

	@Override
	public void enterScopeCallAtom(ScopeCallAtomContext ctx) {
		if (enterContext(ctx)) {
			ScopeCall call = new ScopeCall(parser, ctx.scope_call());
			generator.generateScopeCall(call, pto);
		}
	}

	@Override
	public void exitEveryRule(ParserRuleContext ctx) {
		if (blockingContext != null && ctx.equals(blockingContext)) {
			blockingContext = null;
		}
	}

	protected boolean enterContext(ParserRuleContext ctx) {
		if (blockingContext == null) {
			blockingContext = ctx;
			return true;
		}
		return false;
	}

	protected void openTerm(String functor) {
		if (blockingContext != null) {
			return;
		}
		pto.openTerm(functor);
	}

	protected void closeTerm() {
		if (blockingContext != null) {
			return;
		}
		pto.closeTerm();
	}

	protected void printAtomOrNumber(String content) {
		if (blockingContext != null) {
			return;
		}
		pto.printAtomOrNumber(content);
	}

	protected void printAtom(String content) {
		if (blockingContext != null) {
			return;
		}
		pto.printAtom(content);
	}

	protected void parseTransitionPredicate(final String text, Token token) {
		if (blockingContext != null) {
			return;
		}
		try {
			specParser.parseTransitionPredicate(pto, text, true);
		} catch (ProBParseException e) {
			parser.notifyErrorListeners(token, e.getMessage(), null);
		} catch (UnsupportedOperationException e) {
			parser.notifyErrorListeners(token, e.getMessage(), null);
		}
	}

	protected void parsePredicate(final String text, Token token) {
		if (blockingContext != null) {
			return;
		}
		try {
			specParser.parsePredicate(pto, text, true);
		} catch (ProBParseException e) {
			parser.notifyErrorListeners(token, e.getMessage(), null);
		} catch (UnsupportedOperationException e) {
			parser.notifyErrorListeners(token, e.getMessage(), null);
		}
	}

	@Override
	public void enterNotExpr(NotExprContext ctx) {
		openTerm("not");
	}

	@Override
	public void exitNotExpr(NotExprContext ctx) {
		closeTerm();
	}

	@Override
	public void enterFinallyExpr(FinallyExprContext ctx) {
		openTerm("finally");
	}

	@Override
	public void exitFinallyExpr(FinallyExprContext ctx) {
		closeTerm();
	}

	@Override
	public void enterHistoricallyExpr(HistoricallyExprContext ctx) {
		openTerm("historically");
	}

	@Override
	public void exitHistoricallyExpr(HistoricallyExprContext ctx) {
		closeTerm();
	}

	@Override
	public void enterOnceExpr(OnceExprContext ctx) {
		openTerm("once");
	}

	@Override
	public void exitOnceExpr(OnceExprContext ctx) {
		closeTerm();
	}

	@Override
	public void enterNextExpr(NextExprContext ctx) {
		openTerm("next");
	}

	@Override
	public void exitNextExpr(NextExprContext ctx) {
		closeTerm();
	}

	@Override
	public void enterYesterdayExpr(YesterdayExprContext ctx) {
		openTerm("yesterday");
	}

	@Override
	public void exitYesterdayExpr(YesterdayExprContext ctx) {
		closeTerm();
	}

	@Override
	public void enterGloballyExpr(GloballyExprContext ctx) {
		openTerm("globally");
	}

	@Override
	public void exitGloballyExpr(GloballyExprContext ctx) {
		closeTerm();
	}

	@Override
	public void enterUnaryCombinedExpr(UnaryCombinedExprContext ctx) {
		TerminalNode node = ctx.UNARY_COMBINED();
		String ops = node.getText();
		numberOfTerms.put(ctx, ops.length());

		for (char c : ops.toCharArray()) {
			switch (c) {
			case 'G':
				enterGloballyExpr(null);
				break;
			case 'F':
				enterFinallyExpr(null);
				break;
			case 'X':
				enterNextExpr(null);
				break;
			case 'H':
				enterHistoricallyExpr(null);
				break;
			case 'O':
				enterOnceExpr(null);
				break;
			case 'Y':
				enterYesterdayExpr(null);
				break;
			}
		}
	}

	@Override
	public void exitUnaryCombinedExpr(UnaryCombinedExprContext ctx) {
		int num = numberOfTerms.get(ctx);
		for (int i = 0; i < num; i++) {
			closeTerm();
		}
		numberOfTerms.removeFrom(ctx);
	}

	@Override
	public void enterUntilExpr(UntilExprContext ctx) {
		openTerm("until");
	}

	@Override
	public void exitUntilExpr(UntilExprContext ctx) {
		closeTerm();
	}

	@Override
	public void enterTriggerExpr(TriggerExprContext ctx) {
		openTerm("trigger");
	}

	@Override
	public void exitTriggerExpr(TriggerExprContext ctx) {
		closeTerm();
	}

	@Override
	public void enterSinceExpr(SinceExprContext ctx) {
		openTerm("since");
	}

	@Override
	public void exitSinceExpr(SinceExprContext ctx) {
		closeTerm();
	}

	@Override
	public void enterWeakuntilExpr(WeakuntilExprContext ctx) {
		openTerm("weakuntil");
	}

	@Override
	public void exitWeakuntilExpr(WeakuntilExprContext ctx) {
		closeTerm();
	}

	@Override
	public void enterReleaseExpr(ReleaseExprContext ctx) {
		openTerm("release");
	}

	@Override
	public void exitReleaseExpr(ReleaseExprContext ctx) {
		closeTerm();
	}

	@Override
	public void enterAndExpr(AndExprContext ctx) {
		openTerm("and");
	}

	@Override
	public void exitAndExpr(AndExprContext ctx) {
		closeTerm();
	}

	@Override
	public void enterOrExpr(OrExprContext ctx) {
		openTerm("or");
	}

	@Override
	public void exitOrExpr(OrExprContext ctx) {
		closeTerm();
	}

	@Override
	public void enterImpliesExpr(ImpliesExprContext ctx) {
		openTerm("implies");
	}

	@Override
	public void exitImpliesExpr(ImpliesExprContext ctx) {
		closeTerm();
	}

	@Override
	public void enterPredicateAtom(PredicateAtomContext ctx) {
		openTerm("ap");
		String text = ctx.getText();
		// TODO create token for errors
		parsePredicate(text.substring(1, text.length() - 1), ctx.start);
		closeTerm();
	}

	@Override
	public void enterActionAtom(ActionAtomContext ctx) {
		openTerm("action");
		String text = ctx.getText();
		// TODO create token for errors
		parseTransitionPredicate(text.substring(1, text.length() - 1), ctx.start);
		closeTerm();
	}

	@Override
	public void enterEnabledAtom(EnabledAtomContext ctx) {
		openTerm("ap");
		openTerm("enabled");
		String text = ctx.getText();
		// TODO create token for errors
		parseTransitionPredicate(text.substring(2, text.length() - 1), ctx.start);
		closeTerm();
		closeTerm();
	}

	@Override
	public void enterStateAtom(StateAtomContext ctx) {
		openTerm("ap");
		if (ctx.CURRENT() != null) {
			if (currentStateID != null) {
				openTerm("stateid");
				printAtomOrNumber(currentStateID);
				closeTerm();
			} else {
				printAtom("current");
			}
		} else if (ctx.DEADLOCK() != null) {
			printAtom("deadlock");
		} else {
			printAtom("sink");
		}
		closeTerm();
	}

	@Override
	public void enterBooleanAtom(BooleanAtomContext ctx) {
		if (ctx.TRUE() != null) {
			printAtom("true");
		} else {
			printAtom("false");
		}
	}

}
