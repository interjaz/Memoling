package app.memoling.android.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import app.memoling.android.R;
import app.memoling.android.audio.AudioReplayService;
import app.memoling.android.ui.ResourceManager;

public class AudioReplayActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audioreplay);

		ResourceManager resources = new ResourceManager(this);
		Typeface thinFont = resources.getLightFont();

		Button btnYes = (Button)findViewById(R.id.audioreplay_btnYes);
		Button btnNo = (Button)findViewById(R.id.audioreplay_btnNo);
		
		btnYes.setOnClickListener(new BtnYesEventHandler());
		btnNo.setOnClickListener(new BtnNoEventHandler());
		
		resources.setFont(R.id.textView1, thinFont);
		resources.setFont(R.id.audioreplay_btnYes, thinFont);
		resources.setFont(R.id.audioreplay_btnNo, thinFont);
	}

	private class BtnYesEventHandler implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent audioReplayIntent = new Intent(AudioReplayActivity.this, AudioReplayService.class);
			AudioReplayActivity.this.stopService(audioReplayIntent);
			finish();
		}

	}

	private class BtnNoEventHandler implements OnClickListener {

		@Override
		public void onClick(View v) {
			finish();
		}

	}
}
