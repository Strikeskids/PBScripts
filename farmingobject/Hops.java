package org.logicail.rsbot.scripts.framework.context.providers.farming.farmingobject;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingDefinition;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingHelper;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingObject;
import org.logicail.rsbot.scripts.framework.context.providers.farming.enums.HopsEnum;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.ICanDie;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.ICanWater;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.IGrowthStage;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.IWeeds;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 08/05/2014
 * Time: 18:14
 */
public class Hops extends FarmingObject<Hops.HopsType, HopsEnum> implements IGrowthStage, ICanWater, IWeeds, ICanDie {
	private static final int MODEL_ID_WATERED = 8222;

	public Hops(IClientContext ctx, HopsEnum patch) {
		super(ctx, patch);
	}

	@Override
	public boolean canWater() {
		return !watered() && !grown() && type() != HopsType.HOPS_PATCH;
	}

	@Override
	public boolean grown() {
		return definition().containsAction("Harvest");
	}

	@Override
	public HopsType type() {
		final String name = definition().name().toLowerCase();
		for (HopsType cropType : HopsType.values()) {
			if (name.contains(cropType.name().toLowerCase().replace('_', ' '))) {
				return cropType;
			}
		}

		return HopsType.HOPS_PATCH;
	}

	@Override
	public boolean watered() {
		return definition().containsModel(MODEL_ID_WATERED);
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
	public int weeds() {
		return FarmingHelper.weeds(this);
	}

	@Override
	public int stage() {
		final FarmingDefinition definition = definition();
		final HopsType type = type();
		if (type == HopsType.HOPS_PATCH) {
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

	public enum HopsType {
		// TODO: Can't get IFruit from models,
		HOPS_PATCH(),
		HAMMERSTONE(8197, 8799, 8201, 8203, 8205),
		ASGARNIAN(8197, 8199, 8201, 8203, 8204, 8205),
		YANILLIAN(8197, 8199, 8200, 8201, 8202, 8204, 8205),
		KRANDORIAN(8197, 8198, 8199, 8201, 8202, 8203, 8204, 8205),
		WILDBLOOD(8197, 8198, 8199, 8200, 8201, 8202, 8203, 8204, 8205),
		BARLEY(8186, 8187, 8188, 8189, 8190),
		JUTE(7876, 7877, 7878, 7879, 7880, 7881),
		VINE_FRAME(),
		GRAPEVINES(85931, 85922, 85924, 85936), // DEAD are 85929, 85921, 85934 // Harvest IFruit 85919, 85920, 85918, 85930, 85935, 85933, 85928, 85932
		REEDS(85944, 85938, 85948, 85947, 85943); // DEAD 85941, 85940, 85951 // DISEASED 85949, 85946, 85942 // Harvest 85945, 85939, 85950
		private final int[] stages;

		HopsType(int... stages) {
			this.stages = stages;
		}
	}
}