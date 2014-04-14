package org.logicail.rsbot.scripts.framework.context.providers.farming.patches;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingDefinition;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingObject;
import org.logicail.rsbot.scripts.framework.context.providers.farming.enums.TreeEnum;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 14/04/2014
 * Time: 19:18
 */
public class Tree extends FarmingObject {
	public Tree(IClientContext ctx, TreeEnum tree) {
		super(ctx, tree.id());
	}

	public int branches() {
		if (definition().containsAction("Gather-Branches")) {
			final int bits = bits();
			if ((bits & 0xC0) == 0xC0) {
				return (bits & 0x7) + 1; // TODO: Check could be "6 - (bits & 0x7)
			}
		}

		return 0;
	}

	public boolean checkHealth() {
		return definition().containsAction("Check-health");
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
	 * Is the patch diseased, can be cured by interact("Prune")
	 *
	 * @return <tt>true</tt> if the patch iss diseases, otherwise <tt>false</tt>
	 */
	public boolean diseased() {
		return definition().name().startsWith("Diseased");
	}

	/**
	 * Can the tree be chopped down or check-healthed
	 *
	 * @return <tt>true</tt> if the allotment has finished growing and can be harvested, otherwise <tt>false</tt>
	 */
	public boolean grown() {
		final FarmingDefinition definition = definition();
		return definition.containsAction("Check-health") || definition.containsAction("Chop down");
	}

	public boolean stump() {
		return definition().name().toLowerCase().endsWith(" stump");
	}

	/**
	 * Get the type of tree growing
	 *
	 * @return the type of trr growing, or ALLOTMENT if nothing is growing
	 */
	public TreeType type() {
		final String name = definition().name().toLowerCase();
		for (TreeType cropType : TreeType.values()) {
			if (name.contains(cropType.name().toLowerCase().replace('_', ' '))) {
				return cropType;
			}
		}

		return TreeType.TREE_PATCH;
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

	public enum TreeType {
		TREE_PATCH,
		OAK, // 8031, 8033, 8035, 8037, 64863, 64863, 64864
		WILLOW, // 8031, 8127, 8129, 8131, 8133, 8135, 64870, 64870, 64869
		MAPLE, // 8031, 8013, 8015, 8017, 8019, 8021, 8023, 8026, 20513, 20513, 20512
		YEW, // 8031, 8145, 8147, 8149, 8151, 8153, 8155, 8157, 8159, 8141, 64873, 64873, 64872
		MAGIC // 7977, 7991, 7993, 7995, 7997, 7999, 8002, 8005, 8008, 7978, 7981, 7984, 20468, 20468, 20511
	}
}
