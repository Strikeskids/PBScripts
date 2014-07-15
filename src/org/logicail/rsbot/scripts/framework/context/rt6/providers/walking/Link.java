package org.logicail.rsbot.scripts.framework.context.rt6.providers.walking;

import org.logicail.rsbot.scripts.framework.context.rt6.IClientAccessor;
import org.logicail.rsbot.scripts.framework.context.rt6.IClientContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 27/02/14
 * Time: 18:10
 */
public class Link extends IClientAccessor {
	public final int id;
	public final int startNodeId;
	public final int endNodeId;
	private TileNode startNode;
	private final double cost;
	private TileNode endNode;
	private List<Requirement> requirements = null;

	public Link(IClientContext ctx, int id, int startNodeId, int endNodeId, double cost) {
		super(ctx);
		this.id = id;
		this.startNodeId = startNodeId;
		this.endNodeId = endNodeId;
		this.cost = cost;
	}

	public double getCost() {
		return cost;
	}

	public void setEndNode(TileNode endNode) {
		this.endNode = endNode;
	}

	public void setStartNode(TileNode startNode) {
		this.startNode = startNode;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Link link = (Link) o;

		if (endNodeId != link.endNodeId) return false;
		if (id != link.id) return false;
		return startNodeId == link.startNodeId;
	}

	@Override
	public int hashCode() {
		int result = id;
		result = 31 * result + startNodeId;
		result = 31 * result + endNodeId;
		return result;
	}

	@Override
	public String toString() {
		return "Link{" +
				"id=" + id +
				", startNodeId=" + startNodeId +
				", endNodeId=" + endNodeId +
				'}';
	}

	/**
	 * Requirements to traverse (Level, Items etc.)
	 *
	 * @param requirement
	 */
	public void addRequirement(Requirement requirement) {
		if (this.requirements == null) {
			this.requirements = new ArrayList<Requirement>();
		}
		requirements.add(requirement);
	}

	public boolean isValid() {
		if (requirements == null) {
			return true;
		}

		for (Requirement requirement : requirements) {
			if (!requirement.valid(ctx)) {
				return false;
			}
		}

		return true;
	}

	public int getId() {
		return id;
	}
}
