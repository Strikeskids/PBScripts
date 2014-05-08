package org.logicail.rsbot.scripts.framework.context.providers.farming.farmingobject;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingDefinition;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingHelper;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingObject;
import org.logicail.rsbot.scripts.framework.context.providers.farming.enums.CactusEnum;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 16/04/2014
 * Time: 00:26
 */
public class Cactus extends FarmingObject<Cactus.CactusType> implements IFruit, ICheckHealth, IGrowthStage, IWeeds, ICanDie {
	public Cactus(IClientContext ctx, CactusEnum patch) {
		super(ctx, patch.id());
	}

	@Override
	public boolean checkHealth() {
		return FarmingHelper.checkHealth(this);
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
		final CactusType type = type();
		return type != CactusType.CACTUS_PATCH && definition().containsModel(type.numberOfStages() - 1);
	}

	@Override
	public CactusType type() {
		final String name = definition().name().toLowerCase();
		for (CactusType cropType : CactusType.values()) {
			if (name.contains(cropType.name().toLowerCase().replace('_', ' '))) {
				return cropType;
			}
		}

		return CactusType.CACTUS_PATCH;
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
	public int stage() {
		final FarmingDefinition definition = definition();
		final CactusType type = type();
		if (type == CactusType.CACTUS_PATCH) {
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

	public enum CactusType {
		CACTUS_PATCH(new int[]{-1}, new int[]{-1}),
		PRICKLY_PEAR_CACTUS(new int[]{85893, 85881, 85897, 85903, 85890, 85882, 85891, 85898}, new int[]{85888}),
		POTATO_CACTUS(new int[]{85871, 85865, 85864, 85868, 85872, 85858, 85867, 85873}, new int[]{85876, 85866, 85862}),
		CACTUS(new int[]{7827, 7828, 7829, 7830, 7831, 7832, 7833, 7834}, new int[]{7835, 7836, 7837});

		private final int[] stages;
		private final int[] fruitStages;

		public int numberOfStages() {
			return stages.length;
		}

		CactusType(int[] stages, int[] fruitStages) {
			this.stages = stages;
			this.fruitStages = fruitStages;
		}
	}
}
