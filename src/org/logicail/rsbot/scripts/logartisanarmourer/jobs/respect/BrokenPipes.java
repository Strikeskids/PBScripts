package org.logicail.rsbot.scripts.logartisanarmourer.jobs.respect;

import org.logicail.rsbot.scripts.framework.context.LogicailMethodContext;
import org.logicail.rsbot.scripts.logartisanarmourer.LogArtisanArmourer;
import org.powerbot.script.lang.BasicNamedQuery;
import org.powerbot.script.lang.Filter;
import org.powerbot.script.methods.Skills;
import org.powerbot.script.util.Random;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.GameObject;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 13/08/12
 * Time: 17:36
 */
public class BrokenPipes extends RespectStrategy {
	public BrokenPipes(LogicailMethodContext context) {
		super(context);
	}

	@Override
	public String toString() {
		return "Repair pipes";
	}

	private static final int[] BROKEN_PIPE = {29410, 29411, 29413, 29414, 29761, 29762};

	@Override
	public boolean activate() {
		return super.activate()
				&& ctx.skills.getRealLevel(Skills.SMITHING) >= 50
				&& !getPipe().isEmpty();
	}

	private BasicNamedQuery<GameObject> getPipe() {
		return ctx.objects.select(new Filter<GameObject>() {
			@Override
			public boolean accept(GameObject gameObject) {
				if (Arrays.binarySearch(BROKEN_PIPE, gameObject.getId()) >= 0) {
					return gameObject.getModel().getTriangles().length == 63;
				}
				return false;
			}
		}).nearest().first();
	}

	@Override
	public void run() {
		final Timer t = new Timer(Random.nextInt(15000, 20000));

		for (GameObject pipe : getPipe()) {
			if (ctx.camera.myTurnTo(pipe) && pipe.interact("Mend")) {
				LogArtisanArmourer.status = "Repairing pipe";
				LogArtisanArmourer.isSmithing = false;
				sleep(1000, 2000);

				t.reset();

				while (t.isRunning()) {
					if (getPipe().isEmpty()) {
						sleep(200, 1200);
						break;
					}

					if (ctx.players.local().isInMotion() || ctx.players.local().getAnimation() != -1) {
						sleep(100, 500);
						t.reset();
					}

					sleep(50, 300);
				}
				sleep(50, 500);
			}
		}
	}
}
