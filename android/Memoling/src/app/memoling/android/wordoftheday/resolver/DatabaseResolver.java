package app.memoling.android.wordoftheday.resolver;

import android.content.Context;
import app.memoling.android.adapter.WordListAdapter;
import app.memoling.android.wordoftheday.provider.Provider;

public class DatabaseResolver extends ResolverBase {

	private Context m_context;

	public DatabaseResolver(Provider provider, Context context) {
		super(provider);
		m_context = context;
	}

	@Override
	protected void fetchRaw() {

		WordListAdapter adapter = new WordListAdapter(m_context);
		String word = adapter.getRandom(m_provider.getBaseLanguage());
		
		onFetchRawComplete(word);
	}

	@Override
	protected Object getRootedData(String raw) {
		return raw;
	}

}
