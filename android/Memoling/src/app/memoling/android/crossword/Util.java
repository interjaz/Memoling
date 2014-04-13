package app.memoling.android.crossword;

import java.util.ArrayList;
import java.util.List;

public class Util {
	public static int[] copy(int[] data) {
		int[] copy = new int[data.length];
		for (int i = 0; i < data.length; i++) {
			copy[i] = data[i];
		}

		return copy;
	}

	public static List<MatrixWord> copyMw(List<MatrixWord> data) {
		List<MatrixWord> copy = new ArrayList<MatrixWord>(data.size());
		for (int i = 0; i < data.size(); i++) {
			MatrixWord word = data.get(i);
			copy.add(new MatrixWord(word.word, new Point(word.from.y, word.from.x), new Point(word.to.y, word.to.x)));
		}

		return copy;
	}

	public static List<Point> copyP(List<Point> data) {
		List<Point> copy = new ArrayList<Point>(data.size());

		for (int i = 0; i < data.size(); i++) {
			copy.add(new Point(data.get(i).y, data.get(i).x));
		}

		return copy;
	}

	public static Matrix copy(Matrix data) {
		Matrix copy = new Matrix(data.size);
		for (int i = 0; i < data.size.y; i++) {
			for (int j = 0; j < data.size.x; j++) {
				copy.matrix.get(i).get(j).c = data.matrix.get(i).get(j).c;
			}
		}

		copy.isEmpty = data.isEmpty;
		copy.words = Util.copyMw(data.words);
		copy.size.x = data.size.x;
		copy.size.y = data.size.y;

		return copy;
	}

	public static List<String> copy(List<String> data) {
		List<String> copy = new ArrayList<String>(data.size());
		for (int i = 0; i < data.size(); i++) {
			copy.add(data.get(i));
		}

		return copy;
	}

	public static List<String> diff(List<MatrixWord> a, List<String> b) {
		List<String> c = Util.copy(b);
		for (int i = 0; i < a.size(); i++) {
			c.remove(a.get(i).word);
		}

		return c;
	}

	public static List<Matrix> add(Matrix from, List<Matrix> to) {
		if (from != null) {
			to.add(from);
		}

		return to;
	}

	public static List<Matrix> add(List<Matrix> from, List<Matrix> to) {
		if (from == null) {
			return to;
		}

		for (int i = 0; i < from.size(); i++) {
			Matrix m = from.get(i);
			to.add(m);
		}

		return to;
	}

	public static boolean nextPermutation(int[] numList) {
		int largestIndex = -1;
		for (int i = numList.length - 2; i >= 0; i--) {
			if (numList[i] < numList[i + 1]) {
				largestIndex = i;
				break;
			}
		}

		if (largestIndex < 0)
			return false;

		int largestIndex2 = -1;
		for (int i = numList.length - 1; i >= 0; i--) {
			if (numList[largestIndex] < numList[i]) {
				largestIndex2 = i;
				break;
			}
		}

		int tmp = numList[largestIndex];
		numList[largestIndex] = numList[largestIndex2];
		numList[largestIndex2] = tmp;

		for (int i = largestIndex + 1, j = numList.length - 1; i < j; i++, j--) {
			tmp = numList[i];
			numList[i] = numList[j];
			numList[j] = tmp;
		}

		return true;
	}

}
