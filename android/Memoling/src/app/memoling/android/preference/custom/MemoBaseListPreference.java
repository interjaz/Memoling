package app.memoling.android.preference.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.ListPreference;
import android.util.AttributeSet;

public class MemoBaseListPreference extends ListPreference {

	public MemoBaseListPreference(Context context, AttributeSet attrs) {
		super(context, attrs);

		CharSequence[] entries = new String[] {"test","test2"};
		CharSequence[] entryValues = new String[] {"test","test2"};
		
		this.setEntries(entries);
		this.setEntryValues(entryValues);
		this.setSummary(entries[0]);
	}
	
	@Override
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
	    if (restorePersistedValue) {
	        // Restore existing state
	    } else {
	        // Set default state from the XML attribute
	    }
	}
	
	
	
	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return a.getString(index);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		
	}

	
}
