package org.logicail.scripts.logartisanarmourer.tasks.swords;

import org.logicail.api.methods.LogicailMethodContext;
import org.logicail.framework.script.state.Node;
import org.logicail.scripts.logartisanarmourer.LogArtisanArmourerOptions;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 28/07/13
 * Time: 12:00
 */
public class GetPlan extends Node {
	public static final int[] EGIL_ABEL = {6642, 6647};
	private final LogArtisanArmourerOptions options;

	public GetPlan(LogicailMethodContext ctx, LogArtisanArmourerOptions options) {
		super(ctx);
		this.options = options;
	}

	@Override
	public boolean activate() {
		return options.finishedSword || !options.gotPlan;
	}

	@Override
	public void execute() {
	}
}
