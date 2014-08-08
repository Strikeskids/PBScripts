package org.logicail.rsbot.scripts.rt6.bankorganiser.tasks;

import org.logicail.rsbot.scripts.rt6.bankorganiser.LogBankOrganiser;
import org.logicail.rsbot.scripts.framework.tasks.Node;
import org.powerbot.script.Condition;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 08/03/14
 * Time: 12:53
 */
public class OpenBank extends Node<LogBankOrganiser> {
	public OpenBank(LogBankOrganiser script) {
		super(script);
	}

	@Override
	public String toString() {
		return "Open Bank";
	}

	@Override
	public boolean valid() {
		return !ctx.bank.opened();
	}

	@Override
	public void run() {
		ctx.bank.open();
		Condition.sleep(500);
	}
}
