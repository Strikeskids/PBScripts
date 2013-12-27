package org.logicail.rsbot.scripts.framework;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
import org.logicail.rsbot.scripts.framework.tasks.Task;
import org.logicail.rsbot.scripts.framework.tasks.Tree;
import org.powerbot.event.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.methods.Game;
import org.powerbot.script.util.Random;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/12/13
 * Time: 21:07
 */
public abstract class LogicailScript extends PollingScript implements PaintListener {
	public final LogicailMethodContext ctx;
	protected Tree tree = null;

	protected LogicailScript() {
		this.ctx = new LogicailMethodContext(super.ctx, this);
		this.ctx.init(super.ctx);
	}

	@Override
	public int poll() {
		try {
			if (tree != null && ctx.game.getClientState() == Game.INDEX_MAP_LOADED) {
				tree.run();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Random.nextInt(200, 700);
	}

	@Override
	public void repaint(Graphics g) {
	}

	public void submit(Task task) {
		ctx.submit(task);
	}
}
