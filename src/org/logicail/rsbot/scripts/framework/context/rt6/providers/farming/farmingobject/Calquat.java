package org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.farmingobject;

import org.logicail.rsbot.scripts.framework.context.rt6.IClientContext;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.FarmingDefinition;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.util.FarmingHelper;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.enums.CalquatEnum;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.interfaces.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 15/04/2014
 * Time: 22:48
 */
public class Calquat extends FarmingObject<Calquat.CalquatType, CalquatEnum> implements IFruit, ICheckHealth, IGrowthStage, IWeeds, ICanDie {
	public Calquat(IClientContext ctx, CalquatEnum patch) {
		super(ctx, patch);
	}

	@Override
	public boolean checkHealth() {
		return FarmingHelper.checkHealth(this);
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
	public int fruit() {
		if (!grown()) {
			return 0;
		}

		final FarmingDefinition definition = definition();

		int[] fruitStages = type().fruitStages;
		for (int i = 0; i < fruitStages.length; i++) {
			if (definition.containsModel(fruitStages[i])) {
				return i + 1;
			}
		}

		return 0;
	}

	@Override
	public boolean grown() {
		final CalquatType type = type();
		return type != CalquatType.CALQUAT_PATCH && definition().containsModel(type.numberOfStages() - 1);
	}

	@Override
	public CalquatType type() {
		final String name = definition().name().toLowerCase();
		for (CalquatType cropType : CalquatType.values()) {
			if (name.contains(cropType.name().toLowerCase().replace('_', ' '))) {
				return cropType;
			}
		}

		return CalquatType.CALQUAT_PATCH;
	}

	@Override
	public int stage() {
		final FarmingDefinition definition = definition();
		final CalquatType type = type();
		if (type == CalquatType.CALQUAT_PATCH) {
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

	public enum CalquatType {
		CALQUAT_PATCH(new int[]{-1}, new int[]{-1}),
		CALQUAT(new int[]{7949, 7950, 7951, 7952, 7953, 7954, 7956, 7958, 7960}, new int[]{7943, 7944, 7945, 7946, 7947, 7948});

		private final int[] stages;
		private final int[] fruitStages;

		public int numberOfStages() {
			return stages.length;
		}

		CalquatType(int[] stages, int[] fruitStages) {
			this.stages = stages;
			this.fruitStages = fruitStages;
		}
	}
}
