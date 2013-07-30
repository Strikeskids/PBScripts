package org.logicail.scripts.logartisanarmourer;

import org.logicail.framework.script.ActiveScript;
import org.logicail.framework.script.state.Node;
import org.logicail.framework.script.state.Tree;
import org.logicail.scripts.logartisanarmourer.paint.Paint;
import org.logicail.scripts.logartisanarmourer.tasks.AntiBan;
import org.logicail.scripts.logartisanarmourer.tasks.BurialArmour;
import org.logicail.scripts.tasks.IdleLogout;
import org.powerbot.event.MessageEvent;
import org.powerbot.event.MessageListener;
import org.powerbot.script.Manifest;
import org.powerbot.script.methods.Game;
import org.powerbot.script.util.Random;

import java.util.ArrayList;

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
public class LogArtisanArmourer extends ActiveScript implements MessageListener {
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
	public LogArtisanArmourerOptions options = new LogArtisanArmourerOptions();

	public LogArtisanArmourer() {
		super();

		getExecQueue(State.START).add(new Runnable() {
			@Override
			public void run() {
				submit(new AntiBan(ctx));
				submit(paint = new Paint(ctx));
			}
		});
	}

	/**
	 * Create the job tree
	 */
	public void create() {
		ArrayList<Node> nodes = new ArrayList<>();

		nodes.add(new IdleLogout(ctx, 15 * 60, 20 * 60));

		switch (options.mode) {
			case BURIAL_ARMOUR:
				nodes.add(new BurialArmour(ctx));
				break;
			case CEREMONIAL_SWORDS:
				break;
			case REPAIR_TRACK:
				break;
		}

		tree = new Tree(ctx, nodes.toArray(new Node[nodes.size()]));
	}

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
		return Random.nextInt(100, 500);
	}

	@Override
	public void messaged(MessageEvent messageEvent) {
		if (tree != null & paint instanceof MessageListener) {
			((MessageListener) paint).messaged(messageEvent);
		}
	}
}
