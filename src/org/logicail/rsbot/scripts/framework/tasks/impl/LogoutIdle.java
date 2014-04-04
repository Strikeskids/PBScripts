package org.logicail.rsbot.scripts.framework.tasks.impl;

import org.logicail.rsbot.scripts.framework.LogicailScript;
import org.logicail.rsbot.scripts.framework.tasks.Node;
import org.powerbot.script.Random;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 13/01/14
 * Time: 14:32
 */
public class LogoutIdle<T extends LogicailScript> extends Node<T> {
	private final AtomicInteger watch;
	private long nextRun;
	private int lastValue = -1;

	public LogoutIdle(T script, AtomicInteger watch) {
		super(script);
		this.watch = watch;
		reset();
	}

	private void reset() {
		nextRun = System.currentTimeMillis() + Random.nextInt(19 * 60 * 1000, 21 * 60 * 1000);
	}

	@Override
	public boolean valid() {
		return System.currentTimeMillis() > nextRun && ctx.game.loggedIn();
	}

	@Override
	public void run() {
		final int current = watch.get();
		if (current == lastValue) {
			ctx.stop("No xp gained in last 20 minutes, logging out to avoid idling");
		} else {
			lastValue = current;
			reset();
		}
	}
}
