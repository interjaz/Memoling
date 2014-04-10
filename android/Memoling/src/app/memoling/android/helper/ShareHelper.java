package app.memoling.android.helper;

import android.app.AlertDialog;
import android.content.Intent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import app.memoling.android.Config;
import app.memoling.android.R;
import app.memoling.android.adapter.MemoAdapter;
import app.memoling.android.entity.Memo;
import app.memoling.android.facebook.FacebookFriend;
import app.memoling.android.facebook.FacebookUser;
import app.memoling.android.facebook.FacebookWrapper;
import app.memoling.android.facebook.FacebookWrapper.IFacebookGetUserComplete;
import app.memoling.android.facebook.FacebookWrapper.IFacebookSelectFriendDialogComplete;
import app.memoling.android.ui.FacebookFragment;
import app.memoling.android.ui.adapter.DrawerAdapter;
import app.memoling.android.ui.view.DrawerView;
import app.memoling.android.webservice.helper.PublishedMemoUpload;
import app.memoling.android.webservice.helper.PublishedMemoUpload.IPublishedMemoUpload;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class ShareHelper implements IPublishedMemoUpload {

	private FacebookFragment m_facebookFragment;

	private PublishedMemoUpload m_publishedMemoUpload;

	private boolean m_isFacebookShare;
	private FacebookShareEventHandler m_facebookShareEventHandler;
	private ShareApplicationEventHandler m_applicationShareEventHandler;

	private String m_id;
	private boolean m_isBase;

	public ShareHelper(FacebookFragment facebookFragment, boolean isBase) {
		m_facebookFragment = facebookFragment;
		m_isBase = isBase;

		m_publishedMemoUpload = new PublishedMemoUpload(facebookFragment, this);
		m_facebookShareEventHandler = new FacebookShareEventHandler();
		m_applicationShareEventHandler = new ShareApplicationEventHandler();

		m_facebookShareEventHandler = new FacebookShareEventHandler();
		m_applicationShareEventHandler = new ShareApplicationEventHandler();
	}

	public void setId(String id) {
		m_id = id;
	}

	public void onPopulateDrawerMemo(DrawerAdapter drawer) {
		drawer.addGroup(new DrawerView(R.drawable.ic_sharefacebook, R.string.memo_sharefacebook, m_facebookShareEventHandler));
		drawer.addGroup(new DrawerView(R.drawable.ic_share, R.string.memo_share, m_applicationShareEventHandler));
	}

	public void shareFacebook(String id) {
		m_isFacebookShare = true;
		share(id);
	}

	public void shareApplication(String id) {
		m_isFacebookShare = false;
		share(id);
	}

	private void share(String id) {
		showProgressBar(true);
		m_id = id;

		Toast.makeText(m_facebookFragment.getActivity(), R.string.share_generatingShortcut, Toast.LENGTH_SHORT).show();

		if (!m_isBase) {
			m_publishedMemoUpload.upload(m_id);
		}
	}

	@Override
	public void onSuccess(final String url) {

		m_facebookFragment.getFacebookUser(new IFacebookGetUserComplete() {

			@Override
			public void onGetUserComplete(FacebookUser user) {
				String msg = "";
				String name = "";

				if (!m_isBase) {
					Memo memo = new MemoAdapter(m_facebookFragment.getActivity()).getDeep(m_id);
					String word = (memo.getWordA().getWord() + " - " + memo.getWordB().getWord());
					if (word.length() > 10) {
						word = word.substring(0, 7) + "...";
					}

					name = word;
					msg = String.format(m_facebookFragment.getActivity().getString(R.string.share_message),
							user.getName(), word, url);
				}

				if (m_isFacebookShare) {
					onShareFacebook(url, msg, name);
				} else {
					onShareApplication(msg);
				}

			}

		});

	}

	private void onShareFacebook(final String url, final String msg, final String name) {
		Toast.makeText(m_facebookFragment.getActivity(), R.string.share_fbGettingFriends, Toast.LENGTH_SHORT).show();

		m_facebookFragment.selectFacebookFriends(new IFacebookSelectFriendDialogComplete() {

			@Override
			public void onSelectFriendDialogComplete(FacebookUser user, FacebookFriend friend) {
				showProgressBar(false);

				if (user == null || friend == null) {
					Toast.makeText(m_facebookFragment.getActivity(), R.string.share_fbGettingFriendsError,
							Toast.LENGTH_SHORT).show();
					return;
				}

				WebView vw = new WebView(m_facebookFragment.getActivity()) {
					@Override
					public boolean onCheckIsTextEditor() {
						return true; 
					}
				};
				
				String fbUrl = FacebookWrapper.createFeedUrl(user.getId(), friend.getId(), url, name,
						m_facebookFragment.getActivity().getString(R.string.share_fbCaption), msg);
				fbUrl = fbUrl.replace(' ', '+');

				final AlertDialog vwDialog = new AlertDialog.Builder(m_facebookFragment.getActivity()).setView(vw)
						.create();

				vw.setWebChromeClient(new WebChromeClient());
				vw.setWebViewClient(new WebViewClient() {

					public boolean shouldOverrideUrlLoading(WebView view, String url) {

						if (!url.startsWith(Config.FacebookRedirectUri)) {
							view.loadUrl(url);
						} else {
							vwDialog.dismiss();
						}

						return false;
					}
				});

				vw.loadUrl(fbUrl);
				vwDialog.show();
				
				vw.getSettings().setUseWideViewPort(true);
				vw.setFocusableInTouchMode(true);
				vw.setFocusable(true);
				vw.setHapticFeedbackEnabled(true);
				vw.setClickable(true);
			}

		});
	}

	private void onShareApplication(String msg) {
		showProgressBar(false);

		// Share via Application
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
		sendIntent.setType("text/plain");
		m_facebookFragment.getActivity().startActivity(
				Intent.createChooser(sendIntent,
						m_facebookFragment.getActivity().getResources().getText(R.string.share_intent)));

	}

	@Override
	public void onException(Exception ex) {
		Toast.makeText(m_facebookFragment.getActivity(), R.string.share_error, Toast.LENGTH_SHORT).show();
	}

	private class FacebookShareEventHandler implements DrawerView.OnClickListener {
		@Override
		public void onClick(DrawerView v) {
			shareFacebook(m_id);
		}

	}

	private class ShareApplicationEventHandler implements DrawerView.OnClickListener {
		@Override
		public void onClick(DrawerView v) {
			shareApplication(m_id);
		}

	}

	private void showProgressBar(boolean show) {
		if (m_facebookFragment.getActivity() instanceof SherlockFragmentActivity) {
			((SherlockFragmentActivity) m_facebookFragment.getActivity()).setProgressBarIndeterminateVisibility(show);
		} else {
			m_facebookFragment.getActivity().setProgressBarIndeterminateVisibility(show);
		}
	}
}
