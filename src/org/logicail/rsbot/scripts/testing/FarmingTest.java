package org.logicail.rsbot.scripts.testing;

import org.logicail.rsbot.scripts.framework.LogicailScript;
import org.logicail.rsbot.scripts.framework.context.providers.farming.HerbPatch;
import org.powerbot.script.Script;

import java.util.LinkedHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 11/04/2014
 * Time: 19:04
 */
@Script.Manifest(name = "Farming Test", description = "Test farming methods", properties = "hidden=true;")
public class FarmingTest extends LogicailScript<FarmingTest> {
	@Override
	public LinkedHashMap<Object, Object> getPaintInfo() {
		LinkedHashMap<Object, Object> properties = new LinkedHashMap<Object, Object>();

		if (ctx.farming.ready()) {
			for (HerbPatch patch : ctx.farming.herbs().select()) {
				properties.put(patch.name(), "[" + patch.bits(ctx) + "] state:" + patch.state(ctx) + " stage: " + patch.stage(ctx));
			}
		}

		return properties;
	}

	public static void main(String[] args) {
		//System.out.println(HerbPatch.stage(Herb.GOUTWEED, (-1422313159 & 0xff) & 127));
	}
}
