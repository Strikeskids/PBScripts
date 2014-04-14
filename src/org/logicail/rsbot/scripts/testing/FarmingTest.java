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
@Script.Manifest(name = "Farming Test", description = "Test farming methods", properties = "hidden=true;")
public class FarmingTest extends LogicailScript<FarmingTest> {
	private final FarmingPaint farmingPaint;

	public FarmingTest() {
		super();
		farmingPaint = new FarmingPaint(ctx);
	}

	@Override
	public LinkedHashMap<Object, Object> getPaintInfo() {
		LinkedHashMap<Object, Object> properties = new LinkedHashMap<Object, Object>();

//		for (HerbEnum patch : ctx.farming.herbs().select()) {
//			properties.put(patch.name(), "[" + patch.bits(ctx) + "] state:" + patch.state(ctx) + " stage: " + patch.stage(ctx));
//		}
//		for (CompostEnum patch : ctx.farming.compost().select()) {
//			StringBuilder sb = new StringBuilder();
//			sb.append("[").append(patch.bits(ctx)).append("]");
//			sb.append(" type: ").append(patch.type(ctx));
//			sb.append(" count: ").append(patch.count(ctx));
//			if (patch.closed(ctx)) sb.append(" closed");
//			if (patch.grown(ctx)) sb.append(" grown");
//
//			properties.put(patch.name(), sb.toString());
//		}

		return properties;
	}


	@Override
	public void repaint(Graphics g) {
		super.repaint(g);
		farmingPaint.repaint(g);
	}
}
