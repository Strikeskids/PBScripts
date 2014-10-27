package org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.ParseException;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.util.IOUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 08/01/14
 * Time: 16:42
 */
public class Postback extends LogGildedAltarTask {
	private static final int INTERVAL = 20 * 60 * 1000; // 20 mins
	private static final String POSTBACK_URL = "http://www.logicail.co.uk/data.php";
	private long previousTimeRunning;
	private int previousXPGained;
	private int previousBonesBuried;
	private long nextRun = System.currentTimeMillis() + INTERVAL;

	public Postback(LogGildedAltar script) {
		super(script);
	}

	@Override
	public String toString() {
		return "Postback";
	}

	@Override
	public boolean valid() {
		return nextRun < System.currentTimeMillis();
	}

	@Override
	public void run() {
		if (nextRun < System.currentTimeMillis()) {
			nextRun = System.currentTimeMillis() + INTERVAL;
			postback();
		}
	}

	public synchronized void postback() {
		JsonObject json = new JsonObject();
		json.add("script", script.getName());
		json.add("userid", Integer.parseInt((String) (ctx.properties.get("user.id"))));
		json.add("username", (String) (ctx.properties.get("user.name")));
		final long timeRunning = script.getRuntime() / 1000;
		json.add("timerunning", timeRunning - previousTimeRunning);
		final int experience = script.experience();
		json.add("xpgained", experience - previousXPGained);
		final int bonesBuried = script.options.bonesOffered.get();
		json.add("bonesburied", bonesBuried - previousBonesBuried);
		json.add("status", script.options.status);
		if (script.options.detectHouses.get()) {
			final OpenHouse currentHouse = script.houseHandler.getCurrentHouse();
			if (currentHouse != null) {
				json.add("currenthouse", currentHouse.getPlayerName());
			}
		}
		//json.add("settings", options.toJson());

		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(POSTBACK_URL).openConnection();

			connection.addRequestProperty("User-Agent", ctx.useragent);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json; charset=utf8");
			connection.setRequestProperty("Connection", "close");

			connection.setConnectTimeout(3000);
			connection.setReadTimeout(3000);

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			//Send request
			OutputStream wr = connection.getOutputStream();
			wr.write(json.toString().getBytes("UTF-8"));
			wr.flush();
			wr.close();

			//Get Response
			try {
				String read = IOUtil.read(connection.getInputStream());
				//script.log.info(read);
				if (read != null) {
					if (read.trim().equalsIgnoreCase("SHUTDOWN")) {
						ctx.stop("Forced shutdown see thread on forum", false);
					} else {
						if (read.length() > 0) {
							try {
								final JsonObject jsonObject = JsonObject.readFrom(read);
								final JsonValue latest_version = jsonObject.get("latest_version");
								if (latest_version != null && latest_version.isNumber()) {
									//final double latest_version_double = latest_version.asDouble();
									//if (latest_version_double > script.getVersion()) {
									//	script.options.newVersionAvailable.set(true);
									//}
								}
							} catch (ParseException e) {
								ctx.controller.script().log.info("Postback: " + e.getMessage());
							}
						}
					}
				}
			} catch (Exception ignored) {
				ignored.printStackTrace();
			}
			previousTimeRunning = timeRunning;
			previousXPGained = experience;
			previousBonesBuried = bonesBuried;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
