package org.logicail.scripts.logartisanarmourer;

import org.logicail.api.TimeUtilities;
import org.logicail.api.methods.SimplePaint;
import org.logicail.framework.script.ActiveScript;
import org.logicail.scripts.logartisanarmourer.tasks.AntiBan;
import org.logicail.scripts.tasks.AnimationHistory;
import org.powerbot.event.PaintListener;
import org.powerbot.script.Manifest;
import org.powerbot.script.methods.Game;
import org.powerbot.script.methods.Skills;

import java.awt.*;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 19/06/13
 * Time: 17:25
 */
@Manifest(
		name = "LogArtisanArmourer",
		description = "Cheap smithing xp at Artisans Workshop",
		version = 2.0,
		authors = {"Logicail"},
		instances = 99,
		website = "http://www.powerbot.org/community/topic/704413-logartisan-artisan-armourer-cheap-smither/")
public class LogArtisanArmourer extends ActiveScript implements PaintListener {
	public static final int ID_SMELTER = 29395;
	public static final int ID_SMELTER_SWORDS = 29394;
	public static final int[] ARMOUR_ID_LIST = {20572, 20573, 20574, 20575,
			20576, 20577, 20578, 20579, 20580, 20581, 20582, 20583, 20584,
			20585, 20586, 20587, 20588, 20589, 20590, 20591, 20592, 20593,
			20594, 20595, 20596, 20597, 20598, 20599, 20600, 20601, 20602,
			20603, 20604, 20605, 20606, 20607, 20608, 20609, 20610, 20611,
			20612, 20613, 20614, 20615, 20616, 20617, 20618, 20619, 20620,
			20621, 20622, 20623, 20624, 20625, 20626, 20627, 20628, 20629,
			20630, 20631};
	public static final int[] ANIMATION_SMITHING = {898, 11062, 15121};
	public static LogArtisanArmourer instance;
	public Options options = new Options();
	//private WindowHandler handler = null;
	private SimplePaint paint = new SimplePaint();

	public LogArtisanArmourer() {
		super();

		instance = this;

		Map<String, String> contents = paint.contents;
		contents.put("NAME", getName());
		contents.put("VERSION", String.valueOf(getVersion()));
		contents.put("LINE_1", "");
		contents.put("Time", TimeUtilities.format(getRuntime()));
		contents.put("SPACE_1", "");
		contents.put("Level", ctx.skills.getLevel(Skills.SMITHING) + " (+0)");
		contents.put("TTL", "00:00:00");
		contents.put("XP Gained", "1,234,567");
		contents.put("XP Hour", "123,456");
		contents.put("Bones Offered", "1,234 (1,234/h)");
	}

	@Override
	public void start() {
		/*super.start();
		try {
			removeOldStats();
		} catch (Exception ignored) {
		}*/

		/*SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				handler = new WindowHandler();
				log.addHandler(handler);
			}
		});*/

		submit(new AnimationHistory(ctx));
		submit(new AntiBan(ctx));

		//tree = new Tree(ctx, new Node[]{new MouseMover(ctx), new MouseMover(ctx)});
	}

	/*
		@Override
		public void stop() {
			// Gotta stop executor
			super.stop();

			removeOldStats();
		}
	*/
	/*
	private void removeOldStats() {
		for (Window window : Window.getWindows()) {
			for (WindowListener windowListener : window.getWindowListeners()) {
				if (windowListener instanceof LogWindow) {
					window.removeWindowListener(windowListener);
				}
			}
			for (ComponentListener windowListener : window.getComponentListeners()) {
				if (windowListener instanceof LogWindow) {
					window.removeComponentListener(windowListener);
				}
			}
			if (window instanceof LogWindow) {
				log.info(window.getName() + " disposed");
				window.dispose();
			}
		}
	}
*/
	@Override
	public int poll() {
		try {
			if (tree != null && ctx.game.getClientState() == Game.INDEX_MAP_LOADED) {
				if (tree.activate()) {
					tree.execute();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 200;
	}

	@Override
	public void repaint(Graphics g) {
		paint.draw(g);
	}
}
