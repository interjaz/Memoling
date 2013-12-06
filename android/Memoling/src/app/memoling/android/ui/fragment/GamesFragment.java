package app.memoling.android.ui.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import app.memoling.android.R;
import app.memoling.android.ui.ApplicationFragment;
import app.memoling.android.ui.ResourceManager;

public class GamesFragment extends ApplicationFragment {
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View contentView = super.onCreateView(inflater, container, savedInstanceState,
				inflater.inflate(R.layout.fragment_games, container, false));
		setTitle(getActivity().getString(R.string.games_title));

		ResourceManager resources = getResourceManager();
		Typeface thinFont = resources.getLightFont();
		
		
		Button hangman = (Button)contentView.findViewById(R.id.games_hangman);
		hangman.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startFragment(new GamesHangmanFragment());				
			}
		});
		
		Button crossword = (Button)contentView.findViewById(R.id.games_crossword);
		crossword.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startFragment(new GamesCrosswordFragment());	
			}
		});
		
		Button findword = (Button)contentView.findViewById(R.id.games_findword);
		findword.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startFragment(new GamesFindwordFragment());					
			}
		});
		
		resources.setFont(hangman, thinFont);
		resources.setFont(crossword, thinFont);
		resources.setFont(findword, thinFont);
		
		return contentView;
	}
	
	
	@Override
	protected void onDataBind(Bundle savedInstanceState) {


	}

}
