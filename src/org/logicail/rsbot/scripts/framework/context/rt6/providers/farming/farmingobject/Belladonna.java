package org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.farmingobject;

import org.logicail.rsbot.scripts.framework.context.rt6.IClientContext;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.FarmingDefinition;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.FarmingHelper;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.FarmingObject;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.enums.BelladonnaEnum;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.interfaces.ICanDie;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.interfaces.IGrowthStage;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.interfaces.IWeeds;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 08/05/2014
 * Time: 09:04
 */
public class Belladonna extends FarmingObject<Belladonna.BelladonnaType, BelladonnaEnum> implements IGrowthStage, IWeeds, ICanDie {
	public Belladonna(IClientContext ctx, BelladonnaEnum patch) {
		super(ctx, patch);
	}

	@Override
	public boolean dead() {
		return FarmingHelper.dead(this);
	}

	@Override
	public boolean diseased() {
		return FarmingHelper.diseased(this);
	}

	@Override
	public boolean grown() {
		return definition().containsAction("Pick");
	}

	@Override
	public int stage() {
		final FarmingDefinition definition = definition();
		final BelladonnaType type = type();
		if (type == BelladonnaType.BELLADONNA_PATCH) {
			return 0;
		}

		int[] stages = type.stages;
		for (int i = 0; i < stages.length; i++) {
			if (definition.containsModel(stages[i])) {
				return i + 1;
			}
		}

		return 0;
	}

	@Override
	public BelladonnaType type() {
		final String name = definition().name().toLowerCase();

		if (!name.contains("patch")) {
			return BelladonnaType.BELLADONNA;
		}

		return BelladonnaType.BELLADONNA_PATCH;
	}

	@Override
	public int weeds() {
		return FarmingHelper.weeds(this);
	}

	public enum BelladonnaType {
		BELLADONNA_PATCH(),
		BELLADONNA(7901, 7902, 7903, 7904, 7905);

		private final int[] stages;

		BelladonnaType(int... stages) {
			this.stages = stages;
		}
	}
}