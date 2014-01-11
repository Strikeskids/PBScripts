package org.logicail.rsbot.scripts.loggildedaltar.tasks.banking;

import org.logicail.rsbot.scripts.framework.tasks.Branch;
import org.logicail.rsbot.scripts.framework.tasks.Node;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltarOptions;
import org.logicail.rsbot.util.LogicailArea;
import org.powerbot.script.wrappers.Locatable;
import org.powerbot.script.wrappers.Tile;
import org.powerbot.script.wrappers.TileMatrix;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 21/08/12
 * Time: 19:57
 */
public class Banking extends Branch<LogGildedAltar> {
	public static final int ID_MARRENTIL = 251;
	public static final int[] IDS_RING_OF_DUELLING = new int[]{2566, 2564, 2562, 2560, 2558, 2556, 2554, 2552};
	public static final int[] ALWAYS_DEPOSIT = {
			114, 116, 118, 120, 122, 124, 126, 134, 136, 138, 314, 441, 448, 450, 454, 554, 555, 560, 561, 562, 877,
			882, 884, 886, 1704, 1973, 2327, 2429, 2433, 6961, 6962, 6963, 6965, 14665};
	public AtomicInteger beastOfBurdenWithdraws = new AtomicInteger();
	public AtomicBoolean withdrawnDelegation = new AtomicBoolean();
	public AtomicInteger beastOfBurdenCount = new AtomicInteger();
	public AtomicInteger fail = new AtomicInteger();
	protected final LogGildedAltarOptions options;

	@Override
	public String toString() {
		return "Banking";
	}

	public Banking(LogGildedAltar script) {
		super(script);
		options = script.options;

		//if(options.useBOB) { Don't use since useBob will be disabled if level requirement not met

		add(new BankBob(this));
		add(new BankOpen(this));
		add(new BankWithdraw(this));
	}

	@Override
	public boolean branch() {
		final Locatable nearest = ctx.bank.getNearest();
		if (nearest != Tile.NIL) {
			final Tile location = nearest.getLocation();
			final TileMatrix tileMatrix = location.getMatrix(ctx);
			if (tileMatrix.isOnScreen()) {
				return true;
			}
			final Tile tile = new LogicailArea(location.derive(-2, -2), location.derive(3, 3)).getRandomReachable(ctx, 6);
			return tile.getMatrix(ctx).isValid() && tile.distanceTo(ctx.players.local()) < 10;
		}
		return script.bankingTask.inBank();
	}

	@Override
	public void run() {
		if (fail.incrementAndGet() > 70) {
			StringBuilder reason = new StringBuilder();
			reason.append("Banking error\n\n");
			final Node<LogGildedAltar> state = state();
			reason.append("State: ").append(state).append("\n\n");

			if (state instanceof BankWithdraw) {
				if (options.lightBurners.get()) {
					reason.append(getMarrentilError());
				}
				reason.append(getBonesError());
			}

			ctx.stop(reason.toString());
			return;
		}

		super.run();
	}

	private String getBonesError() {
		int bones = ctx.backpack.select().id(options.offering.getId()).count();
		int bonesBank = ctx.bank.select().id(options.offering.getId()).count(true);

		if (bones + bonesBank < 28) {
			return "Could not find enough \"" + options.offering + "\" [Backpack=" + bones + " Bank=" + bonesBank + "]\n";
		}

		return null;
	}

	private String getMarrentilError() {
		int marrentil = ctx.backpack.select().id(ID_MARRENTIL).count();
		int marrentilBank = ctx.bank.select().id(ID_MARRENTIL).count(true);

		if (marrentil + marrentilBank < 2) {
			return "Could not find enough \"Clean marrentil\" [Backpack=" + marrentil + " Bank=" + marrentilBank + "]\n";
		}

		return null;
	}

	public void setBanking(boolean state) {
		options.banking.set(state);
		if (state) {
			beastOfBurdenWithdraws.set( 0);
			withdrawnDelegation.set(false);
			beastOfBurdenCount.set(0);
		}
	}
}
