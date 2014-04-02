package com.sk.methods.action.magic;

import com.sk.methods.action.ability.AbilityStyle;
import com.sk.methods.action.structure.Spell;
import com.sk.windows.InnerAbilityTab;
import com.sk.windows.Window;
import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.powerbot.script.rt6.Component;
import org.powerbot.script.rt6.Skills;

public enum AllSpell implements Spell {
	POLYPORE_STRIKE(InnerAbilityTab.COMBAT_SPELL, 2598, 80, 162, 14396),
	HOME_TELEPORT(InnerAbilityTab.TELEPORT_SPELL, 2491, 0, 155, 14333),;

	private final Rune[] runes;
	private final int childIndex, childTexture, level, id;
	private final Window window;

	private AllSpell(Window window, int id, int level, int childIndex, int childTexture, Rune... runes) {
		this.window = window;
		this.id = id;
		this.level = level;
		this.childIndex = childIndex;
		this.childTexture = childTexture;
		this.runes = runes;
	}

	@Override
	public int getChildIndex() {
		return childIndex;
	}

	@Override
	public int getChildTexture() {
		return childTexture;
	}

	@Override
	public int id() {
		return id;
	}

	@Override
	public Rune[] getRunes() {
		return runes;
	}

	@Override
	public Window getWindow() {
		return window;
	}

	@Override
	public Component getComponent(IClientContext ctx) {
		return ctx.widgets.component(getWidget(), MAIN_COMPONENT).component(getChildIndex());
	}

	@Override
	public Component getCooldownComponent(IClientContext ctx) {
		return ctx.widgets.component(getWidget(), COOLDOWN_COMPONENT).component(getChildIndex());
	}

	@Override
	public int getSkill() {
		return Skills.MAGIC;
	}

	@Override
	public int getSkillLevel() {
		return level;
	}

	@Override
	public Spellbook getSpellbook() {
		return Spellbook.ALL;
	}

	@Override
	public int getWidget() {
		return AbilityStyle.MAGIC.getWidgetId();
	}

	@Override
	public boolean valid() {
		return true;
	}
}

