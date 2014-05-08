package org.logicail.rsbot.scripts.framework.context.providers.farming.farmingobject;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingDefinition;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingHelper;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingObject;
import org.logicail.rsbot.scripts.framework.context.providers.farming.enums.SpiritTreeEnum;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.ICanDie;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.ICheckHealth;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.IGrowthStage;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.IWeeds;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 15/04/2014
 * Time: 23:35
 */
public class SpiritTree extends FarmingObject<SpiritTree.SpiritTreeType> implements IGrowthStage, ICheckHealth, IWeeds, ICanDie {
	public SpiritTree(IClientContext ctx, SpiritTreeEnum patch) {
		super(ctx, patch.id());
	}

	@Override
	public boolean checkHealth() {
		return FarmingHelper.checkHealth(this);
	}

	@Override
	protected boolean grown() {
		final SpiritTreeType type = type();
		return type != SpiritTreeType.SPIRIT_TREE_PATCH && definition().containsModel(type.numberOfStages() - 1);
	}

	@Override
	public SpiritTreeType type() {
		final String name = definition().name().toLowerCase();
		for (SpiritTreeType cropType : SpiritTreeType.values()) {
			if (name.contains(cropType.name().toLowerCase().replace('_', ' '))) {
				return cropType;
			}
		}

		return SpiritTreeType.SPIRIT_TREE_PATCH;
	}

	@Override
	public int stage() {
		final FarmingDefinition definition = definition();
		final SpiritTreeType type = type();
		if (type == SpiritTreeType.SPIRIT_TREE_PATCH) {
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

	@Override
	public boolean dead() {
		return FarmingHelper.dead(this);
	}

	@Override
	public boolean diseased() {
		return FarmingHelper.diseased(this);
	}

	public enum SpiritTreeType {
		SPIRIT_TREE_PATCH(-1),
		SPIRIT_TREE(7977, 7991, 8112, 8114, 8116, 8118, 8120, 8122, 8124, 8104, 8106, 8108, 19923);

		private final int[] stages;

		public int numberOfStages() {
			return stages.length;
		}

		SpiritTreeType(int... stages) {
			this.stages = stages;
		}
	}
}
