package app.memoling.android.anki;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;
import app.memoling.android.Config;
import app.memoling.android.adapter.MemoBaseAdapter;
import app.memoling.android.anki.entity.AnkiCard;
import app.memoling.android.anki.entity.AnkiDeck;
import app.memoling.android.anki.entity.AnkiNote;
import app.memoling.android.anki.service.AnkiImporter;
import app.memoling.android.entity.Language;
import app.memoling.android.entity.Memo;
import app.memoling.android.entity.MemoBase;
import app.memoling.android.entity.Word;
import app.memoling.android.helper.AppLog;
import app.memoling.android.sync.ConflictResolve.OnConflictResolveHaltable;
import app.memoling.android.sync.SupervisedSync.OnSyncComplete;

public class AnkiIOEngine {

	// there should be always only one database to be imported
	private static String importDatabaseName;
	private static int importDatabaseVersion;
	
	public static void unpackFile(String path) {
		// file with *.apkg
		File m_pkg = new File(path);
		// stream for reading
		BufferedInputStream is = null;
		// stream for writing
		BufferedOutputStream os = null;
		try{
			// file entry
			ZipEntry ze;
			// initialize input stream with *.apkg
			is = new BufferedInputStream(new FileInputStream(m_pkg));
			// initialize zip input stream with input stream containing *.apkg
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));
			try {
				// we want to unpack all files
				while((ze = zis.getNextEntry()) != null) {		
					
					// we already know the filename
					String originalFilename = ze.getName();
					
					// set the imported database name
					if(getImportDatabaseName() == null) {
						String[] originalFilenameSplitted = originalFilename.split("\\.");
						if(originalFilenameSplitted[1].equals("anki")) {
							setImportDatabaseVersion(0);
						} else if(originalFilenameSplitted[1].equals("anki2")) {
							setImportDatabaseVersion(1);
						}
						setImportDatabaseName(originalFilenameSplitted[0]);

						// prepare a file with known filename in db catalogue
						File m_tmp = new File(Config.AppPath + "/db/" + getImportDatabaseName() + ".sqlite");
						// initialize the output stream with file location 
						os = new BufferedOutputStream(new FileOutputStream(m_tmp));		
						// read file
						byte[] buffer = new byte[4096];
						int read = 0;
						while ((read = zis.read(buffer, 0, buffer.length)) > 0) {
							os.write(buffer, 0, read);
						}
					}
				}
			} finally {
				zis.close();
			}
		} catch (IOException ex) {
			AppLog.e("AnkiParser.unpackFile", "Exception while extracting", ex);
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (os != null) {
					os.close();
				}
			} catch (IOException ex) {
				AppLog.e("AnkiParser.unpackFile", "Exception while closing", ex);
			}
		}	
	}
	
	public static void importFile(Context context, String path, 
			final OnConflictResolveHaltable<Memo> onConflictMemo, final OnSyncComplete onComplete) {
		new AnkiImporter(context, path, onConflictMemo, onComplete);
	}

	public static void onAnkiImportComplete() {
		// TODO Auto-generated method stub
		
	}
	
	public static void onAnkiExportComplete() {
		// TODO Auto-generated method stub
		
	}

	public static String getImportDatabaseName() {
		return importDatabaseName;
	}

	public static void setImportDatabaseName(String importDatabaseName) {
		AnkiIOEngine.importDatabaseName = importDatabaseName;
	}

	public static int getImportDatabaseVersion() {
		return importDatabaseVersion;
	}

	public static void setImportDatabaseVersion(int importDatabaseVersion) {
		AnkiIOEngine.importDatabaseVersion = importDatabaseVersion;
	}
}
