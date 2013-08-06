package app.memoling.android.wordoftheday.resolver;

import android.content.Context;
import app.memoling.android.wordoftheday.provider.Provider;


public class ResolverFactory {

	public static ResolverBase getProvider(Provider provider, Context context) {
		switch (provider.getResourceType()) {
		case XML:
		case RSS:
		default:
			return new XmlResolver(provider);
		case DB:
			return new DatabaseResolver(provider, context);
		}
	}

}
