package ml2.logic.games;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.fasterxml.jackson.annotation.JsonProperty;

import ml2.logic.games.dsl.Buffer;

public class Thing {

	@JsonProperty
	private String id;
	@JsonProperty
	private Description description;
	@JsonProperty
	private boolean banned;

	public Thing(String id, Description description, boolean banned) {
		this.id = id;
		this.description = description;
		this.banned = banned;
	}

	public String getId() {
		return id;
	}
	
	public String getDescriptionAsString() {
		return description.toString();
	}
	
	public Description getDescription() {
		return description;
	}
	
	public boolean isBanned() {
		return banned;
	}
	
	public static class Description {
		private Map<String, String> keyValues = new LinkedHashMap<>();
		
		public void add(String name, String value) {
			keyValues.put(name, value);
		}
	
		@Override
		public String toString() {
			Buffer buffer = new Buffer();
			keyValues.forEach((k, v) -> {
				buffer.append(k + " = " + v);
				buffer.nl();
			});
			return buffer.getText();
		}
		
		public List<Pair<String, String>> getElements() {
			List<Pair<String, String>> elements = new ArrayList<Pair<String,String>>();
			keyValues.forEach((k, v) -> {
				elements.add(Pair.of(k, v));
			});
			Collections.sort(elements, (p1, p2) -> p1.getLeft().compareTo(p2.getLeft()));
			return elements;
		}
	}
}
