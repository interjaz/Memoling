package app.memoling.android.crossword;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import app.memoling.android.thread.WorkerThread;

public class SolverThread {

	public static interface ISolver {
		List<String> getSolverWords();
		void onSolverProgress(float progres);
		void onSolverComplete(Matrix words);
	}

	private boolean m_completed;

	private static Handler m_handler = new Handler();
	
	public void start(final boolean allowDiagonal, final boolean allowFitting, final int sizeY, final int sizeX,
			final ISolver onSolver) {

		new WorkerThread<Void, Float, Matrix>() {

			@Override
			protected Matrix doInBackground(Void... params) {
				m_handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						if (!m_completed) {
							publishProgress(Solver.getDualProgress());
							m_handler.postDelayed(this, 100);
						}
					}

				}, 100);				
				
				List<String> words = onSolver.getSolverWords();
				
				// Prefiltering - stadarize input (remove long words if there are too many).
				int size = Math.min(sizeY, sizeX);
				int maxLongWords = size/3;
				int longWords = 0;
				List<String> toRemove = new ArrayList<String>();
				for(int i=0;i<words.size();i++) {
					String word= words.get(i);
					if(size - word.length() < 4) {
						longWords++;
						if(longWords > maxLongWords) {
							toRemove.add(word);
						}
					}
				}
					
				for(String str : toRemove) {
					words.remove(str);
				}
					
					
				return Solver.dualSolve(words, allowDiagonal, allowFitting, new Matrix(new Point(sizeY, sizeX)));
			}

			@Override
			protected void onProgressUpdate(Float... progress) {
				super.onProgressUpdate(progress);
				if (!m_completed) {
					onSolver.onSolverProgress(progress[0]);
				}
			}

			@Override
			protected void onPostExecute(Matrix result) {
				super.onPostExecute(result);
				m_completed = true;
				onSolver.onSolverComplete(result);
			}

		}.execute();
	}

}
