package com.sk.oldcombatapi.methods.action.structure;

import org.logicail.rsbot.scripts.framework.context.rt6.IClientContext;
import org.powerbot.script.rt6.Component;


public interface BookIcon extends WindowedIcon {
	public static final BookIcon NIL = Ability.NIL;

	public static final int MAIN_COMPONENT = 1, COOLDOWN_COMPONENT = 2;

	public int getChildIndex();

	public int getChildTexture();

	public Component getCooldownComponent(IClientContext ctx);

	public int getWidget();
}
