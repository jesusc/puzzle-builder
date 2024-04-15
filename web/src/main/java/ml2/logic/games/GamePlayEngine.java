package ml2.logic.games;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GamePlayEngine {

	@JsonProperty
	private Map<String, Thing> things;
	@JsonProperty
	private List<Clue> clues;
	private GameStatus status;
	
	public GamePlayEngine(Map<String, Thing> things, List<Clue> clues) {
		this.things = things;
		this.clues = clues;
		this.status = new GameStatus();
	}

	public List<Clue> getClues() {
		return clues;
	}
	
	public Collection<Thing> getThings() {
		return things.values();
	}
	
	public GameStatus getStatus() {
		return status;
	}
	
	public static class GameStatus {
		private int currentClue = 0;
		private Map<Thing, Set<Clue>> markedThings = new HashMap<>();
		
		public int getCurrentClue() {
			return currentClue;
		}
	}

	public void setCurrentClue(int clueIndex) {
		System.out.println("Set current clune: " + clueIndex);
		status.currentClue = clueIndex;
	}
	
	/**
	 * Given the current game status, it checks if the selected thing should be ruled out according to the current clue.
	 */
	public MarkResult markThingBannedByCurrentClue(Thing thing) {
		Clue clue = clues.get(status.currentClue);
		
		if (! status.markedThings.containsKey(thing))
			status.markedThings.put(thing, new HashSet<>());
		
		status.markedThings.get(thing).add(clue);
		
		boolean result = clue.isThingBannedByClue(thing);
		
//		if (clue.isCovered(status.markedThings)) {
//			Preconditions.checkState(result == true);
//			//status.currentClue++;
//			return MarkResult.NEW_CLUE;
//		}

		return result ? MarkResult.CORRECT : MarkResult.INCORRECT;
	}
	
	public int getUnmarkedThings() {
		int unmarked = 0;
		for (Thing thing : things.values()) {
			Set<Clue> selectedClues = status.markedThings.get(thing);
			if (selectedClues == null || selectedClues.size() == 0) {
				unmarked++;
			}
		}
		return unmarked;
	}
	
	public int getCurrentClue() {
		return status.currentClue;
	}
	
	public static enum MarkResult {
		INCORRECT, 
		CORRECT,
		NEW_CLUE,
		GAME_FINISH;

		boolean thingIsCorrectlyBanned() {
			return this == CORRECT || this == NEW_CLUE || this == GAME_FINISH;
		}
	}
	
	
	public boolean isThingMarked(Thing thing) {
		return status.markedThings.containsKey(thing) && status.markedThings.get(thing).size() > 0;
	}

	public void unmarkThing(Thing thing) {
		// TODO: This should take into account the clue that is used
		status.markedThings.remove(thing);
	}


	public List<Clue> getCluesFor(Thing thing) {
		List<Clue> clues = new ArrayList<>();
		for (Clue clue : this.clues) {
			if (clue.isThingBannedByClue(thing)) {
				clues.add(clue);
			}
		}
		return clues;
	}
	
	public CheckResult check() {
		HashSet<Thing> t = new HashSet<>(this.getThings());
		t.removeAll(status.markedThings.keySet());
		
		final CheckResult result;
		if (t.size() != 1) {
			System.out.println("Too many or too few things");
			result = new CheckResult();
		} else {
			Thing thing = t.iterator().next();
			if (! thing.isBanned()) {
				result = new CheckResult(true, thing);
			} else {
				System.out.println("The solution is: " + this.getThings().stream().filter(x -> !x.isBanned()).findFirst().get().getDescriptionAsString());
				result = new CheckResult();
			}
		}
		
		status.markedThings.forEach((thing, clues) -> {
			IncorrectBanning banning = new IncorrectBanning(thing);
			for (Clue clue : clues) {
				if (! clue.isThingBannedByClue(thing)) {
					banning.addUserError(clue);
				}
			}
			
			if (! banning.clues.isEmpty()) {
				// TODO: Compute the real clues to be used
				result.addIncorrectBanning(banning);
			} else {
				result.addCorrectlyBannedThing(thing);
			}
		});
		
		
		
		return result;
	}
	
	public static class CheckResult {

		private boolean completeSolution = false;
		private boolean correct;
		private Thing solutionThing;
		private Map<Thing, IncorrectBanning> incorrectBannings = new HashMap<>();
		private Set<Thing> correctlyBannedThings = new HashSet<Thing>();
		
		public CheckResult(boolean isCorrect, Thing solutionThing) {
			this.correct = isCorrect;
			this.solutionThing = solutionThing;
			this.completeSolution = true;
		}
		
		public void addCorrectlyBannedThing(Thing thing) {
			correctlyBannedThings.add(thing);
		}

		public void addIncorrectBanning(IncorrectBanning banning) {
			incorrectBannings.put(banning.thing, banning);
		}

		public Collection<? extends IncorrectBanning> getIncorrectBannings() {
			return incorrectBannings.values();
		}
		
		public Set<? extends Thing> getCorrectlyBannedThings() {
			return correctlyBannedThings;
		}
		
		public CheckResult() {
			// TODO Auto-generated constructor stub
		}

		public boolean isCorrect() {
			return correct;
		}
		
		public Thing getSolutionThing() {
			return solutionThing;
		}
	}
	
	public static class IncorrectBanning {

		private Thing thing;
		private List<Clue> clues = new ArrayList<Clue>();

		public IncorrectBanning(Thing thing) {
			this.thing = thing;
		}

		public void addUserError(Clue clue) {
			clues.add(clue);
		}
		
		public Thing getThing() {
			return thing;
		}
		
		public List<? extends Clue> getIncorrectClues() {
			return clues;
		}
		
	}

}
