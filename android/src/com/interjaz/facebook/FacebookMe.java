package com.interjaz.facebook;

import java.net.URI;
import java.net.URISyntaxException;

import org.json.JSONException;

import com.interjaz.webrequest.HttpGetRequestTask;
import com.interjaz.webrequest.IHttpRequestTaskComplete;

public class FacebookMe implements IHttpRequestTaskComplete {

	private final static String FacebookMeRequest = "https://graph.facebook.com/me?access_token=%s";
	private final static int m_timeout = 8000;
	
	private IFacebookUserFound m_found;

	public void getUser(String token, IFacebookUserFound found) {
		try {
			m_found = found;
			URI uri = new URI(String.format(FacebookMeRequest, token));
			new HttpGetRequestTask(uri, this, m_timeout);
		} catch (URISyntaxException e) {
			// Should not happen
		}
	}

	@Override
	public void onHttpRequestTaskComplete(String response) {
		FacebookUser user = parse(response);
		
		if(m_found != null) {
			m_found.onFacebookUserFound(user);
		}
	}

	@Override
	public void onHttpRequestTimeout(Exception ex) {
		if(m_found != null) {
			m_found.onFacebookUserFound(null);
		}
	}
	
	private static FacebookUser parse(String response) {
		FacebookUser user = new FacebookUser();
		
		try {
			user.deserialize(response);
		} catch(JSONException ex) {
			user = null;
		}
		
		return user;
	}

}
