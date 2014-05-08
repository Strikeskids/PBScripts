package org.logicail.rsbot.scripts.framework.context.providers.farming.farmingobject;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingDefinition;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingHelper;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingObject;
import org.logicail.rsbot.scripts.framework.context.providers.farming.enums.TreeEnum;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.*;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 14/04/2014
 * Time: 19:18
 */
public class Tree extends FarmingObject implements IStump, ICheckHealth, IGrowthStage, IWeeds, ICanDie {
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

	/**
	 * How many branches are on the willow tree
	 *
	 * @return
	 */
	public int branches() {
		if (definition().containsAction("Gather-Branches")) {
			final int bits = bits();
			if ((bits & 0xC0) == 0xC0) {
				return 6 - (bits & 0x7);
			}
		}

		return 0;
	}

	@Override
	public boolean checkHealth() {
		return definition().containsAction("Check-health");
	}

	/**
	 * Can the tree be chopped down or check-healthed
	 *
	 * @return <tt>true</tt> if the tree has reached its final stage of growing, otherwise <tt>false</tt>
	 * @see TreeType#stages
	 */
	@Override
	public boolean grown() {
		final TreeType type = type();
		return type != TreeType.TREE_PATCH && definition().containsModel(type.numberOfStages() - 1);
	}

	/**
	 * Get the type of tree growing
	 *
	 * @return the type of tree growing, or TREE_PATCH if nothing is growing
	 */
	@Override
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
	 * Is the patch weed free but empty (ready to plant seed)
	 *
	 * @return <tt>true</tt> if patch has no weeeds and is ready to plant a seed, otherwise <tt>false</tt>
	 */
	@Override
	public boolean empty() {
		return type() == TreeType.TREE_PATCH;
	}

	@Override
	public void repaint(Graphics2D g, int x, int y) {
		g.setColor(state().color());
		g.fillRect(x, y, 9, 9);
		g.setColor(Color.gray);
		g.drawRect(x, y, 9, 9);
	}

	@Override
	public int stage() {
		final FarmingDefinition definition = definition();
		final TreeType type = type();
		if (type == TreeType.TREE_PATCH) {
			return 0;
		}

		int[] stages = type.stages;
		for (int i = 0; i < stages.length; i++) {
			if (definition.containsModel(stages[i])) {
				return i + 1;
			}
		}

		return 0;
	}

	@Override
	public boolean stump() {
		return FarmingHelper.stump(this);
	}

	@Override
	public int weeds() {
		return FarmingHelper.weeds(this);
	}

	@Override
	public boolean dead() {
		return FarmingHelper.dead(this);
	}

	@Override
	public boolean diseased() {
		return FarmingHelper.diseased(this);
	}

	public enum TreeType {
		TREE_PATCH(-1),
		OAK(8031, 8033, 8035, 8037, 64863), // 64864
		WILLOW(8031, 8127, 8129, 8131, 8133, 8135, 64870), // 64869
		MAPLE(8031, 8013, 8015, 8017, 8019, 8021, 8023, 8026, 20513), // 20512
		YEW(8031, 8145, 8147, 8149, 8151, 8153, 8155, 8157, 8159, 8141, 64873), // 64872
		MAGIC(7977, 7991, 7993, 7995, 7997, 7999, 8002, 8005, 8008, 7978, 7981, 7984, 20468) // 20511
		;

		private final int[] stages;

		public int numberOfStages() {
			return stages.length;
		}

		TreeType(int... stages) {
			this.stages = stages;
		}
	}
}
