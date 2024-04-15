package ml2.logic.games;

import java.util.ArrayList;
import java.util.List;

import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MObjectState;
import org.tzi.use.uml.sys.MSystemState;

public class ClueGenerator extends AbstractGameGenerator {
	
	public List<String> generateDescription(MSystemState model) {
		List<String> clues = new ArrayList<>();
		for(MObject o : model.allObjects()) {
			if (isClue(o)) {
				String clue = generateClue(o, model);
				System.out.println(clue);
				clues.add(clue);
			}
		}
		
		return clues;
	}
	

	private static String generateClue(MObject o, MSystemState model) {
		MObjectState s = o.state(model);
		MObject premise = getPremise(s);
		MObject conclusion = getConclusion(s);
		
		StringBuffer buffer = new StringBuffer();
		
		if (premise != conclusion) {
			buffer.append("If the ");
			
			String premiseName = getPropertyName(premise.state(model));
			buffer.append(premiseName);
		
			if (isNegatedPremise(s)) {
				buffer.append(" is not ");
			} else {
				buffer.append(" is ");
			}
			
			buffer.append(getPropertyValue(premise.state(model)));
		
			buffer.append(" then ");
		}
		
		String conclusionName = getPropertyName(conclusion.state(model));
		buffer.append("the ");
		buffer.append(conclusionName);
		
		if (isNegatedConclusion(s)) {
			buffer.append(" is not ");
		} else {
			buffer.append(" is ");
		}
		
		buffer.append(getPropertyValue(conclusion.state(model)));
		
		return buffer.toString();		
	}



}
