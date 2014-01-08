package com.sk.methods.action.structure;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Identifiable;
import org.powerbot.script.wrappers.Validatable;

public interface BarIcon extends Validatable, Identifiable {
	public static final BarIcon NIL = Ability.NIL;

	public Component getComponent(LogicailMethodContext ctx);
}
