package org.logicail.rsbot.scripts.testing;

import org.logicail.rsbot.scripts.framework.LogicailScript;
import org.powerbot.script.Script;

import java.awt.*;
import java.util.LinkedHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 11/04/2014
 * Time: 19:04
 */
@Script.Manifest(name = "Farming Test", description = "Test farming methods.", properties = "hidden=true;")
public class FarmingTest extends LogicailScript<FarmingTest> {
	private final FarmingPaint farmingPaint;

	public FarmingTest() {
		super();
		farmingPaint = new FarmingPaint(ctx);
		paint.location(new Point(50, 350));
	}

	@Override
	public LinkedHashMap<Object, Object> getPaintInfo() {
		LinkedHashMap<Object, Object> properties = new LinkedHashMap<Object, Object>();

//		for (HerbEnum patch : ctx.farming.herbs().select()) {
//			properties.put(patch.name(), "[" + patch.bits(ctx) + "] state:" + patch.state(ctx) + " stage: " + patch.stage(ctx));
//		}
//		for (CompostEnum patch : CompostEnum.values()) {
//			final Compost compost = patch.compost(ctx);
//
//			StringBuilder sb = new StringBuilder();
//			sb.append("[").append(compost.bits()).append("]");
//			sb.append(" type: ").append(compost.type());
//			sb.append(" count: ").append(compost.count());
//			if (compost.closed()) sb.append(" closed");
//			if (compost.grown()) sb.append(" grown");
//
//			properties.put(patch, sb.toString());
//		}

//		for (TreeEnum treeEnum : TreeEnum.values()) {
//			final Tree tree = treeEnum.tree(ctx);
//
//			StringBuilder sb = new StringBuilder();
//			sb.append("[").append(tree.bits()).append("]");
//			sb.append(" " + tree.type());
//			if (tree.checkHealth()) sb.append(" check health");
//			if (tree.grown()) sb.append(" branches: ").append(tree.branches());
//			if (tree.grown()) sb.append(" grown");
//
//			properties.put(treeEnum, sb.toString());
//		}

		properties.put("Super compost", ctx.farming().superCompost());
		properties.put("Compost", ctx.farming().compost());
		properties.put("Plant cure", ctx.farming().plantCure());
		properties.put("Buckets", ctx.farming().buckets());
		properties.put("Watering can type", ctx.farming().wateringCan());

		return properties;
	}


	@Override
	public void repaint(Graphics g) {
		super.repaint(g);
		farmingPaint.repaint(g);
	}
}
