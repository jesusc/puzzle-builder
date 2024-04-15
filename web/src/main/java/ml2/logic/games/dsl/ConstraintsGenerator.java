package ml2.logic.games.dsl;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import ml2.logic.games.dsl.AST.Concept;
import ml2.logic.games.dsl.AST.GameDescription;
import ml2.logic.games.dsl.AST.Solution;

public class ConstraintsGenerator {

	private int idx = 0;
	
	public String generate(GameDescription gameDescription) {
		Buffer buffer = new Buffer();
		
		for (Solution solution : gameDescription.getSolutions()) {
			generateSolution(buffer, gameDescription, solution);
		}
			
		generateNameFitsValue(buffer, gameDescription);
		
		return buffer.getText();
	}
	
	private void generateNameFitsValue(Buffer buffer, GameDescription gameDescription) {

		buffer.append("context Property inv nameFitsValue:").nl();

		String separator = "";
		for (Concept concept : gameDescription.getConcepts()) {
			String name = concept.getName();
			String values = concept.getValues().stream().map(s -> quote(s)).collect(Collectors.joining(", "));
			
			buffer.append(separator);
			buffer.append("(name='" + name + "' implies Set {" + values + "}->includes(value))");
			
			separator = " and ";
		}
		
		/*
		context Property inv nameFitsValue:
			  (name='top'   implies
			    Set{'blue','green','red'}->includes(value)) and
			  (name='body' implies
			    Set{'blue','green','red','orange'}->includes(value))
		*/
	}

	private void generateSolution(Buffer buffer, GameDescription gameDescription, Solution solution) {
		Map<? extends String, String> properties = solution.getProperties();
		  
		buffer.append("context Thing inv solutionConstraint" + idx++ + ":").nl();
			
		buffer.append("Thing.allInstances()->exists(t | ").nl();
	    if (solution.isActualSolution()) {
	    	buffer.append("(t.banned = false) and ").nl();
	    }
		
		String separator = "";
		for (Entry<? extends String, String> entry : properties.entrySet()) {
			String k = entry.getKey();
			String v = entry.getValue();

			buffer.append(separator);
			buffer.append("t.property->exists(p | p.name = " + quote(k) + " and p.value = " + quote(v) + ")").nl();
			
			separator = " and ";
		}
	      
		buffer.append(")").nl();

		/*
Thing.allInstances()->exists(t | 
t.property->exists(p | p.name = "top" and p.value = "blue") and 
t.property->exists(p | p.name = "body" and p.value = "blue")
				)
*/
	}

	private String quote(String k) {
		return "'" + k + "'";
	}

}
