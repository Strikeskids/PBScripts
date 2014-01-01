package org.logicail.rsbot.scripts.logartisanarmourer.jobs.burialarmour;

import org.logicail.rsbot.scripts.framework.tasks.impl.AnimationMonitor;
import org.logicail.rsbot.scripts.logartisanarmourer.LogArtisanArmourer;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.ArtisanArmourerTask;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.swords.MakeSword;
import org.logicail.rsbot.scripts.logartisanarmourer.wrapper.Mode;
import org.powerbot.script.lang.BasicNamedQuery;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Tile;

import java.util.concurrent.Callable;

public class SmithAnvil extends ArtisanArmourerTask {
	public static final int WIDGET_INSTRUCTION = 1073;
	public static final int WIDGET_INSTRUCTION_CHILD = 11;
	private static final int[] ID_ANVIL = {4046};
	private static final int[] ID_ANVIL_SWORD = {4047, 24664, 24677, 24678, 15520, 20258};
	private static final int[] ID_ANVIL_TRACK = {24820};
	private int animationTimelimit;

	public SmithAnvil(LogArtisanArmourer script) {
		super(script);
		animationTimelimit = Random.nextInt(8000, 16000);
	}

	private int getMakeNextId() {
		String text = ctx.widgets.get(WIDGET_INSTRUCTION, WIDGET_INSTRUCTION_CHILD).getText();
		if (text.equals("Helm")) {
			return 20572 + script.options.ingotType.ordinal() - 1 + (20 * script.options.ingotGrade.ordinal());
		} else if (text.equals("Boots")) {
			return 20577 + script.options.ingotType.ordinal() - 1 + (20 * script.options.ingotGrade.ordinal());
		} else if (text.equals("Chestplate")) {
			return 20582 + script.options.ingotType.ordinal() - 1 + (20 * script.options.ingotGrade.ordinal());
		} else if (text.equals("Gauntlets")) {
			return 20587 + script.options.ingotType.ordinal() - 1 + (20 * script.options.ingotGrade.ordinal());
		}
		return 20572 + script.options.ingotType.ordinal() - 1 + (20 * script.options.ingotGrade.ordinal());
	}

	public String getCategoryName() {
		if (script.options.mode == Mode.BURIAL_ARMOUR) {
			switch (script.options.ingotGrade) {
				case ONE:
					return "Miner's Burial Armour";
				case TWO:
					return "Warrior's Burial Armour";
				case THREE:
					return "Smith's Burial Armour";
				default:
					return "Miner's Burial Armour";
			}
		}
		return "Ingots, Tier IV";
	}

	private int[] getAnvilId() {
		switch (script.options.mode) {
			case BURIAL_ARMOUR:
				return ID_ANVIL;
			case CEREMONIAL_SWORDS:
				return ID_ANVIL_SWORD;
			case REPAIR_TRACK:
				return ID_ANVIL_TRACK;
		}
		return ID_ANVIL_TRACK;
	}

	public static Tile anvilLocation = null;

	public void clickAnvil() {
		if (ctx.skillingInterface.isOpen()) {
			return;
		}

		if (ctx.skillingInterface.isProductionInterfaceOpen()) {
			ctx.skillingInterface.cancelProduction();
		}

		BasicNamedQuery<GameObject> anvils = null;
		if (anvilLocation != null) {
			anvils = ctx.objects.select().id(getAnvilId()).nearest(anvilLocation).first();
		}
		if (anvils == null || anvils.isEmpty()) {
			anvils = ctx.objects.select().id(getAnvilId()).nearest().first();
		}

		for (final GameObject anvil : anvils) {
			if (ctx.camera.prepare(anvil)) {
				script.options.status = "Clicking on anvil";
				if (anvil.interact("Smith", "Anvil")) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return anvil.getLocation().distanceTo(ctx.players.local()) < 2;
						}
					}, 300, 8);
					sleep(300, 1000);
					if (ctx.players.local().getAnimation() != -1 && !ctx.skillingInterface.isOpen()) {
						if (anvil.interact("Smith", "Anvil")) {
							sleep(200, 600);
						}
					}

					if (!Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							if (ctx.skillingInterface.isOpen() || MakeSword.get().isOpen()) {
								return true;
							}
							if (ctx.skillingInterface.isProductionInterfaceOpen()) {
								ctx.skillingInterface.cancelProduction();
							}
							return false;
						}
					}, 300, 6)) {
						anvil.interact("Smith", "Anvil");
					}
					sleep(200, 600);
				}
			}
		}
	}

	@Override
	public String toString() {
		return "Smith Anvil";
	}

	@Override
	public boolean activate() {
		if (super.activate() && ctx.widgets.get(WIDGET_INSTRUCTION, WIDGET_INSTRUCTION_CHILD).isValid()) {
			if (!ctx.backpack.select().id(script.getIngotID()).isEmpty()) {
				if (ctx.skillingInterface.isOpen() || !script.options.isSmithing
						|| !script.options.currentlyMaking.equals(ctx.widgets.get(WIDGET_INSTRUCTION, WIDGET_INSTRUCTION_CHILD).getText())
						|| AnimationMonitor.timeSinceAnimation(LogArtisanArmourer.ANIMATION_SMITHING) > animationTimelimit) {
					return true;
				}
				//}
			}
		}
		return false;
	}

	@Override
	public void run() {
		if (ctx.skillingInterface.getAction().equals("Smith")) {
			//System.out.println("Make: " + getMakeNextId());
			final String make = ctx.widgets.get(WIDGET_INSTRUCTION, WIDGET_INSTRUCTION_CHILD).getText();
			if (ctx.skillingInterface.select(getCategoryName(), getMakeNextId())) {
				final int target = ctx.backpack.select().id(getMakeNextId()).count() + ctx.skillingInterface.getQuantity();
				if (ctx.skillingInterface.getSelectedItem().getId() == getMakeNextId() && ctx.skillingInterface.start()) {
					script.options.isSmithing = true;
					animationTimelimit = Random.nextInt(8000, 16000);
					script.options.currentlyMaking = make;
					if (!make.equals(ctx.widgets.get(WIDGET_INSTRUCTION, WIDGET_INSTRUCTION_CHILD).getText())) {
						script.options.isSmithing = false;
						return;
					}
					script.options.status = "Smithing " + script.options.currentlyMaking;

				/*if (Random.nextInt(0, 5) == 0) {
					sleep(500, 3000);
					Util.mouseOffScreen();
				}*/

					// 250 sec for burial
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return ctx.isPaused() || ctx.isShutdown()
									|| ctx.backpack.select().id(getMakeNextId()).count() >= target
									|| AnimationMonitor.timeSinceAnimation(LogArtisanArmourer.ANIMATION_SMITHING) > 4000
									|| (script.options.mode == Mode.BURIAL_ARMOUR && !script.options.currentlyMaking.equals(make));
						}
					}, 600, script.options.mode == Mode.BURIAL_ARMOUR ? 420 : 100);
					sleep(200, 1000);
					script.options.isSmithing = false;

/*
				if (Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !ctx.skillingInterface.isOpen() && ctx.players.local().getAnimation() != -1;
					}
				})) {
					for (int id : LogArtisanArmourer.ANIMATION_SMITHING) {
						AnimationMonitor.put(id);
					}
				}*/
				}
			}
		} else {
			script.options.isSmithing = false;
			if (ctx.skillingInterface.isOpen() && ctx.skillingInterface.close()) {
				return;
			}
			if (ctx.skillingInterface.isProductionInterfaceOpen()) {
				ctx.skillingInterface.cancelProduction();
				return;
			}

			clickAnvil();
		}
	}
}
