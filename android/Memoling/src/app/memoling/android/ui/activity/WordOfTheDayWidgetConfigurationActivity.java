package app.memoling.android.ui.activity;

import java.util.List;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Spinner;
import app.memoling.android.R;
import app.memoling.android.adapter.MemoBaseAdapter;
import app.memoling.android.adapter.WordOfTheDayWidgetAdapter;
import app.memoling.android.entity.MemoBase;
import app.memoling.android.entity.WordOfTheDayWidget;
import app.memoling.android.ui.ResourceManager;
import app.memoling.android.ui.adapter.ModifiableComplexTextAdapter;
import app.memoling.android.ui.view.MemoBaseNameView;
import app.memoling.android.wordoftheday.WordOfTheDayWidgetProvider;

public class WordOfTheDayWidgetConfigurationActivity extends Activity {

	private int m_appWidgetId;
	private String m_memoBaseId;

	private MemoBaseAdapter m_memoBaseAdapter;
	private WordOfTheDayWidgetAdapter m_wordOfTheDayWidgetAdapter;

	private Spinner m_lstMemoBases;
	private Button m_btnSave;

	private ModifiableComplexTextAdapter<MemoBaseNameView> m_lstMemoBasesAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wordofthedaywidgetconfiguration);

		ResourceManager resources = new ResourceManager(this);
		Typeface thinFont = resources.getLightFont();

		m_memoBaseAdapter = new MemoBaseAdapter(this);
		m_wordOfTheDayWidgetAdapter = new WordOfTheDayWidgetAdapter(this);

		m_lstMemoBases = (Spinner) findViewById(R.id.wordofthedaywidgetconfiguration_lstMemoBases);

		m_lstMemoBasesAdapter = new ModifiableComplexTextAdapter<MemoBaseNameView>(this,
				R.layout.adapter_textdropdown_dark, new int[] { R.id.memo_lblLang }, new Typeface[] { thinFont });

		m_lstMemoBases.setAdapter(m_lstMemoBasesAdapter);

		m_btnSave = (Button) findViewById(R.id.wordofthedaywidgetconfiguration_btnSave);
		m_btnSave.setOnClickListener(new BtnSaveEventHandler());

		resources.setFont(R.id.textView1, thinFont);
		resources.setFont(R.id.wordofthedaywidgetconfiguration_btnSave, thinFont);
	}

	@Override
	protected void onResume() {
		super.onResume();

		Bundle bundle = getIntent().getExtras();
		m_appWidgetId = bundle.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		bindData();
	}

	private void bindData() {
		List<MemoBase> memoBases = m_memoBaseAdapter.getAll();
		m_lstMemoBasesAdapter.addAll(MemoBaseNameView.getAll(memoBases));
	}

	private void save() {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

		WordOfTheDayWidget widget = new WordOfTheDayWidget();
		widget.setMemoBaseId(m_memoBaseId);
		widget.setWidgetId(m_appWidgetId);

		m_wordOfTheDayWidgetAdapter.add(widget);

		WordOfTheDayWidgetProvider.update(this, appWidgetManager, new int[] { m_appWidgetId });

		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, m_appWidgetId);
		setResult(RESULT_OK, resultValue);
		finish();
	}

	private class BtnSaveEventHandler implements OnClickListener {

		@Override
		public void onClick(View v) {
			m_memoBaseId = m_lstMemoBasesAdapter.getItem(m_lstMemoBases.getSelectedItemPosition()).getMemoBase()
					.getMemoBaseId();
			save();
		}

	}
}
