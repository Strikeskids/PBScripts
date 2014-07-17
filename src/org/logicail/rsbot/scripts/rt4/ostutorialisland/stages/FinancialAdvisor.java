package org.logicail.rsbot.scripts.rt4.ostutorialisland.stages;

import com.logicail.wrappers.ObjectDefinition;
import org.logicail.rsbot.scripts.framework.context.rt4.IClientContext;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.GameObject;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 10/07/2014
 * Time: 21:18
 */
public class FinancialAdvisor extends Talker {
	public FinancialAdvisor(IClientContext ctx) {
		super(ctx, "Financial Advisor");
	}

	@Override
	public void run() {
		if (tryContinue()) return;

		ctx.inventory.deselect();

		if (ctx.chat.visible("Continue through the next door.") || stage() > 15) {
			leave();
			return;
		}

		super.run();
	}

	@Override
	protected void enter() {

	}

	@Override
	protected void leave() {
		final GameObject bank = ctx.objects.select().select(ObjectDefinition.name(ctx, "Bank booth")).nearest().poll();
		if (bank.valid()) {
			final GameObject door = Banker.doorsInYByX(ctx, bank).reverse().poll();
			if (ctx.camera.prepare(door) && door.click("Open")) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !valid() || ctx.chat.visible("Follow the path to the chapel");
					}
				}, 200, 20);
				Condition.sleep(200);
			}
		}
	}
}
