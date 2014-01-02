package org.logicail.rsbot.scripts.logartisanarmourer.jobs.burialarmour;

import org.logicail.rsbot.scripts.framework.tasks.impl.AnimationMonitor;
import org.logicail.rsbot.scripts.logartisanarmourer.LogArtisanWorkshop;
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

	public static Tile anvilLocation = null;
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

	@Override
	public boolean activate() {
		if (makeSword == null) {
			script.log.severe("SmithAnvil.makeSword is null!");
			return false;
		}
		if (super.activate() && ctx.widgets.get(WIDGET_INSTRUCTION, WIDGET_INSTRUCTION_CHILD).isValid()) {
			if (!ctx.backpack.select().id(options.getIngotId()).isEmpty()) {
				if (ctx.skillingInterface.isOpen() || !options.isSmithing
						|| !options.currentlyMaking.equals(ctx.widgets.get(WIDGET_INSTRUCTION, WIDGET_INSTRUCTION_CHILD).getText())
						|| AnimationMonitor.timeSinceAnimation(LogArtisanWorkshop.ANIMATION_SMITHING) > animationTimelimit) {
					return true;
				}
				//}
			}
		}
		return false;
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
	public void run() {
		if (ctx.skillingInterface.getAction().equals("Smith")) {
			//System.out.println("Make: " + getMakeNextId());
			final String make = ctx.widgets.get(WIDGET_INSTRUCTION, WIDGET_INSTRUCTION_CHILD).getText();
			if (ctx.skillingInterface.select(getCategoryIndex(), getMakeNextId())) {
				final int target = ctx.backpack.select().id(getMakeNextId()).count() + ctx.skillingInterface.getQuantity();
				if (ctx.skillingInterface.getSelectedItem().getId() == getMakeNextId() && ctx.skillingInterface.start()) {
					options.isSmithing = true;
					animationTimelimit = Random.nextInt(8000, 16000);
					options.currentlyMaking = make;
					if (!make.equals(ctx.widgets.get(WIDGET_INSTRUCTION, WIDGET_INSTRUCTION_CHILD).getText())) {
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
								if (ctx.players.local().getAnimation() == i) {
									return true;
								}
							}
							return false;
						}
					})) {
						for (int id : LogArtisanWorkshop.ANIMATION_SMITHING) {
							AnimationMonitor.put(id);
						}
					}

					// 250 sec for burial
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return ctx.isPaused() || ctx.isShutdown() || !options.isSmithing
									|| ctx.backpack.select().id(getMakeNextId()).count() >= target
									|| AnimationMonitor.timeSinceAnimation(LogArtisanWorkshop.ANIMATION_SMITHING) > 4000
									|| (options.mode == Mode.BURIAL_ARMOUR && !options.currentlyMaking.equals(ctx.widgets.get(WIDGET_INSTRUCTION, WIDGET_INSTRUCTION_CHILD).getText()));
						}
					}, 600, options.mode == Mode.BURIAL_ARMOUR ? 420 : 100);
					sleep(200, 1000);
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
				options.status = "Clicking on anvil";
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
							if (ctx.skillingInterface.isOpen() || makeSword.isOpen()) {
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
		String text = ctx.widgets.get(WIDGET_INSTRUCTION, WIDGET_INSTRUCTION_CHILD).getText();
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
