package app.memoling.android.ui.control;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import app.memoling.android.ui.ResourceManager;

public class BarGraph extends LinearLayout {

	private Context m_context;

	private int m_width;
	private int m_height;
	private boolean m_isPainted = false;
	
	
	private float m_max = 1;
	private float[] m_values = new float[] { 0.0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 0.95f, 1f };
	private String[] m_labels = new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };
	private String m_title = "Title";
	private Typeface m_font;

	public BarGraph(Context context) {
		super(context);
		m_font = new ResourceManager(context).getLightFont();
		m_context = context;
	}

	public BarGraph(Context context, AttributeSet attrs) {
		super(context, attrs);
		m_font = new ResourceManager(context).getLightFont();
		m_context = context;
	}

	private void paint() {

		if(m_isPainted) {
			return;
		}
		m_isPainted = true;
		
		int width = m_width;
		int height = m_height;

		if (width == 0 && height == 0) {
			return;
		}

		int topOffset = 60;
		int bottomOffset = 30;
		int leftOffset = 0;
		int rightOffset = 0;
		int barTopMargin = 10;
		int barBottomMargin = 10;
		int barLeftMargin = 10;
		int barRightMargin = 10;

		final int n = m_values.length;

		int barHeight = (int) ((float) (height - topOffset - bottomOffset - barTopMargin * n - barBottomMargin * n) / n);
		int barWidth = width - leftOffset - rightOffset - barLeftMargin - barRightMargin;
		barHeight = Math.max(barHeight, 15);

		TextView lblTitle = new TextView(m_context);
		lblTitle.setText(m_title);
		lblTitle.setGravity(Gravity.CENTER_HORIZONTAL);
		LayoutParams titleParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lblTitle.setLayoutParams(titleParams);
		lblTitle.setTypeface(m_font);
		lblTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
		this.addView(lblTitle);

		TextView[] labels = new TextView[n];
		int maxLabelWidth = -1;
		for (int i = 0; i < n; i++) {

			LinearLayout line = new LinearLayout(m_context);

			TextView label = new TextView(m_context);
			label.setTypeface(m_font);
			label.setText(m_labels[i]);
			label.measure(width, height);
			LayoutParams lblParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			lblParams.setMargins(0, (int) barTopMargin + (int) ((barHeight - label.getMeasuredHeight()) / 2.0), 0, 0);
			label.setLayoutParams(lblParams);
			labels[i] = label;
			if (label.getMeasuredWidth() > maxLabelWidth) {
				maxLabelWidth = label.getMeasuredWidth();
			}

			float scaledValue = scaleValue(m_values[i]);

			View bar = new View(m_context);
			LayoutParams barParams = new LayoutParams((int) (barWidth * scaledValue), barHeight);
			barParams.setMargins(barLeftMargin, barTopMargin, barRightMargin, barBottomMargin);
			bar.setLayoutParams(barParams);
			bar.setBackgroundColor((int) Math.min(((long) (scaledValue * 0xEE) * 0x1000000L + 0x00000000L), 0xFF000000L));

			line.addView(label);
			line.addView(bar);

			this.addView(line);
		}

		for (int i = 0; i < n; i++) {
			labels[i].setWidth(maxLabelWidth);
		}

		int legendSpacing = 10;
		int legendMaxValue = (int)Math.ceil(m_max);
		TextView lblLegendMax = new TextView(m_context);
		lblLegendMax.setTypeface(m_font);
		lblLegendMax.setText(Integer.toString(legendMaxValue));
		lblLegendMax.measure(width, height);

		int legends = Math.min(width / (legendSpacing + lblLegendMax.getMeasuredWidth()), 5);
		LinearLayout legend = new LinearLayout(m_context);
		LayoutParams legendParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		legendParams.setMargins(maxLabelWidth, 0, 0, 0);
		legend.setLayoutParams(legendParams);

		for (int i = 0; i < legends; i++) {
			TextView lblLegend = new TextView(m_context);
			lblLegend.setTypeface(m_font);
			lblLegend.setText(String.format("%1.2f", (float)(i+1) / legends * legendMaxValue));
			lblLegend.setWidth((width - leftOffset - rightOffset - maxLabelWidth) / legends);
			lblLegend.setGravity(Gravity.RIGHT);
			legend.addView(lblLegend);
		}
		this.addView(legend);

		this.setOrientation(LinearLayout.VERTICAL);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		m_width = w;
		m_height = h;
	}

	public void setLabels(String[] labels) {
		m_labels = labels;
	}

	public void setValues(double[] values) {
		m_values = new float[values.length];
		m_max = Float.MIN_VALUE;
		for (int i = 0; i < values.length; i++) {
			m_values[i] = (float) values[i];
			if(m_values[i] > m_max) {
				m_max = (float)values[i];
			}
		}
	}
	
	public void setValues(int[] values) {
		m_values = new float[values.length];
		m_max = Float.MIN_VALUE;
		for (int i = 0; i < values.length; i++) {
			m_values[i] = (float) values[i];
			if(m_values[i] > m_max) {
				m_max = (float)values[i];
			}
		}		
	}


	public void setTitle(String title) {
		m_title = title;
	}

	private float scaleValue(float value) {
		return value / m_max;
	}

	public void bind() {
		this.post(new Runnable() {
			@Override
			public void run() {
				paint();
			}
		});
	}
}
