package org.logicail.rsbot.scripts.framework.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 02/04/2014
 * Time: 16:15
 */
public class Timer {
	private final AtomicLong start;
	private final AtomicLong end;
	private final AtomicLong length;

	public Timer(final long millis) {
		length = new AtomicLong(millis * 1000000L);
		start = new AtomicLong(System.nanoTime());
		end = new AtomicLong(start.get() + length.get());
	}

	public String elapsedString() {
		return format(elapsed());
	}

	public long elapsed() {
		return (System.nanoTime() - start.get()) / 1000000L;
	}

	public static String format(final long millis) {
		final StringBuilder sb = new StringBuilder();
		final long totalseconds = millis / 1000L;
		final long totalminutes = totalseconds / 60L;
		final long totalhours = totalminutes / 60L;
		final int seconds = (int) totalseconds % 60;
		final int minutes = (int) totalminutes % 60;
		final int hours = (int) totalhours % 24;
		final int days = (int) (totalhours / 24);
		if (days > 0) {
			if (days < 10) {
				sb.append(0);
			}
			sb.append(days);
			sb.append(":");
		}
		if (hours < 10) {
			sb.append(0);
		}
		sb.append(hours);
		sb.append(":");
		if (minutes < 10) {
			sb.append(0);
		}
		sb.append(minutes);
		sb.append(":");
		if (seconds < 10) {
			sb.append(0);
		}
		sb.append(seconds);
		return sb.toString();
	}

	public long endIn(final long millis) {
		end.set(end.get() + millis * 1000000L);
		return end.get();
	}

	public String remainingString() {
		return format(remaining());
	}

	public long remaining() {
		return Math.max(0L, (end.get() - System.nanoTime()) / 1000000L);
	}

	public void reset() {
		end.set(System.nanoTime() + length.get());
	}

	public boolean running() {
		return end.get() > System.nanoTime();
	}
}
