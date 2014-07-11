package com.sk.oldcombatapi.methods.action.structure;

import com.sk.oldcombatapi.windows.Window;

public interface WindowedIcon extends BarIcon {
	public static final WindowedIcon NIL = Ability.NIL;

	public int getSkill();

	public int getSkillLevel();

	public Window getWindow();
}
