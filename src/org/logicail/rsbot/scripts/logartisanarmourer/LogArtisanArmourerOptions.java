package org.logicail.rsbot.scripts.logartisanarmourer;

import org.logicail.rsbot.scripts.logartisanarmourer.wrapper.IngotGrade;
import org.logicail.rsbot.scripts.logartisanarmourer.wrapper.IngotType;
import org.logicail.rsbot.scripts.logartisanarmourer.wrapper.Mode;

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
}
