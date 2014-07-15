package org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.banking;

import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.LogGildedAltarTask;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 06/01/14
 * Time: 19:45
 */
public abstract class BankingAbstract extends LogGildedAltarTask {
	protected final Banking bankingBranch;

	public BankingAbstract(Banking bankingBranch) {
		super(bankingBranch.script);
		this.bankingBranch = bankingBranch;
	}
}
