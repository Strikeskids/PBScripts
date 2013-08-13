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
public class LogArtisanArmourerOptions {
	public boolean killAncestors = false;
	public boolean repairPipes = false;
	public Mode mode = Mode.BURIAL_ARMOUR;
	public boolean isSmithing;
	public IngotType ingotType = IngotType.IRON;
	public IngotGrade ingotGrade = IngotGrade.ONE;
	public boolean gotPlan;
	public int failedConsecutiveWithdrawals;
	public int currentlyMaking;
	public boolean finishedSword;

	public int getIngotID() {
		return 20632 + (ingotGrade.ordinal() * 5) + ingotType.ordinal() - 1 + ((ingotGrade.ordinal() >= IngotGrade.FOUR.ordinal()) ? 1 : 0);
	}

	public int getSmelter() {
		return mode == Mode.BURIAL_ARMOUR ? 29395 : 29394;
	}

	@Override
	public String toString() {
		return "LogArtisanArmourerOptions{" +
				"killAncestors=" + killAncestors +
				", repairPipes=" + repairPipes +
				", mode=" + mode +
				", isSmithing=" + isSmithing +
				", ingotType=" + ingotType +
				", ingotGrade=" + ingotGrade +
				", gotPlan=" + gotPlan +
				", failedConsecutiveWithdrawals=" + failedConsecutiveWithdrawals +
				", currentlyMaking=" + currentlyMaking +
				", finishedSword=" + finishedSword +
				'}';
	}
}
