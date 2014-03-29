package app.memoling.android.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import app.memoling.android.R;
import app.memoling.android.ui.ResourceManager;
import app.memoling.android.ui.view.DrawerView;

public class DrawerAdapter extends BaseExpandableListAdapter {

	private ResourceManager m_resources;

	private Context m_context;
	private Filter m_filter;

	protected Object m_lock;
	protected List<DrawerView> m_groupData;
	protected List<ArrayList<DrawerView>> m_childData;
	protected LayoutInflater m_inflater;

	public final static int GroupPosition = -1;

	public DrawerAdapter(Context context, ResourceManager resources) {
		m_context = context;
		m_resources = resources;

		m_lock = new Object();
		m_inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		m_groupData = new ArrayList<DrawerView>();
		m_childData = new ArrayList<ArrayList<DrawerView>>();
		m_childData.add(new ArrayList<DrawerView>());
	}

	@Override
	public DrawerView getChild(int groupPosition, int childPosition) {
		synchronized (m_lock) {
			return m_childData.get(groupPosition).get(childPosition);
		}
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		synchronized (m_lock) {
			return m_childData.get(groupPosition).get(childPosition).hashCode();
		}
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		return getViewHolderView(groupPosition, childPosition, convertView, parent);
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		synchronized (m_lock) {
			return m_childData.get(groupPosition).size();
		}
	}

	@Override
	public DrawerView getGroup(int groupPosition) {
		synchronized (m_lock) {
			if (groupPosition >= m_groupData.size()) {
				return null;
			}

			return m_groupData.get(groupPosition);
		}
	}

	@Override
	public int getGroupCount() {
		synchronized (m_lock) {
			return m_groupData.size();
		}
	}

	@Override
	public long getGroupId(int groupPosition) {
		synchronized (m_lock) {
			return m_groupData.hashCode();
		}
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		return getViewHolderView(groupPosition, GroupPosition, convertView, parent);
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public int addGroup(DrawerView drawerView) {
		synchronized (m_lock) {
			m_groupData.add(drawerView);
			m_childData.add(new ArrayList<DrawerView>());
			notifyDataSetChanged();

			return m_groupData.size() - 1;
		}
	}

	public int addChild(int groupPosition, DrawerView drawerView) {
		synchronized (m_lock) {
			List<DrawerView> group = m_childData.get(groupPosition);
			group.add(drawerView);
			notifyDataSetChanged();

			return group.size() - 1;
		}
	}

	public DrawerView getItem(int groupPosition, int childPosition) {
		if (childPosition == GroupPosition) {
			return getGroup(groupPosition);
		} else {
			return getChild(groupPosition, childPosition);
		}
	}

	public void clear() {
		synchronized (m_lock) {
			m_groupData.clear();
			m_childData.clear();
			m_childData.add(new ArrayList<DrawerView>());
			notifyDataSetChanged();
		}
	}

	private View getViewHolderView(int groupPosition, int childPosition, View convertView, ViewGroup parent) {
		View view;
		boolean isGroup = childPosition == GroupPosition;

		if (convertView == null) {
			int layoutId = isGroup ? R.layout.adapter_drawer_group : R.layout.adapter_drawer_child;
			view = m_inflater.inflate(layoutId, parent, false);
		} else {
			view = convertView;
		}

		DrawerView item;
		if (isGroup) {
			item = getGroup(groupPosition);
		} else {
			item = getChild(groupPosition, childPosition);
		}

		ViewHolder holder = (ViewHolder) view.getTag();
		if (view.getTag() == null) {
			holder = new ViewHolder(view, m_resources);
		}

		holder.m_icon.setImageResource(item.getIcon());
		holder.m_text.setText(item.getText());

		if (isGroup) {
			if (this.getChildrenCount(groupPosition) > 0) {
				holder.m_expandIcon.setVisibility(View.VISIBLE);
			} else {
				holder.m_expandIcon.setVisibility(View.GONE);
			}
		}

		return view;
	}

	private static class ViewHolder {
		private ImageView m_icon;
		private TextView m_text;
		private ImageView m_expandIcon;

		public ViewHolder(View view, ResourceManager resources) {

			m_icon = (ImageView) view.findViewById(R.id.drawer_adapter_icon);
			m_text = (TextView) view.findViewById(R.id.drawer_adapter_text);
			resources.setFont(m_text, resources.getLightFont());

			m_expandIcon = (ImageView) view.findViewById(R.id.drawer_adapter_expand_icon);

			view.setTag(this);
		}
	}
}
