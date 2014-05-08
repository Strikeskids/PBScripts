package org.logicail.rsbot.scripts.framework.context.providers.farming.farmingobject;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingHelper;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingObject;
import org.logicail.rsbot.scripts.framework.context.providers.farming.enums.HopsEnum;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.ICanDie;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.ICanWater;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.IWeeds;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 08/05/2014
 * Time: 18:14
 */
public class Hops extends FarmingObject<Hops.HopsType, HopsEnum> implements ICanWater, IWeeds, ICanDie {
	private static final int MODEL_ID_WATERED = 8222;

	public Hops(IClientContext ctx, HopsEnum patch) {
		super(ctx, patch);
	}

	@Override
	public boolean canWater() {
		return !watered() && !grown() && type() != HopsType.HOPS_PATCH;
	}

	@Override
	public boolean grown() {
		return definition().containsAction("Harvest");
	}

	@Override
	public HopsType type() {
		final String name = definition().name().toLowerCase();
		for (HopsType cropType : HopsType.values()) {
			if (name.contains(cropType.name().toLowerCase().replace('_', ' '))) {
				return cropType;
			}
		}

		return HopsType.HOPS_PATCH;
	}

	@Override
	public boolean watered() {
		return definition().containsModel(MODEL_ID_WATERED);
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

	public enum HopsType {
		HOPS_PATCH,
		HAMMERSTONE,
		ASGARNIAN,
		YANILLIAN,
		KRANDORIAN,
		WILDBLOOD,
		BARLEY,
		JUTE,
		VINE_FRAME,
		GRAPEVINES,
		REEDS
	}
}