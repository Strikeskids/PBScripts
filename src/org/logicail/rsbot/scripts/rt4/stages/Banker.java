package org.logicail.rsbot.scripts.rt4.stages;

import com.logicail.wrappers.NpcDefinition;
import com.logicail.wrappers.ObjectDefinition;
import org.logicail.rsbot.scripts.framework.context.rt4.IClientContext;
import org.logicail.rsbot.scripts.rt4.OSTutorialIsland;
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
		return super.valid() || stage() >= 14 && ctx.npcs.select().select(NpcDefinition.filter(ctx, "Giant rat")).poll().valid();
	}

	@Override
	protected Npc npc() {
		return ctx.npcs.select().select(NpcDefinition.filter(ctx, name)).select(new Filter<Npc>() {
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

		if (ctx.chat.visible("window and move on through the door indicated")) {
			leave();
			return;
		}

		super.run();
	}

	@Override
	protected void enter() {
		if (ctx.npcs.select().select(NpcDefinition.filter(ctx, "Giant rat")).poll().valid()) {
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
		}).each(Interactive.doSetBounds(OSTutorialIsland.BOUNDS_DOOR_EW)).sort(new Comparator<GameObject>() {
			@Override
			public int compare(GameObject o1, GameObject o2) {
				return Integer.valueOf(o1.tile().x()).compareTo(o2.tile().x());
			}
		});
	}
}
