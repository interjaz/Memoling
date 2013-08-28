package app.memoling.android.crossword;

public class Char {

	public char c;

	public Char() {
		
	}
	
	public Char(char c) {
		this.c = c;
	}
	
	@Override
	public String toString() {
		return Character.toString(c);
	}
}
