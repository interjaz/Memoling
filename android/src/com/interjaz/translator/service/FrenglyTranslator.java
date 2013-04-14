package com.interjaz.translator.service;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

import com.interjaz.Language;
import com.interjaz.entity.Word;
import com.interjaz.translator.ITranslateComplete;
import com.interjaz.translator.TranslatorResult;
import com.interjaz.webrequest.HttpGetRequestTask;
import com.interjaz.webrequest.IHttpRequestTaskComplete;

public class FrenglyTranslator implements IHttpRequestTaskComplete {

	private final static String m_baseUrl = "http://www.syslang.com/frengly/controller?action=translateREST";
	private final static String m_email = "interjaz@gmail.com";
	private final static String m_password = "password";
	private final static String m_tanslationTag = "translationFramed";

	private final static String m_defaultEncode = "UTF-8";

	private Language m_to;
	private Language m_from;
	private Word m_word;
	private ITranslateComplete m_onTranslatorResult;
	private static int m_timeout = 3000;

	public FrenglyTranslator(Word word, Language from, Language to,
			ITranslateComplete onTranslatorResult) {

		m_to = to;
		m_from = from;
		m_word = word;
		m_onTranslatorResult = onTranslatorResult;

		try {
			StringBuilder url = new StringBuilder(m_baseUrl);
			url.append("&src=" + from.getCode());
			url.append("&dest=" + to.getCode());
			url.append("&text="
					+ URLEncoder.encode(word.getWord(), m_defaultEncode));
			url.append("&email=" + URLEncoder.encode(m_email, m_defaultEncode));
			url.append("&password="
					+ URLEncoder.encode(m_password, m_defaultEncode));

			new HttpGetRequestTask(new URI(url.toString()), this, m_timeout).execute();

		} catch (UnsupportedEncodingException ex) {
		} catch (URISyntaxException ex) {
		}

	}

	@Override
	public void onHttpRequestTaskComplete(String response) {
		TranslatorResult result = new TranslatorResult(m_word, m_from, m_to,
				parseXml(response));
		m_onTranslatorResult.onTranslateComplete(result);
	}

	private static ArrayList<Word> parseXml(String xml) {
		ArrayList<Word> words = new ArrayList<Word>();

		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new StringReader(xml));
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

			while (parser.next() != XmlPullParser.END_DOCUMENT) {

				if (parser.getEventType() == XmlPullParser.START_TAG
						&& parser.getName().compareTo(m_tanslationTag) == 0) {

					parser.next();

					String translation = parser.getText();
					String[] translations = translation.split("\\|");

					for (String word : translations) {
						words.add(new Word(word));
					}

					break;
				}
			}

		} catch (XmlPullParserException ex) {
		} catch (IOException ex) {
		}

		return words;
	}

	@Override
	public void onHttpRequestTimeout(Exception ex) {
		// TODO Auto-generated method stub
		
	}
}
