package org.logicail.rsbot.scripts.rt4.ostutorialisland.stages;

import org.logicail.cache.loader.rt4.wrappers.NpcDefinition;
import org.logicail.rsbot.scripts.framework.GraphScript;
import org.logicail.rsbot.scripts.framework.context.rt4.IClientContext;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.rt4.Npc;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 10/07/2014
 * Time: 13:11
 */
public abstract class Talker extends GraphScript.Action<IClientContext> {
	protected final String name;

	public Talker(IClientContext ctx, String name) {
		super(ctx);
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	protected abstract void enter();

	protected abstract void leave();

	@Override
	public void run() {
		ctx.inventory.deselect();

		if (tryContinue()) return;

		tryTalk();
	}

	protected boolean tryContinue() {
		if (ctx.chat.queryContinue()) {
			log.info("[" + name + "] Continue");
			ctx.chat.clickContinue();
			return true;
		}
		return false;
	}

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

	protected Npc npc() {
		return ctx.npcs.select().select(NpcDefinition.name(ctx, name)).select(new Filter<Npc>() {
			@Override
			public boolean accept(Npc npc) {
				return npc.tile().matrix(ctx).reachable();
			}
		}).shuffle().poll();
	}

	public int stage() {
		return ctx.varpbits.varpbit(406);
	}

	@Override
	public boolean valid() {
		return ctx.players.local().animation() == -1 && (!ctx.players.local().inMotion() || ctx.movement.distance(ctx.players.local(), ctx.movement.destination()) < 4) && npc().valid();
	}
}
