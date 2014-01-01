package org.logicail.rsbot.scripts.logartisanarmourer;

import org.logicail.rsbot.scripts.logartisanarmourer.wrapper.IngotGrade;
import org.logicail.rsbot.scripts.logartisanarmourer.wrapper.IngotType;
import org.logicail.rsbot.scripts.logartisanarmourer.wrapper.Mode;
import org.powerbot.script.wrappers.Area;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 01/01/14
 * Time: 17:01
 */
public class LogArtisanArmourerOptions {
	/* Statistics */
	public boolean isSmithing;
	public int swordsSmithed;
	public int brokenSwords;
	public boolean gotPlan = true;
	public int perfectSwords;
	public int completedTracks;
	public boolean finishedSword = false;

	/* Settings */
	public IngotType ingotType = IngotType.IRON;
	public IngotGrade ingotGrade = IngotGrade.ONE;
	public boolean respectAncestors;
	public boolean respectRepairPipes;
	public int ingotsSmithed;
	public String status = "Setting up...";
	public String currentlyMaking = "";
	public Mode mode = Mode.BURIAL_ARMOUR;
	public int failedConsecutiveWithdrawals;

	public int getIngotId() {
		if (mode == Mode.REPAIR_TRACK) {
			return ingotType.getIngotIdRepairTracks();
		}
		return ingotType.getID(ingotGrade);
	}

	public Area getAreaSmall() {
		switch (mode) {
			case BURIAL_ARMOUR:
				return LogArtisanArmourer.AREAS_ARTISAN_WORKSHOP_BURIAL;
			case CEREMONIAL_SWORDS:
				return LogArtisanArmourer.AREAS_ARTISAN_WORKSHOP_SWORDS;
			case REPAIR_TRACK:
				return LogArtisanArmourer.AREAS_ARTISAN_WORKSHOP_TRACKS;
			default:
				return LogArtisanArmourer.AREAS_ARTISAN_WORKSHOP_BURIAL;
		}
	}
}
