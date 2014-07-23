package org.logicail.rsbot.scripts.rt4.ostutorialisland.stages;

import com.logicail.loader.rt4.wrappers.NpcDefinition;
import org.logicail.rsbot.scripts.framework.GraphScript;
import org.logicail.rsbot.scripts.framework.context.rt4.IClientContext;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 10/07/2014
 * Time: 21:54
 */
public class Complete extends GraphScript.Action<IClientContext> {
	public Complete(IClientContext ctx) {
		super(ctx);
	}

	@Override
	public String toString() {
		return "Complete";
	}

	@Override
	public void run() {
		ctx.inventory.deselect();

		if (ctx.chat.queryContinue()) {
			ctx.chat.clickContinue();
			return;
		}

		// TODO: Logout
		ctx.controller.stop();
	}

	@Override
	public boolean valid() {
		return ctx.npcs.select().select(NpcDefinition.name(ctx, "Lumbridge Guide")).poll().valid();
	}
}
