package app.memoling.android.anki;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;
import app.memoling.android.Config;
import app.memoling.android.anki.service.AnkiImporter;
import app.memoling.android.entity.Memo;
import app.memoling.android.helper.AppLog;
import app.memoling.android.sync.file.ConflictResolve.OnConflictResolveHaltable;
import app.memoling.android.sync.file.SupervisedSync.OnSyncComplete;

public class AnkiIOEngine {

	// there should be always only one database to be imported
	private static String importDatabaseName;
	private static int importDatabaseVersion;
	
	private static File tmpFile = null;
	
	public static void removeFile(String path) {
		if(tmpFile != null) {
			tmpFile.delete();			
			tmpFile = null;
		}
	}
	
	public static void cleanAfterImporting() {
		setImportDatabaseName(null);
	}
	
	public static void unpackFile(String path) throws IOException {
		// imported file
		File importedFile = new File(path);
		
		// get file name
		String fileName = importedFile.getName();
		
		// split it with dots
		String[] fileParts = fileName.split("\\.");
		
		// check the last one if it is apkg
		if(!(fileParts != null && fileParts.length > 1 && fileParts[fileParts.length - 1].equals("apkg"))) {
			AppLog.e("AnkiParser.unpackFile", "Exception caused by improper file name");
			// TODO throw custom Exception
			throw new IOException();
		} 
		
		// stream for reading
		BufferedInputStream is = null;
		// stream for writing
		BufferedOutputStream os = null;
		try{
			// file entry
			ZipEntry ze;
			// initialize input stream with *.apkg
			is = new BufferedInputStream(new FileInputStream(importedFile));
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
						int fileLength = originalFilenameSplitted.length;
						
						if(originalFilenameSplitted != null && fileLength > 1) {
							if(originalFilenameSplitted[fileLength - 1].equals("anki")) {
								setImportDatabaseVersion(0);
							} else if(originalFilenameSplitted[fileLength - 1].equals("anki2")) {
								setImportDatabaseVersion(1);
							}
							setImportDatabaseName(originalFilenameSplitted[fileLength - 2]);							
							
							// prepare a file with known filename in db catalogue
							tmpFile = new File(Config.AppPath + "/db/" + getImportDatabaseName() + ".sqlite");
							// initialize the output stream with file location 
							os = new BufferedOutputStream(new FileOutputStream(tmpFile));		
							// read file
							byte[] buffer = new byte[4096];
							int read = 0;
							while ((read = zis.read(buffer, 0, buffer.length)) > 0) {
								os.write(buffer, 0, read);
							}
						} else {
							AppLog.e("AnkiParser.unpackFile", "Exception caused by improper unpacked file name");
							// TODO throw custom Exception
							throw new IOException();
						}
					}
				}
			} finally {
				zis.close();
			}
		} catch (IOException ex) {
			AppLog.e("AnkiParser.unpackFile", "Exception while extracting", ex);
			throw ex;
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
				throw ex;
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