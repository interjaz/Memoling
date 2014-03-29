package app.memoling.android.crossword;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.util.Pair;

public class Solver {

	private boolean found = false;
	private boolean allowDiagonal = false;

	private int operations = 0;
	private int maxOperations = 3000;
	
	public boolean success() {
		return found;
	}

	public float getProgress() {
		return (float) operations / maxOperations;
	}

	private boolean stop() {
		operations++;
		return found || operations > maxOperations;
	}

	private static Solver m_dualSolver1;
	private static Solver m_dualSolver2;
	
	public static float getDualProgress() {
		if(m_dualSolver1 == null || m_dualSolver2 == null) {
			return 0;
		}
		
		return ((float)m_dualSolver1.operations + m_dualSolver2.operations) / (m_dualSolver1.maxOperations + m_dualSolver2.maxOperations); 
	}

    public static Matrix dualSolve(List<String> words, boolean allowDiagonal, boolean allowFitting, Matrix matrix)
    {
    	// Descending
		Collections.sort(words, new Comparator<String>() {
			@Override
			public int compare(String lhs, String rhs) {
				return lhs.length() > rhs.length() ? -1 : 1;
			}
		});

        Solver s1 = new Solver();
        s1.maxOperations = 2000;
        Solver s2 = new Solver();
        s2.maxOperations = 500;
        m_dualSolver1 = s1;
        m_dualSolver2 = s2;
        
        matrix = s1.solve(words, allowDiagonal, matrix);

        words = Util.diff(matrix.words, words);
        // Ascending
		Collections.sort(words, new Comparator<String>() {
			@Override
			public int compare(String lhs, String rhs) {
				return lhs.length() > rhs.length() ? 1 : -1;
			}
		});
        matrix = s2.solve(words, false, matrix);

        if (!s2.success() && allowFitting)
        {
            words = Util.diff(matrix.words, words);
            matrix = Solver.FitFreeSpace(words, matrix);
        }

        m_dualSolver1 = null;
        m_dualSolver2 = null;
        
        return matrix;
    }
	
	public Matrix solve(List<String> words, boolean allowDiagonal, Matrix matrix) {
		this.allowDiagonal = allowDiagonal;
		List<Matrix> ms = new ArrayList<Matrix>();

		int[] perm = new int[words.size()];
		for (int i = 0; i < words.size(); i++) {
			perm[i] = i;
		}

		do {
			List<String> permWords = new ArrayList<String>();
			for (int i = 0; i < words.size(); i++) {
				permWords.add(words.get(perm[i]));
			}

			List<Matrix> lms = lineSolve(permWords, matrix, true);
			if (lms != null) {
				for (int i = 0; i < lms.size(); i++) {
					ms.add(lms.get(i));
				}
			}

		} while (!stop() && Util.nextPermutation(perm));

		Collections.sort(ms, new Comparator<Matrix>() {
			@Override
			public int compare(Matrix lhs, Matrix rhs) {
				return lhs.words.size() < rhs.words.size() ? 1 : -1;
			}
		});

		Matrix last = ms.get(0);
		return last;
	}

	public List<Matrix> lineSolve(List<String> words, Matrix matrix, boolean forward) {
		List<Matrix> mxs = new ArrayList<Matrix>();

		if (stop() || words == null || words.size() == 0) {
			found = true;
			mxs.add(matrix);
			return mxs;
		}

		for (int i = 0; i < words.size(); i++) {
			String word = words.get(i);
			List<String> vWords = Util.copy(words);
			List<String> hWords = Util.copy(words);
			List<String> dlWords = Util.copy(words);
			List<String> drWords = Util.copy(words);
			vWords.remove(i);
			vWords = vWords.size() == 0 ? null : vWords;
			hWords.remove(i);
			hWords = hWords.size() == 0 ? null : hWords;
			dlWords.remove(i);
			dlWords = dlWords.size() == 0 ? null : dlWords;
			drWords.remove(i);
			drWords = drWords.size() == 0 ? null : drWords;

			List<Matrix> ms;
			boolean fwd;
			// Tunning parameter it seems that leaving it as true gives better
			// result
			// fwd = !forward;
			fwd = true;

			if (allowDiagonal) {

				ms = FitLDiagonal(dlWords, word, Util.copy(matrix), fwd);
				if (ms != null) {
					Util.add(ms, mxs);
				}

				if (stop()) {
					return mxs;
				}

				ms = FitRDiagonal(drWords, word, Util.copy(matrix), fwd);
				if (ms != null) {
					Util.add(ms, mxs);
				}

				if (stop()) {
					return mxs;
				}

			}

			ms = FitHorizontal(vWords, word, Util.copy(matrix), fwd);
			if (ms != null) {
				Util.add(ms, mxs);
			}

			if (stop()) {
				return mxs;
			}

			ms = FitVertical(hWords, word, Util.copy(matrix), fwd);
			if (ms != null) {
				Util.add(ms, mxs);
			}

			if (stop()) {
				return mxs;
			}

		}

		return mxs;
	}

	public List<Matrix> FitHorizontal(List<String> words, String word, Matrix matrix, boolean forward) {
		List<Matrix> mxs = new ArrayList<Matrix>();

		if (matrix.isEmpty) {
			Point newPos = new Point(0, 0);
			if (matrix.willHFit(word, newPos)) {
				matrix.setHWord(word, new Point(0, 0));
				Util.add(lineSolve(words, matrix, forward), mxs);
			}
			return mxs;
		}

		boolean fitted = false;

		for (int j = 0; j < matrix.size.y && !stop(); j++) {
			for (int index = 0; index < word.length() && !stop(); index++) {
				int i = forward ? index : word.length() - 1 - index;
				// letters before
				int _i = i;
				// letters after
				int i_ = word.length() - i - 1;

				char c = word.charAt(i);
				Point pos = new Point(j, -1);
				pos = matrix.findChar(c, new Point(pos.y, pos.x + 1));
				if (!pos.isVaild()) {
					continue;
				}

				if (pos.x - i >= 0 && pos.x + i_ < matrix.size.x) {
					Matrix m = Util.copy(matrix);
					Point newPos = new Point(pos.y, pos.x - i);
					if (m.willHFit(word, newPos)) {
						m.setHWord(word, newPos);
						Util.add(lineSolve(words, m, forward), mxs);
						fitted = true;
					}
				} else {
					Pair<Point, Point> bounds = matrix.bounds();
					Point tBounds = bounds.first;
					Point bBounds = bounds.second;

					Point top = new Point(tBounds.y, tBounds.x);
					Point bottom = new Point(matrix.size.y - bBounds.y - 1, matrix.size.y - bBounds.x - 1);

					if (top.x > i) {
						// Move up
						Matrix m = Util.copy(matrix);
						int gap = matrix.size.x - pos.x - 1;
						int mov = -(i_ - gap);
						mov = mov < 0 ? mov : 0;
						m.move(new Point(0, mov));
						Point newPos = new Point(pos.y, pos.x + mov - i);
						if (m.willHFit(word, newPos)) {
							m.setHWord(word, newPos);
							Util.add(lineSolve(words, m, forward), mxs);
							fitted = true;
						}
					}

					if (bottom.x > i) {
						// Move down
						Matrix m = Util.copy(matrix);
						int gap = pos.x;
						int mov = i - gap;
						mov = mov > 0 ? mov : 0;
						// move so first letter is on x=0 position
						m.move(new Point(0, mov));
						Point newPos = new Point(pos.y, 0);
						if (m.willHFit(word, newPos)) {
							m.setHWord(word, newPos);
							Util.add(lineSolve(words, m, forward), mxs);
							fitted = true;
						}
					}
				}

			}
		}

		if (!fitted) {
			Util.add(matrix, mxs);
		}

		if (fitted && words == null) {
			found = true;
		}

		return mxs;
	}

	public List<Matrix> FitVertical(List<String> words, String word, Matrix matrix, boolean forward) {
		List<Matrix> mxs = new ArrayList<Matrix>();

		if (matrix.isEmpty) {
			Point newPos = new Point(0, 0);
			if (matrix.willVFit(word, newPos)) {
				matrix.setVWord(word, newPos);
				Util.add(lineSolve(words, matrix, forward), mxs);
			}
			return mxs;
		}

		boolean fitted = false;

		for (int j = 0; j < matrix.size.x && !stop(); j++) {
			for (int index = 0; index < word.length() && !stop(); index++) {
				int i = forward ? index : word.length() - 1 - index;
				// letters before
				int _i = i;
				// letters after
				int i_ = word.length() - i - 1;

				char c = word.charAt(i);
				Point pos = new Point(-1, j);
				pos = matrix.findChar(c, new Point(pos.y + 1, pos.x));
				if (!pos.isVaild()) {
					continue;
				}

				if (pos.y - i >= 0 && pos.y + i_ < matrix.size.y) {
					Matrix m = Util.copy(matrix);
					Point newPos = new Point(pos.y - i, pos.x);
					if (m.willVFit(word, newPos)) {
						m.setVWord(word, newPos);
						Util.add(lineSolve(words, m, forward), mxs);
						fitted = true;
					}
				} else {
					Pair<Point, Point> bounds = matrix.bounds();
					Point tBounds = bounds.first;
					Point bBounds = bounds.second;

					Point top = new Point(tBounds.y, tBounds.x);
					Point bottom = new Point(matrix.size.y - bBounds.y - 1, matrix.size.y - bBounds.x - 1);

					if (top.y > i) {
						// Move up
						Matrix m = Util.copy(matrix);
						int gap = matrix.size.y - pos.y - 1;
						int mov = -(i_ - gap);
						mov = mov < 0 ? mov : 0;
						m.move(new Point(mov, 0));
						Point newPos = new Point(pos.y + mov - i, pos.x);
						if (m.willVFit(word, newPos)) {
							m.setVWord(word, newPos);
							Util.add(lineSolve(words, m, forward), mxs);
							fitted = true;
						}
					}

					if (bottom.y > i) {
						// Move down
						Matrix m = Util.copy(matrix);
						int gap = pos.y;
						int mov = i - gap;
						mov = mov > 0 ? mov : 0;
						// move so first letter is on y=0 position
						m.move(new Point(mov, 0));
						Point newPos = new Point(0, pos.x);
						if (m.willVFit(word, newPos)) {
							m.setVWord(word, newPos);
							Util.add(lineSolve(words, m, forward), mxs);
							fitted = true;
						}
					}
				}
			}
		}

		if (fitted && words == null) {
			found = true;
		}

		if (!fitted) {
			Util.add(matrix, mxs);
		}

		return mxs;
	}

	public List<Matrix> FitLDiagonal(List<String> words, String word, Matrix matrix, boolean forward) {
		List<Matrix> mxs = new ArrayList<Matrix>();

		if (matrix.isEmpty) {
			Point newPos = new Point(0, 0);
			if (matrix.willLDFit(word, newPos)) {
				matrix.setLDWord(word, newPos);
				Util.add(lineSolve(words, matrix, forward), mxs);
			}
			return mxs;
		}

		boolean fitted = false;

		for (int j = 0; j < matrix.size.x && !stop(); j++) {
			for (int index = 0; index < word.length() && !stop(); index++) {
				int i = forward ? index : word.length() - 1 - index;
				// letters before
				int _i = i;
				// letters after
				int i_ = word.length() - i - 1;

				char c = word.charAt(i);
				Point pos = new Point(-1, j);
				pos = matrix.findChar(c, new Point(pos.y + 1, pos.x));
				if (!pos.isVaild()) {
					continue;
				}

				Pair<Point, Point> bounds = matrix.bounds();
				Point tBounds = bounds.first;
				Point bBounds = bounds.second;

				Point top = new Point(tBounds.y, tBounds.x);
				Point bottom = new Point(matrix.size.y - bBounds.y - 1, matrix.size.y - bBounds.x - 1);

				Matrix m = null;
				int gapX = 0;
				int gapY = 0;
				int movX = 0;
				int movY = 0;

				// Fits without moving
				if (pos.y >= i && pos.y + i_ < matrix.size.y && pos.x >= i && pos.x + i_ < matrix.size.x) {
					m = Util.copy(matrix);
					movX = 0;
					movY = 0;
				}
				// Move left
				else if (top.x >= i_ && pos.x >= i && pos.x + i < matrix.size.x) {

					// Stay
					if (pos.y >= i && pos.y + i_ < matrix.size.y) {
						m = Util.copy(matrix);
						gapX = matrix.size.x - pos.x - 1;
						gapY = matrix.size.y - pos.y - 1;
						movX = -(i_ - gapX);
						movY = 0;
						m.move(new Point(movY, movX));
					}
					// Move up
					else if (pos.y + i_ >= matrix.size.y && top.y > i) {
						m = Util.copy(matrix);
						gapX = matrix.size.x - pos.x - 1;
						gapY = matrix.size.y - pos.y - 1;
						movX = -(i_ - gapX);
						movY = -(i_ - gapY);
						movX = movX < 0 ? movX : 0;
						movY = movY < 0 ? movY : 0;
						m.move(new Point(movY, movX));
					}
					// Move down
					else if (pos.y <= i && bottom.y > i_) {
						m = Util.copy(matrix);
						gapX = matrix.size.x - pos.x - 1;
						gapY = pos.y;
						movX = -(i_ - gapX);
						movY = i - gapY;
						movX = movX < 0 ? movX : 0;
						movY = movY > 0 ? movY : 0;
						m.move(new Point(movY, movX));
					}
				}
				// Move right
				else if (bottom.x >= i && pos.x >= i_ && pos.x + i_ < matrix.size.x) {
					// Stay
					if (pos.y >= i && pos.y + i_ < matrix.size.y) {
						m = Util.copy(matrix);
						gapX = pos.x;
						movX = i - gapX;
						movX = movX > 0 ? movX : 0;
						movY = 0;
						m.move(new Point(movY, movX));
					}
					// Move up
					else if (pos.y + i_ >= matrix.size.y && top.y > i) {
						m = Util.copy(matrix);
						gapX = pos.x;
						gapY = matrix.size.y - pos.y - 1;
						movX = i - gapX;
						movY = -(i_ - gapY);
						movX = movX > 0 ? movX : 0;
						movY = movY < 0 ? movY : 0;
						m.move(new Point(movY, movX));
					}
					// Move down
					else if (pos.y <= i && bottom.y > i_) {
						m = Util.copy(matrix);
						gapX = pos.x;
						gapY = pos.y;
						movX = i - gapX;
						movY = i - gapY;
						movX = movX > 0 ? movX : 0;
						movY = movY > 0 ? movY : 0;
						m.move(new Point(movY, movX));
					}
				}

				if (m != null) {
					Point newPos = new Point(pos.y + movY - i, pos.x + movX - i);
					if (m.willLDFit(word, newPos)) {
						m.setLDWord(word, newPos);
						Util.add(lineSolve(words, m, forward), mxs);
						fitted = true;
					}
				}

			}
		}

		if (!fitted) {
			Util.add(matrix, mxs);
		}

		return mxs;
	}

	public List<Matrix> FitRDiagonal(List<String> words, String word, Matrix matrix, boolean forward) {
		List<Matrix> mxs = new ArrayList<Matrix>();

		if (matrix.isEmpty) {
			Point newPos = new Point(0, 0);
			if (matrix.willRDFit(word, newPos)) {
				matrix.setRDWord(word, newPos);
				Util.add(lineSolve(words, matrix, forward), mxs);
			}
			return mxs;
		}

		boolean fitted = false;

		for (int j = 0; j < matrix.size.x && !stop(); j++) {
			for (int index = 0; index < word.length() && !stop(); index++) {
				int i = forward ? index : word.length() - 1 - index;
				// letters before
				int _i = i;
				// letters after
				int i_ = word.length() - i - 1;

				char c = word.charAt(i);
				Point pos = new Point(-1, j);
				pos = matrix.findChar(c, new Point(pos.y + 1, pos.x));
				if (!pos.isVaild()) {
					continue;
				}

				Pair<Point, Point> bounds = matrix.bounds();
				Point tBounds = bounds.first;
				Point bBounds = bounds.second;

				Point top = new Point(tBounds.y, tBounds.x);
				Point bottom = new Point(matrix.size.y - bBounds.y - 1, matrix.size.y - bBounds.x - 1);

				Matrix m = null;
				int gapX = 0;
				int gapY = 0;
				int movX = 0;
				int movY = 0;

				// Fits without moving
				if (pos.y >= i && pos.y + i_ < matrix.size.y && pos.x >= i_ && pos.x + i < matrix.size.x) {
					m = Util.copy(matrix);
					movX = 0;
					movY = 0;
				}
				// Move left
				else if (top.x >= i && pos.x >= i_ && pos.x + i_ < matrix.size.x) {

					// Stay
					if (pos.y >= i && pos.y + i_ < matrix.size.y) {
						m = Util.copy(matrix);
						gapX = matrix.size.x - pos.x - 1;
						gapY = matrix.size.y - pos.y - 1;
						movX = -(i - gapX);
						movY = 0;
						m.move(new Point(movY, movX));
					}
					// Move up
					else if (pos.y + i_ >= matrix.size.y && top.y > i) {
						m = Util.copy(matrix);
						gapX = matrix.size.x - pos.x - 1;
						gapY = matrix.size.y - pos.y - 1;
						movX = -(i_ - gapX);
						movY = -(i_ - gapY);
						movX = movX < 0 ? movX : 0;
						movY = movY < 0 ? movY : 0;
						m.move(new Point(movY, movX));
					}
					// Move down
					else if (pos.y <= i && bottom.y > i_) {
						m = Util.copy(matrix);
						gapX = matrix.size.x - pos.x - 1;
						gapY = pos.y;
						movX = -(i - gapX);
						movY = i - gapY;
						movX = movX < 0 ? movX : 0;
						movY = movY > 0 ? movY : 0;
						m.move(new Point(movY, movX));
					}
				}
				// Move right
				else if (bottom.x >= i_ && pos.x >= i && pos.x + i < matrix.size.x) {
					// Stay
					if (pos.y >= i && pos.y + i_ < matrix.size.y) {
						m = Util.copy(matrix);
						gapX = pos.x;
						movX = i_ - gapX;
						movX = movX > 0 ? movX : 0;
						movY = 0;
						m.move(new Point(movY, movX));
					}
					// Move up
					else if (pos.y + i_ >= matrix.size.y && top.y > i) {
						m = Util.copy(matrix);
						gapX = pos.x;
						gapY = matrix.size.y - pos.y - 1;
						movX = i_ - gapX;
						movY = -(i_ - gapY);
						movX = movX > 0 ? movX : 0;
						movY = movY < 0 ? movY : 0;
						m.move(new Point(movY, movX));
					}
					// Move down
					else if (pos.y <= i && bottom.y > i_) {
						m = Util.copy(matrix);
						gapX = pos.x;
						gapY = pos.y;
						movX = i_ - gapX;
						movY = i - gapY;
						movX = movX > 0 ? movX : 0;
						movY = movY > 0 ? movY : 0;
						m.move(new Point(movY, movX));
					}
				}

				if (m != null) {
					Point newPos = new Point(pos.y + movY - i, pos.x + movX + i);
					if (m.willRDFit(word, newPos)) {
						m.setRDWord(word, newPos);
						Util.add(lineSolve(words, m, forward), mxs);
						fitted = true;
					}
				}

			}
		}

		if (!fitted) {
			Util.add(matrix, mxs);
		}

		return mxs;
	}

	public static Matrix FitFreeSpace(List<String> words, Matrix matrix) {
		for (int j = 0; j < 20; j++) {
			boolean fitted = false;
			String word = "";

			if (words.size() == 0) {
				return matrix;
			} else {
				// take new any way
				word = words.get(0);
				words.remove(0);
			}

			for (int i = 0; i < matrix.size.x; i++) {
				Point vPos = matrix.maxVSpace(i);
				if (vPos.x >= word.length()) {
					matrix.setVWord(word, new Point(vPos.y, i));
					fitted = true;
					break;
				}
			}

			if (words.size() == 0 && fitted) {
				return matrix;
			} else if (fitted) {
				word = words.get(0);
				words.remove(0);
			}

			for (int i = 0; i < matrix.size.y; i++) {
				Point hPos = matrix.maxHSpace(i);
				if (hPos.y >= word.length()) {
					matrix.setHWord(word, new Point(i, hPos.x));
					break;
				}
			}

			if (words.size() == 0 && fitted) {
				return matrix;
			}
		}

		// Failed, return what we've got
		return matrix;
	}
}
