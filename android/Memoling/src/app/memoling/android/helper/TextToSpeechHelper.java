package app.memoling.android.helper;

import java.util.Locale;

import android.content.Context;
import app.memoling.android.entity.Language;
import app.memoling.android.preference.Preferences;

public class TextToSpeechHelper {

	public static boolean isLanguageSupported(Language language) {
		int value = language.getValue();
		if (value != Language.EN.getValue() && value != Language.ES.getValue() && value != Language.IT.getValue()
				&& value != Language.FR.getValue() && value != Language.DE.getValue()) {
			return false;
		}
		return true;
	}

	public static Locale languageToLocale(Context context, Language language) {

		int value = language.getValue();
		
		if(value == Language.EN.getValue()) {
			return new Preferences(context).getEnglishAccent();
		}
		
		if(value == Language.ES.getValue()) {
			return new Locale("spa", "ESP");
		}
		
		if(value == Language.FR.getValue()) {
			return Locale.FRANCE;
		}
		
		if(value == Language.IT.getValue()) {
			return Locale.ITALY;
		}

		if(value == Language.DE.getValue()) {
			return Locale.GERMANY;
		}
		
		return null;
	}

}
