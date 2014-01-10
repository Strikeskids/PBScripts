package org.logicail.rsbot.scripts.loggildedaltar.tasks;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.util.IOUtil;
import org.powerbot.script.methods.Environment;

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
	private static final int INTERVAL = 1200000; // 20 mins
	private static final String POSTBACK_URL = "http://www.logicail.co.uk/data.php";
	private static Cipher cipher = null;
	private static long previousTimeRunning;
	private static int previousXPGained;
	private static int previousBonesBuried;
	private static final String IV = "czJtyQDAHvVd426X";
	private static final String SECRET_KEY = "WMsaZg3PmrhESxwB";
	private static final IvParameterSpec IV_PARAMETER_SPEC = new IvParameterSpec(IV.getBytes());
	private static final SecretKeySpec SECRET_KEY_SPEC = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
	private long nextRun = System.currentTimeMillis() + INTERVAL;

	@Override
	public String toString() {
		return "Postback";
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

	@Override
	public boolean isValid() {
		if (cipher != null && nextRun < System.currentTimeMillis()) {
			nextRun = System.currentTimeMillis() + INTERVAL;
			return true;
		}
		return false;
	}

	@Override
	public void run() {
		postback();
	}

	public synchronized void postback() {
		if (cipher == null) {
			return;
		}

		JsonObject json = new JsonObject();
		json.add("script", script.getName());
		json.add("userid", Environment.getUserId());
		json.add("username", Environment.getDisplayName());
		final long timeRunning = script.getRuntime() / 1000;
		json.add("timerunning", timeRunning - previousTimeRunning);
		final int experience = script.experience();
		json.add("xpgained", experience - previousXPGained);
		final int bonesBuried = options.bonesOffered;
		json.add("bonesburied", bonesBuried);
		json.add("status", options.status);
		if (options.detectHouses) {
			final OpenHouse currentHouse = script.houseHandler.getCurrentHouse();
			if (currentHouse != null) {
				json.add("currenthouse", currentHouse.getPlayerName());
			}
		}

		//ctx.log.info("Postback: " + json.toString());

		final String data = "data=" + bytesToHex(encrypt(json.toString()));
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(POSTBACK_URL).openConnection();

			connection.addRequestProperty("User-Agent", ctx.useragent);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Length", "" + Integer.toString(data.getBytes().length));

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
				String read = IOUtil.read(connection.getInputStream(), 4096);
				ctx.log.info(read);
				if (read != null) {
					if (read.trim().equalsIgnoreCase("SHUTDOWN")) {
						ctx.stop("Forced shutdown see thread on forum", false);
					} else {
						if (read.length() > 0) {
							final JsonObject jsonObject = JsonObject.readFrom(read);
							final JsonValue latest_version = jsonObject.get("latest_version");
							if (latest_version != null && latest_version.isNumber()) {
								final double latest_version_double = latest_version.asDouble();
								if (latest_version_double > script.getVersion()) {
									options.newVersionAvailable = true;
								}
							}
						}
					}
				}
			} catch (Exception ignored) {
			}
			previousTimeRunning = timeRunning;
			previousXPGained = experience;
			previousBonesBuried = bonesBuried;
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	private static byte[] encrypt(String text) {
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
}
