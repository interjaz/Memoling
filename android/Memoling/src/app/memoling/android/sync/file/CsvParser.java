package app.memoling.android.sync.file;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import app.memoling.android.entity.Language;
import app.memoling.android.entity.Memo;
import app.memoling.android.entity.MemoBase;
import app.memoling.android.entity.Word;

public class CsvParser {

	public static String MemoToString(MemoBase base, Memo memo) {
		return String.format(Locale.US, "%s;%s;%s;%s;%s;%s;%s;%d;%d;%d\r\n", base.getName(), memo.getWordA().getWord(),
				memo.getWordB().getWord(), memo.getWordA().getDescription(), memo.getWordB().getDescription(), memo
						.getWordA().getLanguage(), memo.getWordB().getLanguage(), memo.getCorrectAnsweredWordA(), memo
						.getCorrectAnsweredWordB(), memo.getDisplayed());
	}

	public static Memo StringToMemo(String string) {
		return StringToMemo(string, null);
	}

	public static Memo StringToMemo(String string, MemoBase base) {

		if (base == null) {
			base = new MemoBase();
			base.setActive(true);
			base.setCreated(new Date());
			base.setMemoBaseId(UUID.randomUUID().toString());
		}

		Memo memo = new Memo();
		memo.setMemoBase(base);
		memo.setMemoBaseId(base.getMemoBaseId());
		memo.setMemoId(UUID.randomUUID().toString());
		memo.setCreated(new Date());
		memo.setActive(true);

		Word wordA = new Word();
		wordA.setWordId(UUID.randomUUID().toString());
		Word wordB = new Word();
		wordB.setWordId(UUID.randomUUID().toString());

		String[] vals = string.split("\\;");

		// Basics
		base.setName(vals[0]);
		wordA.setWord(vals[1]);
		wordB.setWord(vals[2]);
		wordA.setDescription(vals[3]);
		wordB.setDescription(vals[4]);
		wordA.setLanguage(Language.parse(vals[5]));
		wordB.setLanguage(Language.parse(vals[6]));

		memo.setWordA(wordA);
		memo.setWordB(wordB);

		// Ignore if not present
		if (vals.length > 7) {
			memo.setCorrectAnsweredWordA(Integer.parseInt(vals[7]));
			memo.setCorrectAnsweredWordB(Integer.parseInt(vals[8]));
			memo.setDisplayed(Integer.parseInt(vals[9]));
		}

		return memo;
	}

	public static ArrayList<Memo> parseFile(String path) throws IOException {

		FileReader reader = new FileReader(path);
		StringBuilder sb = new StringBuilder();

		char[] buffer = new char[256];
		int read = 0;
		while ((read = reader.read(buffer, 0, buffer.length)) != -1) {
			sb.append(buffer, 0, read);
		}

		ArrayList<Memo> memos = new ArrayList<Memo>();
		ArrayList<MemoBase> bases = new ArrayList<MemoBase>();

		String[] lines = sb.toString().split("\\r?\\n");
		// Skip first line (description);
		for (int i = 1; i < lines.length; i++) {
			String line = lines[i];

			Memo memo = StringToMemo(line);
			memos.add(memo);

			// Make MemoBase subset as small as possible
			MemoBase exisitingBase = null;

			for (MemoBase base : bases) {
				if (memo.getMemoBase().getName().equals(base.getName())) {
					exisitingBase = base;
					break;
				}
			}

			if (exisitingBase != null) {
				memo.setMemoBase(exisitingBase);
				memo.setMemoBaseId(exisitingBase.getMemoBaseId());
			}
		}

		reader.close();

		return memos;
	}

}
