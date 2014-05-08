package org.logicail.rsbot.scripts.framework.context.providers.farming.farmingobject;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingDefinition;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingHelper;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingObject;
import org.logicail.rsbot.scripts.framework.context.providers.farming.enums.FruitTreeEnum;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 14/04/2014
 * Time: 21:00
 */
public class FruitTree extends FarmingObject<FruitTree.FruitTreeType, FruitTreeEnum> implements IFruit, IStump, ICheckHealth, IWeeds, ICanDie {
	public FruitTree(IClientContext ctx, FruitTreeEnum patch) {
		super(ctx, patch);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[").append(bits()).append("]");
		sb.append(" ").append(type());
		if (checkHealth()) sb.append(" check health");
		if (grown()) sb.append(" grown");
		if (grown() && type() != FruitTreeType.FRUIT_TREE_PATCH) sb.append(" fruit: ").append(fruit());

		return sb.toString();
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

	/**
	 * Can the tree be chopped down or check-healthed
	 *
	 * @return <tt>true</tt> if the tree has finished growing, otherwise <tt>false</tt>
	 */
	@Override
	public boolean grown() {
		final FruitTreeType type = type();
		return type.grownModel > -1 && definition().containsModel(type.grownModel);
	}

	/**
	 * Get the type of tree growing
	 *
	 * @return the type of trr growing, or ALLOTMENT if nothing is growing
	 */
	@Override
	public FruitTreeType type() {
		final String name = definition().name().toLowerCase();
		for (FruitTreeType cropType : FruitTreeType.values()) {
			if (name.contains(cropType.name().toLowerCase().replace('_', ' '))) {
				return cropType;
			}
		}

		return FruitTreeType.FRUIT_TREE_PATCH;
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
	public boolean stump() {
		return FarmingHelper.stump(this);
	}

	@Override
	public int weeds() {
		return FarmingHelper.weeds(this);
	}

	public enum FruitTreeType {
		FRUIT_TREE_PATCH(null, -1),
		APPLE("Pick-apple", 7921, 7923, 7924, 7925, 7926, 7927, 7928),
		BANANA("Pick-banana", 7935, 7936, 7937, 7938, 7939, 7940, 7941),
		CURRY("Pick-leaf", 7969, 7970, 7971, 7972, 7973, 7974, 7975),
		ORANGE("Pick-orange", 7969, 8056, 8057, 8058, 8059, 8060, 8061),
		PALM("Pick-coconut", 8069, 8070, 8071, 8072, 8073, 8074, 8075),
		PAPAYA("Pick-fruit", 8083, 8084, 8085, 8086, 8087, 8088, 8089),
		PINEAPPLE("Pick-pineapple", 8097, 8098, 8099, 8100, 8101, 8102, 8103);

		private final String pickAction;
		private final int grownModel;
		private final int[] fruitStages;

		FruitTreeType(String pickAction, int grownModel, int... fruitStages) {
			this.pickAction = pickAction;
			this.grownModel = grownModel;
			this.fruitStages = fruitStages;
		}
	}
}
