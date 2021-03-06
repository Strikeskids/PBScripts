package org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks;

import org.logicail.rsbot.scripts.framework.tasks.Branch;
import org.logicail.rsbot.scripts.framework.tasks.Task;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.LogGildedAltarOptions;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.NodePath;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.Path;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.RechargeSummoning;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.burthorpe.BurthorpeRecharge;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.canifis.CanifisRecharge;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.castlewars.CastleWarsRecharge;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.tzhaarcity.MainPlazaRecharge;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.yanille.YanilleRecharge;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/12/13
 * Time: 21:52
 */
public class SummoningTask extends Branch<LogGildedAltar> {
	protected final LogGildedAltarOptions options;
	public int nextPoints = -1;

	public SummoningTask(LogGildedAltar script) {
		super(script);
		options = script.options;

		if (options.onlySummoningPotions.get()) {
			add(new SummoningPotion(script));
			return;
		}

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
						add(new RechargeSummoning(script, Path.EDGEVILLE_OBELISK, new Tile[]{new Tile(3070, 3503, 0), new Tile(3075, 3503, 0), new Tile(3080, 3503, 0),
								new Tile(3085, 3503, 0), new Tile(3090, 3503, 0), new Tile(3095, 3503, 0),
								new Tile(3100, 3503, 0), new Tile(3105, 3504, 0), new Tile(3110, 3506, 0),
								new Tile(3114, 3509, 0), new Tile(3118, 3511, 0), new Tile(3122, 3514, 0), new Tile(3127, 3516, 0)}));
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
	public String toString() {
		return "";
	}

	@Override
	public boolean branch() {
		if (!script.options.onlyHouseObelisk.get() // TODO: Move house obelisk here
				&& script.options.banking.get()
				&& script.options.useBOB.get()
				//&& !ctx.bank.opened()
				&& (ctx.summoning.timeLeft() <= 300 || !ctx.summoning.summoned())) {
			if (nextPoints == -1) {
				nextPoints = Random.nextInt(script.options.beastOfBurden.requiredPoints() + 1, script.options.beastOfBurden.requiredPoints() * 2);
			}
			return ctx.summoning.points() < nextPoints;
		}

		return false;
	}
}
