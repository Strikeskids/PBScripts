package com.sk.windows;

import org.logicail.rsbot.scripts.framework.context.IMethodContext;

public enum InnerAbilityTab implements Window {
	ATTACK_ABILITY(MainWindow.MELEE_ABILITIES, 0, "Attack", 4),

	STRENGTH_ABILITY(MainWindow.MELEE_ABILITIES, 1, "Strength", 4),

	RANGED_ABILITY(MainWindow.RANGED_ABILITIES, 0, "Ranged", 0),

	MAGIC_ABILITY(MainWindow.MAGIC_ABILITIES, 0, "Abilities", 20),

	COMBAT_SPELL(MainWindow.MAGIC_ABILITIES, 1, "Combat", 20),

	TELEPORT_SPELL(MainWindow.MAGIC_ABILITIES, 2, "Teleport", 20),

	SKILLING_SPELL(MainWindow.MAGIC_ABILITIES, 1, "Skilling", 20),

	DEFENCE_ABILITY(MainWindow.DEFENCE_ABILITIES, 0, "Defence", 28),

	CONSTITUTION_ABILITY(MainWindow.DEFENCE_ABILITIES, 1, "Constitution", 28);

	private static final int TAB_SETTING = 3705;

	private final String openAction;
	private final MainWindow superWindow;
	private final int component;
	private final int shift;

	private InnerAbilityTab(final MainWindow superWindow, final int component, final String openAction, final int shiftValue) {
		this.superWindow = superWindow;
		this.component = component;
		this.openAction = openAction;
		this.shift = shiftValue;
	}

	public MainWindow getSuperWindow() {
		return superWindow;
	}

	@Override
	public boolean open(final IMethodContext ctx) {
		return isOpen(ctx) || superWindow.open(ctx) && ((ctx.settings.get(TAB_SETTING) >> shift & 0xf) == component || ctx.widgets.get(superWindow.getSource().getWidget(), 7).getChild(6 + component).interact(openAction));
	}

	@Override
	public boolean isOpen(IMethodContext ctx) {
		return superWindow.isOpen(ctx) && (ctx.settings.get(TAB_SETTING) >> shift & 0xf) == component;
	}
}
