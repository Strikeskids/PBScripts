package org.logicail.scripts.logartisanarmourer.tasks.burial;

import org.logicail.api.methods.MyMethodContext;
import org.logicail.api.providers.Condition;
import org.logicail.framework.script.job.state.Node;
import org.logicail.scripts.logartisanarmourer.LogArtisanArmourer;
import org.logicail.scripts.logartisanarmourer.Options;
import org.logicail.scripts.logartisanarmourer.tasks.Anvil;
import org.logicail.scripts.tasks.AnimationHistory;
import org.powerbot.script.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/13
 * Time: 20:24
 */
public class SmithAnvil extends Node {
	public static final int WIDGET_INSTRUCTION = 1073;
	public static final int WIDGET_INSTRUCTION_CHILD = 11;
	private final Options options;
	Anvil anvilHelper;
	private int animationTimelimit = Random.nextInt(8000, 16000);

	public SmithAnvil(MyMethodContext ctx) {
		super(ctx);
		options = LogArtisanArmourer.instance.options;
		anvilHelper = new Anvil(ctx);
	}

	public String getCategoryName() {
		switch (options.ingotGrade) {
			case ONE:
				return "Ingots, Tier I";
			case TWO:
				return "Ingots, Tier II";
			case THREE:
				return "Ingots, Tier III";
		}
		return "Ingots, Tier I";
	}

	private int getMakeNextId() {
		int base = options.ingotType.ordinal() - 1 + (20 * options.ingotGrade.ordinal());
		switch (ctx.widgets.get(WIDGET_INSTRUCTION, WIDGET_INSTRUCTION_CHILD).getText()) {
			case "Helm":
				return 20572 + base;
			case "Boots":
				return 20577 + base;
			case "Chestplate":
				return 20582 + base;
			case "Gauntlets":
				return 20587 + base;
		}
		return 20572 + base;
	}

	@Override
	public String toString() {
		return "Smith Anvil";
	}

	@Override
	public boolean activate() {
		if (ctx.widgets.get(WIDGET_INSTRUCTION, WIDGET_INSTRUCTION_CHILD).isValid()) {
			if (!ctx.backpack.select().id(options.getIngotID()).isEmpty()) {
				if (ctx.skillingInterface.isOpen() || !options.isSmithing
						|| options.currentlyMaking != getMakeNextId()
						|| AnimationHistory.timeSinceAnimation(LogArtisanArmourer.ANIMATION_SMITHING) > animationTimelimit) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void execute() {
		if (ctx.skillingInterface.getAction().equals("Smith")) {
			//System.out.println("Make: " + getMakeNextId());
			int nextId = getMakeNextId();
			if (ctx.skillingInterface.select(getCategoryName(), nextId) && ctx.skillingInterface.start()) {
				options.isSmithing = true;
				animationTimelimit = Random.nextInt(8000, 16000);
				options.currentlyMaking = nextId;

				/*if (Random.nextInt(0, 5) == 0) {
					sleep(500, 3000);
					Util.mouseOffScreen();
				}*/

				if (ctx.waiting.wait(4000, new Condition() {
					@Override
					public boolean validate() {
						return !ctx.skillingInterface.isOpen() && ctx.players.local().getAnimation() != -1;
					}
				})) {
					for (int id : LogArtisanArmourer.ANIMATION_SMITHING) {
						AnimationHistory.put(id);
					}
				}
			}
		} else {
			options.isSmithing = false;
			if (ctx.skillingInterface.isOpen() && ctx.skillingInterface.close()) {
				return;
			}
			if (ctx.skillingInterface.isProductionInterfaceOpen()) {
				ctx.skillingInterface.cancelProduction();
				return;
			}

			anvilHelper.click();
		}
	}
}
