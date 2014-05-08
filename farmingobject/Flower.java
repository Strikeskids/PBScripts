package org.logicail.rsbot.scripts.framework.context.providers.farming.farmingobject;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingHelper;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingObject;
import org.logicail.rsbot.scripts.framework.context.providers.farming.enums.FlowerEnum;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.ICanDie;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.ICanWater;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.IWeeds;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 16/04/2014
 * Time: 00:03
 */
public class Flower extends FarmingObject<Flower.FlowerType> implements ICanWater, IWeeds, ICanDie {
	private static final int MODEL_WATERED = 7783;

	public Flower(IClientContext context, FlowerEnum patch) {
		super(context, patch.id());
	}

	@Override
	public boolean canWater() {
		return !watered() && !grown() && type() != FlowerType.FLOWER_PATCH;
	}

	@Override
	protected boolean grown() {
		final FlowerType type = type();
		return type != FlowerType.FLOWER_PATCH && definition().containsModel(type.numberOfStages() - 1);
	}

	@Override
	public FlowerType type() {
		final String name = definition().name().toLowerCase();
		for (FlowerType cropType : FlowerType.values()) {
			if (name.contains(cropType.name().toLowerCase().replace('_', ' '))) {
				return cropType;
			}
		}

		return FlowerType.FLOWER_PATCH;
	}

	@Override
	public boolean watered() {
		return definition().containsModel(MODEL_WATERED);
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

	public enum FlowerType {
		FLOWER_PATCH(-1),
		MARIGOLD(7851, 7852, 7853, 7854, 7855),
		ROSEMARY(7861, 7862, 7863, 7864, 7865),
		NASTURTIUM(7856, 7857, 7858, 7859, 7860),
		WOAD_PLANT(7866, 7867, 7868, 7869, 7870),
		LIMPWURT_PLANT(7847, 7848, 7849, 7850, 7119),
		SCARECROW(7801, 7800, 7898, 7812),
		LILY(44872, 14167, 44873, 44871, 44870);

		private final int[] stages;

		public int numberOfStages() {
			return stages.length;
		}

		FlowerType(int... stages) {
			this.stages = stages;
		}
	}
}
