package org.logicail.rsbot.scripts.framework.util;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 15/07/2014
 * Time: 22:34
 */
public class LoopCondition {
	private final Callable<Boolean> reset;
	private final Callable<Boolean> exit;

	public LoopCondition(Callable<Boolean> exit, Callable<Boolean> reset) {
		this.reset = reset;
		this.exit = exit;
	}

	/**
	 * Take care infinite loop possible in reset
	 *
	 * @param condition
	 * @param frequency
	 * @param tries
	 * @return
	 */
	public static boolean wait(LoopCondition condition, int frequency, int tries) {
		tries = Math.max(1, tries + org.powerbot.script.Random.nextInt(-1, 2));
		for (int i = 0; i < tries; i++) {
			try {
				Thread.sleep((long) Math.max(5, (int) ((double) frequency * org.powerbot.script.Random.nextDouble(0.85, 1.5))));
			} catch (InterruptedException ignored) {
			}
			try {
				if (condition.exit.call()) {
					return true;
				}
			} catch (Exception ignored) {
			}
			try {
				if (condition.reset.call()) {
					i = 0;
				}
			} catch (Exception ignored) {
			}
		}

		return false;
	}
}
