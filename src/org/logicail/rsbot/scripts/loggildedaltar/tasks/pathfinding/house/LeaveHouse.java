package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.house;

import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.LogGildedAltarTask;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 05/01/14
 * Time: 16:37
 */
public class LeaveHouse extends LogGildedAltarTask {
	public AtomicBoolean leaveHouse = new AtomicBoolean();

	public LeaveHouse(LogGildedAltar script) {
		super(script);
	}

	@Override
	public boolean isValid() {
		return leaveHouse.get();
	}

	@Override
	public void run() {
		if (script.houseTask.isInHouse()) {
			script.houseTask.leaveHouse();
			if (script.houseTask.isInHouse()) {
				leaveHouse.set(false);
			}
		} else {
			leaveHouse.set(false);
		}
	}
}
