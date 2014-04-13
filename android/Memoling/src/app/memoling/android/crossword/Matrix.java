package app.memoling.android.crossword;

import java.util.ArrayList;
import java.util.List;

import android.util.Pair;

//               y x x x
// [y][x]        y x x x 
//               y x x x
//
public class Matrix {
	public List<List<Char>> matrix;
	public List<MatrixWord> words = new ArrayList<MatrixWord>();

	public Point size;
	public boolean isEmpty = true;

	public Matrix(Point p) {
		size = p;

		matrix = new ArrayList<List<Char>>(size.y);
		for (int i = 0; i < size.y; i++) {
			matrix.add(new ArrayList<Char>(size.x));
			for (int j = 0; j < size.x; j++) {
				matrix.get(i).add(new Char('-'));
			}
		}
	}

	public boolean hasWord(String word) {
		for (int i = 0; i < words.size(); i++) {
			if (word.equals(words.get(i).word)) {
				return true;
			}
		}

		return false;
	}

	public void setHWord(String word, Point pos) {
		for (int i = 0; i < word.length(); i++) {
			matrix.get(pos.y).get(pos.x + i).c = word.charAt(i);
		}
		isEmpty = false;

		words.add(new MatrixWord(word, pos, new Point(pos.y, pos.x + word.length() - 1)));
	}

	public void setVWord(String word, Point pos) {
		for (int i = 0; i < word.length(); i++) {
			matrix.get(pos.y + i).get(pos.x).c = word.charAt(i);
		}
		isEmpty = false;

		words.add(new MatrixWord(word, pos, new Point(pos.y + word.length() - 1, pos.x)));
	}

	public void setLDWord(String word, Point pos) {
		for (int i = 0; i < word.length(); i++) {
			matrix.get(pos.y + i).get(pos.x + i).c = word.charAt(i);
		}
		isEmpty = false;

		words.add(new MatrixWord(word, pos, new Point(pos.y + word.length() - 1, pos.x + word.length() - 1)));
	}

	public void setRDWord(String word, Point pos) {
		for (int i = 0; i < word.length(); i++) {
			matrix.get(pos.y + i).get(pos.x - i).c = word.charAt(i);
		}
		isEmpty = false;

		words.add(new MatrixWord(word, pos, new Point(pos.y + word.length() - 1, pos.x - (word.length() - 1))));
	}

	public Point findChar(char c, Point pos) {
		for (int i = pos.y; i < size.y; i++) {
			for (int j = pos.x; j < size.x; j++) {
				if (matrix.get(i).get(j).c == c) {
					return new Point(i, j);
				}
			}
		}

		return new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	public boolean willHFit(String word, Point pos) {
		int x1 = pos.x + word.length();
		if (pos.y < 0 || pos.x < 0 || pos.y > size.y || x1 > size.x) {
			return false;
		}

		if (hasWord(word)) {
			return false;
		}

		for (int i = 0; i < word.length(); i++) {
			char c = matrix.get(pos.y).get(pos.x + i).c;
			if (c != '-' && c != word.charAt(i)) {
				return false;
			}
		}

		return true;
	}

	public boolean willVFit(String word, Point pos) {
		int y1 = pos.y + word.length();
		if (pos.y < 0 || pos.x < 0 || y1 > size.y || pos.x > size.x) {
			return false;
		}

		if (hasWord(word)) {
			return false;
		}

		for (int i = 0; i < word.length(); i++) {
			char c = matrix.get(pos.y + i).get(pos.x).c;
			if (c != '-' && c != word.charAt(i)) {
				return false;
			}
		}

		return true;
	}

	public boolean willLDFit(String word, Point pos) {
		int y1 = pos.y + word.length();
		int x1 = pos.x + word.length();
		if (pos.y < 0 || pos.x < 0 || y1 > size.y || x1 > size.x) {
			return false;
		}

		if (hasWord(word)) {
			return false;
		}

		for (int i = 0; i < word.length(); i++) {
			char c = matrix.get(pos.y + i).get(pos.x + i).c;
			if (c != '-' && c != word.charAt(i)) {
				return false;
			}
		}

		return true;
	}

	public boolean willRDFit(String word, Point pos) {
		int y1 = pos.y + word.length();
		int x1 = pos.x - (word.length() - 1);
		if (pos.y < 0 || pos.x < 0 || y1 > size.y || x1 < 0) {
			return false;
		}

		if (hasWord(word)) {
			return false;
		}

		for (int i = 0; i < word.length(); i++) {
			char c = matrix.get(pos.y + i).get(pos.x - i).c;
			if (c != '-' && c != word.charAt(i)) {
				return false;
			}
		}

		return true;
	}

	public Pair<Point, Point> bounds() {
		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
		int maxX = -1, maxY = -1;

		for (int y = 0; y < size.y; y++) {
			for (int x = 0; x < size.x; x++) {
				if (matrix.get(y).get(x).c != '-') {
					minY = y < minY ? y : minY;
					minX = x < minX ? x : minX;
					maxY = y >= maxY ? y : maxY;
					maxX = x >= maxX ? x : maxX;
				}
			}
		}

		return new Pair<Point, Point>(new Point(minY, minX), new Point(maxY, maxX));
	}

	public Point maxHSpace(int y) {
		int max = 0;
		int maxPos = 0;
		int current = 0;
		int currentPos = 0;
		for (int i = 0; i < size.x; i++) {
			if (matrix.get(y).get(i).c == '-') {
				current++;
			} else {
				if (current > max) {
					max = current;
					maxPos = currentPos;
				}
				current = 0;
				currentPos = i + 1;
			}
		}

		if (current > max) {
			max = current;
			maxPos = currentPos;
		}

		return new Point(max, maxPos);
	}

	public Point maxVSpace(int x) {
		int max = 0;
		int maxPos = 0;
		int current = 0;
		int currentPos = 0;
		for (int i = 0; i < size.y; i++) {
			if (matrix.get(i).get(x).c == '-') {
				current++;
			} else {
				if (current > max) {
					max = current;
					maxPos = currentPos;
				}
				current = 0;
				currentPos = i + 1;
			}
		}

		if (current > max) {
			max = current;
			maxPos = currentPos;
		}

		return new Point(maxPos, max);
	}

	public void move(Point pos) {
		Matrix m = new Matrix(size);
		for (int i = 0; i < size.y; i++) {
			for (int j = 0; j < size.x; j++) {
				boolean yOut = i - pos.y < 0 || i - pos.y >= size.y;
				boolean xOut = j - pos.x < 0 || j - pos.x >= size.x;
				if (!yOut && !xOut) {
					if (m.matrix.get(i).get(j).c == '-') {
						m.matrix.get(i).get(j).c = matrix.get(i - pos.y).get(j - pos.x).c;
					}
				}

			}
		}

		matrix = m.matrix;

		for (int i = 0; i < words.size(); i++) {
			words.get(i).move(pos);
		}
	}

	public void print() {
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('\n');
		for (int i = 0; i < size.y; i++) {
			if (i == 0) {
				sb.append("  ");
				for (int a = 0; a < size.x; a++) {
					sb.append(" " + a + " ");
				}
				sb.append('\n');
			}

			for (int j = 0; j < size.x; j++) {
				if (j == 0) {
					sb.append(i + " ");
				}

				sb.append(" " + matrix.get(i).get(j).toString() + " ");
			}
			sb.append('\n');
		}
		return sb.toString();
	}
}
