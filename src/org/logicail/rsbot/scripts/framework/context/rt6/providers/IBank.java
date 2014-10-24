package org.logicail.rsbot.scripts.framework.context.rt6.providers;

import org.logicail.rsbot.scripts.framework.context.rt6.IClientContext;
import org.powerbot.script.Condition;
import org.powerbot.script.rt6.Bank;
import org.powerbot.script.rt6.Component;
import org.powerbot.script.rt6.Item;
import org.powerbot.script.rt6.ItemQuery;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 10/01/14
 * Time: 14:37
 */
public class IBank extends Bank {
	public static final int WIDGET_BANK_ITEMS = 7;
	private static final int WIDGET_BOUNDS = 12;

	/**
	 * Backpack when bank is open
	 */
	public final IItemStore backpack;

	public IBank(IClientContext ctx) {
		super(ctx);
		backpack = new IItemStore(ctx, ctx.widgets.component(WIDGET, WIDGET_BANK_ITEMS));
	}

	public org.powerbot.script.rt6.Component getWidget() {
		return ctx.widgets.component(WIDGET, WIDGET_BOUNDS);
	}

	public ArrayList<ArrayList<Item>> getBankTabs() {
		if (!ctx.bank.opened() || (ctx.bank.currentTab() != 0 && !ctx.bank.currentTab(0))) {
			return null;
		}

		ArrayList<ArrayList<Item>> tabs = new ArrayList<ArrayList<Item>>();
		for (BankTab tab : BankTab.values()) {
			final ItemQuery<Item> items = getItemsInTab(tab);
			final int count = items.count();
			if (count == 0) {
				break;
			}

			ArrayList<Item> list = new ArrayList<Item>(count);
			for (Item item : items) {
				list.add(item);
			}

			tabs.add(list);
		}

		return tabs;
	}

	public int getNumberOfTabs() {
		int count = 0;
		for (int i : getTabCount()) {
			if (i == 0) {
				break;
			}
			count++;
		}

		return count;
	}

	private static final int[][] TAB_COUNT = {
			{108, 0}, {108, 10}, {108, 20},
			{109, 0}, {109, 10}, {109, 20},
			{110, 0}, {110, 10},
	};

	public ItemQuery<Item> getItemsInTab(BankTab tab) {
		final int[] count = getTabCount();

		switch (tab) {
			case ONE:
				int total = 0;
				for (Integer i : count) {
					total += i;
				}
				return skip(select(), total);
			default:
				int skip = 0;
				int take = 0;
				int x = 0;

				while (x < count.length) {
					take = count[x];
					if (x == tab.getI()) {
						break;
					}
					skip += count[x];
					x++;
				}

				return skip(select(), skip).limit(take);
		}
	}

	public static ItemQuery<Item> skip(ItemQuery<Item> query, int count) {
		for (int i = 0; i < count; i++) {
			query.poll();
		}

		return query;
	}

	private int[] getTabCount() {
		int[] count = new int[TAB_COUNT.length];

		for (int i = 0; i < TAB_COUNT.length; i++) {
			int[] setting = TAB_COUNT[i];
			count[i] = ctx.varpbits.varpbit(setting[0], setting[1], 0x3ff);
		}

		return count;
	}

	public boolean withdrawBoB(int id, int amount) {
		Component container = ctx.widgets.component(WIDGET, COMPONENT_CONTAINER_ITEMS);
		final Item item = select().id(id).poll();
		if (!container.valid() || !item.valid() || !ctx.summoning.summoned()) {
			return false;
		}

		final Component component = item.component();
		if (component.relativePoint().y == 0 && !this.currentTab(0) && Condition.wait(new Condition.Check() {
			@Override
			public boolean poll() {
				return component.relativePoint().y != 0;
			}
		}, 100, 10)) {
			return false;
		}
		Rectangle rectangle = container.viewportRect();
		if (!(rectangle.contains(component.viewportRect()) || ctx.widgets.scroll(component, ctx.widgets.component(WIDGET, COMPONENT_SCROLL_BAR), rectangle.contains(ctx.input.getLocation())))) {
			return false;
		}
		final int count = item.stackSize();
		if (component.interact("Withdraw-X to BoB") && Condition.wait(new Condition.Check() {
			@Override
			public boolean poll() {
				return isInputOpen();
			}
		})) {
			Condition.sleep();
			ctx.input.sendln("" + amount + "");
		}
		return Condition.wait(new Condition.Check() {
			@Override
			public boolean poll() {
				return item.stackSize() != count;
			}
		});
	}

	private boolean isInputOpen() {
		return ctx.widgets.component(1469, 2).visible();
	}

	public enum BankTab {
		ONE(-1, 142),
		TWO(0, 150),
		THREE(1, 158),
		FOUR(2, 166),
		FIVE(3, 174),
		SIX(4, 182),
		SEVEN(5, 190),
		EIGHT(6, 198),
		NINE(7, 206);
		private final int i;
		private final int bankWidget;

		BankTab(int i, int bankWidget) {
			this.i = i;
			this.bankWidget = bankWidget;
		}

		public boolean exists(IClientContext ctx) {
			if (this == ONE) {
				return true;
			}
			return ctx.bank.getNumberOfTabs() >= ordinal();
		}

		public Component getWidget(IClientContext ctx) {
			return ctx.widgets.component(Bank.WIDGET, bankWidget);
		}

		public int getI() {
			return i;
		}
	}
}
