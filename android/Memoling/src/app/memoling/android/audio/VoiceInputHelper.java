package app.memoling.android.audio;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import app.memoling.android.entity.Language;
import app.memoling.android.translator.service.BingTranslator.BingLanguage;

public class VoiceInputHelper {

	public static Boolean m_isSupported;

	public static boolean isSupported(Context context) {

		if (m_isSupported != null) {
			m_isSupported.booleanValue();
		}

		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> activities = pm
				.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

		m_isSupported = Boolean.valueOf(!(activities.size() == 0));
		return m_isSupported.booleanValue();
	}

	public static Intent buildIntent(Language from) {

		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageToLocale(from));

		return intent;
	}

	public static String getData(Bundle bundle) {
		if (bundle.containsKey(RecognizerIntent.EXTRA_RESULTS)) {
			List<String> data = bundle.getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
			if (data.size() > 0) {
				return data.get(0);
			}
			return null;
		}

		return null;
	}

	public static String languageToLocale(Language language) {
		return BingLanguage.getBingLangauge(language).getBingCode();
	}

}
