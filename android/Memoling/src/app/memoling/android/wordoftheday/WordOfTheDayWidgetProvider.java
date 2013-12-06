package app.memoling.android.wordoftheday;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import app.memoling.android.R;
import app.memoling.android.adapter.MemoAdapter;
import app.memoling.android.adapter.WordOfTheDayWidgetAdapter;
import app.memoling.android.entity.Memo;
import app.memoling.android.entity.WordOfTheDayWidget;
import app.memoling.android.ui.ApplicationActivity;
import app.memoling.android.ui.fragment.MemoListFragment;

public class WordOfTheDayWidgetProvider extends AppWidgetProvider {

	private static final int DescriptionLength = 100;

	public static void update(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		final int n = appWidgetIds.length;

		WordOfTheDayWidgetAdapter wordOfTheDayWidgetAdapter = new WordOfTheDayWidgetAdapter(context);
		MemoAdapter memoAdapter = new MemoAdapter(context);

		// Perform this loop procedure for each App Widget that belongs to this
		// provider
		for (int i = 0; i < n; i++) {
			int appWidgetId = appWidgetIds[i];

			WordOfTheDayWidget widget = wordOfTheDayWidgetAdapter.get(appWidgetId);
			if (widget == null) {
				continue;
			}

			Memo memo = memoAdapter.getRandom(widget.getMemoBaseId());

			String definition = "";
			if (memo.getWordA().getDescription() == "" && memo.getWordB().getDescription() != "") {
				definition = weakSubstring(memo.getWordB().getDescription(), DescriptionLength);
			} else if (memo.getWordA().getDescription() != "" && memo.getWordB().getDescription() == "") {
				definition = weakSubstring(memo.getWordA().getDescription(), DescriptionLength);
			} else if (memo.getWordA().getDescription() != "" && memo.getWordB().getDescription() != "") {
				definition = weakSubstring(memo.getWordA().getDescription(), DescriptionLength / 2) + "\n"
						+ weakSubstring(memo.getWordB().getDescription(), DescriptionLength / 2);
			}

			// Create an Intent to launch Memo Fragment
			Intent intent = new Intent(context, ApplicationActivity.class);
			intent.putExtra(MemoListFragment.MemoBaseId, memo.getMemoBaseId());
			intent.putExtra(MemoListFragment.MemoId, memo.getMemoId());
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

			// Get the layout for the App Widget and attach an on-click listener
			// to the button
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_wordoftheday);
			views.setOnClickPendingIntent(R.id.widget_wordoftheday_layContent, pendingIntent);

			views.setTextViewText(R.id.widget_wordoftheday_wordA, memo.getWordA().getWord());
			views.setTextViewText(R.id.widget_wordoftheday_wordB, memo.getWordB().getWord());
			views.setTextViewText(R.id.widget_wordoftheday_definition, definition);

			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}
	
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		update(context, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		final int n = appWidgetIds.length;

		WordOfTheDayWidgetAdapter wordOfTheDayWidgetAdapter = new WordOfTheDayWidgetAdapter(context);
		
		// Perform this loop procedure for each App Widget that belongs to this
		// provider
		for (int i = 0; i < n; i++) {
			int appWidgetId = appWidgetIds[i];
			wordOfTheDayWidgetAdapter.delete(appWidgetId);
		}
	}

	private static String weakSubstring(String str, int take) {
		if (str == null) {
			return "";
		}

		if (str.length() < take) {
			return str;
		}

		return str.substring(0, take);
	}

}