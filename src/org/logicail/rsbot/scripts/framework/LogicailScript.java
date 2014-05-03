package org.logicail.rsbot.scripts.framework;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.tasks.Tree;
import org.logicail.rsbot.util.Painter;
import org.powerbot.script.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Game;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/12/13
 * Time: 21:07
 */
public abstract class LogicailScript<T extends LogicailScript> extends PollingScript<ClientContext> implements PaintListener {
	public final IClientContext ctx;
	public final Tree<T> tree;
	protected final Painter paint;
	protected JFrame gui;

	protected LogicailScript() {
		this.ctx = new IClientContext(super.ctx, this);

		tree = new Tree<T>((T) this);
		paint = new Painter(this);

		getExecQueue(State.STOP).add(new Runnable() {
			@Override
			public void run() {
				tree.clear();
				try {
					if (gui != null && gui.isVisible()) {
						gui.dispose();
					}
				} catch (Exception ignored) {
				}
			}
		});
	}

	public String version() {
		return manifestExtra().get("version");
	}

	/**
	 * Ordered properties to show on paint box
	 *
	 * @return
	 */
	public abstract java.util.LinkedHashMap<Object, Object> getPaintInfo();

	@Override
	public void poll() {
		try {
			if (ctx.game.clientState() == Game.INDEX_MAP_LOADED) {
				tree.run();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ctx.sleep(250);
	}

	@Override
	public void repaint(Graphics g) {
		paint.properties(getPaintInfo()).repaint(g);
	}

	private java.util.Map<java.lang.String, java.lang.String> manifestExtra = null;

	public java.util.Map<java.lang.String, java.lang.String> manifestExtra() {
		if (manifestExtra != null) return manifestExtra;

		final LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

		final String[] split = getManifest().properties().split(";");

		for (int i = 0; i < split.length; i++) {
			final String[] strings = split[i].split("=", 2);
			if (strings.length == 2) {
				map.put(strings[0], strings[1]);
			}
		}

		return manifestExtra = map;
	}

}
