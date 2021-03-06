package org.logicail.rsbot.scripts.rt4.ostutorialisland.stages;

import org.logicail.cache.loader.rt4.wrappers.NpcDefinition;
import org.logicail.cache.loader.rt4.wrappers.ObjectDefinition;
import org.logicail.rsbot.scripts.framework.context.rt4.IClientContext;
import org.logicail.rsbot.scripts.rt4.ostutorialisland.OSTutorialIsland;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.rt4.*;

import java.util.Comparator;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 10/07/2014
 * Time: 21:03
 */
public class Banker extends Talker {
	public Banker(final IClientContext ctx) {
		super(ctx, "Banker");
	}

	@Override
	public boolean valid() {
		return super.valid() || stage() >= 14 && ctx.npcs.select().select(NpcDefinition.name(ctx, "Giant rat")).poll().valid();
	}

	@Override
	protected Npc npc() {
		return ctx.npcs.select().select(NpcDefinition.name(ctx, name)).select(new Filter<Npc>() {
			@Override
			public boolean accept(Npc npc) {
				return npc.tile().derive(0, -2).matrix(ctx).reachable();
			}
		}).nearest().poll();
	}

	@Override
	protected void tryTalk() {
		final Npc guide = npc();
		log.info("[" + name + "] Talk-to");
		if (ctx.camera.prepare(guide)) {
			if (guide.interact("Talk-to", name)) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return ctx.chat.queryContinue();
					}
				}, 200, 15);
			} else {
				Condition.sleep(1000);
			}
		}
	}

	@Override
	public void run() {
		if (tryContinue()) return;

		ctx.inventory.deselect();

		enter();

		if (ctx.bank.opened()) {
			ctx.bank.close();
			return;
		}

		final Component component = ctx.chat.getComponentByText("Yes.");
		if (component.valid()) {
			component.click();
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return ctx.bank.opened();
				}
			}, 200, 5);
			return;
		}

		final Component closePoll = pollCloseButton();
		if (closePoll.valid() && closePoll.click("Close")) {
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return !closePoll.valid();
				}
			}, 200, 5);
			return;
		}

		if (ctx.chat.visible("visit the poll booth indicated.")) {
			GameObject poll = ctx.objects.select().select(ObjectDefinition.name(ctx, "Poll booth")).nearest().poll();
			if (ctx.camera.prepare(poll) && poll.interact("Use", "Poll booth")) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return pollCloseButton().valid();
					}
				}, 200, 5);
			}
			return;
		}

		if (stage() > 14 || ctx.chat.visible("ready, move on through the door indicated")) {
			leave();
			return;
		}

		super.run();
	}

	private Component pollCloseButton() {
		return ctx.widgets.widget(345).component(1).component(11);
	}

	@Override
	protected void enter() {
		if (ctx.npcs.select().select(NpcDefinition.name(ctx, "Giant rat")).poll().valid()) {
			final GameObject ladder = ctx.objects.select().select(ObjectDefinition.name(ctx, "Ladder")).nearest().poll();
			if (ctx.camera.prepare(ladder) && ladder.interact("Climb-up")) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return ctx.chat.visible("Banking.");
					}
				}, 200, 30);
			}
		}
	}

	@Override
	protected void leave() {
		final GameObject bank = ctx.objects.select().select(ObjectDefinition.name(ctx, "Bank booth")).nearest().poll();
		if (bank.valid()) {
			final BasicQuery<GameObject> doors = doorsInYByX(ctx, bank);
			doors.poll();
			final GameObject first = doors.poll();
			if (ctx.camera.prepare(first)) {
				first.interact("Open");
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return ctx.chat.visible("The guide here will tell you all about making cash");
					}
				}, 200, 10);
			}
		}
	}

	public static BasicQuery<GameObject> doorsInYByX(IClientContext ctx, final GameObject bank) {
		return ctx.objects.select().select(ObjectDefinition.name(ctx, "Door")).select(new Filter<GameObject>() {
			@Override
			public boolean accept(GameObject gameObject) {
				return gameObject.tile().y() == bank.tile().y();
			}
		}).each(Interactive.doSetBounds(OSTutorialIsland.BOUNDS_DOOR_E)).sort(new Comparator<GameObject>() {
			@Override
			public int compare(GameObject o1, GameObject o2) {
				return Integer.valueOf(o1.tile().x()).compareTo(o2.tile().x());
			}
		});
	}
}
