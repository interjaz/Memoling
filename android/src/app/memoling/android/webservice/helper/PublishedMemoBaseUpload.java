package app.memoling.android.webservice.helper;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import app.memoling.android.adapter.MemoAdapter;
import app.memoling.android.adapter.MemoAdapter.Sort;
import app.memoling.android.db.Order;
import app.memoling.android.entity.Memo;
import app.memoling.android.entity.PublishedMemoBase;
import app.memoling.android.facebook.FacebookBase;
import app.memoling.android.facebook.FacebookUser;
import app.memoling.android.facebook.IFacebookUserFound;
import app.memoling.android.webservice.WsFacebookUsers;
import app.memoling.android.webservice.WsFacebookUsers.ILoginComplete;
import app.memoling.android.webservice.WsPublishedLibraries;
import app.memoling.android.webservice.WsPublishedLibraries.IUploadComplete;

public class PublishedMemoBaseUpload extends FacebookBase implements IFacebookUserFound {

	private Activity m_activity;
	private FacebookUser m_facebookUser;
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

	public PublishedMemoBaseUpload(Activity activity, IPublishedMemoBaseUpload publishedMemoBaseUploadInterface) {
		m_activity = activity;
		m_publishedMemoBaseUploadInterface = publishedMemoBaseUploadInterface;
	}

	public void upload(String memoBaseId, String memoBaseGenreId, String description) {
		m_published = new PublishedMemoBase();
		m_published.setMemoBaseId(memoBaseId);
		m_published.setMemoBaseGenreId(memoBaseGenreId);
		m_published.setDescription(description);
		login();
	}

	private void login() {
		super.login(m_activity, this);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.loginOnActivityResult(m_activity, this, requestCode, resultCode, data);
	}

	@Override
	public void onFacebookUserFound(FacebookUser user) {
		if (user == null) {
			// Make toast that failed to upload
			return;
		}
		
		m_facebookUser = user;
		prepareForUpload();
	}

	private void prepareForUpload() {
		try {
			MemoAdapter memoAdapter = new MemoAdapter(m_activity);
			ArrayList<Memo> memos = memoAdapter.getAll(m_published.getMemoBaseId(), Sort.CreatedDate, Order.ASC);
			if (memos.size() == 0) {
				m_publishedMemoBaseUploadInterface.onException(new Exception(ExceptionReasons.NoMemosToUpload));
				return;
			}
			m_published.setMemoBase(memos.get(0).getMemoBase());
			m_published.setFacebookUserId(m_facebookUser.getId());

			loginWs();
		} catch (IOException ex) {
			m_publishedMemoBaseUploadInterface.onException(new Exception(ExceptionReasons.Generic, ex));
		}
	}

	private void loginWs() {
		WsFacebookUsers wsLogin = new WsFacebookUsers();
		wsLogin.login(m_facebookUser, new ILoginComplete() {
			@Override
			public void onLoginComplete(boolean result) {
				if (result != true) {
					// forward exception
					m_publishedMemoBaseUploadInterface.onException(new Exception(ExceptionReasons.WsAuthError));
					return;
				}

				upload();
			}
		});
	}

	private void upload() {
		WsPublishedLibraries wsLibraries = new WsPublishedLibraries();
		wsLibraries.upload(m_published, new IUploadComplete() {
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
