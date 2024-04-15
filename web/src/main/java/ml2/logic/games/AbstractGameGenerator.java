package ml2.logic.games;

import org.tzi.use.uml.ocl.value.BooleanValue;
import org.tzi.use.uml.ocl.value.ObjectValue;
import org.tzi.use.uml.ocl.value.SequenceValue;
import org.tzi.use.uml.ocl.value.StringValue;
import org.tzi.use.uml.ocl.value.Value;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MObjectState;

public abstract class AbstractGameGenerator {
	private final static String PREMISE = "prem";
	private final static String CONCLUSION = "conc";
	private final static String NEGATED_PREMISE = "negatedP";
	private final static String NEGATED_CONCLUSION = "negatedC";


	protected static String getPropertyName(MObjectState state) {
		return ((StringValue) state.attributeValue("name")).value();
	}

	protected static String getPropertyValue(MObjectState state) {
		return ((StringValue) state.attributeValue("value")).value();
	}


	protected static MObject getPremise(MObjectState s) {
		return ((ObjectValue) s.attributeValue(PREMISE)).value();
	}

	protected static boolean isNegatedPremise(MObjectState s) {
		return ((BooleanValue) s.attributeValue(NEGATED_PREMISE)).value();
	}

	protected static MObject getConclusion(MObjectState s) {
		return ((ObjectValue) s.attributeValue(CONCLUSION)).value();
	}

	protected static boolean isNegatedConclusion(MObjectState s) {
		return ((BooleanValue) s.attributeValue(NEGATED_CONCLUSION)).value();
	}


	public boolean isClue(MObject o) {
		return "Clue".equals(o.cls().name());
	}
	
	public boolean isThing(MObject o) {
		return "Thing".equals(o.cls().name());
	}
	
	protected static String get_toStr(MObjectState state) {
		Value v = state.attributeValue("toStr");
		if (v instanceof SequenceValue) {
			return v.toString();
		} else {
			return v.toString();
		}
	}

	
}
