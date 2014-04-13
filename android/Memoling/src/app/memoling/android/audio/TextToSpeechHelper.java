package app.memoling.android.audio;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.widget.Toast;
import app.memoling.android.R;
import app.memoling.android.entity.Language;
import app.memoling.android.helper.AppLog;
import app.memoling.android.helper.Helper;

public class TextToSpeechHelper implements OnInitListener {

	private Context m_context;
	private Activity m_activity;
	private Handler m_handler;
	private TextToSpeech m_textToSpeech;

	private final static int TextToSpeechRequestCode = 4000;
	private static List<ReadItem> m_pendingQueue;
	private OnCompleteReading m_onCompleteHelper;
	private AtomicInteger m_initialized;
	private float m_speechRate = 1.0f;

	static {
		m_pendingQueue = new ArrayList<ReadItem>();
	}

	// If possible use the one with activity
	// This one does not check whether TTS Data is missing or not
	// - it will just fail (quietly) when data is not present
	public TextToSpeechHelper(Context context) {
		m_context = context;
		m_initialized = new AtomicInteger(0);
		m_handler = new Handler();
	}

	// Use this one if you want to check for TTS Missing Data
	public TextToSpeechHelper(Activity activity) {
		this((Context) activity);
		m_activity = activity;
	}

	public void readText(String text, Language language) {
		readText(text, language, null);
	}

	public String readText(String text, Language language, ITextToSpeechUtterance onComplete) {
		boolean read = false;
		ReadItem item;
		synchronized (m_pendingQueue) {
			read = m_pendingQueue.size() == 0;
			item = new ReadItem(text, language, onComplete);
			m_pendingQueue.add(item);
		}

		// Initialize with activity
		// Initialization will trigger read method
		if (m_activity != null && m_initialized.getAndSet(1) == 0) {
			Intent checkIntent = new Intent();
			checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
			m_activity.startActivityForResult(checkIntent, TextToSpeechRequestCode);
			return item.getId();
		}

		// Initialize with context
		// Initialization will trigger read method
		if (m_activity == null && m_initialized.getAndSet(1) == 0) {
			initTextToSpeech();
			return item.getId();
		}

		if (read) {
			// Initialized and requires to trigger reading manually
			read();
		}

		return item.getId();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == TextToSpeechRequestCode) {
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				initTextToSpeech();
			} else {
				// missing data, install it
				Intent installIntent = new Intent();
				installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				m_activity.startActivity(installIntent);
			}
		}
	}

	@Override
	public void onInit(int status) {
		read();
	}

	public void onUtteranceCompleted(String utteranceId) {
		ReadItem item;
		synchronized (m_pendingQueue) {
			if (m_pendingQueue.size() == 0) {
				AppLog.e("TextToSpeech", "Empty Queue (?)");
				return;
			}

			item = m_pendingQueue.remove(0);
		}

		if (item.getCallback() != null) {
			item.getCallback().onUtteranceCompleted(utteranceId);
		}
		
		read();
	}

	public void shutdown() {
		m_pendingQueue.clear();
		
		if (m_textToSpeech != null) {
			m_textToSpeech.shutdown();
		}
	}

	public void setSpeechRate(float speechRate) {
		m_speechRate = speechRate;

		if (m_textToSpeech != null) {
			m_textToSpeech.setSpeechRate(speechRate);
		}
	}

	private void initTextToSpeech() {
		// success, create the TTS instance
		m_textToSpeech = new TextToSpeech(m_context, this);
		m_onCompleteHelper = new OnCompleteReading(m_textToSpeech);
		m_textToSpeech.setSpeechRate(m_speechRate);
	}

	private void read() {
		ReadItem item;

		synchronized (m_pendingQueue) {
			if (m_pendingQueue.size() == 0) {
				return;
			}

			item = m_pendingQueue.get(0);
		}

		Locale locale = item.getLanguage().toLocale(m_context);
		int localeSupport = m_textToSpeech.isLanguageAvailable(locale);

		if (localeSupport == TextToSpeech.LANG_NOT_SUPPORTED) {
			try {
				Toast.makeText(m_context, R.string.texttospeech_languageNotSupported, Toast.LENGTH_SHORT).show();
				onUtteranceCompleted(item.getId());
			} catch (Exception ex) {
				// This will happen if onUtteranceComplete call read and read onUtteranceComplete
				// Normally this should not happen, but can if user cancel a service running TTS
				AppLog.e("TextToSpeechHelper", ex.toString());
			}
			return;
		}

		if (localeSupport == TextToSpeech.LANG_MISSING_DATA) {
			Toast.makeText(m_context, R.string.texttospeech_missingData, Toast.LENGTH_SHORT).show();
		}

		if (Helper.getMusicVolume(m_context) == 0) {
			Toast.makeText(m_context, R.string.texttospeech_muted, Toast.LENGTH_SHORT).show();
		}

		m_textToSpeech.setLanguage(locale);

		HashMap<String, String> map = new HashMap<String, String>();
		map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, item.getId());
		m_textToSpeech.speak(item.getText(), TextToSpeech.QUEUE_FLUSH, map);

	}

	private static class ReadItem {
		private String m_id;
		private String m_text;
		private Language m_language;
		private WeakReference<ITextToSpeechUtterance> m_callback;

		public ReadItem(String text, Language language, ITextToSpeechUtterance callback) {
			m_id = UUID.randomUUID().toString();
			m_text = text;
			m_language = language;

			if (callback != null) {
				m_callback = new WeakReference<ITextToSpeechUtterance>(callback);
			}
		}

		public String getId() {
			return m_id;
		}

		public String getText() {
			return m_text;
		}

		public Language getLanguage() {
			return m_language;
		}

		public ITextToSpeechUtterance getCallback() {
			if (m_callback == null || m_callback.get() == null) {
				return null;
			}

			return m_callback.get();
		}
	}

	private class OnCompleteReading {

		@SuppressLint("NewApi")
		@SuppressWarnings("deprecation")
		public OnCompleteReading(TextToSpeech tts) {

			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {

				tts.setOnUtteranceCompletedListener(new android.speech.tts.TextToSpeech.OnUtteranceCompletedListener() {

					@Override
					public void onUtteranceCompleted(String utteranceId) {
						m_handler.post(createRunnable(utteranceId, false));
					}

				});

			} else {

				tts.setOnUtteranceProgressListener(new android.speech.tts.UtteranceProgressListener() {

					@Override
					public void onDone(String utteranceId) {
						m_handler.post(createRunnable(utteranceId, false));
					}

					@Override
					public void onError(String utteranceId) {
						m_handler.post(createRunnable(utteranceId, true));
					}

					@Override
					public void onStart(String utteranceId) {
					}

				});

			}
		}

		public Runnable createRunnable(final String utteranceId, final boolean error) {
			return new Runnable() {
				@Override
				public void run() {
					if (error) {
						Toast.makeText(m_context, R.string.texttospeech_error, Toast.LENGTH_SHORT).show();
					}
					TextToSpeechHelper.this.onUtteranceCompleted(utteranceId);
				}
			};
		}

	}

}
