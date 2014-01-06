package org.logicail.rsbot.scripts.loggildedaltar;

import org.logicail.rsbot.scripts.loggildedaltar.wrapper.Offering;
import org.powerbot.script.AbstractScript;
import org.powerbot.script.methods.Summoning;

import java.io.*;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/12/13
 * Time: 21:58
 */
public class LogGildedAltarOptions {
	public long TimeLastOffering;

	/* Settings */
	public boolean setupFinished = false;
	public boolean lightBurners = true;
	public boolean useBOB = false;
	public boolean usedBOB = false; // Used BOB this trip
	public Summoning.Familiar beastOfBurden = Summoning.Familiar.BULL_ANT;
	public Offering offering = Offering.IMPIOUS_ASHES;
	public boolean useOtherHouse = false;
	public boolean detectHouses = false;
	//public boolean screenshots = false;
	public boolean stopOffering = true;
	public boolean onlyHouseObelisk = false;
	public boolean useAura = false;
	//public static MyAuras.Aura aura = MyAuras.Aura.CORRUPTION;
	/* Move to bank delegation */
	public boolean banking = false;
	public boolean bobonce = false;
	public boolean stopLevelEnabled = false;
	public int stopLevel;
	public String status = "";
	public int bonesOffered;
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
