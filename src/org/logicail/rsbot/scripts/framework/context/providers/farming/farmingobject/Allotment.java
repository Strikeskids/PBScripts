package org.logicail.rsbot.scripts.framework.context.providers.farming.farmingobject;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.farming.CropState;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingObject;
import org.logicail.rsbot.scripts.framework.context.providers.farming.enums.AllotmentEnum;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.ICanWater;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 13/04/2014
 * Time: 21:29
 */
public class Allotment extends FarmingObject<Allotment.CropType> implements ICanWater {
	// An allotment is made up of several instances of the 1x1 tile dynamic object
	// You should use nearest() & shuffle() and not always interact with the one at the tile()

	private static final Polygon POLYGON_ALLOTMENT_1 = new Polygon(new int[]{0, 21, 21, 9, 9, 0}, new int[]{0, 0, 9, 9, 25, 25}, 6);
	private static final Polygon POLYGON_ALLOTMENT_2 = new Polygon(new int[]{12, 21, 21, 0, 0, 12}, new int[]{0, 0, 25, 25, 16, 16}, 6);
	private static final Polygon POLYGON_ALLOTMENT_RECTANGLE_1 = new Polygon(new int[]{0, 41, 41, 9, 9, 0}, new int[]{0, 0, 9, 9, 13, 13}, 6);
	private static final Polygon POLYGON_ALLOTMENT_RECTANGLE_2 = new Polygon(new int[]{0, 9, 9, 41, 41, 0}, new int[]{0, 0, 4, 4, 13, 13}, 6);

	private static final int MODEL_ID_WATERED = 8222;
	private final Polygon polygon;

	public Allotment(IClientContext ctx, AllotmentEnum patch) {
		super(ctx, patch.id());

		switch (patch) {
			case ARDOUGNE_N:
			case CATHERBY_N:
				polygon = POLYGON_ALLOTMENT_RECTANGLE_1;
				break;
			case ARDOUGNE_S:
			case CATHERBY_S:
				polygon = POLYGON_ALLOTMENT_RECTANGLE_2;
				break;
			case FALADOR_N:
			case PORT_PHASMATYS_N:
				polygon = POLYGON_ALLOTMENT_1;
				break;
			case FALADOR_S:
			case PORT_PHASMATYS_S:
				polygon = POLYGON_ALLOTMENT_2;
				break;
			default:
				polygon = POLYGON_ALLOTMENT_2;
		}
	}

	public boolean canWater() {
		return !watered() && !grown() && type() != CropType.ALLOTMENT;
	}

	public boolean grown() {
		return definition().containsAction("Harvest");
	}

	public CropType type() {
		final String name = definition().name().toLowerCase();
		for (CropType cropType : CropType.values()) {
			if (name.contains(cropType.name().toLowerCase().replace('_', ' '))) {
				return cropType;
			}
		}

		return CropType.ALLOTMENT;
	}

	public boolean watered() {
		return definition().containsModel(MODEL_ID_WATERED);
	}

	@Override
	public void repaint(Graphics2D g, int x, int y) {
		Polygon p = new Polygon(polygon.xpoints, polygon.ypoints, polygon.npoints); // TODO: Improve this
		p.translate(x, y);
		g.setColor(state().color());
		g.fill(p);
		g.setColor(Color.gray);
		g.draw(p);
	}

	public CropState state() {
		if (watered()) {
			return CropState.WATERED;
		}

		return super.state();
	}

	public enum CropType {
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
