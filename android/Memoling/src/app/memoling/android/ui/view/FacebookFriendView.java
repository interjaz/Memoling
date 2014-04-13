package app.memoling.android.ui.view;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import app.memoling.android.R;
import app.memoling.android.facebook.FacebookFriend;
import app.memoling.android.helper.AppLog;
import app.memoling.android.thread.WorkerThread;
import app.memoling.android.ui.ResourceManager;
import app.memoling.android.ui.adapter.IInject;

public class FacebookFriendView implements IInject {

	private FacebookFriend m_friend;
	private Bitmap m_img;

	public FacebookFriendView(FacebookFriend friend) {
		m_friend = friend;
	}

	private void getImage(ImageView imageView) {

		new WorkerThread<ImageView, Void, Pair<ImageView, Bitmap>>() {

			@Override
			protected Pair<ImageView, Bitmap> doInBackground(ImageView... params) {

				if (m_img != null) {
					return new Pair<ImageView, Bitmap>(params[0], m_img);
				}

				try {
					URL url = new URL(m_friend.getPicSqure());
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setDoInput(true);
					connection.connect();
					InputStream input = connection.getInputStream();
					m_img = BitmapFactory.decodeStream(input);

					return new Pair<ImageView, Bitmap>(params[0], m_img);
				} catch (IOException ex) {
					AppLog.e("FacebookFriendView.getImage", "Exception", ex);
				}

				return null;
			}

			@Override
			protected void onPostExecute(Pair<ImageView, Bitmap> result) {
				super.onPostExecute(result);
				if (result != null) {
					result.first.setImageBitmap(result.second);
					result.first.invalidate();
				}
			}
		}.execute(imageView);

	}

	public FacebookFriend get() {
		return m_friend;
	}

	public static List<FacebookFriendView> getAll(List<FacebookFriend> friends) {
		List<FacebookFriendView> views = new ArrayList<FacebookFriendView>();
		for (FacebookFriend friend : friends) {
			views.add(new FacebookFriendView(friend));
		}

		return views;
	}

	@Override
	public void injcet(View view, ResourceManager resource) {
		ImageView image = (ImageView) view.findViewById(R.id.adapter_share_friendlistImage);
		TextView text = (TextView) view.findViewById(R.id.adapter_share_friendlistText);
		getImage(image);
		text.setText(m_friend.getName());
	}

}
