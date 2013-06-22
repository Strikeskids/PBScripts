package org.logicail.framework.node;

import org.powerbot.script.methods.Game;
import org.powerbot.script.methods.MethodContext;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 19/06/13
 * Time: 17:56
 */
public class Tree extends Job {
	protected final JobContainer jobContainer;
	private AtomicReference<Job> currentJob = new AtomicReference<>();

	public Tree(MethodContext ctx, Job... nodes) {
		super(ctx);
		jobContainer = new JobContainer(nodes);
	}

	public boolean branch() {
		return true;
	}

	@Override
	public final boolean activate() {
		if (ctx.game.getClientState() != Game.INDEX_MAP_LOADED) {
			return false;
		}

		if (!branch()) {
			return false;
		}

		Job poll = jobContainer.poll();
		currentJob.set(poll);
		if (poll != null) {
			return true;
		}

		return false;
	}

	@Override
	public void execute() {
		Job node = currentJob.get();
		if (node != null) {
			currentJob.set(null);
			node.execute();
			final int delay = node.delay();
			sleep(delay, (int) (delay * 1.5));
		}
	}
}
