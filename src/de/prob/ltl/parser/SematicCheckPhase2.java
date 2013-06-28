package de.prob.ltl.parser;

import org.antlr.v4.runtime.tree.TerminalNode;

import de.prob.ltl.parser.LtlParser.AfterScopeCallContext;
import de.prob.ltl.parser.LtlParser.AfterUntilScopeCallContext;
import de.prob.ltl.parser.LtlParser.BeforeScopeCallContext;
import de.prob.ltl.parser.LtlParser.BetweenScopeCallContext;
import de.prob.ltl.parser.LtlParser.GlobalScopeCallContext;
import de.prob.ltl.parser.LtlParser.LoopContext;
import de.prob.ltl.parser.LtlParser.Loop_argContext;
import de.prob.ltl.parser.LtlParser.Pattern_callContext;
import de.prob.ltl.parser.LtlParser.Pattern_call_argContext;
import de.prob.ltl.parser.LtlParser.Pattern_defContext;
import de.prob.ltl.parser.symboltable.Pattern;
import de.prob.ltl.parser.symboltable.Pattern.PatternScopes;
import de.prob.ltl.parser.symboltable.SymbolTable;
import de.prob.ltl.parser.symboltable.Variable;
import de.prob.ltl.parser.symboltable.Variable.VariableTypes;

public class SematicCheckPhase2 extends LtlBaseListener {

	private SymbolTable symbolTable;
	private Pattern currentPattern;

	public SematicCheckPhase2(SymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}

	@Override
	public void enterPattern_def(Pattern_defContext ctx) {
		symbolTable.pushScope(ctx);
	}

	@Override
	public void exitPattern_def(Pattern_defContext ctx) {
		symbolTable.popScope();
	}

	@Override
	public void enterLoop(LoopContext ctx) {
		symbolTable.pushScope(ctx);
	}

	@Override
	public void exitLoop(LoopContext ctx) {
		symbolTable.popScope();
	}

	@Override
	public void enterLoop_arg(Loop_argContext ctx) {
		VariableTypes type = (ctx.NUM_POS() != null ? VariableTypes.num : VariableTypes.var);
		if (type.equals(VariableTypes.var)) {
			TerminalNode nameNode = ctx.var_call().ID();
			String name = nameNode.getText();
			Variable arg = (Variable) symbolTable.resolve(name);
			type = arg.getType();
			if (type.equals(VariableTypes.var)) {
				throw new RuntimeException(String.format("The variable '%s' has the wrong type. Only number variables are allowed.", name));
			}
		}
	}

	@Override
	public void exitPattern_call(Pattern_callContext ctx) {
		if (!symbolTable.isDefined(currentPattern)) {
			throw new RuntimeException(String.format("Pattern '%s' cannot be resolved.", currentPattern.getSymbolID()));
		}
		if (!symbolTable.checkTypes(currentPattern)) {
			// TODO
			throw new RuntimeException(String.format("Pattern '%s' ceastzhesolved.", currentPattern.getSymbolID()));
		}
	}

	@Override
	public void enterPattern_call(Pattern_callContext ctx) {
		if (ctx.exception != null) {
			return;
		}
		TerminalNode nameNode = ctx.ID();
		String name = nameNode.getText();

		currentPattern = new Pattern(symbolTable.getCurrentScope(), name);
	}

	@Override
	public void enterPattern_call_arg(Pattern_call_argContext ctx) {
		VariableTypes type = (ctx.NUM_POS() != null ? VariableTypes.num : VariableTypes.var);
		if (ctx.var_call() != null && ctx.var_call().ID() != null) {
			TerminalNode nameNode = ctx.var_call().ID();
			String name = nameNode.getText();
			Variable arg = (Variable) symbolTable.resolve(name);
			type = arg.getType();
		}
		currentPattern.addParameter(new Variable(null, type));
	}

	@Override
	public void enterGlobalScopeCall(GlobalScopeCallContext ctx) {
		currentPattern.setScope(PatternScopes.global);
	}

	@Override
	public void enterBeforeScopeCall(BeforeScopeCallContext ctx) {
		currentPattern.setScope(PatternScopes.before);
	}

	@Override
	public void enterAfterScopeCall(AfterScopeCallContext ctx) {
		currentPattern.setScope(PatternScopes.after);
	}

	@Override
	public void enterBetweenScopeCall(BetweenScopeCallContext ctx) {
		currentPattern.setScope(PatternScopes.between);
	}

	@Override
	public void enterAfterUntilScopeCall(AfterUntilScopeCallContext ctx) {
		currentPattern.setScope(PatternScopes.after_until);
	}

}
