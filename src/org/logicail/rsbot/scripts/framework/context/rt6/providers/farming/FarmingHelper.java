package org.logicail.rsbot.scripts.framework.context.rt6.providers.farming;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 16/04/2014
 * Time: 10:56
 */
public class FarmingHelper {
	public static int weeds(FarmingObject o) {
		switch (o.bits()) {
			case 0:
				return 3;
			case 1:
				return 2;
			case 2:
				return 1;
		}

		return 0;
	}

	public static boolean dead(FarmingObject o) {
		return o.definition().name().startsWith("Dead");
	}

	public static boolean diseased(FarmingObject o) {
		return o.definition().name().startsWith("Diseased");
	}

	public static boolean checkHealth(FarmingObject o) {
		return o.definition().containsAction("Check-health");
	}

	public static boolean stump(FarmingObject o) {
		return o.definition().name().toLowerCase().endsWith(" stump");
	}
}
