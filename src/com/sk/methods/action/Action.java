package com.sk.methods.action;

import com.sk.methods.action.structure.Ability;
import com.sk.methods.action.structure.BarIcon;
import com.sk.methods.action.structure.Spell;
import org.logicail.rsbot.scripts.framework.context.IClientContext;

public class Action extends org.powerbot.script.rt6.Action {
	private static final int WIDGET_ID = 1430;
	private static final int COOLDOWN_CHILD = 97;
	private static final int AVAILABILITY_CHILD = 96;
	private static final int AVAILABLE_TEXT_COLOR = 0xFFFFFF;

	protected final IClientContext ctx;
	private final BarIcon icon;
	private final int slot;

	public Action(IClientContext ctx, int slot, Type type, int id, BarIcon ico) {
		super(ctx, slot, type, id);
		this.ctx = ctx;
		this.icon = ico;
		this.slot = slot;
	}

	public int getSlot() {
		return slot;
	}

	public Ability getAbility() {
		return icon instanceof Ability ? (Ability) icon : Ability.NIL;
	}

	public org.powerbot.script.rt6.Component getAvailabilityComponent() {
		if (0 <= slot && slot < 12)
			return getWidget().component(AVAILABILITY_CHILD + 4 * slot);
		return ctx.widgets.component(0, -1);
	}

	public BarIcon getBarIcon() {
		return icon;
	}

	public org.powerbot.script.rt6.Component getCooldownComponent() {
		if (0 <= slot && slot < 12)
			return getWidget().component(COOLDOWN_CHILD + 4 * slot);
		return ctx.widgets.component(0, -1);
	}

	public Spell getSpell() {
		return icon instanceof Spell ? (Spell) icon : Spell.NIL;
	}

	private org.powerbot.script.rt6.Widget getWidget() {
		return ctx.widgets.widget(WIDGET_ID);
	}

	@Override
	public boolean ready() {
		return valid() && getAvailabilityComponent().textColor() == AVAILABLE_TEXT_COLOR && !getCooldownComponent().visible();
	}

	@Override
	public boolean select() {
		return valid() && ctx.keyboard.key(bind(), 0);
	}
}