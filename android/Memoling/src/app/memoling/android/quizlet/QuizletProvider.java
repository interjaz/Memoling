package app.memoling.android.quizlet;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import app.memoling.android.adapter.QuizletDefinitionAdapter;
import app.memoling.android.entity.QuizletDefinition;
import app.memoling.android.quizlet.Quizlet.IQuizletDefinitionSearchResult;
import app.memoling.android.quizlet.data.DefinitionSearchResult;

public class QuizletProvider {

	public static interface IQuizletGetDefinitions {
		void getQuizletDefinitions(ArrayList<QuizletDefinition> definitions);
	}

	public static void getDefinitions(Context context, final String word, final IQuizletGetDefinitions result) {

		if(word == null || word.equals("")) {
			result.getQuizletDefinitions(null);
			return;
		}
		
		// Check database
		final QuizletDefinitionAdapter adapter = new QuizletDefinitionAdapter(context);

		ArrayList<QuizletDefinition> definitions = null;
		definitions = adapter.get(word);
		if (definitions != null && definitions.size() != 0) {
			result.getQuizletDefinitions(definitions);
			return;
		}

		Quizlet.definitionSearch(word, 0, new IQuizletDefinitionSearchResult() {
			@Override
			public void definitionSearchResult(DefinitionSearchResult webResult) {

				if (webResult.getDefinitions() == null) {
					result.getQuizletDefinitions(null);
					return;
				}

				ArrayList<QuizletDefinition> definitions = new ArrayList<QuizletDefinition>();

				for (QuizletDefinition definition : webResult.getDefinitions()) {
					if (definition.getWord().toLowerCase(Locale.US).equals(word.toLowerCase(Locale.US))) {
						adapter.add(definition);
						definitions.add(definition);
					}
				}

				result.getQuizletDefinitions(definitions);
				return;
			}
		});
	}
}
