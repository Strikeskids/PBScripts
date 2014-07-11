package org.logicail.rsbot.scripts.rt4.stages;

import com.logicail.wrappers.NpcDefinition;
import org.logicail.rsbot.scripts.framework.GraphScript;
import org.logicail.rsbot.scripts.framework.context.rt4.IClientContext;
import org.logicail.rsbot.scripts.rt4.ActionManifest;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 10/07/2014
 * Time: 21:54
 */
@ActionManifest(name = "Complete")
public class Complete extends GraphScript.Action<IClientContext> {
	public Complete(IClientContext ctx) {
		super(ctx);
	}

	@Override
	public void run() {
		// TODO: Logout
		ctx.controller.stop();
	}

	@Override
	public boolean valid() {
		return ctx.npcs.select().select(NpcDefinition.filter(ctx, "Lumbridge Guide")).poll().valid();
	}
}
