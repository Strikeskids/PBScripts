package org.logicail.rsbot.scripts.loggildedaltar.tasks.altar;

import org.logicail.rsbot.scripts.framework.tasks.Branch;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.altar.burners.LightBurners;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.altar.burners.ReturnToBank;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.altar.burners.WaitForBurners;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.astar.Room;
import org.powerbot.script.lang.BasicNamedQuery;
import org.powerbot.script.wrappers.GameObject;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 05/01/14
 * Time: 16:54
 */
public class AltarLightBurnersTask extends Branch<LogGildedAltar> {
	public static final int[] UNLIT_LANTERN = {13208, 13210, 13212};
	public static final int[] LIT_LANTERN = {13209, 13211, 13213};

	public AltarLightBurnersTask(LogGildedAltar script) {
		super(script);

		if (script.options.lightBurners.get()) {
			add(new LightBurners(this));
		}
		if (script.options.useOtherHouse.get()) {
			add(new WaitForBurners(this));
		}
		if (script.options.lightBurners.get()) {
			add(new ReturnToBank(this));
		}
	}

	@Override
	public boolean branch() {
		final GameObject altar = script.altarTask.getAltar();
		if (altar.isValid()) {
			final Room room = script.roomStorage.getRoom(altar);
			return !room.getGameObjectsInRoom(UNLIT_LANTERN).isEmpty();
		}

		return false;
	}

	public BasicNamedQuery<GameObject> getUnlitLanterns(Room room) {
		return room.getGameObjectsInRoom(AltarLightBurnersTask.UNLIT_LANTERN);
	}
}
