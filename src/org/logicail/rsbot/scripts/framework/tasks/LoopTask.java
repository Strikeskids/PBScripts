package org.logicail.rsbot.scripts.framework.tasks;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/12/13
 * Time: 20:58
 */
public abstract class LoopTask extends Task {
	private boolean paused;

	public LoopTask(LogicailMethodContext context) {
		super(context);
	}

	public final boolean isPaused() {
		return paused;
	}

	@Override
	public boolean activate() {
		return true; // Not used for LoopTasks
	}

	@Override
	public void run() {
		while (!ctx.isShutdown()) {
			if (ctx.isPaused()) {
				paused = true;
				sleep(1000);
			} else {
				paused = false;

				int delay;
				try {
					delay = loop();
				} catch (Throwable throwable) {
					throwable.printStackTrace();
					delay = -1;
				}

				if (delay >= 0) {
					sleep(delay);
				} else if (delay == -1) {
					break;
				}
			}
		}
	}

	public abstract int loop();
}
