package org.logicail.api.methods;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/13
 * Time: 15:46
 */
public class IOUtil {
	public static String read(final InputStream is, final int bufferSize) {
		final char[] buffer = new char[bufferSize];
		final StringBuilder out = new StringBuilder();
		try {
			try (Reader in = new InputStreamReader(is, "UTF-8")) {
				while (true) {
					int read = in.read(buffer, 0, buffer.length);
					if (read < 0) {
						break;
					}
					out.append(buffer, 0, read);
				}
			}
		} catch (IOException ex) {
			return null;
		}
		return out.toString();
	}
}