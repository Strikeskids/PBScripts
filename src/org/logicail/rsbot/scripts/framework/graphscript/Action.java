package org.logicail.rsbot.scripts.framework.graphscript;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 21/05/2014
 * Time: 22:03
 */

import org.powerbot.script.AbstractScript;
import org.powerbot.script.ClientAccessor;
import org.powerbot.script.ClientContext;
import org.powerbot.script.Validatable;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A node for {@link GraphScript}. In practical terms this is an atomic (independent) module which holds a
 * small task and/or condition.
 */
public abstract class Action<T extends ClientContext> extends ClientAccessor<T> implements Runnable, Comparable<Action<T>>, Validatable {
	private static final AtomicInteger s = new AtomicInteger(0);
	private final int q;
	private final Queue<Validator> validators;

	/**
	 * The chain of this node.
	 */
	protected final Queue<Action<T>> chain;
	/**
	 * The priority value, which is relative to other nodes in the same leaf. By default the value is 0.
	 */
	protected final AtomicInteger priority;

	/**
	 * Creates a new {@link Action} (a node).
	 *
	 * @param script the container script
	 */
	public Action(final GraphScript<T> script) {
		this(script.ctx);
	}

	/**
	 * Creates a new {@link Action} (a node).
	 *
	 * @param ctx the client context
	 */
	public Action(final T ctx) {
		super(ctx);
		chain = new PriorityBlockingQueue<Action<T>>();
		priority = new AtomicInteger(0);
		q = s.getAndIncrement();
		validators = new ConcurrentLinkedQueue<Validator>();

		push(new Validator() {
			@Override
			public boolean valid() {
				return Action.this.valid();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean valid() {
		return true;
	}

	/**
	 * Pushes a new {@link Action.Validator} to the checking queue.
	 *
	 * @param v a new {@link Action.Validator}
	 * @return {@code true} if the {@link Action.Validator} was added, otherwise {@code false}
	 */
	public final boolean push(final Validator v) {
		return !validators.contains(v) && validators.offer(v);
	}

	public boolean enabled() {
		boolean blocked = false;

		final Iterator<Validator> i = validators.iterator();
		while (i.hasNext()) {
			final Validator c = i.next();
			if (!c.enabled.get()) {
				c.pop();
				i.remove();
			} else if (!blocked && !c.valid()) {
				blocked = true;
			}
		}

		return !blocked;
	}

	/**
	 * Returns the index of the cursor on the graph which takes the form {@code [X]n, n>1}. This value represents
	 * the path of the branch at which the node was accessed.
	 *
	 * @return the index of the cursor on the graph
	 */
	protected final String getCursorIndex() {
		final AbstractScript c = ctx.controller.script();
		if (!(c instanceof GraphScript)) {
			return "";
		}
		@SuppressWarnings("unchecked")
		final GraphScript<T> parent = (GraphScript<T>) c;
		int i = parent.i.size();
		final char[] s = new char[i];

		for (final AtomicInteger n : parent.i) {
			s[--i] = (char) ((n.get() % 26) + 'A');
		}

		return new String(s);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int compareTo(final Action<T> o) {
		final int r = o.priority.get() - priority.get();
		return r == 0 ? q - o.q : r;
	}

	/**
	 * A class indicating a validation state, which can be disabled externally.
	 */
	protected static abstract class Validator implements Validatable {
		private final AtomicBoolean enabled = new AtomicBoolean(true);

		/**
		 * Removes this {@link Action.Validator} from the checking queue.
		 */
		public final void pop() {
			enabled.set(false);
		}
	}
}
