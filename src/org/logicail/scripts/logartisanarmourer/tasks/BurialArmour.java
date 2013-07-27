package org.logicail.scripts.logartisanarmourer.tasks;

import org.logicail.framework.script.state.BranchOnce;
import org.logicail.scripts.logartisanarmourer.LogArtisanArmourer;
import org.logicail.scripts.logartisanarmourer.tasks.burial.DepositArmour;
import org.logicail.scripts.logartisanarmourer.tasks.burial.SmithAnvil;
import org.logicail.scripts.logartisanarmourer.tasks.respect.BrokenPipes;
import org.logicail.scripts.logartisanarmourer.tasks.respect.KillAncestors;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/13
 * Time: 16:07
 */
public class BurialArmour extends BranchOnce {
	public BurialArmour(LogArtisanArmourer script) {
		super(script.ctx);

		nodes.add(new StayInArea(ctx));

		LogArtisanArmourer instance = LogArtisanArmourer.instance;

		if (instance.options.repairPipes) {
			nodes.add(new BrokenPipes(ctx));
		}

		if (instance.options.killAncestors) {
			nodes.add(new KillAncestors(ctx));
		}

		nodes.add(new DepositOre(ctx));

		nodes.add(new DepositArmour(ctx, instance.options.getIngotID()));
		nodes.add(new MakeIngots(ctx));
		nodes.add(new SmithAnvil(ctx));

	}

	@Override
	public boolean branch() {
		return false;
	}
}
