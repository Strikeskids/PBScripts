package org.logicail.rsbot.scripts.logartisanarmourer.jobs.burialarmour;

import org.logicail.rsbot.scripts.framework.tasks.impl.AnimationMonitor;
import org.logicail.rsbot.scripts.logartisanarmourer.LogArtisanWorkshop;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.ArtisanArmourerTask;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.swords.MakeSword;
import org.logicail.rsbot.scripts.logartisanarmourer.wrapper.Mode;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.GameObject;

import java.util.concurrent.Callable;

public class SmithAnvil extends ArtisanArmourerTask {
	public static final int WIDGET_INSTRUCTION = 1073;
	public static final int WIDGET_INSTRUCTION_CHILD = 11;
	private static final int[] ID_ANVIL = {4046};
	private static final int[] ID_ANVIL_SWORD = {4047, 24664, 24677, 24678, 15520, 20258};
	private static final int[] ID_ANVIL_TRACK = {24820};
	private MakeSword makeSword;
	private int animationTimelimit;

	public SmithAnvil(LogArtisanWorkshop script) {
		this(script, null);
	}

	public SmithAnvil(LogArtisanWorkshop logArtisanWorkshop, MakeSword makeSword) {
		super(logArtisanWorkshop);
		animationTimelimit = Random.nextInt(8000, 16000);
		setMakeSword(makeSword);
	}

	public void setMakeSword(MakeSword makeSword) {
		this.makeSword = makeSword;
	}

	@Override
	public String toString() {
		return "Smith Anvil";
	}

	private int[] getAnvilId() {
		switch (options.mode) {
			case BURIAL_ARMOUR:
				return ID_ANVIL;
			case CEREMONIAL_SWORDS:
				return ID_ANVIL_SWORD;
			case REPAIR_TRACK:
				return ID_ANVIL_TRACK;
		}
		return ID_ANVIL_TRACK;
	}

	@Override
	public boolean valid() {
		if (makeSword == null) {
			script.log.severe("SmithAnvil.makeSword is null!");
			return false;
		}
		if (super.valid() && ctx.widgets.component(WIDGET_INSTRUCTION, WIDGET_INSTRUCTION_CHILD).valid()) {
			if (!ctx.backpack.select().id(options.getIngotId()).isEmpty()) {
				if (ctx.skillingInterface.isOpen() || !options.isSmithing
						|| !options.currentlyMaking.equals(ctx.widgets.component(WIDGET_INSTRUCTION, WIDGET_INSTRUCTION_CHILD).text())
						|| AnimationMonitor.timeSinceAnimation(LogArtisanWorkshop.ANIMATION_SMITHING) > animationTimelimit) {
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
			final String make = ctx.widgets.component(WIDGET_INSTRUCTION, WIDGET_INSTRUCTION_CHILD).text();
			if (ctx.skillingInterface.select(getCategoryIndex(), getMakeNextId())) {
				final int target = ctx.backpack.select().id(getMakeNextId()).count() + ctx.skillingInterface.getQuantity();
				if (ctx.skillingInterface.getSelectedItem().id() == getMakeNextId() && ctx.skillingInterface.start()) {
					options.isSmithing = true;
					animationTimelimit = Random.nextInt(8000, 16000);
					options.currentlyMaking = make;
					if (!make.equals(ctx.widgets.component(WIDGET_INSTRUCTION, WIDGET_INSTRUCTION_CHILD).text())) {
						options.isSmithing = false;
						return;
					}
					options.status = "Smithing " + options.currentlyMaking;

				/*if (Random.nextInt(0, 5) == 0) {
					sleep(500, 3000);
					Util.mouseOffScreen();
				}*/

					if (Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							for (int i : LogArtisanWorkshop.ANIMATION_SMITHING) {
								if (ctx.players.local().animation() == i) {
									return true;
								}
							}
							return false;
						}
					})) {
						for (int id : LogArtisanWorkshop.ANIMATION_SMITHING) {
							AnimationMonitor.put(id);
						}
						Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return ctx.controller().isStopping() || ctx.controller().isSuspended() || !options.isSmithing
										|| ctx.backpack.select().id(getMakeNextId()).count() >= target
										|| AnimationMonitor.timeSinceAnimation(LogArtisanWorkshop.ANIMATION_SMITHING) > 4000
										|| (options.followInstructions && options.mode == Mode.BURIAL_ARMOUR && !options.currentlyMaking.equals(ctx.widgets.component(WIDGET_INSTRUCTION, WIDGET_INSTRUCTION_CHILD).text()));
							}
						}, 600, options.mode == Mode.BURIAL_ARMOUR ? 420 : 100);
						// 250 sec for burial
						sleep(300);
					}

					options.isSmithing = false;

/*
				if (Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !ctx.skillingInterface.isOpen() && ctx.players.local().getAnimation() != -1;
					}
				})) {
					for (int id : LogArtisanWorkshop.ANIMATION_SMITHING) {
						AnimationMonitor.put(id);
					}
				}*/
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

			clickAnvil();
		}
	}

	private Tile anvilLocation = null;

	public void clickAnvil() {
		if (ctx.skillingInterface.isOpen()) {
			return;
		}

		if (ctx.skillingInterface.isProductionInterfaceOpen()) {
			ctx.skillingInterface.cancelProduction();
		}

		// Sometimes pick a random anvil from the 2 closest
		// Use the one at the previous location
		// Otherwise pick a random anvil from the 2 closest
		final GameObject anvil = Random.nextInt(0, 10) == 0
				? ctx.objects.select().id(getAnvilId()).nearest().limit(2).shuffle().poll()
				: anvilLocation != null
				? ctx.objects.select().id(getAnvilId()).at(anvilLocation).poll()
				: ctx.objects.select().id(getAnvilId()).nearest().limit(2).shuffle().poll();

		if (anvil.valid()) {
			if (anvilLocation == null) {
				anvilLocation = anvil.tile();
			}
			if (ctx.camera.prepare(anvil)) {
				options.status = "Clicking on anvil";
				if (anvil.interact("Smith", "Anvil")) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return anvil.tile().distanceTo(ctx.players.local()) < 2;
						}
					}, 300, 8);
					sleep(350);
					if (ctx.players.local().animation() != -1 && !ctx.skillingInterface.isOpen()) {
						if (anvil.interact("Smith", "Anvil")) {
							sleep(250);
						}
					}

					if (!Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							if (ctx.skillingInterface.isOpen() || makeSword.isOpen()) {
								return true;
							}
							if (ctx.skillingInterface.isProductionInterfaceOpen()) {
								ctx.skillingInterface.cancelProduction();
							}
							return false;
						}
					}, Random.nextInt(300, 600), Random.nextInt(8, 13))) {
						sleep(100);
						if (!ctx.skillingInterface.isOpen() && !makeSword.isOpen()) {
							anvil.interact("Smith", "Anvil");
						}
					}
					sleep(200);
				}
			}
		}
	}

	public int getCategoryIndex() {
		if (options.mode == Mode.BURIAL_ARMOUR) {
			switch (options.ingotGrade) {
				case ONE:
					return 0;
				case TWO:
					return 1;
				case THREE:
					return 2;
			}
		}
		return 0;
	}

	private int getMakeNextId() {
		String text = ctx.widgets.component(WIDGET_INSTRUCTION, WIDGET_INSTRUCTION_CHILD).text();
		if (text.equals("Helm")) {
			return 20572 + options.ingotType.ordinal() - 1 + (20 * options.ingotGrade.ordinal());
		} else if (text.equals("Boots")) {
			return 20577 + options.ingotType.ordinal() - 1 + (20 * options.ingotGrade.ordinal());
		} else if (text.equals("Chestplate")) {
			return 20582 + options.ingotType.ordinal() - 1 + (20 * options.ingotGrade.ordinal());
		} else if (text.equals("Gauntlets")) {
			return 20587 + options.ingotType.ordinal() - 1 + (20 * options.ingotGrade.ordinal());
		}
		return 20572 + options.ingotType.ordinal() - 1 + (20 * options.ingotGrade.ordinal());
	}
}
