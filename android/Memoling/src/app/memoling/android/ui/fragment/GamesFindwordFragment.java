package app.memoling.android.ui.fragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import app.memoling.android.R;
import app.memoling.android.crossword.Char;
import app.memoling.android.crossword.Matrix;
import app.memoling.android.crossword.MatrixWord;
import app.memoling.android.crossword.Point;
import app.memoling.android.helper.AppLog;
import app.memoling.android.helper.Helper;
import app.memoling.android.ui.ResourceManager;
import app.memoling.android.wordlist.sqlprovider.WordListAdapter;

import com.actionbarsherlock.view.MenuItem;

public class GamesFindwordFragment extends GamesMatrixGame {

	private TextView m_lblScore;

	private float[] m_x = new float[2];
	private float[] m_y = new float[2];

	private int m_itemHeight;
	private int m_itemWidth;
	private int m_itemStartHeight;
	private int m_itemStartWidth;
	private int m_itemTxtHeight;

	private List<MatrixWord> m_found;

	private List<String> m_currentLetters;

	private Paint m_txtPaint;
	private Paint m_foundPaint;
	private Paint m_foundStrokePaint;
	private Paint m_touchPaint;

	private RectF m_paintRect = new RectF();
	
	private List<String> m_wordsInTheGrid;
	private List<String> m_foundWords;
	
	@Override
	protected boolean getAllowDiagonal() {
		//TODO: when there is a word from right to left it looks ugly
		return false;
	}
	
	@Override
	protected int getLayout() {
		return R.layout.games_findword;
	}

	@Override
	protected String getTitle() {
		return getActivity().getString(R.string.memolist_findword);
	}

	@Override
	protected void onCreateView(View contentView) {

		ResourceManager resources = getResourceManager();
		Typeface thinFont = resources.getLightFont();

		m_lblScore = (TextView) contentView.findViewById(R.id.findword_lblScore);

		resources.setFont(m_lblScore, thinFont);

		m_found = new ArrayList<MatrixWord>();
		m_currentLetters = new ArrayList<String>();

		m_txtPaint = new Paint();
		m_txtPaint.setStyle(Style.STROKE);
		m_txtPaint.setColor(0xFF222222);

		m_touchPaint = new Paint();
		m_touchPaint.setStyle(Style.STROKE);
		m_touchPaint.setColor(0xFFFFFF);
		m_touchPaint.setStrokeWidth(Helper.dipToPixels(getActivity(), 30));
		m_touchPaint.setAlpha(0xCC);

		m_foundPaint = new Paint();
		m_foundPaint.setStyle(Style.FILL);
		m_foundPaint.setColor(0xFFFFFF);
		m_foundPaint.setAlpha(0x60);
		
		m_foundStrokePaint = new Paint();
		m_foundStrokePaint.setStyle(Style.STROKE);
		m_foundStrokePaint.setColor(0xFFFFFF);
		m_foundStrokePaint.setStrokeWidth(Helper.dipToPixels(getActivity(), 20));
		m_foundStrokePaint.setAlpha(0x60);
	}
	
	@Override
	protected boolean onCreateOptionsMenu() {

		MenuItem item = createMenuItem(0, getString(R.string.games_showWords));
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		return super.onCreateOptionsMenu();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == 0) {

			if(m_wordsInTheGrid == null) {
				return false;
			}
		
			StringBuilder found = new StringBuilder();
			StringBuilder notFound = new StringBuilder();
			
			found.append(getString(R.string.games_found) + "\n");
			notFound.append(getString(R.string.games_notFound) + "\n");
			
			for(String word : m_wordsInTheGrid) {
				boolean isFound = m_foundWords.contains(word);
				if(isFound) {
					found.append(word + "\n");
				} else {
					notFound.append(word + "\n");
				}
			}

			found.setLength(found.length()-1);
			
			notFound.append("\n");
			notFound.append(found);
			
			new AlertDialog.Builder(getActivity())
			.setTitle(getString(R.string.games_words))
			.setMessage(notFound.toString())
			.setNeutralButton(getString(R.string.games_ok), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
				
			})
			.create().show();
			
			return false;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		m_x[1] = event.getX();
		m_y[1] = event.getY();

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			m_x[0] = event.getX();
			m_y[0] = event.getY();
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			checkWord();
		}

		return true;
	}

	@Override
	protected void onDraw(Canvas c) {

		Paint txtPaint = m_txtPaint;
		
		if(m_words == null) {
			return;
		}

		int sX = m_words.size.x;
		int sY = m_words.size.y;
		int paddingWidth = 20;
		int paddingHeight = 20;
		int h = c.getHeight() - 2 * paddingHeight;
		int w = c.getWidth() - 2 * paddingWidth;
		int itemPadding = 5;
		int itemWidth = w / sX - itemPadding;
		int itemHeight = h / sY - itemPadding;

		m_itemWidth = itemWidth;
		m_itemHeight = itemHeight;
		m_itemStartWidth = paddingWidth;
		m_itemStartHeight = (int) ((itemHeight + itemPadding) * 0.2 + paddingHeight);
		m_itemTxtHeight = Helper.determineMaxTextSize("W", 0.8f * itemWidth);

		txtPaint.setTextSize(m_itemTxtHeight);

		for (int y = 0; y < sY; y++) {
			for (int x = 0; x < sX; x++) {
				String str = Character.toString(m_words.matrix.get(y).get(x).c);
				int hItem = (int) ((itemHeight + itemPadding) * (y + 0.2) + paddingHeight);
				int wItem = (itemWidth + itemPadding) * x + paddingWidth;
				if (str.equals("-")) {
					str = getChar(y * sX + x);
				}
				c.drawText(str.toUpperCase(Locale.getDefault()), wItem + itemWidth/4, hItem + 3*itemHeight/4, txtPaint);
			}
		}

		synchronized (m_found) {
			
			for (MatrixWord word : m_found) {
				
				int y0 = (int) ((itemHeight + itemPadding) * (word.from.y + 0.2) + paddingHeight);
				int x0 = (itemWidth + itemPadding) * (word.from.x) + paddingWidth;
			
				int y1 = (int) ((itemHeight + itemPadding) * (word.to.y + 1  + 0.2) + paddingHeight);
				int x1 = (itemWidth + itemPadding) * (word.to.x + 1) + paddingWidth;
				
				if(word.from.x == word.to.x || word.from.y == word.to.y) {
					m_paintRect.left = x0;
					m_paintRect.right = x1;
					m_paintRect.top = y0;
					m_paintRect.bottom = y1;
					
					c.drawRect(m_paintRect, m_foundPaint);
				} else {
					c.drawLine(x0, y0, x1, y1, m_foundStrokePaint);
				}
				
			}
		}

		c.drawLine(m_x[0], m_y[0], m_x[1], m_y[1], m_touchPaint);
	}

	@Override
	protected void onMatrixFound(Matrix words) {
		m_found.clear();
		m_currentLetters.clear();

		for (MatrixWord w : words.words) {
			for (int i = 0; i < w.word.length(); i++) {
				String letter = Character.toString(w.word.charAt(i));
				if (!m_currentLetters.contains(letter)) {
					m_currentLetters.add(letter);
				}
			}
		}

		findCreatedWords();

		m_lblScore.setText(String.format("%d/%d", 0, m_wordsInTheGrid.size()));
	}

	private void findCreatedWords() {
		// Assume word has to have at least 3 letters
		// Assume that words can be made only from left to right and from top to bottom

		setLoading(true);
		
		List<MatrixWord> newWords = new ArrayList<MatrixWord>();
		Set<String> uniqueWords = new HashSet<String>();
		for(int r=0;r<m_words.matrix.size();r++) {
			for(int c=0;c<m_words.matrix.get(0).size();c++) {
				for(int l=2;l<m_words.matrix.get(0).size()-c;l++) {
					String rowWord = "";
					for(int w=0;w<=l;w++) {
						rowWord = rowWord + getMatrixChar(r, c+w);
					};
					
					MatrixWord word = new MatrixWord();
					word.from = new Point(r,c);
					word.to = new Point(r,c+l);
					word.word = rowWord;
					newWords.add(word);
					uniqueWords.add(rowWord);
				}
			}
		}

		for(int c=0;c<m_words.matrix.get(0).size();c++) {
			for(int r=0;r<m_words.matrix.size();r++) {
				for(int l=2;l<m_words.matrix.size()-r;l++) {
					String colWord = "";
					for(int w=0;w<=l;w++) {
						colWord = colWord +getMatrixChar(r+w, c);
					};

					MatrixWord word = new MatrixWord();
					word.from = new Point(r,c);
					word.to = new Point(r+l,c);
					word.word = colWord;
					newWords.add(word);
					uniqueWords.add(colWord);
				}
			}
		}

		m_foundWords = new ArrayList<String>();
		
		try {
			WordListAdapter adapter = new WordListAdapter(getActivity());
			m_wordsInTheGrid = adapter.exists(uniqueWords, getLanguage());
		} catch (Exception ex) {
			AppLog.e("GamesFindWordFragment", "findCreatedWords", ex);
			m_wordsInTheGrid = new ArrayList<String>();
		}

		
		setLoading(false);
	}
	
	private String getChar(int val) {
		if (m_currentLetters.size() == 0) {
			return "-";
		}

		return m_currentLetters.get(val % m_currentLetters.size());
	}
	
	private String getMatrixChar(int r, int c) {
		
		if(m_words == null || r < 0 || r >= m_words.matrix.size() || c < 0 || c >= m_words.matrix.get(0).size()) {
			return "";
		}
		
		Char matrixChar =  m_words.matrix.get(r).get(c);
		int cols = m_words.matrix.get(0).size();
		
		if(matrixChar.c == '-') {
				return getChar(r * cols + c);
		}
		
		return String.valueOf(matrixChar.c);
	}

	private void checkWord() {

		if (m_itemWidth == 0 || m_itemHeight == 0) {
			// Not loaded yet;
			return;
		}

		float x0 = m_x[0];
		float x1 = m_x[1];
		float y0 = m_y[0];
		float y1 = m_y[1];
		
		if(x1 < x0) {
			float t = x1;
			x1 = x0;
			x0 = t;
		}
		
		if(y1 < y0) {
			float t = y1;
			y1 = y0;
			y0 = t;
		}

		int xPadding = 5;
		int yPadding = 5;
		
		int x0GridItem = Math.max(0, Math.min((int) (x0 - m_itemStartWidth) / (m_itemWidth + xPadding), m_sizeX - 1));
		int y0GridItem = Math.max(0,
				Math.min((int) (y0 - m_itemStartHeight + m_itemTxtHeight - 20) / (m_itemHeight + yPadding), m_sizeY - 1));
		int x1GridItem = Math.max(0, Math.min((int) (x1 - m_itemStartWidth) / (m_itemWidth + xPadding), m_sizeX - 1));
		int y1GridItem = Math.max(0,
				Math.min((int) (y1 - m_itemStartHeight + m_itemTxtHeight - 20) / (m_itemHeight + yPadding), m_sizeY - 1));

		String selectedWord = "";
		
		// Try to fix inaccuracies
		double absX = Math.abs(x0GridItem - x1GridItem);
		double absY = Math.abs(y0GridItem - y1GridItem);
		
		if(absX < 2 && absY > 1) {
			x1GridItem = x0GridItem;
		} else if(absY < 2 && absX > 1) {
			y1GridItem = y0GridItem;
		}
		
		if(x0GridItem == x1GridItem) {
			// Vertical\
			for(int y=y0GridItem;y<=y1GridItem;y++) {
				selectedWord = selectedWord + getMatrixChar(y, x0GridItem);
			}
			
			
		} else if(y0GridItem == y1GridItem) {
			// Horizontal

			for(int x=x0GridItem;x<=x1GridItem;x++) {
				selectedWord = selectedWord + getMatrixChar(y0GridItem, x);
			}
		}
		
		if(m_wordsInTheGrid.contains(selectedWord)) {
			if(!m_foundWords.contains(selectedWord)) {
				MatrixWord matrixWord = new MatrixWord();
				matrixWord.from = new Point(y0GridItem, x0GridItem);
				matrixWord.to = new Point(y1GridItem, x1GridItem);
				matrixWord.word = selectedWord;
				
				synchronized(m_found) {
					m_found.add(matrixWord);
				}
				
				synchronized (m_foundWords) {
					m_foundWords.add(selectedWord);
				}
				
				m_lblScore.setText(String.format("%d/%d", m_foundWords.size(), m_wordsInTheGrid.size()));

				if (m_foundWords.size() == m_wordsInTheGrid.size()) {
					endGame();
				}
			}
		}
	}
}
