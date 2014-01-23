package org.logicail.rsbot.util;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 23/01/14
 * Time: 17:20
 */
public class IPlayerValidator {
	private static final Map<String, Boolean> checkedMap = new ConcurrentHashMap<String, Boolean>();

	private static String clean(String name) {
		return name.trim().toLowerCase().replaceAll(" ", "_");
	}

	public static synchronized boolean isValid(String name) {
		name = clean(name);
		if (checkedMap.containsKey(name)) {
			return checkedMap.get(name);
		}

		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) new URL("http://hiscore.runescape.com/index_lite.ws?player=" + name).openConnection();
			connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.76 Safari/537.36");
			connection.setRequestProperty("Connection", "close");
			connection.setRequestMethod("GET");
			connection.connect();
			connection.setConnectTimeout(3000);
			connection.setReadTimeout(3000);
			checkedMap.put(name, connection.getResponseCode() == 200);
			return checkedMap.get(name);
		} catch (Exception e) {
			//e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

		return false;
	}
}
