package org.logicail.scripts.logartisanarmourer.tasks.respect;

import org.logicail.api.methods.MyMethodContext;
import org.logicail.framework.script.state.Node;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/13
 * Time: 16:20
 */
public abstract class RespectNode extends Node {
	public final static int SETTING_RESPECT = 126;

	public RespectNode(MyMethodContext ctx) {
		super(ctx);
	}

	@Override
	public boolean activate() {
		return getRespect() < 100
				&& !ctx.skillingInterface.isOpen();
	}

	public int getRespect() {
		return ctx.settings.get(SETTING_RESPECT, 10, 0b1111111);
	}
}
