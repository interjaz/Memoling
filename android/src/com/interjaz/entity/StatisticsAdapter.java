package com.interjaz.entity;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.interjaz.Language;
import com.interjaz.db.SqliteAdapter;
import com.interjaz.helper.DatabaseHelper;

public class StatisticsAdapter extends SqliteAdapter {

	public final static String DatabaseName = "TranslateMemo";
	public final static int m_databaseVersion = 1;
	private Calendar m_calendar = Calendar.getInstance();

	public StatisticsAdapter(Context context) throws IOException {
		super(context, DatabaseName, m_databaseVersion);
	}

	public int[] getMonthlyAdded() {
		SQLiteDatabase db = null;

		int months = 12;
		int[] monthly = new int[months];

		String query;
		try {
			db = getDatabase();
			query = "SELECT SUBSTR(Created, 1, 4) as Year, SUBSTR(Created, 6,2) as Month, COUNT(MemoId) AS Memos "
					+ "FROM Memos WHERE YEAR = ? GROUP BY Year, Month";

			Cursor cursor = db.rawQuery(query, new String[] { Integer.toString(m_calendar.get(Calendar.YEAR)) });

			while (cursor.moveToNext()) {
				int month = DatabaseHelper.getInt(cursor, "Month") - 1;
				monthly[month] = DatabaseHelper.getInt(cursor, "Memos");
			}

		} finally {
			if (db != null) {
				close();
			}
		}

		return monthly;
	}

	public int[] getDailyReviewed() {
		SQLiteDatabase db = null;

		int days = m_calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		int[] daily = new int[days];

		String query;
		try {
			db = getDatabase();
			query = "SELECT SUBSTR(LastReviewed, 1, 4) as Year, SUBSTR(LastReviewed, 6,2) as Month, SUBSTR(LastReviewed, 9,2) as Day, COUNT(MemoId) AS Memos "
					+ "FROM Memos WHERE Year = ? AND Month = ? GROUP BY Year, Month, Day";

			String strYear = Integer.toString(m_calendar.get(Calendar.YEAR));
			String strMonth = String.format(Locale.US, "%02d", m_calendar.get(Calendar.MONTH)+1);
			Cursor cursor = db.rawQuery(query, new String[] { strYear, strMonth });

			while (cursor.moveToNext()) {
				int day = DatabaseHelper.getInt(cursor, "Day") - 1;
				daily[day] = DatabaseHelper.getInt(cursor, "Memos");
			}

		} finally {
			if (db != null) {
				close();
			}
		}
		return daily;
	}

	public Statistics getStatitstics() {
		SQLiteDatabase db = null;
		Statistics statistics = new Statistics();
		
		String query;
		try {
			db = getDatabase();
			
			query = "SELECT " + 
					"  COUNT(Memos.MemoId) AS MemoCount," + 
					"  COUNT(MemoBases.MemoBaseId ) AS BaseCount," + 
					"  SUM(Memos.Displayed) AS Repetitied," + 
					"  (SUM(Memos.CorrectAnsweredWordA) + SUM(Memos.CorrectAnsweredWordB)) / SUM(Memos.Displayed) AS AvgPerformance," + 
					"  Most.MemoId AS MostMemoId," + 
					"  Most.MemoBaseId AS MostMemoBaseId," + 
					"  Most.Created AS MostCreated," + 
					"  Most.BaseCreated AS MostBaseCreated," + 
					"  Most.LastReviewed AS MostLastReviewed," + 
					"  Most.Displayed AS MostDisplayed," + 
					"  Most.CorrectAnsweredWordA AS MostCorrectAnsweredWordA, " + 
					"  Most.CorrectAnsweredWordB AS MostCorrectAnsweredWordB, " + 
					"  Most.Active AS MostActive," + 
					"  Most.BaseActive AS MostBaseActive," + 
					"  Most.Name AS MostBaseName, " + 
					"  Most.WA_WordId AS MostWordAId," + 
					"  Most.WA_LanguageIso639 AS MostLanguageA," + 
					"  Most.WA_Word AS MostWordA," + 
					"  Most.WB_WordId AS MostWordBId," + 
					"  Most.WB_LanguageIso639 AS MostLanguageB," + 
					"  Most.WB_Word AS MostWordB," + 
					"  Least.MemoId AS LeastMemoId," + 
					"  Least.MemoBaseId AS LeastMemoBaseId," + 
					"  Least.Created AS LeastCreated," + 
					"  Least.BaseCreated AS LeastBaseCreated," + 
					"  Least.LastReviewed AS LeastLastReviewed," + 
					"  Least.Displayed AS LeastDisplayed," + 
					"  Least.CorrectAnsweredWordA AS LeastCorrectAnsweredWordA, " + 
					"  Least.CorrectAnsweredWordB AS LeastCorrectAnsweredWordB, " + 
					"  Least.Active AS LeastActive," + 
					"  Least.BaseActive AS LeastBaseActive," + 
					"  Least.Name AS LeastBaseName, " + 
					"  Least.WA_WordId AS LeastWordAId," + 
					"  Least.WA_LanguageIso639 AS LeastLanguageA," + 
					"  Least.WA_Word AS LeastWordA," + 
					"  Least.WB_WordId AS LeastWordBId," + 
					"  Least.WB_LanguageIso639 AS LeastLanguageB," + 
					"  Least.WB_Word AS LeastWordB," + 
					"  Longest.WordId LongestWordId," + 
					"  Longest.LanguageIso639 AS LongestLanguage," + 
					"  Longest.Word as LongestWord," + 
					"  Shortest.WordId ShortestWordId," + 
					"  Shortest.LanguageIso639 AS ShortestLanguage," + 
					"  Shortest.Word as ShortestWord" + 
					" " + 
					"FROM Memos, MemoBases," + 
					"(" + 
					"SELECT M.MemoId MemoId, M.Created Created, M.LastReviewed LastReviewed, M.Displayed Displayed, M.CorrectAnsweredWordA CorrectAnsweredWordA, M.CorrectAnsweredWordB CorrectAnsweredWordB, M.Active Active, " + 
					"	B.MemoBaseId MemoBaseId, B.Name Name, B.Created BaseCreated, B.Active BaseActive, " + 
					"	WA.WordId WA_WordId, WA.LanguageIso639 WA_LanguageIso639, WA.Word WA_Word, " + 
					"	WB.WordId WB_WordId, WB.LanguageIso639 WB_LanguageIso639, WB.Word WB_Word " + 
					"FROM Memos  AS M JOIN MemoBases AS B ON M.MemoBaseId = B.MemoBaseId " + 
					"JOIN Words AS WA ON M.WordAId = WA.WordId JOIN Words AS WB ON M.WordBId = WB.WordId " + 
					"ORDER BY Displayed DESC LIMIT 1) AS Most," + 
					"(" + 
					"SELECT M.MemoId MemoId, M.Created Created, M.LastReviewed LastReviewed, M.Displayed Displayed, M.CorrectAnsweredWordA CorrectAnsweredWordA, M.CorrectAnsweredWordB CorrectAnsweredWordB, M.Active Active, " + 
					"	B.MemoBaseId MemoBaseId, B.Name Name, B.Created BaseCreated, B.Active BaseActive, " + 
					"	WA.WordId WA_WordId, WA.LanguageIso639 WA_LanguageIso639, WA.Word WA_Word, " + 
					"	WB.WordId WB_WordId, WB.LanguageIso639 WB_LanguageIso639, WB.Word WB_Word " + 
					"FROM Memos  AS M JOIN MemoBases AS B ON M.MemoBaseId = B.MemoBaseId " + 
					"JOIN Words AS WA ON M.WordAId = WA.WordId JOIN Words AS WB ON M.WordBId = WB.WordId " + 
					"ORDER BY Displayed ASC LIMIT 1) AS Least," + 
					"(" + 
					"SELECT *, LENGTH(Word) AS WordLength FROM Words ORDER BY WordLength DESC LIMIT 1" + 
					") Longest," + 
					"(" + 
					"SELECT *, LENGTH(Word) AS WordLength FROM Words ORDER BY WordLength ASC LIMIT 1" + 
					") Shortest";

			Cursor cursor = db.rawQuery(query, null);
			
			if(cursor.moveToFirst()) {
				statistics.setAveragePerformance(DatabaseHelper.getDouble(cursor, "AvgPerformance"));
				
				Memo memo = new Memo();
				MemoBase base = new MemoBase();
				Word wordA = new Word();
				Word wordB = new Word();
				
				memo.setActive(DatabaseHelper.getBoolean(cursor, "LeastActive"));
				memo.setCorrectAnsweredWordA(DatabaseHelper.getInt(cursor, "LeastCorrectAnsweredWordA"));
				memo.setCorrectAnsweredWordB(DatabaseHelper.getInt(cursor, "LeastCorrectAnsweredWordB"));
				memo.setCreated(DatabaseHelper.getDate(cursor, "LeastCreated"));
				memo.setDisplayed(DatabaseHelper.getInt(cursor, "LeastDisplayed"));
				memo.setLastReviewed(DatabaseHelper.getDate(cursor, "LeastLastReviewed"));
				base.setActive(DatabaseHelper.getBoolean(cursor, "LeastBaseActive"));
				base.setCreated(DatabaseHelper.getDate(cursor, "LeastBaseCreated"));
				base.setMemoBaseId(DatabaseHelper.getString(cursor, "LeastMemoBaseId"));
				base.setName(DatabaseHelper.getString(cursor, "LeastBaseName"));
				memo.setMemoBase(base);
				memo.setMemoBaseId(DatabaseHelper.getString(cursor, "LeastMemoBaseId"));				
				wordA.setLanguage(Language.parse(DatabaseHelper.getString(cursor, "LeastLanguageA")));
				wordA.setWord(DatabaseHelper.getString(cursor, "LeastWordA"));
				wordA.setWordId(DatabaseHelper.getString(cursor, "LeastWordAId"));
				memo.setWordA(wordA);
				wordB.setLanguage(Language.parse(DatabaseHelper.getString(cursor, "LeastLanguageB")));
				wordB.setWord(DatabaseHelper.getString(cursor, "LeastWordB"));
				wordB.setWordId(DatabaseHelper.getString(cursor, "LeastWordBId"));
				memo.setWordB(wordB);
				memo.setWordAId(DatabaseHelper.getString(cursor, "LeastWordAId"));
				memo.setWordBId(DatabaseHelper.getString(cursor, "LeastWordBId"));				
				statistics.setLeastRepeatedMemo(memo);
				
				memo = new Memo();
				base = new MemoBase();
				wordA = new Word();
				wordB = new Word();
				memo.setActive(DatabaseHelper.getBoolean(cursor, "MostActive"));
				memo.setCorrectAnsweredWordA(DatabaseHelper.getInt(cursor, "MostCorrectAnsweredWordA"));
				memo.setCorrectAnsweredWordB(DatabaseHelper.getInt(cursor, "MostCorrectAnsweredWordB"));
				memo.setCreated(DatabaseHelper.getDate(cursor, "MostCreated"));
				memo.setDisplayed(DatabaseHelper.getInt(cursor, "MostDisplayed"));
				memo.setLastReviewed(DatabaseHelper.getDate(cursor, "MostLastReviewed"));
				base.setActive(DatabaseHelper.getBoolean(cursor, "MostBaseActive"));
				base.setCreated(DatabaseHelper.getDate(cursor, "MostBaseCreated"));
				base.setMemoBaseId(DatabaseHelper.getString(cursor, "MostMemoBaseId"));
				base.setName(DatabaseHelper.getString(cursor, "MostBaseName"));
				memo.setMemoBase(base);
				memo.setMemoBaseId(DatabaseHelper.getString(cursor, "MostMemoBaseId"));				
				wordA.setLanguage(Language.parse(DatabaseHelper.getString(cursor, "MostLanguageA")));
				wordA.setWord(DatabaseHelper.getString(cursor, "MostWordA"));
				wordA.setWordId(DatabaseHelper.getString(cursor, "MostWordAId"));
				memo.setWordA(wordA);
				wordB.setLanguage(Language.parse(DatabaseHelper.getString(cursor, "MostLanguageB")));
				wordB.setWord(DatabaseHelper.getString(cursor, "MostWordB"));
				wordB.setWordId(DatabaseHelper.getString(cursor, "MostWordBId"));
				memo.setWordB(wordB);
				memo.setWordAId(DatabaseHelper.getString(cursor, "MostWordAId"));
				memo.setWordBId(DatabaseHelper.getString(cursor, "MostWordBId"));				
				statistics.setMostRepeatedMemo(memo);
				
				statistics.setLibrariesCount(DatabaseHelper.getInt(cursor, "BaseCount"));
				statistics.setTotalMemos(DatabaseHelper.getInt(cursor, "MemoCount"));
				statistics.setTotalRepetitions(DatabaseHelper.getInt(cursor, "Repetitied"));
				
				wordA = new Word();			
				wordA.setLanguage(Language.parse(DatabaseHelper.getString(cursor, "LongestLanguage")));
				wordA.setWord(DatabaseHelper.getString(cursor, "LongestWord"));
				wordA.setWordId(DatabaseHelper.getString(cursor, "LongestWordId"));
				statistics.setLongestWord(wordA);
				
				wordA = new Word();			
				wordA.setLanguage(Language.parse(DatabaseHelper.getString(cursor, "ShortestLanguage")));
				wordA.setWord(DatabaseHelper.getString(cursor, "ShortestWord"));
				wordA.setWordId(DatabaseHelper.getString(cursor, "ShortestWordId"));
				statistics.setShortestWord(wordA);
				
				return statistics;
			}
			
		} finally {
			if(db!= null) {
				db.close();
			}
		}
		
		return null;
	}
}
