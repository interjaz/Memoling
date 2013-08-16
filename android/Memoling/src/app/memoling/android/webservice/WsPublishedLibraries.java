package app.memoling.android.webservice;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.memoling.android.Config;
import app.memoling.android.entity.Language;
import app.memoling.android.entity.Memo;
import app.memoling.android.entity.MemoBase;
import app.memoling.android.entity.PublishedMemoBase;
import app.memoling.android.entity.Word;
import app.memoling.android.helper.AppLog;
import app.memoling.android.webrequest.HttpGetRequestTask;
import app.memoling.android.webrequest.HttpPostRequestTask;
import app.memoling.android.webrequest.IHttpRequestTaskComplete;

public class WsPublishedLibraries {

	private static final String WsUrl = Config.WsUrlRoot + "/PublishedLibraries.php";
	private static final int m_timeout = 8000;

	public interface IIndexComplete {
		void onIndexComplete(ArrayList<PublishedMemoBase> headers);
	}
	
	public void index(int page, final IIndexComplete onComplete) {

		try {

			URI WsUri = new URI(WsUrl + "?action=index?page=" + Integer.toString(page));
			
			new HttpPostRequestTask(WsUri, new IHttpRequestTaskComplete() {

				@Override
				public void onHttpRequestTaskComplete(String response) {
					try {
						JSONArray array = new JSONArray(response);

						ArrayList<PublishedMemoBase> published = new ArrayList<PublishedMemoBase>();
						for(int i=0;i<array.length();i++) {
							published.add(parsePublishedMemoBase(array.getJSONObject(i)));
						}
						
						onComplete.onIndexComplete(published);
						
					} catch(JSONException ex) {
						onComplete.onIndexComplete(null);
					}
				}

				@Override
				public void onHttpRequestTimeout(Exception ex) {
					onComplete.onIndexComplete(null);
				}
				
			}, null, m_timeout).execute();

		} catch(URISyntaxException e) {
			// should not happen
		}
	}
	
	public interface ISearchComplete {
		void onSearchComplete(ArrayList<PublishedMemoBase> headers);
	}
	
	public void search(String keyword, String generId, String languageA, String  languageB, int page, final ISearchComplete onComplete) {
		try {

			String htmlKeyword = URLEncoder.encode(keyword, "utf-8");
			String uri = String.format(Locale.US, "%s?action=search&keyword=%s&genreId=%s&languageAIso639=%s&languageBIso639=%s&page=%d",
					WsUrl, htmlKeyword, generId, languageA, languageB, page);
			URI WsUri = new URI(uri);
			
			new HttpGetRequestTask(WsUri, new IHttpRequestTaskComplete() {

				@Override
				public void onHttpRequestTaskComplete(String response) {
					try {
						JSONArray array = new JSONArray(response);

						ArrayList<PublishedMemoBase> published = new ArrayList<PublishedMemoBase>();
						for(int i=0;i<array.length();i++) {
							published.add(parsePublishedMemoBase(array.getJSONObject(i)));
						}
						
						onComplete.onSearchComplete(published);
						
					} catch(JSONException ex) {
						onComplete.onSearchComplete(null);
					}
				}

				@Override
				public void onHttpRequestTimeout(Exception ex) {
					onComplete.onSearchComplete(null);
				}
				
			}, null, m_timeout).execute();

		} catch(Exception ex) {
			AppLog.e("WsPublishLibraries", "search", ex);
		}
	}
	
	public interface IPreviewComplete {
		void onPreviewComplete(PublishedMemoBase preview);
	}
	
	public void preview(String publishedMemoBaseId, final IPreviewComplete onComplete) {

		try {

			URI WsUri = new URI(WsUrl + "?action=preview&id=" + publishedMemoBaseId);
			
			new HttpGetRequestTask(WsUri, new IHttpRequestTaskComplete() {

				@Override
				public void onHttpRequestTaskComplete(String response) {
					try {
						JSONObject json= new JSONObject(response);

						PublishedMemoBase published = new PublishedMemoBase();
						published = parsePublishedMemoBase(json);
						
						onComplete.onPreviewComplete(published);
						
					} catch(JSONException ex) {
						onComplete.onPreviewComplete(null);
					}
				}

				@Override
				public void onHttpRequestTimeout(Exception ex) {
					onComplete.onPreviewComplete(null);
				}
				
			}, null, m_timeout).execute();

		} catch(URISyntaxException e) {
			// should not happen
		}
	}
	
	public interface IDownloadComplete {
		void onDownloadComplete(PublishedMemoBase published);
	}
	
	public void download(String publishedMemoBaseId, final IDownloadComplete onComplete) {

		try {

			URI WsUri = new URI(WsUrl + "?action=download&id=" + publishedMemoBaseId);
			
			new HttpGetRequestTask(WsUri, new IHttpRequestTaskComplete() {

				@Override
				public void onHttpRequestTaskComplete(String response) {
					try {
						JSONObject json= new JSONObject(response);

						PublishedMemoBase published = new PublishedMemoBase();
						published = parsePublishedMemoBase(json);
						
						onComplete.onDownloadComplete(published);
						
					} catch(JSONException ex) {
						onComplete.onDownloadComplete(null);
					}
				}

				@Override
				public void onHttpRequestTimeout(Exception ex) {
					onComplete.onDownloadComplete(null);
				}
				
			}, null, m_timeout).execute();

		} catch(URISyntaxException e) {
			// should not happen
		}
	}
	
	public interface IUploadComplete {
		void onUploadComplete(boolean result);
	}
	
	public void upload(PublishedMemoBase publishedMemoBase, final IUploadComplete onComplete) {

		try {

			URI WsUri = new URI(WsUrl + "?action=upload");
			JSONObject json = publishedMemoBaseToWsJson(publishedMemoBase);
			BasicNameValuePair post = new BasicNameValuePair("library", json.toString());
			
			new HttpPostRequestTask(WsUri, new IHttpRequestTaskComplete() {

				@Override
				public void onHttpRequestTaskComplete(String response) {
					onComplete.onUploadComplete(Boolean.parseBoolean(response));
				}

				@Override
				public void onHttpRequestTimeout(Exception ex) {
					onComplete.onUploadComplete(false);
				}
				
			}, null, m_timeout, "UTF-8").execute(post);

		} catch(URISyntaxException e) {
			// should not happen
		}
	}
	
	private static PublishedMemoBase parsePublishedMemoBase(JSONObject json) {
		try {
			PublishedMemoBase obj = new PublishedMemoBase();
			
			obj.setPublishedMemoBaseId(json.getString("publishedMemoBaseId"));
			obj.setMemoBaseGenreId(json.getString("memoBaseGenreId"));			
			obj.setDescription(json.optString("description", ""));
			obj.setDownloads(json.getInt("downloads"));
			obj.setPrimaryLanguageAIso639(Language.parse(json.getString("primaryLanguageAIso639")));
			obj.setPrimaryLanguageBIso639(Language.parse(json.getString("primaryLanguageBIso639")));
			obj.setMemosCount(json.getInt("memosCount"));
			obj.setMemoBase(parseMemoBase(json.getJSONObject("memoBase")));
	
			
			return obj;
		} catch(JSONException ex) {
			// Should not happen
			return null;
		}
	}
	
	private static MemoBase parseMemoBase(JSONObject json) {
		try {
			MemoBase obj = new MemoBase();
			
			obj.setActive(true);
			obj.setCreated(new Date());
			obj.setMemoBaseId(UUID.randomUUID().toString());
			obj.setName(json.getString("name"));
			
			JSONArray array = json.optJSONArray("memos");			
			if(array != null) {
				ArrayList<Memo> memos = new ArrayList<Memo>();
				for(int i=0;i<array.length();i++) {
					Memo memo = parseMemo(array.getJSONObject(i));
					memo.setMemoBase(obj);
					memo.setMemoBaseId(obj.getMemoBaseId());
					memos.add(memo);				
				}
				obj.setMemos(memos);
			}
			return obj;
		} catch(JSONException ex) {
			return null;
		}
	}
	
	private static Memo parseMemo(JSONObject json) {
		try {
			Memo obj = new Memo();
			
			obj.setActive(true);
			obj.setCorrectAnsweredWordA(0);
			obj.setCorrectAnsweredWordB(0);
			obj.setCreated(new Date());
			obj.setDisplayed(0);
			obj.setLastReviewed(new Date());
			obj.setMemoId(UUID.randomUUID().toString());
		
			Word wordA = parseWord(json.getJSONObject("wordA"));
			Word wordB = parseWord(json.getJSONObject("wordB"));

			obj.setWordA(wordA);
			obj.setWordAId(wordA.getWordId());
			obj.setWordB(wordB);
			obj.setWordBId(wordB.getWordId());
			
			return obj;
		} catch(JSONException ex) {
			return null;
		}
	}
	
	private static Word parseWord(JSONObject json) {
		try {
			Word obj = new Word();
			
			obj.setLanguage(Language.parse(json.getString("languageIso639")));
			obj.setWord(json.getString("word"));
			obj.setWordId(UUID.randomUUID().toString());
			obj.setDescription(json.optString("description"));
			
			return obj;
		} catch(JSONException ex) {
			return null;
		}
	}

	private static JSONObject publishedMemoBaseToWsJson(PublishedMemoBase obj) {
		try 
		{
			JSONObject json = new JSONObject();
			json.put("description", obj.getDescription());
			json.put("facebookUserId", obj.getFacebookUserId());
			json.put("memoBase", memoBaseToWsJson(obj.getMemoBase()));
			json.put("memoBaseGenreId", obj.getMemoBaseGenreId());
			
			return json;
		} catch(JSONException ex) {
			return null;
		}
	}
	
	private static JSONObject memoBaseToWsJson(MemoBase obj) {
		try {
			JSONObject json = new JSONObject();
			json.put("name", obj.getName());
			json.put("memos", memosToWsJson(obj.getMemos()));
			
			return json;
		} catch(JSONException ex) {
			return null;
		}
	}
	
	private static JSONArray memosToWsJson(ArrayList<Memo> objs) {
		try {
			JSONArray array = new JSONArray();
			
			for(Memo obj : objs) {
				JSONObject json = new JSONObject();
				
				json.put("wordA", wordToWsJson(obj.getWordA()));
				json.put("wordB", wordToWsJson(obj.getWordB()));
				
				array.put(json);
			}
			
			return array;
		} catch(JSONException ex) {
			return null;
		}
	}
	
	private static JSONObject wordToWsJson(Word obj) {
		try {
			JSONObject json = new JSONObject();
			
			json.put("word", obj.getWord());
			json.put("languageIso639", obj.getLanguage());
			
			return json;
		} catch(JSONException ex) {
			return null;
		}
		
	}
	

}
