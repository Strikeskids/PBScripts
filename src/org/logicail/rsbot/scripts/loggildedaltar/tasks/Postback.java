package org.logicail.rsbot.scripts.loggildedaltar.tasks;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.ParseException;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.util.IOUtil;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 08/01/14
 * Time: 16:42
 */
public class Postback extends LogGildedAltarTask {
	private static final int INTERVAL = 20 * 60 * 1000; // 20 mins
	private static final String POSTBACK_URL = "http://www.logicail.co.uk/data.php";
	private static final String IV = "czJtyQDAHvVd426X";
	private static final String SECRET_KEY = "WMsaZg3PmrhESxwB";
	private static final IvParameterSpec IV_PARAMETER_SPEC = new IvParameterSpec(IV.getBytes());
	private static final SecretKeySpec SECRET_KEY_SPEC = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
	private Cipher cipher = null;
	private long previousTimeRunning;
	private int previousXPGained;
	private int previousBonesBuried;
	private long nextRun = System.currentTimeMillis() + INTERVAL;

	public Postback(LogGildedAltar script) {
		super(script);

		try {
			cipher = Cipher.getInstance("AES/CBC/NoPadding");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}

	private static String padString(String source) {
		char paddingChar = ' ';
		int size = 16;
		int x = source.length() % size;
		int padLength = size - x;

		for (int i = 0; i < padLength; i++) {
			source += paddingChar;
		}

		return source;
	}

	private static String bytesToHex(byte[] data) {
		if (data == null) {
			return null;
		}

		StringBuilder str = new StringBuilder();
		for (byte aData : data) {
			if ((aData & 0xFF) < 16)
				str.append("0").append(Integer.toHexString(aData & 0xFF));
			else
				str.append(Integer.toHexString(aData & 0xFF));
		}

		return str.toString();
	}

	private byte[] encrypt(String text) {
		if (text == null || text.length() == 0)
			return null;

		byte[] encrypted = null;

		try {
			cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY_SPEC, IV_PARAMETER_SPEC);
			encrypted = cipher.doFinal(padString(text).getBytes());
		} catch (Exception ignored) {
		}

		return encrypted;
	}

	@Override
	public String toString() {
		return "Postback";
	}

	@Override
	public boolean valid() {
		return cipher != null && nextRun < System.currentTimeMillis();
	}

	@Override
	public void run() {
		if (cipher != null && nextRun < System.currentTimeMillis()) {
			nextRun = System.currentTimeMillis() + INTERVAL;
			postback();
		}
	}

	public synchronized void postback() {
		if (cipher == null) {
			return;
		}

		// TODO: Compress

		JsonObject json = new JsonObject();
		json.add("script", script.getName());
		json.add("userid", Integer.parseInt(ctx.original.properties.get("user.id")));
		json.add("username", ctx.original.properties.get("user.name"));
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

		script.log.info("Postback: " + json.toString());

		final String data = "data=" + bytesToHex(encrypt(json.toString()));
		script.log.info("Enc: " + data);
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(POSTBACK_URL).openConnection();

			connection.addRequestProperty("User-Agent", ctx.useragent);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Length", "" + Integer.toString(data.getBytes().length));
			connection.setRequestProperty("Connection", "close");

			connection.setConnectTimeout(3000);
			connection.setReadTimeout(3000);

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			//Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(data);
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
