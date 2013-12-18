package app.memoling.android.wordoftheday.resolver;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.content.Context;
import app.memoling.android.helper.AppLog;
import app.memoling.android.wordoftheday.provider.Provider;

public class XmlResolver extends HttpResolver {

	public XmlResolver(Context context, Provider provider) {
		super(context, provider);
	}

	@Override
	public void onHttpRequestTimeout(Exception ex) {
		m_onFetchComplete.onFetchComplete(null);
	}

	@Override
	protected Object getRootedData(String raw) {

		try {

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(raw.getBytes("UTF-8")));
			Element element = doc.getDocumentElement();

			String[] path = m_provider.getRoot().split("/");

			for (String step : path) {
				element = (Element) element.getElementsByTagName(step).item(0);
			}

			
			return element;

		} catch (Exception ex) {
			AppLog.e("XmlProvider", "getRootedData", ex);
			return null;
		}
	}
}
