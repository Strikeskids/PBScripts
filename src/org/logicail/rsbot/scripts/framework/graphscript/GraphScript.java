package org.logicail.rsbot.scripts.framework.graphscript;

import org.powerbot.script.ClientContext;
import org.powerbot.script.PollingScript;

import java.util.Deque;
import java.util.LinkedList;
import java.util.NavigableSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 21/05/2014
 * Time: 21:06
 */
public abstract class GraphScript<C extends ClientContext> extends PollingScript<C> {
	public final Deque<AtomicInteger> i;
	public C ctx;

	/**
	 * The root chain where the node cursor will start.
	 */
	protected final NavigableSet<Action<C>> chain;

	/**
	 * Creates a {@link GraphScript}. The root {@link #chain} should be populated here.
	 */
	public GraphScript() {
		i = new LinkedList<AtomicInteger>();
		chain = new ConcurrentSkipListSet<Action<C>>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void poll() {
		propagate(chain);
	}

	private void propagate(final Iterable<Action<C>> chain) {
		final AtomicInteger c = new AtomicInteger(0);
		i.push(c);
		for (final Action<C> a : chain) {
			if (a.enabled()) {
				a.run();
				propagate(a.chain);
			}
			c.incrementAndGet();
		}
		i.pop();
	}
}