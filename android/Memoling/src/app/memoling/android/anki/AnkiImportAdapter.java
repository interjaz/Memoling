package app.memoling.android.anki;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import app.memoling.android.adapter.MemoAdapter.Sort;
import app.memoling.android.anki.entity.AnkiCard;
import app.memoling.android.anki.entity.AnkiCollection;
import app.memoling.android.anki.entity.AnkiGrave;
import app.memoling.android.anki.entity.AnkiIndexStat;
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

	public ArrayList<AnkiCollection> getAllAnkiCollections(Sort sort, Order order) {
		SQLiteDatabase db = null;

		try {
			db = getDatabase();
			return getAllAnkiCollections(this, db, sort, order);
		} finally {
			closeDatabase();
		}
	}	
	
	private ArrayList<AnkiCollection> getAllAnkiCollections(
			AnkiImportAdapter ankiImportAdapter, SQLiteDatabase db,
			Sort sort, Order order) {
		
		ArrayList<AnkiCollection> ankiCollections = new ArrayList<AnkiCollection>();
		
		String query = "SELECT C.id C_id, C.crt C_crt, C.mod C_mod, C.scm C_scm, C.ver C_ver, C.dty C_dty, " 
				+ " C.usn C_usn, C.ls C_ls, C.conf C_conf, C.models C_models, C.decks C_decks, C.dconf C_dconf, "
				+ " C.tags C_tags "
				+ "FROM col AS C";
		
		Cursor cursor = db.rawQuery(query, null);
		
		try {
			while (cursor.moveToNext()) {
				AnkiCollection ankiCollection = new AnkiCollection();
				ankiCollection.setCollectionId(DatabaseHelper.getInt(cursor, "C_id"));
				ankiCollection.setCrt(new Date(DatabaseHelper.getLong(cursor, "C_crt")));
				ankiCollection.setLastModification(new Date(DatabaseHelper.getLong(cursor, "C_mod")));
				ankiCollection.setScm(new Date(DatabaseHelper.getLong(cursor, "C_scm")));
				ankiCollection.setVersion(DatabaseHelper.getInt(cursor, "C_ver"));
				ankiCollection.setDty(DatabaseHelper.getInt(cursor, "C_dty"));
				ankiCollection.setUniversalSerialNumber(DatabaseHelper.getInt(cursor, "C_usn"));
				ankiCollection.setLastSync(new Date(DatabaseHelper.getLong(cursor, "C_ls")));
				ankiCollection.setConfiguration(DatabaseHelper.getString(cursor, "C_conf"));
				ankiCollection.setModels(DatabaseHelper.getString(cursor, "C_models"));
				ankiCollection.setDecks(DatabaseHelper.getString(cursor, "C_decks"));
				ankiCollection.setDefaultConfiguration(DatabaseHelper.getString(cursor, "C_dconf"));
				ankiCollection.setTags(DatabaseHelper.getString(cursor, "C_tags"));
				ankiCollections.add(ankiCollection);
			}
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return ankiCollections;
	}

	public ArrayList<AnkiIndexStat> getAllAnkiIndexStats(Sort sort, Order order) {
		SQLiteDatabase db = null;

		try {
			db = getDatabase();
			return getAllAnkiIndexStats(this, db, sort, order);
		} finally {
			closeDatabase();
		}
	}	
	
	private ArrayList<AnkiIndexStat> getAllAnkiIndexStats(
			AnkiImportAdapter ankiImportAdapter, SQLiteDatabase db,
			Sort sort, Order order) {

		ArrayList<AnkiIndexStat> ankiIndexStats = new ArrayList<AnkiIndexStat>();
		
		String query = "SELECT S.rowid S_rowid, S.tbl S_tbl, S.idx S_idx, S.stat S_stat " 
				+ "FROM sqlite_stat1 AS S";
		
		Cursor cursor = db.rawQuery(query, null);
		
		try {
			while (cursor.moveToNext()) {
				AnkiIndexStat ankiIndexStat = new AnkiIndexStat();
				ankiIndexStat.setRowId(DatabaseHelper.getInt(cursor, "S_rowid"));
				ankiIndexStat.setTable(DatabaseHelper.getString(cursor, "S_tbl"));
				ankiIndexStat.setIndex(DatabaseHelper.getString(cursor, "S_idx"));
				ankiIndexStat.setStatistics(DatabaseHelper.getString(cursor, "S_stat"));
				ankiIndexStats.add(ankiIndexStat);
			}
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return ankiIndexStats;
	}

	public ArrayList<AnkiGrave> getAllAnkiGraves(Sort sort, Order order) {
		SQLiteDatabase db = null;

		try {
			db = getDatabase();
			return getAllAnkiGraves(this, db, sort, order);
		} finally {
			closeDatabase();
		}
	}	
	
	private ArrayList<AnkiGrave> getAllAnkiGraves(
			AnkiImportAdapter ankiImportAdapter, SQLiteDatabase db,
			Sort sort, Order order) {
 
		ArrayList<AnkiGrave> ankiGraves = new ArrayList<AnkiGrave>();
		
		String query = "SELECT G.rowid G_rowid, G.usn G_usn, G.oid G_oid, G.type G_type " 
				+ "FROM graves AS G";
		
		Cursor cursor = db.rawQuery(query, null);
		
		try {
			while (cursor.moveToNext()) {
				AnkiGrave ankiGrave = new AnkiGrave();
				ankiGrave.setGraveId(DatabaseHelper.getInt(cursor, "G_rowid"));
				ankiGrave.setUniversalSerialNumber(DatabaseHelper.getInt(cursor, "G_usn"));
				ankiGrave.setOid(new Date(DatabaseHelper.getLong(cursor, "C_oid")));
				ankiGrave.setType(DatabaseHelper.getInt(cursor, "G_type"));
				ankiGraves.add(ankiGrave);
			}
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return ankiGraves;
	}

	public ArrayList<AnkiReviewLog> getAnkiReviewLogs(long ankiDeckId, Sort sort, Order order) {
		SQLiteDatabase db = null;

		try {
			db = getDatabase();
			return getAnkiReviewLogs(this, db, ankiDeckId, sort, order);
		} finally {
			closeDatabase();
		}
	}
	
	private ArrayList<AnkiReviewLog> getAnkiReviewLogs(
			AnkiImportAdapter ankiImportAdapter, SQLiteDatabase db,
			long ankiDeckId, Sort sort, Order order) {

		ArrayList<AnkiReviewLog> ankiReviewLogs = new ArrayList<AnkiReviewLog>();
		
		String query = "SELECT RL.id RL_id, RL.cid RL_cid, RL.usn RL_usn, RL.ease RL_ease, RL.ivl RL_ivl," 
				+ "RL.lastIvl RL_lastIvl, RL.factor RL_factor, RL.time RL_time, RL.type RL_type " 
				+ "FROM revlog AS RL " 
				+ "WHERE RL.cid = ?";
		
		Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(ankiDeckId)});
		
		try {
			while (cursor.moveToNext()) {
				AnkiReviewLog ankiReviewLog = new AnkiReviewLog();
				ankiReviewLog.setReviewLogId(new Date(DatabaseHelper.getLong(cursor, "RL_id")));
				ankiReviewLog.setCardId(new Date(DatabaseHelper.getLong(cursor, "RL_cid")));
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

	public ArrayList<AnkiReviewLog> getAllAnkiReviewLogs(Sort sort, Order order) {
		SQLiteDatabase db = null;

		try {
			db = getDatabase();
			return getAllAnkiReviewLogs(this, db, sort, order);
		} finally {
			closeDatabase();
		}
	}
	
	private ArrayList<AnkiReviewLog> getAllAnkiReviewLogs(
			AnkiImportAdapter ankiImportAdapter, SQLiteDatabase db,
			Sort sort, Order order) {

		ArrayList<AnkiReviewLog> ankiReviewLogs = new ArrayList<AnkiReviewLog>();
		
		String query = "SELECT RL.id RL_id, RL.cid RL_cid, RL.usn RL_usn, RL.ease RL_ease, RL.ivl RL_ivl," 
				+ "RL.lastIvl RL_lastIvl, RL.factor RL_factor, RL.time RL_time, RL.type RL_type " 
				+ "FROM revlog AS RL";
		
		Cursor cursor = db.rawQuery(query, null);
		
		try {
			while (cursor.moveToNext()) {
				AnkiReviewLog ankiReviewLog = new AnkiReviewLog();
				ankiReviewLog.setReviewLogId(new Date(DatabaseHelper.getLong(cursor, "RL_id")));
				ankiReviewLog.setCardId(new Date(DatabaseHelper.getLong(cursor, "RL_cid")));
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
	
	public ArrayList<AnkiNote> getAllAnkiNotes(Sort sort, Order order) {
		SQLiteDatabase db = null;

		try {
			db = getDatabase();
			return getAllAnkiNotes(this, db, sort, order);
		} finally {
			closeDatabase();
		}
	}
	
	private ArrayList<AnkiNote> getAllAnkiNotes(
			AnkiImportAdapter ankiImportAdapter, SQLiteDatabase db,
			Sort sort, Order order) {
		
		ArrayList<AnkiNote> ankiNotes = new ArrayList<AnkiNote>();
		
		String query = "SELECT N.id N_id, N.guid N_guid, N.mid N_mid, N.mod N_mod, N.usn N_usn, " 
				+ "N.tags N_tags, N.flds N_flds, N.sfld N_sfld, N.csum N_csum, N.flags N_flags, N.data N_data " 
				+ "FROM notes AS N";
		
		Cursor cursor = db.rawQuery(query, null);
		
		try {
			while (cursor.moveToNext()) {
				AnkiNote ankiNote = new AnkiNote();
				ankiNote.setNoteId(new Date(DatabaseHelper.getLong(cursor, "N_id")));
				ankiNote.setGuid(DatabaseHelper.getString(cursor, "N_guid"));
				ankiNote.setMid(new Date(DatabaseHelper.getLong(cursor, "N_mid")));
				ankiNote.setLastModification(new Date(DatabaseHelper.getLong(cursor, "N_mod")));
				ankiNote.setUniversalSerialNumber(DatabaseHelper.getInt(cursor, "N_usn"));
				ankiNote.setTags(DatabaseHelper.getString(cursor, "N_tags"));				
				ankiNote.setFlds(DatabaseHelper.getString(cursor, "N_flds"));
				ankiNote.setSfld(DatabaseHelper.getString(cursor, "N_sfld"));
				ankiNote.setChecksum(DatabaseHelper.getLong(cursor, "N_csum"));
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

	public ArrayList<AnkiCard> getAllAnkiCards(Sort sort, Order order) {
		SQLiteDatabase db = null;

		try {
			db = getDatabase();
			return getAllAnkiCards(this, db, sort, order);
		} finally {
			closeDatabase();
		}
	}

	private ArrayList<AnkiCard> getAllAnkiCards(AnkiImportAdapter ankiImportAdapter,
			SQLiteDatabase db, Sort sort, Order order) {
		
		ArrayList<AnkiCard> ankiCards = new ArrayList<AnkiCard>();
		
		String query = "SELECT C.id C_id, C.nid C_nid, C.did C_did, C.ord C_ord, C.mod C_mod, C.usn C_usn, " 
				+ "C.type C_type, C.queue C_queue, C.due C_due, C.ivl C_ivl, C.factor C_factor, C.reps C_reps, " 
				+ "C.lapses C_lapses, C.left C_left, C.odue C_odue, C.odid C_odid, C.flags C_flags, C.data C_data " 
				+ "FROM cards AS C";
		
		Cursor cursor = db.rawQuery(query, null);
		
		try {
			while (cursor.moveToNext()) {
				AnkiCard ankiCard = new AnkiCard();
				ankiCard.setCardId(new Date(DatabaseHelper.getLong(cursor, "C_id")));
				ankiCard.setNoteId(new Date(DatabaseHelper.getLong(cursor, "C_nid")));
				ankiCard.setDeckId(new Date(DatabaseHelper.getLong(cursor, "C_did")));
				ankiCard.setOrd(DatabaseHelper.getInt(cursor, "C_ord"));
				ankiCard.setLastModification(new Date(DatabaseHelper.getLong(cursor, "C_mod")));
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
	
	public ArrayList<AnkiCard> getAnkiCards(long ankiDeckId, Sort sort, Order order) {
		SQLiteDatabase db = null;

		try {
			db = getDatabase();
			return getAnkiCards(this, db, ankiDeckId, sort, order);
		} finally {
			closeDatabase();
		}
	}

	private ArrayList<AnkiCard> getAnkiCards(AnkiImportAdapter ankiImportAdapter,
			SQLiteDatabase db, long ankiDeckId, Sort sort, Order order) {
		
		ArrayList<AnkiCard> ankiCards = new ArrayList<AnkiCard>();
		
		String query = "SELECT C.id C_id, C.nid C_nid, C.did C_did, C.ord C_ord, C.mod C_mod, C.usn C_usn, " 
				+ "C.type C_type, C.queue C_queue, C.due C_due, C.ivl C_ivl, C.factor C_factor, C.reps C_reps, " 
				+ "C.lapses C_lapses, C.left C_left, C.odue C_odue, C.odid C_odid, C.flags C_flags, C.data C_data " 
				+ "FROM cards AS C " 
				+ "WHERE C.did = ? " 
				+ "ORDER BY C.id";
		
		Cursor cursor = db.rawQuery(query, new String[] {String.valueOf(ankiDeckId)});
		
		try {
			while (cursor.moveToNext()) {
				AnkiCard ankiCard = new AnkiCard();
				ankiCard.setCardId(new Date(DatabaseHelper.getLong(cursor, "C_id")));
				ankiCard.setNoteId(new Date(DatabaseHelper.getLong(cursor, "C_nid")));
				ankiCard.setDeckId(new Date(DatabaseHelper.getLong(cursor, "C_did")));
				ankiCard.setOrd(DatabaseHelper.getInt(cursor, "C_ord"));
				ankiCard.setLastModification(new Date(DatabaseHelper.getLong(cursor, "C_mod")));
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
