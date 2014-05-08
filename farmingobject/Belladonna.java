package org.logicail.rsbot.scripts.framework.context.providers.farming.farmingobject;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingDefinition;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingHelper;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingObject;
import org.logicail.rsbot.scripts.framework.context.providers.farming.enums.BelladonnaEnum;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.IGrowthStage;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.IWeeds;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 08/05/2014
 * Time: 09:04
 */
public class Belladonna extends FarmingObject<Belladonna.BelladonnaType> implements IGrowthStage, IWeeds {
	public Belladonna(IClientContext ctx, BelladonnaEnum patch) {
		super(ctx, patch.id());
	}

	public boolean grown() {
		return definition().containsAction("Pick");
	}

	@Override
	public BelladonnaType type() {
		final String name = definition().name().toLowerCase();

		if (!name.contains("patch")) {
			return BelladonnaType.BELLADONNA;
		}

		return BelladonnaType.BELLADONNA_PATCH;
	}

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