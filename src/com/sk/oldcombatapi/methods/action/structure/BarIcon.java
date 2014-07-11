package com.sk.oldcombatapi.methods.action.structure;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.powerbot.script.Identifiable;
import org.powerbot.script.Validatable;
import org.powerbot.script.rt6.Component;

public interface BarIcon extends Validatable, Identifiable {
	public static final BarIcon NIL = Ability.NIL;

	public Component getComponent(IClientContext ctx);
}
