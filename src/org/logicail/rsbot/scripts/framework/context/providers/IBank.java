package org.logicail.rsbot.scripts.framework.context.providers;

import org.logicail.rsbot.scripts.framework.context.IMethodContext;
import org.powerbot.script.lang.ItemQuery;
import org.powerbot.script.methods.Bank;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Item;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 10/01/14
 * Time: 14:37
 */
public class IBank extends Bank {
	public static final int WIDGET_BANK_ITEMS = 54;
	private static final int WIDGET_BOUNDS = 48;

	/**
	 * Backpack when bank is open
	 */
	public final IItemStore backpack;

	public IBank(IMethodContext ctx) {
		super(ctx);
		backpack = new IItemStore(ctx, ctx.widgets.get(WIDGET, WIDGET_BANK_ITEMS));
	}

	public Component getWidget() {
		return ctx.widgets.get(WIDGET, WIDGET_BOUNDS);
	}


	public ArrayList<ArrayList<Item>> getBankTabs() {
		if (!ctx.bank.isOpen() || (ctx.bank.getCurrentTab() != 0 && !ctx.bank.setCurrentTab(0))) {
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


	private static final int[][] TAB_COUNT = {
			{108, 0}, {108, 10}, {108, 20},
			{109, 0}, {109, 10}, {109, 20},
			{110, 0}, {110, 10},
	};

	public ItemQuery<Item> getItemsInTab(BankTab tab) {
		final int[] count = getTabCount();

		switch (tab) {
			case NONE:
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

				System.out.println();
				System.out.println("Skip " + skip);
				System.out.println("Take " + take);

				return skip(select(), skip).limit(take);
		}
	}

	private ItemQuery<Item> skip(ItemQuery<Item> query, int count) {
		for (int i = 0; i < count; i++) {
			query.poll();
		}

		return query;
	}

	private int[] getTabCount() {
		int[] count = new int[TAB_COUNT.length];

		for (int i = 0; i < TAB_COUNT.length; i++) {
			int[] setting = TAB_COUNT[i];
			count[i] = ctx.settings.get(setting[0], setting[1], 0x3ff);
		}

		return count;
	}

	public enum BankTab {
		NONE(-1), TWO(0), THREE(1), FOUR(2), FIVE(3), SIX(4), SEVEN(5), EIGHT(6);
		private final int i;

		BankTab(int i) {
			this.i = i;
		}

		public int getI() {
			return i;
		}
	}
}
