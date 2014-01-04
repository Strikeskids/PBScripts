package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding;

import org.logicail.rsbot.scripts.framework.context.providers.Lodestones;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.wrapper.BankRequiredItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 03/01/14
 * Time: 19:28
 */
public class LodestoneTeleport extends NodePath {
	protected final Lodestones.Lodestone lodestone;
	protected long timeLastTeleport = 0;

	public LodestoneTeleport(LogGildedAltar script, Path path, Lodestones.Lodestone lodestone) {
		super(script, path);
		this.lodestone = lodestone;
	}

	@Override
	public List<BankRequiredItem> getItemsNeededFromBank() {
		return new ArrayList<BankRequiredItem>();
	}

	@Override
	protected boolean doLarge() {
		if (ctx.lodestones.teleport(lodestone)) {
			timeLastTeleport = System.currentTimeMillis();
			return true;
		}
		return false;
	}

	@Override
	public boolean activate() {
		return !locationAttribute.isInSmallArea(ctx) && (locationAttribute.isInLargeArea(ctx) || (ctx.lodestones.canUse(lodestone) && System.currentTimeMillis() - timeLastTeleport > 15000));
	}
}
