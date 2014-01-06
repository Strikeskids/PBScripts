package org.logicail.rsbot.scripts.loggildedaltar.tasks;

import org.logicail.rsbot.scripts.framework.tasks.Node;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltarOptions;
import org.powerbot.script.lang.ItemQuery;
import org.powerbot.script.wrappers.Item;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 03/01/14
 * Time: 16:10
 */
public abstract class LogGildedAltarTask extends Node<LogGildedAltar> {
	protected final LogGildedAltarOptions options;

	public LogGildedAltarTask(LogGildedAltar script) {
		super(script);
		options = script.options;
	}

	public ItemQuery<Item> getBackpackOffering() {
		return ctx.backpack.select().id(options.offering.getId()).shuffle();
	}
}
