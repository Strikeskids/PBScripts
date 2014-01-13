package org.logicail.rsbot.scripts.loggildedaltar;

import com.eclipsesource.json.JsonObject;
import org.logicail.rsbot.scripts.loggildedaltar.wrapper.Offering;
import org.powerbot.script.AbstractScript;
import org.powerbot.script.methods.Summoning;

import java.io.*;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/12/13
 * Time: 21:58
 */
public class LogGildedAltarOptions {
	public final AtomicLong TimeLastOffering = new AtomicLong();
	public final AtomicInteger timesBob = new AtomicInteger();

	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.add("offering", offering.name());
		json.add("lightBurners", lightBurners.get());
		if (useBOB.get()) {
			json.add("useBOB", useOtherHouse.get());
			json.add("usedBOB", usedBOB.get());
			json.add("bobonce", bobonce.get());
			json.add("beastOfBurden", beastOfBurden.name());
			json.add("onlyHouseObelisk", onlyHouseObelisk.get());
		}
		if(useOtherHouse.get()) {
			json.add("useOtherHouse", useOtherHouse.get());
			json.add("detectHouses", detectHouses.get());
			json.add("stopOffering", stopOffering.get());
		}
		json.add("banking", banking.get());
		json.add("stopLevelEnabled", stopLevelEnabled.get());
		json.add("stopLevel", stopLevel.get());
		json.add("bonesOffered", bonesOffered.get());
		return json;
	}

	/* Settings */
	public final AtomicBoolean lightBurners = new AtomicBoolean(true);
	public final AtomicBoolean useBOB = new AtomicBoolean();;
	public final AtomicBoolean usedBOB = new AtomicBoolean(false); // Used BOB this trip
	public Summoning.Familiar beastOfBurden = Summoning.Familiar.BULL_ANT;
	public Offering offering = Offering.IMPIOUS_ASHES;
	public final AtomicBoolean useOtherHouse = new AtomicBoolean();
	public final AtomicBoolean detectHouses = new AtomicBoolean();
	//public volatile boolean screenshots = false;
	public final AtomicBoolean stopOffering = new AtomicBoolean(true);
	public final AtomicBoolean onlyHouseObelisk = new AtomicBoolean();
	//public boolean useAura = false;
	//public static MyAuras.Aura aura = MyAuras.Aura.CORRUPTION;
	/* Move to bank delegation */
	public final AtomicBoolean banking = new AtomicBoolean();
	public final AtomicBoolean bobonce = new AtomicBoolean();
	public final AtomicBoolean onlySummoningPotions = new AtomicBoolean();
	public final AtomicBoolean stopLevelEnabled = new AtomicBoolean();
	public final AtomicInteger stopLevel = new AtomicInteger(99);
	public String status = "";
	public final AtomicInteger bonesOffered = new AtomicInteger();
	public final AtomicBoolean newVersionAvailable = new AtomicBoolean(false);
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
