package org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.farmingobject;

import org.logicail.rsbot.scripts.framework.context.rt6.IClientContext;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.FarmingDefinition;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.util.FarmingHelper;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.enums.MushroomEnum;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.interfaces.ICanDie;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.interfaces.IFruit;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.interfaces.IWeeds;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 08/05/2014
 * Time: 12:26
 */
public class Mushroom extends FarmingObject<Mushroom.MushroomType, MushroomEnum> implements IFruit, IWeeds, ICanDie {
	public static final int[] MODEL_IDS_GROWTH_STAGE = {7871, 7872, 7873, 7874, 7875};
	public static final int[] MODEL_IDS_GROWTH_STAGE_TROLLHEIM = {19144, 19150, 19143, 19149, 19140};

	public Mushroom(IClientContext ctx, MushroomEnum patch) {
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
		return definition().containsAction("Pick");
	}

	@Override
	public MushroomType type() {
		final String name = definition().name().toLowerCase();
		for (MushroomType cropType : MushroomType.values()) {
			if (name.contains(cropType.name().toLowerCase().replace('_', ' '))) {
				return cropType;
			}
		}

		return MushroomType.MUSHROOM_PATCH;
	}

	@Override
	public int weeds() {
		return FarmingHelper.weeds(this);
	}

	public enum MushroomType {
		MUSHROOM_PATCH(),
		BITTERCAP(7894, 7896, 7897, 7899, 7900, 7898),
		MORCHELLA(52852, 52907, 52947, 52842, 52868, 52899, 52877, 52918, 52891);

		private final int[] fruitStages;

		public int numberOfFruitStages() {
			return fruitStages.length;
		}

		MushroomType(int... fruitStages) {
			this.fruitStages = fruitStages;
		}
	}
}
