package org.logicail.rsbot.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 08/01/14
 * Time: 17:08
 */
public class IOUtil {
	public static String readString(String url, String useragent) {
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) new URL(url).openConnection();
			connection.addRequestProperty("User-Agent", useragent);
			connection.setRequestProperty("Connection", "close");

            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);

			connection.setDoInput(true);
			connection.setDoOutput(false);
			connection.setUseCaches(false);

			return IOUtil.read(connection.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		return "";
	}

	public static String read(final InputStream is) {
		final char[] buffer = new char[2048];
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
			return "";
		}
		return out.toString();
	}
}