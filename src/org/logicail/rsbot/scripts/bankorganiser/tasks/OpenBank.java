package org.logicail.rsbot.scripts.bankorganiser.tasks;

import org.logicail.rsbot.scripts.bankorganiser.LogBankOrganiser;
import org.logicail.rsbot.scripts.framework.tasks.Node;

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
	public boolean isValid() {
		return !ctx.bank.opened();
	}

	@Override
	public void run() {
		ctx.bank.open();
		sleep(500);
	}
}
