package org.logicail.rsbot.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 08/01/14
 * Time: 17:08
 */
public class IOUtil {
	public static String read(final InputStream is, final int bufferSize) {
		final char[] buffer = new char[bufferSize];
		final StringBuilder out = new StringBuilder();
		try {
			Reader in = null;
			try {
				in = new InputStreamReader(is, "UTF-8");
				while (true) {
					int rsz = in.read(buffer, 0, buffer.length);
					if (rsz < 0)
						break;
					out.append(buffer, 0, rsz);
				}
			} finally {
				if (in != null) {
					in.close();
				}
			}
		} catch (IOException ex) {
			return null;
		}
		return out.toString();
	}
}