package ml2.logic.games.dsl;

public class Buffer {
	
	private StringBuffer impl = new StringBuffer();

	public Buffer append(String string) {
		impl.append(string);
		return this;
	}
	
	public Buffer nl() {
		impl.append("\n");
		return this;
	}

	public String getText() {
		return impl.toString();
	}
	
}
