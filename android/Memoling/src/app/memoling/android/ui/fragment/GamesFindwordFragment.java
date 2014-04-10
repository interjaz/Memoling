package app.memoling.android.ui.fragment;

import java.util.ArrayList;
import java.util.Locale;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import app.memoling.android.R;
import app.memoling.android.crossword.Matrix;
import app.memoling.android.crossword.MatrixWord;
import app.memoling.android.helper.Helper;
import app.memoling.android.ui.ResourceManager;

public class GamesFindwordFragment extends GamesMatrixGame {

	private TextView m_lblScore;

	private float[] m_x = new float[2];
	private float[] m_y = new float[2];

	private int m_itemHeight;
	private int m_itemWidth;
	private int m_itemStartHeight;
	private int m_itemStartWidth;
	private int m_itemTxtHeight;

	private ArrayList<MatrixWord> m_found;

	private ArrayList<String> m_currentLetters;

	private Paint m_txtPaint;
	private Paint m_foundPaint;
	private Paint m_foundStrokePaint;
	private Paint m_touchPaint;

	private RectF m_paintRect = new RectF();
	
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
		m_foundPaint.setAlpha(0x77);
		
		m_foundStrokePaint = new Paint();
		m_foundStrokePaint.setStyle(Style.STROKE);
		m_foundStrokePaint.setColor(0xFFFFFF);
		m_foundStrokePaint.setStrokeWidth(Helper.dipToPixels(getActivity(), 20));
		m_foundStrokePaint.setAlpha(0x77);
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
				c.drawText(str.toUpperCase(Locale.getDefault()), wItem, hItem, txtPaint);
			}
		}

		synchronized (m_found) {
			
			for (MatrixWord word : m_found) {

				int x0 = (int)((itemWidth + itemPadding) * (word.from.x - 0.2) + paddingWidth);
				int x1 = (int)((itemWidth + itemPadding) * (word.to.x + 0.7) + paddingWidth);
				
				int y0 = (int) ((itemHeight + itemPadding) * (word.from.y - 0.5) + paddingHeight);
				int y1 = (int) ((itemHeight + itemPadding) * (word.to.y + 0.4) + paddingHeight);

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

		m_lblScore.setText(String.format("%d/%d", m_found.size(), m_words.words.size()));
	}

	private String getChar(int val) {
		if (m_currentLetters.size() == 0) {
			return "-";
		}

		return m_currentLetters.get(val % m_currentLetters.size());
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

		int x0GridItem = Math.max(0, Math.min((int) (x0 - m_itemStartWidth) / m_itemWidth, m_sizeX - 1));
		int y0GridItem = Math.max(0,
				Math.min((int) (y0 - m_itemStartHeight + m_itemTxtHeight) / m_itemHeight, m_sizeY - 1));
		int x1GridItem = Math.max(0, Math.min((int) (x1 - m_itemStartWidth) / m_itemWidth, m_sizeX - 1));
		int y1GridItem = Math.max(0,
				Math.min((int) (y1 - m_itemStartHeight + m_itemTxtHeight) / m_itemHeight, m_sizeY - 1));

		if (m_words != null) {
			for (MatrixWord word : m_words.words) {
				if (Math.abs(word.from.x - x0GridItem) <= 1 && Math.abs(word.from.y - y0GridItem) <= 1
						&& Math.abs(word.to.x - x1GridItem) <= 1 && Math.abs(word.to.y - y1GridItem) <= 1) {
					if (!m_found.contains(word)) {
						synchronized (m_found) {
							m_found.add(word);
						}
						m_x[0] = 0;
						m_x[1] = 0;
						m_y[0] = 0;
						m_y[1] = 0;
						m_lblScore.setText(String.format("%d/%d", m_found.size(), m_words.words.size()));

						if (m_found.size() == m_words.words.size()) {
							endGame();
						}

						return;
					}
				} else if (Math.abs(word.from.x - x1GridItem) <= 1 && Math.abs(word.from.y - y1GridItem) <= 1
						&& Math.abs(word.to.x - x0GridItem) <= 1 && Math.abs(word.to.y - y0GridItem) <= 1) {
					if (!m_found.contains(word)) {
						synchronized (m_found) {
							m_found.add(word);
						}
						m_x[0] = 0;
						m_x[1] = 0;
						m_y[0] = 0;
						m_y[1] = 0;
						m_lblScore.setText(String.format("%d/%d", m_found.size(), m_words.words.size()));

						if (m_found.size() == m_words.words.size()) {
							endGame();
						}

						return;
					}
				}
			}
		}
	}
}
