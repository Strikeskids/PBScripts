package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding;

import org.logicail.rsbot.scripts.framework.context.providers.ILodestone;
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
	protected final ILodestone.Lodestone lodestone;
	protected long timeLastTeleport = 0;

	public LodestoneTeleport(LogGildedAltar script, Path path, ILodestone.Lodestone lodestone) {
		super(script, path);
		this.lodestone = lodestone;
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
	public List<BankRequiredItem> getItemsNeededFromBank() {
		return new ArrayList<BankRequiredItem>();
	}

	@Override
	public boolean valid() {
		return !locationAttribute.isInSmallArea(ctx) && (locationAttribute.isInLargeArea(ctx) || (lodestone.isUnlocked(ctx) && System.currentTimeMillis() - timeLastTeleport > 15000));
	}
}
