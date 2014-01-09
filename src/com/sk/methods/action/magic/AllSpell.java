package com.sk.methods.action.magic;

import org.logicail.rsbot.scripts.framework.context.IMethodContext;
import org.powerbot.script.methods.Skills;
import org.powerbot.script.wrappers.Component;

import com.sk.methods.action.ability.AbilityStyle;
import com.sk.methods.action.structure.Spell;
import com.sk.windows.InnerAbilityTab;
import com.sk.windows.Window;

public enum AllSpell implements Spell {
	POLYPORE_STRIKE(InnerAbilityTab.COMBAT_SPELL, 2598, 80, 162, 14396),
HOME_TELEPORT(InnerAbilityTab.TELEPORT_SPELL, 2491, 0, 155, 14333),
;

	private final Rune[] runes;
	private final int childIndex, childTexture, level, id;
	private final Window window;

	private AllSpell(Window t, int id, int l, int ci, int ct, Rune... r) {
		this.window = t;
		this.id = id;
		this.level = l;
		this.childIndex = ci;
		this.childTexture = ct;
		this.runes = r;
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
	public int getId() {
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
	public Component getComponent(IMethodContext ctx) {
		return ctx.widgets.get(getWidget(), MAIN_COMPONENT).getChild(getChildIndex());
	}

	@Override
	public Component getCooldownComponent(IMethodContext ctx) {
		return ctx.widgets.get(getWidget(), COOLDOWN_COMPONENT).getChild(getChildIndex());
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
	public boolean isValid() {
		return true;
	}
}

