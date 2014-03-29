package app.memoling.android.wordoftheday.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.w3c.dom.Element;

import app.memoling.android.entity.Language;
import app.memoling.android.wordoftheday.provider.Provider.PostProcessor;
import app.memoling.android.wordoftheday.provider.Provider.ResourceType;
import app.memoling.android.wordoftheday.provider.Provider.WordWithDescription;

public class ProviderAdapter {

	private static ArrayList<Provider> m_providers;

	static {

		m_providers = new ArrayList<Provider>();
		int providerId = 0;

		// Merriam-Webster
		m_providers.add(new Provider(providerId++, Language.EN, "Merriam-Webster", ResourceType.RSS,
				"http://www.merriam-webster.com/word/index.xml", "channel/item/description", new PostProcessor() {
					@Override
					public WordWithDescription process(Object raw) {
						if (raw == null) {
							return null;
						}

						String strRaw = ((Element) raw).getTextContent();

						// Look for second strong
						String[] rawParts = splitByHtmlTag(strRaw, "p");
						if (rawParts.length >= 2) {
							String word;
							String desc;

							String[] main = splitByHtmlTag(rawParts[3], "strong");
							word = stripHtml(main[1]).trim();
							desc = stripHtml(main[2]).trim();

							if (rawParts.length > 5) {
								String[] sub = splitByHtmlTag(rawParts[5], "strong");
								desc += "\n" + stripHtml(sub[1]).trim() + "\n" + stripHtml(sub[2]).trim();
							}

							if (rawParts.length > 7) {
								String[] sub = splitByHtmlTag(rawParts[7], "strong");
								desc += "\n" + stripHtml(sub[1]).trim() + "\n" + stripHtml(sub[2]).trim();
							}

							return new WordWithDescription(word, desc);
						}

						return null;
					}
				}));

		// Dictionary.com
		m_providers.add(new Provider(providerId++, Language.EN, "Dictionary.com", ResourceType.RSS,
				"http://dictionary.reference.com/wordoftheday/wotd.rss", "channel/item/description",
				new PostProcessor() {
					@Override
					public WordWithDescription process(Object raw) {
						if (raw == null) {
							return null;
						}

						String strRaw = ((Element) raw).getTextContent();

						// Look for second strong
						String[] rawParts = strRaw.split(":");
						if (rawParts.length >= 1) {
							String word;
							String desc;

							word = stripHtml(rawParts[0]).trim();
							desc = stripHtml(rawParts[1]).trim();

							return new WordWithDescription(word, desc);
						}

						return null;
					}
				}));

		// diccionariolibre
		m_providers.add(new Provider(providerId++, Language.ES, "Diccionario Libre", ResourceType.RSS,
				"http://diccionariolibre.com/daily.rss", "channel/item", new PostProcessor() {
					@Override
					public WordWithDescription process(Object raw) {
						if (raw == null) {
							return null;
						}

						Element elemRaw = (Element) raw;

						try {
							Element xTitle = (Element) elemRaw.getElementsByTagName("title").item(0);
							Element xDesc = (Element) elemRaw.getElementsByTagName("description").item(0);

							String title = xTitle.getTextContent().split(" - ")[1];
							String word = stripHtml(title).trim();
							String desc = stripHtml(xDesc.getTextContent()).trim();

							return new WordWithDescription(word, desc);

						} catch (Exception ex) {
							return null;
						}
					}
				}));

		// Una parola al giorno
		m_providers.add(new Provider(providerId++, Language.IT, "Una parola al giorno", ResourceType.RSS,
				"http://feeds.feedburner.com/unaparolaalgiorno", "channel/item", new PostProcessor() {
					@Override
					public WordWithDescription process(Object raw) {
						if (raw == null) {
							return null;
						}

						Element elemRaw = (Element) raw;

						try {
							Element xTitle = (Element) elemRaw.getElementsByTagName("title").item(0);
							Element xDesc = (Element) elemRaw.getElementsByTagName("description").item(0);

							String title = xTitle.getTextContent();
							String word = stripHtml(title).trim();
							String desc = stripHtml(xDesc.getTextContent()).trim();

							return new WordWithDescription(word, desc);

						} catch (Exception ex) {
							return null;
						}
					}
				}));

		m_providers.add(new Provider(providerId++, Language.FR, "Le mot du jour", ResourceType.RSS,
				"http://feeds2.feedburner.com/lemotdujour", "channel/item", new PostProcessor() {
					@Override
					public WordWithDescription process(Object raw) {
						if (raw == null) {
							return null;
						}

						Element elemRaw = (Element) raw;

						try {
							Element xTitle = (Element) elemRaw.getElementsByTagName("title").item(0);
							Element xDesc = (Element) elemRaw.getElementsByTagName("description").item(0);

							String title = xTitle.getTextContent();
							String word = stripHtml(title).trim();
							String desc = stripHtml(xDesc.getTextContent()).trim();

							return new WordWithDescription(word, desc);

						} catch (Exception ex) {
							return null;
						}
					}
				}));

		m_providers.add(new Provider(providerId++, Language.DE, "Deutsch Perfekt", ResourceType.RSS,
				"http://www.deutsch-perfekt.com/audio/wort-des-tages/archiv/feed", "channel/item", new PostProcessor() {
					@Override
					public WordWithDescription process(Object raw) {
						if (raw == null) {
							return null;
						}

						Element elemRaw = (Element) raw;

						try {
							Element xTitle = (Element) elemRaw.getElementsByTagName("title").item(0);
							Element xDesc = (Element) elemRaw.getElementsByTagName("description").item(0);

							String title = xTitle.getTextContent();
							String word = stripHtml(title).trim();
							String desc = stripHtml(xDesc.getTextContent()).trim();

							return new WordWithDescription(word, desc);

						} catch (Exception ex) {
							return null;
						}
					}
				}));

		// Słowo dnia
		m_providers.add(new Provider(providerId++, Language.PL, "Słow dnia", ResourceType.RSS,
				"http://slowodnia.wordpress.com/feed/", "channel/item", new PostProcessor() {
					@Override
					public WordWithDescription process(Object raw) {
						if (raw == null) {
							return null;
						}

						Element elemRaw = (Element) raw;

						try {
							Element xTitle = (Element) elemRaw.getElementsByTagName("title").item(0);
							Element xDesc = (Element) elemRaw.getElementsByTagName("description").item(0);

							String title = xTitle.getTextContent();
							String word = stripHtml(title).trim();
							String desc = stripHtml(xDesc.getTextContent()).trim();

							return new WordWithDescription(word, desc);

						} catch (Exception ex) {
							return null;
						}
					}
				}));
		
		
		PostProcessor memolingPostProcessor = new PostProcessor() {
			@Override
			public WordWithDescription process(Object raw) {
				if (raw == null) {
					return null;
				}

				return new WordWithDescription((String)raw, null);
			}
		};
		
		
		// Memoling
		m_providers.add(new Provider(providerId++, Language.PL, "Memoling", ResourceType.DB,
				null, null, memolingPostProcessor));
		
		// Memoling
		m_providers.add(new Provider(providerId++, Language.RU, "Memoling", ResourceType.DB,
				null, null, memolingPostProcessor));
		
		// Memoling
		m_providers.add(new Provider(providerId++, Language.FR, "Memoling", ResourceType.DB,
				null, null, memolingPostProcessor));
		
		// Memoling
		m_providers.add(new Provider(providerId++, Language.IT, "Memoling", ResourceType.DB,
				null, null, memolingPostProcessor));
		
		// Memoling
		m_providers.add(new Provider(providerId++, Language.ES, "Memoling", ResourceType.DB,
				null, null, memolingPostProcessor));
		
		// Memoling
		m_providers.add(new Provider(providerId++, Language.EN, "Memoling", ResourceType.DB,
				null, null, memolingPostProcessor));
		
		// Memoling
		m_providers.add(new Provider(providerId++, Language.DE, "Memoling", ResourceType.DB,
				null, null, memolingPostProcessor));

	}

	private static String[] splitByHtmlTag(String data, String tag) {
		return data.split("(?i)[<][/]?" + tag + "[>](?-i)");
	}

	private static String stripHtml(String data) {
		return android.text.Html.fromHtml(data).toString();
	}

	private Provider copy(Provider word) {
		return new Provider(word.getId(), word.getBaseLanguage(), word.getOwner(), word.getResourceType(),
				word.getUri(), word.getRoot(), word.getPostProcessor());
	}

	public List<Provider> getAll() {
		List<Provider> providers = new ArrayList<Provider>();
		for (Provider word : m_providers) {
			providers.add(copy(word));
		}
		
		Collections.sort(providers, new Comparator<Provider>() {
			@Override
			public int compare(Provider lhs, Provider rhs) {
				if(lhs.getBaseLanguage().getPosition() < rhs.getBaseLanguage().getPosition()) {
					return -1;
				} else if(lhs.getBaseLanguage().getPosition() == rhs.getBaseLanguage().getPosition()) {
					if(lhs.getId() < rhs.getId()) {
						return -1;
					} else {
						return 1;
					}
				} else {
					return 1;
				}
			}			
		});

		return providers;
	}

	public Provider getById(int id) {

		for (Provider provider : m_providers) {
			if (provider.getId() == id) {
				return copy(provider);
			}
		}

		return null;
	}

}