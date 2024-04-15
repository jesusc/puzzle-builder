
package ml2.logic.games.dsl;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import ml2.logic.games.dsl.AST.Concept;
import ml2.logic.games.dsl.AST.GameDescription;

public class PropertiesGenerator {

	public String generate(GameDescription gameDescription) {
		Buffer buffer = new Buffer();
		buffer.append("Integer_min = 0").nl();
		buffer.append("Integer_max = 31").nl();
		//buffer.append("Integer_max = 63").nl();
		
		buffer.append("Thing_banned = Set{false,true}").nl();
		
		Set<String> propertyNames = new HashSet<>();
		Set<String> propertyValues = new HashSet<>();
		
		final int THING_SIZE = 9;
		int totalProperties = 0;
		//int totalThingProperty = 0;
		for (Concept concept : gameDescription.getConcepts()) {
			propertyNames.add(concept.getName());
			propertyValues.addAll(concept.getValues());
		
			//totalProperties += propertyValues.size();
			//totalThingProperty += (propertyValues.size() * THING_SIZE);
		}
		
		totalProperties = propertyValues.size();
		int size_of_things = propertyNames.size() * propertyValues.size(); 
		
		
		/*
		 context p:Property inv has16Thing:
  p.thing->size()=16 -- 16*12 = 192 links

context t:Thing inv has3Property:
  t.property->size()=3 -- 3*64 = 192 links

		 */

		// 12 values
		
		// numLinks = propertyNames * thingSize;
		// 
		
		String names = propertyNames.stream().map(s -> "'" + s + "'").collect(Collectors.joining(","));
		String values = propertyValues.stream().map(s -> "'" + s + "'").collect(Collectors.joining(","));
		
		buffer.append("Property_name = Set{" + names + "}").nl();
		buffer.append("Property_value = Set{" + values+ "}").nl();
		
		//int totalProperties = propertyNames.size() * propertyValues.size();
		buffer.append("Property_min = " + totalProperties).nl();
		buffer.append("Property_max = " + totalProperties).nl();
		
		//int totalThingProperty = totalProperties * propertyValues.size();
		//int totalThingProperty = propertyNames.size() * THING_SIZE;
		int totalThingProperty = propertyNames.size() * size_of_things;
		
		buffer.append("ThingProperty_min = " + totalThingProperty).nl();
		buffer.append("ThingProperty_max = " + totalThingProperty).nl();
		
		buffer.append("Clue_min = 4").nl();
		buffer.append("Clue_max = 4").nl();

		buffer.append("Clue_negatedP = Set{false,true}").nl();
		buffer.append("Clue_negatedC = Set{false,true}").nl();
		

		// int size_of_things = THING_SIZE;
		//int size_of_things = totalThingProperty / propertyNames.size();
		
		buffer.append("Thing_min = " + size_of_things).nl();
		buffer.append("Thing_max = " + size_of_things).nl();
	
		return buffer.getText();
	}

	
	/*
	 * Integer_min = 0
Integer_max = 31
# Integer = Set{ ... }

# String_max = 10
# String = Set{ ... }

# ------------------------------------------------------------------------ Thing
Thing_min = 9
Thing_max = 9

Thing_banned = Set{false,true}

# ThingProperty (thing:Thing, property:Property) - - - - - - - - - - - - - - - -
ThingProperty_min = 18
ThingProperty_max = 18

# --------------------------------------------------------------------- Property
Property_min = 6
Property_max = 6

Property_name = Set{'top','body'}
Property_value = Set{'red','green','blue'}

# ------------------------------------------------------------------------- Clue
Clue_min = 4
Clue_max = 4

# Clue_prem = Set{ ... }
Clue_negatedP = Set{false,true}
# Clue_conc = Set{ ... }
Clue_negatedC = Set{false,true}

	 */
}
