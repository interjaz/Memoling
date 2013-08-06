package app.memoling.android.preference.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

public class TimePreference extends DialogPreference {
	private int m_lastHour = 0;
	private int m_lastMinute = 0;
	private TimePicker m_picker = null;

	public int getLastHour() {
		return m_lastHour;
	}
	
	public int getLastMinute() {
		return m_lastMinute;
	}
	
	public static int getHour(String time) {
		String[] pieces = time.split(":");

		return (Integer.parseInt(pieces[0]));
	}

	public static int getMinute(String time) {
		String[] pieces = time.split(":");

		return (Integer.parseInt(pieces[1]));
	}

	public TimePreference(Context ctxt, AttributeSet attrs) {
		super(ctxt, attrs);

		setPositiveButtonText(android.R.string.ok);
		setNegativeButtonText(android.R.string.cancel);
	}

	@Override
	protected View onCreateDialogView() {
		m_picker = new TimePicker(getContext());
		m_picker.setIs24HourView(true);

		return (m_picker);
	}

	@Override
	protected void onBindDialogView(View v) {
		super.onBindDialogView(v);

		m_picker.setCurrentHour(m_lastHour);
		m_picker.setCurrentMinute(m_lastMinute);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (positiveResult) {
			m_lastHour = m_picker.getCurrentHour();
			m_lastMinute = m_picker.getCurrentMinute();

			String time = String.valueOf(m_lastHour) + ":" + String.valueOf(m_lastMinute);

			if (callChangeListener(time)) {
				persistString(time);
			}
		}
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return (a.getString(index));
	}

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
		String time = null;

		if (restoreValue) {
			if (defaultValue == null) {
				time = getPersistedString("00:00");
			} else {
				time = getPersistedString(defaultValue.toString());
			}
		} else {
			time = defaultValue.toString();
		}

		m_lastHour = getHour(time);
		m_lastMinute = getMinute(time);
	}
}