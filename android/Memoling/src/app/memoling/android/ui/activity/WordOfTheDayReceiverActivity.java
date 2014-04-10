package app.memoling.android.ui.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import app.memoling.android.R;
import app.memoling.android.adapter.MemoAdapter;
import app.memoling.android.adapter.SyncClientAdapter;
import app.memoling.android.audio.TextToSpeechHelper;
import app.memoling.android.helper.AppLog;
import app.memoling.android.ui.AdActivity;
import app.memoling.android.ui.ApplicationActivity;
import app.memoling.android.ui.ResourceManager;
import app.memoling.android.ui.fragment.MemoListFragment;
import app.memoling.android.wordoftheday.provider.Provider;
import app.memoling.android.wordoftheday.provider.ProviderAdapter;
import app.memoling.android.wordoftheday.resolver.MemoOfTheDay;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class WordOfTheDayReceiverActivity extends AdActivity {

	public final static String MemoOfTheDayObject = "MemoOfTheDayObject";
	public final static String NotificationId = "NotificationId";
	private final static int InvalidNotificationId = -1;

	private static ResourceManager m_resources;

	private MemoOfTheDay m_memo;

	private TextView m_txtSource;
	private TextView m_txtWordFrom;
	private TextView m_txtWordTo;
	private TextView m_txtDescriptionFrom;
	private TextView m_txtDescriptionTo;
	private Button m_btnSource;
	private ImageButton m_btnVoiceA;
	private ImageButton m_btnVoiceB;
	private TextToSpeechHelper m_textToSpeechHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wordofadayreceiver);
		onCreate_Ads();

		ResourceManager resources = new ResourceManager(this);
		m_resources = resources;

		Typeface thinFont = m_resources.getLightFont();

		m_txtSource = (TextView) findViewById(R.id.wordofadayreceiver_txtSource);
		m_txtWordFrom = (TextView) findViewById(R.id.wordofadayreceiver_txtWordFrom);
		m_txtWordTo = (TextView) findViewById(R.id.wordofadayreceiver_txtWordTo);
		m_txtDescriptionFrom = (TextView) findViewById(R.id.wordofadayreceiver_txtDescriptionFrom);
		m_txtDescriptionTo = (TextView) findViewById(R.id.wordofadayreceiver_txtDescriptionTo);
		m_btnSource = (Button) findViewById(R.id.wordofadayreceiver_btnSource);
		m_btnSource.setOnClickListener(new BtnSourceEventHandler());
		m_btnVoiceA = (ImageButton) findViewById(R.id.wordofadayreceiver_btnVoiceA);
		m_btnVoiceB = (ImageButton) findViewById(R.id.wordofadayreceiver_btnVoiceB);
		m_btnVoiceA.setOnClickListener(new BtnVoiceAEventHandler());
		m_btnVoiceB.setOnClickListener(new BtnVoiceBEventHandler());

		m_textToSpeechHelper = new TextToSpeechHelper(this);

		resources.setFont(R.id.wordofadayreceiver_btnSource, thinFont);
		resources.setFont(R.id.wordofadayreceiver_lblDescriptionFrom, thinFont);
		resources.setFont(R.id.wordofadayreceiver_lblDescriptionTo, thinFont);
		resources.setFont(R.id.wordofadayreceiver_lblSource, thinFont);
		resources.setFont(R.id.wordofadayreceiver_lblWordFrom, thinFont);
		resources.setFont(R.id.wordofadayreceiver_lblWordTo, thinFont);
		resources.setFont(R.id.wordofadayreceiver_txtDescriptionFrom, thinFont);
		resources.setFont(R.id.wordofadayreceiver_txtDescriptionTo, thinFont);
		resources.setFont(R.id.wordofadayreceiver_txtSource, thinFont);
		resources.setFont(R.id.wordofadayreceiver_txtWordFrom, thinFont);
		resources.setFont(R.id.wordofadayreceiver_txtWordTo, thinFont);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuItem save = menu.add(1, 0, Menu.NONE, getString(R.string.wordofthedayreceiver_save));
		save.setIcon(R.drawable.ic_save);
		save.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		MenuItem delete = menu.add(1, 1, Menu.NONE, getString(R.string.wordofthedayreceiver_delete));
		delete.setIcon(R.drawable.ic_delete);
		delete.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Intent intent = new Intent(this, ApplicationActivity.class);

		if (item.getItemId() == 0) {
			// Save
			saveMemo();
			intent.putExtra(MemoListFragment.MemoBaseId, m_memo.getMemoBaseId());
			intent.putExtra(MemoListFragment.MemoId, m_memo.getMemoId());

			Toast.makeText(this, getString(R.string.wordofthedayreceiver_saved), Toast.LENGTH_SHORT).show();
		} else if (item.getItemId() == 1) {
			// Delete

			Toast.makeText(this, getString(R.string.wordofthedayreceiver_deleted), Toast.LENGTH_SHORT).show();
		}

		// Start memoling
		startActivity(intent);
		finish();

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onStart() {
		super.onStart();

		Intent intent = getIntent();
		String strMemo = intent.getStringExtra(MemoOfTheDayObject);
		m_memo = new MemoOfTheDay();
		try {
			m_memo.deserialize(new JSONObject(strMemo));
		} catch (JSONException ex) {
			AppLog.e("WordOfTheDayReviewActivity", "onStart", ex);
		}

		int notificationId = intent.getIntExtra(NotificationId, InvalidNotificationId);
		if (notificationId != InvalidNotificationId) {
			((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(notificationId);
		}

		bindData();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		m_textToSpeechHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (m_textToSpeechHelper != null) {
			m_textToSpeechHelper.shutdown();
		}
	}

	private void bindData() {
		Provider word = new ProviderAdapter().getById(m_memo.getProviderId());

		m_txtSource.setText(word.getUri());
		m_txtWordFrom.setText(m_memo.getWordA().getWord());
		m_txtWordTo.setText(m_memo.getWordB().getWord());

		if (word.getUri() == null || word.getUri().equals("")) {
			m_btnSource.setEnabled(false);
		} else {
			m_btnSource.setEnabled(true);
		}

		String description;
		description = m_memo.getWordA().getDescription();
		if (description.equals("")) {
			findViewById(R.id.wordofadayreceiver_lblDescriptionFrom).setVisibility(View.GONE);
			m_txtDescriptionFrom.setVisibility(View.GONE);
		} else {
			m_txtDescriptionFrom.setText(description);
		}

		description = m_memo.getWordB().getDescription();
		if (description.equals("")) {
			findViewById(R.id.wordofadayreceiver_lblDescriptionTo).setVisibility(View.GONE);
			m_txtDescriptionTo.setVisibility(View.GONE);
		} else {
			m_txtDescriptionTo.setText(description);
		}
	}

	private class BtnSourceEventHandler implements OnClickListener {
		@Override
		public void onClick(View v) {
			String url = m_txtSource.getText().toString();
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url));
			startActivity(intent);
		}
	}

	private class BtnVoiceAEventHandler implements OnClickListener {

		@Override
		public void onClick(View v) {
			m_textToSpeechHelper.readText(m_memo.getWordA().getWord(), m_memo.getWordA().getLanguage());
		}

	}

	private class BtnVoiceBEventHandler implements OnClickListener {

		@Override
		public void onClick(View v) {
			m_textToSpeechHelper.readText(m_memo.getWordB().getWord(), m_memo.getWordB().getLanguage());
		}

	}

	private void saveMemo() {
		MemoAdapter adapter = new MemoAdapter(this);
		adapter.insert(m_memo, new SyncClientAdapter(this).getCurrentSyncClientId());
	}
}
