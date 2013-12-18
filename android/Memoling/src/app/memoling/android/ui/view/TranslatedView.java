package app.memoling.android.ui.view;

import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;
import app.memoling.android.R;
import app.memoling.android.entity.Word;
import app.memoling.android.helper.Helper;
import app.memoling.android.ui.ResourceManager;
import app.memoling.android.ui.adapter.IInject;

public class TranslatedView implements IInject {

	private Word m_from;
	private Word m_to;
	private String m_source;

	public static TranslatedView PendingView(Word word, String pendingText) {
		return new TranslatedView(word, new Word(), pendingText);
	}

	public TranslatedView(Word from, Word to, String source) {
		m_from = from;
		m_to = to;
		m_source = source;
	}

	public Word from() {
		return m_from;
	}

	public Word to() {
		return m_to;
	}

	public String source() {
		return m_source;
	}

	public String get(int index) {
		switch (index) {
		case 0:
			return Helper.coalesce(m_from.getWord(), "");
		case 1:
			return Helper.coalesce(m_to.getWord(), "");
		case 2:
			return Helper.coalesce(m_source, "");
		case 3:
		default:
			String defA = m_from.getDescription();
			String defB = m_to.getDescription();
			if (defA != null && defA != "") {
				return defA;
			}
			if (defB != null && defB != "") {
				return defB;
			}

			return "-";
		}
	}

	@Override
	public void injcet(View view, ResourceManager resources) {

		ViewHolder holder = (ViewHolder) view.getTag();
		if (view.getTag() == null) {
			holder = new ViewHolder(view, resources);
		}

		String def = null;

		String defA = m_from.getDescription();
		String defB = m_to.getDescription();
		if (defA != null && defA != "") {
			def = defA;
		}
		if (defB != null && defB != "") {
			def = defB;
		}

		holder.m_original.setText(Helper.coalesce(m_from.getWord(), ""));
		holder.m_translate.setText(Helper.coalesce(m_to.getWord(), ""));
		holder.m_source.setText(Helper.coalesce(m_source, ""));

		if (def != null && !def.equals("")) {
			holder.m_definition.setVisibility(View.VISIBLE);
			holder.m_definition.setText(def);
		} else {
			holder.m_definition.setVisibility(View.GONE);
		}
		
	}

	private static class ViewHolder {

		public TextView m_original;
		public TextView m_translate;
		public TextView m_source;
		public TextView m_definition;

		public ViewHolder(View view, ResourceManager resources) {

			m_original = (TextView) view.findViewById(R.id.memolist_suggestion_txtWord);
			m_translate = (TextView) view.findViewById(R.id.memolist_suggestion_txtTranslation);
			m_source = (TextView) view.findViewById(R.id.memolist_suggestion_txtSource);
			m_definition = (TextView) view.findViewById(R.id.memolist_suggestion_txtDefinition);

			Typeface thinFont = resources.getLightFont();
			Typeface blackFont = resources.getBlackFont();

			resources.setFont(m_original, blackFont);
			resources.setFont(m_translate, thinFont);
			resources.setFont(m_source, blackFont);
			resources.setFont(m_definition, thinFont);

			view.setTag(this);
		}

	}

}
