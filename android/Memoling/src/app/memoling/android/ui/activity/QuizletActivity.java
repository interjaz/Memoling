package app.memoling.android.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import app.memoling.android.R;

public class QuizletActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quizlet);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.quizlet, menu);
		return true;
	}

}
