package org.logicail.rsbot.scripts.framework.context.providers.farming.farmingobject;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
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

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[").append(bits()).append("]");
		sb.append(" ").append(type());
		if (checkHealth()) sb.append(" check health");
		if (grown()) sb.append(" grown");
		if (grown() && type() == TreeType.WILLOW) sb.append(" branches: ").append(branches());

		return sb.toString();
	}

	public int branches() {
		if (definition().containsAction("Gather-Branches")) {
			final int bits = bits();
			if ((bits & 0xC0) == 0xC0) {
				return 6 - (bits & 0x7);
			}
		}

		return 0;
	}

	public boolean checkHealth() {
		return definition().containsAction("Check-health");
	}

	/**
	 * Can the tree be chopped down or check-healthed
	 *
	 * @return <tt>true</tt> if the tree has reached its final stage of growing, otherwise <tt>false</tt>
	 */
	public boolean grown() {
		final TreeType type = type();
		return definition().containsModel(type.grownModel);
	}

	/**
	 * Get the type of tree growing
	 *
	 * @return the type of tree growing, or TREE_PATCH if nothing is growing
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
	 * Is the tree dead
	 *
	 * @return <tt>true</tt> if the tree has died, otherwise <tt>false</tt>
	 */
	public boolean dead() {
		return definition().name().startsWith("Dead");
	}

	/**
	 * Is the tree diseased, can be cured by interact("Prune")
	 *
	 * @return <tt>true</tt> if the tree is diseased, otherwise <tt>false</tt>
	 */
	public boolean diseased() {
		return definition().name().startsWith("Diseased");
	}

	public boolean stump() {
		return definition().name().toLowerCase().endsWith(" stump");
	}

	/**
	 * Number of weeds on the tree patch
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
		TREE_PATCH(-1),
		OAK(64863), // 8031, 8033, 8035, 8037, 64863, 64863, 64864
		WILLOW(64870), // 8031, 8127, 8129, 8131, 8133, 8135, 64870, 64870, 64869
		MAPLE(20513), // 8031, 8013, 8015, 8017, 8019, 8021, 8023, 8026, 20513, 20513, 20512
		YEW(64873), // 8031, 8145, 8147, 8149, 8151, 8153, 8155, 8157, 8159, 8141, 64873, 64873, 64872
		MAGIC(20468) // 7977, 7991, 7993, 7995, 7997, 7999, 8002, 8005, 8008, 7978, 7981, 7984, 20468, 20468, 20511
		;

		private final int grownModel;

		TreeType(int grownModel) {
			this.grownModel = grownModel;
		}
	}
}
