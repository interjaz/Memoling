package app.memoling.android.ui.view;

import app.memoling.android.helper.Lazy;
import app.memoling.android.ui.ApplicationFragment;

public class DrawerView {

	private int m_icon;
	private int m_text;
	private Lazy<ApplicationFragment> m_fragment;
	private boolean m_isBack;
	private OnClickListener m_onClickListener;
	
	public int getIcon() {
		return m_icon;
	}
	
	public int getText() {
		return m_text;
	}
	
	public ApplicationFragment getFragment() {
		if(m_fragment == null) {
			return null;
		}
		return m_fragment.getValue();
	}
	
	public boolean isBack() {
		return m_isBack;
	}
	
	public OnClickListener getOnClickListener() {
		return m_onClickListener;
	}
	
	public DrawerView() 
	{
		
	}
	
	public DrawerView(int icon, int text, Lazy<ApplicationFragment> fragment) {
		m_icon = icon;
		m_text = text;
		m_fragment = fragment;
	}
	
	public DrawerView(int icon, int text, OnClickListener onClickListener) {
		m_icon = icon;
		m_text = text;
		m_onClickListener = onClickListener;
	}
	
	public DrawerView(int icon, int text) {
		m_icon = icon;
		m_text = text;
		m_isBack = true;
	}
	
	public interface OnClickListener {
		void onClick(DrawerView view);
	}
}
