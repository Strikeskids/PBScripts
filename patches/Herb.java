package org.logicail.rsbot.scripts.framework.context.providers.farming.patches;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.farming.CropState;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingDefinition;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingObject;
import org.logicail.rsbot.scripts.framework.context.providers.farming.IFarming;
import org.logicail.rsbot.scripts.framework.context.providers.farming.enums.HerbEnum;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 14/04/2014
 * Time: 17:18
 */
public class Herb extends FarmingObject {
	public Herb(IClientContext ctx, HerbEnum patch) {
		super(ctx, ctx.farming.dynamic(patch.id()));
	}

	//private static final Polygon POLYGON_ALLOTMENT_1 = new Polygon(new int[]{0, 15, 15, 6, 6, 0}, new int[]{0, 0, 6, 6, 18, 18}, 6);
	//private static final Polygon POLYGON_ALLOTMENT_2 = new Polygon(new int[]{9, 15, 15, 0, 0, 9}, new int[]{0, 0, 18, 18, 12, 12}, 6);


	public void repaint(Graphics2D g, int x, int y) {
		switch (state()) {
			case WEEDS:
				g.setColor(new Color(102, 51, 0));
				break;
			case GROWING:
				g.setColor(Color.blue);
				break;
			case DISEASED:
				g.setColor(Color.YELLOW);
				break;
			case EMPTY:
				g.setColor(Color.gray);
				break;
			case DEAD:
				g.setColor(Color.black);
				break;
			case READY:
				g.setColor(Color.green);
				break;
			default:
				g.setColor(Color.pink);
		}

		g.fillRect(x, y, 6, 6);
		g.setStroke(new BasicStroke(1));
		g.setColor(Color.gray);
		g.drawRect(x, y, 6, 6);
	}

	public CropState state() {
		if (empty()) {
			return CropState.EMPTY;
		}

		if (diseased()) {
			return CropState.DISEASED;
		}

		if (dead()) {
			return CropState.DEAD;
		}

		if (weeds() > 0) {
			return CropState.WEEDS;
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

	/**
	 * Growth stage of patch
	 *
	 * @return 0 (empty) to 5 (grown)
	 */
	public int stage() {
		final FarmingDefinition definition = definition();

		int[] model_ids_growth_stage = this.id() == HerbEnum.TROLLHEIM.id() ? IFarming.MODEL_IDS_GROWTH_STAGE_TROLLHEIM : IFarming.MODEL_IDS_GROWTH_STAGE;
		for (int i = 0; i < model_ids_growth_stage.length; i++) {
			if (definition.containsModel(model_ids_growth_stage[i])) {
				return i + 1;
			}
		}

		return 0;
	}

//	private void drawPatch(IClientContext ctx, Graphics2D g, int x, int y) {
//
//		final Polygon allotment1 = new Polygon(POLYGON_ALLOTMENT_1.xpoints, POLYGON_ALLOTMENT_1.ypoints, POLYGON_ALLOTMENT_1.npoints);
//		allotment1.translate(x, y);
//		g.fillPolygon(allotment1);
//
//		final Polygon allotment2 = new Polygon(POLYGON_ALLOTMENT_2.xpoints, POLYGON_ALLOTMENT_2.ypoints, POLYGON_ALLOTMENT_2.npoints);
//		allotment2.translate(x + 15, y + 12);
//		g.fill(allotment2);
//
//		g.fillRect(x + 24, y, 6, 6);
//
//		g.fillRect(x + 12, y + 12, 6, 6);
//	}
}
