package org.logicail.rsbot.scripts.loggildedaltar.tasks.banking;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 06/01/14
 * Time: 19:44
 */
public class CloseBank extends BankingAbstract {
	public CloseBank(Banking script) {
		super(script);
	}

	@Override
	public String toString() {
		return "Banking: Close";
	}

	@Override
	public boolean isValid() {
		return ctx.bank.isOpen();
	}

	@Override
	public void run() {

	}
}
