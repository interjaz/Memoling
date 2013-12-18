package app.memoling.android.test;

import android.app.Activity;
import android.os.Bundle;
import app.memoling.android.wiktionary.WiktionaryProviderService;

public class UiTestActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//WiktionaryProviderService.download(this, "wikien");
		
		//WikiSynonymAdapter adapter = new WikiSynonymAdapter(this);
		
		//ArrayList<WikiSynonym> synonyms = adapter.get("free", Language.EN);
		//synonyms.size();
	}
}
