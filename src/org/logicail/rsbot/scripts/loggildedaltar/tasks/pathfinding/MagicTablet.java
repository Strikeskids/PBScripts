package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding;

import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.wrapper.BankRequiredItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 02/09/12
 * Time: 22:38
 */
public abstract class MagicTablet extends NodePath {
	protected final int tabletID;

	protected MagicTablet(LogGildedAltar script, final Path path, int tabletID) {
		super(script, path);
		this.tabletID = tabletID;
	}

	@Override
	protected boolean doLarge() {
		return false;
	}

	@Override
	public List<BankRequiredItem> getItemsNeededFromBank() {
		List<BankRequiredItem> list = new ArrayList<BankRequiredItem>();

		if (ctx.backpack.select().id(tabletID).isEmpty()) {
			list.add(new BankRequiredItem(0, false, null, tabletID));
		}

		return list;
	}

	@Override
	public boolean activate() {
		return getItemsNeededFromBank().isEmpty();
	}
}
