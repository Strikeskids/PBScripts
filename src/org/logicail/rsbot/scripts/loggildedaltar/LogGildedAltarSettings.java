package org.logicail.rsbot.scripts.loggildedaltar;

import org.powerbot.script.methods.Summoning;

import java.io.*;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/12/13
 * Time: 21:58
 */
public class LogGildedAltarSettings {
	/* Settings */
	public static boolean setupFinished = false;
	public static boolean lightBurners = true;
	public static boolean useBOB = false;
	public static boolean usedBOB = false; // Used BOB this trip
	public static Summoning.Familiar beastOfBurden = Summoning.Familiar.BULL_ANT;
	//public static Offering offering = Offering.IMPIOUS_ASHES;
	public static boolean useOtherHouse = false;
	public static boolean detectHouses = false;
	public static boolean customMouseSpeed = false;
	public static int mousespeed = 2;
	public static boolean screenshots = false;
	public static boolean stopOffering = true;
	public static boolean onlyHouseObelisk = false;
	public static boolean useAura = false;
	//public static MyAuras.Aura aura = MyAuras.Aura.CORRUPTION;
	/* Move to bank delegation */
	public static boolean banking = false;
	public static boolean bobonce = false;
	public static boolean stopLevelEnabled = false;
	public static int stopLevel;
	private static final String FILENAME = "settings.ini";

	public static void save(Properties properties, File file) {
		FileWriter fw = null;
		try {
			fw = new FileWriter(file);
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

	public static Properties load(File file) {
		final Properties settings = new Properties();
		FileReader fr = null;
		try {
			fr = new FileReader(file);
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
}
