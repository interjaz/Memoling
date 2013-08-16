package app.memoling.android.helper;

import java.util.ArrayList;

import android.content.Context;
import android.util.Pair;
import app.memoling.android.adapter.MemoSentenceAdapter;
import app.memoling.android.entity.Language;
import app.memoling.android.entity.Memo;
import app.memoling.android.entity.MemoSentence;
import app.memoling.android.thread.WorkerThread;
import app.memoling.android.webservice.WsSentences;
import app.memoling.android.webservice.WsSentences.IGetComplete;

public class SentenceProvider {

	private static int m_sentenceCacheSize = 20;
	private static CacheHelper<String, ArrayList<MemoSentence>> m_sentenceListCache = new CacheHelper<String, ArrayList<MemoSentence>>(
			m_sentenceCacheSize);

	public static void getSentences(Context context, String word, final String memoId, Language from, Language to,
			final IGetComplete onComplete) {

		final String cacheKey = word + from.getCode() + to.getCode();

		// Check if already in the cache
		if (m_sentenceListCache.containsKey(cacheKey)) {
			ArrayList<MemoSentence> sentences = m_sentenceListCache.get(cacheKey);
			if (sentences.size() == 0) {
				onComplete.getComplete(null);
			} else {
				onComplete.getComplete(sentences);
			}
			return;
		}

		// Check if there is a word in the db
		final MemoSentenceAdapter sentenceAdapter = new MemoSentenceAdapter(context);
		ArrayList<MemoSentence> sentences = sentenceAdapter.getMemoSentences(memoId, from, to);

		if (sentences.size() != 0) {
			onComplete.getComplete(sentences);
			return;
		}

		// Otherwise download one
		WsSentences ws = new WsSentences();
		ws.get(word, from, to, new IGetComplete() {

			@Override
			public void getComplete(ArrayList<MemoSentence> memoSentences) {
				if (memoSentences != null && memoSentences.size() > 0) {
					for (MemoSentence sentence : memoSentences) {
						sentence.setMemoId(memoId);
					}

					sentenceAdapter.addAll(memoSentences);
					m_sentenceListCache.put(cacheKey, memoSentences);
				} else {
					// I need to think what to do at this case, for now let
					// application allow to try it again in next call
					m_sentenceListCache.put(cacheKey, new ArrayList<MemoSentence>());
				}

				onComplete.getComplete(memoSentences);
			}

		});
	}

	public static interface IGetManyComplete {
		void onComplete(ArrayList<Pair<Memo, ArrayList<MemoSentence>>> result);
	}

	private static class ManyState {
		public ArrayList<Pair<Memo, ArrayList<MemoSentence>>> received;
		public final int sent;
		public boolean completed = false;

		public ManyState(ArrayList<Memo> memos) {
			// Get unique only
			ArrayList<String> unique = new ArrayList<String>();
			for (Memo memo : memos) {
				if (!unique.contains(memo.getMemoId())) {
					unique.add(memo.getMemoId());
				}
			}
			sent = unique.size();
			received = new ArrayList<Pair<Memo, ArrayList<MemoSentence>>>();
		}
	}

	public static void getSentences(final Context context, ArrayList<Memo> memos, final IGetManyComplete onComplete) {

		final ManyState state = new ManyState(memos);

		for (Memo memo : memos) {
			new WorkerThread<Memo, Void, Void>() {

				@Override
				protected Void doInBackground(Memo... params) {

					final Memo m = params[0];

					getSentences(context, m.getWordA().getWord(), m.getMemoId(), m.getWordA().getLanguage(), m
							.getWordB().getLanguage(), new IGetComplete() {

						@Override
						public void getComplete(ArrayList<MemoSentence> memoSentences) {
							state.received.add(new Pair<Memo, ArrayList<MemoSentence>>(m, memoSentences));

							int size = state.received.size();

							if (!state.completed) {
								if (size == state.sent) {
									state.completed = true;
									onComplete.onComplete(state.received);
								}
							}
						}
					});

					return null;
				}

			}.execute(memo);
		}
	}
}
