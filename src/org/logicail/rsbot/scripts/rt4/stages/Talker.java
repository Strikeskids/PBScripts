package org.logicail.rsbot.scripts.rt4.stages;

import com.logicail.wrappers.NpcDefinition;
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

	@Override
	public String toString() {
		return name;
	}

	public Talker(IClientContext ctx, String name) {
		super(ctx);
		this.name = name;
	}

	@Override
	public void run() {
		if (tryContinue()) return;

		tryTalk();
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

	protected boolean tryContinue() {
		if (ctx.chat.queryContinue()) {
			log.info("[" + name + "] Continue");
			ctx.chat.clickContinue();
			return true;
		}
		return false;
	}

	protected Npc npc() {
		return ctx.npcs.select().select(NpcDefinition.filter(ctx, name)).select(new Filter<Npc>() {
			@Override
			public boolean accept(Npc npc) {
				return npc.tile().matrix(ctx).reachable();
			}
		}).nearest().poll();
	}

	@Override
	public boolean valid() {
		return ctx.players.local().animation() == -1 && !ctx.players.local().inMotion() && npc().valid();
	}
}
