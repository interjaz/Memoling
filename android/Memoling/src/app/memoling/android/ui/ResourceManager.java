package app.memoling.android.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class ResourceManager {

	private Context m_ctx;
	private LayoutInflater m_inflater;

	public ResourceManager(Context ctx) {
		m_ctx = ctx;
	}

	public ResourceManager(Activity ctx) {
		m_ctx = ctx;
		m_inflater = ctx.getLayoutInflater();
	}

	public Typeface getThinFont() {
		return Typeface.createFromAsset(m_ctx.getAssets(), "Roboto-Thin.ttf");
	}

	public Typeface getCondensedFont() {
		return Typeface.createFromAsset(m_ctx.getAssets(), "Roboto-Condensed.ttf");
	}

	public void setFont(TextView view, Typeface font) {
		view.setTypeface(font);
	}

	public void setFont(int resource, Typeface font) {
		setFont(((TextView) ((Activity)m_ctx).findViewById(resource)), font);
	}

	public void setFont(int parent, int resource, Typeface font) {
		View root = m_inflater.inflate(parent, null);
		setFont(((TextView) root.findViewById(resource)), font);
	}
	
	public void setFont(View parent, int resource, Typeface font) {
		setFont(((TextView) parent.findViewById(resource)), font);
	}
}
