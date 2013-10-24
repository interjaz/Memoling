package app.memoling.android.helper;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Pair;
import android.widget.Toast;
import app.memoling.android.R;
import app.memoling.android.entity.Language;

public class TextToSpeechHelper implements OnInitListener {

	private Activity m_activity;
	private TextToSpeech m_textToSpeech;
	
	private final static int TextToSpeechRequestCode = 4000;
	private static ArrayList<Pair<String,Language>> m_queue;
	
	static {
		m_queue = new ArrayList<Pair<String,Language>>();
	}
	
	public TextToSpeechHelper(Activity activity) {
		m_activity = activity;
	}
	
	public void readText(String text, Language language) {
		synchronized(m_queue) {
			m_queue.add(new Pair<String,Language>(text,language));
		}
		
		Intent checkIntent = new Intent();
		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		m_activity.startActivityForResult(checkIntent, TextToSpeechRequestCode);
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == TextToSpeechRequestCode) {
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				// success, create the TTS instance
				m_textToSpeech = new TextToSpeech(m_activity,this);
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
		Pair<String, Language> pair;
		synchronized(m_queue) {
			pair = m_queue.get(0);
			m_queue.remove(0);
		}
		
		Locale locale = pair.second.toLocale(m_activity);
		int localeSupport = m_textToSpeech.isLanguageAvailable(locale);
		
		if(localeSupport == TextToSpeech.LANG_NOT_SUPPORTED) {
			Toast.makeText(m_activity, R.string.texttospeech_languageNotSupported, Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(localeSupport == TextToSpeech.LANG_MISSING_DATA) {
			Toast.makeText(m_activity, R.string.texttospeech_missingData, Toast.LENGTH_SHORT).show();
		}
		
		if(Helper.getMusicVolume(m_activity) == 0) {
			Toast.makeText(m_activity, R.string.texttospeech_muted, Toast.LENGTH_SHORT).show();
		}
		
		m_textToSpeech.setLanguage(locale);
		m_textToSpeech.speak(pair.first, TextToSpeech.QUEUE_FLUSH, null);		
	}
	
	public void shutdown() {
		if(m_textToSpeech != null) {
			m_textToSpeech.shutdown();
		}
	}

}
