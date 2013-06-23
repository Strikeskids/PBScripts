package org.logicail.framework.script.job;

import org.powerbot.script.methods.MethodContext;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 23/06/13
 * Time: 17:20
 */
public abstract class LoopTask extends Task {
	private boolean paused;

	public LoopTask(MethodContext ctx) {
		super(ctx);
	}

	public final void execute() {
		Container container = getContainer();
		while (!container.isShutdown()) {
			if (container.isPaused()) {
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

	public boolean isPaused() {
		return this.paused;
	}
}
