package org.logicail.rsbot.scripts.loggildedaltar.tasks;

import org.logicail.rsbot.scripts.framework.tasks.Branch;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltarOptions;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.altar.*;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.astar.Room;
import org.powerbot.script.wrappers.GameObject;

import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 05/01/14
 * Time: 16:35
 */
public class AltarTask extends Branch<LogGildedAltar> {
	public static final int[] ALTAR = {13197, 13198, 13199/*, 13188, 13191*/};
	private final LogGildedAltarOptions options;

	public AltarTask(LogGildedAltar script) {
		super(script);
		options = script.options;

		add(new AltarHouseLoading(script));
		add(new AltarNoAltar(script));

		if (options.useBOB) {
			add(new AltarObelisk(script));
		}

		add(new AltarWalkToAltar(script));
		add(new AltarLightBurnersTask(script));

		add(new AltarOfferBones(script));
		add(new AltarOutOfBones(script));
		add(new AltarOffering(script));
	}

	@Override
	public boolean branch() {
		return !options.banking && script.houseTask.isInHouse();
	}

	public GameObject getAltar() {
		return ctx.objects.select().id(ALTAR).sort(new Comparator<GameObject>() {
			@Override
			public int compare(GameObject lhs, GameObject rhs) {
				Room lhsRoom = script.roomStorage.getRoom(lhs);
				Room rhsRoom = script.roomStorage.getRoom(rhs);
				return Integer.compare(lhsRoom.getGameObjectsInRoom(AltarLightBurnersTask.LIT_LANTERN, AltarLightBurnersTask.UNLIT_LANTERN).size(), rhsRoom.getGameObjectsInRoom(AltarLightBurnersTask.LIT_LANTERN, AltarLightBurnersTask.UNLIT_LANTERN).size());
			}
		}).poll();
	}

	public GameObject getMiniObelisk() {
		return ctx.objects.select().id(AltarObelisk.MINI_OBELISK).nearest().poll();
	}

	@Override
	public String toString() {
		return "AltarTask";
	}
}
