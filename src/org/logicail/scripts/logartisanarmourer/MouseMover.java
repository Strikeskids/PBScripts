package org.logicail.scripts.logartisanarmourer;

import org.logicail.framework.script.job.state.Node;
import org.powerbot.script.methods.MethodContext;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 23/06/13
 * Time: 17:40
 */
public class MouseMover extends Node {
	public MouseMover(MethodContext ctx) {
		super(ctx);
	}

	@Override
	public boolean activate() {
		return true;
	}

	@Override
	public void execute() {
		Dimension dimensions = ctx.game.getDimensions();
		ctx.mouse.move(org.powerbot.script.util.Random.nextInt(0, dimensions.width), org.powerbot.script.util.Random.nextInt(0, dimensions.height));
		sleep(500,1000);
	}
}
