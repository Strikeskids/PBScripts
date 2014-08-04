package org.logicail.rsbot.scripts.rt4.ostutorialisland.stages;

import org.logicail.rsbot.scripts.framework.GraphScript;
import org.logicail.rsbot.scripts.framework.context.rt4.RT4ClientContext;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 10/07/2014
 * Time: 11:54
 */
public class RandomCharacter extends GraphScript.Action<RT4ClientContext> {
	private static final int WIDGET_CHARACTER = 269;

	@Override
	public String toString() {
		return "Random Character";
	}

	private static int ACCEPT = 100;
	private static int[] GENDER = {138, 139};
	private static int[] CHANGE = {106, 107, 108, 109, 110, 111, 112}; // Next is +1

	private String playername = null;

	private HashMap<Integer, Integer> count = new HashMap<Integer, Integer>();

	public RandomCharacter(RT4ClientContext ctx) {
		super(ctx);

		reset();
	}

	private void reset() {
		for (int i : CHANGE) {
			putRandomCount(i);
			putRandomCount(i + 1);
		}

		for (Colour colour : Colour.values()) {
			putRandomCount(colour.previous);
			putRandomCount(colour.next);
		}

		if (Random.nextBoolean()) {
			count.put(GENDER[1], 1);
		}
	}

	private void putRandomCount(int i) {
		if (i < 0 || Random.nextInt(0, 3) == 0) {
			return;
		}

		final int timesToClick = Random.nextInt(0, 5);
		if (timesToClick > 0) {
			count.put(i, timesToClick);
		}
	}

	@Override
	public void run() {
		final ArrayList<Integer> keys = new ArrayList<Integer>(count.keySet());
		Collections.shuffle(keys);

		for (Integer key : keys) {
			final Integer clicksRemaining = this.count.get(key);
			if (clicksRemaining == 0) {
				continue;
			}

			int clicksToDo = Random.nextInt(0, clicksRemaining + 1);
			if (clicksToDo > 0) {
				final Component component = ctx.widgets.widget(WIDGET_CHARACTER).component(key);
				for (int i = 0; i < clicksToDo; i++) {
					if (component.click()) {
						count.put(key, clicksRemaining - 1);
						Condition.sleep(300); // TODO: varp change?
					}
				}
			}
		}

		for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
			if (entry.getValue() > 0) {
				return;
			}
		}

		final Component accept = ctx.widgets.widget(WIDGET_CHARACTER).component(ACCEPT);
		if (accept.valid()) {
			if (accept.click("Accept")) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !accept.valid();
					}
				}, 200, 15);
			}
		}
	}

	@Override
	public boolean valid() {
		if (playername != null && !ctx.players.local().name().equals(playername)) {
			playername = ctx.players.local().name();
			reset();
		}

		return ctx.widgets.widget(WIDGET_CHARACTER).component(CHANGE[0]).valid();
	}

	enum Colour {
		HAIR(-1, 121),
		TORSO(123, 127),
		LEGS(122, 129),
		FEET(124, 130),
		SKIN(125, 131);
		private final int previous;
		private final int next;

		Colour(int previous, int next) {
			this.previous = previous;
			this.next = next;
		}
	}
}
