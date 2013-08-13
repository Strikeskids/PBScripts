package org.logicail.scripts.logartisanarmourer.tasks.respect;

import org.logicail.api.methods.LogicailMethodContext;
import org.logicail.scripts.logartisanarmourer.LogArtisanArmourer;
import org.logicail.scripts.logartisanarmourer.LogArtisanArmourerOptions;
import org.powerbot.script.lang.Filter;
import org.powerbot.script.methods.Skills;
import org.powerbot.script.util.Random;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Model;
import org.powerbot.script.wrappers.Player;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/13
 * Time: 17:45
 */
public class BrokenPipes extends RespectNode {
	private static final int[] BROKEN_PIPE = {29410, 29411, 29413, 29414, 29761, 29762};
	private LogArtisanArmourerOptions options;

	public BrokenPipes(LogicailMethodContext ctx) {
		super(ctx);
		options = ((LogArtisanArmourer) ctx.script).options;
	}

	@Override
	public boolean activate() {
		return super.activate()
				&& ctx.skills.getRealLevel(Skills.SMITHING) >= 50
				&& !ctx.objects.select().id(BROKEN_PIPE).select(new Filter<GameObject>() {
			@Override
			public boolean accept(GameObject gameObject) {
				return getNumFaces(gameObject.getModel()) == 63;
			}
		}).nearest().first().isEmpty();
	}

	@Override
	public void execute() {
		for (GameObject pipe : ctx.objects) {
			if (ctx.interaction.interact(pipe, "Mend")) {
				sleep(1000, 2000);

				options.isSmithing = false;

				final Timer t = new Timer(Random.nextInt(15000, 20000));

				while (t.isRunning()) {
					if (!activate()) {
						sleep(100, 500);
						break;
					}

					Player local = ctx.players.local();
					if (local.isInMotion() || local.getAnimation() != -1) {
						t.reset();
						sleep(100, 500);
					}

					sleep(200);
				}

				sleep(50, 500);
			}
		}
	}

	public int getNumFaces(Model model) {
		if (model != null) {
			int length = model.getFaceA().length;
			System.out.println("Pipe.getFaceA " + length);
			return length;
		}

		return -1;
	}
}
