package ml2.logic.games;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.ocl.value.BooleanValue;
import org.tzi.use.uml.ocl.value.CollectionValue;
import org.tzi.use.uml.ocl.value.ObjectValue;
import org.tzi.use.uml.ocl.value.StringValue;
import org.tzi.use.uml.ocl.value.Value;
import org.tzi.use.uml.sys.MLink;
import org.tzi.use.uml.sys.MLinkEnd;
import org.tzi.use.uml.sys.MLinkSet;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MObjectState;
import org.tzi.use.uml.sys.MSystemState;

import com.google.common.io.Files;

import ml2.efinder.DirectUseMvFinder;
import ml2.logic.games.Thing.Description;
import ml2.logic.games.dsl.AST.GameDescription;
import ml2.logic.games.dsl.DSLCompiler;

public class GameGenerator extends AbstractGameGenerator {

	private static String puzzleModel = readPuzzleModel();
	private MSystemState gameModel;
	
	/**
	 * @param gameDescription Currently an USE properties file
	 * @param additionalConstraints 
	 * @throws Exception 
	 */
	public GameGenerator(String gameDescription, String additionalConstraints) throws Exception {
		GameDescription game = null;
		if (! isUSEProgram(gameDescription)) {
			game = new DSLCompiler().compile(gameDescription);
		}

		String props = getProperties(game, gameDescription);
		String puzzleModel = GameGenerator.puzzleModel;
		
		String generatedConstraints = getConstraints(game);
		
		System.out.println("# Puzzle model");
		System.out.println(puzzleModel);
		System.out.println("# Properties");
		System.out.println(props);
		System.out.println("# Additional constraints");
		System.out.println(additionalConstraints);
		System.out.println("# Generated constraints");
		System.out.println(generatedConstraints);
		
		
		if (additionalConstraints == null)
			additionalConstraints = "";
		
		puzzleModel = puzzleModel + "\n\n" + additionalConstraints + "\n\n" + generatedConstraints;
		
		Files.write(puzzleModel.getBytes(), new File("/tmp/s.use"));
		
		
		DirectUseMvFinder finder = new DirectUseMvFinder();
		this.gameModel = finder.directFind(puzzleModel, props);
	}


	private String getConstraints(GameDescription game) {
		if (game == null) {
			return "";
		}

		return game.toConstraints();
	}

	private String getProperties(GameDescription game, String gameDescription) {
		if (game == null) {
			// Heuristic to check if it s a properties file
			return gameDescription;
		}
		
		return game.toProperties();
	}


	private boolean isUSEProgram(String gameDescription) {
		return gameDescription.contains("_min") && gameDescription.contains("_max");
	}


	private static String readPuzzleModel() {
		try {
			try (InputStream inputStream = GameGenerator.class.getClassLoader().getResourceAsStream("puzzle.use")) {				
				return org.apache.commons.io.IOUtils.toString(inputStream, Charset.defaultCharset());
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		} 
	}


	public GamePlayEngine getGamePlayEngine() {
		Map<String, Thing> things = getThings(gameModel);
		List<Clue> clues = generateClues(gameModel, things);
		
		return new GamePlayEngine(things, clues);
	}


	private Map<String, Thing> getThings(MSystemState model) {
		Map<String, Thing> things = new HashMap<>();
		
		for(MObject o : model.allObjects()) {
			if (isThing(o)) {
				boolean banned = ((BooleanValue) o.state(model).attributeValue("banned")).value();
				//String description = get_toStr(o.state(model));
				Description description = getDescription(o, model);

				
				Thing thing = new Thing(o.name(), description, banned);
				things.put(o.name(), thing);
			}
			
		}		
		return things;
	}

	private Description getDescription(MObject thing, MSystemState model) {
		Description d = new Description();

		MClass thingClass = gameModel.system().model().getClass("Thing");
		//MAssociation propertyClass = gameModel.system().model().getAssociation("Property");

		MAssociation thingProperty = gameModel.system().model().getAssociation("ThingProperty");
		//MAssociationEnd propertyEnd = thingProperty.getAssociationEnd(thingClass, "property");
		
		
		MLinkSet set = model.linksOfAssociation(thingProperty);
		
		
		for (MLink mLink : set.links()) {
			//mLink.linkEnd(propertyEnd).object();
			MLinkEnd src = mLink.getLinkEnd(0);
			MLinkEnd tgt = mLink.getLinkEnd(1);
			
			if (src.object() == thing) {
				// System.out.println(src + " - " + tgt);
				MObject prop = tgt.object();

				MObjectState state = prop.state(model);
				String name  = ((StringValue) state.attributeValue("name")).value();
				String value = ((StringValue) state.attributeValue("value")).value();

				//System.out.println(src + " : " + name + " = " + value);
				d.add(name, value);
			}
		}
	
		return d;
	}


	public List<Clue> generateClues(MSystemState model, Map<String, Thing> things) {
		List<Clue> clues = new ArrayList<>();
		for(MObject o : model.allObjects()) {
			if (isClue(o)) {
				String clue = generateClue(o, model);
				System.out.println(clue);
				
				List<Thing> bannedThings = new ArrayList<Thing>();
				CollectionValue collection = (CollectionValue) o.state(model).attributeValue("bannedThings");
				for(Value v: collection) {
					if (v instanceof ObjectValue) {
						String thingId = ((ObjectValue) v).value().name();
						Thing t = things.get(thingId);
						if (t == null) {
							throw new IllegalStateException();
						}
						bannedThings.add(t);
					} else {
						throw new IllegalStateException();
					}
				}
				
				Clue c = new Clue(o.name(), clue, bannedThings);
				clues.add(c);
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
		
		return buffer.toString() + "\n" + get_toStr(s);		
	}



}
