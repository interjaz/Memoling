package app.memoling.android.facebook;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import app.memoling.android.Config;
import app.memoling.android.R;
import app.memoling.android.helper.AppLog;
import app.memoling.android.preference.Preferences;
import app.memoling.android.ui.adapter.ModifiableInjectableAdapter;
import app.memoling.android.ui.view.FacebookFriendView;
import app.memoling.android.webrequest.HttpGetRequestTask;
import app.memoling.android.webrequest.IHttpRequestTaskComplete;

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

	// %d is FacebookUserId
	private static String FacebookFeedUrl = "https://www.facebook.com/dialog/feed";
	private final static String FacebookSharePictureUri = "http://memoling.com/fbShareLogo.jpg";

	// %d is FacebookUserId %s is AccessToken
	private static String FacebookGraphUrl = "https://graph.facebook.com/";
	private static String FacebookFriendsSql = "/fql?q=SELECT%20uid,%20name,%20pic_square%20FROM%20user%20WHERE%20uid%20IN%20%28SELECT%20uid2%20FROM%20friend%20WHERE%20uid1%20=%20me%28%29%29&access_token=";

	private int m_graphTimeout = 6000;

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

	public static interface IFacebookGetFriendsComplete {
		void onGetFriendsCompete(FacebookUser user, ArrayList<FacebookFriend> friends);
	}

	public static interface IFacebookSelectFriendDialogComplete {
		void onSelectFriendDialogComplete(FacebookUser user, FacebookFriend friend);
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
				session = new Session(m_fragment.getActivity());
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

	public static void logout() {
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

	public void getFriends(final IFacebookGetFriendsComplete onFacebookGetFriendsComplete) {

		getUser(new IFacebookGetUserComplete() {
			@Override
			public void onGetUserComplete(final FacebookUser user) {

				// We are not using Request.newMyFriends... since it does not
				// retrieve images

				URI uri = null;

				try {
					String url = FacebookGraphUrl + user.getId() + FacebookFriendsSql
							+ Session.getActiveSession().getAccessToken();
					uri = new URI(url);
				} catch (URISyntaxException ex) {
					AppLog.e("FacebookGraph.getFriends", "new URI exception", ex);
					onFacebookGetFriendsComplete.onGetFriendsCompete(null, null);
					return;
				}

				new HttpGetRequestTask(uri, new IHttpRequestTaskComplete() {

					@Override
					public void onHttpRequestTaskComplete(String response) {

						try {
							JSONObject json = new JSONObject(response);
							JSONArray array = json.getJSONArray("data");

							ArrayList<FacebookFriend> friends = new ArrayList<FacebookFriend>();
							for (int i = 0; i < array.length(); i++) {
								friends.add(new FacebookFriend().deserialize(array.getJSONObject(i)));
							}

							onFacebookGetFriendsComplete.onGetFriendsCompete(user, friends);
						} catch (Exception ex) {
							onFacebookGetFriendsComplete.onGetFriendsCompete(user, null);
						}
					}

					@Override
					public void onHttpRequestTimeout(Exception ex) {
						AppLog.e("FacebookGraph.getFriends.onHttpRequestTimeout", "Timeout", ex);
						onFacebookGetFriendsComplete.onGetFriendsCompete(user, null);
					}

				}, m_graphTimeout).execute();

			}
		});
	}

	public void selectFriendDialog(final IFacebookSelectFriendDialogComplete onFacebookFriendSelectFriendDialogComplete) {
		getFriends(new IFacebookGetFriendsComplete() {

			@Override
			public void onGetFriendsCompete(final FacebookUser user, ArrayList<FacebookFriend> friends) {

				if (user == null || friends == null) {
					onFacebookFriendSelectFriendDialogComplete.onSelectFriendDialogComplete(user, null);
					return;
				}

				final ModifiableInjectableAdapter<FacebookFriendView> adapter = new ModifiableInjectableAdapter<FacebookFriendView>(
						m_fragment.getActivity(), R.layout.adapter_share_friendlist, null, false);

				adapter.addAll(FacebookFriendView.getAll(friends));

				new AlertDialog.Builder(m_fragment.getActivity())
						.setAdapter(adapter, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								FacebookFriend friend = null;
								try {
									friend = adapter.getItem(which).get();
								} catch (Exception ex) {
									AppLog.e("FacebookWrapper.selectFriendDialog", "Failed to get selection", ex);
								}
								onFacebookFriendSelectFriendDialogComplete.onSelectFriendDialogComplete(user, friend);
							}

						}).create().show();
			}

		});
	}

	public static String createFeedUrl(String fromFacebookUserId, String toFacebookUserId, String link, String name,
			String caption, String description) {

		StringBuilder url = new StringBuilder(FacebookFeedUrl);

		url.append("?app_id=" + Session.getActiveSession().getApplicationId());
		url.append("&picture=" + FacebookSharePictureUri);
		url.append("&display=" + "page");
		url.append("&from=" + fromFacebookUserId);
		url.append("&to=" + toFacebookUserId);

		if (link != null && !link.equals("")) {
			url.append("&link=" + link);
		}
		if (name != null && !name.equals("")) {
			url.append("&name=" + name);
		}
		if (caption != null && !caption.equals("")) {
			url.append("&caption=" + caption);
		}
		if (description != null && !description.equals("")) {
			url.append("&description=" + description);
		}
		url.append("&redirect_uri=" + Config.FacebookRedirectUri);

		return url.toString();
	}

	private class SessionStatusCallback implements Session.StatusCallback {

		@Override
		public void call(Session session, SessionState state, Exception exception) {
			if (exception != null) {
				AppLog.e("SessionStatusCallback.call", "Exception", exception);
			}

			if (state == SessionState.OPENED) {
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
		Preferences preferences = new Preferences(m_fragment.getActivity());
		m_user = preferences.getFacebookUser();

		return m_user;
	}

	private void saveUser(FacebookUser user) {
		if (user == null) {
			return;
		}

		m_user = user;
		Preferences preferences = new Preferences(m_fragment.getActivity());
		preferences.setFacebookUser(user);
	}
}
