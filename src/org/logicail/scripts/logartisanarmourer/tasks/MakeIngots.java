package org.logicail.scripts.logartisanarmourer.tasks;

import org.logicail.api.methods.LogicailMethodContext;
import org.logicail.api.methods.Shutdown;
import org.logicail.api.methods.providers.Condition;
import org.logicail.api.util.ArrayUtil;
import org.logicail.framework.script.state.Node;
import org.logicail.scripts.logartisanarmourer.LogArtisanArmourer;
import org.logicail.scripts.logartisanarmourer.LogArtisanArmourerOptions;
import org.logicail.scripts.logartisanarmourer.tasks.swords.MakeSword;
import org.logicail.scripts.logartisanarmourer.wrapper.Mode;
import org.powerbot.script.wrappers.GameObject;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/13
 * Time: 20:24
 */
public class MakeIngots extends Node {
	private static final String CATEGORY_TIER_I = "Ingots, Tier I";
	private static final String CATEGORY_TIER_II = "Ingots, Tier II";
	private static final String CATEGORY_TIER_III = "Ingots, Tier III";
	private static final String CATEGORY_TIER_IV = "Ingots, Tier IV";
	private final LogArtisanArmourerOptions options;
	private final int[] ingots;

	public MakeIngots(LogicailMethodContext ctx) {
		super(ctx);
		this.options = ((LogArtisanArmourer) ctx.script).options;
		ingots = ArrayUtil.Join(MakeSword.HEATED_INGOTS, options.getIngotID());
	}

	@Override
	public void execute() {
		options.isSmithing = false;

		if (ctx.skillingInterface.getAction().equals("Smelt")) {
			if (options.mode == Mode.CEREMONIAL_SWORDS) {
				if (!ctx.skillingInterface.select(getCategoryName(), options.getIngotID(), ctx.backpack.select().id(MakeSword.TONGS).isEmpty() ? 26 - ctx.backpack.select().count() : -1)) {
					return;
				}
			} else {
				if (!ctx.skillingInterface.select(getCategoryName(), options.getIngotID())) {
					return;
				}
			}

			if (!ctx.skillingInterface.canStart()) {
				options.failedConsecutiveWithdrawals++;
				sleep(1000, 2500);
				if (options.failedConsecutiveWithdrawals >= 3) {
					ctx.skillingInterface.close();
					new Shutdown(ctx).stop("Ran out of ore... stopping!");
				}
			} else {
				options.failedConsecutiveWithdrawals = 0;
				ctx.skillingInterface.start();
				sleep(100, 1000);
			}
		} else {
			if (ctx.skillingInterface.isOpen() && ctx.skillingInterface.close()) {
				return;
			}

			//ArtisanArmourer.setStatus("Search for smelter");

			for (GameObject smelter : ctx.objects.select().id(options.getSmelter()).nearest().first()) {
				if (ctx.interaction.interact(smelter, "Withdraw-ingots", "Smelter")) {
					ctx.waiting.wait(6000, new Condition() {
						@Override
						public boolean validate() {
							return ctx.skillingInterface.isOpen();
						}
					});
					sleep(100, 1000);
				}
			}
		}
	}

	@Override
	public boolean activate() {
		return (ctx.skillingInterface.getAction().equals("Smelt") && !ctx.skillingInterface.select().id(MakeSword.HEATED_INGOTS).isEmpty())
				|| (ctx.backpack.select().id(ingots).isEmpty() && !ctx.backpack.isFull());
	}

	private String getCategoryName() {
		if (options.mode == Mode.BURIAL_ARMOUR) {
			switch (options.ingotGrade) {
				case TWO:
					return CATEGORY_TIER_II;
				case THREE:
					return CATEGORY_TIER_III;
			}
			return CATEGORY_TIER_I;
		}
		return CATEGORY_TIER_IV;
	}
}
