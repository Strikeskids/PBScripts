package org.logicail.api;

import org.logicail.api.methods.IOUtil;
import org.logicail.framework.script.ActiveScript;
import org.logicail.framework.script.job.state.Node;
import org.powerbot.script.methods.Game;
import org.powerbot.script.util.Timer;

import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 24/07/13
 * Time: 21:38
 */
public class Tracking extends Node {
	private final Timer timer = new Timer(5 * 60 * 1000);
	ConcurrentHashMap<String, Serializable> data = new ConcurrentHashMap<>();
	int[] previousExperience = new int[25];
	String cookie = null;

	public Tracking(ActiveScript script) {
		super(script.ctx);
		data.put("Script", script.getName());
		data.put("Version", script.getVersion());

		// Open a new session
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) new URL("http://www.logicail.co.uk/tracking/index.php").openConnection();
			connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.72 Safari/537.36");


			Map<String, List<String>> headerFields = connection.getHeaderFields();
			if (headerFields.containsKey("Set-Cookie")) {
				cookie = headerFields.get("Set-Cookie").toString();
			}

			System.out.println(cookie);

			//Get Response
			String read = IOUtil.read(connection.getInputStream(), 1024);
			System.out.println(read);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	@Override
	public boolean activate() {
		final int state = ctx.game.getClientState();
		return !timer.isRunning()
				&& (state == Game.INDEX_MAP_LOADED || state == Game.INDEX_MAP_LOADING);
	}

	@Override
	public void execute() {

	}
}
