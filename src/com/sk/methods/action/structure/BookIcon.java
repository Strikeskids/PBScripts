package com.sk.methods.action.structure;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
import org.powerbot.script.wrappers.Component;


public interface BookIcon extends WindowedIcon {
	public int getChildIndex();

	public int getChildTexture();
	
	public int getWidget();
	
	public Component getCooldownComponent(LogicailMethodContext ctx);
	
	public static final BookIcon NIL = Ability.NIL;
	
	public static final int MAIN_COMPONENT = 1, COOLDOWN_COMPONENT = 2;
}
