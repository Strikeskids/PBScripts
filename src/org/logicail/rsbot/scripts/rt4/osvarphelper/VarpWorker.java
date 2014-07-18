package org.logicail.rsbot.scripts.rt4.osvarphelper;

import org.powerbot.script.rt4.ClientContext;

import javax.swing.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 18/07/2014
 * Time: 21:34
 */
public class VarpWorker extends SwingWorker<HashMap<Integer, Integer>, VarpWorker.Previous> {
	private final ClientContext ctx;
	private final VarpHelperGUI gui;
	private final DefaultListModel model;
	private final HashMap<Integer, Integer> previous;
	DateFormat df = new SimpleDateFormat("HH:mm:ss");

	public VarpWorker(ClientContext ctx, VarpHelperGUI gui, DefaultListModel model, HashMap<Integer, Integer> previous) {
		this.ctx = ctx;
		this.gui = gui;
		this.model = model;
		this.previous = previous;
	}

	@Override
	protected void process(List<Previous> chunks) {
		if (!gui.paused.get()) {
			for (Previous chunk : chunks) {
				if (model.size() <= chunk.index) {
					model.add(chunk.index, chunk.index + ": " + chunk.newValue);
				} else {
					model.set(chunk.index, chunk.index + ": " + chunk.newValue);
				}
				if (chunk.oldValue != null) {
					gui.historyText.append("[" + df.format(new Date()) + "] " + chunk.index + " - " + chunk.oldValue + "(0x" + Integer.toHexString(chunk.oldValue) + ")" + " => " + chunk.newValue + "(0x" + Integer.toHexString(chunk.newValue) + ")\n");
				}
				gui.updateSelected();
			}
		}
	}

	@Override
	protected HashMap<Integer, Integer> doInBackground() throws Exception {
		final int[] array = ctx.varpbits.array();
		for (int i = 0; i < array.length; i++) {
			final int value = array[i];
			Integer old = previous.get(i);
			if (old == null || old != value) {
				previous.put(i, value);
				publish(new Previous(i, old, value));
			}
		}

		return previous;
	}

	class Previous {
		private int index;
		private Integer oldValue;
		private int newValue;

		public Previous(int index, Integer oldValue, int newValue) {
			this.index = index;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}
	}
}
