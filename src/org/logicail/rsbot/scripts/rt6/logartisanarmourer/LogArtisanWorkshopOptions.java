package org.logicail.rsbot.scripts.rt6.logartisanarmourer;

import org.logicail.rsbot.scripts.rt6.logartisanarmourer.wrapper.IngotGrade;
import org.logicail.rsbot.scripts.rt6.logartisanarmourer.wrapper.IngotType;
import org.logicail.rsbot.scripts.rt6.logartisanarmourer.wrapper.Mode;
import org.powerbot.script.Area;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 01/01/14
 * Time: 17:01
 */
public class LogArtisanWorkshopOptions {
	/* Statistics */
	public boolean isSmithing;
	public int swordsSmithed;
	public int brokenSwords;
	public AtomicBoolean gotPlan = new AtomicBoolean(true);
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
	public boolean followInstructions = true;

	public Area getAreaSmall() {
		switch (mode) {
			case BURIAL_ARMOUR:
				return LogArtisanWorkshop.AREAS_ARTISAN_WORKSHOP_BURIAL;
			case CEREMONIAL_SWORDS:
				return LogArtisanWorkshop.AREAS_ARTISAN_WORKSHOP_SWORDS;
			case REPAIR_TRACK:
				return LogArtisanWorkshop.AREAS_ARTISAN_WORKSHOP_TRACKS;
			default:
				return LogArtisanWorkshop.AREAS_ARTISAN_WORKSHOP_BURIAL;
		}
	}

	public int getIngotId() {
		if (mode == Mode.REPAIR_TRACK) {
			return ingotType.getIngotIdRepairTracks();
		}
		return ingotType.getID(ingotGrade);
	}
}
