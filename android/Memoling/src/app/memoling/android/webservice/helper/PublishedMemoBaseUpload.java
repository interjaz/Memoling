package app.memoling.android.webservice.helper;

import java.util.ArrayList;

import app.memoling.android.adapter.MemoAdapter;
import app.memoling.android.adapter.MemoAdapter.Sort;
import app.memoling.android.db.DatabaseHelper.Order;
import app.memoling.android.entity.Memo;
import app.memoling.android.entity.MemoBase;
import app.memoling.android.entity.PublishedMemoBase;
import app.memoling.android.facebook.FacebookUser;
import app.memoling.android.facebook.FacebookWrapper.IFacebookGetUserComplete;
import app.memoling.android.ui.FacebookFragment;
import app.memoling.android.webservice.WsFacebookUsers;
import app.memoling.android.webservice.WsFacebookUsers.ILoginComplete;
import app.memoling.android.webservice.WsPublishedLibraries;
import app.memoling.android.webservice.WsPublishedLibraries.IUploadComplete;

// TODO: Add translated name, so search engine can find stuff more easily
public class PublishedMemoBaseUpload {

	private FacebookFragment m_facebookFragment;
	private static PublishedMemoBase m_published;
	private static IPublishedMemoBaseUpload m_publishedMemoBaseUploadInterface;

	public static class ExceptionReasons {
		public final static String NoMemosToUpload = "NoMemosToUpload";
		public final static String WsAuthError = "WsAuthError";
		public final static String WsUploadError = "WsUploadError";
		public final static String Generic = "Generic";
	}

	public static interface IPublishedMemoBaseUpload {
		void onSuccess();

		void onException(Exception ex);
	}

	public PublishedMemoBaseUpload(FacebookFragment facebookFragment,
			IPublishedMemoBaseUpload publishedMemoBaseUploadInterface) {
		m_facebookFragment = facebookFragment;
		m_publishedMemoBaseUploadInterface = publishedMemoBaseUploadInterface;
	}

	public void upload(String memoBaseId, String memoBaseGenreId, String description) {
		m_published = new PublishedMemoBase();
		m_published.setMemoBaseId(memoBaseId);
		m_published.setMemoBaseGenreId(memoBaseGenreId);
		m_published.setDescription(description);

		m_facebookFragment.getFacebookUser(new IFacebookGetUserComplete() {

			@Override
			public void onGetUserComplete(FacebookUser user) {

				MemoAdapter memoAdapter = new MemoAdapter(m_facebookFragment.getActivity());
				ArrayList<Memo> memos = memoAdapter.getAll(m_published.getMemoBaseId(), Sort.CreatedDate, Order.ASC);
				if (memos.size() == 0) {
					m_publishedMemoBaseUploadInterface.onException(new Exception(ExceptionReasons.NoMemosToUpload));
					return;
				}
				MemoBase memoBase = memos.get(0).getMemoBase();
				m_published.setMemoBase(memoBase);
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
					m_publishedMemoBaseUploadInterface.onException(new Exception(ExceptionReasons.WsAuthError));
					return;
				}

				uploadContent();
			}
		});
	}

	private void uploadContent() {
		WsPublishedLibraries.upload(m_published, new IUploadComplete() {
			@Override
			public void onUploadComplete(boolean result) {
				if (result != true) {
					// forward exception
					m_publishedMemoBaseUploadInterface.onException(new Exception(ExceptionReasons.WsUploadError));
					return;
				}

				complete();
			}
		});
	}

	private void complete() {
		m_publishedMemoBaseUploadInterface.onSuccess();
	}
}
