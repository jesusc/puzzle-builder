package ml2.logic.games.dsl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AST {

	public static class GameDescription {

		private String domain;
		private List<Concept> concepts;
		private List<Solution> solutions;
		
		public GameDescription(String domain) {
			this.domain = domain;
			this.concepts = new ArrayList<AST.Concept>();
			this.solutions = new ArrayList<AST.Solution>();
		}
		
		public void addConcept(Concept c) {
			this.concepts.add(c);
		}
	
		public List<Concept> getConcepts() {
			return concepts;
		}
		
		public String toProperties() {
			return new PropertiesGenerator().generate(this);
		}

		public String toConstraints() {
			return new ConstraintsGenerator().generate(this);			
		}
		
		public void addSolution(Solution solution) {
			this.solutions.add(solution);
		}
		
		public List<Solution> getSolutions() {
			return solutions;
		}
	}

	public static class Concept {
		private List<String> values;
		private String name;

		public Concept(String name, List<String> values) {
			this.name = name;
			this.values = new ArrayList<>(values);
		}
		
		public String getName() {
			return name;
		}
		
		public List<String> getValues() {
			return values;
		}
	}
	
	public static class Solution {
		private Map<String, String> properties = new HashMap<>();
		private boolean actualSolution;
		
		public Solution(boolean isActualSolution) {
			this.actualSolution = isActualSolution;
		}

		public boolean isActualSolution() {
			return actualSolution;
		}
		
		
		public void addPropertyValue(String key, String value) {
			properties.put(key, value);
		}
		
		public Map<? extends String, String> getProperties() {
			return properties;
		}
		
	}
}
