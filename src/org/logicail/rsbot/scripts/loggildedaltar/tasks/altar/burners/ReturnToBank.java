package org.logicail.rsbot.scripts.loggildedaltar.tasks.altar.burners;

import org.logicail.rsbot.scripts.loggildedaltar.tasks.altar.AltarLightBurnersTask;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 05/01/14
 * Time: 19:15
 */
public class ReturnToBank extends BurnerAbstract {
	public ReturnToBank(AltarLightBurnersTask burnersTask) {
		super(burnersTask);
	}

	@Override
	public String toString() {
		return "Not lit and I can't light them!";
	}

	@Override
	public boolean isValid() {
		return options.lightBurners;
	}

	@Override
	public void run() {
		options.status = "Burners can't be lit";
		script.bankingTask.setBanking(true);
	}
}
