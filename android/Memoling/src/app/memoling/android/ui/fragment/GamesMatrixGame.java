package app.memoling.android.ui.fragment;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.TextView;
import app.memoling.android.R;
import app.memoling.android.adapter.WordListAdapter;
import app.memoling.android.crossword.SolverThread;
import app.memoling.android.crossword.SolverThread.ISolver;
import app.memoling.android.entity.Language;
import app.memoling.android.helper.Helper;
import app.memoling.android.ui.ApplicationFragment;
import app.memoling.android.ui.ResourceManager;
import app.memoling.android.ui.adapter.ModifiableComplexTextAdapter;
import app.memoling.android.ui.view.GamesSourceView;

public abstract class GamesMatrixGame extends ApplicationFragment implements OnTouchListener, Callback, ISolver,
		OnItemSelectedListener {

	private SurfaceView m_surface;
	private SurfaceThread m_surfaceThread;

	private int m_originalOrientation;
	private TextView m_lblResult;
	private Spinner m_spSource;
	private ModifiableComplexTextAdapter<GamesSourceView> m_adapter;

	protected app.memoling.android.crossword.Matrix m_words;

	private float m_solverProgress;
	private boolean m_showProgress = false;
	private boolean m_wordsLoading = false;
	private int m_wordsLoadingEffect = 0;

	protected int m_sizeX = 12;
	protected int m_sizeY = 12;

	private Animation m_fadeIn;
	private Animation m_fadeOut;

	private Paint m_progressPaint;
	private BitmapDrawable m_bkg;
	private int m_width;
	private int m_height;

	protected abstract int getLayout();
	
	protected abstract String getTitle();

	protected abstract void onCreateView(View view);

	protected abstract void onDraw(Canvas c);

	protected abstract void onMatrixFound(app.memoling.android.crossword.Matrix words);

	//
	// Fragment
	//

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View contentView = super.onCreateView(inflater, container, savedInstanceState,
				inflater.inflate(getLayout(), container, false));
		setTitle(getTitle());

		m_originalOrientation = getActivity().getRequestedOrientation();
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		ResourceManager resources = getResourceManager();
		Typeface thinFont = resources.getThinFont();

		m_surface = (SurfaceView) contentView.findViewById(R.id.matrixgame_surface);
		m_surface.setOnTouchListener(this);
		m_surface.getHolder().addCallback(this);

		m_lblResult = (TextView) contentView.findViewById(R.id.matrixgame_lblResult);

		m_spSource = (Spinner) contentView.findViewById(R.id.matrixgame_spSource);
		m_adapter = new ModifiableComplexTextAdapter<GamesSourceView>(getActivity(), R.layout.adapter_textdropdown,
				new int[] { R.id.textView1 }, new Typeface[] { thinFont });
		m_adapter.addAll(GamesSourceView.getSourceViews(getActivity()));
		m_spSource.setAdapter(m_adapter);

		m_spSource.setOnItemSelectedListener(this);

		resources.setFont(m_lblResult, thinFont);
		resources.setFont(contentView, R.id.textView1, thinFont);

		onCreateView(contentView);

		// Set Animations
		m_fadeIn = new AlphaAnimation(0.0f, 1.0f);
		m_fadeIn.setDuration(1000);
		m_fadeIn.setAnimationListener(new FadeInEventHandler());
		m_fadeOut = new AlphaAnimation(1.0f, 0.0f);
		m_fadeOut.setDuration(1000);
		m_fadeOut.setAnimationListener(new FadeOutEventHandler());

		m_progressPaint = new Paint();
		m_progressPaint.setStyle(Style.STROKE);
		m_progressPaint.setColor(0xAAAAAA);

		m_progressPaint.setStrokeWidth(Helper.dipToPixels(getActivity(), 10));
		m_progressPaint.setAlpha(0xCC);

		m_bkg = (BitmapDrawable) getResources().getDrawable(R.drawable.bkg);
		m_width = Helper.getActivityWidth(getActivity());
		m_height = Helper.getActivityHeight(getActivity());
		
		return contentView;
	}

	@Override
	public void onDestroyView() {
		getActivity().setRequestedOrientation(m_originalOrientation);
		super.onDestroyView();
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		newGame();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	//
	// ApplicationFragment
	//

	@Override
	protected void onDataBind(Bundle savedInstanceState) {
		newGame();
	}

	//
	// Surface
	//

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		m_surfaceThread = new SurfaceThread(m_surface.getHolder());
		m_surfaceThread.setRunning(true);
		m_surfaceThread.start();

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		m_surfaceThread.setRunning(false);
		while (retry) {
			try {
				m_surfaceThread.join();
				retry = false;
			} catch (InterruptedException e) {

			}
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	private class SurfaceThread extends Thread {

		private SurfaceHolder surfaceHolder;
		private boolean runFlag = false;
		boolean firstTime = true;

		public SurfaceThread(SurfaceHolder surfaceHolder) {
			this.surfaceHolder = surfaceHolder;
		}

		public void setRunning(boolean run) {
			this.runFlag = run;
		}

		@Override
		public void run() {
			Canvas c;

			while (this.runFlag) {

				if (firstTime) {
					initDraw();
					firstTime = false;
					continue;
				}

				c = null;
				try {

					c = this.surfaceHolder.lockCanvas(null);
					synchronized (this.surfaceHolder) {
						draw(c);
					}
				} finally {

					if (c != null) {
						this.surfaceHolder.unlockCanvasAndPost(c);

					}
				}
			}
		}

	}

	private void initDraw() {

	}

	@SuppressLint("WrongCall")
	private void draw(Canvas c) {

		if (c == null) {
			return;
		}

		m_bkg.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
		m_bkg.setBounds(0, 0, m_surface.getWidth(), m_surface.getHeight());
		m_bkg.draw(c);

		if (m_showProgress) {

			m_progressPaint.setAlpha(0xFF);

			if (m_wordsLoading) {
				m_wordsLoadingEffect = (m_wordsLoadingEffect + 1) & 0xFF;
				m_progressPaint.setAlpha(m_wordsLoadingEffect);
				c.drawLine(c.getWidth() / 2f - m_width/3, c.getHeight() / 2f, c.getWidth() / 2f + m_width/3, c.getHeight() / 2f,
						m_progressPaint);
				return;
			}

			c.drawLine(c.getWidth() / 2f - m_width/3, c.getHeight() / 2f, c.getWidth() / 2f - m_width/3 + m_solverProgress * 2*m_width/3,
					c.getHeight() / 2f, m_progressPaint);

			return;
		}

		onDraw(c);
	}

	//
	// Animations
	//

	private class FadeInEventHandler implements AnimationListener {

		@Override
		public void onAnimationEnd(Animation animation) {
			m_lblResult.startAnimation(m_fadeOut);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationStart(Animation animation) {
		}

	}

	private class FadeOutEventHandler implements AnimationListener {

		@Override
		public void onAnimationEnd(Animation animation) {
			newGame();
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationStart(Animation animation) {
		}

	}

	//
	// Game
	//

	protected boolean isLoading() {
		return m_showProgress;
	}

	protected void newGame() {

		if (m_showProgress) {
			// Already loading new game
			return;
		}

		m_wordsLoading = true;
		m_solverProgress = 0.0f;
		m_wordsLoadingEffect = 0;

		SolverThread th = new SolverThread();
		th.start(false, false, m_sizeY, m_sizeX, this);
		m_showProgress = true;
	}

	protected void endGame() {
		m_lblResult.startAnimation(m_fadeIn);
	}

	//
	// Solver
	//

	@Override
	public ArrayList<String> getSolverWords() {

		int count = 20;
		int min = 4;
		int max = 12;

		WordListAdapter wordAdapter = new WordListAdapter(getActivity());
		ArrayList<String> words;

		int value = m_adapter.getItem(m_spSource.getSelectedItemPosition()).getValue();
		switch (value) {
		case 0:
		default:
			// EN
			words = wordAdapter.getRandom(Language.EN, count, min, max);
			break;
		case 1:
			// ES
			words = wordAdapter.getRandom(Language.ES, count, min, max);
			break;
		case 2:
			// DE
			words = wordAdapter.getRandom(Language.DE, count, min, max);
			break;
		case 3:
			// FR
			words = wordAdapter.getRandom(Language.FR, count, min, max);
			break;
		case 4:
			// IT
			words = wordAdapter.getRandom(Language.IT, count, min, max);
			break;
		case 5:
			// PL
			words = wordAdapter.getRandom(Language.PL, count, min, max);
			break;
		}

		return words;
	}

	@Override
	public void onSolverProgress(float progress) {
		if (m_solverProgress > 0) {
			m_wordsLoading = false;
		}
		m_solverProgress = progress;
	}

	@Override
	public void onSolverComplete(app.memoling.android.crossword.Matrix words) {
		m_showProgress = false;
		m_words = words;
		onMatrixFound(words);
	}

}
