package org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks;

import org.logicail.rsbot.scripts.framework.context.rt6.providers.ILodestone;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.IMovement;
import org.logicail.rsbot.scripts.framework.tasks.Branch;
import org.logicail.rsbot.scripts.framework.tasks.Task;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.LogGildedAltarOptions;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.banking.Banking;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.*;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.burthorpe.BurthorpeLodestone;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.canifis.KharyrllPortalRoom;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.daemonheim.DaemonheimKinship;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.edgeville.HouseGlory;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.tzhaarcity.FightCavesBank;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.yanille.YanilleBankWalk;
import org.powerbot.script.Locatable;
import org.powerbot.script.rt6.Equipment;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 03/01/14
 * Time: 16:14
 */
public class BankingTask extends Branch<LogGildedAltar> {
	public final Banking banking;
	protected final LogGildedAltarOptions options;

	public BankingTask(LogGildedAltar script, List<Path> enabledPaths) {
		super(script);
		options = script.options;
		// Always add banking (activate if in bank)
		add(banking = new Banking(script));
		for (Path path : enabledPaths) {
			add(createPath(path));
		}
	}

	private NodePath createPath(Path path) {
		switch (path) {
			case EDGEVILLE_MOUNTED_AMULET_OF_GLORY:
				return new HouseGlory(script);
			case CASTLE_WARS_RING_OF_DUELING:
				return new ItemTeleport(script, path, "Castle Wars", Equipment.Slot.RING, 2566, 2564, 2562, 2560, 2558, 2556, 2554, 2552);
			case LUNAR_ISLE_LODESTONE:
				return new LodestoneTeleport(script, Path.LUNAR_ISLE_LODESTONE, ILodestone.Lodestone.LUNAR_ISLE);
			case BURTHORPE_LODESTONE:
				return new BurthorpeLodestone(script);
			case DAEMONHEIM_RING_OF_KINSHIP:
				return new DaemonheimKinship(script);
			case YANILLE_BANK_WALK:
				return new YanilleBankWalk(script);
			case EDGEVILLE_AMULET_OF_GLORY:
				return new ItemTeleport(script, path, "Edgeville", Equipment.Slot.NECK, 1706, 1708, 1710, 1712);
			case FIGHT_CAVES:
				return new FightCavesBank(script);
			case EDGEVILLE_LODESTONE:
				return new LodestoneTeleport(script, path, ILodestone.Lodestone.EDGEVILLE);
			case CANIFIS:
				return new KharyrllPortalRoom(script);
			//case BURTHORPE_TROLL_INVASION:
			//	return new ItemTeleport(script, path, "Troll Invasion", Equipment.Slot.NECK, 3867, 3865, 3863, 3861, 3859, 3857, 3855, 3853);
		}
		return null;
	}

	@Override
	public String toString() {
		return "";
	}

	@Override
	public boolean branch() {
		return options.banking.get()
				&& (script.summoningTask == null || !script.summoningTask.valid());
	}

	public boolean inBank() {
		if (!LocationAttribute.EDGEVILLE.isInLargeArea(ctx)) {
			final Locatable nearest = ctx.bank.nearest();
			if (IMovement.Euclidean(ctx.players.local(), nearest) < 8) {
				return true;
			}
		}

		for (Task node : tasks) {
			if (node != null && node instanceof NodePath && ((NodePath) node).getPath().getLocation().isInSmallArea(ctx)) {
				return true;
			}
		}
		return false;
	}

	public void setBanking() {
		banking.setBanking(true);
	}
}
