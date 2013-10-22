package app.memoling.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import app.memoling.android.facebook.FacebookWrapper;
import app.memoling.android.facebook.FacebookWrapper.IFacebookGetUserComplete;
import app.memoling.android.facebook.FacebookWrapper.IFacebookGetFriendsComplete;
import app.memoling.android.facebook.FacebookWrapper.IFacebookSelectFriendDialogComplete;

public abstract class FacebookFragment extends ApplicationFragment {

	private FacebookWrapper m_facebookWrapper;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, View contentView) {
		View parentView = super.onCreateView(inflater, container, savedInstanceState, contentView);
		
		m_facebookWrapper = new FacebookWrapper(this);
		m_facebookWrapper.onCreateView(savedInstanceState);
		
		return parentView;
	}

	@Override
	public void onStart() {
		super.onStart();
		m_facebookWrapper.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
		m_facebookWrapper.onStop();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		m_facebookWrapper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		m_facebookWrapper.onSaveInstanceState(outState);
	}
	
	public void getFacebookUser(IFacebookGetUserComplete onFacebookGetUserComplete) {
		m_facebookWrapper.getUser(onFacebookGetUserComplete);
	}
	
	public void getFacebookFriends(IFacebookGetFriendsComplete onFacebookGetFriendsComplete) {
		m_facebookWrapper.getFriends(onFacebookGetFriendsComplete);
	}
	
	public void selectFacebookFriends(IFacebookSelectFriendDialogComplete onSelectFriendDialogComplete) {
		m_facebookWrapper.selectFriendDialog(onSelectFriendDialogComplete);
	}

}
