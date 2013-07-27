package app.memoling.android.translator.service;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import app.memoling.android.entity.Language;
import app.memoling.android.entity.Word;
import app.memoling.android.helper.AppLog;
import app.memoling.android.translator.ITranslateComplete;
import app.memoling.android.translator.TranslatorResult;
import app.memoling.android.webrequest.HttpGetRequestTask;
import app.memoling.android.webrequest.IHttpRequestTaskComplete;

public class _GoogleTranslateTranslator implements IHttpRequestTaskComplete {

	private Word m_word;
	private Language m_from;
	private Language m_to;
	private ITranslateComplete m_onTranslateComplete;
	private static int m_timeout = 3000;

	public _GoogleTranslateTranslator(Word word, Language from, Language to,
			ITranslateComplete onTranslateComplete) {

		m_word = new Word(word.getWord().trim());
		m_from = from;
		m_to = to;
		m_onTranslateComplete = onTranslateComplete;

		try {
			StringBuilder url = new StringBuilder(
					"http://translate.google.com/translate_a/t?client=t&text=");
			url.append(URLEncoder.encode(m_word.getWord(), "UTF-8") + "&hl=en");
			url.append("&sl=" + LanguageString(m_from));
			url.append("&tl=" + LanguageString(m_to));
			url.append("&ie=UTF-8&oe=UTF-8&multires=1&ssel=0&tsel=0&sc=1");

			URI uri = new URI(url.toString());

			if (uri != null) {
				// Make async-callback
				new HttpGetRequestTask(uri, this, m_timeout).execute();
			}
		} catch (UnsupportedEncodingException ex) {
		} catch (URISyntaxException ex) {
		}
	}

	private static String LanguageString(Language lang) {
		switch (lang) {
		case EN:
			return "en";
		case PL:
			return "pl";
		case FI:
			return "fi";
		default:
			return "en";
		}
	}

	@Override
	public void onHttpRequestTaskComplete(String response) {
		// Process page result
		TranslatorResult result = new TranslatorResult(m_word, m_from, m_to,
				ParseJson(response));

		m_onTranslateComplete.onTranslateComplete(result);
	}

	private ArrayList<Word> ParseJson(String jsonString) {
		ArrayList<Word> words = new ArrayList<Word>();
		final int[] tranlstaionPosition = new int[] { 1, 0, 1 };

		try {
			JSONArray completeData = new JSONArray(jsonString);
			JSONArray wordsArray = completeData
					.getJSONArray(tranlstaionPosition[0])
					.getJSONArray(tranlstaionPosition[1])
					.getJSONArray(tranlstaionPosition[2]);

			for (int i = 0; i < wordsArray.length(); i++) {
				String entry = (String) wordsArray.get(i);
				words.add(new Word(entry));
			}

		} catch (JSONException ex) {
			AppLog.e("_GoogleTranslate", "ParseJson", ex);
		}

		return words;
	}

	@Override
	public void onHttpRequestTimeout(Exception ex) {
		AppLog.w("GoogleTranslator", "onHttpRequestTimeout", ex);
	}

}
