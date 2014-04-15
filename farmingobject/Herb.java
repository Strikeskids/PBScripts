package org.logicail.rsbot.scripts.framework.context.providers.farming.farmingobject;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingDefinition;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingObject;
import org.logicail.rsbot.scripts.framework.context.providers.farming.enums.HerbEnum;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 14/04/2014
 * Time: 17:18
 */
public class Herb extends FarmingObject<Herb.HerbType> {
	public static final int[] MODEL_IDS_GROWTH_STAGE = {7871, 7872, 7873, 7874, 7875};
	public static final int[] MODEL_IDS_GROWTH_STAGE_TROLLHEIM = {19144, 19150, 19143, 19149, 19140};

	public Herb(IClientContext ctx, HerbEnum patch) {
		super(ctx, patch.id());
	}

	/**
	 * Can the patch be picked
	 *
	 * @return <tt>true</tt> if the patch has finished growing and can be picked, otherwise <tt>false</tt>
	 */
	public boolean grown() {
		return definition().containsAction("Pick");
	}

	/**
	 * Growth stage of patch
	 *
	 * @return 0 (empty) to 5 (grown)
	 */
	public int stage() {
		final FarmingDefinition definition = definition();

		int[] model_ids_growth_stage = this.id() == HerbEnum.TROLLHEIM.id() ? MODEL_IDS_GROWTH_STAGE_TROLLHEIM : MODEL_IDS_GROWTH_STAGE;
		for (int i = 0; i < model_ids_growth_stage.length; i++) {
			if (definition.containsModel(model_ids_growth_stage[i])) {
				return i + 1;
			}
		}

		return 0;
	}

	@Override
	public HerbType type() {
		return HerbType.UNKNOWN;
	}

	public enum HerbType {
		UNKNOWN // TODO
	}
}
