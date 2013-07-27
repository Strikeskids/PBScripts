package org.logicail.scripts.logartisanarmourer;

import org.logicail.api.methods.MyMethodContext;
import org.logicail.framework.script.state.Node;
import org.powerbot.script.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 23/06/13
 * Time: 17:40
 */
public class MouseMover extends Node {
	public MouseMover(MyMethodContext ctx) {
		super(ctx);
	}

	@Override
	public boolean activate() {
		System.out.println("activate: " + Thread.currentThread().getId());
		return Random.nextBoolean();
	}

	@Override
	public void execute() {
		System.out.println("execute: " + Thread.currentThread().getId());
		//Dimension dimensions = ctx.game.getDimensions();
		//ctx.mouse.move(org.powerbot.scriptold.util.Random.nextInt(0, dimensions.width), org.powerbot.scriptold.util.Random.nextInt(0, dimensions.height));
		sleep(1000, 2000);
	}
}
