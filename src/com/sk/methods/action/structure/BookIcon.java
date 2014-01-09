package com.sk.methods.action.structure;

import org.logicail.rsbot.scripts.framework.context.IMethodContext;
import org.powerbot.script.wrappers.Component;


public interface BookIcon extends WindowedIcon {
	public static final BookIcon NIL = Ability.NIL;
	
	public static final int MAIN_COMPONENT = 1, COOLDOWN_COMPONENT = 2;
	public int getChildIndex();

	public int getChildTexture();
	
	public Component getCooldownComponent(IMethodContext ctx);
	
	public int getWidget();
}
