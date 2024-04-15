package ml2.logic.games.dsl;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import logic.games.dslLexer;
import logic.games.dslParser;
import logic.games.dslParser.ConceptContext;
import logic.games.dslParser.Key_valueContext;
import logic.games.dslParser.Key_value_seqContext;
import logic.games.dslParser.NameContext;
import logic.games.dslParser.ProgramContext;
import logic.games.dslParser.SolutionContext;
import logic.games.dslParser.StringContext;
import ml2.logic.games.dsl.AST.Concept;
import ml2.logic.games.dsl.AST.GameDescription;
import ml2.logic.games.dsl.AST.Solution;

public class DSLCompiler {

    public ProgramContext doParse(String code) {
        //ErrorListener listener = new ErrorListener();
        dslLexer lexer = new dslLexer(CharStreams.fromString(code));
        dslParser parser = new dslParser(new CommonTokenStream(lexer));
        //parser.addErrorListener(listener);
        //lexer.addErrorListener(listener);
        
        ProgramContext p = parser.program();
        
        //if (listener.errors.hasErrors()) {
        //    throw new CompilerErrorException(listener.errors);
        //}
        
        return p;
    }
    
    public GameDescription compile(String code) {
    	ProgramContext program = doParse(code);
    	
    	String domain = handleString(program.name().getText());

    	AST.GameDescription game = new AST.GameDescription(domain);
    	
    	for(ConceptContext c : program.concept()) {
    		Concept concept = process(c);
    		game.addConcept(concept);
    	}
    	
    	for (SolutionContext solutionContext : program.solution()) {
			Solution solution = process(solutionContext);
			game.addSolution(solution);
		}
    	
    	return game;
    }

    private Solution process(SolutionContext solutionContext) {
    	boolean isSolution = solutionContext.getText().startsWith("solution");
    	Solution solution = new Solution(isSolution);
    	
		for (Key_value_seqContext key_value_seqContext : solutionContext.key_value_seq()) {
			for (Key_valueContext key_valueContext : key_value_seqContext.key_value()) {
				String key = handleString(key_valueContext.name(0).getText());
				String value = handleString(key_valueContext.name(1).getText());				
				solution.addPropertyValue(key, value);
			}
		}
		return solution;
	}

	private Concept process(ConceptContext c) {
    	String id = handleString(c.name().getText());
    	List<String> values = new ArrayList<>();
    	for(NameContext v: c.value_seq().name()) {
    		values.add(handleString(v.getText()));
    	}
    	return new AST.Concept(id, values);
	}

	private String handleString(String text) {
		if (! text.startsWith("\""))
			return text;
        return text.substring(1, text.length() - 1);
	}

	private boolean isDefined(ParseTree node) {
        return node != null;
    }

}
