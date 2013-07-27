package org.logicail.scripts.logartisanarmourer.tasks.respect;

import org.logicail.api.methods.MyMethodContext;
import org.powerbot.script.methods.Skills;
import org.powerbot.script.util.Random;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Model;
import org.powerbot.script.wrappers.Player;
import org.powerbot.script.wrappers.Tile;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/13
 * Time: 17:45
 */
public class BrokenPipes extends RespectNode {
	private static final int[] BROKEN_PIPE = {29410, 29411, 29413, 29414, 29761, 29762};
	private GameObject pipe;

	public BrokenPipes(MyMethodContext ctx) {
		super(ctx);
	}

	@Override
	public boolean activate() {
		return super.activate()
				&& ctx.skills.getRealLevel(Skills.SMITHING) >= 50
				&& (pipe = getPipe()) != null;
	}

	@Override
	public void execute() {
		final Timer t = new Timer(Random.nextInt(15000, 20000));
		Player local = ctx.players.local();
		if (!ctx.camera.turnTo(pipe) || ctx.movement.getDistance(pipe) > 9) {
			final Tile destination = ctx.movement.reachableNear(pipe);
			if (ctx.movement.findPath(destination).traverse()) {
				while (t.isRunning()) {
					if (local.isInMotion()) {
						t.reset();
					}

					if (ctx.movement.getDistance(destination) < 3) {
						sleep(50, 750);
						break;
					}
					sleep(100, 300);
				}
			}
		}

		if (ctx.camera.turnTo(pipe) && pipe.interact("Mend")) {
			sleep(1000, 2000);

			// Set not smithing

			t.reset();

			while (t.isRunning()) {
				GameObject newPipe = getPipe();
				if (newPipe == null || newPipe.getLocation().equals(pipe.getLocation())) {
					sleep(100, 500);
					break;
				}

				if (local.isInMotion() || local.getAnimation() != -1) {
					t.reset();
					sleep(100, 500);
				}

				sleep(200);
			}

			sleep(50, 500);
		}
	}

	public int getNumFaces(Model model) {
		System.out.println(model.getClass().getName());
		//if (model == null) {
		return -1;
		//}
		//return Math.min(model.getIndices1().length, Math.min(model.getIndices2().length, model.getIndices3().length));
	}

	public GameObject getPipe() {
		for (GameObject gameObject : ctx.objects.select().id(BROKEN_PIPE).nearest()) {
			Model model = gameObject.getModel();
			if (model != null && getNumFaces(model) == 63) {
				return gameObject;
			}
		}
		return null;
	}
}
