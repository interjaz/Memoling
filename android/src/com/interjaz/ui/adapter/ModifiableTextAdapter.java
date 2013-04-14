package com.interjaz.ui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ModifiableTextAdapter<T> extends ModifiableAdapter<T> {

	private int m_fieldId;
	
	public ModifiableTextAdapter(Context context, int resourceId) {
		super(context, resourceId);
	}
	
	public ModifiableTextAdapter(Context context, int resourceId, int fieldId) {
		super(context, resourceId);
		m_fieldId = fieldId;
	}
	
	public ModifiableTextAdapter(Context context, int resourceId, ArrayList<T> data) {
		super(context, resourceId, data);
	}
	
	public ModifiableTextAdapter(Context context, int resourceId, ArrayList<T> data, int fieldId) {
		super(context, resourceId, data);
		m_fieldId = fieldId;
	}

	@Override
	protected View getViewDefinition(int position, View convertView,
			ViewGroup parent) {
		View view;
        TextView text;

        if (convertView == null) {
            view = m_inflater.inflate(m_resourceId, parent, false);
        } else {
            view = convertView;
        }

        try {
            if (m_fieldId == 0) {
                //  If no custom field is assigned, assume the whole resource is a TextView
                text = (TextView) view;
            } else {
                //  Otherwise, find the TextView field within the layout
                text = (TextView) view.findViewById(m_fieldId);
            }
        } catch (ClassCastException e) {
            Log.e("ModifiableTextAdapter", "You must supply a resource ID for a TextView");
            throw new IllegalStateException(
                    "ModifiableTextAdapter requires the resource ID to be a TextView", e);
        }
        
        T object = getItem(position);
        if (object instanceof CharSequence) {
            text.setText((CharSequence)object);
        } else {
            text.setText(object.toString());
        }

        return view;
	}

}
