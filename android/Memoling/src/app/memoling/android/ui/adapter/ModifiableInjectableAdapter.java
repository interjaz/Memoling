package app.memoling.android.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import app.memoling.android.helper.AppLog;
import app.memoling.android.ui.ResourceManager;

public class ModifiableInjectableAdapter<T extends IInject> extends ModifiableAdapter<T> {

	private boolean m_alternateBackground;
	private Drawable m_alternateBackgroundDrawable = new ColorDrawable(0x44666666);
	private boolean m_originalBackgroundSet;
	private Drawable m_originalBackgroundDrawable;
	private ResourceManager m_resources;
	
	public ModifiableInjectableAdapter(Context context, int resourceId, ResourceManager resources, boolean alternateBackground) {
		super(context, resourceId);
		m_resources = resources;
		m_alternateBackground = alternateBackground;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	protected View getViewDefinition(int position, View convertView, ViewGroup parent) {
		View view;

		if (convertView == null) {
			view = m_inflater.inflate(m_resourceId, parent, false);
		} else {
			view = convertView;
		}
		
		if(Build.VERSION.SDK_INT == Build.VERSION_CODES.GINGERBREAD_MR1 && view instanceof TextView) {
			view.setBackgroundColor(0xff1A1A1A);
		}
		
		try {
			
			IInject object = getItem(position);
			object.injcet(view,m_resources);

			if (!m_originalBackgroundSet && position % 2 == 0) {
				m_originalBackgroundDrawable = view.getBackground();
				m_originalBackgroundSet = true;
			}

			if (m_alternateBackground) {
				if (position % 2 == 1) {
					if (view != null) {
						Drawable background = view.getBackground();
						if (background != null && background != m_alternateBackgroundDrawable) {
							view.getBackground().setAlpha(127);
						} else {
							if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
								view.setBackgroundDrawable(m_alternateBackgroundDrawable);
							} else {
								view.setBackground(m_alternateBackgroundDrawable);
							}
						}
					}
				} else {
					if (view != null) {
						Drawable background = view.getBackground();
						if (background != null && background != m_originalBackgroundDrawable) {
							view.getBackground().setAlpha(255);
							// This need to be done as well
							if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
								view.setBackgroundDrawable(m_originalBackgroundDrawable);
							} else {
								view.setBackground(m_originalBackgroundDrawable);
							}
						}
					}
				}
			}

		} catch (ClassCastException e) {
			AppLog.e("ArrayAdapter", "You must supply a resource ID for a TextView");
			throw new IllegalStateException("ArrayAdapter requires the resource ID to be a TextView", e);
		}

		return view;
	}

}
