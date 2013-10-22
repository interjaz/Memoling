package app.memoling.android.webservice.helper;

import java.util.UUID;

import app.memoling.android.adapter.MemoAdapter;
import app.memoling.android.entity.Memo;
import app.memoling.android.entity.PublishedMemo;
import app.memoling.android.facebook.FacebookUser;
import app.memoling.android.facebook.FacebookWrapper.IFacebookGetUserComplete;
import app.memoling.android.ui.FacebookFragment;
import app.memoling.android.webservice.WsFacebookUsers;
import app.memoling.android.webservice.WsFacebookUsers.ILoginComplete;
import app.memoling.android.webservice.WsPublishedLibraries;
import app.memoling.android.webservice.WsPublishedLibraries.IUploadMemoShareComplete;

public class PublishedMemoUpload {

	private FacebookFragment m_facebookFragment;
	private PublishedMemo m_published;
	private IPublishedMemoUpload m_publishedMemoUploadInterface;

	public static interface IPublishedMemoUpload {
		void onSuccess(String url);

		void onException(Exception ex);
	}

	public static class ExceptionReasons {
		public final static String NoMemoToUpload = "NoMemoToUpload";
		public final static String WsAuthError = "WsAuthError";
		public final static String WsUploadError = "WsUploadError";
		public final static String Generic = "Generic";
	}

	public PublishedMemoUpload(FacebookFragment facebookFragment, IPublishedMemoUpload publishedMemoUpload) {
		m_facebookFragment = facebookFragment;
		m_publishedMemoUploadInterface = publishedMemoUpload;
	}

	public void upload(final String memoId) {

		m_facebookFragment.getFacebookUser(new IFacebookGetUserComplete() {

			@Override
			public void onGetUserComplete(FacebookUser user) {

				m_published = new PublishedMemo();
				m_published.setPublishedMemoId(UUID.randomUUID().toString());
				m_published.setMemoId(memoId);

				MemoAdapter memoAdapter = new MemoAdapter(m_facebookFragment.getActivity());
				Memo memo = memoAdapter.get(m_published.getMemoId());
				if (memo == null) {
					m_publishedMemoUploadInterface.onException(new Exception(ExceptionReasons.NoMemoToUpload));
					return;
				}

				m_published.setMemo(memo);
				m_published.setFacebookUserId(user.getId());

				loginWs(user);

			}

		});
	}

	private void loginWs(FacebookUser user) {
		WsFacebookUsers.login(user, new ILoginComplete() {
			@Override
			public void onLoginComplete(boolean result) {
				if (result != true) {
					// forward exception
					m_publishedMemoUploadInterface.onException(new Exception(ExceptionReasons.WsAuthError));
					return;
				}

				uploadContent();
			}
		});
	}

	private void uploadContent() {
		WsPublishedLibraries.uploadMemoShare(m_published, new IUploadMemoShareComplete() {
			@Override
			public void onUploadMemoShareComplete(boolean result, String url) {
				if (result != true) {
					// forward exception
					m_publishedMemoUploadInterface.onException(new Exception(ExceptionReasons.WsUploadError));
					return;
				}

				complete(url);
			}
		});
	}

	private void complete(String url) {
		m_publishedMemoUploadInterface.onSuccess(url);
	}

}
