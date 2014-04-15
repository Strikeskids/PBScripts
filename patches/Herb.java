package org.logicail.rsbot.scripts.framework.context.providers.farming.patches;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.farming.CropState;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingDefinition;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingObject;
import org.logicail.rsbot.scripts.framework.context.providers.farming.enums.HerbEnum;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 14/04/2014
 * Time: 17:18
 */
public class Herb extends FarmingObject {
	public static final int[] MODEL_IDS_GROWTH_STAGE = {7871, 7872, 7873, 7874, 7875};
	public static final int[] MODEL_IDS_GROWTH_STAGE_TROLLHEIM = {19144, 19150, 19143, 19149, 19140};

	public Herb(IClientContext ctx, HerbEnum patch) {
		super(ctx, patch.id());
	}

	public void repaint(Graphics2D g, int x, int y) {
		g.setColor(state().color());
		g.fillRect(x, y, 9, 9);
		g.setStroke(new BasicStroke(1));
		g.setColor(Color.gray);
		g.drawRect(x, y, 9, 9);
		g.drawRect(x, y, 9, 9);
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

	public CropState state() {
		if (weeds() > 0) {
			return CropState.WEEDS;
		}

		if (empty()) {
			return CropState.EMPTY;
		}

		if (diseased()) {
			return CropState.DISEASED;
		}

		if (dead()) {
			return CropState.DEAD;
		}

		if (grown()) {
			return CropState.READY;
		}

		return CropState.GROWING;
	}

	/**
	 * Is the patch dead
	 *
	 * @return <tt>true</tt> if the herb has died, otherwise <tt>false</tt>
	 */
	public boolean dead() {
		return definition().name().startsWith("Dead");
	}

	/**
	 * Is the patch diseased
	 *
	 * @return <tt>true</tt> if the patch iss diseases, otherwise <tt>false</tt>
	 */
	public boolean diseased() {
		return definition().name().startsWith("Diseased");
	}

	/**
	 * Is the patch weed free but empty (ready to plant seed)
	 *
	 * @return <tt>true</tt> if patch has no weeeds and is ready to plant a seed, otherwise <tt>false</tt>
	 */
	public boolean empty() {
		return bits() == 3;
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
	 * Number of weeds on patch
	 *
	 * @return 3 to 0
	 */
	public int weeds() {
		switch (bits()) {
			case 0:
				return 3;
			case 1:
				return 2;
			case 2:
				return 1;
		}

		return 0;
	}
}
