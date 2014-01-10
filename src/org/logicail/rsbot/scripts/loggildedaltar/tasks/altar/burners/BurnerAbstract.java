package org.logicail.rsbot.scripts.loggildedaltar.tasks.altar.burners;

import org.logicail.rsbot.scripts.loggildedaltar.tasks.LogGildedAltarTask;
import org.logicail.rsbot.scripts.loggildedaltar.tasks.altar.AltarLightBurnersTask;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 05/01/14
 * Time: 18:19
 */
public abstract class BurnerAbstract extends LogGildedAltarTask {
	protected final AltarLightBurnersTask burnersTask;

	public BurnerAbstract(AltarLightBurnersTask burnersTask) {
		super(burnersTask.script);
		this.burnersTask = burnersTask;
	}
}
