package org.logicail.rsbot.scripts.loggildedaltar.tasks;

import org.logicail.rsbot.scripts.framework.tasks.Branch;
import org.logicail.rsbot.scripts.framework.tasks.Task;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.NodePath;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.Path;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.RechargeSummoning;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.burthorpe.BurthorpeRecharge;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.canifis.CanifisRecharge;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.castlewars.CastleWarsRecharge;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.tzhaarcity.MainPlazaRecharge;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding.yanille.YanilleRecharge;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Tile;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/12/13
 * Time: 21:52
 */
public class SummoningTask extends Branch<LogGildedAltar> {
	public int nextPoints = -1;

	@Override
	public String toString() {
		return "SummoningTask";
	}

	public SummoningTask(LogGildedAltar script) {
		super(script);

		boolean edgeville = false;

		for (Task<LogGildedAltar> node : script.bankingTask.getNodes()) {
			if (!(node instanceof NodePath)) {
				continue;
			}

			switch (((NodePath) node).getPath()) {
				case EDGEVILLE_LODESTONE:
				case EDGEVILLE_AMULET_OF_GLORY:
				case EDGEVILLE_MOUNTED_AMULET_OF_GLORY:
					if (!edgeville) {
						edgeville = true;
						add(new RechargeSummoning(script, Path.EDGEVILLE_OBELISK, new Tile[]{new Tile(3073, 3504, 0), new Tile(3077, 3504, 0), new Tile(3081, 3504, 0),
								new Tile(3085, 3504, 0), new Tile(3089, 3504, 0), new Tile(3093, 3504, 0),
								new Tile(3097, 3504, 0), new Tile(3101, 3504, 0), new Tile(3105, 3505, 0),
								new Tile(3109, 3506, 0), new Tile(3112, 3507, 0), new Tile(3114, 3511, 0),
								new Tile(3115, 3515, 0), new Tile(3119, 3516, 0), new Tile(3123, 3516, 0),
								new Tile(3126, 3516, 0), new Tile(3127, 3516, 0)}));
					}
					break;
				case CASTLE_WARS_RING_OF_DUELING:
					add(new CastleWarsRecharge(script));
					break;
				case LUNAR_ISLE_LODESTONE:
					add(new RechargeSummoning(script, Path.LUNAR_ISLE_OBELISK));
					break;
				case BURTHORPE_LODESTONE:
					add(new BurthorpeRecharge(script));
					break;
				case YANILLE_BANK_WALK:
					add(new YanilleRecharge(script));
					break;
				case FIGHT_CAVES:
					add(new MainPlazaRecharge(script));
					break;
				case CANIFIS:
					add(new CanifisRecharge(script));
					break;
			}
		}
	}

	@Override
	public boolean branch() {
		if (!script.options.onlyHouseObelisk
				&& script.options.banking
				&& script.options.useBOB
				&& !ctx.bank.isOpen()
				&& (ctx.summoning.getTimeLeft() <= 300 || !ctx.summoning.isFamiliarSummoned())) {
			if (nextPoints == -1) {
				nextPoints = Random.nextInt(script.options.beastOfBurden.getRequiredPoints() + 1, script.options.beastOfBurden.getRequiredPoints() * 2);
			}
			return ctx.summoning.getSummoningPoints() < nextPoints;
		}

		return false;
	}
}
