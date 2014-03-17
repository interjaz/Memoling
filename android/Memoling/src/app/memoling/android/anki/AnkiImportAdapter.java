package app.memoling.android.anki;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import app.memoling.android.adapter.MemoAdapter.Sort;
import app.memoling.android.anki.entity.AnkiCard;
import app.memoling.android.anki.entity.AnkiNote;
import app.memoling.android.anki.entity.AnkiReviewLog;
import app.memoling.android.db.DatabaseHelper;
import app.memoling.android.db.DatabaseHelper.Order;

public class AnkiImportAdapter extends AnkiDb {

	public AnkiImportAdapter(Context context, String databaseName, int version,
			boolean persistant) {
		super(context, databaseName, version, persistant);
		// TODO Auto-generated constructor stub
	}

	public ArrayList<AnkiReviewLog> getAllAnkiReviewLogs(int ankiDeckId, Sort sort, Order order) {
		SQLiteDatabase db = null;

		try {
			db = getDatabase();
			return getAllAnkiReviewLogs(this, db, ankiDeckId, sort, order);
		} finally {
			closeDatabase();
		}
	}
	
	private ArrayList<AnkiReviewLog> getAllAnkiReviewLogs(
			AnkiImportAdapter ankiImportAdapter, SQLiteDatabase db,
			int ankiDeckId, Sort sort, Order order) {

		ArrayList<AnkiReviewLog> ankiReviewLogs = new ArrayList<AnkiReviewLog>();
		
		String query = "SELECT RL.id RL_id, RL.cid RL_cid, RL.usn RL_usn, RL.ease RL_ease, RL.ivl RL_ivl," 
				+ "RL.lastIvl RL_lastIvl, RL.factor RL_factor, RL.time RL_time, RL.type RL_type" 
				+ "FROM revlog AS RL";
		
		Cursor cursor = db.rawQuery(query, null);
		
		try {
			while (cursor.moveToNext()) {
				AnkiReviewLog ankiReviewLog = new AnkiReviewLog();
				ankiReviewLog.setReviewLogId(DatabaseHelper.getInt(cursor, "RL_id"));
				ankiReviewLog.setCardId(DatabaseHelper.getInt(cursor, "RL_cid"));
				ankiReviewLog.setUniversalSerialNumber(DatabaseHelper.getInt(cursor, "RL_usn"));
				ankiReviewLog.setEase(DatabaseHelper.getInt(cursor, "RL_ease"));
				ankiReviewLog.setInterval(DatabaseHelper.getInt(cursor, "RL_ivl"));
				ankiReviewLog.setLastInterval(DatabaseHelper.getInt(cursor, "RL_lastIvl"));
				ankiReviewLog.setDifficulty(DatabaseHelper.getInt(cursor, "RL_factor"));
				ankiReviewLog.setTime(DatabaseHelper.getInt(cursor, "RL_time"));
				ankiReviewLog.setType(DatabaseHelper.getInt(cursor, "RL_type"));
				ankiReviewLogs.add(ankiReviewLog);
			}
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return ankiReviewLogs;
	}

	public ArrayList<AnkiNote> getAllAnkiNotes(int ankiDeckId, Sort sort, Order order) {
		SQLiteDatabase db = null;

		try {
			db = getDatabase();
			return getAllAnkiNotes(this, db, ankiDeckId, sort, order);
		} finally {
			closeDatabase();
		}
	}
	
	private ArrayList<AnkiNote> getAllAnkiNotes(
			AnkiImportAdapter ankiImportAdapter, SQLiteDatabase db,
			int ankiDeckId, Sort sort, Order order) {
		
		ArrayList<AnkiNote> ankiNotes = new ArrayList<AnkiNote>();
		
		String query = "SELECT N.id N_id, N.guid N_guid, N.mid N_mid, N.mod N_mod, N.usn N_usn, " 
				+ "N.tags N_tags, N.flds N_flds, N.sfld N_sfld, N.csum N_csum, N.flags N_flags, N.data N_data" 
				+ "FROM notes AS N";
		
		Cursor cursor = db.rawQuery(query, null);
		
		try {
			while (cursor.moveToNext()) {
				AnkiNote ankiNote = new AnkiNote();
				ankiNote.setNoteId(DatabaseHelper.getInt(cursor, "N_nid"));
				ankiNote.setGuid(DatabaseHelper.getString(cursor, "N_guid"));
				ankiNote.setMid(DatabaseHelper.getInt(cursor, "N_mid"));
				ankiNote.setLastModification(DatabaseHelper.getDate(cursor, "N_mod"));
				ankiNote.setUniversalSerialNumber(DatabaseHelper.getInt(cursor, "N_usn"));
				ankiNote.setTags(DatabaseHelper.getString(cursor, "N_tags"));				
				ankiNote.setFlds(DatabaseHelper.getString(cursor, "N_flds"));
				ankiNote.setSfld(DatabaseHelper.getString(cursor, "N_sfld"));
				ankiNote.setChecksum(DatabaseHelper.getInt(cursor, "N_csum"));
				ankiNote.setFlags(DatabaseHelper.getInt(cursor, "N_flags"));
				ankiNote.setData(DatabaseHelper.getString(cursor, "N_data"));
				ankiNotes.add(ankiNote);
			}
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return ankiNotes;
	}

	public ArrayList<AnkiCard> getAllAnkiCards(int ankiDeckId, Sort sort, Order order) {
		SQLiteDatabase db = null;

		try {
			db = getDatabase();
			return getAllAnkiCards(this, db, ankiDeckId, sort, order);
		} finally {
			closeDatabase();
		}
	}

	private ArrayList<AnkiCard> getAllAnkiCards(AnkiImportAdapter ankiImportAdapter,
			SQLiteDatabase db, int ankiDeckId, Sort sort, Order order) {
		
		ArrayList<AnkiCard> ankiCards = new ArrayList<AnkiCard>();
		
		String query = "SELECT C.id C_id, C.nid C_nid, C.did C_did, C.ord C_ord, C.mod C_mod, C.usn C_usn, " 
				+ "C.type C_type, C.queue C_queue, C.due C_due, C.ivl C_ivl, C.factor C_factor, C.reps C_reps, " 
				+ "C.lapses C_lapses, C.left C_left, C.odue C_odue, C.odid C_odid, C.flags C_flags, C.data C_data" 
				+ "FROM cards AS C";
		
		Cursor cursor = db.rawQuery(query, null);
		
		try {
			while (cursor.moveToNext()) {
				AnkiCard ankiCard = new AnkiCard();
				ankiCard.setCardId(DatabaseHelper.getInt(cursor, "C_id"));
				ankiCard.setNoteId(DatabaseHelper.getInt(cursor, "C_nid"));
				ankiCard.setDeckId(DatabaseHelper.getInt(cursor, "C_did"));
				ankiCard.setOrd(DatabaseHelper.getInt(cursor, "C_ord"));
				ankiCard.setLastModification(DatabaseHelper.getDate(cursor, "C_mod"));
				ankiCard.setUniversalSerialNumber(DatabaseHelper.getInt(cursor, "C_usn"));
				ankiCard.setType(DatabaseHelper.getInt(cursor, "C_type"));
				ankiCard.setQueue(DatabaseHelper.getInt(cursor, "C_queue"));
				ankiCard.setDue(DatabaseHelper.getInt(cursor, "C_due"));
				ankiCard.setInterval(DatabaseHelper.getInt(cursor, "C_ivl"));
				ankiCard.setDifficulty(DatabaseHelper.getInt(cursor, "C_factor"));
				ankiCard.setNumberAllAnswers(DatabaseHelper.getInt(cursor, "C_reps"));
				ankiCard.setNumberWrongAnswers(DatabaseHelper.getInt(cursor, "C_lapses"));
				ankiCard.setLeft(DatabaseHelper.getInt(cursor, "C_left"));
				ankiCard.setOdue(DatabaseHelper.getInt(cursor, "C_odue"));
				ankiCard.setOdid(DatabaseHelper.getInt(cursor, "C_odid"));
				ankiCard.setFlags(DatabaseHelper.getInt(cursor, "C_flags"));
				ankiCard.setData(DatabaseHelper.getString(cursor, "C_data"));
				ankiCards.add(ankiCard);
			}
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return ankiCards;
	}
}
