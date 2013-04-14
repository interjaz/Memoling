package com.interjaz.ui.control;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.interjaz.R;
import com.interjaz.WorkerThread;
import com.interjaz.helper.Helper;

public class BouncyLogo extends View implements Runnable {

	private Paint[] m_paints = new Paint[20];
	private Bitmap m_bmpLogo;
	private Handler m_uiHandler = new Handler();
	private int m_rotate = 0;
	private int m_inc = 2;
	
	private int m_width;
	private int m_height;

	private Matrix[] m_matrices = new Matrix[20];

	public BouncyLogo(Context context, AttributeSet attrs) {
		super(context, attrs);

		for (int i = 0; i < m_paints.length; i++) {
			m_paints[i] = new Paint();
			m_paints[i].setAlpha(100 * (i + 1) / m_paints.length);
		}

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		m_bmpLogo = BitmapFactory.decodeResource(getResources(), R.drawable.ic_main, options);

		m_uiHandler.postDelayed(this, 10);
	}

	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		new WorkerThread<Void,Void,Void>() {

			private Bitmap m_bmpLogoCopy;
			
			@Override
			protected Void doInBackground(Void... arg0) {
				m_bmpLogoCopy = Helper.invertBitmapColors(m_bmpLogo);
				m_bmpLogo = m_bmpLogoCopy;
				return null;
			}
			
		}.execute();
		return super.onTouchEvent(event);
	}



	@Override
	public void onDraw(Canvas canvas) {
		m_width = canvas.getHeight();
		m_height = canvas.getWidth();

		if (m_matrices[0] != null) {

			for (int i = 0; i < m_matrices.length; i++) {

				canvas.drawBitmap(m_bmpLogo, m_matrices[i], m_paints[i]);
			}
		}
	}

	private Matrix generateMatrix(int rotate) {

		if (m_width == 0 || m_height == 0) {
			return null;
		}

		Matrix matrix = new Matrix();
		
		int x = m_width/2;
		int y = m_height/2;
	

		float xS = 0.7f + (float) (Math.sin(rotate / 23f) + 1) / 4f;
		float yS = 0.7f + (float) (Math.cos(rotate / 17f) + 1) / 4f;
		
		int w =  (int) (m_bmpLogo.getWidth() * xS);
		int h = (int) (m_bmpLogo.getHeight() * yS);

		
		matrix.preTranslate(x-w/2, y-h/2);
		matrix.postRotate(rotate, x+0.1f*w, y+0.1f*h);
		matrix.preScale(xS, yS);

		return matrix;
	}

	@Override
	public void run() {
		invalidate();
		m_rotate += m_inc;
		
		for (int i = 0; i < m_matrices.length; i++) {
			m_matrices[i] = generateMatrix(m_rotate + i*m_inc);
		}

		m_uiHandler.postDelayed(this, 10);
	}

}
