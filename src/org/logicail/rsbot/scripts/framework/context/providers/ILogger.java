package org.logicail.rsbot.scripts.framework.context.providers;

import java.awt.*;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 11/01/14
 * Time: 20:06
 */
public class ILogger extends Handler {
	private static final long MAX_LIFETIME = 10000;
	private final int capacity;
	private final AtomicBoolean painted = new AtomicBoolean();
	private final LinkedBlockingDeque<LogEntry> logEntries = new LinkedBlockingDeque<LogEntry>();
	private long lastLoop = System.currentTimeMillis();

	public ILogger(int capacity) {
		this.capacity = capacity;
	}

	@Override
	public void close() throws SecurityException {
	}

	@Override
	public void flush() {
		logEntries.clear();
	}

	@Override
	public void publish(LogRecord record) {
		if (painted.get()) {
			final LogEntry poll = logEntries.poll();
			if (poll == null || !poll.text.equals(record.getMessage())) {
				logEntries.push(new LogEntry(record));
			}
		}
	}

	public void repaint(Graphics g, int x, int y) {
		painted.set(true);
		Graphics2D g2d = (Graphics2D) g;
		Composite previousComposite = g2d.getComposite();

		int i = 0;
		long timeBetween = System.currentTimeMillis() - lastLoop;

		final Iterator<LogEntry> iterator = logEntries.iterator();
		while (iterator.hasNext()) {
			final LogEntry logEntry = iterator.next();
			if (logEntry.alpha <= 0.0f) {
				iterator.remove();
				continue;
			}

			y += 15;

			g.setColor(Color.BLACK);
			g2d.setComposite(AlphaComposite.SrcOver.derive(logEntry.alpha));
			g.drawString(logEntry.text, x + 1, y + 1);
			g.setColor(logEntry.color);
			g.drawString(logEntry.text, x, y);

			if (i > capacity - 1 || System.currentTimeMillis() - logEntry.timeSent > MAX_LIFETIME) {
				logEntry.alpha -= timeBetween * 0.0005;
			}
			i++;
		}

		g2d.setComposite(previousComposite);

		lastLoop = System.currentTimeMillis();
	}

	private class LogEntry {
		public final String text;
		public final Color color;
		public final long timeSent;
		public float alpha = 1f;

		public LogEntry(LogRecord record) {
			this(record.getMessage(), Color.WHITE);
		}

		private LogEntry(String text, Color color) {
			this.text = text;
			this.color = color;
			timeSent = System.currentTimeMillis();
		}
	}
}
