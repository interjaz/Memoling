package app.memoling.android.ui.fragment;

import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import app.memoling.android.R;
import app.memoling.android.crossword.Matrix;
import app.memoling.android.crossword.MatrixWord;
import app.memoling.android.helper.Helper;
import app.memoling.android.ui.ResourceManager;

import com.actionbarsherlock.view.MenuItem;

public class GamesCrosswordFragment extends GamesMatrixGame {

	private Button m_btnHorizontal;
	private Button m_btnVertical;
	private Button m_btnSubmit;
	private EditText m_txtWord;
	private LinearLayout m_layInput;

	private int m_itemHeight;
	private int m_itemWidth;
	private int m_itemStartHeight;
	private int m_itemStartWidth;
	private int m_itemTxtHeight;

	private int[][] m_grid;
	private boolean[][] m_letterGrid;

	private final static int Horizontal = 1;
	private final static int Vertical = 2;

	private MatrixWord m_hWord;
	private MatrixWord m_vWord;
	
	private Paint m_txtPaint;
	private Paint m_borderPaint;
	private Rect m_rectangle;

	@Override
	protected int getLayout() {
		return R.layout.games_crossword;
	}

	@Override
	protected String getTitle() {
		return getActivity().getString(R.string.games_crossword);
	}
	
	@Override
	protected void onCreateView(View contentView) {

		ResourceManager resources = getResourceManager();
		Typeface thinFont = resources.getThinFont();

		m_btnVertical = (Button) contentView.findViewById(R.id.crossword_btnVertical);
		m_btnVertical.setOnClickListener(new BtnVerticalEventHandler());
		m_btnHorizontal = (Button) contentView.findViewById(R.id.crossword_btnHorizontal);
		m_btnHorizontal.setOnClickListener(new BtnHorizontalEventHandler());
		m_btnSubmit = (Button) contentView.findViewById(R.id.crossword_btnSubmit);
		m_btnSubmit.setOnClickListener(new BtnSubmitEventHandler());
		m_txtWord = (EditText) contentView.findViewById(R.id.crossword_txtWord);
		m_layInput = (LinearLayout) contentView.findViewById(R.id.crossword_layInput);

		resources.setFont(m_btnVertical, thinFont);
		resources.setFont(m_btnHorizontal, thinFont);
		resources.setFont(m_btnSubmit, thinFont);
		resources.setFont(m_txtWord, thinFont);
		resources.setFont(contentView, R.id.textView1, thinFont);
		
		m_txtPaint = new Paint();
		m_txtPaint.setStyle(Style.STROKE);
		m_txtPaint.setColor(0xCCEEEEEE);

		m_borderPaint = new Paint();
		m_borderPaint.setStyle(Style.STROKE);
		m_borderPaint.setColor(0xFFDDDDDD);
		m_borderPaint.setStrokeWidth(Helper.dipToPixels(getActivity(), 2f));
		
		m_rectangle = new Rect();
	}
	
	@Override
	protected boolean onCreateOptionsMenu() {

		MenuItem item = createMenuItem(0, getString(R.string.games_hint));
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		return super.onCreateOptionsMenu();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == 0) {
			showLetter();
			if (isGameFinished()) {
				endGame();
			}
			return false;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			checkWord(event.getX(), event.getY());
		}

		return true;
	}

	@Override
	public boolean onBackPressed() {

		if (m_layInput.getVisibility() == View.VISIBLE) {
			m_layInput.setVisibility(View.GONE);
			return false;
		}

		return super.onBackPressed();
	}

	@Override
	protected void onMatrixFound(Matrix words) {
		m_grid = new int[words.size.y][words.size.x];
		m_letterGrid = new boolean[words.size.y][words.size.x];
		Random r = new Random();

		for (MatrixWord w : words.words) {
			int y0 = w.from.y;
			int x0 = w.from.x;

			int val = Vertical;
			if (w.to.x > w.from.x) {
				val = Horizontal;
			}

			for (int i = 0; i < w.word.length(); i++) {
				int y = y0 + (int) (((float) w.to.y + 1 - w.from.y) / w.word.length() * i);
				int x = x0 + (int) (((float) w.to.x + 1 - w.from.x) / w.word.length() * i);
				if (m_grid[y][x] != 0) {
					m_letterGrid[y][x] = true;
				} else {
					float showFactor = 0.62f;
					m_letterGrid[y][x] |= (r.nextFloat() > showFactor);
				}
				m_grid[y][x] = m_grid[y][x] | val;
			}
		}
	}

	@Override
	protected void onDraw(Canvas c) {
		if (c == null) {
			return;
		}
		
		Paint txtPaint = m_txtPaint;
		Paint borderPaint = m_borderPaint;

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
		m_itemStartWidth = paddingWidth + itemWidth;
		m_itemStartHeight = (int) ((itemHeight + itemPadding) * 0.8 + paddingHeight);
		m_itemTxtHeight = Helper.determineMaxTextSize("W", 0.8f * itemWidth);

		txtPaint.setTextSize(m_itemTxtHeight);

		Rect r = m_rectangle;
		for (int y = 0; y < sY; y++) {
			for (int x = 0; x < sX; x++) {
				String str = Character.toString(m_words.matrix.get(y).get(x).c);
				int hItem = (int) ((itemHeight + itemPadding) * (y + 0.8) + paddingHeight);
				int wItem = (itemWidth + itemPadding) * x + paddingWidth;

				if (!str.equals("-")) {
					if (m_letterGrid[y][x]) {
						c.drawText(str.toUpperCase(), wItem, hItem, txtPaint);
					}

					r.top = (int) ((itemHeight + itemPadding) * (y + 0.1) + paddingHeight);
					r.bottom = (int) ((itemHeight + itemPadding) * (y + 1.1) + paddingHeight);
					r.left = (itemWidth + itemPadding) * x + paddingWidth - 8;
					r.right = (itemWidth + itemPadding) * (x + 1) + paddingWidth - 8;

					if (y == 0 || m_grid[y - 1][x] == 0 || !sameWord(x, y - 1, x, y)) {
						c.drawLine(r.left, r.top, r.right, r.top, borderPaint);
					} else {
						c.drawLine(r.left, r.top, r.right, r.top, txtPaint);
					}
					if (y == sY - 1 || m_grid[y + 1][x] == 0 || !sameWord(x, y, x, y + 1)) {
						c.drawLine(r.left, r.bottom, r.right, r.bottom, borderPaint);
					} else {
						c.drawLine(r.left, r.bottom, r.right, r.bottom, txtPaint);
					}
					if (x == 0 || m_grid[y][x - 1] == 0 || !sameWord(x - 1, y, x, y)) {
						c.drawLine(r.left, r.top, r.left, r.bottom, borderPaint);
					} else {
						c.drawLine(r.left, r.top, r.left, r.bottom, txtPaint);
					}
					if (x == sX - 1 || m_grid[y][x + 1] == 0 || !sameWord(x, y, x + 1, y)) {
						c.drawLine(r.right, r.top, r.right, r.bottom, borderPaint);
					} else {
						c.drawLine(r.right, r.top, r.right, r.bottom, txtPaint);
					}

				}
			}
		}
	}

	public boolean sameWord(int x0, int y0, int x1, int y1) {

		for (int i = 0; i < m_words.words.size(); i++) {
			MatrixWord word = m_words.words.get(i);

			if (word.from.x <= x0 && word.to.x >= x1 && word.from.y <= y0 && word.to.y >= y1) {
				return true;
			}
		}

		return false;
	}
	
	public synchronized void showLetter() {
		if (m_grid == null || isLoading()) {
			return;
		}

		Random r = new Random();

		for (int k = 0; k < 200; k++) {
			for (int i = 0; i < m_words.size.y; i++) {
				for (int j = 0; j < m_words.size.x; j++) {
					if (m_grid[j][i] != 0 && !m_letterGrid[j][i]) {
						boolean show = r.nextFloat() > 0.95;
						if (show) {
							m_letterGrid[j][i] |= true;
							return;
						}
					}
				}
			}
		}
	}

	private void checkWord(float x, float y) {

		if (m_itemWidth == 0 || m_itemHeight == 0) {
			// Not loaded yet;
			return;
		}

		int xGridItem = Math.max(0, Math.min((int) (x - m_itemStartWidth) / m_itemWidth, m_sizeX - 1));
		int yGridItem = Math.max(0,
				Math.min((int) (y - m_itemStartHeight + m_itemTxtHeight) / m_itemHeight, m_sizeY - 1));

		m_hWord = null;
		m_vWord = null;

		if (m_words != null) {
			for (MatrixWord word : m_words.words) {
				// Vertical
				if (word.from.y < word.to.y) {
					if (word.from.y <= yGridItem && yGridItem <= word.to.y && word.from.x == xGridItem) {
						m_vWord = word;
					}
				}
				// Horizontal
				else {
					if (word.from.x <= xGridItem && xGridItem <= word.to.x && word.from.y == yGridItem) {
						m_hWord = word;
					}
				}
			}
		}

		if (m_hWord != null || m_vWord != null) {
			fillInput(true);
			m_layInput.setVisibility(View.VISIBLE);
		}
	}

	private void fillInput(boolean horizontal) {
		m_btnHorizontal.setVisibility(View.GONE);
		m_btnVertical.setVisibility(View.GONE);

		String word = "";
		if (m_hWord != null && m_vWord != null) {
			if (horizontal) {
				m_btnVertical.setVisibility(View.VISIBLE);
				word = getVisiblePart(m_hWord);
			} else {
				m_btnHorizontal.setVisibility(View.VISIBLE);
				word = getVisiblePart(m_vWord);
			}
		} else if (m_hWord != null) {
			word = getVisiblePart(m_hWord);
		} else if (m_vWord != null) {
			word = getVisiblePart(m_vWord);
		}

		m_txtWord.setText(word);
	}

	private String getVisiblePart(MatrixWord word) {
		String str = "";
		for (int i = 0; i < word.word.length(); i++) {
			int x = word.from.x;
			int y = word.from.y;
			if (word.from.x == word.to.x) {
				y += i;
			} else {
				x += i;
			}

			if (!m_letterGrid[y][x]) {
				str += " ";
			} else {
				str += Character.toString(m_words.matrix.get(y).get(x).c).toUpperCase();
			}
		}
		return str;
	}

	private boolean isGameFinished() {

		for (MatrixWord word : m_words.words) {
			for (int y = word.from.y; y <= word.to.y; y++) {
				for (int x = word.from.x; x <= word.to.x; x++) {
					if (!m_letterGrid[y][x]) {
						return false;
					}
				}
			}
		}

		return true;
	}

	private class BtnHorizontalEventHandler implements OnClickListener {
		@Override
		public void onClick(View v) {
			fillInput(true);
		}
	}

	private class BtnVerticalEventHandler implements OnClickListener {
		@Override
		public void onClick(View v) {
			fillInput(false);
		}
	}

	private class BtnSubmitEventHandler implements OnClickListener {
		@Override
		public void onClick(View v) {
			MatrixWord word;
			if (m_hWord == null) {
				word = m_vWord;
			} else if (m_vWord == null) {
				word = m_hWord;
			} else {
				if (m_btnVertical.getVisibility() == View.VISIBLE) {
					word = m_hWord;
				} else {
					word = m_vWord;
				}
			}

			if (word.word.toUpperCase().equals(m_txtWord.getText().toString().toUpperCase())) {
				for (int y = word.from.y; y <= word.to.y; y++) {
					for (int x = word.from.x; x <= word.to.x; x++) {
						m_letterGrid[y][x] = true;
					}
				}
			}

			m_layInput.setVisibility(View.GONE);

			if (isGameFinished()) {
				endGame();
			}
		}
	}

}
