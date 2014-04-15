package org.logicail.rsbot.scripts.framework.context.providers.farming.patches;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.farming.CropState;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingObject;
import org.logicail.rsbot.scripts.framework.context.providers.farming.enums.AllotmentEnum;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 13/04/2014
 * Time: 21:29
 */
public class Allotment extends FarmingObject {
	// An allotment is made up of several instances of the 1x1 tile dynamic object
	// You should use nearest() & shuffle() and not always interact with the one at the tile()

	private static final Polygon POLYGON_ALLOTMENT_1 = new Polygon(new int[]{0, 16, 16, 7, 7, 0}, new int[]{0, 0, 7, 7, 19, 19}, 6);
	private static final Polygon POLYGON_ALLOTMENT_2 = new Polygon(new int[]{9, 16, 16, 0, 0, 9}, new int[]{0, 0, 19, 19, 12, 12}, 6);
	private static final Polygon POLYGON_ALLOTMENT_RECTANGLE_1 = new Polygon(new int[]{0, 33, 33, 7, 7, 0}, new int[]{0, 0, 7, 7, 11, 11}, 6);
	private static final Polygon POLYGON_ALLOTMENT_RECTANGLE_2 = new Polygon(new int[]{0, 7, 7, 33, 33, 0}, new int[]{0, 0, 4, 4, 11, 11}, 6);

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
		g.setStroke(new BasicStroke(1));
		g.setColor(Color.gray);
		g.draw(p);
	}

	public CropState state() {
		if (weeds() > 0) {
			return CropState.WEEDS;
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

		if (watered()) {
			return CropState.WATERED;
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
