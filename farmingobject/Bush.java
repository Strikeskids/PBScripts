package org.logicail.rsbot.scripts.framework.context.providers.farming.farmingobject;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingDefinition;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingHelper;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingObject;
import org.logicail.rsbot.scripts.framework.context.providers.farming.enums.BushEnum;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 08/05/2014
 * Time: 11:24
 */
public class Bush extends FarmingObject<Bush.BushType> implements ICheckHealth, IGrowthStage, IWeeds, ICanDie, IFruit {
	private static final int[] MODELS_FRUIT = {7816, 7813, 7814, 7815};
	private static final int BARBERRY_FRUIT_MODEL = 85831;

	public Bush(IClientContext ctx, BushEnum bushEnum) {
		super(ctx, bushEnum.id());
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

	public boolean empty() {
		return type() == BushType.BUSH_PATCH;
	}

	public BushType type() {
		final String name = definition().name().toLowerCase();
		for (BushType cropType : BushType.values()) {
			if (name.contains(cropType.name().toLowerCase().replace('_', ' '))) {
				return cropType;
			}
		}

		return BushType.BUSH_PATCH;
	}

	@Override
	public int fruit() {
		if (!grown()) {
			return 0;
		}

		final FarmingDefinition definition = definition();

		final BushType type = type();
		if (type == BushType.BARBERRY) {
			return definition.containsModel(BARBERRY_FRUIT_MODEL) ? 1 : 0;
		}

		for (int i = 0; i < MODELS_FRUIT.length; i++) {
			if (definition.containsModel(MODELS_FRUIT[i])) {
				return i + 1;
			}
		}

		return 0;
	}

	@Override
	public boolean grown() {
		return definition().containsAction("Pick-from");
	}

	public int stage() {
		final FarmingDefinition definition = definition();
		final BushType type = type();
		if (type == BushType.BUSH_PATCH) {
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

	public enum BushType {
		BUSH_PATCH(-1),
		REDBERRY(7817, 7819, 7821, 7823, 7825, 7826),
		CADAVABERRY(7817, 7819, 7820, 7821, 7823, 7825, 7826),
		DWELLBERRY(7817, 7818, 7819, 7820, 7822, 7823, 7825, 7826),
		JANGERBERRY(7817, 7818, 7819, 7820, 7822, 7823, 7824, 7825, 7826),
		WHITEBERRY(7817, 7818, 7819, 7820, 7822, 7823, 7824, 7825, 7826),
		WISHING_WELL(85839, 85855, 85837, 85838, 85847, 85840),
		POISON_IVY(7817, 7818, 7819, 7820, 7822, 7823, 7824, 7825, 7826),
		BARBERRY(85821, 85832, 85827, 85822, 85817, 85829);

		private final int[] stages;

		public int numberOfStages() {
			return stages.length;
		}

		BushType(int... stages) {
			this.stages = stages;
		}
	}
}