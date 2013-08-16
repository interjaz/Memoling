package app.memoling.android.wordoftheday;

import app.memoling.android.wordoftheday.resolver.MemoOfTheDay;

public interface IFetchComplete {
	void onFetchComplete(MemoOfTheDay memo);
}
