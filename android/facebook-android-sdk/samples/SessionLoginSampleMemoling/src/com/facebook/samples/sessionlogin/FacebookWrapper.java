package com.facebook.samples.sessionlogin;

import java.util.ArrayList;
import java.util.prefs.Preferences;

import org.json.JSONException;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

public class FacebookWrapper {

	private static FacebookUser m_user;

	private Session.StatusCallback m_statusCallback = new SessionStatusCallback();
	private Fragment m_fragment;
	private ArrayList<IPendingAction> m_pendingActions;

	public FacebookWrapper(Fragment fragment) {
		m_fragment = fragment;
		m_pendingActions = new ArrayList<IPendingAction>();
	}

	//
	// Interfaces
	//

	private static interface IPendingAction {
		void onPendingAction(Session session, SessionState state, Exception exception);
	}

	public static interface IFacebookLoginComplete {
		void onLoginComplete(boolean success);
	}

	public static interface IFacebookGetUserComplete {
		void onGetUserComplete(FacebookUser user);
	}

	//
	// Methods that need to be called in fragment
	//

	public void onCreateView(Bundle savedInstanceState) {

		Session session = Session.getActiveSession();
		if (session == null) {
			if (savedInstanceState != null) {
				session = Session.restoreSession(m_fragment.getActivity(), null, m_statusCallback, savedInstanceState);
			}
			if (session == null) {
				session = new Session.Builder(m_fragment.getActivity()).setApplicationId(Config.FacebookApplicationId)
						.build();
			}
			Session.setActiveSession(session);
			if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
				session.openForRead(new Session.OpenRequest(m_fragment).setCallback(m_statusCallback));
			}
		}

	}

	public void onStart() {
		Session.getActiveSession().addCallback(m_statusCallback);
	}

	public void onStop() {
		Session.getActiveSession().removeCallback(m_statusCallback);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Session.getActiveSession().onActivityResult(m_fragment.getActivity(), requestCode, resultCode, data);
	}

	public void onSaveInstanceState(Bundle outState) {
		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);
	}

	//
	// Actual actions
	//

	public void login(final IFacebookLoginComplete onFacebookLoginComplete) {
		Session session = Session.getActiveSession();

		m_pendingActions.add(new IPendingAction() {
			@Override
			public void onPendingAction(Session session, SessionState state, Exception exception) {
				if (exception != null && onFacebookLoginComplete != null) {
					onFacebookLoginComplete.onLoginComplete(false);
				}

				if (onFacebookLoginComplete != null) {
					if (session.isOpened()) {
						onFacebookLoginComplete.onLoginComplete(true);
					} else {
						onFacebookLoginComplete.onLoginComplete(false);
					}
				}
			}
		});

		if (!session.isOpened() && !session.isClosed()) {
			session.openForRead(new Session.OpenRequest(m_fragment).setCallback(m_statusCallback));
		} else {
			Session.openActiveSession(m_fragment.getActivity(), true, m_statusCallback);
		}
	}

	public void logout() {
		Session session = Session.getActiveSession();
		if (!session.isClosed()) {
			session.closeAndClearTokenInformation();
		}
	}

	public void getUser(final IFacebookGetUserComplete onFacebookGetUserComplete) {

		FacebookUser user = readUser();
		if (user != null) {
			if (onFacebookGetUserComplete != null) {
				onFacebookGetUserComplete.onGetUserComplete(user);
			}
			return;
		}

		login(new IFacebookLoginComplete() {

			@Override
			public void onLoginComplete(boolean success) {
				if (success == false) {
					if (onFacebookGetUserComplete != null) {
						onFacebookGetUserComplete.onGetUserComplete(null);
					}
					return;
				}

				final Session session = Session.getActiveSession();
				Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
					@Override
					public void onCompleted(GraphUser user, Response response) {
						if (response.getError() != null) {

						}

						if (session == Session.getActiveSession()) {
							if (user != null) {
								try {
									FacebookUser fbUser = new FacebookUser(user);
									saveUser(fbUser);

									if (onFacebookGetUserComplete != null) {
										onFacebookGetUserComplete.onGetUserComplete(fbUser);
									}
								} catch (JSONException ex) {
									AppLog.e("FacebookUser.deserialize", "JSON Exception", ex);
									if (onFacebookGetUserComplete != null) {
										onFacebookGetUserComplete.onGetUserComplete(null);
									}
								}
							}
						}
					}
				});
				request.executeAsync();
			}

		});
	}

	private class SessionStatusCallback implements Session.StatusCallback {

		@Override
		public void call(Session session, SessionState state, Exception exception) {
			if (exception != null) {
				AppLog.e("SessionStatusCallback.call", "Exception", exception);
			}

			if (state == SessionState.OPENED_TOKEN_UPDATED) {
				for (int i = 0; i < m_pendingActions.size(); i++) {
					try {
						IPendingAction action = m_pendingActions.get(i);
						action.onPendingAction(session, state, exception);
					} catch (Exception ex) {
						AppLog.e("SessionStatusCallback.call", "Exception on executing callback", ex);
					}
				}

				m_pendingActions.clear();
			}
		}

	}

	private FacebookUser readUser() {

		return m_user;
	}

	private void saveUser(FacebookUser user) {
		if (user == null) {
			return;
		}

		m_user = user;
	}
}
