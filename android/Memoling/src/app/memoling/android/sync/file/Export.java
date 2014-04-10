package app.memoling.android.sync.file;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import app.memoling.android.adapter.MemoAdapter;
import app.memoling.android.adapter.MemoAdapter.Sort;
import app.memoling.android.adapter.MemoBaseAdapter;
import app.memoling.android.db.DatabaseHelper.Order;
import app.memoling.android.entity.Memo;
import app.memoling.android.entity.MemoBase;
import app.memoling.android.helper.AppLog;
import app.memoling.android.helper.Helper;

public class Export {

	public static String exportMemoling(Context context, String[] memoBaseIds) {

		SimpleDateFormat spd = new SimpleDateFormat("yyyyMMddhhmm", Locale.US);
		String path = Helper.AppRoot + "/export_" + spd.format(new Date()) + ".mlg";

		File file = new File(path);
		FileWriter fw = null;

		try {
			MemoBaseAdapter baseAdapter = new MemoBaseAdapter(context);
			MemoAdapter memoAdapter = new MemoAdapter(context);

			fw = new FileWriter(file);

			MemolingFile memoFile = new MemolingFile();
			ArrayList<MemoBase> memoBases = new ArrayList<MemoBase>();
			memoFile.setMemoBases(memoBases);

			for (String memoBaseId : memoBaseIds) {
				MemoBase memoBase = baseAdapter.get(memoBaseId);
				memoBase.setMemos(memoAdapter.getAllDeep(memoBaseId, Sort.CreatedDate, Order.ASC));
				memoBases.add(memoBase);
			}

			fw.write(memoFile.serialize().toString());

		} catch (Exception ex) {
			AppLog.w("Export", "exportMemoling", ex);
			return null;
		} finally {
			try {
				if (fw != null) {
					fw.close();
				}
			} catch (Exception ex) {
				AppLog.e("Export", "exportMemoling - close", ex);
			}

		}

		return path;
	}

	public static String exportCsv(Context context, String[] memoBaseIds) {
		SimpleDateFormat spd = new SimpleDateFormat("yyyyMMddhhmm", Locale.US);
		String path = Helper.AppRoot + "/export_" + spd.format(new Date()) + ".csv";

		File file = new File(path);
		FileWriter fw = null;

		try {
			MemoBaseAdapter baseAdapter = new MemoBaseAdapter(context);
			MemoAdapter memoAdapter = new MemoAdapter(context);

			fw = new FileWriter(file);

			ArrayList<MemoBase> bases = new ArrayList<MemoBase>();
			for (String memoBaseId : memoBaseIds) {
				bases.add(baseAdapter.get(memoBaseId));
			}

			fw.write("Library;Word 1;Word 2;Language 1;Language 2;Correct 1;Correct 2;Displayed\r\n");

			for (MemoBase base : bases) {
				String line;
				ArrayList<Memo> memos = memoAdapter.getAllDeep(base.getMemoBaseId(), Sort.CreatedDate, Order.ASC);

				for (Memo memo : memos) {
					line = CsvParser.MemoToString(base, memo);
					fw.write(line);
				}
			}

		} catch (Exception ex) {
			AppLog.w("Export", "exportCsv", ex);
			return null;
		} finally {
			try {
				if (fw != null) {
					fw.close();
				}
			} catch (Exception ex) {
				AppLog.e("Export", "exportCsv - close", ex);
			}
		}

		return path;
	}

	public static String exportEvernote(Context context, String[] memoBaseIds) {

		StringBuilder noteText = new StringBuilder();

		try {
			MemoBaseAdapter baseAdapter = new MemoBaseAdapter(context);
			MemoAdapter memoAdapter = new MemoAdapter(context);

			ArrayList<MemoBase> bases = new ArrayList<MemoBase>();
			for (String memoBaseId : memoBaseIds) {
				bases.add(baseAdapter.get(memoBaseId));
			}

			for (MemoBase base : bases) {
				String line;
				ArrayList<Memo> memos = memoAdapter.getAllDeep(base.getMemoBaseId(), Sort.CreatedDate, Order.ASC);

				noteText.append(String.format("Library %s\r\n", base.getName()));

				for (Memo memo : memos) {
					line = String.format(Locale.US, "%s (%s) - %s (%s)\r\n", memo.getWordA().getWord(), memo
							.getWordA().getLanguage(), memo.getWordB().getWord(), memo.getWordB().getLanguage());
					noteText.append(line);
				}

			}

		} catch (Exception ex) {
			AppLog.w("Export", "exportEvernote", ex);
			return null;
		}

		return noteText.toString();
	}

}
