package app.memoling.android.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipHelper {

	public static byte[] compress(byte[] input) throws IOException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream(
				input.length);

		try {
			GZIPOutputStream gzipStream = new GZIPOutputStream(outStream);
			try {
				gzipStream.write(input);
			} finally {
				gzipStream.close();
			}
		} finally {
			outStream.close();
		}

		return outStream.toByteArray();
	}

	public static byte[] decompress(byte[] input) throws IOException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		ByteArrayInputStream inStream = new ByteArrayInputStream(input);

		try {
			GZIPInputStream gzipStream = new GZIPInputStream(inStream);
			try {
				byte[] buffer = new byte[8000];
				int read = 0;
				while ((read = gzipStream.read(buffer, 0, buffer.length)) > 0) {
					outStream.write(buffer, 0, read);
				}

			} finally {
				gzipStream.close();
			}
		} finally {
			inStream.close();
			outStream.close();
		}

		return outStream.toByteArray();
	}

}
