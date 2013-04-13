package com.interjaz.ui.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

public abstract class ModifiableAdapter<T> extends BaseAdapter implements
		Filterable {

	private Context m_context;
	private Filter m_filter;
	
	protected Object m_lock;
	protected ArrayList<T> m_data;
	protected int m_resourceId;
	protected LayoutInflater m_inflater;

	public ModifiableAdapter(Context context, int resourceId) {
		this(context, resourceId, new ArrayList<T>());
	}

	public ModifiableAdapter(Context context, int resourceId, ArrayList<T> data) {
		m_context = context;
		m_data = data;
		m_resourceId = resourceId;
		m_filter = new ArrayFilter();
		m_inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		m_lock = new Object();
	}

	@Override
	public int getCount() {
		synchronized (m_lock) {
			return m_data.size();
		}
	}

	@Override
	public T getItem(int position) {
		synchronized (m_lock) {
			return m_data.get(position);
		}
	}

	@Override
	public long getItemId(int position) {
		synchronized (m_lock) {
			return m_data.get(position).hashCode();
		}
	}

	@Override
	public Filter getFilter() {
		return m_filter;
	}

	public Context getContext() {
		return m_context;
	}

	public int getPosition(T object) {
		synchronized (m_lock) {
			return m_data.indexOf(object);
		}
	}

	public void add(T object) {
		synchronized (m_lock) {
			m_data.add(object);
		}
		notifyDataSetChanged();
	}

	public void addAll(ArrayList<T> objects) {
		synchronized (m_lock) {
			for (T object : objects) {
				m_data.add(object);
			}
		}
		notifyDataSetChanged();
	}

	public void insert(T object, int position) {
		synchronized (m_lock) {
			m_data.add(position, object);
		}
		notifyDataSetChanged();
	}

	public T remove(int position) {
		T object = null;
		synchronized (m_lock) {
			object = m_data.remove(position);
		}
		notifyDataSetChanged();

		return object;
	}

	public T remove(T object) {
		return remove(getPosition(object));
	}

	public void clear() {
		synchronized (m_lock) {
			m_data.clear();
		}
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getViewDefinition(position, convertView, parent);
	}

	protected abstract View getViewDefinition(int position,
			View convertView, ViewGroup parent);
	
	/**
	 * <p>
	 * An array filter constrains the content of the array adapter with a
	 * prefix. Each item that does not start with the supplied prefix is removed
	 * from the list.
	 * </p>
	 */
	private class ArrayFilter extends Filter {

		@SuppressLint("DefaultLocale")
		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			FilterResults results = new FilterResults();

			if (prefix == null || prefix.length() == 0) {
				synchronized (m_lock) {
					results.values = m_data;
					results.count = m_data.size();
				}
			} else {
				String prefixString = prefix.toString().toLowerCase();

				final ArrayList<T> values = m_data;
				final int count = values.size();

				final ArrayList<T> newValues = new ArrayList<T>(count);

				for (int i = 0; i < count; i++) {
					final T value = values.get(i);
					final String valueText = value.toString().toLowerCase();

					// First match against the whole, non-splitted value
					if (valueText.startsWith(prefixString)) {
						newValues.add(value);
					} else {
						final String[] words = valueText.split(" ");
						final int wordCount = words.length;

						for (int k = 0; k < wordCount; k++) {
							if (words[k].startsWith(prefixString)) {
								newValues.add(value);
								break;
							}
						}
					}
				}

				results.values = newValues;
				results.count = newValues.size();
			}
			
			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			
			synchronized (m_lock) {
				if(results.values == null) {
					m_data.clear();
				} else {
					m_data = (ArrayList<T>) results.values;
				}
			}

			if (ModifiableAdapter.this.getCount() > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}
	}
}
