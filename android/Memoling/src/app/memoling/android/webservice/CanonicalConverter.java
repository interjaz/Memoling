package app.memoling.android.webservice;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.memoling.android.entity.Language;
import app.memoling.android.entity.Memo;
import app.memoling.android.entity.MemoBase;
import app.memoling.android.entity.MemoSentence;
import app.memoling.android.entity.PublishedMemo;
import app.memoling.android.entity.PublishedMemoBase;
import app.memoling.android.entity.Word;
import app.memoling.android.facebook.FacebookUser;
import app.memoling.android.helper.AppLog;

class CanonicalConverter {

	static PublishedMemoBase parsePublishedMemoBase(JSONObject json) {
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
		} catch (JSONException ex) {
			// Should not happen
			return null;
		}
	}

	static MemoBase parseMemoBase(JSONObject json) {
		try {
			MemoBase obj = new MemoBase();

			obj.setActive(true);
			obj.setCreated(new Date());
			obj.setMemoBaseId(UUID.randomUUID().toString());
			obj.setName(json.getString("name"));

			JSONArray array = json.optJSONArray("memos");
			if (array != null) {
				ArrayList<Memo> memos = new ArrayList<Memo>();
				for (int i = 0; i < array.length(); i++) {
					Memo memo = parseMemo(array.getJSONObject(i));
					memo.setMemoBase(obj);
					memo.setMemoBaseId(obj.getMemoBaseId());
					memos.add(memo);
				}
				obj.setMemos(memos);
			}
			return obj;
		} catch (JSONException ex) {
			return null;
		}
	}

	static Memo parseMemo(JSONObject json) {
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
		} catch (JSONException ex) {
			return null;
		}
	}

	static Word parseWord(JSONObject json) {
		try {
			Word obj = new Word();

			obj.setLanguage(Language.parse(json.getString("languageIso639")));
			obj.setWord(json.getString("word"));
			obj.setWordId(UUID.randomUUID().toString());
			obj.setDescription(json.optString("description"));

			return obj;
		} catch (JSONException ex) {
			return null;
		}
	}

	static JSONObject publishedMemoBaseToWsJson(PublishedMemoBase obj) {
		try {
			JSONObject json = new JSONObject();
			json.put("description", obj.getDescription());
			json.put("facebookUserId", obj.getFacebookUserId());
			json.put("memoBase", memoBaseToWsJson(obj.getMemoBase()));
			json.put("memoBaseGenreId", obj.getMemoBaseGenreId());

			return json;
		} catch (JSONException ex) {
			return null;
		}
	}

	static JSONObject publishedMemoToWsJson(PublishedMemo obj) {
		try {
			JSONObject json = new JSONObject();
			json.put("facebookUserId", obj.getFacebookUserId());
			json.put("memo", memoToWsJson(obj.getMemo()));

			return json;
		} catch (JSONException ex) {
			return null;
		}
	}

	static JSONObject memoBaseToWsJson(MemoBase obj) {
		try {
			JSONObject json = new JSONObject();
			json.put("name", obj.getName());
			json.put("memos", memosToWsJson(obj.getMemos()));

			return json;
		} catch (JSONException ex) {
			return null;
		}
	}

	static JSONArray memosToWsJson(ArrayList<Memo> objs) {
		JSONArray array = new JSONArray();

		for (Memo obj : objs) {
			JSONObject json = memoToWsJson(obj);
			array.put(json);
		}

		return array;
	}

	static JSONObject memoToWsJson(Memo obj) {
		try {
			JSONObject json = new JSONObject();

			json.put("wordA", wordToWsJson(obj.getWordA()));
			json.put("wordB", wordToWsJson(obj.getWordB()));

			return json;
		} catch (JSONException ex) {
			return null;
		}
	}

	static JSONObject wordToWsJson(Word obj) {
		try {
			JSONObject json = new JSONObject();

			json.put("word", obj.getWord());
			json.put("languageIso639", obj.getLanguage());

			return json;
		} catch (JSONException ex) {
			return null;
		}

	}

	static JSONObject facebookUserToWsJson(FacebookUser user) {

		try {
			JSONObject jsonUser = new JSONObject();
			jsonUser.put("facebookUserId", user.getId());

			jsonUser.put("name", user.getName());
			jsonUser.put("firstName", user.getFirstName());
			jsonUser.put("lastName", user.getLastName());
			jsonUser.put("link", user.getLink());
			jsonUser.put("username", user.getUsername());

			JSONObject jsonHometown = new JSONObject();
			jsonHometown.put("facebookLocationId", user.getHometown().getId());
			jsonHometown.put("name", user.getHometown().getName());
			jsonUser.put("hometown", jsonHometown);

			jsonUser.put("locationId", user.getLocation().getId());
			jsonUser.put("gender", user.getGender());
			jsonUser.put("timezone", user.getTimezone());
			jsonUser.put("locale", user.getLocale());
			jsonUser.put("updatedTime", user.getUpdatedTime());
			jsonUser.put("verified", user.getVerified());

			JSONObject jsonLocation = new JSONObject();
			jsonLocation.put("facebookLocationId", user.getLocation().getId());
			jsonLocation.put("name", user.getLocation().getName());

			jsonUser.put("location", jsonLocation);

			return jsonUser;
		} catch (JSONException ex) {
			return null;
		}
	}

	static MemoSentence parseMemoSentence(JSONObject json) {

		try {

			MemoSentence sentence = new MemoSentence();

			JSONObject original = json.getJSONObject("original");
			JSONObject translated = json.getJSONObject("translated");

			sentence.setMemoSentenceId(UUID.randomUUID().toString());

			sentence.setOriginalSentence(original.getString("sentence"));
			sentence.setOriginalLanguage(Language.parse(original.getString("languageIso639")));

			sentence.setTranslatedSentence(translated.getString("sentence"));
			sentence.setTranslatedLanguage(Language.parse(translated.getString("languageIso639")));

			return sentence;

		} catch (Exception ex) {
			AppLog.e("WsSentcence", "parseMemoSentence", ex);
			return null;
		}
	}
}
