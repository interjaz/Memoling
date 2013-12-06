package app.memoling.android.ui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import app.memoling.android.R;
import app.memoling.android.ui.ResourceManager;
import app.memoling.android.ui.view.DrawerView;

public class DrawerAdapter extends ModifiableAdapter<DrawerView> {

	private ResourceManager m_resources;
	
	public DrawerAdapter(Context context, int resourceId, ResourceManager resources) {
		this(context, resourceId, new ArrayList<DrawerView>());
		m_resources = resources;
	}
	
	public DrawerAdapter(Context context, int resourceId, ArrayList<DrawerView> data) {
		super(context, resourceId, data);
		m_resourceId = resourceId;
	}

	@Override
	protected View getViewDefinition(int position, View convertView, ViewGroup parent) {
		View view;

		if (convertView == null) {
			view = m_inflater.inflate(m_resourceId, parent, false);
		} else {
			view = convertView;
		}

		DrawerView item = getItem(position);
		
		ViewHolder holder = (ViewHolder) view.getTag();
		if(view.getTag() == null) {
			holder = new ViewHolder(view, m_resources);
		}
		
		holder.m_icon.setImageResource(item.getIcon());		
		holder.m_text.setText(item.getText());
		
		
		return view;
	}

	private static class ViewHolder {
		private ImageView m_icon;
		private TextView m_text;
		
		public ViewHolder(View view, ResourceManager resources) {

			m_icon = (ImageView)view.findViewById(R.id.drawer_adapter_icon);
			m_text = (TextView)view.findViewById(R.id.drawer_adapter_text);
			resources.setFont(m_text, resources.getLightFont());
			
			view.setTag(this);
		}
	}
	
}
