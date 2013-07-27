package org.logicail.scripts.logartisanarmourer;

import org.logicail.scripts.logartisanarmourer.wrapper.IngotGrade;
import org.logicail.scripts.logartisanarmourer.wrapper.IngotType;
import org.logicail.scripts.logartisanarmourer.wrapper.Mode;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/13
 * Time: 16:24
 */
public class Options {
	public boolean killAncestors = false;
	public boolean repairPipes = false;
	public Mode mode = Mode.BURIAL_ARMOUR;
	public boolean isSmithing;
	public IngotType ingotType = IngotType.IRON;
	public IngotGrade ingotGrade = IngotGrade.ONE;
	public boolean gotPlan;
	public int failedConsecutiveWithdrawals;
	public int currentlyMaking;

	public int getIngotID() {
		return 20632 + (ingotGrade.ordinal() * 5) + ingotType.ordinal() - 1 + ((ingotGrade.ordinal() >= IngotGrade.FOUR.ordinal()) ? 1 : 0);
	}
}
