package ml2.logic.games;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Clue {

	@JsonProperty
	private String id;
	@JsonProperty(value = "description")
	private String clueDescription;
	@JsonIgnore
	private Set<Thing> bannedThings;

	public Clue(String id, String clue, Collection<Thing> bannedThings) {
		this.id = id;
		this.clueDescription = clue;
		this.bannedThings = new HashSet<>(bannedThings);
	}

	public String getDescription() {
		return clueDescription;
	}
	
	public boolean isThingBannedByClue(Thing thing) {
		return bannedThings.contains(thing);
	}

	public boolean isCovered(Set<Thing> markedThings) {
		return markedThings.containsAll(bannedThings);
	}

	@JsonProperty(value = "bannedThings")
	public List<String> getBannedThingReferences() {
		return bannedThings.stream().map(t -> t.getId()).collect(Collectors.toList());
	}
}
