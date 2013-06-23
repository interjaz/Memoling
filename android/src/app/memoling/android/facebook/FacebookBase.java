package app.memoling.android.facebook;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import app.memoling.android.R;
import app.memoling.android.ui.activity.FbLoginActivity;

public abstract class FacebookBase {
	
	protected void login(Activity activity, IFacebookUserFound onFacebookUserFound) {
		
		FacebookUser user = FacebookUser.read(activity);
		if(user != null) {
			onFacebookUserFound.onFacebookUserFound(user);
			return;
		}
		
		FacebookUser fbUser = FacebookUser.read(activity);
		if (fbUser == null) {
			Intent fbLoginIntent = new Intent(activity, FbLoginActivity.class);
			activity.startActivityForResult(fbLoginIntent, FbLoginActivity.CredentialsRequest);
		}
	}

	protected void loginOnActivityResult(final Context context, final IFacebookUserFound onFacebookUserFound, int requestCode, int resultCode, Intent data) {
		
		if(resultCode == Activity.RESULT_OK) {
			if(requestCode == FbLoginActivity.CredentialsRequest) {
					FacebookMe fbMe = new FacebookMe();
					fbMe.getUser(data.getStringExtra(FbLoginActivity.AccessToken), 
							new IFacebookUserFound() {
						@Override
						public void onFacebookUserFound(FacebookUser user) {
							if(context != null) {
								FacebookUser.save(user, context);
								onFacebookUserFound.onFacebookUserFound(user);
							}							
						}		
					});
			}
		} else {
			if(requestCode == FbLoginActivity.CredentialsRequest) {
				Toast.makeText(context, R.string.memobaselist_fbFailed, Toast.LENGTH_LONG).show();
				onFacebookUserFound.onFacebookUserFound(null);
			}
		}
	}	

}
