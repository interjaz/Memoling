package app.memoling.android.translator.service;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.memoling.android.entity.Language;
import app.memoling.android.entity.Word;
import app.memoling.android.helper.AppLog;
import app.memoling.android.translator.ITranslateComplete;
import app.memoling.android.translator.TranslatorResult;
import app.memoling.android.webrequest.HttpGetRequestTask;
import app.memoling.android.webrequest.HttpPostRequestTask;
import app.memoling.android.webrequest.IHttpRequestTaskComplete;

public class BingTranslator implements IHttpRequestTaskComplete {

	private static String m_token;
	private static GregorianCalendar m_tokenCreation = new GregorianCalendar(1, 1, 1);
	private static long m_tokenValidityTime = 9 * 60 * 1000L;
	private static Boolean m_tokenObtaining = false;
	private final static String m_defaultEncode = "UTF-8";
	private final static int m_MaxTranslationsPerRequest = 10;

	private final static int m_minMatchDegree = 100;
	private final static int m_minRating = 1;

	private final static String m_bingTranslateUrl = "http://api.microsofttranslator.com/V2/Ajax.svc/GetTranslations";
	private final static URI m_bingTokenUri = URI.create("https://datamarket.accesscontrol.windows.net/v2/OAuth2-13/");

	private static String m_clientId = "TranslateMemo";
	private static String m_clientSecret = "87dfyqGFVxbTVyrqlo2YV+AF2An3gBIH2eOIgI8UPs4=";
	private static String m_scope = "http://api.microsofttranslator.com";
	private static String m_grantType = "client_credentials";
	private static String m_accessTokenName = "access_token";

	private final static String m_TranslatedText = "TranslatedText";
	private final static String m_Rating = "Rating";
	private final static String m_MatchDegree = "MatchDegree";
	private final static String m_Translations = "Translations";

	private Word m_word;
	private BingLanguage m_from;
	private BingLanguage m_to;
	private ITranslateComplete m_onTranslateComplete;
	private static int m_timeout = 3000;

	private static ArrayList<BingTranslator> m_pendingTranslations = new ArrayList<BingTranslator>();

	public BingTranslator(Word word, Language from, Language to, ITranslateComplete onTranslatorResult) {

		BingLanguage bFrom = BingLanguage.getBingLangauge(from);
		BingLanguage bTo = BingLanguage.getBingLangauge(to);

		if (bFrom == BingLanguage.Unsupported || bTo == BingLanguage.Unsupported) {
			return;
		}

		m_word = new Word(word.getWord().trim());
		m_from = bFrom;
		m_to = bTo;
		m_onTranslateComplete = onTranslatorResult;

		if (!isTokenValid()) {
			synchronized (m_tokenObtaining) {
				if (!isTokenValid()) {
					obtainToken();
					m_pendingTranslations.add(this);
					return;
				}
			}
		}

		translate();
	}

	private static boolean isTokenValid() {
		long diff;

		synchronized (m_tokenObtaining) {
			diff = new GregorianCalendar().getTimeInMillis() - m_tokenCreation.getTimeInMillis();
		}

		return diff < m_tokenValidityTime;
	}

	private static void obtainToken() {

		synchronized (m_tokenObtaining) {

			if (m_tokenObtaining) {
				return;
			}

			m_tokenObtaining = true;
		}

		new HttpPostRequestTask(m_bingTokenUri, new IHttpRequestTaskComplete() {
			@Override
			public void onHttpRequestTaskComplete(String response) {

				synchronized (m_tokenObtaining) {
					try {
						if (response != null) {
							String accessToken = null;

							try {
								JSONObject completeData = new JSONObject(response);
								accessToken = (String) completeData.get(m_accessTokenName);

								if (accessToken != null) {
									m_token = accessToken;
									m_tokenCreation = new GregorianCalendar();

									for (int i = m_pendingTranslations.size() - 1; i >= 0; i--) {
										BingTranslator pending = m_pendingTranslations.remove(i);
										pending.translate();
									}
								}

							} catch (Exception ex) {
								AppLog.e("BingTranslator", "obtainToken", ex);
							}
						}
					} finally {
						m_tokenObtaining = false;
					}
				}
			}

			@Override
			public void onHttpRequestTimeout(Exception ex) {
				AppLog.w("BingTranslator", "obtainToken - timeout", ex);
			}
		}, m_timeout).execute(new BasicNameValuePair("client_id", m_clientId), new BasicNameValuePair("client_secret",
				m_clientSecret), new BasicNameValuePair("scope", m_scope), new BasicNameValuePair("grant_type",
				m_grantType));
	}

	private void translate() {
		try {			
			StringBuilder url = new StringBuilder(m_bingTranslateUrl);
			url.append("?appId=" + URLEncoder.encode("Bearer " + m_token, m_defaultEncode));
			url.append("&text=" + URLEncoder.encode(m_word.getWord(), m_defaultEncode));
			url.append("&from=" + m_from.getBingCode());
			url.append("&to=" + m_to.getBingCode());
			url.append("&maxTranslations=" + m_MaxTranslationsPerRequest);

			URI uri = new URI(url.toString());
			if (uri != null) {
				new HttpGetRequestTask(uri, this, m_timeout).execute();
			}
		} catch (UnsupportedEncodingException ex) {
		} catch (URISyntaxException ex) {
			AppLog.e("BingTranslator", "translate", ex);
		}
	}

	@Override
	public void onHttpRequestTaskComplete(String response) {

		TranslatorResult result = new TranslatorResult(m_word, m_from.toLanguage(), m_to.toLanguage(),
				parseJson(response));

		m_onTranslateComplete.onTranslateComplete(result);
	}

	private static ArrayList<Word> parseJson(String response) {
		ArrayList<Word> words = new ArrayList<Word>();

		try {
			JSONArray translations = new JSONObject(response).getJSONArray(m_Translations);
			
			for (int i = 0; i < translations.length(); i++) {

				JSONObject translation = translations.getJSONObject(i);
				int matchDegree = translation.getInt(m_MatchDegree);
				int rating = translation.getInt(m_Rating);

				if (matchDegree >= m_minMatchDegree && rating >= m_minRating) {

					Word newWord = new Word(translation.getString(m_TranslatedText));

					// Ignore duplicates
					if (!words.contains(newWord)) {
						words.add(newWord);
					}
				}

			}

		} catch (JSONException ex) {
			AppLog.e("BingTranslator", "prepareJson", ex);
		}

		return words;
	}

	public enum BingLanguage {

		Unsupported("", ""),
		AR("Arabic", "ar"),
		BG("Bulgarian", "bg"),
		CA("Catalan", "ca"),
		ZH("Chinese", "zh-CHS", "zh"),
		ZHCHS("Chinese (Simplified)", "zh-CHS"),
		ZHCHT("Chinese (Traditional)", "zh-CHT"),
		CS("Czech", "cs"),
		DA("Danish", "da"),
		NL("Dutch", "nl"),
		EN("English", "en"),
		ET("Estonian", "et"),
		FA("Persian (Farsi)","fa"),
		FI("Finnish", "fi"),
		FR("French", "fr"),
		DE("German", "de"),
		EL("Greek", "el"),
		HT("Haitian Creole", "ht"),
		HE("Hebrew", "he"),
		HI("Hindi", "hi"),
		HU("Hungarian", "hu"),
		ID("Indonesian","id"),
		IT("Italian", "it"),
		JA("Japanese", "ja"),
		KO("Korean", "ko"),
		LV("Latvian", "lv"),
		LT("Lithuanian", "lt"),
		MWW("Hmong Daw", "mww"),
		NO("Norwegian", "no"),
		PL("Polish", "pl"),
		PT("Portuguese", "pt"),
		RO("Romanian", "ro"),
		RU("Russian", "ru"),
		SK("Slovak", "sk"),
		SL("Slovenian","sl"),
		ES("Spanish", "es", "spa"),
		SV("Swedish", "sv"),
		TH("Thai", "th"),
		TR("Turkish", "tr"),
		UK("Ukrainian","uk"),
		VI("Vietnamese", "vi");

		private String m_code;
		private String m_name;
		private String m_bingCode;

		private BingLanguage(String name, String code) {
			m_name = name;
			m_code = code;
			m_bingCode = code;
		}
		
		private BingLanguage(String name, String bingCode, String code) {
			m_name = name;
			m_code = code;
			m_bingCode = bingCode;
		}

		public String getCode() {
			return m_code;
		}
		
		public String getBingCode() {
			return m_bingCode;
		}

		public String getName() {
			return m_name;
		}

		public Language toLanguage() {
			
			String code = this.getCode().toUpperCase(Locale.US);
			for(Language language : Language.values()) {
				if(language.getCode().toUpperCase(Locale.US).equals(code)) {
					return language;
				}
			}

			return Language.Unsupported;
		}

		public static BingLanguage getBingLangauge(Language language) {

			String code = language.getCode().toUpperCase(Locale.US);
			for(BingLanguage bingLanguage : BingLanguage.values()) {
				if(bingLanguage.getCode().toUpperCase(Locale.US).equals(code)) {
					return bingLanguage;
				}
			}

			return  BingLanguage.Unsupported;
		}
	}

	@Override
	public void onHttpRequestTimeout(Exception ex) {
		AppLog.w("BingTranslator", "onHttpRequestTimeout", ex);
	}
}