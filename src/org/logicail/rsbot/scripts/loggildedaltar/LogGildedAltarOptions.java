package org.logicail.rsbot.scripts.loggildedaltar;

import com.eclipsesource.json.JsonObject;
import org.logicail.rsbot.scripts.loggildedaltar.wrapper.Offering;
import org.powerbot.script.AbstractScript;
import org.powerbot.script.methods.Summoning;

import java.io.*;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/12/13
 * Time: 21:58
 */
public class LogGildedAltarOptions {
	public volatile long TimeLastOffering;

	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.add("offering", offering.name());
		json.add("lightBurners", lightBurners);
		if (useBOB) {
			json.add("useBOB", useOtherHouse);
			json.add("usedBOB", usedBOB.get());
			json.add("beastOfBurden", beastOfBurden.name());
			json.add("onlyHouseObelisk", onlyHouseObelisk);
		}
		if(useOtherHouse) {
			json.add("useOtherHouse", useOtherHouse);
			json.add("detectHouses", detectHouses);
			json.add("stopOffering", stopOffering);
		}
		json.add("banking", banking.get());
		json.add("stopLevelEnabled", stopLevelEnabled);
		json.add("stopLevel", stopLevel);
		json.add("bonesOffered", bonesOffered.get());
		return json;
	}

	/* Settings */
	public boolean lightBurners = true;
	public boolean useBOB = false;
	public AtomicBoolean usedBOB = new AtomicBoolean(false); // Used BOB this trip
	public Summoning.Familiar beastOfBurden = Summoning.Familiar.BULL_ANT;
	public Offering offering = Offering.IMPIOUS_ASHES;
	public boolean useOtherHouse = false;
	public boolean detectHouses = false;
	//public volatile boolean screenshots = false;
	public boolean stopOffering = true;
	public boolean onlyHouseObelisk = false;
	//public boolean useAura = false;
	//public static MyAuras.Aura aura = MyAuras.Aura.CORRUPTION;
	/* Move to bank delegation */
	public volatile AtomicBoolean banking = new AtomicBoolean(false);
	public boolean bobonce = false;
	public boolean stopLevelEnabled = false;
	public int stopLevel;
	public String status = "";
	public AtomicInteger bonesOffered = new AtomicInteger();
	public AtomicBoolean newVersionAvailable = new AtomicBoolean(false);
	private File settingsFile;
	private final String FILENAME = "settings.ini";

	public LogGildedAltarOptions(AbstractScript script) {
		settingsFile = new File(script.getStorageDirectory(), FILENAME);
	}

	public Properties load() {
		final Properties settings = new Properties();
		FileReader fr = null;
		try {
			fr = new FileReader(settingsFile);
			settings.load(fr);
		} catch (FileNotFoundException ignored) {
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return settings;
	}

	public void save(Properties properties) {
		FileWriter fw = null;
		try {
			fw = new FileWriter(settingsFile);
			properties.store(fw, "Settings for LogGildedaltar");
		} catch (Exception ignored) {
		} finally {
			if (fw != null) {
				try {
					fw.flush();
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
