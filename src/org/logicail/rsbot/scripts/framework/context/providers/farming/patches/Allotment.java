package org.logicail.rsbot.scripts.framework.context.providers.farming.patches;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingObject;
import org.logicail.rsbot.scripts.framework.context.providers.farming.enums.AllotmentEnum;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 13/04/2014
 * Time: 21:29
 */
public class Allotment extends FarmingObject {
	// An allotment is made up of several instances of the 1x1 tile dynamic object
	// You should use nearest() & shuffle() and not always interact with the one at the tile()

	private static final int MODEL_ID_WATERED = 8222;

	public Allotment(IClientContext ctx, AllotmentEnum patch) {
		super(ctx, ctx.farming.dynamic(patch.id()));
	}

	public boolean canWater() {
		return !watered() && !grown() && type() != Crop.ALLOTMENT;
	}

	/**
	 * Can the patch be harvested
	 *
	 * @return <tt>true</tt> if the allotment has finished growing and can be harvested, otherwise <tt>false</tt>
	 */
	public boolean grown() {
		return definition().containsAction("Harvest");
	}

	/**
	 * Get the type of crop growing in the allotment
	 *
	 * @return the type of crop growing in the allotment, or ALLOTMENT if nothing is growing
	 */
	public Crop type() {
		final String name = definition().name().toLowerCase();
		for (Crop crop : Crop.values()) {
			if (name.contains(crop.name().toLowerCase().replace('_', ' '))) {
				return crop;
			}
		}

		return Crop.ALLOTMENT;
	}

	public boolean watered() {
		return definition().containsModel(MODEL_ID_WATERED);
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

	public enum Crop {
		ALLOTMENT,
		POTATO,
		ONION,
		CABBAGES,
		TOMATO,
		SWEETCORN,
		STRAWBERRY,
		WATERMELON,
		FLY_TRAPS,
		SNAPE_GRASS,
		SUNCHOKES
	}
}
