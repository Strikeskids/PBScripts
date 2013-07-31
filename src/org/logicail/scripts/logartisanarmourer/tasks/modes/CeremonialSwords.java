package org.logicail.scripts.logartisanarmourer.tasks.modes;

import org.logicail.api.methods.LogicailMethodContext;
import org.logicail.framework.script.state.BranchOnce;
import org.logicail.scripts.logartisanarmourer.tasks.DepositOre;
import org.logicail.scripts.logartisanarmourer.tasks.MakeIngots;
import org.logicail.scripts.logartisanarmourer.tasks.swords.GetPlan;
import org.logicail.scripts.logartisanarmourer.tasks.swords.GetTongs;
import org.logicail.scripts.logartisanarmourer.tasks.swords.HeatIngots;
import org.logicail.scripts.logartisanarmourer.tasks.swords.MakeSword;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 31/07/13
 * Time: 11:30
 */
public class CeremonialSwords extends BranchOnce {
	public CeremonialSwords(LogicailMethodContext ctx) {
		super(ctx);

		//LogArtisanArmourerOptions options = ((LogArtisanArmourer) ctx.script).options;

		nodes.add(new DepositOre(ctx));
		nodes.add(new MakeIngots(ctx));
		nodes.add(new GetTongs(ctx));
		nodes.add(new GetPlan(ctx));
		nodes.add(new HeatIngots(ctx));
		nodes.add(new MakeSword(ctx));
	}

	@Override
	public boolean branch() {
		return true;
	}
}
