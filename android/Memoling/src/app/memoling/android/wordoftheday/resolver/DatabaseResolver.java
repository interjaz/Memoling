package app.memoling.android.wordoftheday.resolver;

import android.content.Context;
import app.memoling.android.adapter.WordListAdapter;
import app.memoling.android.wordoftheday.provider.Provider;

public class DatabaseResolver extends ResolverBase {

	public DatabaseResolver(Context context, Provider provider) {
		super(context, provider);
	}

	@Override
	protected void fetchRaw() {

		WordListAdapter adapter = new WordListAdapter(getContext());
		String word = adapter.getRandom(m_provider.getBaseLanguage());
		
		onFetchRawComplete(word);
	}

	@Override
	protected Object getRootedData(String raw) {
		return raw;
	}

}
